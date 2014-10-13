package org.craft.atom.io;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author mindwind
 * @version 1.0, Dec 24, 2013
 */
@ToString
public class IoReactorX implements Serializable {


	private static final long serialVersionUID = 5772691776878955554L;

	
	@Getter @Setter protected boolean              isSelectable                                       ;
	@Getter @Setter protected List<IoProcessorX>   ioProcessorXList = new ArrayList<IoProcessorX>(0) ;
	@Getter @Setter protected Set<Channel<byte[]>> aliveChannels    = new HashSet<Channel<byte[]>>(0);

	
	public void add(IoProcessorX ipx) {
		ioProcessorXList.add(ipx);
	}
	
}
