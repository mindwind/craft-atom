package org.craft.atom.protocol.rpc;


import org.craft.atom.protocol.rpc.model.RpcBody;
import org.craft.atom.test.CaseCounter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@code KryoSerialization}
 * 
 * @author mindwind
 * @version 1.0, Jul 25, 2014
 */
public class TestKryoSerialization {
	
	
	private KryoSerialization ks = KryoSerialization.getInstance();
	private RpcBody           rb = new RpcBody(); 
	private SerialA           sa = new SerialA();
	private SerialB           sb = new SerialB();
	
	
	@Before
	public void before() {
		sa.setB((byte) 1);
		sa.setI(2);
		sa.setL(1000L);
		sa.setF(2.0f);
		sa.setD(123.33);
		sa.setBool(true);
		sa.setS("foo.bar");
		sa.addList("1").addList("2").addList("3");
		sa.addSet("a").addSet("b").addSet("c");
		sa.putMap(1L, "a").putMap(2L, "b").putMap(3L, "c");
		sa.putNested("a", "a1", "a2", "a3");
		sa.putNested("b", "b1", "b2", "b3");
		sa.putNested("c", "c1", "c2", "c3");
		sa.setSenum(SerialEnum.A);
		sa.setSeb(sb);
		sb.setSea(sa);
		sb.setBytes(new byte[] { 1, 2, 3, 4, 5 });
		rb.setArgTypes(SerialA.class, SerialB.class);
		rb.setArgs(sa, sb);
	}
	
	@Test 
	public void testBasic() {
		byte[] bytes = ks.serialize(rb);
		RpcBody body = ks.deserialize(bytes);
		Class<?>[] argTypes = body.getArgTypes();
		Object[] args = body.getArgs();
		
		Assert.assertEquals(2, argTypes.length);
		Assert.assertEquals(2, args.length);
		Assert.assertEquals(SerialA.class, argTypes[0]);
		Assert.assertEquals(SerialB.class, argTypes[1]);
		
		SerialA aa = (SerialA) args[0];
		SerialB ab = (SerialB) args[1];
		Assert.assertEquals(sa.getB(), aa.getB());
		Assert.assertEquals(sa.getI(), aa.getI());
		Assert.assertEquals(sa.getL(), aa.getL());
		Assert.assertEquals(sa.getF(), aa.getF(), 0.00001);
		Assert.assertEquals(sa.getD(), aa.getD(), 0.00001);
		Assert.assertEquals(sa.isBool(), aa.isBool());
		Assert.assertEquals(sa.getS(), aa.getS());
		Assert.assertEquals(sa.getDate(), aa.getDate());
		Assert.assertEquals(sa.getList(), aa.getList());
		Assert.assertEquals(sa.getSet(), aa.getSet());
		Assert.assertEquals(sa.getMap(), aa.getMap());
		Assert.assertEquals(sa.getNested(), aa.getNested());
		Assert.assertEquals(sa.getSenum().getCode(), aa.getSenum().getCode());
		Assert.assertEquals(sa.getSenum().getDesc(), aa.getSenum().getDesc());
		Assert.assertArrayEquals(sb.getBytes(), ab.getBytes());
		Assert.assertEquals(aa.getSeb(), ab);
		Assert.assertEquals(ab.getSea(), aa);
		System.out.format("[CRAFT-ATOM-PROTOCOL-RPC] (^_^)  <%s>  Case -> test kryo serialization basic.\n", CaseCounter.incr(21));
	}
	
	
}
