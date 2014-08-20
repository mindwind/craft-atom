package org.craft.atom.nio;

import lombok.ToString;

import org.craft.atom.io.ChannelEventType;
import org.craft.atom.io.IoHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * NIO byte channel event consume by {@link IoHandler}
 * 
 * @author mindwind
 * @version 1.0, Feb 21, 2013
 */
@ToString(of = { "parameter" })
public class NioByteChannelEvent extends AbstractNioByteChannelEvent {
	
	
	private static final Logger LOG = LoggerFactory.getLogger(NioByteChannelEvent.class);
	
	
	private final Object    parameter;
	private final IoHandler handler  ;
	
	
	// ~ --------------------------------------------------------------------------------------------------------------
	
	
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
	
	
	// ~ --------------------------------------------------------------------------------------------------------------
	
	
	@Override
	public void fire() {
		try {
			fire0();
		} catch (Exception e) {
			try {
				handler.channelThrown(channel, e);
			} catch (Exception ex) {
				LOG.info("[CRAFT-ATOM-NIO] Catch channel thrown exception", ex);
			}
		}
	}
	
	private void fire0() {
		switch (type) {
		case CHANNEL_READ:
			handler.channelRead(channel, (byte[]) parameter);
			break;
		case CHANNEL_FLUSH:
			handler.channelFlush(channel, (byte[]) parameter);
			break;
		case CHANNEL_WRITTEN:
			handler.channelWritten(channel, (byte[]) parameter);
			break;
		case CHANNEL_THROWN:
			handler.channelThrown(channel, (Exception) parameter);
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
	
}
