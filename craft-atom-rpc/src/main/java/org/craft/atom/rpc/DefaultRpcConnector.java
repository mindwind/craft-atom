package org.craft.atom.rpc;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;

import org.craft.atom.io.Channel;
import org.craft.atom.io.IoConnector;
import org.craft.atom.io.IoHandler;
import org.craft.atom.nio.api.NioFactory;
import org.craft.atom.rpc.spi.RpcConnector;

/**
 * @author mindwind
 * @version 1.0, Aug 15, 2014
 */
public class DefaultRpcConnector implements RpcConnector {
	
	
	@Getter         private int           connectTimeoutInMillis;
	@Getter         private SocketAddress address               ;
	@Getter @Setter private IoHandler     ioHandler             ;
	@Getter @Setter private IoConnector   ioConnector           ;
	@Getter @Setter private Map<Long, Channel<byte[]>> channels ;
	
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	
	public DefaultRpcConnector() {
		channels    = new ConcurrentHashMap<Long, Channel<byte[]>>();
		ioHandler   = new RpcClientIoHandler();
		ioConnector = NioFactory.newTcpConnectorBuilder(ioHandler)
				                .connectTimeoutInMillis(connectTimeoutInMillis)
				                .build();
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------------

	
	@Override
	public long connect() throws IOException {
		Future<Channel<byte[]>> future = ioConnector.connect(address);
		try {
			Channel<byte[]> channel = future.get(connectTimeoutInMillis, TimeUnit.MILLISECONDS);
			long id = channel.getId();
			channels.put(id, channel);
			return id;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public void disconnect(long connectionId) throws IOException {
		Channel<byte[]> channel = channels.remove(connectionId);
		if (channel != null) {
			channel.close();
		}
	}

	@Override
	public void setAddress(SocketAddress address) {
		this.address = address;
	}

}
