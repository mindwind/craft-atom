package org.craft.atom.protocol.http.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an http chunk entity.
 * <br>
 * Chunked entity format as follows:
 * <pre>
 *  -----------------------------------------------------------------------------
 *       Chunked-Body   = *chunk
 *                        last-chunk
 *                        trailer
 *                        CRLF
 *       chunk          = chunk-size [ chunk-extension ] CRLF
 *                        chunk-data CRLF
 *       chunk-size     = 1*HEX
 *       last-chunk     = 1*("0") [ chunk-extension ] CRLF
 *       chunk-extension= *( ";" chunk-ext-name [ "=" chunk-ext-val ] )
 *       chunk-ext-name = token
 *       chunk-ext-val  = token | quoted-string
 *       chunk-data     = chunk-size(OCTET)
 *       trailer        = *(entity-header CRLF)
 *  -----------------------------------------------------------------------------
 *  
 *  The chunked data example as follows:
 *  -----------------------------------------------------------------------------
 *       Transfer-Encoding: chunked CRLF
 *       23;chunk_extension_name=chunk_extension_value CRLF
 *       This is the data in the first chunk CRLF
 *       1A CRLF
 *       and this is the second one CRLF
 *       0 CRLF
 * -----------------------------------------------------------------------------
 * </pre>
 * 
 * @author mindwind
 * @version 1.0, Feb 8, 2013
 */
public class HttpChunkEntity extends HttpEntity {

	private static final long serialVersionUID = -8469016024998851045L;
	
	private List<HttpChunk> chunks = new ArrayList<HttpChunk>();
	private Map<String, HttpHeader> trailers = new LinkedHashMap<String, HttpHeader>();
	
	public HttpChunkEntity() {
		super();
	}
	
	public HttpChunkEntity(String content) {
		super(content);
	}
	
	public HttpChunkEntity(List<HttpChunk> chunks) {
		this.chunks = chunks;
	}
	
	public HttpChunkEntity(List<HttpChunk> chunks, Map<String, HttpHeader> trailers) {
		this(chunks);
		this.trailers = trailers;
	}
	
	/**
	 * Add a chunk 
	 * 
	 * @param chunk
	 */
	public void addChunk(HttpChunk chunk) {
		chunks.add(chunk);
	}
	
	/**
	 * Add a new trailer header to chunk entity, if the exists replace it.
	 * 
	 * @param header
	 */
	public void addTrailer(HttpHeader trailer) {
		if (trailer == null || trailer.getName() == null) {
			throw new IllegalArgumentException("trailer or trailer name is null!");
		}
		
		trailers.put(trailer.getName(), trailer);
	}
	
	public String getContent() {
		if (content == null) {
			StringBuilder sb = new StringBuilder();
			for (HttpChunk chunk : chunks) {
				sb.append(chunk.getData());
			}
			this.content = sb.toString();
		}
		return this.content;
	}

	public List<HttpChunk> getChunks() {
		return chunks;
	}

	public void setChunks(List<HttpChunk> chunks) {
		this.chunks = chunks;
	}

	public Map<String, HttpHeader> getTrailers() {
		return trailers;
	}

	public void setTrailers(Map<String, HttpHeader> trailers) {
		this.trailers = trailers;
	}

	@Override
	public String toString() {
		return String.format("HttpChunkEntity [chunks=%s, trailers=%s, content=%s]", chunks, trailers, content);
	}
	
	public String toHttpString() {
		StringBuilder sb = new StringBuilder();
		for (HttpChunk chunk : getChunks()) {
			sb.append(chunk.toHttpString());
		}
		for (HttpHeader trailer : trailers.values()) {
			sb.append(trailer.toHttpString());
		}
		return sb.toString();
	}

}
