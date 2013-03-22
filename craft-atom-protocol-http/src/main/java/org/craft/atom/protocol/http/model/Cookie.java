package org.craft.atom.protocol.http.model;

import static org.craft.atom.protocol.http.HttpConstants.S_EQUAL_SIGN;
import static org.craft.atom.protocol.http.HttpConstants.S_SEMICOLON;
import static org.craft.atom.protocol.http.HttpConstants.S_SP;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
 * Set-Cookie: SID=31d4d96e407aad42; Domain=example.com; Path=/; HttpOnly; Secure; Expires=Wed, 09 Jun 2021 10:18:14 GMT
 * Cookie: SID=31d4d96e407aad42; lang=en-US
 * </pre>
 * 
 * More about cookie definition please reference <a href="http://tools.ietf.org/html/rfc6265">rfc6265</a>.
 * 
 * @author mindwind
 * @version 1.0, Mar 22, 2013
 */
public class Cookie implements Serializable {

	private static final long serialVersionUID = 5584804359930330729L;
	
	public static final String DOMAIN = "Domain";
	public static final String PATH = "Path";
	public static final String HTTP_ONLY = "HttpOnly";
	public static final String SECURE = "Secure";
	public static final String EXPIRES = "Expires";
	public static final String MAX_AGE = "Max-Age";
	
	// ~ ----------------------------------------------------------------------------------------------------------
	
	private String name; 
	private String value;
	
	private String domain;
	private String path;
	private Boolean httpOnly;
	private Boolean secure;
	private Date expires;
	private Integer maxAge; 
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
		this.httpOnly = httpOnly;
	}
	
	public static Cookie from(HttpHeaderValueElement element) {
		if (element == null) {
			throw new IllegalArgumentException();
		}
		
		Cookie cookie = new Cookie();
		cookie.setName(element.getName());
		cookie.setValue(element.getValue());
		Map<String, String> map = element.getParams();
		Set<Entry<String, String>> avs = map.entrySet();
		for (Entry<String, String> av : avs) {
			String k = av.getKey();
			String v = av.getValue();
			if (DOMAIN.equalsIgnoreCase(k)) {
				cookie.setDomain(v);
			} else if (PATH.equalsIgnoreCase(k)) {
				cookie.setPath(v);
			} else if (HTTP_ONLY.equalsIgnoreCase(k)) {
				cookie.setHttpOnly(true);
			} else if (SECURE.equalsIgnoreCase(k)) {
				cookie.setSecure(true);
			} else if (EXPIRES.equalsIgnoreCase(k)) {
				cookie.setExpires(HttpDates.parse(v));
			} else if (MAX_AGE.equalsIgnoreCase(k)) {
				cookie.setMaxAge(Integer.parseInt(v));
			} else {
				cookie.addExtensionAttribute(k, v);
			}
		}
		
		return cookie;
	}
	
	// ~ ----------------------------------------------------------------------------------------------------------

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Date getExpires() {
		return expires;
	}

	public void setExpires(Date expires) {
		this.expires = expires;
	}

	public Integer getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(Integer maxAge) {
		this.maxAge = maxAge;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Boolean isSecure() {
		return secure;
	}

	public void setSecure(Boolean secure) {
		this.secure = secure;
	}

	public Boolean isHttpOnly() {
		return httpOnly;
	}

	public void setHttpOnly(Boolean httpOnly) {
		this.httpOnly = httpOnly;
	}

	public Map<String, String> getExtensionAttributes() {
		return extension;
	}

	public void setExtensionAttributes(Map<String, String> attributes) {
		this.extension = attributes;
	}
	
	public void addExtensionAttribute(String name, String value) {
		this.extension.put(name, value);
	}
	
	public void removeExtensionAttribute(String name) {
		this.extension.remove(name);
	}

	@Override
	public String toString() {
		return String
				.format("Cookie [name=%s, value=%s, expires=%s, maxAge=%s, domain=%s, path=%s, secure=%s, httpOnly=%s, extension=%s]",
						name, value, expires, maxAge, domain, path, secure, httpOnly, extension);
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
