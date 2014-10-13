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
import org.craft.atom.io.IoConnector;
import org.craft.atom.io.IoHandler;
import org.craft.atom.nio.NioOrderedDirectChannelEventDispatcher;
import org.craft.atom.nio.api.NioFactory;
import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.rpc.api.RpcContext;
import org.craft.atom.rpc.spi.RpcChannel;
import org.craft.atom.rpc.spi.RpcConnector;
import org.craft.atom.rpc.spi.RpcProtocol;
import org.craft.atom.util.thread.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mindwind
 * @version 1.0, Aug 15, 2014
 */
public class DefaultRpcConnector implements RpcConnector {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultRpcConnector.class);
	
	
	@Getter @Setter private int                          connectTimeoutInMillis;
	@Getter @Setter private int                          rpcTimeoutInMillis    ;
	@Getter         private int                          heartbeatInMillis     ;
	@Getter @Setter private int                          reconnectDelay        ;
	@Getter @Setter private boolean                      allowReconnect        ;
	@Getter         private SocketAddress                address               ;
	@Getter @Setter private Map<Long, DefaultRpcChannel> channels              ;
	@Getter @Setter private IoHandler                    ioHandler             ;
	@Getter @Setter private IoConnector                  ioConnector           ;
	@Getter @Setter private ScheduledExecutorService     hbScheduler           ;
	@Getter @Setter private ExecutorService              reconnectExecutor     ;
	@Getter         private RpcProtocol                  protocol              ;            
	
	
	// ~ ------------------------------------------------------------------------------------------------------------
	
	
	public DefaultRpcConnector() {
		reconnectDelay         = 6000;
		allowReconnect         = true;
		connectTimeoutInMillis = Integer.MAX_VALUE;
		rpcTimeoutInMillis     = Integer.MAX_VALUE;
		heartbeatInMillis      = 0;
		reconnectExecutor      = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("craft-atom-rpc-connector-reconnect"));
		channels               = new ConcurrentHashMap<Long, DefaultRpcChannel>();
		ioHandler              = new RpcClientIoHandler(this);
		ioConnector            = NioFactory.newTcpConnectorBuilder(ioHandler)
						                   .connectTimeoutInMillis(connectTimeoutInMillis)
						                   .dispatcher(new NioOrderedDirectChannelEventDispatcher())
						                   .build();
	}
	
	
	// ~ ------------------------------------------------------------------------------------------------------------

	
	@Override
	public long connect() throws RpcException {
		try {
			Future<Channel<byte[]>> future = ioConnector.connect(address);
			Channel<byte[]> channel = future.get(connectTimeoutInMillis, TimeUnit.MILLISECONDS);
			DefaultRpcChannel rpcChannel = new DefaultRpcChannel(channel, protocol.getRpcEncoder(), protocol.getRpcDecoder());
			rpcChannel.setFutures(new ConcurrentHashMap<Long, RpcFuture<?>>());
			channel.setAttribute(RpcIoHandler.RPC_CHANNEL, rpcChannel);
			long id = channel.getId();
			channels.put(id, rpcChannel);
			LOG.debug("[CRAFT-ATOM-RPC] Rpc client connector established connection, |channel={}|.", rpcChannel);
			return id;
		} catch (TimeoutException e) {
			throw new RpcException(RpcException.CLIENT_TIMEOUT, "client timeout", e);
		} catch (IOException e) {
			throw new RpcException(RpcException.NETWORK, "network error", e);
		} catch (Exception e) {
			throw new RpcException(RpcException.UNKNOWN, "unknown error", e);
		}
	}

	@Override
	public boolean disconnect(long connectionId) {
		DefaultRpcChannel channel = channels.remove(connectionId);
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
	public RpcMessage send(RpcMessage req, boolean async) throws RpcException {
		long mid = req.getId();
		DefaultRpcChannel channel = select(mid);
		if (channel == null) throw new RpcException(RpcException.NETWORK, "network error");
		
		try {
			boolean oneway = req.isOneway();
			RpcFuture<Object> future = null;
			if (!oneway) {
				future = new DefaultRpcFuture<Object>();
				channel.setRpcFuture(mid, future);
			 }
			channel.write(req);
			
			// One way request, client does not expect response
			if (oneway) { return null; }
			
			if (async) {
				// async and set future
				RpcContext.getContext().setFuture(future);
				return null;
			} else {
				// sync and wait response
				future.await(req.getRpcTimeoutInMillis(), TimeUnit.MILLISECONDS);
				return future.getResponse();
			}
		} catch (RpcException e) {
			throw e;
		} catch (IOException e) {
			throw new RpcException(RpcException.NETWORK, "network error", e); 
		} catch (TimeoutException e) {
			throw new RpcException(RpcException.CLIENT_TIMEOUT, "client timeout", e);
		} catch (Exception e) {
			throw new RpcException(RpcException.UNKNOWN, "unknown error", e);
		}
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
						LOG.debug("[CRAFT-ATOM-RPC] Rpc client connector reconnect success, |connectionId={}|", connId);
						return true;
					} else {
						LOG.debug("[CRAFT-ATOM-RPC] Rpc client connector reconnect fail");
						return false;
					}
				} catch (Exception e) {
					return false;
				}
			}
		});
	}
	
	private DefaultRpcChannel select(long id) {
		Collection<DefaultRpcChannel> collection = channels.values();
		Object[] chs = collection.toArray();
		if (chs.length == 0) return null;
		int i = (int) (Math.abs(id) % chs.length);
		return (DefaultRpcChannel) chs[i];
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
					for (RpcChannel channel : channels.values()) {
						try {
							RpcMessage hbmsg = RpcMessages.newHbRequestRpcMessage();
							channel.write(hbmsg);
							LOG.debug("[CRAFT-ATOM-RPC] Rpc client connector heartbeat, |hbmsg={}, channel={}|", hbmsg, channel);
						} catch (Exception e) {
							LOG.warn("[CRAFT-ATOM-RPC] Rpc client connector heartbeat error", e);
						}
					}
				}
			}, 0, heartbeatInMillis, TimeUnit.MILLISECONDS);
		}
	}

	@Override
	public void setProtocol(RpcProtocol protocol) {
		this.protocol    = protocol;
	}
	
	
	// ~ ----------------------------------------------------------------------------------------------------- for test
	
	
	/**
	 * Broke all connections
	 */
	public void brokeAll() {
		for (DefaultRpcChannel channel : channels.values()) {
			channel.close();
		}
	}
	
	/**
	 * @return all alive connection number at the moment.
	 */
	public int aliveConnectionNum() {
		int num = 0;
		for (DefaultRpcChannel channel : channels.values()) {
			if (channel.isOpen()) num++;
		}
		return num;
	}
	
}
