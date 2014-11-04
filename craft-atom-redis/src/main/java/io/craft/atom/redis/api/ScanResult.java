package io.craft.atom.redis.api;

import java.util.List;

import lombok.Getter;
import lombok.ToString;

/**
 * Redis SCAN SSCAN HSCAN ZSCAN command return result object.
 * 
 * @author mindwind
 * @version 1.0, Apr 23, 2014
 */
@ToString
public class ScanResult<T> {

	
	@Getter private String  cursor  ;
    @Getter private List<T> elements;
	
    
    public ScanResult(String cursor, List<T> elements) {
    	this.cursor   = cursor  ;
    	this.elements = elements;
    }

}
