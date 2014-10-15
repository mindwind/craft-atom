package org.craft.atom.io;

import java.net.SocketAddress;
import java.util.Set;


/**
 * The x-ray of {@link IoAcceptor}
 * 
 * @author mindwind
 * @version 1.0, Oct 15, 2014
 */
public interface IoAcceptorX extends IoReactorX {
	
	/**
	 * @return wait to bind address set.
	 */
	Set<SocketAddress> waitBindAddresses();
	
	/**
	 * @return wait to unbind address set.
	 */
	Set<SocketAddress> waitUnbindAddresses();
	
	/**
	 * @return already bound address set.
	 */
	Set<SocketAddress> boundAddresses();

}
