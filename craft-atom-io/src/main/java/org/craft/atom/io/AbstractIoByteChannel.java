package org.craft.atom.io;

/**
 * Abstract channel transmit bytes.
 * 
 * @author mindwind
 * @version 1.0, Feb 22, 2013
 */
abstract public class AbstractIoByteChannel extends AbstractIoChannel implements Channel<byte[]> {

	public AbstractIoByteChannel() {
		super();
	}
	
	public AbstractIoByteChannel(long id) {
		super(id);
	}
	
	public AbstractIoByteChannel(int minReadBufferSize) {
		super(minReadBufferSize);
	}
	
	public AbstractIoByteChannel(int minReadBufferSize, int defaultReadBufferSize) {
		super(minReadBufferSize, defaultReadBufferSize);
	}

	public AbstractIoByteChannel(int minReadBufferSize, int defaultReadBufferSize, int maxReadBufferSize) {
		super(minReadBufferSize, defaultReadBufferSize, maxReadBufferSize);
	}
	
}
