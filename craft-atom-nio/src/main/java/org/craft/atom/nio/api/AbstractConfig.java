package org.craft.atom.nio.api;

/**
 * Abstract config to extends.
 *
 * @author Hu Feng
 * @version 1.0, 2011-11-29
 */
public class AbstractConfig {
	
	public static final int MIN_READ_BUFFER_SIZE = 64;
	public static final int READ_BUFFER_SIZE = 1024;
	public static final int MAX_READ_BUFFER_SIZE = 65536;
	
	/** I/O processor pool size */
	protected int processorPoolSize = Runtime.getRuntime().availableProcessors();
	
	/** Min read buffer seize */
	protected int minReadBufferSize = MIN_READ_BUFFER_SIZE;
	
	/** Default read buffer size */
	protected int readBufferSize = READ_BUFFER_SIZE;
	
	/** Max read buffer size */
	protected int maxReadBufferSize = MAX_READ_BUFFER_SIZE;
	
	/** Executor size */
	protected int executorSize = processorPoolSize << 3;
	
	/** 10s default I/O time out, the value must > 1s */
	protected int ioTimeoutInMillis = 10000;
	
	/** Processor flush read-write fair mode */
	protected boolean readWritefair = true;
	
	public int getProcessorPoolSize() {
		return processorPoolSize;
	}

	public void setProcessorPoolSize(int processorPoolSize) {
		if(processorPoolSize <= 0) {
			throw new IllegalArgumentException("processor pool size must > 0");
		}
		
		this.processorPoolSize = processorPoolSize;
	}

	public int getMinReadBufferSize() {
		return minReadBufferSize;
	}

	public void setMinReadBufferSize(int minReadBufferSize) {
		this.minReadBufferSize = minReadBufferSize;
	}

	public int getReadBufferSize() {
		return readBufferSize;
	}

	public void setReadBufferSize(int readBufferSize) {
		this.readBufferSize = readBufferSize;
	}

	public int getMaxReadBufferSize() {
		return maxReadBufferSize;
	}

	public void setMaxReadBufferSize(int maxReadBufferSize) {
		this.maxReadBufferSize = maxReadBufferSize;
	}

	public int getExecutorSize() {
		return executorSize;
	}

	public void setExecutorSize(int executorSize) {
		this.executorSize = executorSize;
	}

	public int getIoTimeoutInMillis() {
		return ioTimeoutInMillis;
	}

	public void setIoTimeoutInMillis(int ioTimeoutInMillis) {
		if (ioTimeoutInMillis <= 1000) {
			this.ioTimeoutInMillis = 2000;
		}
		
		this.ioTimeoutInMillis = ioTimeoutInMillis;
	}

	public boolean isReadWritefair() {
		return readWritefair;
	}

	public void setReadWritefair(boolean readWritefair) {
		this.readWritefair = readWritefair;
	}

	@Override
	public String toString() {
		return String
				.format("AbstractConfig [processorPoolSize=%s, minReadBufferSize=%s, readBufferSize=%s, maxReadBufferSize=%s, executorSize=%s, ioTimeoutInMillis=%s, readWritefair=%s]",
						processorPoolSize, minReadBufferSize, readBufferSize,
						maxReadBufferSize, executorSize, ioTimeoutInMillis,
						readWritefair);
	}
	
}
