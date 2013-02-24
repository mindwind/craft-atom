package org.craft.atom.nio;

import org.craft.atom.nio.spi.NioBufferSizePredictor;
import org.craft.atom.nio.spi.NioBufferSizePredictorFactory;

/**
 * @author mindwind
 * @version 1.0, Feb 24, 2013
 */
public class NioAdaptiveBufferSizePredictorFactory implements NioBufferSizePredictorFactory {

	@Override
	public NioBufferSizePredictor newPredictor(int minimum, int initial, int maximum) {
		return new NioAdaptiveBufferSizePredictor(minimum, initial, maximum);
	}

}
