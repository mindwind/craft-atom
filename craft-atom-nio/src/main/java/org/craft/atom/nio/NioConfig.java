package org.craft.atom.nio;

import org.craft.atom.io.IoConfig;

/**
 * Nio component common configuration object.
 * 
 * @author mindwind
 * @version 1.0, Feb 21, 2013
 */
abstract public class NioConfig extends IoConfig {

	/** I/O processor pool size */
	protected int processorPoolSize = Runtime.getRuntime().availableProcessors();
	
	/** Executor size */
	protected int executorSize = processorPoolSize << 3;
	
	/** Processor flush read-write fair mode */
	protected boolean readWritefair = true;
	
	/** The queued events size per channel, zero means no limit. */
	protected int channelEventSize = 0;
	
	/** The total queued events size, zero means no limit.  */
	protected int totalEventSize = 0;
	
	// ~ -------------------------------------------------------------------------------------------------------------

	public int getProcessorPoolSize() {
		return processorPoolSize;
	}

	public void setProcessorPoolSize(int processorPoolSize) {
		if (processorPoolSize <= 0) {
			throw new IllegalArgumentException("processor pool size must > 0");
		}
		
		this.processorPoolSize = processorPoolSize;
	}

	public int getExecutorSize() {
		return executorSize;
	}

	public void setExecutorSize(int executorSize) {
		this.executorSize = executorSize;
	}

	public boolean isReadWritefair() {
		return readWritefair;
	}

	public void setReadWritefair(boolean readWritefair) {
		this.readWritefair = readWritefair;
	}

	public int getChannelEventSize() {
		return channelEventSize;
	}

	public void setChannelEventSize(int channelEventSize) {
		this.channelEventSize = channelEventSize;
	}

	public int getTotalEventSize() {
		return totalEventSize;
	}

	public void setTotalEventSize(int totalEventSize) {
		this.totalEventSize = totalEventSize;
	}

	@Override
	public String toString() {
		return String
				.format("NioConfig [processorPoolSize=%s, executorSize=%s, readWritefair=%s, channelEventSize=%s, totalEventSize=%s, minReadBufferSize=%s, defaultReadBufferSize=%s, maxReadBufferSize=%s, ioTimeoutInMillis=%s]",
						processorPoolSize, executorSize, readWritefair,
						channelEventSize, totalEventSize, minReadBufferSize,
						defaultReadBufferSize, maxReadBufferSize,
						ioTimeoutInMillis);
	}
	
}
