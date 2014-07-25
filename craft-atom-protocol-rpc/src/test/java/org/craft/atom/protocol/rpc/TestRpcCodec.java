package org.craft.atom.protocol.rpc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.craft.atom.protocol.rpc.model.RpcBody;
import org.craft.atom.protocol.rpc.model.RpcHeader;
import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.test.CaseCounter;
import org.craft.atom.util.ByteUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for {@code RpcEncoder} and {@code RpcDecoder}
 * 
 * @author mindwind
 * @version 1.0, Jul 22, 2014
 */
public class TestRpcCodec {

	private static final Logger LOG         = LoggerFactory.getLogger(TestRpcCodec.class);
	private static final byte   STATUS_CODE = 40                                         ;
	private static final byte   ID          = 11                                         ;
	
	
	private RpcMessage rm      = new RpcMessage();
	private RpcHeader  rh      = new RpcHeader() ;
	private RpcEncoder encoder = new RpcEncoder();
	private RpcDecoder decoder = new RpcDecoder();
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	
	
	@Before
	public void before() {
		rh.setSt(KryoSerialization.getInstance().type());
		rh.setHb();
		rh.setOw();
		rh.setRp();
		rh.setId(ID);
		rh.setStatusCode(STATUS_CODE);
		RpcBody rb = new RpcBody();
		rb.setClazz(RpcService.class);
		rb.setMethod("rpc");
		rb.setArgTypes(String.class, Integer.class);
		rb.setArgs("hello", 1);
		rm.setHeader(rh);
		rm.setBody(rb);
	}
	
	
	@Test
	public void testEncode() {
		byte[] b = encoder.encode(rm);
		Assert.assertArrayEquals(ByteUtil.short2bytes(RpcHeader.MAGIC), ByteUtil.split(b, 0, 2));
		Assert.assertArrayEquals(ByteUtil.short2bytes(RpcHeader.HEADER_SIZE), ByteUtil.split(b, 2, 4));
		Assert.assertEquals(RpcHeader.VERSION, b[4]);
		Assert.assertEquals(KryoSerialization.getInstance().type(), rh.getSt());
		Assert.assertEquals(rh.getSt() | rh.getHb() | rh.getOw() | rh.getRp(), b[5]);
		Assert.assertEquals(STATUS_CODE, b[6]);
		Assert.assertEquals(0, b[7]);
		Assert.assertEquals(ID, ByteUtil.bytes2long(b, 8));
		Assert.assertTrue(ByteUtil.bytes2int(b, 16) > 0);
		System.out.println(String.format("[CRAFT-ATOM-PROTOCOL-RPC] (^_^)  <%s>  Case -> test encode. ", CaseCounter.incr(9)));
	}
	
	@Test
	public void testDecode() {
		byte[] b = encoder.encode(rm);
		List<RpcMessage> l = decoder.decode(b);
		Assert.assertEquals(1, l.size());
		RpcMessage drm = l.get(0);
		Assert.assertEquals(rm, drm);
		
		LOG.debug("[CRAFT-ATOM-PROTOCOL-RPC] |expected ={}|", rm);
		LOG.debug("[CRAFT-ATOM-PROTOCOL-RPC] |actual   ={}|", drm);
		System.out.println(String.format("[CRAFT-ATOM-PROTOCOL-RPC] (^_^)  <%s>  Case -> test decode. ", CaseCounter.incr(2)));
	}
	
	@Test 
	public void testStreamingDecode() {
		byte[] bytes = encoder.encode(rm);
		testInRandomLoop(rm, bytes, 1, true);
		testInRandomLoop(rm, bytes, 100, false);
		System.out.format("[CRAFT-ATOM-PROTOCOL-RPC] (^_^)  <%s>  Case -> test streaming decode. ", CaseCounter.incr(2));
	}
	
	private void testInRandomLoop(RpcMessage expected, byte[] bytes, int loop, boolean onebyte) {
		for (int i = 0; i < loop; i++) {
			List<byte[]> barr = new ArrayList<byte[]>();
			if (onebyte) {
				for (int j = 0; j < bytes.length; j++) {
					barr.add(new byte[] { bytes[j] });
				}
			} else {
				int rand = 1;
				for (int j = 0; j < bytes.length; j +=rand) {
					rand = new Random().nextInt(32);
					if (j + rand > bytes.length) {
						rand = bytes.length - j;
					}
					barr.add(ByteUtil.split(bytes, j, j + rand));
				}
			}
			
			List<RpcMessage> rms = null;
			for (byte[] b : barr) {
				rms = decoder.decode(b);
			}
			
			Assert.assertEquals(1, rms.size());
			RpcMessage actual = rms.get(0);
			Assert.assertEquals(expected, actual);
		}
	}
	
}
