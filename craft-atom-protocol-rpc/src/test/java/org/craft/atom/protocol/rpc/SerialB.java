package org.craft.atom.protocol.rpc;

import lombok.Getter;
import lombok.Setter;


/**
 * @author mindwind
 * @version 1.0, Jul 25, 2014
 */
public class SerialB {
	
	@Getter @Setter private SerialA sea  ;
	@Getter @Setter private byte[]  bytes;
}
