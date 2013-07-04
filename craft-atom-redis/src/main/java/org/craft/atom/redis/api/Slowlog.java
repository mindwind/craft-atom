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
	private final long elapse;

	/** The command with arguments. */
	private final List<String> command;

	public Slowlog(long id, long timestamp, long elapse, List<String> command) {
		super();
		this.id = id;
		this.timestamp = timestamp;
		this.elapse = elapse;
		this.command = command;
	}

	public long getId() {
		return id;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public long getElapse() {
		return elapse;
	}

	public List<String> getCommand() {
		return command;
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("{")
		   .append("class: ").append(getClass().getName()).append(", ")
		   .append("elapse: ").append(elapse).append(", ")
		   .append("command: ").append(command)
		   .append("}");
		return buf.toString();
	}

}
