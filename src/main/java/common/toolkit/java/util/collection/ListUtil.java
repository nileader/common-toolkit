package common.toolkit.java.util.collection;


import static common.toolkit.java.constant.EmptyObjectConstant.EMPTY_STRING;

import java.util.ArrayList;
import java.util.List;

import common.toolkit.java.constant.SymbolConstant;
import common.toolkit.java.util.StringUtil;

/**
 * 类说明: List相关工具类
 * @author 银时 yinshi.nc@taobao.com
 */
public class ListUtil {

	/**
	 * 把一个字符串转换成List
	 * @param  @param originalStr   abc, def,helloword,myname
	 * @param  @param splitStr			,
	 * @return List<String>   
	 */
	public static List<String> parseList( String originalStr, String splitStr ){
		List<String> list = new ArrayList<String>();
		if( StringUtil.isBlank( originalStr ) || StringUtil.isBlank(splitStr) )
			return list;
		return ArrayUtil.toArrayList( originalStr.split( splitStr) );
	}
	
	/**
	 * Convert ArrayList<String> to String, 并且使用split来分隔，不含空格。
	 * @param split 需要分隔的字符
	 * @return String
	 */
	public static String toString( List<String> list, String split ){
		
		if( null == list || list.isEmpty() ){
			return EMPTY_STRING;
		}
		String str = EMPTY_STRING;
		for( String _str : list ){
			str += _str + split;
		}
		str = StringUtil.replaceLast(str, split, EMPTY_STRING );
		return str;
	}
	
	/**
	 * Convert ArrayList<String> to String, 并且使用 ,来分隔，不含空格。
	 * @param split 需要分隔的字符
	 * @return String
	 */
	public static String toString( List<String> list ){
		return toString( list, SymbolConstant.COMMA );
	}
	
	
	
}