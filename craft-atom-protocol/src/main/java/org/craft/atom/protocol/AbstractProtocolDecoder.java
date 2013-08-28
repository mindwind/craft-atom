package org.craft.atom.protocol;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.craft.atom.util.ByteArrayBuffer;

/**
 * @author mindwind
 * @version 1.0, Feb 3, 2013
 */
@ToString(callSuper = true, of = { "defaultBufferSize", "maxSize", "splitIndex", "searchIndex", "stateIndex", "buf" })
abstract public class AbstractProtocolDecoder extends AbstractProtocolCodec {
	
	/** The separator index position according to specific protocol, indicates next byte nearby last complete protocol object. */
	@Getter @Setter protected int splitIndex = 0;
	
	/** The cursor index position for protocol process, indicates next byte would be process by protocol codec. */
	@Getter @Setter protected int searchIndex = 0;
	
	/** The index position for protocol state machine process */
	@Getter @Setter protected int stateIndex = 0;
	
	@Getter @Setter protected int defaultBufferSize = 2048;
	@Getter @Setter protected int maxSize = defaultBufferSize * 1024;
	@Getter @Setter protected ByteArrayBuffer buf = new ByteArrayBuffer(defaultBufferSize);
	
	// ~ ----------------------------------------------------------------------------------------------------------

	protected void reset() {
		if (splitIndex > 0 && splitIndex < buf.length()) {
			byte[] tailBytes = buf.array(splitIndex, buf.length());
			buf.clear();
			buf.append(tailBytes);
			splitIndex = 0;
			searchIndex = buf.length();
		}
		
		if (splitIndex > 0 && splitIndex == buf.length()) {
			buf.clear();
			splitIndex = searchIndex = 0;
		}
		
		if (buf.length() == 0 && buf.capacity() > maxSize * 2) {
			buf.reset(defaultBufferSize);
		}
	}
	
}
