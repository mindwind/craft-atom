package org.craft.atom.test.nio;

import org.craft.atom.nio.api.Session;
import org.craft.atom.nio.spi.Handler;

public class CraftEchoHandler implements Handler {

	@Override
	public void sessionOpened(Session session) {
//		System.out.println("session open=" + session);
	}

	@Override
	public void sessionClosed(Session session) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sessionIdle(Session session) {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageReceived(Session session, byte[] bytes) {
		session.write(bytes);
	}

	@Override
	public void messageSent(Session session, byte[] bytes) {
		// TODO Auto-generated method stub

	}

	@Override
	public void exceptionCaught(Session session, Throwable cause) {
		// TODO Auto-generated method stub

	}

}
