package org.craft.atom.nio;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.craft.atom.io.AbstractIoByteChannel;
import org.craft.atom.io.ChannelState;
import org.craft.atom.nio.spi.NioBufferSizePredictor;

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
	protected NioBufferSizePredictor predictor;
	
	private Queue<ByteBuffer> writeBufferQueue = new ConcurrentLinkedQueue<ByteBuffer>();
	private Queue<NioByteChannelEvent> eventQueue = new ConcurrentLinkedQueue<NioByteChannelEvent>();
	private NioProcessor processor;
	private final Object lock = new Object();
	private volatile boolean eventProcessing = false;
	
	// ~ ------------------------------------------------------------------------------------------------------------

	public NioByteChannel(NioConfig config, NioBufferSizePredictor predictor) {
		super(config.getMinReadBufferSize(), config.getDefaultReadBufferSize(), config.getMaxReadBufferSize());
		this.predictor = predictor;
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
		
		this.setLastIoTime(System.currentTimeMillis());
		getWriteBufferQueue().add(ByteBuffer.wrap(data));
		processor.flush(this);
		
		return true;
	}
	
	public void setProcessor(NioProcessor processor) {
		this.processor = processor;
	}
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	void add(NioByteChannelEvent event) {
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
	
	boolean isClosing() {
		return state == ChannelState.CLOSING;
	}
	
	boolean isClosed() {
		return state == ChannelState.CLOSED;
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
	
	Queue<NioByteChannelEvent> getEventQueue() {
		return eventQueue;
	}
	
	boolean isEventProcessing() {
		return eventProcessing;
	}
	
	void setEventProcessing(boolean eventProcessing) {
		this.eventProcessing = eventProcessing;
	}
	
	boolean isReadable() {
		return selectionKey.isValid() && selectionKey.isReadable();
	}
	
	boolean isWritable() {
		return selectionKey.isValid() && selectionKey.isWritable();
	}
	
	NioBufferSizePredictor getPredictor() {
		return predictor;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[id=").append(id).append(" ").append(remoteAddress).append(" -> ").append(localAddress).append("]");
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
