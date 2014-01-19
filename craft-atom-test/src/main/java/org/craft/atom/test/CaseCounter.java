package org.craft.atom.test;

import java.text.DecimalFormat;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test case counter.
 * 
 * @author mindwind
 * @version 1.0, Sep 26, 2013
 */
public class CaseCounter {
	
	
	private static final AtomicInteger counter = new AtomicInteger(0);
	
	
	public static String incr(int delta) {
		String format = "00000";
		DecimalFormat df = new DecimalFormat(format);
		long c = counter.addAndGet(delta);
		return df.format(c);
	}
}
