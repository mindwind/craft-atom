package org.craft.atom.protocol.http;

import org.craft.atom.protocol.AbstractProtocolDecoder;

/**
 * @author mindwind
 * @version 1.0, Feb 3, 2013
 * @see HttpRequestDecoder
 * @see HttpResponseDecoder
 */
abstract public class HttpDecoder extends AbstractProtocolDecoder {
	
	protected static final int START = 0;
	protected static final int REQUEST_LINE = 100;
	protected static final int STATUS_LINE = 200;
	protected static final int HEADER = 300;
	protected static final int ENTITY = 400;
	protected static final int END = -1;
	protected int state = START;

}
