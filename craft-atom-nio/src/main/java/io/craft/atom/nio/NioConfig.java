package io.craft.atom.nio;

import io.craft.atom.io.IoConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * Nio component common configuration object.
 * 
 * @author mindwind
 * @version 1.0, Feb 21, 2013
 */
@ToString(callSuper = true, of = { "processorPoolSize", "executorSize", "readWritefair", "channelEventSize", "totalEventSize" })
abstract public class NioConfig extends IoConfig {

	
	@Getter         protected int     processorPoolSize = Runtime.getRuntime().availableProcessors();
	@Getter @Setter protected int     executorSize      = processorPoolSize << 3                    ;
	@Getter @Setter protected boolean readWritefair     = true                                      ;
	@Getter         protected int     channelEventSize  = Integer.MAX_VALUE                         ;
	@Getter         protected int     totalEventSize    = Integer.MAX_VALUE                         ;
	
	
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
