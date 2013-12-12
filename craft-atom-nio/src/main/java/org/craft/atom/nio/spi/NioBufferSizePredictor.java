package org.craft.atom.nio.spi;

/**
 * Predicts the nio buffer size.
 * <p>
 * Predict buffer size allocation, More accurate the prediction is, more effective the memory utilization will be.
 *
 * @author mindwind
 * @version 1.0, Jan 25, 2013
 */
public interface NioBufferSizePredictor {
	
	/**
	 * Predicts the size of next operaion.
	 * 
	 * @return the expected size at this time for next operation
	 */
	int next();
	
	/**
	 * Updates predictor by specifying the actual size in the previous operation.
	 * 
	 * @param previousSize the actual size in the previous read operation
	 */
	void previous(int previousSize);
	
}
