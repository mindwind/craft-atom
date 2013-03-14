package org.craft.atom.protocol.http;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.craft.atom.protocol.http.model.HttpHeader;
import org.craft.atom.protocol.http.model.HttpHeaderType;

/**
 * Factory and utility methods for {@link HttpHeader}.
 * 
 * @author mindwind
 * @version 1.0, Mar 14, 2013
 */
public class HttpHeaders {
	
	/**
	 * Creates a HTTP "Server" header with specified server name.
	 * 
	 * @param serverName
	 * @return the newly created header
	 */
	public static HttpHeader newServerHeader(String serverName) {
		return new HttpHeader(HttpHeaderType.SERVER.getName(), serverName);
	}
	
	/**
	 * Creates a HTTP "Date" header with current datetime.
	 * 
	 * @param keepAlive is keep alive.
	 * @return
	 */
	public static HttpHeader newConnectionHeader(boolean keepAlive) {
		if (keepAlive) {
			return new HttpHeader(HttpHeaderType.CONNECTION.getName(), "keep-alive");
		} else {
			return new HttpHeader(HttpHeaderType.CONNECTION.getName(), "close");
		}
	}
	
	/**
	 * Creates a HTTP "Date" header with current date time.
	 * 
	 * @return the newly created header
	 */
	public static HttpHeader newDateHeader() {
		return new HttpHeader(HttpHeaderType.DATE.getName(), HttpDates.currentDate());
	}
	
	// ~ -----------------------------------------------------------------------------------------------------------
	
	private static class HttpDates {
		/** The date format pattern used to generate the header in RFC 1123 format. */
	    private static final String DATE_FORMAT_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
	    
	    /** The time zone to use in the date header. */
	    private static final TimeZone GMT = TimeZone.getTimeZone("GMT");
	    
	    /** The date format object */
	    private static final DateFormat DATE_FORMAT;
	    
	    static {
	    	 DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_RFC1123, Locale.US);
	    	 DATE_FORMAT.setTimeZone(GMT);
	    }
	    
	    private static long dateAsLong = 0L;
	    private static String dateAsText = null;
	    
	    synchronized public static String currentDate() {
	        long now = System.currentTimeMillis();
	        if (now - dateAsLong > 1000) {
	            dateAsText = DATE_FORMAT.format(new Date(now));
	            dateAsLong = now;
	        }
	        return dateAsText;
	    }
	}
	
}
