package org.craft.atom.nio;

import java.util.ArrayList;
import java.util.List;

import lombok.ToString;

import org.craft.atom.nio.spi.NioBufferSizePredictor;

/**
 * The {@link NioAdaptiveBufferSizePredictor} that automatically increases and
 * decreases the predicted buffer size on feed back.
 * <p>
 * It gradually increases the expected number of readable bytes if the previous
 * read fully filled the allocated buffer. It gradually decreases the expected
 * number of readable bytes, if the read operation was not able to fill a
 * certain amount of the allocated buffer two times consecutively. Otherwise, it
 * keeps returning the same prediction.
 * 
 * @author netty, this implementation from netty framework
 */
@ToString(of = { "minIndex", "maxIndex", "index", "nextSize", "decreaseNow" })
public class NioAdaptiveBufferSizePredictor implements NioBufferSizePredictor {
	
	private static final int DEFAULT_MINIMUM = 64;
	private static final int DEFAULT_INITIAL = 1024;
	private static final int DEFAULT_MAXIMUM = 65536;
	private static final int INDEX_INCREMENT = 4;
    private static final int INDEX_DECREMENT = 1;
    private static final int[] SIZE_TABLE;
    
    private final int minIndex;
    private final int maxIndex;
    private int index;
    private int nextSize;
    private boolean decreaseNow;
    
    // ~ -------------------------------------------------------------------------------------------------------------

    static {
        List<Integer> sizeTable = new ArrayList<Integer>();
        for (int i = 1; i <= 8; i ++) {
            sizeTable.add(i);
        }

        for (int i = 4; i < 32; i ++) {
            long v = 1L << i;
            long inc = v >>> 4;
            v -= inc << 3;

            for (int j = 0; j < 8; j ++) {
                v += inc;
                if (v > Integer.MAX_VALUE) {
                    sizeTable.add(Integer.MAX_VALUE);
                } else {
                    sizeTable.add((int) v);
                }
            }
        }

        SIZE_TABLE = new int[sizeTable.size()];
        for (int i = 0; i < SIZE_TABLE.length; i ++) {
            SIZE_TABLE[i] = sizeTable.get(i);
        }
    }
    
    private static int getSizeTableIndex(final int size) {
        if (size <= 16) {
            return size - 1;
        }

        int bits = 0;
        int v = size;
        do {
            v >>>= 1;
            bits ++;
        } while (v != 0);

        final int baseIdx = bits << 3;
        final int startIdx = baseIdx - 18;
        final int endIdx = baseIdx - 25;

        for (int i = startIdx; i >= endIdx; i --) {
            if (size >= SIZE_TABLE[i]) {
                return i;
            }
        }

        throw new Error("shouldn't reach here; please file a bug report.");
    }
    
    // ~ -------------------------------------------------------------------------------------------------------------
    
    /**
     * Creates a new predictor with the default parameters.  With the default
     * parameters, the expected buffer size starts from {@code 1024}, does not
     * go down below {@code 64}, and does not go up above {@code 65536}.
     */
    NioAdaptiveBufferSizePredictor() {
        this(DEFAULT_MINIMUM, DEFAULT_INITIAL, DEFAULT_MAXIMUM);
    }

    /**
     * Creates a new predictor with the specified parameters.
     *
     * @param minimum  the inclusive lower bound of the expected buffer size
     * @param initial  the initial buffer size when no feed back was received
     * @param maximum  the inclusive upper bound of the expected buffer size
     */
    NioAdaptiveBufferSizePredictor(int minimum, int initial, int maximum) {
        if (minimum <= 0) {
            throw new IllegalArgumentException("minimum: " + minimum);
        }
        if (initial < minimum) {
            throw new IllegalArgumentException("initial: " + initial);
        }
        if (maximum < initial) {
            throw new IllegalArgumentException("maximum: " + maximum);
        }

        int minIndex = getSizeTableIndex(minimum);
        if (SIZE_TABLE[minIndex] < minimum) {
            this.minIndex = minIndex + 1;
        } else {
            this.minIndex = minIndex;
        }

        int maxIndex = getSizeTableIndex(maximum);
        if (SIZE_TABLE[maxIndex] > maximum) {
            this.maxIndex = maxIndex - 1;
        } else {
            this.maxIndex = maxIndex;
        }

        index = getSizeTableIndex(initial);
        nextSize = SIZE_TABLE[index];
    }
    
    // ~ -------------------------------------------------------------------------------------------------------------

	@Override
	public int next() {
		 return nextSize;
	}

	@Override
	public void previous(int previousSize) {
		if (previousSize <= SIZE_TABLE[Math.max(0, index - INDEX_DECREMENT - 1)]) {
            if (decreaseNow) {
                index = Math.max(index - INDEX_DECREMENT, minIndex);
                nextSize = SIZE_TABLE[index];
                decreaseNow = false;
            } else {
                decreaseNow = true;
            }
        } else if (previousSize >= nextSize) {
            index = Math.min(index + INDEX_INCREMENT, maxIndex);
            nextSize = SIZE_TABLE[index];
            decreaseNow = false;
        }
	}

	static int[] getSizeTable() {
		return SIZE_TABLE;
	}

}
