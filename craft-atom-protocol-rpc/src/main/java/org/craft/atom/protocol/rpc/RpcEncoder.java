package org.craft.atom.protocol.rpc;

import lombok.Getter;
import lombok.Setter;

import org.craft.atom.protocol.ProtocolEncoder;
import org.craft.atom.protocol.ProtocolException;
import org.craft.atom.protocol.rpc.model.RpcBody;
import org.craft.atom.protocol.rpc.model.RpcHeader;
import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.protocol.rpc.spi.Serialization;
import org.craft.atom.protocol.rpc.spi.SerializationFactory;
import org.craft.atom.util.Assert;
import org.craft.atom.util.ByteUtil;

/**
 * A {@link ProtocolEncoder} which encodes a {@code RpcMessage} object into bytes follow the generic RPC format.
 * <p>
 * thread safe.
 * 
 * @author mindwind
 * @version 1.0, Jul 17, 2014
 */
public class RpcEncoder implements ProtocolEncoder<RpcMessage> {
	
	
	@Getter @Setter private SerializationFactory<RpcBody> factory;
	
	
	// ~ --------------------------------------------------------------------------------------------------------------

	
	public RpcEncoder() {}
	
	public RpcEncoder(SerializationFactory<RpcBody> factory) {
		this.factory = factory;
	}
	
	
	// ~ --------------------------------------------------------------------------------------------------------------
	
	
	@Override
	public byte[] encode(RpcMessage rm) throws ProtocolException {
		Assert.notNull(rm);
		RpcHeader rh = rm.getHeader();
		RpcBody rb = rm.getBody();
		Assert.notNull(rh);
		Assert.notNull(rb);
		
		Serialization<RpcBody> serializer = factory.newSerialization();
		byte[] body = encodeBody(rb, serializer);
		
		byte[] encoded = new byte[rh.getHeaderSize() + body.length];
		rh.setBodySize(body.length);
		rh.setSt(serializer.type());
		
		encodeHeader(encoded, rh);
		System.arraycopy(body, 0, encoded, rh.getHeaderSize(), body.length);
		return encoded;
	}
	
	private byte[] encodeBody(RpcBody rb, Serialization<RpcBody> serializer) {
		return serializer.serialize(rb);
	}
	
	private void encodeHeader(byte[] b, RpcHeader rh) {
		// magic
		ByteUtil.short2bytes(rh.getMagic(), b, 0);
		// header siez
		ByteUtil.short2bytes(rh.getHeaderSize(), b, 2);
		// version
		b[4] = rh.getVersion();
		// st | hb | tw | rr
		b[5] = (byte) (rh.getSt() | rh.getHb() | rh.getOw() | rh.getRp());
		// status code
		b[6] = rh.getStatusCode();
	    // message id
		ByteUtil.long2bytes(rh.getId(), b, 8);
		// body size
		ByteUtil.int2bytes(rh.getBodySize(), b, 16);
	}

}
