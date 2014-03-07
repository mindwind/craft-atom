package org.craft.atom.nio.api;

import org.craft.atom.io.IoHandler;
import org.craft.atom.nio.NioAdaptiveBufferSizePredictorFactory;
import org.craft.atom.nio.NioConfig;
import org.craft.atom.nio.NioOrderedThreadPoolChannelEventDispatcher;
import org.craft.atom.nio.spi.NioBufferSizePredictorFactory;
import org.craft.atom.nio.spi.NioChannelEventDispatcher;


/**
 * @author mindwind
 * @version 1.0, Mar 7, 2014
 */
public abstract class NioBuilder {
	
	protected final   IoHandler                     handler                                                             ;
	protected         NioChannelEventDispatcher     dispatcher        = new NioOrderedThreadPoolChannelEventDispatcher();
	protected         NioBufferSizePredictorFactory predictorFactory  = new NioAdaptiveBufferSizePredictorFactory()     ;
	protected         int                           readBufferSize    = 2048                                            ;
	protected         int                           minReadBufferSize = 64                                              ;
	protected         int                           maxReadBufferSize = 65536                                           ;
	protected         int                           ioTimeoutInMillis = 120 * 1000                                      ;
	protected         int                           processorPoolSize = Runtime.getRuntime().availableProcessors()      ;
	protected         int                           executorSize      = processorPoolSize << 3                          ;
	protected         int                           channelEventSize  = Integer.MAX_VALUE                               ;
	protected         int                           totalEventSize    = Integer.MAX_VALUE                               ;
	protected         boolean                       readWriteFair     = true                                            ;
	
	
	public NioBuilder(IoHandler handler) {
		this.handler = handler;
	}
	
	
	public NioBuilder minReadBufferSize(int size)                              { this.minReadBufferSize = size;       return this; }
	public NioBuilder maxReadBufferSize(int size)                              { this.maxReadBufferSize = size;       return this; }
	public NioBuilder readBufferSize   (int size)                              { this.readBufferSize    = size;       return this; }
	public NioBuilder processorPoolSize(int size)                              { this.processorPoolSize = size;       return this; }
	public NioBuilder executorSize     (int size)                              { this.executorSize      = size;       return this; }
	public NioBuilder channelEventSize (int size)                              { this.channelEventSize  = size;       return this; }
	public NioBuilder totalEventSize   (int size)                              { this.totalEventSize    = size;       return this; }
	public NioBuilder readWriteFair    (boolean fair)                          { this.readWriteFair     = fair;       return this; }
	public NioBuilder ioTimeoutInMillis(int timeout)                           { this.ioTimeoutInMillis = timeout;    return this; }
	public NioBuilder dispatcher       (NioChannelEventDispatcher dispatcher)  { this.dispatcher        = dispatcher; return this; }
	public NioBuilder predictorFactory (NioBufferSizePredictorFactory factory) { this.predictorFactory  = factory;    return this; }
	
	
	protected void set(NioConfig config) {
		config.setReadWritefair(readWriteFair);
		config.setTotalEventSize(totalEventSize);
		config.setChannelEventSize(channelEventSize);
		config.setExecutorSize(executorSize);
		config.setProcessorPoolSize(processorPoolSize);
		config.setIoTimeoutInMillis(ioTimeoutInMillis);
		config.setDefaultReadBufferSize(readBufferSize);
		config.setMinReadBufferSize(minReadBufferSize);
		config.setMaxReadBufferSize(maxReadBufferSize);
	}
	
}
