package common.toolkit.java.util;

import static common.toolkit.java.constant.EmptyObjectConstant.EMPTY_STRING;
import static common.toolkit.java.constant.RegExpConstant.PATTERN_OF_CONTAINS_IP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.toolkit.java.constant.BaseConstant;
import common.toolkit.java.constant.EmptyObjectConstant;
import common.toolkit.java.exception.NotExpectedFormatedException;

/**
 * Description: String Utils
 * @author   nileader / nileader@gmail.com
 * @Date	 Feb 10, 2012
 */
public class StringUtil {
	
	/**
	 * Check String is equals. targetStr compare with compareStrArray, and
	 * return true if equals one or more
	 * 
	 * @param targetStr 待比较的字符串
	 * @param compareStrArray 要比较的一个或多个字符串标准
	 * @return
	 */
	public static boolean containsIgnoreCase( final String originalStr, final CharSequence targetStr ) {

		if ( null == originalStr ){
			return false;
		}

		String originalStrCaps = originalStr.toUpperCase();
		String targetStrCaps = targetStr.toString().toUpperCase();
		return originalStrCaps.contains( targetStrCaps );
	}
	
	/**
	 * Check if string contains ip,此方法不能完全判断ip是否合法
	 * 
	 * @return
	 */
	public static boolean containsIp( String originalStr ) {

		if ( StringUtil.isBlank( originalStr ) )
			return false;

		Matcher match = PATTERN_OF_CONTAINS_IP.matcher( originalStr );
		return match.matches();
	}
	
	/**
	 * Description: Remove {_, -, @, $, #, /, &} in string and make letter after this uppercase.<br>
	 * e.g. ni_lea-der@gmail./com -> niLeaDerGmail.Com
	 * @param @param inputString
	 * @param @param firstCharacterUppercase The first letter is need uppercase.
	 * @return String 
	 * @throws
	 */
	public static String convertToCamelCaseString( String inputString, boolean firstCharacterUppercase ) {
		if ( null == inputString ) {
			return null;
		}
		StringBuilder sb = new StringBuilder();

		boolean nextUpperCase = false;
		for ( int i = 0; i < inputString.length(); i++ ) {
			char c = inputString.charAt( i );

			switch (c) {
			case '_':
			case '-':
			case '@':
			case '$':
			case '#':
			case ' ':
			case '/':
			case '&':
				if ( sb.length() > 0 ) {
					nextUpperCase = true;
				}
				break;

			default:
				if ( nextUpperCase ) {
					sb.append( Character.toUpperCase( c ) );
					nextUpperCase = false;
				} else {
					sb.append( c );
				}
				break;
			}
		}

		if ( firstCharacterUppercase ) {
			sb.setCharAt( 0, Character.toUpperCase( sb.charAt( 0 ) ) );
		} else {
			sb.setCharAt( 0, Character.toLowerCase( sb.charAt( 0 ) ) );
		}

		return sb.toString();
	}
	
	
	
	/**
	 * Return Default if originalStr is empty.
	 * @param originalStr	待确认值
	 * @param defaultStr	默认值
	 * @return				如果originalStr为空，那么就返回defaultStr
	 */
	public static String defaultIfBlank( String originalStr, String defaultStr ) {
		if ( StringUtil.isBlank( originalStr ) ) {
			return defaultStr;
		}
		Collections.emptyList();
		return originalStr;
	}
	
	/**
	 * Check String is equals Ignore Case. targetStr compare with compareStrArray, and
	 * return true if equals all
	 * 
	 * @param targetStr 待比较的字符串
	 * @param compareStrArray 要比较的一个或多个字符串标准
	 * @return true if targetStr same with every string in compareStrArray
	 */
	public static boolean equalsIgnoreCaseAll( String targetStr, String... compareStrArray ) {

		if ( StringUtil.isBlank( targetStr ) || null == compareStrArray || 0 == compareStrArray.length ) {
			return false;
		}
		for ( int i = 0; i < compareStrArray.length; i++ ) {
			if ( !targetStr.equalsIgnoreCase( compareStrArray[i] ) ) {
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * Check String is equals. targetStr compare with compareStrArray, and
	 * return true if equals one or more
	 * 
	 * @param targetStr 待比较的字符串
	 * @param compareStrArray 要比较的一个或多个字符串标准
	 * @return true if targetStr same with string in compareStrArray one at
	 *         least
	 */
	public static boolean equalsIgnoreCaseOne( String targetStr, String... compareStrArray ) {

		if ( StringUtil.isBlank( targetStr ) || null == compareStrArray || 0 == compareStrArray.length ) {
			return false;
		}
		for ( int i = 0; i < compareStrArray.length; i++ ) {
			if ( targetStr.equalsIgnoreCase( compareStrArray[i] ) ) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * 通过正则表达方式，找出一个字符串中所有指定的子串
	 * 
	 * @param @param originalStr 字符串
	 * @param @param regex 待查找子串的正则表达式
	 * @return List<String> 子串集合
	 * 
	 *         <pre>
	 * 	对 /1.1.1.1:sid=0x2337c7074dofj02e,37775[1](queued=0,recved=6,sent=7,sid=0x2337c7074f1102e,sdlfjle,dsfe的结果是：
	 * [sid=0x2337c7074dofj02e, , sid=0x2337c7074f1102e, ]
	 * </pre>
	 */
	public static List< String > findAllByRegex( String originalStr, String regex ) {

		if ( StringUtil.isBlank( originalStr ) || StringUtil.isBlank( regex ) )
			return null;

		List< String > targetStrList = new ArrayList< String >();
		final Pattern patternOfTargetStr = Pattern.compile( regex, Pattern.CANON_EQ );
		final Matcher matcherOfTargetStr = patternOfTargetStr.matcher( originalStr );
		/** 开始解析 */
		while ( matcherOfTargetStr.find() ) {
			targetStrList.add( StringUtil.trimToEmpty( matcherOfTargetStr.group() ) );
		}
		return targetStrList;
	}


	/**
	 * 通过正则表达方式，找出一个字符串中第一个指定的子串
	 * 
	 * @param @param originalStr 字符串
	 * @param @param regex 待查找子串的正则表达式
	 * @return List<String> 子串集合
	 * 
	 *         <pre>
	 * 	对 /1.1.1.1:sid=0x2337c7074dofj02e,37775[1](queued=0,recved=6,sent=7,sid=0x2337c7074f1102e,sdlfjle,dsfe的结果是：
	 * sid=0x2337c7074dofj02e,
	 * </pre>
	 */
	public static String findFirstByRegex( String originalStr, String regex ) {

		if ( StringUtil.isBlank( originalStr ) || StringUtil.isBlank( regex ) )
			return EMPTY_STRING;

		final Pattern patternOfTargetStr = Pattern.compile( regex, Pattern.CANON_EQ );
		final Matcher matcherOfTargetStr = patternOfTargetStr.matcher( originalStr );
		/** 开始解析 */
		if ( matcherOfTargetStr.find() ) {
			return StringUtil.trimToEmpty( matcherOfTargetStr.group() );
		}
		return EMPTY_STRING;
	}
	
	/**
	 * 生成空白行.
	 * 
	 * @param lines 行数
	 */
	public static String generateLineBlank( int lines ) {
		StringBuilder sb = new StringBuilder();

		for ( int i = 0; i < lines; i++ ) {
			sb.append( "\n" );
		}

		return sb.toString();
	}
	
	
	/**
	 * make first letter lower case for str
	 * 
	 * @return Same letter, but the first letter is lower case.
	 */
	public static String makeFirstLetterLowerCase( String str ) {
		String firstLetter = str.substring( 0, 1 );
		return firstLetter.toLowerCase() + str.substring( 1, str.length() );
	}
	
	/***
	 * check if orginalStr is null or empty. <br>
	 * If have more than one originalStr, use isBlank(String...
	 * originalStrArray)
	 * 
	 * @param originalStr	待确认值
	 * @return true or false;
	 */
	public static boolean isBlank( String originalStr ) {
		if ( null == originalStr || trimToEmpty( originalStr ).isEmpty() ){
			return true;
		}
		return false;
	}
	
	/***
	 * check if orginalStr is null or empty
	 * 
	 * @param String ... originalStrArray
	 * @return true if have one blank at least.
	 */
	public static boolean isBlank( String... originalStrArray ) {

		if ( null == originalStrArray || 0 == originalStrArray.length )
			return true;
		for ( int i = 0; i < originalStrArray.length; i++ ) {
			if ( isBlank( originalStrArray[i] ) )
				return true;
		}
		return false;
	}
	
	/**
	 * check the originalStr is contain the whitespace
	 * 
	 * @param originalStr
	 * @return true if contain whitespace
	 */
	public static boolean isContainWhitespace( String originalStr ) {

		if ( StringUtil.isBlank( originalStr ) ) {
			return true;
		}
		int strLen = originalStr.length();
		for ( int i = 0; i < strLen; i++ ) {
			char ch = originalStr.charAt( i );
			if ( Character.isWhitespace( ch ) ) {
				return true;
			}
		}
		return false;
	}
	

	/**
	 * Description: Replaces last substring of this string that matches the
	 * given regular expression with the given replacement.<br>
	 * Do not worry about null pointer
	 * @param @param regex
	 * @param @param replacement
	 * @return String
	 * @throws
	 */
	public static String replaceAll( String originalStr, String replacement, String regex ) {
		if( StringUtil.isBlank( regex ) )
			return originalStr;
		return StringUtil.trimToEmpty( originalStr ).replace( regex, replacement );
	}
	
	public static String replaceAll( String originalStr, String replacement, String... regexArray ) {
		
		if( 0 == regexArray.length )
			return originalStr;
		
		for( String regex : regexArray){
			originalStr = StringUtil.replaceAll( originalStr, replacement, regex );
		}
		
		return originalStr;
	}
	
	
	/**
	 * Description: Replaces last substring of this string that matches the
	 * given regular expression with the given replacement.
	 * 
	 * @param @param regex
	 * @param @param replacement
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String replaceLast( String originalStr, String regex, String replacement ) {

		if ( StringUtil.isBlank( originalStr ) )
			return EMPTY_STRING;

		int index = originalStr.lastIndexOf( regex );
		if ( -1 == index )
			return originalStr;

		// 先存储这个index之前的所有str
		String temp = originalStr.substring( 0, index );
		String temp2 = originalStr.substring( index, originalStr.length() );

		temp2 = temp2.replaceFirst( regex, replacement );

		originalStr = temp + temp2;

		return originalStr;
	}

	/**
	 * Description: Replaces all {n} placeholder use params
	 * 
	 * @param originalStr a string such as :
	 *            "select * from table where id={0}, name={1}, gender={3}"
	 * @param replacementParams real params: 1,yinshi.nc,male
	 * @note n start with 0
	 */
	public static String replaceSequenced( String originalStr, Object... replacementParams ) {

		if ( StringUtil.isBlank( originalStr ) )
			return EMPTY_STRING;
		if ( null == replacementParams || 0 == replacementParams.length )
			return originalStr;

		for ( int i = 0; i < replacementParams.length; i++ ) {
			String elementOfParams = replacementParams[i] + EmptyObjectConstant.EMPTY_STRING;
			if( StringUtil.trimToEmpty( elementOfParams ).equalsIgnoreCase( "null" ) )
				elementOfParams = EmptyObjectConstant.EMPTY_STRING;
			originalStr = originalStr.replace( "{" + i + "}", StringUtil.trimToEmpty( elementOfParams ) );
		}

		return originalStr;
	}
	
	
	/**
	 * 设置前缀，如果这个字符串已经是这个前缀了，那么就不作任何操作。
	 * TODO none test
	 * */
	public static String setPrefix( String originalStr, String prefix ) {
		originalStr = StringUtil.trimToEmpty( originalStr );
		prefix = StringUtil.trimToEmpty( prefix );
		if ( !originalStr.startsWith( prefix ) ) {
			originalStr = prefix + originalStr;
		}
		return originalStr;
	}
	
	
	
	/**
	 * 分割字符串，如果不是指定长度，抛出异常
	 * 
	 * @param originalStr 原字符串
	 * @param regex 分割符
	 * @param fixedLength 指定fixed长度
	 * @return 分割后的字符串数组
	 * @throws NotExpectedFormatedException
	 */
	public static String[] splitWithFixedLength( String originalStr, String regex, int fixedLength ) throws NotExpectedFormatedException {

		if ( StringUtil.isBlank( regex ) ) {
			return EmptyObjectConstant.EMPTY_STRING_ARRAY;
		}

		if ( StringUtil.isBlank( originalStr ) ) {
			if ( 0 == fixedLength )
				return EmptyObjectConstant.EMPTY_STRING_ARRAY;
			throw new NotExpectedFormatedException( "指定fixedLength为：" + fixedLength + ", 但是originalStr为空" );
		}

		String[] strArray = originalStr.split( regex );
		if ( fixedLength != strArray.length ) {
			throw new NotExpectedFormatedException( "指定fixedLength为：" + fixedLength + ", 但是originalStr为:" + originalStr );
		}
		return strArray;
	}

	/**
	 * 分割字符串，如果小于指定长度（>=leastLength ok），抛出异常
	 * 
	 * @param originalStr 原字符串
	 * @param regex 分割符
	 * @param leastLength 指定的最小least长度
	 * @return 分割后的字符串数组
	 * @throws NotExpectedFormatedException
	 */
	public static String[] splitWithLeastLength( String originalStr, String regex, int leastLength ) throws NotExpectedFormatedException {
		if ( StringUtil.isBlank( regex ) ) {
			return EmptyObjectConstant.EMPTY_STRING_ARRAY;
		}

		if ( StringUtil.isBlank( originalStr ) ) {
			if ( 0 == leastLength )
				return EmptyObjectConstant.EMPTY_STRING_ARRAY;
			throw new NotExpectedFormatedException( "指定leastLength为：" + leastLength + ", 但是originalStr为空" );
		}

		String[] strArray = originalStr.split( regex );
		if ( leastLength > strArray.length ) {
			throw new NotExpectedFormatedException( "指定leastLength为：" + leastLength + ", 但是originalStr为:" + originalStr );
		}
		return strArray;
	}

	/**
	 * 分割字符串，如果大于指定长度（<=leastLength ok），抛出异常
	 * 
	 * @param originalStr 原字符串
	 * @param regex 分割符
	 * @param maxLength 指定的最大max长度
	 * @return 分割后的字符串数组
	 * @throws NotExpectedFormatedException
	 */
	public static String[] splitWithMaxLength( String originalStr, String regex, int maxLength ) throws NotExpectedFormatedException {
		if ( StringUtil.isBlank( regex ) ) {
			return EmptyObjectConstant.EMPTY_STRING_ARRAY;
		}

		if ( StringUtil.isBlank( originalStr ) ) {
			return EmptyObjectConstant.EMPTY_STRING_ARRAY;
		}

		String[] strArray = originalStr.split( regex );
		if ( maxLength < strArray.length ) {
			throw new NotExpectedFormatedException( "指定maxLength为：" + maxLength + ", 但是originalStr为:" + originalStr );
		}
		return strArray;
	}
	
	
	/**
	 * 判断字符串是否超过指定长度，如何超过，添加指定后缀
	 * @param originalStr	"银时的"
	 * @param maxLength		2
	 * @param suffix		...
	 * @return			"银时..."
	 */
	public static String subStringIfTooLong( String originalStr, int maxLength, String suffix ) {
		if( StringUtil.isBlank( originalStr ) )
			return EmptyObjectConstant.EMPTY_STRING;
		if( maxLength < 0 )
			maxLength = 0;
		if( originalStr.length() > maxLength )
			return originalStr.substring( 0, maxLength ) + StringUtil.trimToEmpty( suffix );
		return originalStr;
	}
	
	/**
	 * toString()方法<br>
	 * Note:不会出现npe，不会返回null
	 * @param t
	 * @return
	 */
	public static <T> String toString( T t ){
		if( ObjectUtil.isBlank( t ) )
			return EmptyObjectConstant.EMPTY_STRING;
		return t.toString();
	}
	
	
	
	/**
	 * Returns a copy of the string, with leading and trailing whitespace
	 * omitted. Don't worry the NullPointerException. Will never return Null.
	 * 
	 * @param originalStr
	 * @return "" or String without empty str.
	 */
	public static String trimToEmpty( String originalStr ) {
		if ( null == originalStr || originalStr.isEmpty() )
			return EMPTY_STRING;
		if( originalStr.equals( BaseConstant.WORD_SEPARATOR ) )
			return originalStr;
		return originalStr.trim();
	}
	

}
