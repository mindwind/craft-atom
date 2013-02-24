package org.craft.atom.io;

/**
 * Base implementation class for common io concept of channel
 * 
 * @author mindwind
 * @version 1.0, Feb 22, 2013
 */
public abstract class AbstractIoChannel extends AbstractChannel {

	protected long lastIoTime = System.currentTimeMillis();
	protected int minReadBufferSize = IoConfig.MIN_READ_BUFFER_SIZE;
	protected int defaultReadBufferSize = IoConfig.DEFAULT_READ_BUFFER_SIZE;
	protected int maxReadBufferSize = IoConfig.MAX_READ_BUFFER_SIZE;
	protected int maxWriteBufferSize = maxReadBufferSize + (maxReadBufferSize >>> 1);
	
	// ~ -----------------------------------------------------------------------------------------------------------
	
	public AbstractIoChannel() {
		super();
	}

	public AbstractIoChannel(long id) {
		super(id);
	}
	
	public AbstractIoChannel(int minReadBufferSize) {
		super();
		this.minReadBufferSize = minReadBufferSize;
	}
	
	public AbstractIoChannel(int minReadBufferSize, int defaultReadBufferSize) {
		super();
		this.minReadBufferSize = minReadBufferSize;
		this.defaultReadBufferSize = defaultReadBufferSize;
	}
	
	public AbstractIoChannel(int minReadBufferSize, int defaultReadBufferSize, int maxReadBufferSize) {
		super();
		this.minReadBufferSize = minReadBufferSize;
		this.defaultReadBufferSize = defaultReadBufferSize;
		this.maxReadBufferSize = maxReadBufferSize;
	}
	
	public AbstractIoChannel(long lastIoTime, int minReadBufferSize, int defaultReadBufferSize, int maxReadBufferSize, int maxWriteBufferSize) {
		super();
		this.lastIoTime = lastIoTime;
		this.minReadBufferSize = minReadBufferSize;
		this.defaultReadBufferSize = defaultReadBufferSize;
		this.maxReadBufferSize = maxReadBufferSize;
		this.maxWriteBufferSize = maxWriteBufferSize;
	}

	// ~ -----------------------------------------------------------------------------------------------------------

	public long getLastIoTime() {
		return lastIoTime;
	}

	public void setLastIoTime(long lastIoTime) {
		this.lastIoTime = lastIoTime;
	}

	public int getMinReadBufferSize() {
		return minReadBufferSize;
	}

	public void setMinReadBufferSize(int minReadBufferSize) {
		if (minReadBufferSize <= 0) {
            throw new IllegalArgumentException("minReadBufferSize: " + minReadBufferSize + " (expected: 1+)");
        }
		
        if (minReadBufferSize > defaultReadBufferSize ) {
            throw new IllegalArgumentException("minReadBufferSize: " + minReadBufferSize + " (expected: smaller than " + defaultReadBufferSize + ')');
        }
        
		this.minReadBufferSize = minReadBufferSize;
	}

	public int getDefaultReadBufferSize() {
		return defaultReadBufferSize;
	}

	public void setDefaultReadBufferSize(int defaultReadBufferSize) {
		if (defaultReadBufferSize < minReadBufferSize) {
			defaultReadBufferSize = this.minReadBufferSize;
		}
		
		if (defaultReadBufferSize > maxReadBufferSize) {
			defaultReadBufferSize = this.maxReadBufferSize;
		}
		
		this.defaultReadBufferSize = defaultReadBufferSize;
	}

	public int getMaxReadBufferSize() {
		return maxReadBufferSize;
	}

	public void setMaxReadBufferSize(int maxReadBufferSize) {
		if (maxReadBufferSize <= 0) {
			throw new IllegalArgumentException("maxReadBufferSize: " + maxReadBufferSize + " (expected: > 1)");
		}
		
		if (maxReadBufferSize < defaultReadBufferSize) {
            throw new IllegalArgumentException("maxReadBufferSize: " + maxReadBufferSize + " (expected: greater than " + defaultReadBufferSize + ')');
        }
		
		this.maxReadBufferSize = maxReadBufferSize;
	}
	
	public int getMaxWriteBufferSize() {
		return this.maxWriteBufferSize;
	}

	public void setMaxWriteBufferSize(int maxWriteBufferSize) {
		if (maxWriteBufferSize < maxReadBufferSize) {
			maxWriteBufferSize = maxReadBufferSize;
		}
		
		this.maxWriteBufferSize = maxWriteBufferSize;
	}

	@Override
	public String toString() {
		return String
				.format("AbstractIoChannel [lastIoTime=%s, minReadBufferSize=%s, defaultReadBufferSize=%s, maxReadBufferSize=%s, maxWriteBufferSize=%s, id=%s, attributes=%s, state=%s]",
						lastIoTime, minReadBufferSize, defaultReadBufferSize,
						maxReadBufferSize, maxWriteBufferSize, id, attributes,
						state);
	}

}
