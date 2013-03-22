package org.craft.atom.protocol.http.model;

import static org.craft.atom.protocol.http.HttpConstants.S_CR;
import static org.craft.atom.protocol.http.HttpConstants.S_EQUAL_SIGN;
import static org.craft.atom.protocol.http.HttpConstants.S_LF;
import static org.craft.atom.protocol.http.HttpConstants.S_SEMICOLON;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Arrays;
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
	private byte[] data;
	
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

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public byte[] getData() {
		return data;
	}
	
	public String getDataString(Charset charset) {
		return new String(data, charset);
	}

	public void setData(byte[] data) {
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
		return String.format("HttpChunk [size=%s, data=%s, extension=%s]", size, Arrays.toString(data), extension);
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
