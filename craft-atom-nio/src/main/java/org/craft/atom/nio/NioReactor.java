package org.craft.atom.nio;

import lombok.ToString;

import org.craft.atom.io.IoHandler;
import org.craft.atom.io.IoReactor;
import org.craft.atom.nio.spi.NioBufferSizePredictorFactory;
import org.craft.atom.nio.spi.NioChannelEventDispatcher;

/**
 * Nio component provide a reactor pattern implementation. {@link NioReactor }
 * Represents a base reactor object.
 * 
 * @author mindwind
 * @version 1.0, Feb 21, 2013
 * @see NioAcceptor
 * @see NioProcessor
 * @see NioConnector
 */
@ToString(of = { "handler", "dispatcher", "predictorFactory" })
abstract public class NioReactor implements IoReactor {

	
	protected IoHandler                     handler         ;
	protected NioChannelEventDispatcher     dispatcher      ;
	protected NioBufferSizePredictorFactory predictorFactory;
	protected NioProcessorPool              pool            ;
	
	
	// ~ ----------------------------------------------------------------------------------------------------------

	
	@Override
	public IoHandler getHandler() {
		return handler;
	}
	
	public NioChannelEventDispatcher getDispatcher() {
		return dispatcher;
	}
	
	public NioBufferSizePredictorFactory getPredictorFactory() {
		return predictorFactory;
	}

}
