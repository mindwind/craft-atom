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
		
		int headerSize = rh.getHeaderSize();
		byte[] body = encodeBody(rb);
		byte[] encoded = new byte[headerSize + body.length];
		encodeHeader(encoded, rh, body.length);
		System.arraycopy(body, 0, encoded, headerSize, body.length);
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
	
	private void encodeHeader(byte[] b, RpcHeader rh, int bodySize) {
		// magic
		ByteUtil.short2bytes(rh.getMagic());
		// header siez
		ByteUtil.short2bytes(rh.getHeaderSize(), b, 2);
		// version
		b[4] = rh.getVersion();
		// st | hb | tw | rr
		b[5] = (byte) (rh.getSt() | rh.getHb() | rh.getTw() | rh.getRp());
		// status code
		b[6] = rh.getStatusCode();
	    // message id
		ByteUtil.long2bytes(rh.getId(), b, 8);
		// body size
		ByteUtil.int2bytes(bodySize, b, 16);
	}

}
