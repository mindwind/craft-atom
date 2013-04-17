package org.craft.atom.test.charset;

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * @author mindwind
 * @version 1.0, Apr 16, 2013
 */
public class CharsetResearch {
	
	public static void main(String[] args) {
		// 单字节编码字符集
		Charset iso88591 = Charset.forName("iso-8859-1");
		// 双字节编码字符集
		Charset big5 = Charset.forName("big5");
		// 可变长度编码字符集
		Charset utf8 = Charset.forName("utf-8");
		
		
		// 原始字符串 
		String src = "测试";                
		// utf8 编码的字节数组
		byte[] utf8Bytes = src.getBytes(utf8);    
		// 使用 iso－8859－1 错误解码的字符串（乱码）
		String wrongStr = new String(utf8Bytes, iso88591);   
		// 使用 big5 错误解码的字符串（还是乱码）
		String wrongStr2 = new String(utf8Bytes, big5);
		
		
		System.out.println("wrongStr-iso88591-decoding = " + wrongStr + "    len=" + wrongStr.length());
		System.out.println("wrongStr-big5-decoding     = " + wrongStr2 + "   len=" + wrongStr2.length());
		System.out.println("orignal-utf8-bytes         = " + Arrays.toString(utf8Bytes));
		
		
		// 把 iso－8859－1 错误解码的字符串恢复utf8编码的字节数组 － 可逆
		byte[] resumeBytes = wrongStr.getBytes(iso88591);   
		String rightStr = new String(resumeBytes, utf8);
		
		
		// 把 big5 错误解码的字符串恢复utf8编码的字节数组 － 不可逆
		byte[] resumeBytes2 = wrongStr2.getBytes(big5);
		String rightStr2 = new String(resumeBytes2, utf8);
		
		
		System.out.println("resume-iso88591-utf8-bytes = " + Arrays.toString(resumeBytes));
		System.out.println("resume-big5-utf8-bytes     = " + Arrays.toString(resumeBytes2));
		System.out.println(rightStr);
		System.out.println(rightStr2);
		
		System.out.println(Arrays.toString("?".getBytes()));
	}
}
