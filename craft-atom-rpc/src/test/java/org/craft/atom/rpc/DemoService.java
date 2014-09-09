package org.craft.atom.rpc;

/**
 * @author mindwind
 * @version 1.0, Sep 5, 2014
 */
public interface DemoService {

	
	String echo(String in);
	String attachment();
	String oneway();
	void noreturn(String in);
	void timeout(String in) throws InterruptedException;
	void overload() throws InterruptedException;
	void bizException() throws IllegalAccessException;
	
}
