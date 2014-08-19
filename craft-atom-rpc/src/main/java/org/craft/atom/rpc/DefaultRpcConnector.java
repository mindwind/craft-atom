package org.craft.atom.rpc;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;

import org.craft.atom.io.Channel;
import org.craft.atom.io.IoConnector;
import org.craft.atom.io.IoHandler;
import org.craft.atom.nio.api.NioFactory;
import org.craft.atom.protocol.ProtocolEncoder;
import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.rpc.spi.RpcConnector;
import org.craft.atom.rpc.spi.RpcProtocol;
import org.craft.atom.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mindwind
 * @version 1.0, Aug 15, 2014
 */
public class DefaultRpcConnector implements RpcConnector {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultRpcConnector.class);
	
	
	@Getter @Setter private int                        connectTimeoutInMillis;
	@Getter         private int                        heartbeatInMillis     ;
	@Getter         private SocketAddress              address               ;
	@Getter @Setter private Map<Long, Channel<byte[]>> channels              ;
	@Getter @Setter private IoHandler                  ioHandler             ;
	@Getter @Setter private IoConnector                ioConnector           ;
	@Getter @Setter private ScheduledExecutorService   hbScheduler           ;
	@Getter @Setter private RpcProtocol                protocol              ;
	
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	
	public DefaultRpcConnector() {
		connectTimeoutInMillis = Integer.MAX_VALUE;
		channels               = new ConcurrentHashMap<Long, Channel<byte[]>>();
		ioHandler              = new RpcClientIoHandler();
		ioConnector            = NioFactory.newTcpConnectorBuilder(ioHandler)
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
	public void disconnect(long connectionId) {
		Channel<byte[]> channel = channels.remove(connectionId);
		if (channel != null) {
			channel.close();
		}
	}

	@Override
	public void setAddress(SocketAddress address) {
		this.address = address;
	}

	@Override
	public void setHeartbeatInMillis(int heartbeatInMillis) {
		this.heartbeatInMillis = heartbeatInMillis;
		heartbeat();
	}
	
	private void heartbeat() {
		if (hbScheduler != null) {
			hbScheduler.shutdown();
		}
		
		if (heartbeatInMillis > 0) {
			hbScheduler = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("craft-atom-rpc-connector-heartbeat"));
			hbScheduler.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					for (Channel<byte[]> channel : channels.values()) {
						try {
							ProtocolEncoder<RpcMessage> encoder = protocol.getRpcEncoder();
							byte[] data = encoder.encode(RpcMessages.newHbRequestRpcMessage());
							channel.write(data);
						} catch (Throwable t) {
							LOG.warn("[CRAFT-ATOM-RPC] Rpc connector heartbeat error", t);
						}
					}
				}
			}, 0, heartbeatInMillis, TimeUnit.MILLISECONDS);
		}
	}
	
}
