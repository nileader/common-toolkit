package common.toolkit.java.util.collection;

import static org.junit.Assert.assertEquals;
import static common.toolkit.java.constant.SymbolConstant.COMMA;
import static common.toolkit.java.constant.EmptyObjectConstant.EMPTY_STRING;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * 类说明: 
 * @author 银时 yinshi.nc@taobao.com
 */
public class ListUtilTest {

	@Test
	public void parseList( ){
		
		List<String> list = new ArrayList<String>();
		
		List<String> list2 = new ArrayList<String>();
		list2.add( "1.2.37.111:2181" );
		list2.add( "1.2.37.112:2181" );
		list2.add( "1.2.37.114:2181" );
		list2.add( "1.2.20.120:2181" );
		
		String originalStr = "1.2.37.111:2181,1.2.37.112:2181,1.2.37.114:2181,1.2.20.120:2181";
		String splitStr      = COMMA;
		assertEquals(list2, ListUtil.parseList(originalStr, splitStr) );
		assertEquals(list,   ListUtil.parseList( null, null ) );
		assertEquals(list,   ListUtil.parseList( EMPTY_STRING, null ) );
		assertEquals(list,   ListUtil.parseList( null, EMPTY_STRING ) );
		assertEquals(list,   ListUtil.parseList( EMPTY_STRING, EMPTY_STRING ) );
	}
	
	
	
}
