package com.block.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>针对字符串的若干静态方法。</p>
 * 
 */
public abstract class StringUtils {

	private static final String FOLDER_SEPARATOR = "/";

	private static final String WINDOWS_FOLDER_SEPARATOR = "\\";

	private static final String TOP_PATH = "..";

	private static final String CURRENT_PATH = ".";

	private static final char EXTENSION_SEPARATOR = '.';


	//---------------------------------------------------------------------
	// General convenience methods for working with Strings
	//---------------------------------------------------------------------

	/**
	 * 检查给出的字符串对象是否有长度。<br>
	 * 
	 * <pre>
	 * StringUtils.hasLength(null) = false
	 * StringUtils.hasLength("") = false
	 * StringUtils.hasLength(" ") = true
	 * StringUtils.hasLength("Hello") = true
	 * </pre>
	 * 
	 * @param str 需要检查的字符串对象，可以为null。
	 * @return boolean 字符串是否为空。
	 */
	public static boolean hasLength(String str) {
		return (str != null && str.length() > 0);
	}

	/**
	 * 检查给出的字符串对象是否用以至少一个非空字符。<br>
	 * 
	 * <pre>
	 * StringUtils.hasText(null) = false
	 * StringUtils.hasText("") = false
	 * StringUtils.hasText(" ") = false
	 * StringUtils.hasText("12345") = true
	 * StringUtils.hasText(" 12345 ") = true
	 * </pre>
	 * 
	 * @param str 需要检查的字符串对象，可以为null。
	 * @return boolean 字符串是否拥有至少一个非空字符。
	 * @see java.lang.Character#isWhitespace
	 */
	public static boolean hasText(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return false;
		}
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 检查给出的字符串对象是否以特定的前缀开始，忽略大小写。
	 * 
	 * @param str 需要检查的字符串对象。
	 * @param prefix 特定的前缀。
	 * @return boolean 字符串对象是否以特定的前缀开始。
	 * @see java.lang.String#startsWith
	 */
	public static boolean startsWithIgnoreCase(String str, String prefix) {
		if (str == null || prefix == null) {
			return false;
		}
		if (str.startsWith(prefix)) {
			return true;
		}
		if (str.length() < prefix.length()) {
			return false;
		}
		String lcStr = str.substring(0, prefix.length()).toLowerCase();
		String lcPrefix = prefix.toLowerCase();
		return lcStr.equals(lcPrefix);
	}

	/**
	 * <p>检查两个字符串的内容是否相同，忽略大小写。</p>
	 * <p>任意一个字符串为Null时，返回false。</p>
	 *
	 * @param str1 第一个字符串。
	 * @param str2 第二个字符串。
	 * @return 内容是否相同。
	 */
	public static boolean equalsWithIgnoreCase(String str1, String str2) {
		if (str1 == null || str2 == null) {
			return false;
		}
		String lcStr1 = str1.toLowerCase();
		String lcStr2 = str2.toLowerCase();
		return lcStr1.equals(lcStr2);
	}
	
	/**
	 * 检查给出的字符串对象是否以特定的后缀结束，忽略大小写。
	 * 
	 * @param str 需要检查的字符串对象。
	 * @param suffix 特定的后缀。
	 * @return boolean 字符串对象是否以特定的后缀结束。
	 * @see java.lang.String#endsWith
	 */
	public static boolean endsWithIgnoreCase(String str, String suffix) {
		if (str == null || suffix == null) {
			return false;
		}
		if (str.endsWith(suffix)) {
			return true;
		}
		if (str.length() < suffix.length()) {
			return false;
		}
		String lcStr = str.substring(str.length() - suffix.length()).toLowerCase();
		String lcSuffix = suffix.toLowerCase();
		return lcStr.equals(lcSuffix);
	}

	/**
	 * 统计特定的子串在给出的字符串对象中出现的次数。
	 * 
	 * @param str 给出的字符串对象。如果为null，则返回0。
	 * @param sub 特定的子串。如果为null，则返回0。
	 * @return int 次数。
	 */
	public static int countOccurrencesOf(String str, String sub) {
		if (str == null || sub == null || str.length() == 0 || sub.length() == 0) {
			return 0;
		}
		int count = 0, pos = 0, idx = 0;
		while ((idx = str.indexOf(sub, pos)) != -1) {
			++count;
			pos = idx + sub.length();
		}
		return count;
	}

	/**
	 * 将给出的字符串对象中与特定子串匹配的部分全部替换为另一个子串。
	 * 
	 * @param inString 给出的字符串对象。
	 * @param oldPattern 匹配子串。
	 * @param newPattern 替换字串。
	 * @return String 替换后的字符串对象。
	 */
	public static String replace(String inString, String oldPattern, String newPattern) {
		if (inString == null) {
			return null;
		}
		if (oldPattern == null || newPattern == null||oldPattern=="") {
			return inString;
		}

		StringBuffer sbuf = new StringBuffer();
		// output StringBuffer we'll build up
		int pos = 0; // our position in the old string
		int index = inString.indexOf(oldPattern);
		// the index of an occurrence we've found, or -1
		int patLen = oldPattern.length();
		//必须保证patLen＞0，如果patLen==0形成死循环。  宋军庆 2013-06-25
		while (index >= 0) {
			sbuf.append(inString.substring(pos, index));
			sbuf.append(newPattern);
			pos = index + patLen;//死循环产生点。
			index = inString.indexOf(oldPattern, pos);
		}
		sbuf.append(inString.substring(pos));

		// remember to append any characters to the right of a match
		return sbuf.toString();
	}

	/**
	 * 将给出的字符串对象中与特定子串匹配的部分删除掉。
	 * 
	 * @param inString 给出的字符串对象。
	 * @param pattern 特定的子串。
	 * @return String 删除匹配子串后的字符串对象。
	 */
	public static String delete(String inString, String pattern) {
		return replace(inString, pattern, "");
	}

	/**
	 * 删除给出的字符串中包含的若干字符。
	 * 
	 * @param inString 给出的字符串对象。
	 * @param charsToDelete  需要从字符串对象中删除的字符集合。
	 * @return String 删除匹配字符后的字符串对象。
	 */
	public static String deleteAny(String inString, String charsToDelete) {
		if (inString == null || charsToDelete == null) {
			return inString;
		}
		StringBuffer out = new StringBuffer();
		for (int i = 0; i < inString.length(); i++) {
			char c = inString.charAt(i);
			if (charsToDelete.indexOf(c) == -1) {
				out.append(c);
			}
		}
		return out.toString();
	}


	//---------------------------------------------------------------------
	// Convenience methods for working with formatted Strings
	//---------------------------------------------------------------------

	/**
	 * 用单引号将给出的字符串括起来。<br>
	 * 如果给出的字符串为null，则返回null。
	 * 
	 * @param str 给出的字符串。
	 * @return 用单引号括起来后的字符串。
	 */
	public static String quote(String str) {
		return (str != null ? "'" + str + "'" : null);
	}

	/**
	 * 将字符串按照分隔符'.'分解，并返回最后部分。
	 * 
	 * @param qualifiedName 给出的字符串。
	 * @return 最后部分。
	 */
	public static String unqualify(String qualifiedName) {
		return unqualify(qualifiedName, '.');
	}

	/**
	 * 将字符串按照特定的分隔符分解，并返回最后部分。<br>
	 * 例如：字符串"this:name:is:qualified" 返回 "qualified"，分隔符为':'。
	 * 
	 * @param qualifiedName 给出的字符串。
	 * @param separator 分隔符。
	 * @return 最后部分。
	 */
	public static String unqualify(String qualifiedName, char separator) {
		return qualifiedName.substring(qualifiedName.lastIndexOf(separator) + 1);
	}

	/**
	 * 将字符串的首字母变为大写，其他字符不变。
	 * 
	 * @param str 给出的字符串，可以为null。
	 * @return String 变换后的字符串。
	 */
	public static String capitalize(String str) {
		return changeFirstCharacterCase(str, true);
	}

	/**
	 * 将字符串的首字母变为小写，其他字符不变。
	 * 
	 * @param str 给出的字符串，可以为null。
	 * @return String 变换后的字符串。
	 */
	public static String uncapitalize(String str) {
		return changeFirstCharacterCase(str, false);
	}

	/**
	 * 改变字符串首字母的大小写，其他字符不变。
	 * 
	 * @param str 给出的字符串，可以为null。
	 * @param capitalize 大小写。
	 * @return String 变换后的字符串。
	 */
	private static String changeFirstCharacterCase(String str, boolean capitalize) {
		if (str == null || str.length() == 0) {
			return str;
		}
		StringBuffer buf = new StringBuffer(str.length());
		if (capitalize) {
			buf.append(Character.toUpperCase(str.charAt(0)));
		}
		else {
			buf.append(Character.toLowerCase(str.charAt(0)));
		}
		buf.append(str.substring(1));
		return buf.toString();
	}
	
	/**
	 * 从路径字符串中分解出路径名称部分。<br>
	 * 例如："mypath/myfile.txt" -> "mypath"。
	 * 
	 * @param path 路径字符串，可以为null。
	 * @return String 分解出的路径名称部分，有可能为null。
	 */
	public static String getPathname(String path) {
		if (path == null) {
			return null;
		}
		int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
		return (separatorIndex != -1 ? path.substring(0, separatorIndex) : path);
	}

	/**
	 * 从路径字符串中分解出文件名称部分。<br>
	 * 例如："mypath/myfile.txt" -> "myfile.txt"。
	 * 
	 * @param path 路径字符串，可以为null。
	 * @return String 分解出的文件名称部分，有可能为null。
	 */
	public static String getFilename(String path) {
		if (path == null) {
			return null;
		}
		int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
		return (separatorIndex != -1 ? path.substring(separatorIndex + 1) : path);
	}
	
	/**
	 * 从路径字符串中分解出文件名称的名称部分。<br>
	 * 例如："mypath/myfile.txt" -> "myfile"。
	 * 
	 * @param path 路径字符串，可以为null。
	 * @return String 分解出的文件名称部分，有可能为null。
	 */
	public static String getFilenameName(String path) {
		if (path == null) {
			return null;
		}
		int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
		String filename = (separatorIndex != -1 ? path.substring(separatorIndex + 1) : path);
		int sepIndex = filename.lastIndexOf(EXTENSION_SEPARATOR);
		return (sepIndex != -1 ? filename.substring(0, sepIndex) : filename);
	}

	/**
	 * 从路径字符串中分解出文件后缀部分。<br>
	 * 例如："mypath/myfile.txt" -> "txt"。
	 * 
	 * @param path 路径字符串，可以为null。
	 * @return String 分解出的文件后缀部分，有可能为null。
	 */
	public static String getFilenameExtension(String path) {
		if (path == null) {
			return null;
		}
		int sepIndex = path.lastIndexOf(EXTENSION_SEPARATOR);
		return (sepIndex != -1 ? path.substring(sepIndex + 1) : null);
	}

	/**
	 * 去除路径字符串中的文件后缀部分。<br>
	 * 例如："mypath/myfile.txt" -> "mypath/myfile"。
	 * 
	 * @param path 路径字符串，可以为null。
	 * @return String 去除文件后缀部分后的字符串，有可能为null。
	 */
	public static String stripFilenameExtension(String path) {
		if (path == null) {
			return null;
		}
		int sepIndex = path.lastIndexOf(EXTENSION_SEPARATOR);
		return (sepIndex != -1 ? path.substring(0, sepIndex) : path);
	}

	/**
	 * 在路径字符串中添加相对路径。
	 * 
	 * @param path 路径字符串。
	 * @param relativePath 要添加的相对路径。
	 * @return String 添加相对路径后的路径字符串。
	 */
	public static String applyRelativePath(String path, String relativePath) {
		int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
		if (separatorIndex != -1) {
			String newPath = path.substring(0, separatorIndex);
			if (!relativePath.startsWith(FOLDER_SEPARATOR)) {
				newPath += FOLDER_SEPARATOR;
			}
			return newPath + relativePath;
		}
		else {
			return relativePath;
		}
	}

	/**
	 * 清洗路径字符串中的'..'和'.'，得到完整的路径字符串。<br>
	 * 清洗后的路径字符串可以用于有效的比较。<br>
	 * 路径字符串中的windows分隔符"\\"将被替换成简单分隔符"/"。
	 *
	 * @param path 需要清洗的路径字符串。
	 * @return String 清洗后的路径字符串。
	 */
	public static String cleanPath(String path) {
		String pathToUse = replace(path, WINDOWS_FOLDER_SEPARATOR, FOLDER_SEPARATOR);

		// Strip prefix from path to analyze, to not treat it as part of the
		// first path element. This is necessary to correctly parse paths like
		// "file:core/../core/io/Resource.class", where the ".." should just
		// strip the first "core" directory while keeping the "file:" prefix.
		int prefixIndex = pathToUse.indexOf(":");
		String prefix = "";
		if (prefixIndex != -1) {
			prefix = pathToUse.substring(0, prefixIndex + 1);
			pathToUse = pathToUse.substring(prefixIndex + 1);
		}

		String[] pathArray = delimitedListToStringArray(pathToUse, FOLDER_SEPARATOR);
		List<String> pathElements = new LinkedList<String>();
		int tops = 0;

		for (int i = pathArray.length - 1; i >= 0; i--) {
			if (CURRENT_PATH.equals(pathArray[i])) {
				// Points to current directory - drop it.
			}
			else if (TOP_PATH.equals(pathArray[i])) {
				// Registering top path found.
				tops++;
			}
			else {
				if (tops > 0) {
					// Merging path element with corresponding to top path.
					tops--;
				}
				else {
					// Normal path element found.
					pathElements.add(0, pathArray[i]);
				}
			}
		}

		// Remaining top paths need to be retained.
		for (int i = 0; i < tops; i++) {
			pathElements.add(0, TOP_PATH);
		}

		return prefix + collectionToDelimitedString(pathElements, FOLDER_SEPARATOR);
	}

	/**
	 * 比较两个路径字符串是否相等，比较之前分别清洗两个路径字符串。
	 * 
	 * @param path1 要比较的第一个路径字符串。
	 * @param path2 要比较的第二个路径字符串。
	 * @return boolean 两个路径字符串是否相等。
	 */
	public static boolean pathEquals(String path1, String path2) {
		return cleanPath(path1).equals(cleanPath(path2));
	}

	/**
	 * 将一个locale string变换成java.util.Locale对象。<br>
	 * 这是和Locales的toString方法相反的过程。
	 * 
	 * @param localeString locale string。
	 * @return Locale Locale对象实例。
	 */
	public static Locale parseLocaleString(String localeString) {
		String[] parts = tokenizeToStringArray(localeString, "_ ", false, false);
		String language = (parts.length > 0 ? parts[0] : "");
		String country = (parts.length > 1 ? parts[1] : "");
		String variant = (parts.length > 2 ? parts[2] : "");
		return (language.length() > 0 ? new Locale(language, country, variant) : null);
	}


	//---------------------------------------------------------------------
	// 字符串 数组
	//---------------------------------------------------------------------



	/**
	 * 将一个字符串集合对象变换为字符串数组对象。
	 * 
	 * @param collection 字符串集合对象，可以为null。
	 * @return String[] 字符串数组对象，有可能为null。
	 */
	public static String[] toStringArray(Collection<String> collection) {
		if (collection == null) {
			return null;
		}
		return (String[]) collection.toArray(new String[collection.size()]);
	}

	/**
	 * 用特定的分隔符拆分给出的字符串，按照分隔符第一次出现的位置将字符串拆成前后两个子串。<br>
	 * 两个子串均不含分隔符。<br>
	 * 如果给出的字符串中不包含分隔符，则返回null。
	 * 
	 * @param toSplit 给出的字符串，可以为null。
	 * @param delimiter 分隔符，可以为null。
	 * @return 拥有两个元素的字符串数组，第一个元素为分隔符前的子串，第二个元素为分隔符后的子串。有可能为null。
	 */
	public static String[] split(String toSplit, String delimiter) {
		if (!hasLength(toSplit) || !hasLength(delimiter)) {
			return null;
		}
		int offset = toSplit.indexOf(delimiter);
		if (offset < 0) {
			return null;
		}
		String beforeDelimiter = toSplit.substring(0, offset);
		String afterDelimiter = toSplit.substring(offset + delimiter.length());
		return new String[] {beforeDelimiter, afterDelimiter};
	}

	/**
	 * 将用字符串数组存储的<key-value>表达式变换成Properties对象实例。<br>
	 * 字符串数组中的每个元素都是以特定分隔符隔开的<key-value>表达式，分隔符左边是关键字，右边是值。<br>
	 * 
	 * @param array 给出的字符串数组，可以为null。
	 * @param delimiter <key-value>对的分隔符，可以为null。
	 * @return Properties Properties对象实例，可以为null。
	 */
	public static Properties splitArrayElementsIntoProperties(String[] array, String delimiter) {
		return splitArrayElementsIntoProperties(array, delimiter, null);
	}

	/**
	 * 将用字符串数组存储的<key-value>表达式变换成Properties对象实例。<br>
	 * 字符串数组中的每个元素都是以特定分隔符隔开的<key-value>表达式，分隔符左边是关键字，右边是值。<br>
	 * 不论是关键字还是值，在加入Properties对象之前都可以删除给定的字符集合（例如空格）。
	 * 
	 * @param array 给出的字符串数组，可以为null。
	 * @param delimiter <key-value>对的分隔符，可以为null。
	 * @param charsToDelete 需要删除的字符集合，可以为null。
	 * @return Properties Properties对象实例，可以为null。
	 */
	public static Properties splitArrayElementsIntoProperties(
			String[] array, String delimiter, String charsToDelete) {

		if (array == null || array.length == 0) {
			return null;
		}

		Properties result = new Properties();
		for (int i = 0; i < array.length; i++) {
			String element = array[i];
			if (charsToDelete != null) {
				element = deleteAny(array[i], charsToDelete);
			}
			String[] splittedElement = split(element, delimiter);
			if (splittedElement == null) {
				continue;
			}
			result.setProperty(splittedElement[0].trim(), splittedElement[1].trim());
		}
		return result;
	}

	/**
	 * 将给出的字符串用特定的分隔符字符串中的字符拆分成字符串数组。<br>
	 * 使用StringTokenizer类。<br>
	 * trim每个拆分后的结果字符串，忽略空的拆分结果字符串。
	 * 
	 * @param str 给出的字符串。
	 * @param delimiters 分隔符字符集合，其中的每个字符都是分隔符。
	 * @return String[] 拆分后的字符串数组。
	 * @see java.util.StringTokenizer
	 * @see java.lang.String#trim
	 * @see #delimitedListToStringArray
	 */
	public static String[] tokenizeToStringArray(String str, String delimiters) {
		return tokenizeToStringArray(str, delimiters, true, true);
	}

	/**
	 * 将给出的字符串用特定的分隔符字符串中的字符拆分成字符串数组。<br>
	 * 使用StringTokenizer类。
	 * 
	 * @param str 给出的字符串。
	 * @param delimiters 分隔符字符集合，其中的每个字符都是分隔符。
	 * @param trimTokens 是否需要trim拆分后的结果。
	 * @param ignoreEmptyTokens 是否忽略空的拆分结果。
	 * @return String[] 拆分后的字符串数组。
	 * @see java.util.StringTokenizer
	 * @see java.lang.String#trim
	 * @see #delimitedListToStringArray
	 */
	public static String[] tokenizeToStringArray(
			String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {

		StringTokenizer st = new StringTokenizer(str, delimiters);
		List<String> tokens = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (trimTokens) {
				token = token.trim();
			}
			if (!ignoreEmptyTokens || token.length() > 0) {
				tokens.add(token);
			}
		}
		return toStringArray(tokens);
	}

	/**
	 * 将给出的字符串用特定的分隔符拆分成字符串数组。<br>
	 * <ul>
	 * <li>如果给出的字符串为null，则返回String[0]。</li>
	 * <li>如果分隔符为null，则返回以源字符串为唯一元素的字符串数组。</li>
	 * <li>如果分隔符为""，则将源字符串按照每个字符拆分成字符串数组。</li>
	 * <li>否则，用分隔符拆分源字符串。</li>
	 * </ul>
	 * 
	 * @param str 给出的字符串，可以为null。
	 * @param delimiter 分隔符可以为null和""。
	 * @return 拆分后的字符串数组。
	 * @see #tokenizeToStringArray
	 */
	public static String[] delimitedListToStringArray(String str, String delimiter) {
		if (str == null) {
			return new String[0];
		}
		if (delimiter == null) {
			return new String[] {str};
		}

		List<String> result = new ArrayList<String>();
		if ("".equals(delimiter)) {
			for (int i = 0; i < str.length(); i++) {
				result.add(str.substring(i, i + 1));
			}
		}
		else {
			int pos = 0;
			int delPos = 0;
			while ((delPos = str.indexOf(delimiter, pos)) != -1) {
				result.add(str.substring(pos, delPos));
				pos = delPos + delimiter.length();
			}
			if (str.length() > 0 && pos <= str.length()) {
				// Add rest of String, but not in case of empty input.
				result.add(str.substring(pos));
			}
		}
		return toStringArray(result);
	}

	/**
	 * 将以逗号为分隔符的字符串拆分成字符串数组。
	 * 
	 * @param str 以逗号分隔的字符串，可以为null。
	 * @return String[] 拆分后的字符串数组。
	 */
	public static String[] commaDelimitedListToStringArray(String str) {
		return delimitedListToStringArray(str, ",");
	}

	/**
	 * 将以逗号为分隔符的字符串拆分成集合对象（Set&lt;String&gt;）<br>
	 * 将忽略重复的元素。
	 * 
	 * @param str 以逗号为分隔符的字符串，可以为null。
	 * @return Set&lt;String&gt; 集合对象实例。
	 */
	public static Set<String> commaDelimitedListToSet(String str) {
		Set<String> set = new TreeSet<String>();
		String[] tokens = commaDelimitedListToStringArray(str);
		for (int i = 0; i < tokens.length; i++) {
			set.add(tokens[i]);
		}
		return set;
	}

	/**
	 * 将对象数组组装成以特定分隔符分隔的字符串。<br>
	 * 使用toString方法获得字符串。
	 * 
	 * @param arr 对象数组。
	 * @param delim 分隔符。
	 * @return String 组装后的字符串。
	 */
	public static String arrayToDelimitedString(Object[] arr, String delim) {
		if (arr == null) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			if (i > 0) {
				sb.append(delim);
			}
			sb.append(arr[i]);
		}
		return sb.toString();
	}

	/**
	 * 将集合对象的每个元素组装成以特定分隔符分隔的字符串，每个元素可以加上特定的前缀和后缀字符串。<br>
	 * 使用toString方法获得字符串。
	 * 
	 * @param coll 集合对象实例，可以为null。
	 * @param delim 分隔符。
	 * @param prefix 前缀字符串。
	 * @param suffix 后缀字符串。
	 * @return String 组装后的字符串，一定不是null。
	 */
	@SuppressWarnings({ "rawtypes" })
	public static String collectionToDelimitedString(Collection coll, String delim, String prefix, String suffix) {
		if (coll == null) {
			return "";
		}

		StringBuffer sb = new StringBuffer();
		Iterator it = coll.iterator();
		int i = 0;
		while (it.hasNext()) {
			if (i > 0) {
				sb.append(delim);
			}
			sb.append(prefix).append(it.next()).append(suffix);
			i++;
		}
		return sb.toString();
	}

	/**
	 * 将集合对象的每个元素组装成以特定分隔符分隔的字符串。<br>
	 * 使用toString方法获得字符串。
	 * 
	 * @param coll 集合对象实例，可以为null。
	 * @param delim 分隔符。
	 * @return String 组装后的字符串，一定不是null。
	 */
	public static String collectionToDelimitedString(Collection<String> coll, String delim) {
		return collectionToDelimitedString(coll, delim, "", "");
	}

	/**
	 * 将字符串数组组装成以逗号（","）为分隔符的字符串。
	 * 
	 * @param arr 字符串数组。
	 * @return 组装后的字符串。
	 */
	public static String arrayToCommaDelimitedString(Object[] arr) {
		return arrayToDelimitedString(arr, ",");
	}

	/**
	 * 将字符串集合对象组装成以逗号（","）为分隔符的字符串。
	 * 
	 * @param coll 字符串集合对象。
	 * @return 组装后的字符串。
	 */
	public static String collectionToCommaDelimitedString(Collection<String> coll) {
		return collectionToDelimitedString(coll, ",");
	}
	
	/**
	 * <p>将int值转换成16进制字符串，并补齐到指定长度。</p>
	 *
	 * @param value int值。
	 * @param length 指定长度。
	 * @return String 16进制字符串。
	 */
	public static String toHexString(int value, int length) {
		String str  = Integer.toHexString(value).toUpperCase();
		if (length > 0) {
			while(length > str.length()) {
				str = "0" + str;
			}
		}
		return str;
	}
	
	/**
	 * <p>将一个byte类型的数转换成十六进制的ASCII表示。</p>
	 *
	 * @param ib byte值。
	 * @return String 十六进制字符串。
	 */
	public static String byteHEX(byte ib) {
        char[] Digit = { '0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F' };
        char [] ob = new char[2];
        ob[0] = Digit[(ib >>> 4) & 0X0F];
        ob[1] = Digit[ib & 0X0F];
        return new String(ob);
	}
	
	/**
	 * <p>将一个byte类型的数组转换成十六进制的ASCII表示。</p>
	 *
	 * @param ibArray byte值数组。
	 * @return String 十六进制字符串。
	 */
	public static String bytesHEX(byte[] ibArray) {
		if (ibArray == null || ibArray.length <= 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (byte ib : ibArray) {
			sb.append(byteHEX(ib));
		}
		return sb.toString();
	}

	/**
	 * <p>将一个十六进制ASCII表示的字符串转换成一个byte类型数据。</p>
	 *
	 * @param str 十六进制字符串。
	 * @return byte byte值。
	 */
	public static byte hexByte(String str) {
		return Integer.decode("0x" + str).byteValue();
	}
	
	/**
	 * <p>将一个十六进制ASCII表示的字符串转换成一个byte类型数组。</p>
	 *
	 * @param str 十六进制字符串。
	 * @return byte byte值。
	 */
	public static byte[] hexBytes(String str) {
		byte[] ob = new byte[str.length() / 2];
		for (int i=0; i<ob.length; i++) {
			ob[i] = hexByte(str.substring(i*2, (i+1)*2));
		}
		return ob;
	}
	
	/**
	 *	<p>格式化字符串长度，超出部分显示省略号。<p>
	 * @param str 传入的字符串
	 * @return 返回String
	 */
	public static String splitString(String str, int len){
        //如果长度比需要的长度n小,返回原字符串
		String temp = null;
        if (str.length() <= len){
            return str;
        }else{
	        temp = str.substring(0, len);
	        return (temp + ".....");
        }
    }
	
	/**
	 * <p>格式化字符串长度，超出部分显示指定字符。<p>
	 * @param str 传入的字符串
	 * @param len 
	 * @param rep 
	 * @return
	 */
	public static String splitString(String str, int len, String rep){
        //如果长度比需要的长度n小,返回原字符串
		String temp = null;
        if (str.length() <= len){
            return str;
        }else{
	        temp = str.substring(0, len);
	        return (temp + rep);
        }
    } 
	
	/** 
	 * <p>说明：过滤字符串中json的敏感字符。</p>
	 * @param s
	 * @return String
	 */ 
	public static String JsonF(String s) {
		if(s==null) return "";
		StringBuilder sb = new StringBuilder(s.length() + 20);
		//sb.append('\"');
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case '\"':
				sb.append("\\\"");
				break;
			case '\\':
				sb.append("\\\\");
				break;
			case '/':
				sb.append("\\/");
				break;
			case '\'':
				sb.append("\\\'");
				break;	
			case '\b':
				sb.append("\\b");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case '\t':
				sb.append("\\t");
				break;
			default:
				sb.append(c);
			}
		}
		//sb.append('\"');
		return sb.toString();
	}

	/**
	 * 检查给出的字符串对象是否有长度。<br>
	 * 
	 * <pre>
	 * StringUtils.hasLength(null) = false
	 * StringUtils.hasLength("") = false
	 * StringUtils.hasLength(" ") = true
	 * StringUtils.hasLength("Hello") = true
	 * </pre>
	 * 
	 * @param str 需要检查的字符串对象，可以为null。
	 * @return boolean 字符串是否为空。
	 */
	public static boolean isEmpty(String str){
		return !hasLength(str);
	}
	
	/**
	 * 校验邮箱是否合规
	 * 
	 * @param email
	 *            邮箱
	 * @return true 合规 false 不合规
	 */
	public static boolean isEmailFormat(String email) {

		Pattern pattern = Pattern
				.compile("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}
	/** 
	* 方法: getNumberFromString <br>
	* 描述: 从字符串中获取数字<br>
	* @param str 如：AB000011
	* @return
	*/ 
	public static Long getNumberFromString(String str){
		str=str.replaceAll("\\D", "").replace("_+", "");
		return Long.parseLong(str);
	}
	/** 
	* 方法: getNumberStrNext <br>
	* 描述: 根据数字字符串返回它的根据加减之后的数字字符串 <br>
	* @param str 如：AB000011
	* @param num 如：1（表示加一），-2（表示减二）
	* @return
	*/ 
	public static String getNumberStrPlus(String str,int num){
		//取出字符字符串
		StringBuffer letterStr=new StringBuffer();
		for(char c:str.toCharArray()){
			if(Character.isLetter(c)){
				letterStr.append(c);	
			}
		}
		Long strNum=getNumberFromString(str);
		if(strNum<=0)  return str;
		else{
			strNum=strNum+num;
			String numStr=strNum.toString();
			//需要0占位符的个数
			int zeroNum=str.length()-letterStr.toString().length()-numStr.length();
			StringBuffer zeroStr=new  StringBuffer();
			for(int i=0;i<zeroNum;i++){
				zeroStr.append("0");
			}
			return   letterStr.toString()+zeroStr.toString()+numStr;
		}
	}
	
	public static String generateUUID(){
		String uuid=UUID.randomUUID().toString();
		return uuid.replace("-", "");
	}
}
