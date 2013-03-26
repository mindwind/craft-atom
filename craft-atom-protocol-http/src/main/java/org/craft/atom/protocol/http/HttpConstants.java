package org.craft.atom.protocol.http;

import java.util.Arrays;

/**
 * @author mindwind
 * @version 1.0, Feb 6, 2013
 */
public class HttpConstants {
	
	public static final String CHARSET = "charset";
	
	public static final String TRANSFER_ENCODING_CHUNKED = "chunked";
	public static final String CONTENT_ENCODING_IDENTITY = "identity";
	public static final String CONTENT_ENCODING_GZIP = "gzip";
	public static final String CONTENT_ENCODING_DEFLATE = "deflate";
	public static final String CONTENT_ENCODING_COMPRESS = "compress";
	public static final String CONNECTION_CLOSE = "close";
	public static final String CONNECTION_KEEP_ALIVE = "Keep-alive";
	public static final String KEEP_ALIVE_OPTIONS = "timeout=120, max=10000";
	
	// ~ ------------------------------------------------------------------------------------- some special character
			
	public static final byte NUL = 0;           // NULL              "\0";
	public static final byte CR = 13;           // Carriage Return   "\r"
	public static final byte LF = 10;           // Line Feed         "\n"
	public static final byte SP = 32;           // Space             " "
	public static final byte HT = 9;            // Horizontal Tab    "\t"
	public static final byte COLON = 58;        // Colon             ":"
	public static final byte SEMICOLON = 59;    // Semicolon         ";"
	public static final byte EQUAL_SIGN = 61;   // Equal Sign        "="
	public static final byte COMMA = 44;        // Comma             ","
	public static final byte Q_MARK = 63;       // Question Mark     "?"
	public static final byte AMPERSAND = 38;    // Ampersand         "&"
	public static final byte PLUS_SIGN = 43;    // Plus Sign         "+"
	public static final byte PERCENT_SIGN = 37; // Percent Sign      "%"
	
	public static final String S_NUL = "\0";         
	public static final String S_CR = "\r";         
	public static final String S_LF = "\n";          
	public static final String S_SP = " ";          
	public static final String S_HT = "\t";          
	public static final String S_COLON = ":";      
	public static final String S_SEMICOLON = ";";
	public static final String S_EQUAL_SIGN = "=";
	public static final String S_COMMA = ",";
	public static final String S_Q_MARK = "?";
	public static final String S_AMPERSAND = "&";
	public static final String S_PLUS_SIGN = "+";
	public static final String S_PERCENT_SIGN = "+";
	
	public static void main(String[] args) {
		System.out.println(Arrays.toString("\0\r\n \t:;=,?&+%".getBytes()));
		System.out.println(Integer.toHexString(EQUAL_SIGN));
	}
	
}
