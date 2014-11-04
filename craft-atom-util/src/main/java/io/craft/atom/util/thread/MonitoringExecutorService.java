package io.craft.atom.util.thread;

import java.util.concurrent.ExecutorService;

/**
 * An {@link ExecutorService} that provides extra monitoring function.
 * 
 * @author mindwind
 * @version 1.0, Oct 13, 2014
 */
public interface MonitoringExecutorService extends ExecutorService {
	
	/**
	 * @return the approximate wait task count of the executor.
	 */
	int waitCount();
	
	/**
	 * @return the approximate executing count of the executor.
	 */
	int executingCount();
	
	/**
	 * @return the approximate complete count of the executor.
	 */
	long completeCount();
	
}
