package org.craft.atom.nio;

import org.craft.atom.io.IoHandler;
import org.craft.atom.nio.spi.NioBufferSizePredictor;
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
abstract public class NioReactor {

	protected IoHandler handler;
	protected NioChannelEventDispatcher dispatcher;
	protected NioBufferSizePredictor predictor;
	
	// ~ ----------------------------------------------------------------------------------------------------------

	public IoHandler getHandler() {
		return handler;
	}

	public void setHandler(IoHandler handler) {
		this.handler = handler;
	}

	public NioChannelEventDispatcher getDispatcher() {
		return dispatcher;
	}

	public void setDispatcher(NioChannelEventDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public NioBufferSizePredictor getPredictor() {
		return predictor;
	}

	public void setPredictor(NioBufferSizePredictor predictor) {
		this.predictor = predictor;
	}

	@Override
	public String toString() {
		return String.format(
				"NioReactor [handler=%s, dispatcher=%s, predictor=%s]",
				handler, dispatcher, predictor);
	}

}
