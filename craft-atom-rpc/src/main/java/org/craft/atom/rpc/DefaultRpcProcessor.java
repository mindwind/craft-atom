package org.craft.atom.rpc;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.craft.atom.protocol.ProtocolDecoder;
import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.rpc.spi.RpcInvoker;
import org.craft.atom.rpc.spi.RpcProcessor;
import org.craft.atom.rpc.spi.RpcProtocol;

/**
 * @author mindwind
 * @version 1.0, Aug 7, 2014
 */
public class DefaultRpcProcessor implements RpcProcessor {
		
	
	@Getter @Setter private RpcProtocol protocol;
	@Getter @Setter private RpcInvoker  invoker ;
	
	
	@Override
	public byte[] process(byte[] bytes, ProtocolDecoder<RpcMessage> decoder) {
		List<RpcMessage> reqs = decoder.decode(bytes);
		for (RpcMessage req : reqs) {
			invoker.invoke(req);
		}
		return null;
	}
	

}
