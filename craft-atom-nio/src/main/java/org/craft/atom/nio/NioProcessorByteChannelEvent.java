package org.craft.atom.nio;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.craft.atom.io.ChannelEventType;

/**
 * NIO byte channel event consume by {@link NioProcessor}
 * 
 * @author mindwind
 * @version 1.0, Feb 26, 2013
 */
public class NioProcessorByteChannelEvent extends NioByteChannelEvent {
	
	private final NioProcessor processor;
	private final byte[] parameter;

	NioProcessorByteChannelEvent(ChannelEventType type, NioByteChannel channel, NioProcessor processor, byte[] parameter) {
		super(type, channel);
		
		if (processor == null) {
        	throw new IllegalArgumentException("processor == null");
        }
		
		this.processor = processor;
		this.parameter = parameter;
	}

	@Override
	public void fire() {
		channel.getWriteBufferQueue().add(ByteBuffer.wrap(parameter));
		processor.flush(channel);
	}

	@Override
	public String toString() {
		return String
				.format("NioProcessorByteChannelEvent [processor=%s, parameter=%s, channel=%s, type=%s]",
						processor, Arrays.toString(parameter), channel, type);
	}

}
