package org.craft.atom.nio.spi;


/**
 * @author Hu Feng
 * @version 1.0, Jan 25, 2013
 */
public interface SizePredictorFactory {
	
	/**
	 * Return a size predictor
	 * 
	 * @return
	 */
	SizePredictor getPredictor();
	
}
