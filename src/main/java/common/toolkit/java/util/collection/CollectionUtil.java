package common.toolkit.java.util.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import common.toolkit.java.util.ObjectUtil;

/**
 * 集合类的公共方法
 * @author 银时：yinshi.nc / yinshi.nc@taobao.com
 * @Date Jan 11, 2012
 */
public class CollectionUtil extends ObjectUtil {

	/**
	 * Check if collection contains element.
	 * @return true:  null == collection || 0 == collection.size()
	 * @return false: null != collection && 0 != collection.size()
	 */
	public static boolean isBlank( Collection< ? extends Object > collection ) {
		if( null == collection || 0 == collection.size() ){
			return true;
		}
		return false;
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
