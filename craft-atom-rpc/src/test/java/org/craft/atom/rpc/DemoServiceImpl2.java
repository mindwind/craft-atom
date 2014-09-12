package org.craft.atom.rpc;

/**
 * @author mindwind
 * @version 1.0, Sep 12, 2014
 */
public class DemoServiceImpl2 extends DemoServiceImpl1 {

	@Override
	public String echo(String in) {
		return in + in;
	}

}
