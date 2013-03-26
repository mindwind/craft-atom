package org.craft.atom.protocol.http.model;


/**
 * MIME type enumeration.
 * <p>
 * <a href="http://www.iana.org/assignments/media-types">MIME type reference </a>
 * 
 * @author mindwind
 * @version 1.0, Mar 5, 2013
 */
public enum MimeType {
	
	// ~ ---------------------------------------------------------------------------------------------------------
	
	
	// application
	APPLICATION_X_WWW_FORM_URLENCODED("application", "x-www-form-urlencoded"),
	APPLICATION_JSON("application", "json"),
	APPLICATION_XML("application", "xml"),
	APPLICATION_JAVASCRIPT("application", "javascript"),
	
	// text
	TEXT_HTML("text", "html"),
	TEXT_XML("text", "xml"),
	TEXT_PLAIN("text", "plain"),
	
	// multipart
	MULTIPART_FORM_DATA("multipart", "form-data"),
	
	// wildcard
	WILDCARD("*", "*");
	
	
	// ~ ---------------------------------------------------------------------------------------------------------
	
	public static MimeType from(String type) {
		if (APPLICATION_X_WWW_FORM_URLENCODED.toString().equalsIgnoreCase(type)) {
			return APPLICATION_X_WWW_FORM_URLENCODED;
		}
		if (APPLICATION_JSON.toString().equalsIgnoreCase(type)) {
			return APPLICATION_JSON;
		}
		if (APPLICATION_XML.toString().equalsIgnoreCase(type)) {
			return APPLICATION_XML;
		}
		if (APPLICATION_JAVASCRIPT.toString().equalsIgnoreCase(type)) {
			return APPLICATION_JAVASCRIPT;
		}
		if (TEXT_HTML.toString().equalsIgnoreCase(type)) {
			return TEXT_HTML;
		}
		if (TEXT_XML.toString().equalsIgnoreCase(type)) {
			return TEXT_XML;
		}
		if (TEXT_PLAIN.toString().equalsIgnoreCase(type)) {
			return TEXT_PLAIN;
		}
		if (MULTIPART_FORM_DATA.toString().equalsIgnoreCase(type)) {
			return MULTIPART_FORM_DATA;
		}
		if (MULTIPART_FORM_DATA.toString().equalsIgnoreCase(type)) {
			return MULTIPART_FORM_DATA;
		}
		
		throw new IllegalArgumentException("unsupported content type.");
	}
	
	@Override
	public String toString() {
		return primaryType + "/" + subType;
	}
	
	// ~ ---------------------------------------------------------------------------------------------------------
	
	private final String primaryType;
	private final String subType;

	private MimeType(String primaryType, String subType) {
		this.primaryType = primaryType;
		this.subType = subType;
	}

	public String getPrimaryType() {
		return primaryType;
	}

	public String getSubType() {
		return subType;
	}
	
}
