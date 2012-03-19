package common.toolkit.java.util.collection;

import java.util.LinkedHashSet;
import java.util.Set;

import common.toolkit.java.util.StringUtil;

/**
 * 类说明: List相关工具类
 * @author 银时 yinshi.nc@taobao.com
 */
public class SetUtil {

	/**
	 * parse the string to Set
	 * @param @param originalStr abc, def,helloword,myname
	 * @param @param splitStr ,
	 * @return Set<String>
	 */
	public static Set< String > parseSet( String originalStr, String splitStr ) {
		Set< String > set = new LinkedHashSet< String >();
		
		if ( StringUtil.isBlank( originalStr ) || StringUtil.isBlank( splitStr ) )
			return set;
		
		for( String str : originalStr.split( splitStr ) ){
			if( StringUtil.isBlank( str ) )
				continue;
			set.add( StringUtil.trimToEmpty( str ) );
		}
		return set;
	}

}