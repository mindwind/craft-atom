package org.craft.atom.protocol.http.model;

import static org.craft.atom.protocol.http.HttpConstants.S_CR;
import static org.craft.atom.protocol.http.HttpConstants.S_EQUAL_SIGN;
import static org.craft.atom.protocol.http.HttpConstants.S_LF;
import static org.craft.atom.protocol.http.HttpConstants.S_SEMICOLON;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Represent a http chunk data structure.
 * 
 * @author mindwind
 * @version 1.0, Feb 8, 2013
 */
@ToString(of = { "size", "extension", "data" })
public class HttpChunk implements Serializable {

	
	private static final long serialVersionUID = 8782130672644634878L;
	
	
	@Getter @Setter private int                 size                                           ;
	@Getter @Setter private Map<String, String> extension = new LinkedHashMap<String, String>();
	@Getter @Setter private byte[]              data                                           ;
	
	
	// ~ --------------------------------------------------------------------------------------------------------
	
	
	public HttpChunk() {
		super();
	}
	
	public HttpChunk(int size) {
		this.size = size;
	}

	public HttpChunk(int size, byte[] data) {
		this(size);
		this.data = data;
	}
	
	public HttpChunk(int size, String data, Charset charset) {
		this(size);
		this.data = data.getBytes(charset);
	}

	public HttpChunk(int size, byte[] data, Map<String, String> extension) {
		this(size, data);
		this.extension = extension;
	}
	
	public HttpChunk(int size, String data, Charset charset, Map<String, String> extension) {
		this(size, data, charset);
		this.extension = extension;
	}
	
	
	// ~ --------------------------------------------------------------------------------------------------------
	
	
	public String getDataString(Charset charset) {
		return new String(data, charset);
	}
	
	public void addExtension(String name, String value) {
		this.extension.put(name, value);
	}

	public String toHttpString(Charset charset) {
		StringBuilder sb = new StringBuilder();
		sb.append(Integer.toHexString(size));
		Set<Entry<String, String>> extSet = extension.entrySet();
		for (Entry<String, String> entry : extSet) {
			sb.append(S_SEMICOLON);
			String extName = entry.getKey();
			String extValue = entry.getValue();
			sb.append(extName);
			if (extValue != null) {
				sb.append(S_EQUAL_SIGN).append(extValue);
			}
		}
		sb.append(S_CR).append(S_LF);
		if (data != null) {
			sb.append(new String(data, charset)).append(S_CR).append(S_LF);
		}
		return sb.toString();
	}

}
