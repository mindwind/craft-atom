package org.craft.atom.nio;

import org.craft.atom.nio.spi.SizePredictor;
import org.craft.atom.nio.spi.SizePredictorFactory;

/**
 * @author Hu Feng
 * @version 1.0, Jan 25, 2013
 */
public class AdaptiveSizePredictorFactory implements SizePredictorFactory {

	@Override
	public SizePredictor getPredictor(int minimum, int initial, int maximum) {
		return new AdaptiveSizePredictor(minimum, initial, maximum);
	}

}
