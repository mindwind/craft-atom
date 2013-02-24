package org.craft.atom.nio;

import java.io.IOException;

import org.craft.atom.nio.api.NioTcpAcceptor;
import org.junit.Assert;

/**
 * @author mindwind
 * @version 1.0, 2011-12-20
 */
public class NioTcpAcceptorMain {
	
	private static final int PORT = 12345;
	
	public static void main(String[] args) throws IOException {
		NioTcpAcceptorMain tam = new NioTcpAcceptorMain();
		
		// case 1
		tam.testDuplicateBind();
		
		// case 2
		tam.testDuplicateUnbind();
		
		// case 3
		tam.testBindAndUnbindManyTimes(16);
		
		// exit
		System.exit(0);
	}
	
    public void testDuplicateBind() throws IOException {
		NioTcpAcceptor acceptor = new NioTcpAcceptor(new NioEchoHandler(), PORT);
		
		Assert.assertEquals(1, acceptor.getBoundAddresses().size());
		
		try {
			acceptor.bind(PORT);
			Assert.fail("Exception is not thrown");
		} catch(IOException e) {
			System.out.println("Duplicate bind throw exception=" + e);
			Assert.assertTrue(true);
		}
	}
	
    public void testDuplicateUnbind() throws IOException {
    	NioTcpAcceptor acceptor = new NioTcpAcceptor(new NioEchoHandler(), PORT);

        // this should succeed
        acceptor.unbind(PORT);

        // this shouldn't fail
        acceptor.unbind(PORT);
        System.out.println("Duplicate unbind pass...");
        
        Assert.assertEquals(0, acceptor.getBoundAddresses().size());
    }
    
    public void testBindAndUnbindManyTimes(int times) throws IOException {
    	NioTcpAcceptor acceptor = new NioTcpAcceptor(new NioEchoHandler(), PORT);

        for (int i = 0; i < times; i++) {
            acceptor.unbind(PORT);
            Assert.assertEquals(0, acceptor.getBoundAddresses().size());
            acceptor.bind(PORT);
            System.out.println("Bind and unbind time=" + i);
        }
        Assert.assertEquals(1, acceptor.getBoundAddresses().size());
    }
	
}
