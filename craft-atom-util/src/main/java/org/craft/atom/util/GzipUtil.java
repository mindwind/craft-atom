package org.craft.atom.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * GZip util
 * 
 * @author mindwind
 * @version 1.0, Jun 3, 2012
 */
public class GzipUtil {
	
	/**
	 * Compress data bytes with gzip algorithm
	 * 
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public static byte[] gzip(byte[] data) throws IOException {
		if (data == null) {
			return data;
		}
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gos = null;
		try {
			gos = new GZIPOutputStream(out);
			gos.write(data);
		} finally {
			gos.close();
		}
		
		return out.toByteArray();
	}

	/**
	 * Decompress data bytes with gzip algorithm
	 * 
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public static byte[] ungzip(byte[] data) throws IOException {
		if (data == null) { return data; }
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		
		GZIPInputStream gis = null;
		try {
			gis = new GZIPInputStream(in);
			byte[] buffer = new byte[1024];
			int n;
			while ((n = gis.read(buffer)) >= 0) {
				out.write(buffer, 0, n);
			}
		} finally {
			gis.close();
		}
		
		return out.toByteArray();
	}

}
