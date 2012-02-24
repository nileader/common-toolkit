package common.toolkit.java.util.security;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

import common.toolkit.java.constant.EmptyObjectConstant;

/**
 * @author 银时 yinshi.nc@taobao.com
 */
public class CipherUtilTest {
	
	static String str1 = "银时是天才";
	static String str2 = "银时是1个天才";
	static String str3 = "yinshi's a genius";
	
	@Test
	public void testEncryptAndDecrypt() throws Exception {
		
		for( int i = 0; i < 1000; i++ ){
			assertEquals( str1, CipherUtil.decrypt( CipherUtil.encrypt( str1 ) ) );
			assertEquals( str2, CipherUtil.decrypt( CipherUtil.encrypt( str2 ) ) );
			assertEquals( str3, CipherUtil.decrypt( CipherUtil.encrypt( str3 ) ) );
			assertEquals( EmptyObjectConstant.EMPTY_STRING, CipherUtil.decrypt( CipherUtil.encrypt( null ) ) );
		}
	}

}
