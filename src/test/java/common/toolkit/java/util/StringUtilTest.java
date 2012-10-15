package common.toolkit.java.util;

import static common.toolkit.java.constant.EmptyObjectConstant.EMPTY_STRING;
import static common.toolkit.java.constant.EmptyObjectConstant.EMPTY_STRING_ARRAY;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import common.toolkit.java.constant.BaseConstant;
import common.toolkit.java.constant.EmptyObjectConstant;
import common.toolkit.java.constant.SymbolConstant;
import common.toolkit.java.exception.NotExpectedFormatedException;

/**
 * Description: Test for StringUtil
 * @author nileader / nileader@gmail.com
 * @Date Feb 10, 2012
 */
public class StringUtilTest {

	private final static String originalStr = "common-toolkit-ori";
	private final static String defaultStr = "common-toolkit-test";

	public static String NILEADER_MAIL_STR 	  = "nileader@gmail.com";
	public static String NILEADER_STR      	  = "nileader";
	
	@Test
	public void containsIgnoreCase() {

		String originalStr = NILEADER_MAIL_STR;

		String targetStr = "nILeader";
		String targetStr2 = "NILEADER";
		String targetStr3 = "yinshi.nc";

		assertTrue( StringUtil.containsIgnoreCase( originalStr, targetStr ) );
		assertTrue( StringUtil.containsIgnoreCase( originalStr, targetStr2 ) );
		assertFalse( StringUtil.containsIgnoreCase( originalStr, targetStr3 ) );
	}
	
	@Test
	public void containsIp() {
		assertTrue( StringUtil.containsIp( "127.0.0.1" ) );
		assertTrue( StringUtil.containsIp( "ee127.0.0.1ee" ) );
		assertTrue( StringUtil.containsIp( "ee127.0.0.1中文" ) );
		assertTrue( StringUtil.containsIp( "ee127.0.0.1" ) );
		assertFalse( StringUtil.containsIp( "这里没有ip" ) );
		assertFalse( StringUtil.containsIp( "这里有一个错误的ip1.2.036.257" ) );
		assertFalse( StringUtil.containsIp( null ) );
	}

	@Test
	public void convertToCamelCaseString() {

		String inputString1 = "ni_lea-der@gmail./com";
		String inputString2 = "ni$lea#der@gmail.&com";

		assertEquals( "niLeaDerGmail.Com", StringUtil.convertToCamelCaseString( inputString1, false ) );
		assertEquals( "NiLeaDerGmail.Com", StringUtil.convertToCamelCaseString( inputString2, true ) );

	}
	
	@Test
	public void defaultIfBlank() {

		assertEquals( originalStr, StringUtil.defaultIfBlank( originalStr, defaultStr ) );
		assertEquals( defaultStr, StringUtil.defaultIfBlank( EMPTY_STRING, defaultStr ) );
		assertEquals( defaultStr, StringUtil.defaultIfBlank( null, defaultStr ) );
	}

	@Test
	public void equalsIgnoreCaseAll() {

		String[] compareStrArray = { "nileader", "NILEADER", "nileader" };
		String[] compareStrArray2 = { "nileader", "nileader", "nileader", "yinshi.nc" };

		assertTrue( StringUtil.equalsIgnoreCaseAll( "nileader", compareStrArray ) );
		assertFalse( StringUtil.equalsIgnoreCaseAll( "nileader", compareStrArray2 ) );
	}

	@Test
	public void equalsIgnoreCaseOne() {

		String[] compareStrArray = { "nileader", "NILEADER", "nileader" };
		String[] compareStrArray2 = { "nileader", "nileader", "nileader", "yinshi.nc" };
		String[] compareStrArray3 = { "a", "b", "c", "d" };

		assertTrue( StringUtil.equalsIgnoreCaseOne( "nileader", compareStrArray ) );
		assertTrue( StringUtil.equalsIgnoreCaseOne( "nileader", compareStrArray2 ) );
		assertFalse( StringUtil.equalsIgnoreCaseOne( "nileader", compareStrArray3 ) );
	}

	@Test
	public void findAllByRegex() {
		String originalStr = "My name is name:nileader-dev1, you can call me name:nileader-dev2,";
		List< String > targetList = new ArrayList< String >();
		targetList.add( "name:nileader-dev1," );
		targetList.add( "name:nileader-dev2," );

		assertEquals( targetList, StringUtil.findAllByRegex( originalStr, "name:(?s).*?," ) );
		assertEquals( null, StringUtil.findAllByRegex( EMPTY_STRING, "name:(?s).*?," ) );
		assertEquals( null, StringUtil.findAllByRegex( originalStr, null ) );
	}
	
	
	@Test
	public void isBlankString() {
		String str = null;
		assertTrue( StringUtil.isBlank( str ) );
		assertTrue( StringUtil.isBlank( EMPTY_STRING ) );
		assertFalse( StringUtil.isBlank( originalStr ) );
	}

	@Test
	public void isBlankStringArray() {

		String str = null;
		String[] originalStrArray = { str, "123", "456" };
		String[] originalStrArray2 = { EMPTY_STRING, "123", "456" };
		String[] originalStrArray3 = { "123", "456" };

		assertTrue( StringUtil.isBlank( originalStrArray ) );
		assertTrue( StringUtil.isBlank( originalStrArray2 ) );
		assertFalse( StringUtil.isBlank( originalStrArray3 ) );
	}

	@Test
	public void isContainWhitespace() {

		String str = null;

		assertTrue( StringUtil.isContainWhitespace( str ) );
		assertTrue( StringUtil.isContainWhitespace( EMPTY_STRING ) );
		assertTrue( StringUtil.isContainWhitespace( " nileader" ) );
		assertTrue( StringUtil.isContainWhitespace( "ni leader" ) );
		assertTrue( StringUtil.isContainWhitespace( "nileader " ) );
		assertFalse( StringUtil.isContainWhitespace( "nileader" ) );
	}
	
	@Test
	public void join() {
		String str1 = "nileader";
		String str2 = "";
		String str3 = "中文";
		
		String expectedStr1 = str1 + BaseConstant.WORD_SEPARATOR + str2 + BaseConstant.WORD_SEPARATOR + str3;
		String expectedStr2 = "" + BaseConstant.WORD_SEPARATOR + "" + BaseConstant.WORD_SEPARATOR + "";
		
		assertEquals( expectedStr1, StringUtil.join( str1,str2,str3 ) );
		assertEquals( "", StringUtil.join( null ) );
		assertEquals( expectedStr2, StringUtil.join( "","","" ) );
		
	}
	
	@Test
	public void testMakeFirstLetterLowerCase() {
		assertEquals( "nileader", StringUtil.makeFirstLetterLowerCase( "Nileader" ) );
	}
	
	

	@Test
	public void replaceAll() {

		String originalStr = "My name is nileader, i came form China, nileader is my network id.";
		String replacement = "abc";

		assertEquals( "My name is abc, i came form China, abc is my network id.", StringUtil.replaceAll( originalStr, replacement, NILEADER_STR ) );
		
		assertEquals( "My name is abc, i came form China, abc is my network abc.", StringUtil.replaceAll( originalStr, replacement, NILEADER_STR,"id" ) );
		
		assertEquals( "My name is abc, i came form China, abc is my network id.", StringUtil.replaceAll( originalStr, replacement, NILEADER_STR,"dd" ) );
		
		assertEquals( "My name is abc, i came form China, abc is my network id.", StringUtil.replaceAll( originalStr, replacement, NILEADER_STR,"" ) );
		
		assertEquals( "My name is abc, i came form China, abc is my network id.", StringUtil.replaceAll( originalStr, replacement, NILEADER_STR,null ) );
		
		assertEquals( "abc name is nileader, i came form China, nileader is my network id.", StringUtil.replaceAll( originalStr, replacement, "My" ) );
		assertEquals( originalStr, StringUtil.replaceAll( originalStr, "yinshi.nc", replacement ) );
		assertEquals( EmptyObjectConstant.EMPTY_STRING, StringUtil.replaceAll( null, replacement, "yinshi.nc" ) );
		assertEquals( EmptyObjectConstant.EMPTY_STRING, StringUtil.replaceAll( EmptyObjectConstant.EMPTY_STRING, replacement, "yinshi.nc" ) );
	}
	
	@Test
	public void replaceLast() {

		String originalStr = "My name is nileader, i came form China, nileader is my network id.";
		String replacement = "abc";

		assertEquals( "My name is nileader, i came form China, abc is my network id.",
				StringUtil.replaceLast( originalStr, NILEADER_STR, replacement ) );
		assertEquals( "abc name is nileader, i came form China, nileader is my network id.", StringUtil.replaceLast( originalStr, "My", replacement ) );
		assertEquals( originalStr, StringUtil.replaceLast( originalStr, "yinshi.nc", replacement ) );
	}


	@Test
	public void replacePlaceholder() {

		
		Map<String,String> values = new HashMap<String,String>();
		values.put( "id", "11" );
		values.put( "name", "银时" );
		values.put( "gender", "male" );
		
		String originalStr = "select * from table where id=${id}, name=${name}, gender=${gender},{gender}";
		String expectedStr = "select * from table where id=11, name=银时, gender=male,{gender}";

		assertEquals( expectedStr, StringUtil.replacePlaceholder( originalStr, values ) );
		assertEquals( EMPTY_STRING, StringUtil.replacePlaceholder( EMPTY_STRING, values ) );
		assertEquals( EMPTY_STRING, StringUtil.replacePlaceholder( EMPTY_STRING, null ) );
		assertEquals( originalStr, StringUtil.replacePlaceholder( originalStr, null) );
	}
	
	
	
	@Test
	public void replaceSequenced() {

		String originalStr = "select * from table where id={0}, name={1}, gender={2}";
		String expectedStr = "select * from table where id='1', name='yinshi.nc', gender='male'";
		String expectedStr2 = "select * from table where id='1', name='yinshi.nc', gender=";
		String expectedStr3 = "select * from table where id=, name=, gender={2}";

		assertEquals( expectedStr, StringUtil.replaceSequenced( originalStr, "'1'", "'yinshi.nc'", "'male'" ) );
		assertEquals( EMPTY_STRING, StringUtil.replaceSequenced( EMPTY_STRING, "'1'", "'yinshi.nc'", "'male'" ) );
		assertEquals( expectedStr2, StringUtil.replaceSequenced( originalStr, "'1'", "'yinshi.nc'", null ) );
		assertEquals( originalStr, StringUtil.replaceSequenced( originalStr ) );
		assertEquals( originalStr, StringUtil.replaceSequenced( originalStr, null ) );
		assertEquals( expectedStr3, StringUtil.replaceSequenced( originalStr, null, null ) );
	}

	/**
	 * 测试：splitWithFixedLength( String originalStr, String regex, int
	 * fixedLength ) <br>
	 * splitWithLeastLength( String originalStr, String regex, int leastLength ) <br>
	 * splitWithMaxLength( String originalStr, String regex, int maxLength ) <br>
	 * @throws NotExpectedFormatedException
	 * 
	 */
	@Test
	public void splitWithLength() {
		String originalStr = "/yinshi.nc//银时/41521";
		String[] expectedArray = new String[] { "", "yinshi.nc", "", "银时", "41521" };

		{
			/**
			 * 测试 splitWithFixedLength( String originalStr, String regex, int
			 * fixedLength )
			 */
			// 正常情况下
			try {
				assertArrayEquals( expectedArray, StringUtil.splitWithFixedLength( originalStr, SymbolConstant.SLASH, 5 ) );
			} catch ( Exception e ) {
				fail( "这里不应抛出异常的" );
			}
			try {
				assertArrayEquals( EMPTY_STRING_ARRAY, StringUtil.splitWithFixedLength( originalStr, EMPTY_STRING, Integer.MAX_VALUE ) );
			} catch ( Exception e ) {
				fail( "这里不应抛出异常的" );
			}

			// 这里让他抛 NotExpectedFormatedException
			try {
				assertArrayEquals( expectedArray, StringUtil.splitWithFixedLength( originalStr, SymbolConstant.SLASH, 4 ) );
				fail( "不应用执行到我" );
			} catch ( NotExpectedFormatedException e ) {
			} catch ( Exception e ) {
				fail( "这里不应抛出异常的" );
			}

			// 如果originalStr="", 并且fixedLength=0，那么不抛出异常。
			try {
				assertArrayEquals( EMPTY_STRING_ARRAY, StringUtil.splitWithFixedLength( EMPTY_STRING, SymbolConstant.SLASH, 0 ) );
			} catch ( Exception e ) {
				fail( "这里不应抛出异常的" );
			}

			// 如果originalStr="", 并且fixedLength>0，那么抛出异常。
			try {
				assertArrayEquals( EMPTY_STRING_ARRAY, StringUtil.splitWithFixedLength( EMPTY_STRING, SymbolConstant.SLASH, 1 ) );
				fail( "不应用执行到我" );
			} catch ( NotExpectedFormatedException e ) {
			} catch ( Exception e ) {
				fail( "这里不应抛出异常的" );
			}

		}
		/**
		 * 测试 splitWithFixedLength( String originalStr, String regex, int
		 * fixedLength )
		 */

		{
			/**
			 * 测试 splitWithLeastLength( String originalStr, String regex, int
			 * leastLength )
			 */
			// 正常情况下
			try {
				assertArrayEquals( expectedArray, StringUtil.splitWithLeastLength( originalStr, SymbolConstant.SLASH, 5 ) );
			} catch ( Exception e ) {
				fail( "这里不应抛出异常的" );
			}
			try {
				assertArrayEquals( expectedArray, StringUtil.splitWithLeastLength( originalStr, SymbolConstant.SLASH, 4 ) );
			} catch ( Exception e ) {
				fail( "这里不应抛出异常的" );
			}
			try {
				assertArrayEquals( EMPTY_STRING_ARRAY, StringUtil.splitWithLeastLength( originalStr, EMPTY_STRING, Integer.MAX_VALUE ) );
			} catch ( Exception e ) {
				fail( "这里不应抛出异常的" );
			}

			// 这里让他抛 NotExpectedFormatedException
			try {
				assertArrayEquals( expectedArray, StringUtil.splitWithLeastLength( originalStr, SymbolConstant.SLASH, 6 ) );
				fail( "不应用执行到我" );
			} catch ( NotExpectedFormatedException e ) {
			} catch ( Exception e ) {
				fail( "这里不应抛出异常的" );
			}

			// 如果originalStr="", 并且leastLength=0，那么不抛出异常。
			try {
				assertArrayEquals( EMPTY_STRING_ARRAY, StringUtil.splitWithLeastLength( EMPTY_STRING, SymbolConstant.SLASH, 0 ) );
			} catch ( Exception e ) {
				fail( "这里不应抛出异常的" );
			}

			// 如果originalStr="", 并且leastLength>0，那么抛出异常。
			try {
				assertArrayEquals( EMPTY_STRING_ARRAY, StringUtil.splitWithLeastLength( EMPTY_STRING, SymbolConstant.SLASH, 1 ) );
				fail( "不应用执行到我" );
			} catch ( NotExpectedFormatedException e ) {
			} catch ( Exception e ) {
				fail( "这里不应抛出异常的" );
			}

		}
		/**
		 * 测试 splitWithLeastLength( String originalStr, String regex, int
		 * leastLength )
		 */

		{
			/**
			 * 测试 splitWithMaxLength( String originalStr, String regex, int
			 * maxLength )
			 */
			// 正常情况下
			try {
				assertArrayEquals( expectedArray, StringUtil.splitWithMaxLength( originalStr, SymbolConstant.SLASH, 5 ) );
			} catch ( Exception e ) {
				fail( "这里不应抛出异常的" );
			}
			try {
				assertArrayEquals( expectedArray, StringUtil.splitWithMaxLength( originalStr, SymbolConstant.SLASH, 6 ) );
			} catch ( Exception e ) {
				fail( "这里不应抛出异常的" );
			}
			try {
				assertArrayEquals( EMPTY_STRING_ARRAY, StringUtil.splitWithMaxLength( originalStr, EMPTY_STRING, Integer.MIN_VALUE ) );
			} catch ( Exception e ) {
				fail( "这里不应抛出异常的" );
			}

			// 这里让他抛 NotExpectedFormatedException
			try {
				assertArrayEquals( expectedArray, StringUtil.splitWithMaxLength( originalStr, SymbolConstant.SLASH, 4 ) );
				fail( "不应用执行到我" );
			} catch ( NotExpectedFormatedException e ) {
			} catch ( Exception e ) {
				fail( "这里不应抛出异常的" );
			}

			// 如果originalStr="", 并且leastLength=0，那么不抛出异常。
			try {
				assertArrayEquals( EMPTY_STRING_ARRAY, StringUtil.splitWithMaxLength( EMPTY_STRING, SymbolConstant.SLASH, 0 ) );
			} catch ( Exception e ) {
				fail( "这里不应抛出异常的" );
			}

		}
		/**
		 * 测试 splitWithMaxLength( String originalStr, String regex, int
		 * leastLength )
		 */

	}
	
	
	@Test
	public void subStringIfTooLong(){
		
		String str1 = "银时的一个字符串";
		String expectedStr1_1 = "银时的...";
		String expectedStr1_2 = "银时的";
		
		assertEquals( EMPTY_STRING, StringUtil.subStringIfTooLong( null, 3, "..." ) );
		assertEquals( expectedStr1_1, StringUtil.subStringIfTooLong( str1, 3, "..." ) );
		assertEquals( expectedStr1_2, StringUtil.subStringIfTooLong( str1, 3, "" ) );
		assertEquals( expectedStr1_2, StringUtil.subStringIfTooLong( str1, 3, null ) );
		assertEquals( EMPTY_STRING, StringUtil.subStringIfTooLong( str1, 0, null ) );
		assertEquals( EMPTY_STRING, StringUtil.subStringIfTooLong( str1, -1, null ) );
		assertEquals( str1, StringUtil.subStringIfTooLong( str1, 100, "..." ) );
		
		
		
		String str2 = "YinShi's String";
		String expectedStr2_1 = "Yin...";
		String expectedStr2_2 = "Yin";
		
		assertEquals( expectedStr2_1, StringUtil.subStringIfTooLong( str2, 3, "..." ) );
		assertEquals( expectedStr2_2, StringUtil.subStringIfTooLong( str2, 3, "" ) );
		assertEquals( expectedStr2_2, StringUtil.subStringIfTooLong( str2, 3, null ) );
		assertEquals( EMPTY_STRING, StringUtil.subStringIfTooLong( str2, 0, null ) );
		assertEquals( EMPTY_STRING, StringUtil.subStringIfTooLong( str2, -1, null ) );
		assertEquals( str2, StringUtil.subStringIfTooLong( str2, 100, "..." ) );
		
	}
	

	@Test
	public void trimToEmpty() {

		String originalStr = " nileader nileader ";
		assertEquals( "nileader nileader", StringUtil.trimToEmpty( originalStr ) );
	}
}
