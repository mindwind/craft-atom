package org.craft.atom.protocol;

import java.nio.charset.Charset;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author mindwind
 * @version 1.0, Feb 6, 2013
 */
@ToString(of = "charset")
public class AbstractProtocolCodec {
	
	@Getter @Setter protected Charset charset = Charset.forName("utf-8");

}
