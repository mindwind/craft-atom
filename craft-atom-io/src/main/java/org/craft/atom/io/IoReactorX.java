package org.craft.atom.io;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author mindwind
 * @version 1.0, Dec 24, 2013
 */
@ToString
public class IoReactorX implements Serializable {

	
	private static final long serialVersionUID = 3536608515158732642L;

	@Getter @Setter protected boolean            isSelectable                                    ;
	@Getter @Setter protected List<IoProcessorX> ioProcessorXList = new ArrayList<IoProcessorX>();

	
	public void add(IoProcessorX ipx) {
		ioProcessorXList.add(ipx);
	}
	
}
