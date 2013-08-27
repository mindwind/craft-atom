package org.craft.atom.nio;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.craft.atom.io.IoConfig;

/**
 * Nio component common configuration object.
 * 
 * @author mindwind
 * @version 1.0, Feb 21, 2013
 */
@ToString(callSuper = true, of = { "processorPoolSize", "executorSize", "readWritefair", "channelEventSize", "totalEventSize" })
abstract public class NioConfig extends IoConfig {

	/** I/O processor pool size */
	@Getter protected int processorPoolSize = Runtime.getRuntime().availableProcessors();
	
	/** Executor size */
	@Getter @Setter protected int executorSize = processorPoolSize << 3;
	
	/** Processor flush read-write fair mode */
	@Getter @Setter protected boolean readWritefair = true;
	
	/** The queued events size per channel */
	@Getter protected int channelEventSize = Integer.MAX_VALUE;
	
	/** The total queued events size */
	@Getter protected int totalEventSize = Integer.MAX_VALUE;
	
	// ~ -------------------------------------------------------------------------------------------------------------

	public void setProcessorPoolSize(int processorPoolSize) {
		if (processorPoolSize <= 0) {
			throw new IllegalArgumentException("processor pool size must > 0");
		}
		
		this.processorPoolSize = processorPoolSize;
	}

	public void setChannelEventSize(int channelEventSize) {
		if (channelEventSize <= 0) {
			channelEventSize = Integer.MAX_VALUE;
		}
		
		this.channelEventSize = channelEventSize;
	}

	public void setTotalEventSize(int totalEventSize) {		
		if (totalEventSize <= 0) {
			totalEventSize = Integer.MAX_VALUE;
		}
		
		this.totalEventSize = totalEventSize;
	}
	
}
