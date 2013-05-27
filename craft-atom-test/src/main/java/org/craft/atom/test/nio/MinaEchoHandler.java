package org.craft.atom.test.nio;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 * @author Hu Feng
 * @version 1.0, Jan 22, 2013
 */
public class MinaEchoHandler implements IoHandler {

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		System.out.println("session open=" + session);
	}

	@Override
	public void sessionClosed(IoSession session) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		session.write(getHttpResponse());
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		// TODO Auto-generated method stub
//		System.out.println("session=" + session + ", sent=" + message);
	}
	
	private String getHttpResponse() {
		String rsp = "";
		rsp += "HTTP/1.1 200 OK\r\n";
		rsp += "Server: arthas\r\n";
		rsp += "Date: Thu, 18 Apr 2013 10:52:34 GMT\r\n";
		rsp += "Content-Length: 11\r\n";
		rsp += "Content-Type: text/html; charset=UTF-8\r\n";
		rsp += "Connection: close\r\n\r\n";
		rsp += "hello, SSL!";
		return rsp;
	}

}
