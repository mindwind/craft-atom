package org.craft.atom.protocol.http.model;

import static org.craft.atom.protocol.http.model.HttpConstants.S_CR;
import static org.craft.atom.protocol.http.model.HttpConstants.S_EQUAL_SIGN;
import static org.craft.atom.protocol.http.model.HttpConstants.S_LF;
import static org.craft.atom.protocol.http.model.HttpConstants.S_SEMICOLON;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Represent a http chunk data structure.
 * 
 * @author mindwind
 * @version 1.0, Feb 8, 2013
 */
public class HttpChunk implements Serializable {

	private static final long serialVersionUID = 8782130672644634878L;
	
	private int size;
	private Map<String, String> extension = new LinkedHashMap<String, String>();
	private String data;
	
	public HttpChunk() {
		super();
	}
	
	public HttpChunk(int size) {
		this.size = size;
	}

	public HttpChunk(int size, String data) {
		this(size);
		this.data = data;
	}

	public HttpChunk(int size, String data, Map<String, String> extension) {
		this(size, data);
		this.extension = extension;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Map<String, String> getExtension() {
		return extension;
	}

	public void setExtension(Map<String, String> extension) {
		this.extension = extension;
	}
	
	public void addExtension(String name, String value) {
		this.extension.put(name, value);
	}

	@Override
	public String toString() {
		return String.format("HttpChunk [size=%s, data=%s, extension=%s]", size, data, extension);
	}
	
	public String toHttpString() {
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
			sb.append(data).append(S_CR).append(S_LF);
		}
		return sb.toString();
	}

}
