package io.craft.atom.protocol.http.model;

import java.io.Serializable;
import java.nio.charset.Charset;

import lombok.Getter;
import lombok.ToString;

/**
 * Represents an http content type header value, it is useful for content codec.
 * <p>
 * Content type information consisting of a MIME type and an optional charset.
 * 
 * @author mindwind
 * @version 1.0, Mar 5, 2013
 */
@ToString(of = { "mimeType", "charset" })
public class HttpContentType implements Serializable {
	
	private static final long            serialVersionUID = -7286615457150709389L                                            ;
	public  static final HttpContentType DEFAULT          = new HttpContentType(MimeType.TEXT_HTML, Charset.forName("utf-8"));
		
	
	@Getter private final MimeType mimeType;
	@Getter private final Charset  charset ;
	
	
	// ~ -----------------------------------------------------------------------------------------------------------
	
	
	public HttpContentType(Charset charset) {
		this(null, charset);
	}
	
	public HttpContentType(MimeType mimeType) {
		this(mimeType, null);
	}

	public HttpContentType(MimeType mimeType, Charset charset) {
		this.mimeType = mimeType;
		this.charset = charset;
	}
	
	
	// ~ -----------------------------------------------------------------------------------------------------------
	
	
    public String toHttpString() {
        StringBuilder buf = new StringBuilder();
        buf.append(this.mimeType.toString());
        if (this.charset != null) {
            buf.append("; charset=");
            buf.append(this.charset.name());
        }
        return buf.toString();
    }
    
}
