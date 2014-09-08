package org.craft.atom.rpc;
import java.io.IOException;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.Getter;
import lombok.Setter;

import org.craft.atom.io.Channel;
import org.craft.atom.io.IllegalChannelStateException;
import org.craft.atom.io.IoConnector;
import org.craft.atom.io.IoHandler;
import org.craft.atom.nio.NioOrderedDirectChannelEventDispatcher;
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
	@Getter @Setter private int                        rpcTimeoutInMillis    ;
	@Getter         private int                        heartbeatInMillis     ;
	@Getter @Setter private int                        reconnectDelay        ;
	@Getter @Setter private boolean                    allowReconnect        ;
	@Getter         private SocketAddress              address               ;
	@Getter @Setter private Map<Long, Channel<byte[]>> channels              ;
	@Getter @Setter private IoHandler                  ioHandler             ;
	@Getter @Setter private IoConnector                ioConnector           ;
	@Getter @Setter private ScheduledExecutorService   hbScheduler           ;
	@Getter @Setter private ExecutorService            reconnectExecutor     ;
	@Getter         private RpcProtocol                protocol              ;            
	
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	
	public DefaultRpcConnector() {
		reconnectDelay         = 6000;
		allowReconnect         = true;
		connectTimeoutInMillis = Integer.MAX_VALUE;
		rpcTimeoutInMillis     = Integer.MAX_VALUE;
		heartbeatInMillis      = 0;
		reconnectExecutor      = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("craft-atom-rpc-connector-reconnect"));
		channels               = new ConcurrentHashMap<Long, Channel<byte[]>>();
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------------

	
	@Override
	public long connect() throws RpcException {
		try {
			Future<Channel<byte[]>> future = ioConnector.connect(address);
			Channel<byte[]> channel = future.get(connectTimeoutInMillis, TimeUnit.MILLISECONDS);
			channel.setAttribute(RpcClientIoHandler.RPC_FUTURE_CHANNEL, new ConcurrentHashMap<Long, RpcFuture>());
			long id = channel.getId();
			channels.put(id, channel);
			LOG.debug("[CRAFT-ATOM-RPC] Rpc connector established connection, |channel={}|.", channel);
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
	public boolean disconnect(long connectionId) {
		Channel<byte[]> channel = channels.remove(connectionId);
		if (channel != null) {
			channel.close();
			return true;
		}
		return false;
	}
	
	@Override
	public void close() {
		brokeAll();
		channels.clear();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public RpcMessage send(RpcMessage req) throws RpcException {
		long reqId = req.getId();
		Channel<byte[]> channel = select(reqId);
		if (channel == null) throw new RpcException(RpcException.NET_IO);
		
		try {
			RpcFuture future = new DefaultRpcFuture();
			Map<Long, RpcFuture> map = (Map<Long, RpcFuture>) channel.getAttribute(RpcClientIoHandler.RPC_FUTURE_CHANNEL);
			map.put(reqId, future);
			boolean succ = write(channel, req);
			if (!succ) throw new IOException("Unknown I/O error!");
			
			// One way request, client does not expect response
			if (req.isOneway()) return null;
			
			// Wait response
			future.await(req.getRpcTimeoutInMillis(), TimeUnit.MILLISECONDS);
			return future.getResponse();
		} catch (IllegalChannelStateException e) {
			reconnect(channel.getId());
			throw new RpcException(RpcException.NET_IO, e);
		} catch (IOException e) {
			reconnect(channel.getId());
			throw new RpcException(RpcException.NET_IO, e);
		} catch (TimeoutException e) {
			throw new RpcException(RpcException.CLIENT_TIMEOUT, e);
		} catch (Exception e) {
			throw new RpcException(RpcException.UNKNOWN, e);
		}
	}
	
	private boolean write(Channel<byte[]> channel, RpcMessage msg) {
		ProtocolEncoder<RpcMessage> encoder = protocol.getRpcEncoder();
		byte[] data = encoder.encode(msg);
		boolean succ = channel.write(data);
		LOG.debug("[CRAFT-ATOM-RPC] Rpc connector sent request bytes, |length={}, bytes={}, channel={}|", data.length, data, channel);
		return succ;
	}
	
	void reconnect(final long connectionId) {
		if (!disconnect(connectionId)) return;
		
		reconnectExecutor.execute(new Runnable() {
			
			@Override
			public void run() {
				while (!retryConnect()) {
					try { Thread.sleep(reconnectDelay); } catch (InterruptedException e) {}
				}
			}
			
			private boolean retryConnect() {
				try {
					if (!allowReconnect) return false;
					long connId = connect();
					if (connId > 0) {
						LOG.debug("[CRAFT-ATOM-RPC] Rpc connector reconnect success, |connectionId={}|", connId);
						return true;
					} else {
						LOG.debug("[CRAFT-ATOM-RPC] Rpc connector reconnect fail");
						return false;
					}
				} catch (Exception e) {
					return false;
				}
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private Channel<byte[]> select(long id) {
		Collection<Channel<byte[]>> collection = channels.values();
		Object[] chs = collection.toArray();
		if (chs.length == 0) return null;
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
						} catch (Exception e) {
							LOG.warn("[CRAFT-ATOM-RPC] Rpc connector heartbeat error", e);
						}
					}
				}
			}, 0, heartbeatInMillis, TimeUnit.MILLISECONDS);
		}
	}

	@Override
	public void setProtocol(RpcProtocol protocol) {
		this.protocol    = protocol;
		this.ioHandler   = new RpcClientIoHandler(protocol, this);
		this.ioConnector = NioFactory.newTcpConnectorBuilder(ioHandler)
						             .connectTimeoutInMillis(connectTimeoutInMillis)
						             .dispatcher(new NioOrderedDirectChannelEventDispatcher())
						             .build();
	}
	
	
	// ~ ----------------------------------------------------------------------------------------------------- for test
	
	
	/**
	 * Broke all connections
	 */
	public void brokeAll() {
		for (Channel<byte[]> channel : channels.values()) {
			channel.close();
		}
	}
	
	/**
	 * @return all alive connection number at the moment.
	 */
	public int aliveConnectionNum() {
		int num = 0;
		for (Channel<byte[]> ch : channels.values()) {
			if (ch.isOpen()) num++;
		}
		return num;
	}
	
}
