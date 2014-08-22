package org.craft.atom.io;

/**
 * Unchecked exception thrown when an attempt is made to write data to channel that is not available.
 * 
 * @author mindwind
 * @version 1.0, Aug 22, 2014
 */
public class IllegalChannelStateException extends IllegalStateException {

	
	private static final long serialVersionUID = 668172854459635294L;

	
	public IllegalChannelStateException() {}

	public IllegalChannelStateException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalChannelStateException(String s) {
		super(s);
	}

	public IllegalChannelStateException(Throwable cause) {
		super(cause);
	}

}
