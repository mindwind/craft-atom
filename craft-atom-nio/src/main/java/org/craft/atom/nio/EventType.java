package org.craft.atom.nio;

/**
 *  An {@link Enum} that represents the type of I/O events.
 *
 * @author Hu Feng
 * @version 1.0, 2011-12-15
 */
public enum EventType {
	SESSION_OPENED,
    SESSION_CLOSED,
    MESSAGE_RECEIVED,
    MESSAGE_SENT,
    SESSION_IDLE,
    EXCEPTION_CAUGHT
}
