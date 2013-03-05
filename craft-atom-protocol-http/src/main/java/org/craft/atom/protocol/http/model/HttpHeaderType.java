package org.craft.atom.protocol.http.model;

/**
 * HTTP header type enumeration.
 * 
 * @author mindwind
 * @version 1.0, Feb 6, 2013
 */
public enum HttpHeaderType {

	// ~ --------------------------------------------------------------------------------------- general headers
	
	
	/**
	 * HTTP/1.0 - RCF1945 -> HTTP/1.1 - RFC2616
	 * <p>
	 * The Authorization header is sent by a client to authenticate itself with
	 * a server. A client will include this header in its request after
	 * receiving a 401 Authentication Required response from a server. The value
	 * of this header depends on the authentication scheme in use
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Authorization: Basic YnJpYW4tdG90dHk6T3ch
	 * </pre>
	 */
	AUTHORIZATION("Authorization"),
	
	/**
	 * HTTP/1.1 - RFC2616
	 * <p>
	 * The Cache-Control header is used to pass information about how an object
	 * can be cached. This header is one of the more complex headers introduced
	 * in HTTP/1.1. Its value is a caching directive, giving caches special
	 * instructions about an object's cacheability.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Cache-Control: no-cache
	 * Cache-Control: max-age=484200
	 * </pre>
	 */
	CACHE_CONTROL("Cache-Control"),
	
	/**
	 * HTTP/1.1 - RFC2616
	 * <p>
	 * The Connection header is a somewhat overloaded header that can lead to a
	 * bit of confusion. This header was used in HTTP/1.0 clients that were
	 * extended with keep-alive connections for control information.In HTTP/1.1,
	 * the older semantics are mostly recognized, but the header has taken on a
	 * new function. In HTTP/1.1, the Connection header's value is a list of
	 * tokens that correspond to header names. Applications receiving an
	 * HTTP/1.1 message with a Connection header are supposed to parse the list
	 * and remove any of the headers in the message that are in the Connection
	 * header list. This is mainly for proxies, allowing a server or other proxy
	 * to specify hop-by-hop headers that should not be passed along. One
	 * special token value is "close". This token means that the connection is
	 * going to be closed after the response is completed. HTTP/1.1 applications
	 * that do not support persistent connections need to insert the Connection
	 * header with the "close" token in all requests and responses.
	 * <p>
	 * While RFC 2616 does not specifically mention keep-alive as a connection
	 * token, some browsers (including those sending HTTP/1.1 as their versions)
	 * use it in making requests.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Connection: Keep-Alive
	 * Connection: close
	 * </pre>
	 */
	CONNECTION("Connection"),
	
	/**
	 * HTTP/1.0 - RCF1945 -> HTTP/1.1 - RFC2616
	 * <p>
	 * The Date header gives the date and time at which the message was created.
	 * This header is required in servers' responses because the time and date
	 * at which the server believes the message was created can be used by
	 * caches in evaluating the freshness of a response. For clients, this
	 * header is completely optional, although it's good form to include it.<br>
	 * HTTP has a few specific date formats. This one is defined in RFC 822 and
	 * is the preferred format for HTTP/1.1 messages. However, in earlier
	 * specifications of HTTP, the date format was not spelled out as well, so
	 * server and client implementors have used other formats, which need to be
	 * supported for the sake of legacy. You will run into date formats like the
	 * one specified in RFC 850, as well as dates in the format produced by the
	 * asctime( ) system call. Here they are for the date represented above:
	 * 
	 * <pre>
	 * Date: Tuesday, 03-Oct-97 02:15:31 GMT  - RFC 850 format 
	 * Date: Tue Oct 3 02:15:31 1997  - asctime() format
	 * </pre>
	 * 
	 * The asctime( ) format is looked down on because it is in local time and
	 * it does not specify its time zone (e.g., GMT). In general, the date
	 * header should be in GMT; however, robust applications should handle dates
	 * that either do not specify the time zone or include Date values in
	 * non-GMT time.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Date: Tue, 3 Oct 1997 02:15:31 GMT
	 * </pre>
	 */
	DATE("Date"),
	
	/**
	 * HTTP/1.1 - RFC2616
	 * <p>
	 * The Trailer header is used to indicate which headers are present in the
	 * trailer of a message.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Trailer: Content-MD5
	 * </pre>
	 */
	TRAILER("Trailer"),

	/**
	 * HTTP/1.1 - RFC2616
	 * <p>
	 * If some encoding had to be performed to transfer the HTTP message body
	 * safely, the message will contain the Transfer-Encoding header. Its value
	 * is a list of the encodings that were performed on the message body. If
	 * multiple encodings were performed, they are listed in order.<br>
	 * The Transfer-Encoding header differs from the Content-Encoding header
	 * because the transfer encoding is an encoding that was performed by a
	 * server or other intermediary application to transfer the message.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Transfer-Encoding: chunked
	 * </pre>
	 */
	TRANSFER_ENCODING("Transfer-Encoding"),
	
	/**
	 * HTTP/1.1 - RFC2616
	 * <p>
	 * The Upgrade header provides the sender of a message with a means of
	 * broadcasting the desire to use another, perhaps completely different,
	 * protocol. For instance, an HTTP/1.1 client could send an HTTP/1.0 request
	 * to a server and include an Upgrade header with the value "HTTP/1.1",
	 * allowing the client to test the waters and see whether the server speaks
	 * HTTP/1.1.<br>
	 * If the server is capable, it can send an appropriate response letting the
	 * client know that it is okay to use the new protocol. This provides an
	 * efficient way to move to other protocols. Most servers currently are only
	 * HTTP/1.0-compliant, and this strategy allows a client to avoid confusing
	 * a server with too many HTTP/1.1 headers until it determines whether the
	 * server is indeed capable of speaking HTTP/1.1.<br>
	 * When a server sends a 101 Switching Protocols response, it must include
	 * this header.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Upgrade: HTTP/1.1
	 * </pre>
	 */
	UPGRADE("Upgrade"),
	
	/**
	 * HTTP/1.1 - RFC2616
	 * <p>
	 * The Via header is used to trace messages as they pass through proxies and
	 * gateways. It is an informational header that can be used to see what
	 * applications are handling requests and responses.<br>
	 * When a message passes through an HTTP application on its way to a client
	 * or a server, that application can use the Via header to tag the message
	 * as having gone via it. This is an HTTP/1.1 header; many older
	 * applications insert a Via-like string in the User-Agent or Server headers
	 * of requests and responses.<br>
	 * If the message passes through multiple in-between applications, each one
	 * should tack on its Via string. The Via header must be inserted by
	 * HTTP/1.1 proxies and gateways.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Via: 1.1 joes-hardware.com ( Joes-Server/1.0)
	 * </pre>
	 * 
	 * The above says that the message passed through the Joes Server Version
	 * 1.0 software running on the machine joes-hardware.com. Joe's Server was
	 * speaking HTTP 1.1. The Via header should be formatted like this:
	 * 
	 * <pre>
	 * HTTP-Version machine-hostname (Application-Name-Version)
	 * </pre>
	 */
	VIA("Via"),
	

	// ~ --------------------------------------------------------------------------------------- request headers
	

	/**
	 * HTTP/1.1 - RFC2616
	 * <p>
	 * The Accept header is used by clients to let servers know what media type
	 * acceptable. The value of the Accept header field is a list of media types
	 * that the client can use. For instance, your web browser cannot display
	 * every type of multimedia object on the Web. By including an Accept header
	 * in your requests, your browser can save you from downloading a video or
	 * other type of object that you can't use. The Accept header field also may
	 * include a list of quality values (q values) that tell the server which
	 * media type is preferred, in case the server has multiple versions of the
	 * media type.
	 * <p>
	 * "*" is a special value that is used to wildcard media types. For example,
	 * "* / *" represents all types, and "image/*" represents all image types.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Accept: text/*, image/*
	 * Accept: text/*, image/gif, image/jpeg;q=1
	 * </pre>
	 */
	ACCEPT("Accept"),
	
	/**
	 * HTTP/1.1 - RFC2616
	 * <p>
	 * The Accept-Charset header is used by clients to tell servers what
	 * character sets are acceptable or preferred. The value of this request
	 * header is a list of character sets and possibly quality values for the
	 * listed character sets. The quality values let the server know which
	 * character set is preferred, in case the server has the document in
	 * multiple acceptable character sets.
	 * <p>
	 * "*" is a special character. If present, it represents all character sets,
	 * except those that also are mentioned explicitly in the value. If it's not
	 * present, any charset not in the value field has a default q value of
	 * zero, with the exception of the iso-latin-1 charset, which gets a default
	 * of 1.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Accept-Charset: iso-latin-1
	 * </pre>
	 */
	ACCEPT_CHARSET("Accept-Charset"),
	
	/**
	 * HTTP/1.1 - RFC2616
	 * <p>
	 * The Accept-Encoding header is used by clients to tell servers what
	 * encodings are acceptable. If the content the server is holding is encoded
	 * (perhaps compressed), this request header lets the server know whether
	 * the client will accept it.
	 * <p>
	 * The empty Accept-Encoding example is not a typo. It refers to the
	 * identity encoding— that is, the unencoded content. If the Accept-Encoding
	 * header is present and empty, only the unencoded content is acceptable.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Accept-Encoding: gzip
	 * Accept-Encoding: compress;q=0.5, gzip;q=1
	 * </pre>
	 */
	ACCEPT_ENCODING("Accept-Encoding"),
	
	/**
	 * HTTP/1.1 - RFC2616
	 * <p>
	 * The Accept-Language request header functions like the other Accept
	 * headers, allowing clients to inform the server about what languages
	 * (e.g., the natural language for content) are acceptable or preferred.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Accept-Language: en
	 * Accept-Language: zh-CN;q=1, en-gb;q=0.5
	 * </pre>
	 */
	ACCEPT_LANGUAGE("Accept-Language"),
	
	/**
	 * HTTP/1.1 - Original Netscape cookie standard which is widely used, extension request header.
	 * <p>
	 * The Cookie header is an extension header used for client identification
	 * and tracking.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Cookie: ink=IUOK164y59BC708378908CFF89OE5573998A115
	 * </pre>
	 */
	COOKIE("Cookie"),
	
	/**
	 * HTTP/1.1 - RFC2616
	 * <p>
	 * The Expect header is used by clients to let servers know that they expect
	 * certain behavior. This header currently is closely tied to the response
	 * code 100 Continue<br>
	 * If a server does not understand the Expect header's value, it should
	 * respond with a status code of 417 Expectation Failed.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Expect: 100-continue
	 * </pre>
	 */
	EXPECT("Expect"),
	
	/**
	 * HTTP/1.1 - RFC2616
	 * <p>
	 * The Host header is used by clients to provide the server with the
	 * Internet hostname and port number of the machine from which the client
	 * wants to make a request. The hostname and port are those from the URL the
	 * client was requesting. <br>
	 * The Host header allows servers to differentiate different relative URLs
	 * based on the hostname, giving the server the ability to host several
	 * different hostnames on the same machine (i.e., the same IP address).
	 * <p>
	 * HTTP/1.1 clients must include a Host header in all requests. All HTTP/1.1
	 * servers must respond with the 400 Bad Request status code to HTTP/1.1
	 * clients that do not provide a Host header.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Host: www.hotbot.com:80
	 * </pre>
	 */
	HOST("Host"),
	
	/**
	 * HTTP/1.0 - RFC1945 -> HTTP/1.1 - RFC2616
	 * <p>
	 * The If-Modified-Since request header is used to make conditional
	 * requests. A client can use the GET method to request a resource from a
	 * server, having the response hinge on whether the resource has been
	 * modified since the client last requested it.<br>
	 * If the object has not been modified, the server will respond with a 304
	 * Not Modified response, instead of with the resource. If the object has
	 * been modified, the server will respond as if it was a non-conditional GET
	 * request.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * If-Modified-Since: Thu, 03 Oct 1997 17:15:00 GMT
	 * </pre>
	 */
	IF_MODIFIED_SINCE("If-Modified-Since"),
	
	/**
	 * HTTP/1.1 - RFC2616
	 * <p>
	 * Like the If-Modified-Since header, the If-Match header can be used to
	 * make a request conditional. Instead of a date, the If-Match request uses
	 * an entity tag. The server compares the entity tag in the If-Match header
	 * with the current entity tag of the resource and returns the object if the
	 * tags match.<br>
	 * The server should use the If-Match value of "*" to match any entity tag
	 * it has for a resource; "*" will always match, unless the server no longer
	 * has the resource.<br>
	 * This header is useful for updating resources that a client or cache
	 * already has. The resource is returned only if it has changed— that is, if
	 * the previously requested object's entity tag does not match the entity
	 * tag of the current version on the server.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * If-Match: "11e92a-457b-31345aa"
	 * </pre>
	 */
	IF_MATCH("If-Match"),
	
	/**
	 * HTTP/1.1 - RFC2616
	 * <p>
	 * The If-None-Match header, like all the If headers, can be used to make a
	 * request conditional. The client supplies the server with a list of entity
	 * tags, and the server compares those tags against the entity tags it has
	 * for the resource, returning the resource only if none match.<br>
	 * This allows a cache to update resources only if they have changed. Using
	 * the If-None-Match header, a cache can use a single request to both
	 * invalidate the entities it has and receive the new entity in the
	 * response.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * If-None-Match: "11e92a-457b-31345aa"
	 * </pre>
	 */
	IF_NONE_MATCH("If-None-Match"),
	
	/**
	 * HTTP/1.1 - RFC2616
	 * <p>
	 * The If-Range header, like all the If headers, can be used to make a
	 * request conditional. It is used when an application has a copy of a range
	 * of a resource, to revalidate the range or get the complete resource if
	 * the range is no longer valid.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * If-Range: Tue, 3 Oct 1997 02:15:31 GMT 
	 * If-Range: "11e92a-457b-3134b5aa"
	 * </pre>
	 */
	IF_RANGE("If-Range"),
	
	/**
	 * HTTP/1.1 - RFC2616
	 * <p>
	 * The If-Unmodified-Since header is the twin of the If-Modified-Since
	 * header. Including it in a request makes the request conditional. The
	 * server should look at the date value of the header and return the object
	 * only if it has not been modified since the date provided.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * If-Unmodified-Since: Thu, 03 Oct 1997 17:15:00 GMT
	 * </pre>
	 */
	IF_UNMODIFIED_SINCE("If-Unmodified-Since"),
	
	/**
	 * HTTP/1.1 - RFC2616
	 * <p>
	 * This header is used only with the TRACE method, to limit the number of
	 * proxies or other intermediaries that a request goes through. Its value is
	 * an integer. Each application that receives a TRACE request with this
	 * header should decrement the value before it forwards the request along.
	 * <br>
	 * If the value is zero when the application receives the request, it should
	 * send back a 200 OK response to the request, with an entity body
	 * containing the original request. If the Max-Forwards header is missing
	 * from a TRACE request, assume that there is no maximum number of forwards.
	 * <br>
	 * For other HTTP methods, this header should be ignored.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Max-Forwards: 5
	 * </pre>
	 */
	MAX_FORWARDS("Max-Forwards"),
	
	/**
	 * HTTP/1.0 - RFC1945 -> HTTP/1.1 - RFC2616
	 * <p>
	 * The Pragma header is used to pass directions along with the message.
	 * These directions could be almost anything, but often they are used to
	 * control caching behavior. Proxies and gateways must not remove the Pragma
	 * header, because it could be intended for all applications that receive
	 * the message.<br>
	 * The most common form of Pragma, Pragma: no-cache, is a request header
	 * that forces caches to request or revalidate the document from the origin
	 * server even when a fresh copy is available in the cache. It is sent by
	 * browsers when users click on the Reload/Refresh button. Many servers send
	 * Pragma: no-cache as a response header (as an equivalent to Cache-Control:
	 * no-cache), but despite its common use, this behavior is technically
	 * undefinded. Not all applications support Pragma response headers.
	 * <p>
	 * The only specification-defined Pragma directive is "no-cache"; however,
	 * you may run into other Pragma headers that have been defined as
	 * extensions to the specification.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Pragma: no-cache
	 * </pre>
	 */
	PRAGMA("Pragma"),
	
	/**
	 * HTTP/1.1 - RFC2616
	 * <p>
	 * The Proxy-Authorization header functions like the Authorization header.
	 * It is used by client applications to respond to Proxy-Authenticate
	 * challenges.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Proxy-Authorization: Basic YnJpYW4tdG90dHk6T3ch
	 * </pre>
	 */
	PROXY_AUTHORIZATION("Proxy-Authorization"),
	
	/**
	 * HTTP/1.0 - RFC1945 -> HTTP/1.1 - RFC2616
	 * <p>
	 * The Referer header is inserted into client requests to let the server
	 * know where the client got the URL from. This is a voluntary effort, for
	 * the server's benefit; it allows the server to better log the requests or
	 * perform other tasks. The misspelling of "Referer" hearkens back to the
	 * early days of HTTP, to the frustration of English-speaking copyeditors
	 * throughout the world.<br>
	 * What your browser does is fairly simple. If you get home page A and click
	 * on a link to go to home page B, your browser will insert a Referer header
	 * in the request with value A. Referer headers are inserted by your browser
	 * only when you click on links; requests for URLs you type in yourself will
	 * not contain a Referer header.<br>
	 * Because some pages are private, there are some privacy concerns with this
	 * header. While some of this is unwarranted paranoia, this header does
	 * allow web servers and their administrators to see where you came from,
	 * potentially allowing them to better track your surfing. As a result, the
	 * HTTP/1.1 specification recommends that application writers allow the user
	 * to decide whether this header is transmitted.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Referer: http://www.inktomi.com/index.html
	 * </pre>
	 */
	REFERER("Referer"),
	
	/**
	 * HTTP/1.1 - RFC2616
	 * <p>
	 * The poorly named TE header functions like the Accept-Encoding header, but
	 * for transfer encodings (it could have been named
	 * Accept-Transfer-Encoding, but it wasn't). The TE header also can be used
	 * to indicate whether a client can handle headers in the trailer of a
	 * response that has been through the chunked encoding. <br>
	 * If the value is empty, only the chunked transfer encoding is acceptable.
	 * The special token "trailers" indicates that trailer headers are
	 * acceptable in a chunked response.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * TE:
	 * TE: chunked
	 * TE: trailers
	 * </pre>
	 */
	TE("TE"),
	
	/**
	 * HTTP/1.0 - RFC1945 -> HTTP/1.1 - RFC2616
	 * <p>
	 * The User-Agent header is used by client applications to identify
	 * themselves, much like the Server header for servers. Its value is the
	 * product name and possibly a comment describing the client application.<br>
	 * This header's format is somewhat free-form. Its value varies from client
	 * product to product and release to release. This header sometimes even
	 * contains information about the machine on which the client is running.<br>
	 * As with the Server header, don't be surprised if older proxy or gateway
	 * applications insert what amounts to a Via header in the User-Agent header
	 * itself.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * User-Agent: Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0)
	 * </pre>
	 */
	USER_AGENT("User-Agent"),

	/**
	 * Extension forwarded for
	 * <p>
	 * This header is used by many proxy servers (e.g., Squid) to note whom a
	 * request has been forwarded for. This request header notes the address
	 * from which the request originates.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * X-Forwarded-For: 64.95.76.161
	 * </pre>
	 */
	X_FORWARDED_FOR("X-Forwarded-For"),

	// ~ --------------------------------------------------------------------------------------- response headers
	
	
	/**
	 * HTTP/1.1 - RFC2616
	 * <p>
	 * The Accept-Ranges header differs from the other Accept headers— it is a
	 * response header used by servers to tell clients whether they accept
	 * requests for ranges of a resource. The value of this header tells what
	 * type of ranges, if any, the server accepts for a given resource. A client
	 * can attempt to make a range request on a resource without having received
	 * this header. If the server does not support range requests for that
	 * resource, it can respond with an appropriate status code and the
	 * Accept-Ranges value "none". Servers might want to send the "none" value
	 * for normal requests to discourage clients from making range requests in
	 * the future.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Accept-Ranges: none
	 * Accept-Ranges: bytes
	 * </pre>
	 */
	ACCEPT_RANGES("Accept-Ranges"),
	
	/**
	 * HTTP/1.1 - RFC2616
	 * <p>
	 * The Age header tells the receiver how old a response is. It is the
	 * sender's best guess as to how long ago the response was generated by or
	 * revalidated with the origin server. The value of the header is the
	 * sender's guess, a delta in seconds. HTTP/1.1 caches must include an Age
	 * header in every response they send.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Age: 60
	 * </pre>
	 */
	AGE("Age"),
	
	/**
	 * HTTP/1.0 - RCF1945 -> HTTP/1.1 - RFC2616
	 * <p>
	 * The Allow header is used to inform clients what HTTP methods are
	 * supported on a particular resource. An HTTP/1.1 server sending a 405
	 * Method Not Allowed response must include an Allow header.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Allow: GET, HEAD
	 * </pre>
	 */
	ALLOW("Allow"),
	
	/**
	 * HTTP/1.0 - RFC1945 -> HTTP/1.1 - RFC2616
	 * <p>
	 * The Location header is used by servers to direct clients to the location
	 * of a resource that either was moved since the client last requested it or
	 * was created in response to the request.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Location: http://www.hotbot.com
	 * </pre>
	 */
	LOCATION("Location"),
	
	/**
	 * HTTP/1.1 - RFC2616
	 * <p>
	 * The Proxy-Authenticate header functions like the WWW-Authenticate header.
	 * It is used by proxies to challenge an application sending a request to
	 * authenticate itself.<br>
	 * If an HTTP/1.1 proxy server is sending a 407 Proxy Authentication
	 * Required response, it must include the Proxy-Authenticate header.<br>
	 * Proxies and gateways must be careful in interpreting all the Proxy
	 * headers. They generally are hop-by-hop headers, applying only to the
	 * current connection. For instance, the Proxy-Authenticate header requests
	 * authentication for the current connection.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * roxy-Authenticate: Basic realm="Super Secret Corporate Financial Documents"
	 * </pre>
	 */
	PROXY_AUTHENTICATE("Proxy-Authenticate"),
	
	/**
	 * HTTP/1.1 - RFC2616
	 * <p>
	 * Servers can use the Retry-After header to tell a client when to retry its
	 * request for a resource. It is used with the 503 Service Unavailable
	 * status code to give the client a specific date and time (or number of
	 * seconds) at which it should retry its request.<br>
	 * A server can also use this header when it is redirecting clients to
	 * resources, giving the client a time to wait before making a request on
	 * the resource to which it is redirected. This can be very useful to
	 * servers that are creating dynamic resources, allowing the server to
	 * redirect the client to the newly created resource but giving time for the
	 * resource to be created.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Retry-After: Tue, 3 Oct 1997 02:15:31 GMT 
	 * Retry-After: 120
	 * </pre>
	 */
	RETRY_AFTER("Retry-After"),
	
	/**
	 * HTTP/1.0 - RFC1945 -> HTTP/1.1 - RFC2616
	 * <p>
	 * The Server header is akin to the User-Agent header; it provides a way for
	 * servers to identify themselves to clients. Its value is the server name
	 * and an optional comment about the server.<br>
	 * Because the Server header identifies the server product and can contain
	 * additional comments about the product, its format is somewhat free-form.
	 * If you are writing software that depends on how a server identifies
	 * itself, you should experiment with the server software to see what it
	 * sends back, because these tokens vary from product to product and release
	 * to release.<br>
	 * As with the User-Agent header, don't be surprised if an older proxy or
	 * gateway inserts what amounts to a Via header in the Server header itself.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Server: Microsoft-Internet-Information-Server/1.0
	 * Server: websitepro/1.1f (s/n wpo-07d0)
	 * Server: apache/1.2b6 via proxy gateway CERN-HTTPD/3.0 libwww/2.13
	 * </pre>
	 */
	SERVER("Server"),
	
	/**
	 * HTTP/1.1 - Original Netscape cookie standard which is widely used,
	 * extension response header
	 * <p>
	 * The Set-Cookie header is the partner to the Cookie header
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Set-Cookie: lastorder=00183; path=/orders 
	 * Set-Cookie: private_id=519; secure
	 * </pre>
	 */
	SET_COOKIE("Set-Cookie"),
	
	/**
	 * HTTP/1.1 - RFC2616
	 * <p>
	 * The Vary header is used by servers to inform clients what headers from a
	 * client's request will be used in server-side negotiation.Its value is a
	 * list of headers that the server looks at to determine what to send the
	 * client as a response.<br>
	 * An example of this would be a server that sends special HTML pages based
	 * on your web browser's features. A server sending these special pages for
	 * a URL would include a Vary header that indicated that it looked at the
	 * User-Agent header of the request to determine what to send as a response.
	 * <br>
	 * 
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Vary: User-Agent
	 * </pre>
	 */
	VARY("Vary"),
	
	/**
	 * HTTP/1.1 - RFC2616
	 * <p>
	 * The Warning header is used to give a little more information about what
	 * happened during a request. It provides the server with a way to send
	 * additional information that is not in the status code or reason phrase.
	 * <p>
	 * Several warning codes are defined in the HTTP/1.1 specification:
	 * 
	 * <pre>
	 * 101 Response Is Stale
	 *     When a response message is known to be stale— for instance, 
	 *     if the origin server is unavailable for revalidation— this warning must be included.
	 * 111 Revalidation Failed
	 * 	   If a cache attempts to revalidate a response with an origin server and the revalidation fails because 
	 *     the cache cannot reach the origin server, this warning must be included in the response to the client.
	 * 112 Disconnected Operation
	 *     An informative warning; should be used if a cache's connectivity to the network is removed.
	 * 113 Heuristic Expiration
	 *     Caches must include this warning if their freshness heuristic is greater than 24 hours 
	 *     and they are returning a response with an age greater than 24 hours.
	 * 199 Miscellaneous Warning
	 *     Systems receiving this warning must not take any automated response; the message may 
	 *     and probably should contain a body with additional information for the user.
	 * 214 Transformation Applied
	 *     Must be added by any intermediate application, such as a proxy, 
	 *     if the application performs any transformation that changes the content encoding of the response.
	 * 299 Miscellaneous Persistent Warning
	 *     Systems receiving this warning must not take any automated reaction; 
	 *     the error may contain a body with more information for the user.
	 * </pre>
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Warning: 113
	 * </pre>
	 */
	WARNING("Warning"),
	
	/**
	 * HTTP/1.0 - RCF1945 -> HTTP/1.1 - RFC2616
	 * <p>
	 * The WWW-Authenticate header is used in 401 Unauthorized responses to
	 * issue a challenge authentication scheme to the client.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * WWW-Authenticate: Basic realm="Your Private Travel Profile"
	 * </pre>
	 */
	WWW_AUTHENTICATE("WWW-Authenticate"),
	
	/**
	 * Extension response header
	 * <p>
	 * The X headers are all extension headers. The X-Cache header is used by
	 * Squid to inform a client whether a resource is available.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * X-Cache: HIT
	 * </pre>
	 */
	X_CACHE("X-Cache"),
	
	/**
	 * Extension response header
	 * <p>
	 * This header is used to overcome a bug related to response header length
	 * in some browsers; it pads the response message headers with extra bytes
	 * to work around the bug.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * X-Pad: pad-text
	 * </pre>
	 */
	X_PAD("X-Pad"),

	// ~ --------------------------------------------------------------------------------------- entity headers

	/**
	 * HTTP/1.0 - RCF1945 -> HTTP/1.1 - RFC2616
	 * <p>
	 * The Content-Encoding header is used to specify whether any encodings have
	 * been performed on the object. By encoding the content, a server can
	 * compress it before sending the response. The value of the
	 * Content-Encoding header tells the client what type or types of encoding
	 * have been performed on the object. With that information, the client can
	 * then decode the message. <br>
	 * Sometimes more than one encoding is applied to an entity, 
	 * in which case the encodings must be listed in the order in which they were performed.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Content-Encoding: gzip 
	 * Content-Encoding: compress, gzip
	 * </pre>
	 */
	CONTENT_ENCODING("Content-Encoding"),
	
	/**
	 * HTTP/1.1 - RFC2616
	 * <p>
	 * The Content-Language header tells the client the natural language that
	 * should be understood in order to understand the object. For instance, a
	 * document written in French would have a Content-Language value indicating
	 * French. If this header is not present in the response, the object is
	 * intended for all audiences. Multiple languages in the header's value
	 * indicate that the object is suitable for audiences of each language
	 * listed. <br>
	 * One caveat about this header is that the header's value may just
	 * represent the natural language of the intended audience of this object,
	 * not all or any of the languages contained in the object. Also, this
	 * header is not limited to text or written data objects; images, video, and
	 * other media types can be tagged with their intended audiences' natural
	 * languages.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Content-Language: zh 
	 * Content-Language: en, fr
	 * </pre>
	 */
	CONTENT_LANGUAGE("Content-Language"),
	
	/**
	 * HTTP/1.0 - RCF1945 -> HTTP/1.1 - RFC2616
	 * <p>
	 * The Content-Length header gives the length or size of the entity body. If
	 * the header is in a response message to a HEAD HTTP request, the value of
	 * the header indicates the size that the entity body would have been had it
	 * been sent.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Content-Length: 2417
	 * </pre>
	 */
	CONTENT_LENGTH("Content-Length"),
	
	/**
	 * HTTP/1.1 - RFC2616
	 * <p>
	 * The Content-Location header is included in an HTTP message to give the
	 * URL corresponding to the entity in the message. For objects that may have
	 * multiple URLs, a response message can include a Content-Location header
	 * indicating the URL of the object used to generate the response. The
	 * Content-Location can be different from the requested URL. <br>
	 * This generally is used by servers that are directing or redirecting a
	 * client to a new URL. If the URL is relative, it should be interpreted
	 * relative to the Content-Base header. If the Content-Base header is not
	 * present, the URL used in the request should be used.
	 * <p>
	 * For examples: 
	 * 
	 * <pre>
	 * Content-Location: http://www.joes-hardware.com/index.html
	 * </pre>
	 */
	CONTENT_LOCATION("Content-Location"),
	
	/**
	 * HTTP/1.1 - RFC2616
	 * <p>
	 * The Content-MD5 header is used by servers to provide a message-integrity
	 * check for the message body. Only an origin server or requesting client
	 * should insert a Content-MD5 header in the message. The value of the
	 * header is an MD5 digest of the (potentially encoded) message body.<br>
	 * The MD5 digest is defined in RFC 1864. The value of this header allows
	 * for an end-to-end check on the data, useful for detecting unintentional
	 * modifications to the data in transit. It is not intended to be used for
	 * security purposes.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Content-MD5: Q2h1Y2sgSW51ZwDIAXR5IQ==
	 * </pre>
	 */
	CONTENT_MD5("Content-MD5"),
	
	/**
	 * HTTP/1.1 - RFC2616
	 * <p>
	 * The Content-Range header is sent as the result of a request that
	 * transmitted a range of a document. It provides the location (range)
	 * within the original entity that this entity represents. It also gives the
	 * length of the entire entity.<br>
	 * If an "*" is present in the value instead of the length of the entire
	 * entity, this means that the length was not known when the response was
	 * sent.
	 * <p>
	 * Servers responding with the 206 Partial Content response code must not
	 * include a Content-Range header with an "*" as the length.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Content-Range: bytes 500-999 / 5400
	 * </pre>
	 */
	CONTENT_RANGE("Content-Range"),
	
	/**
	 * HTTP/1.0 - RCF1945 -> HTTP/1.1 - RFC2616
	 * <p>
	 * The Content-Type header tells the media type of the object in the
	 * message.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Content-Type: text/html; charset=iso-latin-1
	 * </pre>
	 */
	CONTENT_TYPE("Content-Type"),
	
	/**
	 * HTTP/1.1 - RFC2616
	 * <p>
	 * The ETag header provides the entity tag for the entity contained in the
	 * message. An entity tag is basically a way of identifying a resource.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * ETag: "11e92a-457b-31345aa" 
	 * ETag: W/"11e92a-457b-3134b5aa"
	 * </pre>
	 */
	ETAG("ETag"),
	
	/**
	 * HTTP/1.0 - RCF1945 -> HTTP/1.1 - RFC2616
	 * <p>
	 * The Expires header gives a date and time at which the response is no
	 * longer valid. This allows clients such as your browser to cache a copy
	 * and not have to ask the server if it is still valid until after this time
	 * has expired. 
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Expires: Thu, 03 Oct 1997 17:15:00 GMT
	 * </pre>
	 */
	EXPIRES("Expires"),
	
	/**
	 * HTTP/1.0 - RCF1945 -> HTTP/1.1 - RFC2616
	 * <p>
	 * The From header says who the request is coming from. The format is just a
	 * valid Internet email address (specified in RFC 1123) for the user of the
	 * client.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * From: slurp@inktomi.com
	 * </pre>
	 */
	FROM("From"),
	
	/**
	 * HTTP/1.0 - RFC1945 -> HTTP/1.1 - RFC2616
	 * <p>
	 * The Last-Modified header tries to provide information about the last time
	 * this entity was changed. This could mean a lot of things. For example,
	 * resources typically are files on a server, so the Last-Modified value
	 * could be the last-modified time provided by the server's filesystem. On
	 * the other hand, for dynamically created resources such as those created
	 * by scripts, the Last-Modified value could be the time the response was
	 * created. <br>
	 * Servers need to be careful that the Last-Modified time is not in the
	 * future. HTTP/1.1 servers should reset the Last-Modified time if it is
	 * later than the value that would be sent in the Date header.
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Last-Modified: Thu, 03 Oct 1997 17:15:00 GMT
	 * </pre>
	 */
	LAST_MODIFIED("Last-Modified"),
	
	/**
	 * HTTP/1.1 - RFC2616
	 * <p>
	 * The Range header is used in requests for parts or ranges of an entity.
	 * Its value indicates the range of the entity that is included in the
	 * message.<br>
	 * Requests for ranges of a document allow for more efficient requests of
	 * large objects (by requesting them in segments) or for recovery from
	 * failed transfers (allowing a client to request the range of the resource
	 * that did not make it).
	 * <p>
	 * For examples:
	 * 
	 * <pre>
	 * Range: bytes=500-1500
	 * </pre>
	 */
	RANGE("Range");

	// ~ ---------------------------------------------------------------------------------------------------------

	private final String name;

	private HttpHeaderType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
