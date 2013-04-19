package org.craft.atom.test.nio;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

/**
 * @author mindwind
 * @version 1.0, Apr 18, 2013
 */
public class MinaSslEchoServer {
	
	private static final int PORT = 7180;
	
	private static KeyManagerFactory KEY_MANAGER_FACTORY;
	
	static {
		InputStream is = null;
		try {
			char[] keystorePassword = "chat.jd.com".toCharArray();
			KeyStore ks = KeyStore.getInstance("JKS");
			is = new FileInputStream("/Users/mindwind/chatkeys");
			ks.load(is, keystorePassword);
			KEY_MANAGER_FACTORY = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			KEY_MANAGER_FACTORY.init(ks, keystorePassword);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		IoAcceptor acceptor = new NioSocketAcceptor();
		
		DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
		SSLContext sslContext = SSLContext.getInstance("SSL");
		sslContext.init(KEY_MANAGER_FACTORY.getKeyManagers(), null, null);
		SslFilter sslFilter = new SslFilter(sslContext);
		chain.addLast("sslFilter", sslFilter);   
        chain.addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));  
        
        acceptor.setHandler(new MinaEchoHandler());
		acceptor.bind(new InetSocketAddress(PORT));
		System.out.println("mina ssl echo server listening on port=" + PORT);
	}

}
