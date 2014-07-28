package org.craft.atom.protocol.rpc;

import lombok.Getter;

/**
 * @author mindwind
 * @version 1.0, Jul 25, 2014
 */
public enum SerialEnum {
	
	A(1, "a"),
	B(2, "b");
	
	@Getter private int    code;
	@Getter private String desc;
	
	private SerialEnum(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}

}
