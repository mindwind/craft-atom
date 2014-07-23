package org.craft.atom.protocol.rpc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.craft.atom.protocol.rpc.model.RpcBody;
import org.craft.atom.protocol.rpc.spi.Serialization;
import org.craft.atom.util.Assert;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * The implementor using <a href="https://github.com/EsotericSoftware/kryo">kryo</a>.
 * <p>
 * Not thread safe.
 * 
 * @author mindwind
 * @version 1.0, Jul 23, 2014
 */
public class KryoSerialization implements Serialization<RpcBody> {
	
	
	private Kryo kryo = new Kryo();

	
	public KryoSerialization() {
		kryo.register(RpcBody.class);
	}
	
	@Override
	public byte type() {
		return 1;
	}

	@Override
	public byte[] serialize(RpcBody rb) {
		Assert.notNull(rb);
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    Output output = new Output(baos);
	    kryo.writeObject(output, rb);
	    output.close();
	    return baos.toByteArray();
	}

	@Override
	public RpcBody deserialize(byte[] bytes) {
		Assert.notNull(bytes);
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		Input input = new Input(bais);
		RpcBody rb = kryo.readObject(input, RpcBody.class);
	    input.close();
		return rb;
	}

}
