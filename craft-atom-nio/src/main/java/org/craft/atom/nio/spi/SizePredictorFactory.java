package org.craft.atom.nio.spi;

/**
 * The factory object for {@link SizePredictor}
 * 
 * @author mindwind
 * @version 1.0, Jan 25, 2013
 */
public interface SizePredictorFactory {
	
	/**
	 * Return a size predictor
	 * 
	 * @param minimum  the inclusive lower bound of the expected buffer size
     * @param initial  the initial buffer size when no feed back was received
     * @param maximum  the inclusive upper bound of the expected buffer size
	 * @return
	 */
	SizePredictor getPredictor(int minimum, int initial, int maximum);
	
}
