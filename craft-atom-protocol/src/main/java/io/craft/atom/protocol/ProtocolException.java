package io.craft.atom.protocol;

import java.util.Arrays;



/**
 * An exception that is thrown when {@link ProtocolEncoder} or {@link ProtocolDecoder} 
 * cannot understand or failed to validate data to process.
 *
 * @author mindwind
 * @version 1.0, Oct 16, 2012
 */
public class ProtocolException extends RuntimeException {

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

	public ProtocolException(ProtocolExceptionType type, Object... params) {
		super(type.getDesc() + Arrays.toString(params));
	}
	
}
