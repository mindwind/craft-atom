package org.craft.atom.nio;

import java.io.IOException;

import org.craft.atom.nio.api.TcpAcceptor;
import org.junit.Assert;

/**
 * Tests for {@link TcpAcceptor}
 *
 * @author Hu Feng
 * @version 1.0, 2011-12-20
 */
public class TcpAcceptorMain {
	
	private static final int PORT = 12345;
	
	public static void main(String[] args) throws IOException {
		TcpAcceptorMain tam = new TcpAcceptorMain();
		
		// case 1
		tam.testDuplicateBind();
		
		// case 2
		tam.testDuplicateUnbind();
		
		// case 3
		tam.testManyTimes();
	}
	
    public void testDuplicateBind() throws IOException {
		TcpAcceptor acceptor = new TcpAcceptor(new EchoHandler(), PORT);
		
		Assert.assertEquals(1, acceptor.getBoundAddresses().size());
		
		try {
			acceptor.bind(PORT);
			Assert.fail("Exception is not thrown");
		} catch(IOException e) {
			e.printStackTrace();
			Assert.assertTrue(true);
		}
	}
	
    public void testDuplicateUnbind() throws IOException {
    	TcpAcceptor acceptor = new TcpAcceptor(new EchoHandler(), PORT);

        // this should succeed
        acceptor.unbind(PORT);

        // this shouldn't fail
        acceptor.unbind(PORT);
        
        Assert.assertEquals(0, acceptor.getBoundAddresses().size());
    }
    
    public void testManyTimes() throws IOException {
    	TcpAcceptor acceptor = new TcpAcceptor(new EchoHandler(), PORT);

        for (int i = 0; i < 1024; i++) {
            acceptor.unbind(PORT);
            Assert.assertEquals(0, acceptor.getBoundAddresses().size());
            acceptor.bind(PORT);
        }
        Assert.assertEquals(1, acceptor.getBoundAddresses().size());
    }
	
}
