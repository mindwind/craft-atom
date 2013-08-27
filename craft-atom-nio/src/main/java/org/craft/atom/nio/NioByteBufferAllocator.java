package org.craft.atom.nio;

import java.nio.ByteBuffer;

import lombok.ToString;

/**
 * Allocate byte buffer and reuse original buffer as far as possible. A trade
 * off for memory waste and efficiency.
 * <p>
 * Not thread safe
 * 
 * @author mindwind
 * @version 1.0, Jan 25, 2013
 */
@ToString(of = { "buf", "exceedCount", "maxExceedCount", "percentual" })
public class NioByteBufferAllocator {
	
	private ByteBuffer buf;
    private int exceedCount;
    private final int maxExceedCount;
    private final int percentual;
    
    // ~ ----------------------
    
    NioByteBufferAllocator() {
        this(16, 80);
    }

    NioByteBufferAllocator(int maxExceedCount, int percentual) {
        this.maxExceedCount = maxExceedCount;
        this.percentual = percentual;
    }
    
    ByteBuffer allocate(int size) {
        if (buf == null) {
            return newBuffer(size);
        }
        if (buf.capacity() < size) {
            return newBuffer(size);
        }
        if (buf.capacity() * percentual / 100 > size) {
            if (++exceedCount == maxExceedCount) {
               return newBuffer(size);
            } else {
                buf.clear();
            }
        } else {
            exceedCount = 0;
            buf.clear();
        }
        return buf;
    }
    
    private ByteBuffer newBuffer(int size) {
        if (buf != null) {
            exceedCount = 0;
        }
        buf = ByteBuffer.allocate(normalizeCapacity(size));
        return buf;
    }

    private static int normalizeCapacity(int capacity) {
        // Normalize to multiple of 1024
        int q = capacity >>> 10;
        int r = capacity & 1023;
        if (r != 0) {
            q ++;
        }
        return q << 10;
    }
	
}
