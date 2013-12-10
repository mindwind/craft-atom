package org.craft.atom.protocol.ssl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyStore;
import java.security.Security;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.ws.ProtocolException;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craft.atom.io.AbstractIoHandler;
import org.craft.atom.io.Channel;
import org.craft.atom.io.IoHandler;
import org.craft.atom.nio.api.NioTcpAcceptor;
import org.craft.atom.test.AvailablePortFinder;
import org.craft.atom.test.CaseCounter;
import org.junit.Test;

/**
 * @author mindwind
 * @version 1.0, Oct 18, 2013
 */
public class TestSslCodec {
	
	
	private static final    Log    LOG        = LogFactory.getLog(TestSslCodec.class);
	private static final    String SSL_CODEC  = "ssl.codec"                          ;
	private static final    int    PORT                                              ;      
	private static final    int    MSG_NUM    = 100                                  ;
	private static final    String ALGORITHM                                         ;
	private static volatile int    count      = 0                                    ;
	
	
	static {
        String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null) {
            algorithm = KeyManagerFactory.getDefaultAlgorithm();
        }

        PORT      = AvailablePortFinder.getNextAvailable(5555);
        ALGORITHM = algorithm;
    }
	
	
	// ~ ---------------------------------------------------------------------------------------------------------
	
	
	private static SSLContext createSSLContext() {
		try {
			return createSSLContext0();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ProtocolException(e);
		}
	}
	
	private static SSLContext createSSLContext0() throws Exception {
        char[] passphrase       = "password".toCharArray();
        SSLContext ctx          = SSLContext.getInstance("TLS");
        KeyManagerFactory   kmf = KeyManagerFactory.getInstance(ALGORITHM);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(ALGORITHM);
        
        KeyStore ks = KeyStore.getInstance("JKS");
        KeyStore ts = KeyStore.getInstance("JKS");

        ks.load(TestSslCodec.class.getResourceAsStream("ssl.keystore"), passphrase);
        ts.load(TestSslCodec.class.getResourceAsStream("ssl.truststore"), passphrase);

        kmf.init(ks, passphrase);
        tmf.init(ts);
        ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        return ctx;
    }
	
	private static void startServer() throws Exception {
		IoHandler handler       = new TestIoHandler();
		NioTcpAcceptor acceptor = new NioTcpAcceptor(handler);
		acceptor.bind(PORT);
	}
	
    private static void startClient() throws Exception {
    	InetAddress      address = InetAddress.getByName("localhost");
        SSLContext       context = createSSLContext();
        SSLSocketFactory factory = context.getSocketFactory();
        connectAndSend(address, factory);
    }
    
    private static void connectAndSend(InetAddress address, SSLSocketFactory factory) throws Exception {
        Socket parent = new Socket(address, PORT);
        Socket socket = factory.createSocket(parent, address.getCanonicalHostName(), PORT, false);

        for (int i = 0; i < MSG_NUM; i++) {
            LOG.debug("Client send: hello-" + i);
            socket.getOutputStream().write("hello\n".getBytes());
            socket.getOutputStream().flush();
            socket.setSoTimeout(10000);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = in.readLine();
            LOG.debug("Client got: " + line);
            ++count;
		}
        
        socket.close();
    }
    
    @Test
    public void testSslCodec() throws Exception {
    	 startServer();
    	 Thread t = new Thread() {
             public void run() {
                 try {
                     startClient();
                 } catch (Exception e) {
                     clientError = e;
                 }
             }
         };
         t.start();
         if (clientError != null) {
             throw clientError;
         }
         t.join(3000);
         Assert.assertEquals(MSG_NUM, count);
         System.out.println(String.format("[CRAFT-ATOM-PROTOCOL-SSL] (^_^)  <%s>  Case -> test ssl codec. ", CaseCounter.incr(1)));
    }
    
    private static Exception clientError = null;
	
	
	// ~ ---------------------------------------------------------------------------------------------------------
	
	
	private static class TestIoHandler extends AbstractIoHandler {
		
		@Override
		public void channelOpened(Channel<byte[]> channel) {
			SSLContext ctx   = createSSLContext();
			SslCodec   codec = new SslCodec(ctx, new NioSslHandshakeHandler(channel));
			
			codec.init();
			channel.setAttribute(SSL_CODEC, codec);
		}

		@Override
		public void channelRead(Channel<byte[]> channel, byte[] bytes) {
			SslCodec codec = (SslCodec) channel.getAttribute(SSL_CODEC);
			byte[] ddata = codec.decode(bytes);
			if (ddata != null) { LOG.debug("Receive data: " + new String(ddata)); }
			
			if (ddata != null) {
				byte[] edata = codec.encode("hi, how are you?\n".getBytes());
				channel.write(edata);
				if (edata != null) { LOG.debug("Sent data: " + new String(edata)); }
			}
 		}
	}
	
	private static class NioSslHandshakeHandler implements SslHandshakeHandler {
		
		private Channel<byte[]> channel;
		
		public NioSslHandshakeHandler(Channel<byte[]> channel) {
			this.channel = channel;
		}

		@Override
		public void needWrite(byte[] bytes) {
			channel.write(bytes);
		}
	}
}
