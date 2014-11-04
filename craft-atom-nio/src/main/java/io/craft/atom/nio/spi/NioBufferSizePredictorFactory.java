package io.craft.atom.nio.spi;

/**
 * Factory of {@link NioBufferSizePredictor}
 * 
 * @author mindwind
 * @version 1.0, Feb 24, 2013
 */
public interface NioBufferSizePredictorFactory {
	
	/**
	 * Return a nio buffer size predictor
	 * 
	 * @param minimum  the inclusive lower bound of the expected buffer size
     * @param initial  the initial buffer size when no feed back was received
     * @param maximum  the inclusive upper bound of the expected buffer size
	 * @return a nio buffer size predictor
	 */
	NioBufferSizePredictor newPredictor(int minimum, int initial, int maximum);
	
}
