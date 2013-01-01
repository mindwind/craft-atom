package org.craft.atom.cache.impl;

/**
 * Cache Host
 * 
 * @author Hu Feng
 * @version 1.0, Oct 22, 2012
 */
public class CacheHost {

	private String ip;
	private Integer port;

	public CacheHost() {
		super();
	}

	public CacheHost(String ip, Integer port) {
		super();
		this.ip = ip;
		this.port = port;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

}
