package org.craft.atom.rpc;

/**
 * @author mindwind
 * @version 1.0, Sep 5, 2014
 */
public class DefaultDemoService implements DemoService {

	
	@Override
	public String echo(String in) {
		return in;
	}

	@Override
	public void give(String in) {
		System.out.println("Invoked give() in=" + in);
	}

	@Override
	public void timeout(String in) throws InterruptedException {
		Thread.sleep(200);
	}

}
