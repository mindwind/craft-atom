package org.craft.atom.protocol.http.model;

import static org.craft.atom.protocol.http.HttpConstants.S_EQUAL_SIGN;
import static org.craft.atom.protocol.http.HttpConstants.S_SEMICOLON;
import static org.craft.atom.protocol.http.HttpConstants.S_SP;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.craft.atom.protocol.http.HttpDates;

/**
 * Represents a token or short packet of state information. <br>
 * These header fields can be used by HTTP servers to store state (called cookies) at HTTP user agents, 
 * letting the servers maintain a stateful session over the mostly stateless HTTP protocol.
 * <p>
 * Cookie syntax:
 * <pre>
 * set-cookie-header = "Set-Cookie:" SP set-cookie-string
 * set-cookie-string = cookie-pair *( ";" SP cookie-av )
 * cookie-pair       = cookie-name "=" cookie-value
 * cookie-name       = token
 * cookie-value      = *cookie-octet / ( DQUOTE *cookie-octet DQUOTE )
 * cookie-octet      = %x21 / %x23-2B / %x2D-3A / %x3C-5B / %x5D-7E
 *                     ; US-ASCII characters excluding CTLs,
 *                     ; whitespace DQUOTE, comma, semicolon,
 *                     ; and backslash
 * token             = <token, defined in [RFC2616], Section 2.2>
 *
 * cookie-av         = expires-av / max-age-av / domain-av /
 *                     path-av / secure-av / httponly-av /
 *                     extension-av
 * expires-av        = "Expires=" sane-cookie-date
 * sane-cookie-date  = <rfc1123-date, defined in [RFC2616], Section 3.3.1>
 * max-age-av        = "Max-Age=" non-zero-digit *DIGIT
 *                     ; In practice, both expires-av and max-age-av
 *                     ; are limited to dates representable by the
 *                     ; user agent.
 * non-zero-digit    = %x31-39
 *                     ; digits 1 through 9
 * domain-av         = "Domain=" domain-value
 * domain-value      = <subdomain>
 *                     ; defined in [RFC1034], Section 3.5, as
 *                     ; enhanced by [RFC1123], Section 2.1
 * path-av           = "Path=" path-value
 * path-value        = <any CHAR except CTLs or ";">
 * secure-av         = "Secure"
 * httponly-av       = "HttpOnly"
 * extension-av      = <any CHAR except CTLs or ";">
 * 
 * 
 * cookie-header = "Cookie:" OWS cookie-string OWS
 * cookie-string = cookie-pair *( ";" SP cookie-pair )
 * </pre>
 * 
 * For examples:
 * <pre>
 * Set-Cookie: SID=31d4d96e407aad42; Domain=example.com; Path=/; HttpOnly; Secure; Expires=Wed, 09 Jun 2021 10:18:14 GMT; Max-Age=86400
 * Cookie: SID=31d4d96e407aad42; lang=en-US
 * </pre>
 * 
 * More about cookie definition please reference <a href="http://tools.ietf.org/html/rfc6265">rfc6265</a>.
 * 
 * @author mindwind
 * @version 1.0, Mar 22, 2013
 */
@ToString(of = { "name", "value", "domain", "path", "httpOnly", "secure", "expires", "maxAge", "extension" })
public class Cookie implements Serializable {

	private static final long serialVersionUID = 5584804359930330729L;
	
	public static final String DOMAIN = "Domain";
	public static final String PATH = "Path";
	public static final String HTTP_ONLY = "HttpOnly";
	public static final String SECURE = "Secure";
	public static final String EXPIRES = "Expires";
	public static final String MAX_AGE = "Max-Age";
	
	// ~ ----------------------------------------------------------------------------------------------------------
	
	@Getter @Setter private String name; 
	@Getter @Setter private String value;
	@Getter @Setter private String domain;
	@Getter @Setter private String path;
	@Setter private Boolean httpOnly;
	@Setter private Boolean secure;
	@Getter @Setter private Date expires;
	@Getter @Setter private Integer maxAge; 
	private Map<String, String> extension = new LinkedHashMap<String, String>();
	
	// ~ ----------------------------------------------------------------------------------------------------------
	
	public Cookie() {
		super();
	}

	public Cookie(String name, String value) {
		if (name == null) {
            throw new IllegalArgumentException("Name should not be null");
        }
		this.name = name;
		this.value = value;
	}

	public Cookie(String name, String value, String domain) {
		this(name, value);
		this.domain = domain;
	}

	public Cookie(String name, String value, String domain, String path) {
		this(name, value, domain);
		this.path = path;
	}

	public Cookie(String name, String value, String domain, String path, boolean httpOnly) {
		this(name, value, domain, path);
	}
	
	public Cookie(String name, String value, String domain, String path, boolean httpOnly, int maxAge) {
		this(name, value, domain, path, httpOnly);
		this.maxAge = maxAge;
	}
	
	// ~ ----------------------------------------------------------------------------------------------------------


	public Boolean isSecure() {
		return secure;
	}

	public Boolean isHttpOnly() {
		return httpOnly;
	}

	public Map<String, String> getExtensionAttributes() {
		return Collections.unmodifiableMap(extension);
	}
	
	public void addExtensionAttribute(String name, String value) {
		this.extension.put(name, value);
	}
	
	public void removeExtensionAttribute(String name) {
		this.extension.remove(name);
	}
	
	public String getExtensionAttribute(String name) {
		return this.extension.get(name);
	}
	
	public String toHttpString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName()).append(S_EQUAL_SIGN).append(getValue());
		
		if (domain != null) {
			sb.append(S_SEMICOLON).append(S_SP).append(DOMAIN).append(S_EQUAL_SIGN).append(getDomain());
		}
		if (path != null) {
			sb.append(S_SEMICOLON).append(S_SP).append(PATH).append(S_EQUAL_SIGN).append(getPath());
		}
		if (httpOnly != null) {
			sb.append(S_SEMICOLON).append(S_SP).append(HTTP_ONLY);
		}
		if (secure != null) {
			sb.append(S_SEMICOLON).append(S_SP).append(SECURE);
		}
		if (expires != null) {
			sb.append(S_SEMICOLON).append(S_SP).append(EXPIRES).append(S_EQUAL_SIGN).append(HttpDates.format(getExpires()));
		}
		if (maxAge != null) {
			sb.append(S_SEMICOLON).append(S_SP).append(MAX_AGE).append(S_EQUAL_SIGN).append(getMaxAge());
		}
		Set<Entry<String, String>> entrys = extension.entrySet();
		for (Entry<String, String> entry : entrys) {
			String k = entry.getKey();
			String v = entry.getValue();
			sb.append(S_SEMICOLON).append(S_SP).append(k);
			if ( v != null) {
				sb.append(S_EQUAL_SIGN).append(v);
			}
		}
		
		return sb.toString();
	}
	
}
