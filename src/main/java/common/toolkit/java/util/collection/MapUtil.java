package common.toolkit.java.util.collection;

import java.util.Map;

import common.toolkit.java.util.ObjectUtil;

/**
 * Map相关的工具类
 * @author 银时：yinshi.nc / yinshi.nc@taobao.com
 * @Date Jan 8, 2012
 */
public class MapUtil extends CollectionUtil{

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
	public static boolean isBlank( Map< ?,? > map ){
		if( 0 == MapUtil.size( map ) )
			return true;
		return false;
	}
	
	
	

}
