package org.craft.atom.nio;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.craft.atom.nio.api.AbstractConfig;
import org.craft.atom.nio.api.Session;
import org.craft.atom.nio.spi.SizePredictor;

/**
 * @author Hu Feng
 * @version 1.0, 2011-11-10
 */
public abstract class AbstractSession implements Session {
	
	private static final AtomicLong ID_GENERATOR = new AtomicLong(0);
	
	protected long id;
	protected SocketAddress localAddress;
	protected SocketAddress remoteAddress;
	private SelectionKey selectionKey;
	private long lastIoTime = System.currentTimeMillis();
	private int minReadBufferSize = AbstractConfig.MIN_READ_BUFFER_SIZE;
	private int readBufferSize = AbstractConfig.READ_BUFFER_SIZE;
	private int maxReadBufferSize = AbstractConfig.MAX_READ_BUFFER_SIZE;
	private Queue<ByteBuffer> writeBufferQueue = new ConcurrentLinkedQueue<ByteBuffer>();
	private Queue<Event> eventQueue = new ConcurrentLinkedQueue<Event>();
	private Map<Object, Object> attributes = new ConcurrentHashMap<Object, Object>(4);
	private SizePredictor sizePredictor = new AdaptiveSizePredictor();
	
	/** read/write fairness from mina, it is a trade off for tps or fast response */
	private int maxWriteBufferSize = maxReadBufferSize + (maxReadBufferSize >>> 1);
	
	/** hold the reference of processor which process the session */
	private Processor processor;
	
	private final Object lock = new Object();
	private final AtomicBoolean scheduledForFlush = new AtomicBoolean(false);
	private volatile boolean eventProcessing = false;
	
	/** session state */
	private volatile State state = State.CREATED;
	private enum State { CREATED, OPENED, CLOSING, CLOSED; }
	
	
	// ~ ------------------------------------------------------------------------------------------------------------

	public AbstractSession(AbstractConfig config, SizePredictor sizePredictor) {
		this.id = ID_GENERATOR.incrementAndGet();
		this.setMinReadBufferSize(config.getMinReadBufferSize());
		this.setMaxReadBufferSize(config.getMaxReadBufferSize());
		this.setReadBufferSize(config.getReadBufferSize());
		this.setSizePredictor(sizePredictor);
	}
	
	public AbstractSession(AbstractSession session) {
		this.id = ID_GENERATOR.incrementAndGet();
		
		this.localAddress = session.getLocalAddress();
		this.remoteAddress = session.getRemoteAddress();
		this.lastIoTime = session.getLastIoTime();
		this.minReadBufferSize = session.getMinReadBufferSize();
		this.readBufferSize = session.getReadBufferSize();
		this.maxReadBufferSize = session.getMaxReadBufferSize();
		this.selectionKey = session.getSelectionKey();
		this.writeBufferQueue = session.getWriteBufferQueue();
		this.eventQueue = session.getEventQueue();
		this.attributes = session.getAttributes();
		this.maxWriteBufferSize = session.getMaxWriteBufferSize();
		this.processor = session.getProcessor();
		this.sizePredictor = session.getSizePredictor();
	}
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	@Override
	public long getId() {
		return id;
	}

	@Override
	public SocketAddress getRemoteAddress() {
		return remoteAddress;
	}
	
	public void setRemoteAddress(SocketAddress remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	@Override
	public SocketAddress getLocalAddress() {
		return localAddress;
	}

	@Override
	public void setSelectionKey(SelectionKey key) {
		this.selectionKey = key;
	}

	@Override
	public SelectionKey getSelectionKey() {
		return selectionKey;
	}

	@Override
	public void setMaxReadBufferSize(int size) {
		if(size <= 0) {
			throw new IllegalArgumentException("maxReadBufferSize: " + maxReadBufferSize + " (expected: 1+)");
		}
		
		if (size < readBufferSize) {
            throw new IllegalArgumentException("maxReadBufferSize: " + maxReadBufferSize + " (expected: greater than " + readBufferSize + ')');
        }
		
		this.maxReadBufferSize = size;
	}

	@Override
	public int getMaxReadBufferSize() {
		return this.maxReadBufferSize;
	}

	@Override
	public void setMaxWriteBufferSize(int size) {
		if(size < maxReadBufferSize) {
			size = maxReadBufferSize;
		}
		
		this.maxWriteBufferSize = size;
	}

	@Override
	public int getMaxWriteBufferSize() {
		return this.maxWriteBufferSize;
	}

	@Override
	public void setMinReadBufferSize(int size) {
		if (size <= 0) {
            throw new IllegalArgumentException("minReadBufferSize: " + minReadBufferSize + " (expected: 1+)");
        }
		
        if (size > readBufferSize ) {
            throw new IllegalArgumentException("minReadBufferSize: " + minReadBufferSize + " (expected: smaller than " + readBufferSize + ')');
        }
        
		this.minReadBufferSize = size;
	}

	@Override
	public int getMinReadBufferSize() {
		return minReadBufferSize;
	}

	@Override
	public void setReadBufferSize(int size) {
		if (size < minReadBufferSize) {
			size = this.minReadBufferSize;
		}
		
		if (size > maxReadBufferSize) {
			size = this.maxReadBufferSize;
		}
		
		this.readBufferSize = size;
	}

	@Override
	public int getReadBufferSize() {
		return readBufferSize;
	}

	@Override
	public Queue<ByteBuffer> getWriteBufferQueue() {
		return writeBufferQueue;
	}

	@Override
	public void close() {
		synchronized (lock) {
            if (isClosing() || isClosed()) {
                return;
            }
        }
		
		getProcessor().remove(this);
	}

	@Override
	public void write(byte[] bytes) {
		if (!this.isValid()) {
			throw new IllegalStateException("Session state is invalid, session id=" + id);
		}
		
		this.setLastIoTime(System.currentTimeMillis());
		getWriteBufferQueue().add(ByteBuffer.wrap(bytes));
		getProcessor().flush(this);
	}
	
	@Override
	public Queue<Event> getEventQueue() {
		return eventQueue;
	}
	
	@Override
	public long getLastIoTime() {
		return lastIoTime;
	}

	public void add(Event event) {
		eventQueue.offer(event);
	}
	
	@Override
	public Object getAttribute(Object key) {
		if (key == null) {
            throw new IllegalArgumentException("key");
        }

        return attributes.get(key);
	}

	@Override
	public Object setAttribute(Object key, Object value) {
		if (key == null) {
            throw new IllegalArgumentException("key");
        }

        if (value == null) {
            return attributes.remove(key);
        }
        
        return attributes.put(key, value);
	}
	
	@Override
	public boolean containsAttribute(Object key) {
		return attributes.containsKey(key);
	}
	
	/**
     * Change the session's status : it's not anymore scheduled for flush
     */
    public final void unscheduledForFlush() {
        scheduledForFlush.set(false);
    }
	
	/**
     * Tells if the session is scheduled for flushed
     *
     * @param true if the session is scheduled for flush
     */
    public final boolean isScheduledForFlush() {
        return scheduledForFlush.get();
    }
    
    /**
     * Set the scheduledForFLush flag. As we may have concurrent access to this
     * flag, we compare and set it in one call.
     *
     * @param schedule
     *            the new value to set if not already set.
     * @return true if the session flag has been set, and if it wasn't set
     *         already.
     */
    public final boolean setScheduledForFlush(boolean schedule) {
        if (schedule) {
            // If the current tag is set to false, switch it to true,
            // otherwise, we do nothing but return false : the session
            // is already scheduled for flush
            return scheduledForFlush.compareAndSet(false, schedule);
        }

        scheduledForFlush.set(schedule);
        return true;
    }

	
	// ~ ---------------------------------------------------------------------------------------------------------------
	
	public Processor getProcessor() {
		return processor;
	}

	public void setProcessor(Processor processor) {
		this.processor = processor;
	}

	public boolean isEventProcessing() {
		return eventProcessing;
	}

	public void setEventProcessing(boolean eventProcessing) {
		this.eventProcessing = eventProcessing;
	}
	
	@Override
	public void setLastIoTime(long lastIoTime) {
		this.lastIoTime = lastIoTime;
	}

	public Map<Object, Object> getAttributes() {
		return attributes;
	}
	
	@Override
	public boolean isClosing() {
		return state == State.CLOSING;
	}

	public void setClosing() {
		this.state = State.CLOSING;
	}
	
	public void setClosed() {
		this.state = State.CLOSED;
	}
	
	public void setOpened() {
		this.state = State.OPENED;
	}
	
	public boolean isClosed() {
		return state == State.CLOSED;
	}
	
	public boolean isOpened() {
		return state == State.OPENED;
	}
	
	public SizePredictor getSizePredictor() {
		return sizePredictor;
	}

	public void setSizePredictor(SizePredictor sizePredictor) {
		this.sizePredictor = sizePredictor;
	}

	public State getState() {
		return state;
	}

	@Override
	public boolean isValid() {		
		if (isClosing()) {
			return false;
		}
		
		if (isClosed()) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[id=").append(id).append(" ").append(remoteAddress)
		       .append(" -> ").append(localAddress).append("]");
		
		return builder.toString();
	}
	
	public String toFullString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Session [id=").append(id)
			   .append(", localAddress=").append(localAddress)
			   .append(", remoteAddress=").append(remoteAddress)
			   .append(", key=").append(selectionKey).append(", lastIoTime=")
			   .append(lastIoTime).append(", minReadBufferSize=")
			   .append(minReadBufferSize).append(", readBufferSize=")
			   .append(readBufferSize).append(", maxReadBufferSize=")
			   .append(maxReadBufferSize).append(", writeBufferQueue=")
			   .append(writeBufferQueue).append(", eventQueue=")
			   .append(eventQueue).append(", attributes=").append(attributes)
			   .append(", maxWriteBufferSize=").append(maxWriteBufferSize)
			   .append(", processor=").append(processor)
			   .append(", eventProcessing=").append(eventProcessing)
			   .append(", state=").append(state)
			   .append(", sizePredictor").append(sizePredictor)
			   .append("]");
		
		return builder.toString();
	}

}
