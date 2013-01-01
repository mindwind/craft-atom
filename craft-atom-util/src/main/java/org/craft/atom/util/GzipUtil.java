package org.craft.atom.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * GZip util
 * 
 * @author Hu Feng
 * @version 1.0, Jun 3, 2012
 */
public class GzipUtil {

	public static String gzip(String str) throws IOException {
		if (str == null || str.length() == 0) { return str; }
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = null;
		try {
			gzip = new GZIPOutputStream(out);
			gzip.write(str.getBytes("UTF-8"));
		} finally {
			gzip.close();
		}
		
		return out.toString("ISO-8859-1");
	}

	public static String ungzip(String str) throws IOException {
		if (str == null || str.length() == 0) { return str; }
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
		
		GZIPInputStream gunzip = null;
		try {
			gunzip = new GZIPInputStream(in);
			byte[] buffer = new byte[1024];
			int n;
			while ((n = gunzip.read(buffer)) >= 0) {
				out.write(buffer, 0, n);
			}
		} finally {
			gunzip.close();
		}
		
		return out.toString("UTF-8");
	}

}
