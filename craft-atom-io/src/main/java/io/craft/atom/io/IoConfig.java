package io.craft.atom.io;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * I/O common configuration object
 * 
 * @author mindwind
 * @version 1.0, Feb 22, 2013
 */
@ToString(of = { "minReadBufferSize", "defaultReadBufferSize", "maxReadBufferSize", "ioTimeoutInMillis" })
public abstract class IoConfig {

	
	public static final int MIN_READ_BUFFER_SIZE     = 64   ;
	public static final int DEFAULT_READ_BUFFER_SIZE = 2048 ;
	public static final int MAX_READ_BUFFER_SIZE     = 65536;

	
	@Getter @Setter protected int minReadBufferSize     = MIN_READ_BUFFER_SIZE    ;
	@Getter @Setter protected int defaultReadBufferSize = DEFAULT_READ_BUFFER_SIZE;
	@Getter @Setter protected int maxReadBufferSize     = MAX_READ_BUFFER_SIZE    ;
	@Getter @Setter protected int ioTimeoutInMillis     = 120 * 1000              ;
	
}
