package org.craft.atom.protocol.rpc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.ref.SoftReference;

import org.craft.atom.protocol.ProtocolException;
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
	
	// singleton
	private static final KryoSerialization INSTNACE = new KryoSerialization();
	public static KryoSerialization getInstance() { return INSTNACE; } 
	private KryoSerialization() {}
	
	
	// thread local cache
    private static final ThreadLocal<SoftReference<Kryo>> CACHE = new ThreadLocal<SoftReference<Kryo>>() {
    	@Override
    	protected SoftReference<Kryo> initialValue() {
            Kryo kryo = new Kryo();
            kryo.register(RpcBody.class);
            return new SoftReference<Kryo>(kryo);
        }
    };
	
	@Override
	public byte type() {
		return 1;
	}

	@Override
	public byte[] serialize(RpcBody rb) {
		try {
			Assert.notNull(rb);
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    Output output = new Output(baos);
		    kryo().writeObject(output, rb);
		    output.close();
		    return baos.toByteArray();
		} catch (Exception e) {
			throw new ProtocolException(e);
		}
	}
	
	private Kryo kryo() {
		Kryo kryo = CACHE.get().get();
		if (kryo == null) {
			kryo = new Kryo();
			CACHE.set(new SoftReference<Kryo>(kryo));
		}
		return kryo;
	}

	@Override
	public RpcBody deserialize(byte[] bytes) {
		return deserialize(bytes, 0);
	}

	@Override
	public RpcBody deserialize(byte[] bytes, int off) {
		try {
		    Assert.notNull(bytes);
		    ByteArrayInputStream bais = new ByteArrayInputStream(bytes, off, bytes.length - off);
		    Input input = new Input(bais);
		    RpcBody rb = kryo().readObject(input, RpcBody.class);
	        input.close();
		    return rb;
		} catch (Exception e) {
			throw new ProtocolException(e);
		}
	}

}
