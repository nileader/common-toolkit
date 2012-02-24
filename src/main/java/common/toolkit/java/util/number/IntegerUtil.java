package common.toolkit.java.util.number;

import common.toolkit.java.util.StringUtil;

/**
 * Integer 工具类
 * @author   银时：yinshi.nc / yinshi.nc@taobao.com
 * @Date	 Jan 13, 2012
 */
public class IntegerUtil {

	
	/**
	 * 如果为0,则返回默认值
	 * @param originalInt
	 * @param defaultInt	默认Integer
	 * @return
	 */
	public static Integer defaultIfZero( Integer originalInt, Integer defaultInt ) {
		if ( 0 == originalInt ) {
			return defaultInt;
		}
		return originalInt;
	}
	
	/**
	 * 如果为0,则返回默认值
	 * @param originalInt
	 * @param defaultInt	默认Integer
	 * @return
	 */
	public static Integer defaultIfError( String originalStr, Integer defaultInt ) {

		try {
			return Integer.parseInt( StringUtil.trimToEmpty( originalStr ) );
		} catch ( Exception e ) {
			return defaultInt;
		}
	}
	
	/**
	 * 如果非正,则返回默认值
	 * @param originalInt
	 * @param defaultInt	默认Integer
	 * @return
	 */
	public static Integer defaultIfNotPositive ( Integer originalInt, Integer defaultInt ) {
		if ( 0 >= originalInt ) {
			return defaultInt;
		}
		return originalInt;
	}
	
	
	
}
