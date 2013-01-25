package org.craft.atom.nio;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craft.atom.nio.api.AbstractConfig;
import org.craft.atom.nio.spi.EventDispatcher;
import org.craft.atom.nio.spi.Handler;

/**
 * Processor process actual I/O operations. It abstracts Java NIO to simplify transport implementations. 
 *
 * @author Hu Feng
 * @version 1.0, 2011-11-10
 */
public class Processor extends Abstractor {
	
	private static final Log LOG = LogFactory.getLog(Processor.class);
	
	/** A timeout used for the select, as we need to get out to deal with idle sessions */
	private static final long SELECT_TIMEOUT = 1000L;
	
	/** Flush spin count */
	private static final long FLUSH_SPIN_COUNT = 256;
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	/** A Session queue containing the newly created sessions */
	private final Queue<AbstractSession> newSessions = new ConcurrentLinkedQueue<AbstractSession>();
	
	/** A queue used to store the sessions to be flushed */
    private final Queue<AbstractSession> flushingSessions = new ConcurrentLinkedQueue<AbstractSession>();
    
    /** A queue used to store the sessions to be closed */
    private final Queue<AbstractSession> closingSessions = new ConcurrentLinkedQueue<AbstractSession>();
    
    /** UDP session holders */
    private final Map<String, AbstractSession> udpSessionMap = new ConcurrentHashMap<String, AbstractSession>();
    
    private final AtomicReference<ProcessThread> processThreadRef = new AtomicReference<ProcessThread>();
	private final Executor executor = Executors.newCachedThreadPool();
	private AbstractConfig config;
	private AtomicBoolean wakeupCalled = new AtomicBoolean(false);
	private Protocol protocol = Protocol.TCP;
	private volatile Selector selector;
	private volatile boolean shutdown = false;
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	public Processor(AbstractConfig config, Handler handler, EventDispatcher eventDispatcher) {
		this.config = config;
		this.handler = handler;
		this.eventDispatcher = eventDispatcher;
		try {
            init();
        } catch (IOException e) {
            throw new RuntimeException("Fail to startup a processor", e);
        }
	}
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	/**
	 * Adds a session to processor's new sessions queue, so that processor can process I/O operations associated this session.
	 * 
	 * @param session
	 */
	public void add(AbstractSession session) {
		if (this.shutdown) {
			throw new IllegalStateException("The processor already shutdown!");
		}
		
		if (session == null) {
			return;
		}
		
		newSessions.add(session);
		startup();
        wakeup();
	}
	
	private void startup() {
		ProcessThread pt = processThreadRef.get();

        if (pt == null) {
            pt = new ProcessThread();

            if (processThreadRef.compareAndSet(null, pt)) {
                executor.execute(pt);
            }
        }
    }
	
	/** 
	 * shutdown the processor, stop the process thread and close all the session within this processor
	 */
	public void shutdown() {
		this.shutdown = true;
		this.selector.wakeup();
	}
	
	private void shutdown0() throws IOException {
		// close all the session within this processor
		this.closingSessions.addAll(newSessions);
		newSessions.clear();
		this.closingSessions.addAll(flushingSessions);
		flushingSessions.clear();
		close();
		
		// close processor selector
		this.selector.close();

		if (LOG.isDebugEnabled()) {
			LOG.debug("shutdown processor successful!");
		}
	}
	
	private void init() throws IOException {
		selector = Selector.open();
	}
	
	private void wakeup() {
		wakeupCalled.getAndSet(true);
		selector.wakeup();
	}
	
	private int select() throws IOException {
		long t0 = System.currentTimeMillis();
		int selected = selector.select(SELECT_TIMEOUT);
		long t1 = System.currentTimeMillis();
		long delta = (t1 - t0);
		
		if ((selected == 0) && !wakeupCalled.get() && (delta < 100)) {
            // the select() may have been interrupted because we have had an closed channel.
            if (isBrokenConnection()) {
                LOG.warn("Broken connection wakeup");
            } else {
                LOG.warn("Create a new selector. Selected is 0, delta = " + (t1 - t0));
                
                // it is a workaround method for jdk bug, see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6403933
                registerNewSelector();
            }

            // Set back the flag to false and continue the loop
            wakeupCalled.getAndSet(false);
        }
		
		return selected;
	}
	
	private void registerNewSelector() throws IOException {
        synchronized (selector) {
            Set<SelectionKey> keys = selector.keys();

            // Open a new selector
            Selector newSelector = Selector.open();

            // Loop on all the registered keys, and register them on the new selector
            for (SelectionKey key : keys) {
                SelectableChannel ch = key.channel();

                // Don't forget to attache the session, and back !
                AbstractSession session = (AbstractSession) key.attachment();
                ch.register(newSelector, key.interestOps(), session);
            }

            // Now we can close the old selector and switch it
            selector.close();
            selector = newSelector;
        }
    }
	
	private boolean isBrokenConnection() throws IOException {
		boolean brokenSession = false;
		
		synchronized (selector) {
			Set<SelectionKey> keys = selector.keys();
			for (SelectionKey key : keys) {
				SelectableChannel channel = key.channel();
				if (!((SocketChannel) channel).isConnected()) {
					// The channel is not connected anymore. Cancel the associated key.
					key.cancel();
					brokenSession = true;
				}
			}
		}
		
		return brokenSession;
	}
	
	private int register() throws ClosedChannelException {
		int n = 0;
		for (AbstractSession session = newSessions.poll(); session != null; session = newSessions.poll()) {
			SelectableChannel sc = session.getChannel();
			SelectionKey key = sc.register(selector, SelectionKey.OP_READ, session);
			session.setSelectionKey(key);
			
			// set session state open, so we can read / write
			session.setOpened();
			
			// fire session opened event
			eventDispatcher.dispatch(new Event(EventType.SESSION_OPENED, session, null, handler));
			
			n++;
		}
		return n;
	}
	
	private void process() {
		Iterator<SelectionKey> it = selector.selectedKeys().iterator();
		while (it.hasNext()) {
			AbstractSession session = (AbstractSession) it.next().attachment();
			if (session.isValid()) {
				process0(session);
			} else {
				LOG.warn("session is invalid, but some event trigger to process! session id = " + session.getId());
			}
			it.remove();
		}
	}
	
	private void process0(AbstractSession session) {
		// set last IO time
		session.setLastIoTime(System.currentTimeMillis());
		
		// Process reads
		if (session.isOpened() && isReadable(session)) {
			read(session);
		}

		// Process writes
		if (session.isOpened() && isWritable(session)) {
			asyWrite(session);
		}
	}
	
	private void read(AbstractSession session) {
		int bufferSize = session.getSizePredictor().next();
		ByteBuffer buf = ByteBuffer.allocate(bufferSize);

		try {
			if (protocol.equals(Protocol.TCP)) {
				readTcp(session, buf);
			} else if (protocol.equals(Protocol.UDP)) {
				readUdp(session, buf);
			}
		} catch (Exception e) {
			LOG.error("catch read exception and fire it, session=" + session, e);

			// fire exception caught event
			eventDispatcher.dispatch(new Event(EventType.EXCEPTION_CAUGHT, session, e, handler));
			
			// if it is IO exception close session avoid infinite loop.
			if (e instanceof IOException) {
				asyClose(session);
			}
		}
	}
	
	private void fireMessageReceived(AbstractSession session, ByteBuffer buf, int length) {
		// fire message received event, here we copy buffer bytes to a new byte array to avoid handler expose <code>ByteBuffer</code> to end user.
		byte[] barr = new byte[length];
		System.arraycopy(buf.array(), 0, barr, 0, length);
		eventDispatcher.dispatch(new Event(EventType.MESSAGE_RECEIVED, session, barr, handler));
	}
	
	private void readUdp(AbstractSession session, ByteBuffer buf) throws IOException {
		DatagramChannel dc = (DatagramChannel) session.getChannel();
		SocketAddress remoteAddress = dc.receive(buf);
		
		if (remoteAddress != null) {
			String key = generateKey(session.getLocalAddress(), remoteAddress);
			if (session.getRemoteAddress() == null) {
				// handle first packet with current session
				session.setRemoteAddress(remoteAddress);
				udpSessionMap.put(key, session);
			} else if (!udpSessionMap.containsKey(key)) {
				// handle new packet not belong to any session.
				AbstractSession newSession = new UdpSession((UdpSession) session);
				newSession.setRemoteAddress(remoteAddress);
				session = newSession;
				udpSessionMap.put(key, session);
			} else {
				session = udpSessionMap.get(key);
			}
			
			session.setLastIoTime(System.currentTimeMillis());
			fireMessageReceived(session, buf, buf.position());
		}
	}
	
	private String generateKey(SocketAddress localAddress, SocketAddress remoteAddress) {
		return localAddress.toString() + "-" + remoteAddress.toString();
	}
	
	private void readTcp(AbstractSession session, ByteBuffer buf) throws IOException {
		int readBytes = 0;
		int ret;
		while ((ret = ((SocketChannel) session.getChannel()).read(buf)) > 0) {
			readBytes += ret;
			if (!buf.hasRemaining()) {
				break;
			}
		}

		if (readBytes > 0) {
			session.getSizePredictor().previous(readBytes);
			fireMessageReceived(session, buf, readBytes);
		}

		// read end-of-stream, remote peer may close channel so close session.
		if (ret < 0) {
			asyClose(session);
		}
	}

	private void asyWrite(AbstractSession session) {
		// Add session to flushing queue, soon after it will be flushed in the same select loop.
		flushingSessions.add(session);
	}
	
	private boolean isReadable(AbstractSession session) {
		SelectionKey key = session.getSelectionKey();
		return key.isValid() && key.isReadable();
	}
	
	private boolean isWritable(AbstractSession session) {
        SelectionKey key = session.getSelectionKey();
        return key.isValid() && key.isWritable();
    }
	
	/**
	 * Add the session to the processor's flushing session queue, and notify processor flush it immediately.
	 * 
	 * @param session
	 */
	public void flush(AbstractSession session) {
		if (this.shutdown) {
			throw new IllegalStateException("The processor already shutdown!");
		}
		
		if (session == null) {
			return;
		}
		
		flushingSessions.add(session);
		wakeup();
	}
	
	/**
     * Removes and closes the specified {@code Session} from the processor so that processor closes the connection
     * associated with the {@code Session} and releases any other related resources.
     */
    void remove(AbstractSession session) {
    	if(this.shutdown) {
			throw new IllegalStateException("The processor already shutdown!");
		}
		
		if(session == null) {
			return;
		}
		
		asyClose(session);
		wakeup();
    }
    
	private void flush() {
		int c = 0;
		while (!flushingSessions.isEmpty() && c < FLUSH_SPIN_COUNT) {
			AbstractSession session = flushingSessions.poll();
            if (session == null) {
                // Just in case ... It should not happen.
                break;
            }
            
            // spin counter avoid infinite loop in this method.
            c++;
            
            try {
            	if (session.isOpened()) {
            		flush0(session);
            	} else if (!session.isValid()) {
            		throw new IllegalStateException("Session state is invalid, can't be flush, session id = " + session);
            	} else {
              		// Retry later if session is not yet opened, in case that Session.write() is called before add() is processed.
            		asyWrite(session);
            		return;
            	}
			} catch (Exception e) {
				LOG.error("catch flush exception and fire it", e);
				
				// fire exception caught event 
				eventDispatcher.dispatch(new Event(EventType.EXCEPTION_CAUGHT, session, e, handler));
				
				// if it is IO exception close session avoid infinite loop.
				if (e instanceof IOException) {
					asyClose(session);
				}
			}
		}
	}
	
	private void flush0(AbstractSession session) throws IOException {
		if (LOG.isDebugEnabled()) { LOG.debug("Flushing session: " + session); }
		
		final Queue<ByteBuffer> writeQueue = session.getWriteBufferQueue();

		// First set not be interested to write event
		setInterestedInWrite(session, false);
		
		// flush by mode
		if (config.isReadWritefair()) {
			fairFlush0(session, writeQueue);
		} else {
			oneOffFlush0(session, writeQueue);
		}
		
		// The write buffer queue is not empty, we re-interest in writing and later flush it.
		if (!writeQueue.isEmpty()) {
			setInterestedInWrite(session, true);
			flushingSessions.add(session);
		}
	}
	
	private void oneOffFlush0(AbstractSession session, Queue<ByteBuffer> writeQueue) throws IOException {
		ByteBuffer buf = writeQueue.peek();
		if (buf == null) {
			return;
		}
		
		write(session, buf, buf.remaining());
		
		if (buf.hasRemaining()) {
			setInterestedInWrite(session, true);
			flushingSessions.add(session);
			return;
		} else {
			writeQueue.remove();
			// fire message sent event
			eventDispatcher.dispatch(new Event(EventType.MESSAGE_SENT, session, buf.array(), handler));
		}
	}
	
	private void fairFlush0(AbstractSession session, Queue<ByteBuffer> writeQueue) throws IOException {
		ByteBuffer buf = null;
		int writtenBytes = 0;
		final int maxWrittenBytes = session.getMaxWriteBufferSize();
		if (LOG.isDebugEnabled()) { LOG.debug("Max write byte size: " + maxWrittenBytes); }
		
		do {
			if (buf == null) {
				buf = writeQueue.peek();
				if (buf == null) {
					return;
				}
			}
			
			int qota = maxWrittenBytes - writtenBytes;
			int localWrittenBytes = write(session, buf, qota);
			
			if (LOG.isDebugEnabled()) { 
				LOG.debug("Flush buffer: " + new String(buf.array()) + " session id=" + session.getId() + " written-bytes=" + localWrittenBytes + " buf-bytes=" + buf.array().length + " qota=" + qota + " buf-remaining=" + buf.hasRemaining());
			}
			
			writtenBytes += localWrittenBytes;
			
			// The buffer is all flushed, remove it from write queue
			if (!buf.hasRemaining()) {
				if (LOG.isDebugEnabled()) { LOG.debug("The buffer is all flushed, remove it from write queue"); }
				
				writeQueue.remove();
				
				// fire message sent event
				eventDispatcher.dispatch(new Event(EventType.MESSAGE_SENT, session, buf.array(), handler));
				
				// set buf=null and the next loop if no byte buffer to write then break the loop.
				buf = null;
				continue;
			}

			// 0 byte be written, maybe kernel buffer is full so we re-interest in writing and later flush it.
			if (localWrittenBytes == 0) {
				if (LOG.isDebugEnabled()) { LOG.debug("0 byte be written, maybe kernel buffer is full so we re-interest in writing and later flush it"); }
				
				setInterestedInWrite(session, true);
				flushingSessions.add(session);
				return;
			}
			
			// The buffer isn't empty(bytes to flush more than max bytes), we re-interest in writing and later flush it.
			if (localWrittenBytes > 0 && buf.hasRemaining()) {
				if (LOG.isDebugEnabled()) { LOG.debug("The buffer isn't empty(bytes to flush more than max bytes), we re-interest in writing and later flush it"); }
				
				setInterestedInWrite(session, true);
				flushingSessions.add(session);
				return;
			}

			// Wrote too much, so we re-interest in writing and later flush other bytes.
			if (writtenBytes >= maxWrittenBytes && buf.hasRemaining()) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Wrote too much, so we re-interest in writing and later flush other bytes");
				}
				
				setInterestedInWrite(session, true);
				flushingSessions.add(session);
				return;
			}
		} while (writtenBytes < maxWrittenBytes);
	}
	
	private int write(AbstractSession session, ByteBuffer buf, int maxLength) throws IOException {		
		int localWrittenBytes = 0;
		if (LOG.isDebugEnabled()) {
			LOG.debug("Allow write max len: " + maxLength);
			LOG.debug("Waiting write byte buffer: " + buf);
		}

		if (buf.hasRemaining()) {
			int length = Math.min(buf.remaining(), maxLength);
			if (protocol.equals(Protocol.TCP)) {
				localWrittenBytes = writeTcp(session, buf, length);
			} else if (protocol.equals(Protocol.UDP)) {
				localWrittenBytes = writeUdp(session, buf, length);
			}
		}
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Actual write byte size: " + localWrittenBytes);
		}

		return localWrittenBytes;
	}
	
	private int writeUdp(AbstractSession session, ByteBuffer buf, int length) throws IOException {
		if (buf.remaining() <= length) {
			return ((DatagramChannel) session.getChannel()).send(buf, session.getRemoteAddress());
		}

		int oldLimit = buf.limit();
		buf.limit(buf.position() + length);
		try {
			return ((DatagramChannel) session.getChannel()).send(buf, session.getRemoteAddress());
		} finally {
			buf.limit(oldLimit);
		}

	}
	
	private int writeTcp(AbstractSession session, ByteBuffer buf, int length) throws IOException {
		if (buf.remaining() <= length) {
			return ((SocketChannel) session.getChannel()).write(buf);
		}

		int oldLimit = buf.limit();
		buf.limit(buf.position() + length);
		try {
			return ((SocketChannel) session.getChannel()).write(buf);
		} finally {
			buf.limit(oldLimit);
		}
	}
	
	private void setInterestedInWrite(AbstractSession session, boolean isInterested) {
		SelectionKey key = session.getSelectionKey();

		if (key == null || !key.isValid()) {
			return;
		}

		int newInterestOps = key.interestOps();

		if (isInterested) {
			newInterestOps |= SelectionKey.OP_WRITE;
		} else {
			newInterestOps &= ~SelectionKey.OP_WRITE;
		}

		key.interestOps(newInterestOps);
	}
	
	private void asyClose(AbstractSession session) {
		if (session.isClosing() || session.isClosed()) {
			return;
		}
		
		closingSessions.add(session);
	}
	
	private int close() throws IOException {
		int n = 0;
		for (AbstractSession session = closingSessions.poll(); session != null; session = closingSessions.poll()) {
			if (LOG.isDebugEnabled()) { LOG.debug("Closing session: " + session); }
			
			if (session.isClosed()) {
				if (LOG.isDebugEnabled()) { LOG.debug("Escape close session, it has been closed: " + session); }
				continue;
			}
			
			session.setClosing();
			
			close(session);
			n++;
			
			session.setClosed();
			
			// fire session closed event
			eventDispatcher.dispatch(new Event(EventType.SESSION_CLOSED, session, null, handler));
			
			if (LOG.isDebugEnabled()) { LOG.debug("Closed session: " + session); }
		}
		return n;
	}
	
	private void close(AbstractSession session) throws IOException {
		if (protocol.equals(Protocol.TCP)) {
			closeTcp(session);
		} else {
			closeUdp(session);
		}
	}
	
	private void closeUdp(AbstractSession session) {
		String key = generateKey(session.getLocalAddress(), session.getRemoteAddress());
		udpSessionMap.remove(key);
	}
	
	private void closeTcp(AbstractSession session) throws IOException {
		SelectableChannel sc = session.getChannel();
		SelectionKey key = session.getSelectionKey();
		if(key != null) {
			key.cancel();
		}
		sc.close();
	}
	
	private void notifyIdleSessions(long currentTime) {
		for (AbstractSession s : allSessions()) {
			long elapse = currentTime - s.getLastIoTime();
			if (elapse > config.getIoTimeoutInMillis()) {
				s.setLastIoTime(currentTime);
				eventDispatcher.dispatch(new Event(EventType.SESSION_IDLE, s, null, handler));
			}
		}
	}

	private List<AbstractSession> allSessions() {
		List<AbstractSession> sessions = new ArrayList<AbstractSession>();
		Set<SelectionKey> keys = selector.keys();
		for (SelectionKey key : keys) {
			sessions.add((AbstractSession) key.attachment());
		}
		return sessions;
	}
	
	// ~ -------------------------------------------------------------------------------------------------------------

	private class ProcessThread implements Runnable {
		public void run() {
			int num = 0;
			while (!shutdown) {
				try {
					int selected = select();
					
					// register new sessions
					num += register();
					
					if (selected > 0) {
                        process();
                    }
					
					// flush sessions
					flush();
					
					// close sessions
					num -= close();
					
					// notify idle sessions
					notifyIdleSessions(System.currentTimeMillis());
					
					// last get a chance to exit infinite loop, just for TCP protocol.
					if (num == 0 && Protocol.TCP.equals(protocol)) {
						processThreadRef.set(null);
						
						if (newSessions.isEmpty() && flushingSessions.isEmpty() && selector.keys().isEmpty()) {
							break;
						}
						
						if (!processThreadRef.compareAndSet(null, this)) {
							// a new ProcessThread created, we must exit now
                            break;
                        }
					}
				} catch (Exception e) {
					LOG.error("Unexpected exception caught while process", e);
				}
			}
			
			// if shutdown == true, we shutdown the processor
			if (shutdown) {
				try {
					shutdown0();
				} catch (Exception e) {
					LOG.error("Unexpected exception caught while shutdown", e);
				}
			}
		}
	}
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	public Selector getSelector() {
		return selector;
	}

}
