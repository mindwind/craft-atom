package org.craft.atom.protocol;


/**
 * An exception that is thrown when {@link ProtocolEncoder} or {@link ProtocolDecoder} 
 * cannot understand or failed to validate data to process.
 *
 * @author Hu Feng
 * @version 1.0, Oct 16, 2012
 */
public class ProtocolException extends Exception {

	private static final long serialVersionUID = 2606442495710868565L;

	public ProtocolException() {
		super();
	}

	public ProtocolException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProtocolException(String message) {
		super(message);
	}

	public ProtocolException(Throwable cause) {
		super(cause);
	}

}
