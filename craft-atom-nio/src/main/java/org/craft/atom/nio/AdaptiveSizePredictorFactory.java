package org.craft.atom.nio;

import org.craft.atom.nio.spi.SizePredictor;
import org.craft.atom.nio.spi.SizePredictorFactory;

/**
 * @author Hu Feng
 * @version 1.0, Jan 25, 2013
 */
public class AdaptiveSizePredictorFactory implements SizePredictorFactory {

	@Override
	public SizePredictor getPredictor() {
		return new AdaptiveSizePredictor();
	}

}
