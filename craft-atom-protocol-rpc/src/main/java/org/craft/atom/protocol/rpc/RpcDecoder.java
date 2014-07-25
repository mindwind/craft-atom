package org.craft.atom.protocol.rpc;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import org.craft.atom.protocol.AbstractProtocolDecoder;
import org.craft.atom.protocol.ProtocolDecoder;
import org.craft.atom.protocol.ProtocolException;
import org.craft.atom.protocol.rpc.model.RpcBody;
import org.craft.atom.protocol.rpc.model.RpcHeader;
import org.craft.atom.protocol.rpc.model.RpcMessage;
import org.craft.atom.protocol.rpc.spi.Serialization;
import org.craft.atom.util.ByteUtil;

/**
 * A {@link ProtocolDecoder} which decodes bytes into {@code RpcMessage} object.
 * <p>
 * Not thread safe.
 * 
 * @author mindwind
 * @version 1.0, Jul 24, 2014
 */
public class RpcDecoder extends AbstractProtocolDecoder implements ProtocolDecoder<RpcMessage> {
	
	
	private static final int START       = 0;
	private static final int MAGIC       = 11;
	private static final int HEADER_SIZE = 12;
	private static final int VERSION     = 13;
	private static final int BIT_FLAG    = 14;
	private static final int STATUS_CODE = 15;
	private static final int RESERVED    = 16;
	private static final int MESSAGE_ID  = 17;
	private static final int BODY_SIZE   = 18;
	private static final int BODY        = 20;
	private static final int END         = -1;
	
	
	        private SerializationRegistry registry = SerializationRegistry.getInstance();
	@Getter private RpcMessage            rm                                            ;
	@Getter private int                   state    = START                              ;
	
	
	// ~ --------------------------------------------------------------------------------------------------------------

	
	public RpcDecoder() {}
		
		
	// ~ --------------------------------------------------------------------------------------------------------------

	
	@Override
	public List<RpcMessage> decode(byte[] bytes) throws ProtocolException {
		List<RpcMessage> msgs = new ArrayList<RpcMessage>();
		adapt();
		buf.append(bytes);
		
		while (searchIndex < buf.length() || state == END) {
			switch (state) {
			case START      : state4START()      ; break;
			case MAGIC      : state4MAGIC()      ; break;
			case HEADER_SIZE: state4HEADER_SIZE(); break;
			case VERSION    : state4VERSION()    ; break;
			case BIT_FLAG   : state4BIT_FLAG()   ; break;
			case STATUS_CODE: state4STATUS_CODE(); break;
			case RESERVED   : state4RESERVED()   ; break;
			case MESSAGE_ID : state4MESSAGE_ID() ; break;
			case BODY_SIZE  : state4BODY_SIZE()  ; break;
			case BODY       : state4BODY()       ; break;
			case END        : state4END(msgs)    ; break;
			default         : throw new IllegalStateException("Invalid decoder state!");
			}
		}
		
		return msgs;
	}
	
	private void state4END(List<RpcMessage> msgs) {
		msgs.add(rm);
		splitIndex = searchIndex;
		rm = null;
		state = START;
	}
	
	private void state4BODY() {
		// need more bytes
		int hs = rm.getHeader().getHeaderSize();
		int bs = rm.getHeader().getBodySize();
		if (buf.length() <  hs + bs) { searchIndex = buf.length(); return; }
		
		Serialization<RpcBody> deserializer = registry.lookup(rm.getHeader().getSt());
		if (deserializer == null) throw new ProtocolException("No mapping `deserializer`!");
		RpcBody rb = deserializer.deserialize(buf.buffer(), 20 + splitIndex);
		rm.setBody(rb);
		searchIndex = hs + bs + splitIndex;
		state = END;
	}
	
	private void state4BODY_SIZE() {
		// need more bytes
		if (buf.length() < 20 + splitIndex) { searchIndex = buf.length(); return; }
		
		int bs = ByteUtil.bytes2int(buf.buffer(), 16 + splitIndex);
		rm.getHeader().setBodySize(bs);
		state = BODY;
		searchIndex = 20 + splitIndex;
	}
	
	private void state4MESSAGE_ID() {
		// need more bytes
		if (buf.length() < 16 + splitIndex) { searchIndex = buf.length(); return; }
		
		long id = ByteUtil.bytes2long(buf.buffer(), 8 + splitIndex);
		rm.getHeader().setId(id);
		state = BODY_SIZE;
		searchIndex = 16 + splitIndex;
	}
	
	private void state4RESERVED() {
		// need more bytes
		if (buf.length() < 8 + splitIndex) { searchIndex = buf.length(); return; }
		
		rm.getHeader().setReserved(buf.byteAt(7 + splitIndex));
		state = MESSAGE_ID;
		searchIndex = 8 + splitIndex;
	}
	
	private void state4STATUS_CODE() {
		// need more bytes
		if (buf.length() < 7 + splitIndex) { searchIndex = buf.length(); return; }
		
		rm.getHeader().setStatusCode(buf.byteAt(6 + splitIndex));
		state = RESERVED;
		searchIndex = 7 + splitIndex;
	}
	
	private void state4BIT_FLAG() {
		// need more bytes
		if (buf.length() < 6 + splitIndex) { searchIndex = buf.length(); return; }
		
		rm.getHeader().setSt(buf.byteAt(5 + splitIndex));
		rm.getHeader().setHb(buf.byteAt(5 + splitIndex));
		rm.getHeader().setOw(buf.byteAt(5 + splitIndex));
		rm.getHeader().setRp(buf.byteAt(5 + splitIndex));
		state = STATUS_CODE;
		searchIndex = 6 + splitIndex;
	}
	
	private void state4VERSION() {
		// need more bytes
		if (buf.length() < 5 + splitIndex) { searchIndex = buf.length(); return; }
		
		rm.getHeader().setVersion(buf.byteAt(4 + splitIndex));
		state = BIT_FLAG;
		searchIndex = 5 + splitIndex;
	}
	
	private void state4HEADER_SIZE() {
		// need more bytes
		if (buf.length() < 4 + splitIndex) { searchIndex = buf.length(); return; }
		
		short hs = ByteUtil.bytes2short(buf.buffer(), 2 + splitIndex);
		rm.getHeader().setHeaderSize(hs);
		state = VERSION;
		searchIndex = 4  + splitIndex;
	}
	
	private void state4MAGIC() {
		// need more bytes
		if (buf.length() < 2 + splitIndex) { searchIndex = buf.length(); return; }
		
		if (RpcHeader.MAGIC_0 == buf.byteAt(0 + splitIndex) && RpcHeader.MAGIC_1 == buf.byteAt(1 + splitIndex)) {
			RpcHeader rh = new RpcHeader();
			rm = new RpcMessage();
			rm.setHeader(rh);
			state = HEADER_SIZE;
			searchIndex = 2 + splitIndex;
		} else {
			throw new ProtocolException("Invalid bytes format!");
		}
	}
	
	private void state4START() {
		if (buf.length() > 0 + splitIndex) {
			state = MAGIC;
		}
	}

}
