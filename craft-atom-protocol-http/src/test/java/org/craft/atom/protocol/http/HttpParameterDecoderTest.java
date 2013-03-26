package org.craft.atom.protocol.http;

import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.craft.atom.protocol.ProtocolException;
import org.junit.Test;

/**
 * @author mindwind
 * @version 1.0, Mar 25, 2013
 */
public class HttpParameterDecoderTest {
	
	private HttpParameterDecoder decoder = new HttpParameterDecoder();
	private HttpParameterEncoder encoder = new HttpParameterEncoder();

	@Test
	public void test() throws ProtocolException {
		String queryString = "a=aa&%E6%B5%8B%E8%AF%95=%E6%B5%8B%E8%AF%95value&a=bb";
		List<Map<String, List<String>>> paralist = decoder.decode(queryString.getBytes());
		Map<String, List<String>> paras = paralist.get(0);
		Assert.assertEquals(2, paras.size());
		
		System.out.println(new String(encoder.encode(paras)));
	}
	
}
