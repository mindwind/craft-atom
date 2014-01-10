package org.craft.atom.protocol;

/** 
 * Some common protocol exception type enumeration
 * 
 * @author mindwind
 * @version 1.0, Feb 4, 2013
 */
public enum ProtocolExceptionType {

	
	UNEXPECTED       ("Unexpected protocol processing error, cause="),
	LINE_LENGTH_LIMIT("Line length limit exceeded, limit="), 
	MAX_SIZE_LIMIT   ("Max size limit exceeded, limit=");
	
	
	private final String desc;

	
	private ProtocolExceptionType(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}
	
}
