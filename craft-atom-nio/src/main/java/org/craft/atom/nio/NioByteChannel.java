package org.craft.atom.nio;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import org.craft.atom.io.AbstractIoByteChannel;
import org.craft.atom.io.ChannelEvent;
import org.craft.atom.io.ChannelEventType;
import org.craft.atom.io.ChannelState;
import org.craft.atom.nio.spi.NioBufferSizePredictor;
import org.craft.atom.nio.spi.NioChannelEventDispatcher;
import org.craft.atom.util.NamedThreadFactory;

/**
 * Channel transmit bytes base nio
 * 
 * @author mindwind
 * @version 1.0, Feb 21, 2013
 * @see NioTcpByteChannel
 * @see NioUdpByteChannel
 */
abstract public class NioByteChannel extends AbstractIoByteChannel {
	
	protected SocketAddress localAddress;
	protected SocketAddress remoteAddress;
	protected SelectionKey selectionKey;
	protected NioProcessor processor;
	
	protected final Semaphore semaphore;
	protected final NioChannelEventDispatcher dispatcher;
	protected final NioBufferSizePredictor predictor;
	protected final Queue<ByteBuffer> writeBufferQueue = new ConcurrentLinkedQueue<ByteBuffer>();
	protected final Queue<ChannelEvent<byte[]>> eventQueue = new ConcurrentLinkedQueue<ChannelEvent<byte[]>>();
	protected final Executor executor = Executors.newCachedThreadPool(new NamedThreadFactory("craft-atom-nio-writer"));
	protected final Object lock = new Object();
	
	protected volatile boolean eventProcessing = false;
	
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
	public boolean write(byte[] data) {
		if (!isValid()) {
			throw new IllegalStateException("Channel state is invalid, channel=" + this.toString());
		}
		if (data == null) {
			throw new IllegalArgumentException("Write data is null.");
		}
		
		if (isPaused()) {
			// channel paused, reject I/O operation
			return false;
		}
		
		setLastIoTime(System.currentTimeMillis());
		final NioProcessorByteChannelEvent event = new NioProcessorByteChannelEvent(ChannelEventType.CHANNEL_WRITTEN, this, processor, data);
		executor.execute(new Runnable() {
			@Override
			public void run() {
				dispatcher.dispatch(event);
			}
		});
		
		return true;
	}
	
	public void setProcessor(NioProcessor processor) {
		this.processor = processor;
	}
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	boolean tryAcquire() {
		return semaphore.tryAcquire();
	}
	
	void release() {
		semaphore.release();
	}
	
	int availablePermits() {
		return semaphore.availablePermits();
	}
	
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
	
	SocketAddress getRemoteAddress() {
		return remoteAddress;
	}
	
	void setRemoteAddress(SocketAddress remoteAddress) {
		this.remoteAddress = remoteAddress;
	}
	
	SocketAddress getLocalAddress() {
		return localAddress;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[id=").append(id).append(" ").append(localAddress).append(" <-> ").append(remoteAddress).append("]");
		return builder.toString();
	}
	
	// ~ ------------------------------------------------------------------------------------------------------------

	protected void close0() throws IOException { /* override this */ }
	protected int readTcp(ByteBuffer buf) throws IOException { return 0; /* override this */ }
	protected int writeTcp(ByteBuffer buf) throws IOException { return 0; /* override this */ }
	protected int writeUdp(ByteBuffer buf, SocketAddress target) throws IOException { return 0; /* override */ }
	protected SocketAddress readUdp(ByteBuffer buf) throws IOException { return null; /* override this */ }
	abstract protected SelectableChannel innerChannel();
	
}
