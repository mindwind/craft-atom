package org.craft.atom.io;

import java.io.Serializable;
import java.util.Collections;
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
	
	
	private static final long serialVersionUID = 5369014456865137851L;
	
	
	@Getter @Setter private Set<Channel<byte[]>> newChannels      = Collections.emptySet();
	@Getter @Setter private Set<Channel<byte[]>> flushingChannels = Collections.emptySet();
	@Getter @Setter private Set<Channel<byte[]>> closingChannels  = Collections.emptySet();
	
}
