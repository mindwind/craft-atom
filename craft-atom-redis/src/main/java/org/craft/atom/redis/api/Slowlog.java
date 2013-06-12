package org.craft.atom.redis.api;

import java.util.List;

/**
 * Redis slow log object.
 * 
 * @author mindwind
 * @version 1.0, Jun 12, 2013
 */
public class Slowlog {

	/** A unique progressive identifier for every slow log entry. */
	private final long id;

	/** The unix timestamp at which the logged command was processed. */
	private final long timestamp;

	/** The amount of time needed for its execution, in microseconds. */
	private final long executionTime;

	/** The array composing the arguments of the command. */
	private final List<String> commandWithArguments;

	public Slowlog(long id, long timestamp, long executionTime, List<String> commandWithArguments) {
		super();
		this.id = id;
		this.timestamp = timestamp;
		this.executionTime = executionTime;
		this.commandWithArguments = commandWithArguments;
	}

	public long getId() {
		return id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public long getExecutionTime() {
		return executionTime;
	}

	public List<String> getCommandWithArguments() {
		return commandWithArguments;
	}

	@Override
	public String toString() {
		return String
				.format("Slowlog [id=%s, timestamp=%s, executionTime=%s, commandWithArguments=%s]",
						id, timestamp, executionTime, commandWithArguments);
	}

}
