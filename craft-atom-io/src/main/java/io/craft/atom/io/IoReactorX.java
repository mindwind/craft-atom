package io.craft.atom.io;

/**
 * The x-ray of {@link IoReactor}
 * 
 * @author mindwind
 * @version 1.0, Oct 15, 2014
 */
public interface IoReactorX {

	
	/**
	 * @return current alive channel count.
	 */
	int aliveChannelCount();
	
	/**
	 * @return current new channel to be processed count.
	 */
	int newChannelCount();
	
	/**
	 * @return current flushing channel count.
	 */
	int flushingChannelCount();
	
	/**
	 * @return current closing channel count.
	 */
	int closingChannelCount();
	
}
