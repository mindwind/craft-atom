package org.craft.atom.redis;

/**
 * @author mindwind
 * @version 1.0, Jun 28, 2013
 */
public abstract class AbstractMain {
	
	protected static void before(String desc) {
		System.out.println("case -- " + desc);
	}
	
	protected static void after() {
		
	}
	
}
