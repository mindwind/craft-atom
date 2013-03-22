package org.craft.atom.protocol.http;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Generates date in the format required by the HTTP protocol.
 * 
 * @author mindwind
 * @version 1.0, Mar 22, 2013
 */
public class HttpDates {
	
	/** The date format pattern used to generate the header in RFC 1123 format. */
    private static final String DATE_FORMAT_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
    
    /** The time zone to use in the date header. */
    private static final TimeZone GMT = TimeZone.getTimeZone("GMT");
    
    /** The date format thread local object */
    private static final ThreadLocal<DateFormat> threadLocal = new ThreadLocal<DateFormat>();
   
    
    private static long dateAsLong = 0L;
    private static String dateAsText = null;
    
    // ~ -----------------------------------------------------------------------------------------------------------
    
    private static DateFormat getDateFormat() {
    	DateFormat df = threadLocal.get();
    	if (df == null) {
    		df = new SimpleDateFormat(DATE_FORMAT_RFC1123, Locale.US);
    		df.setTimeZone(GMT);
    		threadLocal.set(df);
    	}
    	return df;
    }

    public static String formatCurrentDate() {
        long now = System.currentTimeMillis();
        if (now - dateAsLong > 1000) {
        	dateAsText = format(new Date(now));
            dateAsLong = now;
        }
        return dateAsText;
    }
    
    public static String format(Date date) {
    	DateFormat df = getDateFormat();
    	return df.format(date);
    }
    
    public static Date parse(String dateString) {
    	DateFormat df = getDateFormat();
    	try {
			return df.parse(dateString);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Invalid date string format=" + dateString, e);
		}
    }
	
}
