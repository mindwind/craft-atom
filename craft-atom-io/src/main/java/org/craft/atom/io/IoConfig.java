package org.craft.atom.io;

/**
 * I/O common configuration object
 * 
 * @author mindwind
 * @version 1.0, Feb 22, 2013
 */
public abstract class IoConfig {

	public static final int MIN_READ_BUFFER_SIZE = 64;
	public static final int DEFAULT_READ_BUFFER_SIZE = 2048;
	public static final int MAX_READ_BUFFER_SIZE = 65536;

	protected int minReadBufferSize = MIN_READ_BUFFER_SIZE;
	protected int defaultReadBufferSize = DEFAULT_READ_BUFFER_SIZE;
	protected int maxReadBufferSize = MAX_READ_BUFFER_SIZE;
	protected int ioTimeoutInMillis = 30000;
	
	// ~ -------------------------------------------------------------------------------------------------------------

	public int getMinReadBufferSize() {
		return minReadBufferSize;
	}

	public void setMinReadBufferSize(int minReadBufferSize) {
		this.minReadBufferSize = minReadBufferSize;
	}

	public int getDefaultReadBufferSize() {
		return defaultReadBufferSize;
	}

	public void setDefaultReadBufferSize(int defaultReadBufferSize) {
		this.defaultReadBufferSize = defaultReadBufferSize;
	}

	public int getMaxReadBufferSize() {
		return maxReadBufferSize;
	}

	public void setMaxReadBufferSize(int maxReadBufferSize) {
		this.maxReadBufferSize = maxReadBufferSize;
	}

	public int getIoTimeoutInMillis() {
		return ioTimeoutInMillis;
	}

	public void setIoTimeoutInMillis(int ioTimeoutInMillis) {
		if (ioTimeoutInMillis <= 0) {
			this.ioTimeoutInMillis = 10000;
		}
		
		this.ioTimeoutInMillis = ioTimeoutInMillis;
	}

	@Override
	public String toString() {
		return String
				.format("IoConfig [minReadBufferSize=%s, defaultReadBufferSize=%s, maxReadBufferSize=%s, ioTimeoutInMillis=%s]",
						minReadBufferSize, defaultReadBufferSize,
						maxReadBufferSize, ioTimeoutInMillis);
	}

}
