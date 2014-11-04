package io.craft.atom.protocol.rpc;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

/**
 * @author mindwind
 * @version 1.0, Jul 25, 2014
 */
public class SerialA {
	
	
	// primitive
	@Getter @Setter private byte    b   ;
	@Getter @Setter private int     i   ;
	@Getter @Setter private long    l   ;
	@Getter @Setter private float   f   ;
	@Getter @Setter private double  d   ;
	@Getter @Setter private boolean bool;
	
	// object
	@Getter @Setter private String                    s                                           ;
	@Getter @Setter private Date                      date   = new Date()                         ;
	@Getter @Setter private List<String>              list   = new ArrayList<String>()            ;
	@Getter @Setter private Set<String>               set    = new HashSet<String>()              ;
	@Getter @Setter private Map<Long, String>         map    = new HashMap<Long, String>()        ;
	@Getter @Setter private Map<String, List<String>> nested = new HashMap<String, List<String>>();
	@Getter @Setter private SerialEnum                senum                                       ;
	@Getter @Setter private SerialB                   seb                                         ;
	
	// transient
	@Getter @Setter transient private String t;
	
	
	// temp
	@Getter @Setter public String t1 = "111111";
	@Getter @Setter public String t2 = "222222" ;
	
	
	// ~ -------------------------------------------------------------------------------------------------------------
	

	public SerialA addList(String e) {
		list.add(e);
		return this;
	}
	
	public SerialA addSet(String e) {
		set.add(e);
		return this;
	}
	
	public SerialA putMap(Long k, String v) {
		map.put(k, v);
		return this;
	}
	
	public SerialA putNested(String k, String... vs) {
		List<String> ls = new ArrayList<String>();
		for (String v : vs) {
			ls.add(v);
		}
		nested.put(k, ls);
		return this;
	}
	
}
