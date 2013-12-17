package org.craft.atom.protocol.http;

import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craft.atom.protocol.ProtocolException;
import org.craft.atom.test.CaseCounter;
import org.junit.Test;

/**
 * @author mindwind
 * @version 1.0, Mar 25, 2013
 */
public class TestHttpParameterDecoder {
	
	
	private static final Log LOG = LogFactory.getLog(TestHttpParameterDecoder.class);
	
	
	private HttpParameterDecoder decoder = new HttpParameterDecoder();
	private HttpParameterEncoder encoder = new HttpParameterEncoder();

	
	@Test
	public void testParameter() throws ProtocolException {
		String queryString = "a=aa&%E6%B5%8B%E8%AF%95=%E6%B5%8B%E8%AF%95value&a=bb";
		List<Map<String, List<String>>> paralist = decoder.decode(queryString.getBytes());
		Map<String, List<String>> paras = paralist.get(0);
		Assert.assertEquals(2, paras.size());
		LOG.debug(new String(encoder.encode(paras)));
		System.out.println(String.format("[CRAFT-ATOM-PROTOCOL-HTTP] (^_^)  <%s>  Case -> test parameter. ", CaseCounter.incr(1)));
	}
	
}
