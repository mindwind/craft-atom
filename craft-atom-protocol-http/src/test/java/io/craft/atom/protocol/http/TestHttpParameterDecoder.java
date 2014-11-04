package io.craft.atom.protocol.http;

import io.craft.atom.protocol.ProtocolDecoder;
import io.craft.atom.protocol.ProtocolEncoder;
import io.craft.atom.protocol.ProtocolException;
import io.craft.atom.protocol.http.api.HttpCodecFactory;
import io.craft.atom.test.CaseCounter;

import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mindwind
 * @version 1.0, Mar 25, 2013
 */
public class TestHttpParameterDecoder {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(TestHttpParameterDecoder.class);
	
	
	private ProtocolDecoder<Map<String, List<String>>> decoder = HttpCodecFactory.newHttpParameterDecoder();
	private ProtocolEncoder<Map<String, List<String>>> encoder = HttpCodecFactory.newHttpParameterEncoder();

	
	@Test
	public void testParameter() throws ProtocolException {
		String queryString = "a=aa&%E6%B5%8B%E8%AF%95=%E6%B5%8B%E8%AF%95value&a=bb";
		List<Map<String, List<String>>> paralist = decoder.decode(queryString.getBytes());
		Map<String, List<String>> paras = paralist.get(0);
		Assert.assertEquals(2, paras.size());
		LOG.debug("[CRAFT-ATOM-PROTOCOL-HTTP] Encoded parameter={}", new String(encoder.encode(paras)));
		System.out.println(String.format("[CRAFT-ATOM-PROTOCOL-HTTP] (^_^)  <%s>  Case -> test parameter. ", CaseCounter.incr(1)));
	}
	
}
