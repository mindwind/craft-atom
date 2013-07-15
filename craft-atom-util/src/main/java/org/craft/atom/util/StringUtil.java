package org.craft.atom.util;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

/**
 * String util class.
 *
 * @author Hu Feng
 * @version 1.0, 2011-12-23
 */
public class StringUtil {
	
	/**
	 * Convert from DBC case to SBC case.
	 * 
	 * @param src 
	 * @return SBC case string
	 */
	public static String dbc2sbcCase(String src) {
		if (src == null) {
			return null;
		}
		
		char[] c = src.toCharArray();
		for (int i = 0; i < c.length; i++) {
			// WHITESPCE ASCII-32
			if (c[i] == 32) {
				c[i] = (char) 12288;
				continue;
			}
			
			// ASCII character 33-126 <-> unicode 65281-65374
			if (c[i] < 127)
				c[i] = (char) (c[i] + 65248);
		}
		
		return new String(c);
	}
	
	/**
	 * Convert from SBC case to DBC case
	 * 
	 * @param src
	 * @return DBC case
	 */
	public static String sbc2dbcCase(String src) {
		if (src == null) {
			return null;
		}
		char[] c = src.toCharArray();
		for (int i = 0; i < c.length; i++) {
			// WHITESPCE ASCII-32
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			
			// ASCII character 33-126 <-> unicode 65281-65374
			if (c[i] > 65280 && c[i] < 65375) {
				c[i] = (char) (c[i] - 65248);
			}
		}
		return new String(c);
	}
	
	/**
	 * Convert a string array to a string using delim as separator. If array == null return null.
	 * 
	 * @param array
	 * @param delim if delim == null, convert to ""
	 * @return a new concatenated string
	 */
	public static String toString(String[] array, String delim) {
		if (array == null) {
			return null;
		}
		
		int length = array.length - 1;
		if (delim == null) {
			delim = "";
		}
		
		StringBuilder buf = new StringBuilder(length * 8);
		for (int i = 0; i < length; i++) {
			buf.append(array[i]);
			buf.append(delim);
		}
		buf.append(array[length]);
		
		return buf.toString();
	}
	
	/**
	 * Judge the string within a string array or not.<br>
	 * <li>strings == null("")    return false
	 * <li>string == null("")     return false
	 * 
	 * @param strings
	 * @param string
	 * @param caseSensitive
	 * @return true if string within the string array, otherwise false
	 */
	public static boolean contains(String[] strings, String string, boolean caseSensitive) {
		if (strings == null || strings.length == 0) {
			return false;
		}
		
		if (string == null || string.length() == 0) {
			return false;
		}

		for (int i = 0; i < strings.length; i++) {
			if (caseSensitive == true) {
				if (strings[i].equals(string)) {
					return true;
				}
			} else {
				if (strings[i].equalsIgnoreCase(string)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Judge the string within a string array or not, if the string is a substring of any string in the array return true. <br>
	 * <li>strings == null("")    return false
	 * <li>string == null("")     return false
	 * 
	 * @param strings
	 * @param string
	 * @param caseSensitive
	 * @return true if string or its substring within the string array, otherwise false
	 */
	public static boolean containSubstring(String[] strings, String string, boolean caseSensitive) {
		if (strings == null || strings.length == 0) {
			return false;
		}
		
		if (string == null || string.length() == 0) {
			return false;
		}

		for (int i = 0; i < strings.length; i++) {
			if (caseSensitive == true) {
				if (strings[i].equals(string) || strings[i].indexOf(string) > -1) {
					return true;
				}
			} else {
				if (strings[i].equalsIgnoreCase(string) || strings[i].toLowerCase().indexOf(string.toLowerCase()) > -1) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Count the number of target string occur in the source.
	 * 
	 * @param src
	 * @param target
	 * @return the number of target string occur in the source
	 */
	public static int count(String src, String target) {
		int count = 0;
		int index = src.indexOf(target);
		while (index != -1) {
			count++;
			index = src.indexOf(target, index + 1);
		}
		return count;
	}
	
	/**
	 * Convert first char to lower case
	 * 
	 * @param src
	 * @return string which first char converted to lower case
	 */
	public static String firstCharToLowerCase(String src) {
		if (src.length() == 1) {
			return src.substring(0, 1).toLowerCase();
		}
		else {
			return src.substring(0, 1).toLowerCase() + src.substring(1);
		}
	}
	
	/**
	 * Convert first char to upper case
	 * 
	 * @param src
	 * @return string which first char converted to upper case
	 */
	public static String firstCharToUpperCase(String src) {
		if (src.length() == 1) {
			return src.substring(0, 1).toUpperCase();
		} 
		else {
			return src.substring(0, 1).toUpperCase() + src.substring(1);
		}
	}
	
	/**
	 * Convert a string to currency string
	 * 
	 * @param src
	 * @param locale
	 * @return The currency format string
	 */
	public static String toCurrencyString(String src, Locale locale) {
		Double currency = Double.parseDouble(src);
		return toCurrencyString(currency, locale);
	}
	
	/**
	 * Convert a double to currency string.
	 * 
	 * @param src
	 * @param locale
	 * @return The currency format string
	 */
	public static String toCurrencyString(Double src, Locale locale) {
		if (src == null) {
			throw new NullPointerException("src == null");
		}

		NumberFormat nf = NumberFormat.getCurrencyInstance(locale);
		nf.setMaximumFractionDigits(2);
		nf.setMinimumFractionDigits(2);
		return nf.format(src);
	}
	
	/**
	 * Using value to replace variable which with a specified prefix in the src string.
	 * <br>
	 * Note: it's not a nested process, if the values array has variable ignore it. 
	 * 
	 * <pre>
	 * variable = prefix + num (num start from 1)<br>
	 * eg:
	 * abc%1def%2ghi%3jk, like %1 is variable
	 * abc$1def$2, like $1 is variable and prefix is $
	 * </pre>
	 * 
	 * <li> if src == null or values == null or values.length == 0 return src itself.
	 * <li> if values.length > variable num, ignore redundant value.
	 * <li> if values.length < variable num, use the last value replace the redundant variable.
	 * 
	 * @param prefix variable prefix, if prefix is null using default prefix "%"
	 * @param src
	 * @param value 
	 * @return replaced string
	 */
	public static String replace(String prefix, String src, String value) {
		String[] values = new String[1];
		values[0] = value;
		return replace(prefix, src, values);
	}
	
	
	public static String getReplaceString(String src, String[] values) {
		return replace("%", src, values);
	}
	
	/**
	 * Using values string array to replace variable which with a specified prefix in the src string.
	 * <br>
	 * Note: it's not a nested process, if the values array has variable ignore it. 
	 * 
	 * <pre>
	 * variable = prefix + num (num start from 1)<br>
	 * eg:
	 * abc%1def%2ghi%3jk, like %1 is variable
	 * abc$1def$2, like $1 is variable and prefix is $
	 * </pre>
	 * 
	 * <li> if src == null or values == null or values.length == 0 return src itself.
	 * <li> if values.length > variable num, ignore redundant value.
	 * <li> if values.length < variable num, use the last value replace the redundant variable.
	 * 
	 * @param prefix variable prefix, if prefix is null using default prefix "%"
	 * @param src
	 * @param values 
	 * @return replaced string
	 */
	public static String replace(String prefix, String src, String[] values) {
		if (src == null || values == null || values.length < 1) {
			return src;
		}
		if (prefix == null) {
			prefix = "%";
		}

		StringBuilder result = new StringBuilder();
		int beginIndex = 0;
		for (int i = 0, count = 0; true; i++) {
			String argument = prefix + Integer.toString(i + 1);
			int endIndex = src.indexOf(argument, beginIndex);
			count++;
			if (endIndex != -1) {
				int len = Integer.valueOf(count).toString().length() + 1;
				StringBuilder part = new StringBuilder(src.substring(beginIndex, endIndex));
				if (i < values.length) {
					part.append(values[i]);
				} else {
					part.append(values[values.length - 1]);
				}
				result.append(part);
				beginIndex = endIndex + len;
			} else {
				result.append(src.substring(beginIndex));
				break;
			}
		}
		return result.toString();
	}
	
	/**
	 * Insert target string to src string at index position. If index is invalid return original string.
	 * 
	 * @param src
	 * @param target
	 * @param index
	 * @return string after inserted.
	 */
	public static String insert(String src, String target, int index) {
		if (src == null) {
			return src;
		}
		if (index < 0 || index > src.length()) {
			return src;
		}
		
		StringBuilder sb = new StringBuilder();
		if (index != 0) {
			sb.append(src.substring(0, index));
		}
		sb.append(target);
		sb.append(src.substring(index));

		return sb.toString();
	}
	
    /**
     * <p>Checks if a String is empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is empty or null
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }
    
    /**
     * <p>Checks if a String is whitespace, empty ("") or null.</p>
     *
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is null, empty or whitespace
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }
    
	/**
	 * <p>
	 * Checks if some strings has any empty string.
	 * </p>
	 * If strings == null, return true
	 * 
	 * @param strings
	 * @return true is any string is empty, otherwise false.
	 */
	public static boolean isAnyEmpty(String... strings) {
		if (strings == null) {
			return true;
		}

		for (String s : strings) {
			if (isEmpty(s)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if some strings has any blank string.<br>
	 * If strings == null, return true
	 * 
	 * @param strings
	 * @return true if any string is blank, otherwise false.
	 */
	public static boolean isAnyBlank(String... strings) {
		if (strings == null) {
			return true;
		}

		for (String s : strings) {
			if (isBlank(s)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Checks if all string is empty. If strings == null, return true
	 * 
	 * @param strings
	 * @return true if all string is empty, otherwise false.
	 */
	public static boolean isAllEmpty(String... strings) {
		if (strings == null) {
			return true;
		}
		
		boolean b = true;
		for (String s : strings) {
			b &= isEmpty(s);
		}
		
		return b;
	}
	
	/**
	 * Checks if all string is blank. If strings == null, return true
	 * 
	 * @param strings
	 * @return true if all string is blank, otherwise false.
	 */
	public static boolean isAllBlank(String... strings) {
		if (strings == null) {
			return true;
		}
		
		boolean b = true;
		for (String s : strings) {
			b &= isBlank(s);
		}
		
		return b;
	}
	
	/**
	 * Checks if all the strings is not blank.
	 * 
	 * @param strings
	 * @return true if all string is not blank, otherwise false.
	 */
	public static boolean isAllNotBlank(String... strings) {
		if (isAnyBlank(strings)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Checks if all the strings is not empty.
	 * 
	 * @param strings
	 * @return true if all string is not empty, otherwise false.
	 */
	public static boolean isAllNotEmpty(String... strings) {
		if (isAnyEmpty(strings)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Check if the string is a digital string, eg: 123 or 123.58 etc.
	 * 
	 * @param src
	 * @return true if string is digital
	 */
	public static boolean isDigitalString(String src) {
		if (src == null) {
			return false;
		}

		for (int i = 0; i < src.length(); i++) {
			if (Character.isDigit(src.charAt(0)) || src.charAt(0) == '.') {
				continue;
			} else {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Check that the given CharSequence is neither <code>null</code> nor of
	 * length 0. Note: Will return <code>true</code> for a CharSequence that
	 * purely consists of whitespace.
	 * <p>
	 * 
	 * <pre>
	 * StringUtils.hasLength(null) = false
	 * StringUtils.hasLength("") = false
	 * StringUtils.hasLength(" ") = true
	 * StringUtils.hasLength("Hello") = true
	 * </pre>
	 * 
	 * @param src
	 *            the CharSequence to check (may be <code>null</code>)
	 * @return <code>true</code> if the CharSequence is not null and has length
	 * @see #hasText(String)
	 */
	private static boolean hasLength(CharSequence src) {
		return (src != null && src.length() > 0);
	}
	
	/**
	 * Trim <i>all</i> whitespace from the given string: leading, trailing, and inbetween characters.
	 * 
	 * @param src
	 * @return string trimmed.
	 * @see java.lang.Character#isWhitespace
	 */
	public static String trimAll(String src) {
		if (!hasLength(src)) {
			return src;
		}

		StringBuilder sb = new StringBuilder(src);
		int index = 0;
		while (sb.length() > index) {
			if (Character.isWhitespace(sb.charAt(index))) {
				sb.deleteCharAt(index);
			} else {
				index++;
			}
		}
		return sb.toString();
	}
	
	/**
	 * Trim leading whitespace from the given string.
	 * 
	 * @param str
	 *            the String to check
	 * @return the trimmed String
	 * @see java.lang.Character#isWhitespace
	 */
	public static String trimLeading(String str) {
		if (!hasLength(str)) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
			sb.deleteCharAt(0);
		}
		return sb.toString();
	}

	/**
	 * Trim trailing whitespace from the given string.
	 * 
	 * @param str
	 * @return the trimmed String
	 * @see java.lang.Character#isWhitespace
	 */
	public static String trimTrailing(String str) {
		if (!hasLength(str)) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str);
		while (sb.length() > 0
				&& Character.isWhitespace(sb.charAt(sb.length() - 1))) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}
	
	/**
	 * Random split a string into number of segments.
	 * 
	 * @param num
	 * @return the array of strings split.
	 */
	public static String[] split(String str, int num) {
		if (str == null) {
			return null;
		}
		
		if (num <= 1) {
			return new String[] { str };
		}
		
		String[] sarr = null;
		int len = str.length();
		if (num >= len) {
			sarr = new String[len];
			for (int i = 0; i < len; i++) {
				sarr[i] = Character.toString(str.charAt(i));
			}
			return sarr;
		}
		
		Random ran = new Random();
		int scope = len / num;
		int start = 0;
		int end = 0;
		sarr = new String[num];
		for (int i = 0; i < num; i++) {
			if (i == num - 1) {
				sarr[i] = str.substring(start);
			} else {
				end = ran.nextInt(scope) + start;
				sarr[i] = str.substring(start, end);
				start = end;
			}
		}
		return sarr;
	}
	
	// ~ --------------------------------------------------------------------------------------------------------------
	
	private StringUtil() {
		throw new UnsupportedOperationException();
	}
}
