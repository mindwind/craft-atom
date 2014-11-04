package io.craft.atom.nio.api;

import io.craft.atom.io.IoAcceptor;
import io.craft.atom.io.IoConnector;
import io.craft.atom.io.IoHandler;
import io.craft.atom.nio.NioTcpAcceptor;
import io.craft.atom.nio.NioTcpConnector;
import io.craft.atom.nio.spi.NioBufferSizePredictorFactory;
import io.craft.atom.nio.spi.NioChannelEventDispatcher;


/**
 * Nio Factory,
 * which provides static factory method and builder to creating {@link IoAcceptor} and {@link IoConnector} instance.
 * 
 * @author mindwind
 * @version 1.0, Dec 17, 2013
 */
public class NioFactory {
	
	public static IoAcceptor newTcpAcceptor(IoHandler handler) {
		return newTcpAcceptorBuilder(handler).build();
	}
	
	public static IoAcceptor newTcpAcceptor(IoHandler handler, NioAcceptorConfig config) {
		return new NioTcpAcceptor(handler, config);
	}
	
	public static IoAcceptor newTcpAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher) {
		return new NioTcpAcceptor(handler, config, dispatcher);
	}
	
	public static IoAcceptor newTcpAcceptor(IoHandler handler, NioAcceptorConfig config, NioChannelEventDispatcher dispatcher, NioBufferSizePredictorFactory predictorFactory) {
		return new NioTcpAcceptor(handler, config, dispatcher, predictorFactory);
	}
	
	public static IoConnector newTcpConnector(IoHandler handler) {
		return newTcpConnectorBuilder(handler).build();
	}
	
	public static IoConnector newTcpConnector(IoHandler handler, NioConnectorConfig config) {
		return new NioTcpConnector(handler, config);
	}
	
	public static IoConnector newTcpConnector(IoHandler handler, NioConnectorConfig config, NioChannelEventDispatcher dispatcher) {
		return new NioTcpConnector(handler, config, dispatcher);
	}
	
	public static IoConnector newTcpConnector(IoHandler handler, NioConnectorConfig config, NioChannelEventDispatcher dispatcher, NioBufferSizePredictorFactory predictorFactory) {
		return new NioTcpConnector(handler, config, dispatcher, predictorFactory);
	}
	
	public static NioTcpAcceptorBuilder newTcpAcceptorBuilder(IoHandler handler) {
		return new NioTcpAcceptorBuilder(handler);
	}
	
	public static NioTcpConnectorBuilder newTcpConnectorBuilder(IoHandler handler) {
		return new NioTcpConnectorBuilder(handler);
	}
}
