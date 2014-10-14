package org.craft.atom.nio;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.ToString;

import org.craft.atom.io.AbstractIoByteChannel;
import org.craft.atom.io.ChannelEvent;
import org.craft.atom.io.ChannelState;
import org.craft.atom.io.IllegalChannelStateException;
import org.craft.atom.nio.spi.NioBufferSizePredictor;
import org.craft.atom.nio.spi.NioChannelEventDispatcher;

/**
 * Channel transmit bytes base nio
 * 
 * @author mindwind
 * @version 1.0, Feb 21, 2013
 * @see NioTcpByteChannel
 * @see NioUdpByteChannel
 */
@ToString(callSuper = true, of = { "localAddress", "remoteAddress" })
abstract public class NioByteChannel extends AbstractIoByteChannel {
	                   
	
	protected          SocketAddress               localAddress                                                        ;
	protected          SocketAddress               remoteAddress                                                       ;
	protected          SelectionKey                selectionKey                                                        ;
	protected          NioProcessor                processor                                                           ;
	protected final    Semaphore                   semaphore                                                           ;
	protected final    NioChannelEventDispatcher   dispatcher                                                          ;
	protected final    NioBufferSizePredictor      predictor                                                           ;
	protected final    Queue<ByteBuffer>           writeBufferQueue = new ConcurrentLinkedQueue<ByteBuffer>()          ;
	protected final    Queue<ChannelEvent<byte[]>> eventQueue       = new ConcurrentLinkedQueue<ChannelEvent<byte[]>>();
	protected final    Object                      lock             = new Object()                                     ;
	protected final    AtomicBoolean               scheduleFlush    = new AtomicBoolean(false)                         ;
	protected volatile boolean                     eventProcessing  = false                                            ;
	
	
	// ~ ------------------------------------------------------------------------------------------------------------

	
	public NioByteChannel(NioConfig config, NioBufferSizePredictor predictor, NioChannelEventDispatcher dispatcher) {
		super(config.getMinReadBufferSize(), config.getDefaultReadBufferSize(), config.getMaxReadBufferSize());
		this.semaphore = new Semaphore(config.getChannelEventSize(), false);
		this.predictor = predictor;
		this.dispatcher = dispatcher;
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------------

	
	@Override
	public void close() {
		synchronized (lock) {
            if (isClosing() || isClosed()) {
                return;
            }
        }
		
		processor.remove(this);
	}

	@Override
	public boolean write(byte[] data) throws IllegalChannelStateException {
		if (isClosed())   { throw new IllegalChannelStateException("Channel is closed"); }
		if (isClosing())  { throw new IllegalChannelStateException("Channel is closing"); }
		if (isPaused())   { throw new IllegalChannelStateException("Channel is paused"); }
		if (data == null) { return false; }
		
		setLastIoTime(System.currentTimeMillis());
		getWriteBufferQueue().add(ByteBuffer.wrap(data));
		processor.flush(this);
		return true;
	}
	
	@Override
	public Queue<byte[]> getWriteQueue() {
		Queue<byte[]> q = new LinkedBlockingQueue<byte[]>();
		for (ByteBuffer buf : writeBufferQueue) {
			q.add(buf.array());
		}
		return q;
	}
	
	@Override
	public SocketAddress getLocalAddress() {
		return localAddress;
	}
	
	@Override
	public SocketAddress getRemoteAddress() {
		return remoteAddress;
	}
	
	public void setProcessor(NioProcessor processor) {
		this.processor = processor;
	}
	
	public boolean tryAcquire() {
		return semaphore.tryAcquire();
	}
	
	public void release() {
		semaphore.release();
	}
	
	public int availablePermits() {
		return semaphore.availablePermits();
	}
	
    public void unsetScheduleFlush() {
    	scheduleFlush.set(false);
    }
    
    public boolean setScheduleFlush(boolean schedule) {
        if (schedule) {
            return scheduleFlush.compareAndSet(false, schedule);
        }

        scheduleFlush.set(schedule);
        return true;
    }
	
    
	// ~ ------------------------------------------------------------------------------------------------------------
	
    
	void add(ChannelEvent<byte[]> event) {
		eventQueue.offer(event);
	}
	
	boolean isValid() {		
		if (isClosing()) {
			return false;
		}
		
		if (isClosed()) {
			return false;
		}
		
		return true;
	}
	
	void setClosing() {
		this.state = ChannelState.CLOSING;
	}
	
	void setClosed() {
		this.state = ChannelState.CLOSED;
	}
	
	void setRemoteAddress(SocketAddress remoteAddress) {
		this.remoteAddress = remoteAddress;
	}
	
	SelectionKey getSelectionKey() {
		return selectionKey;
	}
	
	void setSelectionKey(SelectionKey key) {
		this.selectionKey = key;
	}
	
	Queue<ByteBuffer> getWriteBufferQueue() {
		return writeBufferQueue;
	}
	
	Queue<ChannelEvent<byte[]>> getEventQueue() {
		return eventQueue;
	}
	
	boolean isEventProcessing() {
		return eventProcessing;
	}
	
	void setEventProcessing(boolean eventProcessing) {
		this.eventProcessing = eventProcessing;
	}
	
	boolean isReadable() {
		return isOpen() && selectionKey.isValid() && selectionKey.isReadable();
	}
	
	boolean isWritable() {
		return (isOpen() || isPaused()) && selectionKey.isValid() && selectionKey.isWritable();
	}
	
	NioBufferSizePredictor getPredictor() {
		return predictor;
	}

	
	// ~ ------------------------------------------------------------------------------------------------------------

	
	protected void close0() throws IOException { /* override this */ }
	protected int readTcp(ByteBuffer buf) throws IOException { return 0; /* override this */ }
	protected int writeTcp(ByteBuffer buf) throws IOException { return 0; /* override this */ }
	protected int writeUdp(ByteBuffer buf, SocketAddress target) throws IOException { return 0; /* override */ }
	protected SocketAddress readUdp(ByteBuffer buf) throws IOException { return null; /* override this */ }
	abstract protected SelectableChannel innerChannel();
	
}
