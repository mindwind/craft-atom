package org.craft.atom.nio;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.craft.atom.io.ChannelEventType;
import org.craft.atom.io.IoHandler;

/**
 * NIO byte channel event consume by {@link IoHandler}
 * 
 * @author mindwind
 * @version 1.0, Feb 21, 2013
 */
public class NioByteChannelEvent extends AbstractNioByteChannelEvent {
	
	private static final Log LOG = LogFactory.getLog(NioByteChannelEvent.class);
	
	private final Object parameter;
	private final IoHandler handler;
	
	// ~ ----------------------------------------------------------------------------------------------------------
	
	NioByteChannelEvent(ChannelEventType type, NioByteChannel channel, IoHandler handler) {
        this(type, channel, handler, null);
    }
	
	NioByteChannelEvent(ChannelEventType type, NioByteChannel channel, IoHandler handler, Object parameter) {
		super(type, channel);
		
        if (handler == null) {
        	throw new IllegalArgumentException("handler == null");
        }
        
        this.handler = handler;
        this.parameter = parameter;
    }
	
	// ~ ----------------------------------------------------------------------------------------------------------
	
	@Override
	public void fire() {
		try {
			fire0();
		} catch (Throwable t) {
			try {
				handler.channelThrown(channel, t);
			} catch (Throwable t1) {
				LOG.warn("Catch hanlder.channelThrown() thrown exception", t1);
			}
		}
	}
	
	private void fire0() {
		switch (type) {
		case CHANNEL_READ:
			handler.channelRead(channel, (byte[]) parameter);
			break;
		case CHANNEL_WRITTEN:
			handler.channelWritten(channel, (byte[]) parameter);
			break;
		case CHANNEL_THROWN:
			handler.channelThrown(channel, (Throwable) parameter);
			break;
		case CHANNEL_IDLE:
			handler.channelIdle(channel);
			break;
		case CHANNEL_OPENED:
			handler.channelOpened(channel);
			break;
		case CHANNEL_CLOSED:
			handler.channelClosed(channel);
			break;
		default:
			throw new IllegalArgumentException("Unknown event type: " + type);
		}
	}

	@Override
	public String toString() {
		return String
				.format("NioByteChannelEvent [type=%s, channel=%s, parameter=%s, handler=%s]", type, channel, parameter, handler);
	}

}
