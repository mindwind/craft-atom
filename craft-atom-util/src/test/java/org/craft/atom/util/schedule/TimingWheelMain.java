package org.craft.atom.util.schedule;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Hu Feng
 * @version 1.0, Sep 21, 2012
 */
public class TimingWheelMain {
	
	private TimingWheel<String> timingWheel;
	private long startTime;
	private long endTime;
	
	public static void main(String[] args) throws InterruptedException {
		TimingWheelMain twm = new TimingWheelMain();
		twm.setup();
		
		// case 1
		twm.testAdd();
		
		// case 2
		twm.testAddTwice();
		
		// case 3
		twm.testRemove();
	}
	
	public void setup() {
		timingWheel = new TimingWheel<String>(1, 15, TimeUnit.SECONDS);
		timingWheel.addExpirationListener(new TestExpirationListener());
		timingWheel.start();
	}
	
	public void testAdd() throws InterruptedException {
		startTime = System.currentTimeMillis();
		long ttl = timingWheel.add("test-0");
		System.out.println("Add object test-0 to timing wheel, will timeout after " + ttl + " ms, start-time=" + new Date(startTime));
		for (int i = 1; i <= 30; i++) {
			Thread.sleep(1000);
			timingWheel.add("test-" + i);
		}
		
		Thread.sleep(60 * 1000);
	}
	
	public void testRemove() throws InterruptedException {
		for (int i = 1; i <= 10; i++) {
			Thread.sleep(100);
			timingWheel.add("test-" + i);
		}
		
		Thread.sleep(10000);
		timingWheel.remove("test-3");
		timingWheel.remove("test-4");
		timingWheel.remove("test-5");
		Thread.sleep(60 * 1000);
	}
	
	public void testAddTwice() throws InterruptedException {
		startTime = System.currentTimeMillis();
		long ttl = timingWheel.add("test-1");
		System.out.println("Add object test-1 to timing wheel, will timeout after " + ttl + " ms, start-time=" + new Date(startTime));
		
		Thread.sleep(10000);
		ttl = timingWheel.add("test-1");
		System.out.println("Add object test-1 second to timing wheel, will timeout after " + ttl + " ms, start-time=" + new Date(startTime));
		Thread.sleep(60 * 1000);
	}
	
	private class TestExpirationListener implements ExpirationListener<String> {
		@Override
		public void expired(String expiredObject) {
			endTime = System.currentTimeMillis();
			System.out.println("Object expired: " + expiredObject + ", end-time=" + new Date(endTime));
		}
	}
}
