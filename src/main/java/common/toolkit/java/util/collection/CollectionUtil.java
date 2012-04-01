package common.toolkit.java.util.collection;

import static common.toolkit.java.constant.EmptyObjectConstant.EMPTY_STRING;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import common.toolkit.java.constant.SymbolConstant;
import common.toolkit.java.util.ObjectUtil;
import common.toolkit.java.util.StringUtil;

/**
 * 集合类的公共方法
 * @author 银时：yinshi.nc / yinshi.nc@taobao.com
 * @Date Jan 11, 2012
 */
public class CollectionUtil extends ObjectUtil {

	/**
	 * Check if collection contains element.
	 * @return true: null == collection || 0 == collection.size()
	 * @return false: null != collection && 0 != collection.size()
	 */
	public static boolean isBlank( Collection< ? extends Object > collection ) {
		if ( null == collection || 0 == collection.size() ) {
			return true;
		}
		return false;
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

	public static final <K, V> Map< K, V > emptyMap() {
		return Collections.emptyMap();
	}

	public static final <T> Set< T > emptySet() {
		return Collections.emptySet();
	}

	public static final <T> List< T > emptyList() {
		return Collections.emptyList();
	}

}
