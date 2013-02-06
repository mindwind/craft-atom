package org.craft.atom.protocol.http.model;

/**
 * Constants enumerating the HTTP headers. 
 * 
 * @author mindwind
 * @version 1.0, Feb 6, 2013
 */
public enum HttpHeaders {
	
	// ~ --------------------------------------------------------------------------------------- general headers
	
	
	/** Transfer-Encoding: chunked */
	TRANSFER_ENCODING("Transfer-Encoding"),
	
	
	// ~ --------------------------------------------------------------------------------------- request headers
	
	
	/** Accept: text/*, image/gif, image/jpeg;q=1 */
	ACCEPT("Accept"),
	
	
	// ~ --------------------------------------------------------------------------------------- response headers
	
	
	
	
	// ~ --------------------------------------------------------------------------------------- entity headers
	
	
	CONTENT_LENGTH("Content-Length"),
	CONTENT_ENCODING("Content-Encoding");
	
	
	// ~ ---------------------------------------------------------------------------------------------------------
	
	private final String name;

	private HttpHeaders(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
