package org.craft.atom.protocol.rpc;

import org.craft.atom.protocol.rpc.model.RpcBody;
import org.craft.atom.protocol.rpc.model.RpcHeader;
import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.protocol.rpc.spi.SerializationFactory;
import org.craft.atom.test.CaseCounter;
import org.craft.atom.util.ByteUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link RpcEncoder}
 * 
 * @author mindwind
 * @version 1.0, Jul 22, 2014
 */
public class TestRpcEncoder {

	
	private static final byte STATUS_CODE = 40;
	private static final byte ID          = 11;
	
	
	private SerializationFactory<RpcBody> factory = KryoSerializationFactory.getInstance();
	private RpcEncoder                    encoder = new RpcEncoder(factory);
	
	
	@Test
	public void testEncode() {
		RpcHeader rh = new RpcHeader();
		rh.setHb();
		rh.setOw();
		rh.setRp();
		rh.setId(ID);
		rh.setStatusCode(STATUS_CODE);
		RpcBody rb = new RpcBody();
		rb.setClazz(RpcService.class);
		rb.setMethod("rpc");
		rb.setArgsTypes(String.class, Integer.class);
		rb.setArgs("hello", 1);
		RpcMessage rm = new RpcMessage();
		rm.setHeader(rh);
		rm.setBody(rb);
		
		
		byte[] b = encoder.encode(rm);
		Assert.assertArrayEquals(ByteUtil.short2bytes(RpcHeader.MAGIC), ByteUtil.split(b, 0, 2));
		Assert.assertArrayEquals(ByteUtil.short2bytes(RpcHeader.HEADER_SIZE), ByteUtil.split(b, 2, 4));
		Assert.assertEquals(RpcHeader.VERSION, b[4]);
		Assert.assertEquals( factory.newSerialization().type(), rh.getSt());
		Assert.assertEquals(rh.getSt() | rh.getHb() | rh.getOw() | rh.getRp(), b[5]);
		Assert.assertEquals(STATUS_CODE, b[6]);
		Assert.assertEquals(0, b[7]);
		Assert.assertEquals(ID, ByteUtil.bytes2long(b, 8));
		Assert.assertTrue(ByteUtil.bytes2int(b, 16) > 0);
		System.out.println(String.format("[CRAFT-ATOM-PROTOCOL-RPC] (^_^)  <%s>  Case -> test encode. ", CaseCounter.incr(9)));
	}
	
}
