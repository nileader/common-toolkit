package common.toolkit.java.util.collection;

import static common.toolkit.java.constant.EmptyObjectConstant.EMPTY_STRING;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import common.toolkit.java.constant.SymbolConstant;
import common.toolkit.java.util.StringUtil;
import common.toolkit.java.util.number.IntegerUtil;
import common.toolkit.java.util.number.NumberUtil;

/**
 * 类说明: List相关工具类
 * @author 银时 yinshi.nc@taobao.com
 */
public class ListUtil {

	/**
	 * 把一个字符串转换成List
	 * @param @param originalStr abc, def,helloword,myname
	 * @param @param splitStr ,
	 * @return List<String>
	 */
	public static List< String > parseList( String originalStr, String splitStr ) {
		List< String > list = new ArrayList< String >();
		if ( StringUtil.isBlank( originalStr ) || StringUtil.isBlank( splitStr ) )
			return list;
		return ArrayUtil.toArrayList( originalStr.split( splitStr ) );
	}

	/**
	 * Return the sublist of list.<br>
	 * Note:No worry of java.lang.StringIndexOutOfBoundsException
	 * @param list	original
	 * @param fromIndex
	 * @param size
	 * @return
	 */
	public static <T> List< T > subList( List< T > list, int fromIndex, int size ) {
		
		if( CollectionUtil.isBlank( list ) )
			return CollectionUtil.emptyList();
		if( NumberUtil.isNegative( fromIndex, size ) ){
			return CollectionUtil.emptyList();
		}
		
		int endIndex = IntegerUtil.maxIfTooBig( fromIndex + size, list.size() );
		return list.subList( fromIndex, endIndex );
	}

	/**
	 * Convert Collection< String > to String, 并且使用split来分隔，不含空格。
	 * @param split 需要分隔的字符
	 * @return String
	 */
	public static String toString( Collection< String > collection, String split ) {

		if ( null == collection || collection.isEmpty() ) {
			return EMPTY_STRING;
		}
		String str = EMPTY_STRING;
		for ( String _str : collection ) {
			str += _str + split;
		}
		str = StringUtil.replaceLast( str, split, EMPTY_STRING );
		return str;
	}

	/**
	 * Convert Collection< String > to String, 并且使用 ,来分隔，不含空格。
	 * @param split 需要分隔的字符
	 * @return String
	 */
	public static String toString( Collection< String > collection ) {
		return toString( collection, SymbolConstant.COMMA );
	}

}