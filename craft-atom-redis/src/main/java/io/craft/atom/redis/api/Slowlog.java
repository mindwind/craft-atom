package io.craft.atom.redis.api;

import java.util.List;

import lombok.Getter;
import lombok.ToString;

/**
 * Redis slow log object.
 * 
 * @author mindwind
 * @version 1.0, Jun 12, 2013
 */
@ToString
public class Slowlog {

	/** 
	 * Fields description:<br>
	 * id       : A unique progressive identifier for every slow log entry. <br>
	 * timestamp: The unix timestamp at which the logged command was processed.<br>
	 * elapse   : The amount of time needed for its execution, in microseconds.<br>
	 * command  : The command with arguments. 
	 */
	@Getter private final long         id       ;
	@Getter private final long         timestamp;
	@Getter private final long         elapse   ;
	@Getter private final List<String> command  ;
	
	
	// ~ -----------------------------------------------------------------------------------------------------------
	

	public Slowlog(long id, long timestamp, long elapse, List<String> command) {
		super();
		this.id        = id       ;
		this.timestamp = timestamp;
		this.elapse    = elapse   ;
		this.command   = command  ;
	}

}
