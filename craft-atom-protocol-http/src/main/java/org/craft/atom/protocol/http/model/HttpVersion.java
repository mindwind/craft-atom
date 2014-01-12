package org.craft.atom.protocol.http.model;

/**
 * Represents an HTTP version. HTTP uses a "major.minor" numbering
 * scheme to indicate versions of the protocol.
 * <p>
 * The version of an HTTP message is indicated by an HTTP-Version field
 * in the first line of the message.
 * </p>
 * <pre>
 *     HTTP-Version   = "HTTP" "/" 1*DIGIT "." 1*DIGIT
 * </pre>
 * 
 * @author mindwind
 * @version 1.0, Feb 1, 2013
 * @see HttpStartLine
 * @see HttpRequestLine
 * @see HttpStatusLine
 */
public enum HttpVersion {
	
	
	HTTP_1_1("HTTP/1.1"), 
	HTTP_1_0("HTTP/1.0"),
	HTTP_0_9("HTTP/0.9");
	
	
	private final String value;

	private HttpVersion(String value) {
		this.value = value;
	}
	
	/**
	 * Returns the {@link HttpVersion} instance from the specified string.
	 * 
	 * @return The version, or <code>null</code> if no version is matched
	 */
	public static HttpVersion from(String version) {
		if (HTTP_1_1.value.equalsIgnoreCase(version)) {
			return HTTP_1_1;
		}

		if (HTTP_1_0.value.equalsIgnoreCase(version)) {
			return HTTP_1_0;
		}
		
		if (HTTP_0_9.value.equalsIgnoreCase(version)) {
			return HTTP_0_9;
		}

		return null;
	}

	public String getValue() {
		return value;
	}
	
}
