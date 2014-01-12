package org.craft.atom.protocol.http.model;

/**
 * Constants enumerating the HTTP status codes and reason phrase.
 * All status codes defined in RFC1945 (HTTP/1.0), RFC2616 (HTTP/1.1)
 * 
 * @author mindwind
 * @version 1.0, Feb 1, 2013
 * @see HttpStatusLine
 */
public enum HttpStatus {
	
	
	// ~ 1xx --------------------------------------------------------------------------------------- Informational
	
	
	/** (HTTP/1.1 - RFC 2616) An initial part of the request was received, and the client should continue. */
	CONTINUE(100, "Continue"),
	
	/** (HTTP/1.1 - RFC 2616) The server is changing protocols, as specified by the client, to one listed in the Upgrade header. */
	SWITCHING_PROTOCOLS(101, "Switching Protocols"),
	
	
	// ~ 2xx --------------------------------------------------------------------------------------- Successful
	
	
	/** (HTTP/1.0 - RFC 1945) The request is okay. */
	OK(200, "OK"),
	
	/** (HTTP/1.0 - RFC 1945) The resource was created (for requests that create server objects). */
	CREATED(201, "Created"),
	
	/** (HTTP/1.0 - RFC 1945) The request was accepted, but the server has not yet performed any action with it. */
	ACCEPTED(202, "￼￼￼Accepted"),
	
	/** (HTTP/1.1 - RFC 2616) The transaction was okay, except the information contained in the entity headers was not from the origin server, but from a copy of the resource. */
	NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information"),
	
	/** (HTTP/1.0 - RFC 1945) The response message contains headers and a status line, but no entity body. */
	NO_CONTENT(204, "No Content"),
	
	/** (HTTP/1.1 - RFC 2616) Another code primarily for browsers; basically means that the browser should clear any HTML form elements on the current page. */
	RESET_CONTENT(205, "Reset Content"),
	
	/** (HTTP/1.1 - RFC 2616) A partial request was successful.  */
	PARTIAL_CONTENT(206, "Partial Content"),
	
	
	// ~ 3xx --------------------------------------------------------------------------------------- Redirection
	
	
	/** (HTTP/1.1 - RFC 2616) A client has requested a URL that actually refers to multiple resources. This code is returned along with a list of options; the user can then select which one he wants. */
	MULTIPLE_CHOICES(300, "Multiple Choices"),
	
	/** (HTTP/1.0 - RFC 1945) The requested URL has been moved. The response should contain a Location URL indicating where the resource now resides. */
	MOVED_PERMANENTLY(301, "Moved Permanently"),
	
	/** (HTTP/1.0 - RFC 1945) Like the 301 status code, but the move is temporary. The client should use the URL given in the Location header to locate the resource temporarily. */
	MOVED_TEMPORARILY(302, "Moved Temporarily"),
	
	/** (HTTP/1.0 - RFC 1945) As same as MOVED_TEMPORARILY */
	FOUND(302, "Found"),
	
	/** (HTTP/1.1 - RFC 2616) Tells the client that the resource should be fetched using a different URL. This new URL is in the Location header of the response message. */
	SEE_OTHER(303, "See Other"),
	
	/** (HTTP/1.0 - RFC 1945) Clients can make their requests conditional by the request headers they include. This code indicates that the resource has not changed. */
	NOT_MODIFIED(304, "Not Modified"),
	
	/** (HTTP/1.1 - RFC 2616) The resource must be accessed through a proxy, the location of the proxy is given in the Location header. */
    USE_PROXY(305, "Use Proxy"),
    
    /** (HTTP/1.1 - RFC 2616) Like the 301 status code; however, the client should use the URL given in the Location header to locate the resource temporarily. */
    TEMPORARY_REDIRECT(307, "Temporary Redirect"),
	
	
    // ~ 4xx --------------------------------------------------------------------------------------- Client error
	
    
    /** (HTTP/1.1 - RFC 2616) Tells the client that it sent a malformed request. */
    BAD_REQUEST(400, "Bad Request"),
    
    /** (HTTP/1.0 - RFC 1945) Returned along with appropriate headers that ask the client to authenticate itself before it can gain access to the resource. */
    UNAUTHORIZED(401, "Unauthorized"),
    
    /** (HTTP/1.1 - RFC 2616) Currently this status code is not used, but it has been set aside for future use. */
    PAYMENT_REQUIRED(402, "Payment Required"),
    
    /** (HTTP/1.0 - RFC 1945) The request was refused by the server. */
    FORBIDDEN(403, "Forbidden"),
    
    /** ￼(HTTP/1.0 - RFC 1945) The server can't find request URL */
    NOT_FOUND(404, "Not Found"),
    
    /** (HTTP/1.1 - RFC 2616) A request was made with a method that is not supported for the requested URL. The Allow header should be included in the response to tell the client what methods are allowed on the requested resource. */
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    
    /** (HTTP/1.1 - RFC 2616) Clients can specify parameters about what types of entities they are willing to accept. This code is used when the server has no resource matching the URL that is acceptable for the client.*/
    NOT_ACCEPTABLE(406, "Not Acceptable"),
    
    /** (HTTP/1.1 - RFC 2616) Like the 401 status code, but used for proxy servers that require authentication for a resource. */
    PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
    
    /** (HTTP/1.1 - RFC 2616) If a client takes too long to complete its request, a server can send back this status code and close down the connection. */
    REQUEST_TIMEOUT(408, "Request Timeout"),
    
    /** (HTTP/1.1 - RFC 2616) The request is causing some conflict on a resource. */
    CONFLICT(409, "Conflict"),
    
    /** (HTTP/1.1 - RFC 2616) Like the 404 status code, except that the server once held the resource. */
    GONE(410, "Like the 404 status code, except that the server once held the resource."),
    
    /** (HTTP/1.1 - RFC 2616) Servers use this code when they require a Content-Length header in the request message. The server will not accept requests for the resource without the Content-Length header.*/
    LENGTH_REQUIRED(411, "Length Required"),
    
    /** (HTTP/1.1 - RFC 2616) If a client makes a conditional request and one of the conditions fails, this response code is returned. */
    PRECONDITION_FAILED(412, "Precondition Failed"),
    
    /** (HTTP/1.1 - RFC 2616) The client sent an entity body that is larger than the server can or wants to process. */
    REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"),
    
    /** (HTTP/1.1 - RFC 2616) The client sent a request with a request URL that is larger than what the server can or wants to process. */
    REQUEST_URI_TOO_LONG(414, "Request URI Too Long"),

    /** (HTTP/1.1 - RFC 2616) The client sent an entity of a content type that the server does not understand or support. */
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
    
    /** (HTTP/1.1 - RFC 2616) The request message requested a range of a given resource, and that range either was invalid or could not be met. */
    REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"),
    
    /** (HTTP/1.1 - RFC 2616) The request contained an expectation in the Expect request header that could not be satisfied by the server. */
    EXPECTATION_FAILED(417, "Expectation Failed"),
    
	
	// ~ 5xx --------------------------------------------------------------------------------------- Server error
    
    
    /** (HTTP/1.0 - RFC 1945) The server encountered an error that prevented it from servicing the request. */
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    
    /** (HTTP/1.0 - RFC 1945) The client made a request that is beyond the server's capabilities. */
    NOT_IMPLEMENTED(501, "Not Implemented"),
    
    /** (HTTP/1.0 - RFC 1945) A server acting as a proxy or gateway encountered a bogus response from the next link in the request response chain. */
    BAD_GATEWAY(502, "Bad Gateway"),
    
    /** (HTTP/1.0 - RFC 1945) The server cannot currently service the request but will be able to in the future. */
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    
    /** (HTTP/1.1 - RFC 2616) Similar to the 408 status code, except that the response is coming from a gateway or proxy that has timed out waiting for a response to its request from another server. */
    GATEWAY_TIMEOUT(504, "Gateway Timeout"),
    
    /** (HTTP/1.1 - RFC 2616) The server received a request in a version of the protocol that it can't or won't support. */
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported");
    
	
	// ~ ---------------------------------------------------------------------------------------------------------
	
	
	private final int    statusCode  ;
	private final String reasonPhrase;
	
	
	private HttpStatus(int statusCode, String reasonPhrase) {
		this.statusCode = statusCode;
		this.reasonPhrase = reasonPhrase;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getReasonPhrase() {
		return reasonPhrase;
	}
	
}
