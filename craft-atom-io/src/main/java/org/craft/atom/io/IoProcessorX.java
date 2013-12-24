package org.craft.atom.io;

import java.io.Serializable;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author mindwind
 * @version 1.0, Dec 24, 2013
 */
@ToString
public class IoProcessorX implements Serializable {

	
	private static final long serialVersionUID = 3536608515158732642L;
	
	
	@Getter @Setter private Set<Channel<byte[]>> newChannels     ;
	@Getter @Setter private Set<Channel<byte[]>> flushingChannels;
	@Getter @Setter private Set<Channel<byte[]>> closingChannels ;
	@Getter @Setter private Set<Channel<byte[]>> aliveChannels   ;
	
}
