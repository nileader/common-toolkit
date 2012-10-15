package common.toolkit.java.util.number;

import common.toolkit.java.util.StringUtil;

/**
 * Integer 工具类
 * 
 * @author 银时：yinshi.nc / yinshi.nc@taobao.com
 * @Date Jan 13, 2012
 */
public class IntegerUtil {

	/**
	 * 如果为0,则返回默认值
	 * 
	 * @param originalInt
	 * @param defaultInt
	 *            默认Integer
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
	 * 
	 * @param originalInt
	 * @param defaultInt
	 *            默认Integer
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
	 * 如果是一个不合法的整型，那么返回一个默认值
	 * 
	 * @param originalInt
	 * @param defaultInt
	 *            默认Integer
	 * @return
	 */
	public static Integer defaultIfError( Integer originalStr, Integer defaultInt ) {

		try {
			return Integer.valueOf( originalStr );
		} catch ( Exception e ) {
			return defaultInt;
		}
	}

	/**
	 * 如果非正,则返回默认值
	 * 
	 * @param originalInt
	 * @param defaultInt
	 *            默认Integer
	 * @return
	 */
	public static Integer defaultIfNotPositive( Integer originalInt, Integer defaultInt ) {
		if ( 0 >= originalInt ) {
			return defaultInt;
		}
		return originalInt;
	}
	
	/**
	 * 判断是否大余0
	 * @return false if num <=0  , true if num >0
	 */
	public static boolean isBiggerThan0( int num ){
		if( 0>= num )
			return false;
		return true;
	}

	/**
	 * Return maxInt if too big, else return original.
	 * 
	 * @param originalInt
	 * @param maxInt
	 *            max int
	 * @return
	 */
	public static Integer maxIfTooBig( Integer originalInt, Integer maxInt ) {
		if ( originalInt >= maxInt ) {
			originalInt = maxInt;
		}
		return originalInt;
	}

}
