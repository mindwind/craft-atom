package org.craft.atom.protocol.rpc;

import java.io.ByteArrayOutputStream;

import org.craft.atom.protocol.ProtocolEncoder;
import org.craft.atom.protocol.ProtocolException;
import org.craft.atom.protocol.rpc.model.RpcBody;
import org.craft.atom.protocol.rpc.model.RpcHeader;
import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.util.Assert;
import org.craft.atom.util.ByteUtil;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;

/**
 * A {@link ProtocolEncoder} which encodes a {@code RpcMessage} object into bytes follow the generic RPC format.
 * <p>
 * thread safe.
 * 
 * @author mindwind
 * @version 1.0, Jul 17, 2014
 */
public class RpcEncoder implements ProtocolEncoder<RpcMessage> {

	@Override
	public byte[] encode(RpcMessage rm) throws ProtocolException {
		Assert.notNull(rm);
		RpcHeader rh = rm.getHeader();
		RpcBody rb = rm.getBody();
		Assert.notNull(rh);
		Assert.notNull(rb);
		
		byte[] body = encodeBody(rb);
		byte[] encoded = new byte[RpcHeader.HEADER_SIZE + body.length];
		rh.setBodySize(body.length);
		encodeHeader(encoded, rh);
		System.arraycopy(body, 0, encoded, RpcHeader.HEADER_SIZE, body.length);
		return encoded;
	}
	
	private byte[] encodeBody(RpcBody rb) {
		Kryo kryo = new Kryo();
	    kryo.register(RpcBody.class);
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    Output output = new Output(baos);
	    kryo.writeObject(output, rb);
	    output.close();
	    return baos.toByteArray();
	}
	
	private void encodeHeader(byte[] b, RpcHeader rh) {
		// magic
		ByteUtil.short2bytes(RpcHeader.MAGIC, b, 0);
		// header siez
		ByteUtil.short2bytes(RpcHeader.HEADER_SIZE, b, 2);
		// version
		b[4] = RpcHeader.VERSION;
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
