package org.craft.atom.nio;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
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
import org.craft.atom.io.ChannelEventType;
import org.craft.atom.io.IoHandler;
import org.craft.atom.io.IoProtocol;
import org.craft.atom.nio.spi.NioChannelEventDispatcher;
import org.craft.atom.util.NamedThreadFactory;

/**
 * Processor process actual I/O operations. 
 * It abstracts Java NIO to simplify transport implementations. 
 * 
 * @author mindwind
 * @version 1.0, Feb 22, 2013
 */
public class NioProcessor extends NioReactor {
	
	private static final Log LOG = LogFactory.getLog(NioProcessor.class);
	
	/** Flush spin count */
	private static final long FLUSH_SPIN_COUNT = 256;
	
	/** A channel queue containing the newly created channel */
	private final Queue<NioByteChannel> newChannels = new ConcurrentLinkedQueue<NioByteChannel>();
	
	/** A queue used to store the channel to be flushed */
    private final Queue<NioByteChannel> flushingChannels= new ConcurrentLinkedQueue<NioByteChannel>();
    
    /** A queue used to store the channel to be closed */
    private final Queue<NioByteChannel> closingChannels = new ConcurrentLinkedQueue<NioByteChannel>();
    
    /** UDP channel holders */
    private final Map<String, NioByteChannel> udpChannels = new ConcurrentHashMap<String, NioByteChannel>();
    private final Executor executor;
    private final NioConfig config;
    private final AtomicReference<ProcessThread> processThreadRef = new AtomicReference<ProcessThread>();
    private final NioByteBufferAllocator allocator = new NioByteBufferAllocator();
    private final AtomicBoolean wakeupCalled = new AtomicBoolean(false);
    private final NioChannelIdleTimer idleTimer = NioChannelIdleTimer.getInstance();
    
    private IoProtocol protocol;
    
    private volatile Selector selector;
    private volatile boolean shutdown = false;
    
	// ~ ------------------------------------------------------------------------------------------------------------
    
    NioProcessor(NioConfig config, IoHandler handler, NioChannelEventDispatcher dispatcher) {
		this.config = config;
		this.handler = handler;
		this.dispatcher = dispatcher;
		this.executor = Executors.newCachedThreadPool(new NamedThreadFactory("craft-atom-nio-processor"));
		
		try {
			selector = Selector.open();
        } catch (IOException e) {
            throw new RuntimeException("Fail to startup a processor", e);
        }
	}
    
    // ~ ------------------------------------------------------------------------------------------------------------
    
	/**
	 * Adds a nio channel to processor's new channel queue, so that processor can process I/O operations associated this channel.
	 * 
	 * @param channel
	 */
	public void add(NioByteChannel channel) {
		if (this.shutdown) {
			throw new IllegalStateException("The processor already shutdown!");
		}
		
		if (channel == null) {
			LOG.warn("Add null, return!");
			return;
		}
		
		newChannels.add(channel);
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
	
	private void wakeup() {
		wakeupCalled.getAndSet(true);
		selector.wakeup();
	}
	
	/** 
	 * shutdown the processor, stop the process thread and close all the channel within this processor
	 */
	public void shutdown() {
		this.shutdown = true;
		this.selector.wakeup();
	}
	
	private void shutdown0() throws IOException {
		// close all the channel within this processor
		this.closingChannels.addAll(newChannels);
		newChannels.clear();
		this.closingChannels.addAll(flushingChannels);
		flushingChannels.clear();
		close();
		
		// close processor selector
		this.selector.close();

		if (LOG.isDebugEnabled()) { LOG.debug("shutdown processor successful!"); }
	}
	
	private void close() throws IOException {
		for (NioByteChannel channel = closingChannels.poll(); channel != null; channel = closingChannels.poll()) {
			if (LOG.isDebugEnabled()) { LOG.debug("Closing channel=" + channel); }
			
			if (channel.isClosed()) {
				if (LOG.isDebugEnabled()) { LOG.debug("Skip closes channel=" + channel); }
				continue;
			}
			
			channel.setClosing();
			close(channel);
			channel.setClosed();
			idleTimer.remove(channel);
			
			// fire channel closed event
			fireChannelClosed(channel);
			
			if (LOG.isDebugEnabled()) { LOG.debug("Closed channel=" + channel); }
		}
	}
	
	private void close(NioByteChannel channel) throws IOException {
		channel.close0();
		
		if (protocol == IoProtocol.UDP) {
			String key = udpChannelKey(channel.getLocalAddress(), channel.getRemoteAddress());
			udpChannels.remove(key);
		}
	}
	
	private int select() throws IOException {
		int selected = selector.select();
		
		if ((selected == 0) && !wakeupCalled.get()) {
            // the select() may have been interrupted because we have had an closed channel.
            if (isBrokenConnection()) {
                LOG.warn("Broken connection wakeup");
            } else {
                LOG.warn("Create a new selector. Selected is 0");
                
                // it is a workaround method for jdk bug, see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6403933
                registerNewSelector();
            }

            // Set back the flag to false and continue the loop
            wakeupCalled.getAndSet(false);
        }
		
		return selected;
	}
	
	private void registerNewSelector() throws IOException {
        synchronized (this) {
            Set<SelectionKey> keys = selector.keys();

            // Open a new selector
            Selector newSelector = Selector.open();

            // Loop on all the registered keys, and register them on the new selector
            for (SelectionKey key : keys) {
                SelectableChannel ch = key.channel();

                // Don't forget to attache the channel, and back !
                NioByteChannel channel = (NioByteChannel) key.attachment();
                ch.register(newSelector, key.interestOps(), channel);
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
	
	private void register() throws ClosedChannelException {
		for (NioByteChannel channel = newChannels.poll(); channel != null; channel = newChannels.poll()) {
			SelectableChannel sc = channel.innerChannel();
			SelectionKey key = sc.register(selector, SelectionKey.OP_READ, channel);
			channel.setSelectionKey(key);
			idleTimer.add(channel);
			
			// fire channel opened event
			fireChannelOpened(channel);
		}
	}
	
	private void process() {
		Iterator<SelectionKey> it = selector.selectedKeys().iterator();
		while (it.hasNext()) {
			NioByteChannel channel = (NioByteChannel) it.next().attachment();
			if (channel.isValid()) {
				process0(channel);
			} else {
				LOG.warn("Channel is invalid, but some event trigger to process! channel=" + channel);
			}
			it.remove();
		}
	}
	
	private void process0(NioByteChannel channel) {
		// set last IO time
		channel.setLastIoTime(System.currentTimeMillis());
		
		// Process reads
		if (channel.isReadable()) {
			if (LOG.isDebugEnabled()) { LOG.debug("Read event process on channel=" + channel); }
			read(channel);
		}

		// Process writes
		if (channel.isWritable()) {
			if (LOG.isDebugEnabled()) { LOG.debug("Write event process on channel=" + channel); }
			asyWrite(channel);
		}
	}
	
	private void read(NioByteChannel channel) {
		int bufferSize = channel.getPredictor().next();
		ByteBuffer buf = allocator.allocate(bufferSize);
		if (LOG.isDebugEnabled()) { LOG.debug("Predict buffer size=" + bufferSize + ", allocate buffer=" + buf); }
		
		int readBytes = 0;
		try {
			if (protocol.equals(IoProtocol.TCP)) {
				readBytes = readTcp(channel, buf);
			} else if (protocol.equals(IoProtocol.UDP)) {
				readBytes = readUdp(channel, buf);
			}
		} catch (Throwable t) {
			LOG.error("Catch read exception and fire it, channel=" + channel, t);

			// fire exception caught event
			fireChannelThrown(channel, t);
			
			// if it is IO exception close channel avoid infinite loop.
			if (t instanceof IOException) {
				asyClose(channel);
			}
		} finally {
			if (readBytes > 0) { buf.clear(); }
		}
	}
	
	private int readTcp(NioByteChannel channel, ByteBuffer buf) throws IOException {
		int readBytes = 0;
		int ret;
		while ((ret = channel.readTcp(buf)) > 0) {
			readBytes += ret;
			if (!buf.hasRemaining()) {
				break;
			}
		}

		if (readBytes > 0) {
			channel.getPredictor().previous(readBytes);
			fireChannelRead(channel, buf, readBytes);
		}

		// read end-of-stream, remote peer may close channel so close channel.
		if (ret < 0) {
			asyClose(channel);
		}
		
		return readBytes;
	}
	
	private void asyClose(NioByteChannel channel) {
		if (channel.isClosing() || channel.isClosed()) {
			return;
		}
		
		closingChannels.add(channel);
	}
	
	private int readUdp(NioByteChannel channel, ByteBuffer buf) throws IOException {
		SocketAddress remoteAddress = channel.readUdp(buf);
		if (remoteAddress == null) {
			// no datagram was immediately available
			return 0;
		}
		
		int readBytes = buf.position();
		String key = udpChannelKey(channel.getLocalAddress(), remoteAddress);
		if (!udpChannels.containsKey(key)) {
			// handle first datagram with current channel
			channel.setRemoteAddress(remoteAddress);
			udpChannels.put(key, channel);
		}
		channel.setLastIoTime(System.currentTimeMillis());
		fireChannelRead(channel, buf, buf.position());
		
		return readBytes;
	}
	
	private String udpChannelKey(SocketAddress localAddress, SocketAddress remoteAddress) {
		return localAddress.toString() + "-" + remoteAddress.toString();
	}
	
	/**
	 * Add the channel to the processor's flushing channel queue, and notify processor flush it immediately.
	 * 
	 * @param channel
	 */
	public void flush(NioByteChannel channel) {
		if (this.shutdown) {
			throw new IllegalStateException("The processor is already shutdown!");
		}
		
		if (channel == null) {
			return;
		}
		
		asyWrite(channel);
		wakeup();
	}
	
	private void asyWrite(NioByteChannel channel) {
		// Add channel to flushing queue, soon after it will be flushed in the same select loop.
		flushingChannels.add(channel);
	}
	
	private void flush() {
		int c = 0;
		while (!flushingChannels.isEmpty() && c < FLUSH_SPIN_COUNT) {
			NioByteChannel channel = flushingChannels.poll();
            if (channel == null) {
                // Just in case ... It should not happen.
                break;
            }
            
            // spin counter avoid infinite loop in this method.
            c++;
            
            try {
            	if (channel.isClosed() || channel.isClosing()) {
            		throw new IllegalStateException("Channel state is invalid, can not be flush, channel=" + channel);
            	} else {
            		flush0(channel);
            	}
			} catch (Throwable t) {
				LOG.error("Catch flush exception and fire it", t);
				
				// fire channel thrown event 
				fireChannelThrown(channel, t);
				
				// if it is IO exception close channel avoid infinite loop.
				if (t instanceof IOException) {
					asyClose(channel);
				}
			}
		}
	}
	
	private void flush0(NioByteChannel channel) throws IOException {
		if (LOG.isDebugEnabled()) { LOG.debug("Flushing channel=" + channel); }
		
		Queue<ByteBuffer> writeQueue = channel.getWriteBufferQueue();

		// First set not be interested to write event
		setInterestedInWrite(channel, false);
		
		// flush by mode
		if (config.isReadWritefair()) {
			fairFlush0(channel, writeQueue);
		} else {
			oneOffFlush0(channel, writeQueue);
		}
		
		// The write buffer queue is not empty, we re-interest in writing and later flush it.
		if (!writeQueue.isEmpty()) {
			setInterestedInWrite(channel, true);
			flushingChannels.add(channel);
		}
	}
	
	private void oneOffFlush0(NioByteChannel channel, Queue<ByteBuffer> writeQueue) throws IOException {
		ByteBuffer buf = writeQueue.peek();
		if (buf == null) {
			return;
		}
		
		write(channel, buf, buf.remaining());
		
		if (buf.hasRemaining()) {
			setInterestedInWrite(channel, true);
			flushingChannels.add(channel);
			return;
		} else {
			writeQueue.remove();
			
			// fire channel written event
			fireChannelWritten(channel, buf);
		}
	}
	
	private void fairFlush0(NioByteChannel channel, Queue<ByteBuffer> writeQueue) throws IOException {
		ByteBuffer buf = null;
		int writtenBytes = 0;
		final int maxWrittenBytes = channel.getMaxWriteBufferSize();
		if (LOG.isDebugEnabled()) { LOG.debug("Max write byte size=" + maxWrittenBytes); }
		
		do {
			if (buf == null) {
				buf = writeQueue.peek();
				if (buf == null) {
					return;
				}
			}
			
			int qota = maxWrittenBytes - writtenBytes;
			int localWrittenBytes = write(channel, buf, qota);
			
			if (LOG.isDebugEnabled()) {  LOG.debug("Flush buffer=" + new String(buf.array()) + " channel=" + channel + " written-bytes=" + localWrittenBytes + " buf-bytes=" + buf.array().length + " qota=" + qota + " buf-remaining=" + buf.hasRemaining()); }
			
			writtenBytes += localWrittenBytes;
			
			// The buffer is all flushed, remove it from write queue
			if (!buf.hasRemaining()) {
				if (LOG.isDebugEnabled()) { LOG.debug("The buffer is all flushed, remove it from write queue"); }
				
				writeQueue.remove();
				
				// fire channel written event
				fireChannelWritten(channel, buf);
				
				// set buf=null and the next loop if no byte buffer to write then break the loop.
				buf = null;
				continue;
			}

			// 0 byte be written, maybe kernel buffer is full so we re-interest in writing and later flush it.
			if (localWrittenBytes == 0) {
				if (LOG.isDebugEnabled()) { LOG.debug("Zero byte be written, maybe kernel buffer is full so we re-interest in writing and later flush it, channel=" + channel); }
				
				setInterestedInWrite(channel, true);
				flushingChannels.add(channel);
				return;
			}
			
			// The buffer isn't empty(bytes to flush more than max bytes), we re-interest in writing and later flush it.
			if (localWrittenBytes > 0 && buf.hasRemaining()) {
				if (LOG.isDebugEnabled()) { LOG.debug("The buffer isn't empty(bytes to flush more than max bytes), we re-interest in writing and later flush it, channel=" + channel); }
				
				setInterestedInWrite(channel, true);
				flushingChannels.add(channel);
				return;
			}

			// Wrote too much, so we re-interest in writing and later flush other bytes.
			if (writtenBytes >= maxWrittenBytes && buf.hasRemaining()) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Wrote too much, so we re-interest in writing and later flush other bytes, channel=" + channel);
				}
				
				setInterestedInWrite(channel, true);
				flushingChannels.add(channel);
				return;
			}
		} while (writtenBytes < maxWrittenBytes);
	}
	
	private void setInterestedInWrite(NioByteChannel channel, boolean isInterested) {
		SelectionKey key = channel.getSelectionKey();

		if (key == null || !key.isValid()) {
			return;
		}

		int oldInterestOps = key.interestOps();
		int newInterestOps = oldInterestOps;
		if (isInterested) {
			newInterestOps |= SelectionKey.OP_WRITE;
		} else {
			newInterestOps &= ~SelectionKey.OP_WRITE;
		}

		if (oldInterestOps != newInterestOps) {
            key.interestOps(newInterestOps);
        }
	}
	
	private int write(NioByteChannel channel, ByteBuffer buf, int maxLength) throws IOException {		
		int writtenBytes = 0;
		if (LOG.isDebugEnabled()) { LOG.debug("Allow write max len=" + maxLength + ", Waiting write byte buffer=" + buf); }

		if (buf.hasRemaining()) {
			int length = Math.min(buf.remaining(), maxLength);
			if (protocol.equals(IoProtocol.TCP)) {
				writtenBytes = writeTcp(channel, buf, length);
			} else if (protocol.equals(IoProtocol.UDP)) {
				writtenBytes = writeUdp(channel, buf, length);
			}
		}
		
		if (LOG.isDebugEnabled()) { LOG.debug("Actual written byte size=" + writtenBytes); }

		return writtenBytes;
	}
	
	private int writeTcp(NioByteChannel channel, ByteBuffer buf, int length) throws IOException {
		if (buf.remaining() <= length) {
			return channel.writeTcp(buf);
		}

		int oldLimit = buf.limit();
		buf.limit(buf.position() + length);
		try {
			return channel.writeTcp(buf);
		} finally {
			buf.limit(oldLimit);
		}
	}
	
	private int writeUdp(NioByteChannel channel, ByteBuffer buf, int length) throws IOException {
		if (buf.remaining() <= length) {
			return channel.writeUdp(buf, channel.getRemoteAddress());
		}

		int oldLimit = buf.limit();
		buf.limit(buf.position() + length);
		try {
			return channel.writeUdp(buf, channel.getRemoteAddress());
		} finally {
			buf.limit(oldLimit);
		}

	}
	
	/**
	 * Removes and closes the specified channel from the processor,
	 * so that processor closes the channel and releases any other related resources.
     * 
	 * @param channel
	 */
    void remove(NioByteChannel channel) {
    	if (this.shutdown) {
			throw new IllegalStateException("The processor is already shutdown!");
		}
		
		if (channel == null) {
			return;
		}
		
		asyClose(channel);
		wakeup();
    }
	
	// ~ -------------------------------------------------------------------------------------------------------------
    
    private void fireChannelOpened(NioByteChannel channel) {
    	dispatcher.dispatch(new NioHandlerByteChannelEvent(ChannelEventType.CHANNEL_OPENED, channel, handler));
    }
	
	private void fireChannelRead(NioByteChannel channel, ByteBuffer buf, int length) {
		// fire channel received event, here we copy buffer bytes to a new byte array to avoid handler expose <code>ByteBuffer</code> to end user.
		byte[] barr = new byte[length];
		System.arraycopy(buf.array(), 0, barr, 0, length);
		dispatcher.dispatch(new NioHandlerByteChannelEvent(ChannelEventType.CHANNEL_READ, channel, handler, barr));
	}
	
	private void fireChannelWritten(NioByteChannel channel, ByteBuffer buf) {
		dispatcher.dispatch(new NioHandlerByteChannelEvent(ChannelEventType.CHANNEL_WRITTEN, channel, handler, buf.array()));
	}
	
	private void fireChannelThrown(NioByteChannel channel, Throwable t) {
		dispatcher.dispatch(new NioHandlerByteChannelEvent(ChannelEventType.CHANNEL_THROWN, channel, handler, t));
	}
	
	private void fireChannelClosed(NioByteChannel channel) {
		dispatcher.dispatch(new NioHandlerByteChannelEvent(ChannelEventType.CHANNEL_CLOSED, channel, handler));
	}
	
	// ~ -------------------------------------------------------------------------------------------------------------

	private class ProcessThread implements Runnable {
		public void run() {
			while (!shutdown) {
				try {
					int selected = select();
					
					// flush channels
					flush();
					
					// register new channels
					register();
					
					if (selected > 0) { process(); }
					
					// close channels
					close();
					
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
	
	public void setProtocol(IoProtocol protocol) {
		this.protocol = protocol;
	}

}
