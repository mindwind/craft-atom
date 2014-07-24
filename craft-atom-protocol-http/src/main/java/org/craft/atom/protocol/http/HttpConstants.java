package org.craft.atom.protocol.http;


/**
 * @author mindwind
 * @version 1.0, Feb 6, 2013
 */
public class HttpConstants {
	
	
	public static final String CHARSET                   = "charset"               ;
	public static final String TRANSFER_ENCODING_CHUNKED = "chunked"               ;
	public static final String CONTENT_ENCODING_IDENTITY = "identity"              ;
	public static final String CONTENT_ENCODING_GZIP     = "gzip"                  ;
	public static final String CONTENT_ENCODING_DEFLATE  = "deflate"               ;
	public static final String CONTENT_ENCODING_COMPRESS = "compress"              ;
	public static final String CONNECTION_CLOSE          = "close"                 ;
	public static final String CONNECTION_KEEP_ALIVE     = "Keep-alive"            ;
	public static final String KEEP_ALIVE_OPTIONS        = "timeout=120, max=10000";
	
	
	// ~ ------------------------------------------------------------------------------------- some special character
	
    
	/**
	 * <pre>
	 * NULL              "\0";     
	 * Carriage Return   "\r"      
	 * Line Feed         "\n"      
	 * Space             " "       
	 * Horizontal Tab    "\t"      
	 * Colon             ":"       
	 * Semicolon         ";"       
	 * Equal Sign        "="       
	 * Comma             ","       
	 * Question Mark     "?"       
	 * Ampersand         "&"       
	 * Plus Sign         "+"       
	 * Percent Sign      "%" 
	 * </pre>
	 */      
	public static final byte NUL          = 0 ;           
	public static final byte CR           = 13;           
	public static final byte LF           = 10;           
	public static final byte SP           = 32;           
	public static final byte HT           = 9 ;            
	public static final byte COLON        = 58;        
	public static final byte SEMICOLON    = 59;    
	public static final byte EQUAL_SIGN   = 61;   
	public static final byte COMMA        = 44;        
	public static final byte Q_MARK       = 63;       
	public static final byte AMPERSAND    = 38;    
	public static final byte PLUS_SIGN    = 43;    
	public static final byte PERCENT_SIGN = 37; 
	
	
	public static final String S_NUL          = "\0";         
	public static final String S_CR           = "\r";         
	public static final String S_LF           = "\n";          
	public static final String S_SP           = " " ;          
	public static final String S_HT           = "\t";          
	public static final String S_COLON        = ":" ;      
	public static final String S_SEMICOLON    = ";" ;
	public static final String S_EQUAL_SIGN   = "=" ;
	public static final String S_COMMA        = "," ;
	public static final String S_Q_MARK       = "?" ;
	public static final String S_AMPERSAND    = "&" ;
	public static final String S_PLUS_SIGN    = "+" ;
	public static final String S_PERCENT_SIGN = "+" ;

}
