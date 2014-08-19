package org.craft.atom.rpc;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.Getter;
import lombok.Setter;

import org.craft.atom.io.Channel;
import org.craft.atom.io.IoConnector;
import org.craft.atom.io.IoHandler;
import org.craft.atom.nio.api.NioFactory;
import org.craft.atom.protocol.ProtocolEncoder;
import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.rpc.spi.RpcConnector;
import org.craft.atom.rpc.spi.RpcFuture;
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
		ioHandler              = new RpcClientIoHandler(protocol);
		ioConnector            = NioFactory.newTcpConnectorBuilder(ioHandler)
				                           .connectTimeoutInMillis(connectTimeoutInMillis)
				                           .build();
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------------

	
	@Override
	public long connect() throws RpcException {
		try {
			Future<Channel<byte[]>> future = ioConnector.connect(address);
			Channel<byte[]> channel = future.get(connectTimeoutInMillis, TimeUnit.MILLISECONDS);
			long id = channel.getId();
			channels.put(id, channel);
			return id;
		} catch (TimeoutException e) {
			throw new RpcException(RpcException.CLIENT_TIMEOUT, e);
		} catch (IOException e) {
			throw new RpcException(RpcException.NET_IO, e);
		} catch (Exception e) {
			throw new RpcException(RpcException.UNKNOWN, e);
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
	@SuppressWarnings("unchecked")
	public RpcMessage send(RpcMessage req) throws RpcException {
		try {
			long id = req.getId();
			Channel<byte[]> ch = select(id);
			boolean succ = write(ch, req);
			if (!succ) throw new IOException("Unknown I/O error!");
			
			// one way request, client does not expect response
			if (req.isOneWay()) {
				return null;
			}
			
			// wait response
			RpcFuture future = new DefaultRpcFuture();
			Map<Long, RpcFuture> map = (Map<Long, RpcFuture>) ch.getAttribute(RpcClientIoHandler.RPC_FUTURE_CHANNEL);
			map.put(id, future);
			future.await(req.getRpcTimeoutInMillis(), TimeUnit.MILLISECONDS);
			return future.getResponse();
		} catch (IOException e) {
			throw new RpcException(RpcException.NET_IO, e);
		} catch (InterruptedException e) {
			throw new RpcException(RpcException.CLIENT_TIMEOUT, e);
		} catch (Exception e) {
			throw new RpcException(RpcException.UNKNOWN, e);
		}
	}
	
	private boolean write(Channel<byte[]> channel, RpcMessage msg) {
		ProtocolEncoder<RpcMessage> encoder = protocol.getRpcEncoder();
		byte[] data = encoder.encode(msg);
		return channel.write(data);
	}
	
	@SuppressWarnings("unchecked")
	private Channel<byte[]> select(long id) {
		Collection<Channel<byte[]>> collection = channels.values();
		Object[] chs = collection.toArray();
		int i = (int) (Math.abs(id) % chs.length);
		return (Channel<byte[]>) chs[i];
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
							write(channel, RpcMessages.newHbRequestRpcMessage());
						} catch (Throwable t) {
							LOG.warn("[CRAFT-ATOM-RPC] Rpc connector heartbeat error", t);
						}
					}
				}
			}, 0, heartbeatInMillis, TimeUnit.MILLISECONDS);
		}
	}
	
}
