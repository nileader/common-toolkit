package common.toolkit.java.util.collection;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import common.toolkit.java.util.ObjectUtil;

/**
 * Map相关的工具类
 * @author 银时：yinshi.nc / yinshi.nc@taobao.com
 * @Date Jan 8, 2012
 */
public class MapUtil extends CollectionUtil {

	/**
	 * 获取map的大小，放心，这个方法不会发生NPE
	 * @param map
	 * @return size of map(0 if null == map)
	 */
	public static int size( Map< ?, ? > map ) {
		if ( ObjectUtil.isBlank( map ) ) {
			return 0;
		} else
			return map.size();
	}

	/**
	 * map对象是否为空
	 * @param map if null == map || map.size == 0
	 */
	public static boolean isBlank( Map< ?, ? > map ) {
		if ( 0 == MapUtil.size( map ) )
			return true;
		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map sortByValue( Map map ) {
		List list = new LinkedList( map.entrySet() );
		Collections.sort( list, new Comparator() {

			public int compare( Object o1, Object o2 ) {
				return ( ( Comparable ) ( ( Map.Entry ) ( o2 ) ).getValue() ).compareTo( ( ( Map.Entry ) ( o1 ) ).getValue() );

			}
		} );
		Map result = new LinkedHashMap();

		for ( Iterator it = list.iterator(); it.hasNext(); ) {
			Map.Entry entry = ( Map.Entry ) it.next();
			result.put( entry.getKey(), entry.getValue() );
		}
		return result;
	}

}
