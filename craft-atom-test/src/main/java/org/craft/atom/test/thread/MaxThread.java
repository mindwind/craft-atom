package org.craft.atom.test.thread;

import java.util.concurrent.atomic.AtomicInteger;

public class MaxThread extends Thread {

	private static final AtomicInteger count = new AtomicInteger();

	public static void main(String[] args) {
		while (true)
			(new MaxThread()).start();

	}

	@Override
	public void run() {
		System.out.println(count.incrementAndGet());

		while (true)
			try {
				Thread.sleep(Integer.MAX_VALUE);
			} catch (InterruptedException e) {
				break;
			}
	}

}
