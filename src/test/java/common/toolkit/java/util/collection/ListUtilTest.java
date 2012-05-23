package common.toolkit.java.util.collection;

import static common.toolkit.java.constant.EmptyObjectConstant.EMPTY_STRING;
import static common.toolkit.java.constant.SymbolConstant.COMMA;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	@Test
	public void subList( ) {
		
		
		List<String> list = new ArrayList< String >();
		list.add( "1" );
		list.add( "2" );
		list.add( "3" );
		list.add( "4" );
		
		
		List<String> list1 = new ArrayList< String >();
		list.add( "2" );
		list.add( "3" );
		
		List<String> list2 = new ArrayList< String >();
		list.add( "2" );
		list.add( "3" );
		list.add( "4" );
		
		
		assertEquals( list1, ListUtil.subList( list1, 1, 2 ) );
		assertEquals( list1, ListUtil.subList( list2, 1, 3 ) );
		assertEquals( list1, ListUtil.subList( list2, 1, 4 ) );
		
	}
	
	
	@Test
	public void split() {
		
		List<String> list = new ArrayList< String >();
		list.add( "1" );
		list.add( "2" );
		list.add( "3" );
		list.add( "4" );
		list.add( "5" );
		list.add( "6" );
		list.add( "7" );
		list.add( "8" );
		list.add( "9" );
		list.add( "10" );
		list.add( "11" );
		list.add( "12" );
		list.add( "13" );
		list.add( "14" );
		list.add( "15" );
		list.add( "16" );
		list.add( "17" );
		
		
		
		Map<Integer, List<String> > map = new HashMap< Integer, List<String> >();
		
		List<String> list1 = new ArrayList< String >();
		list1.add( "1" );
		list1.add( "2" );
		list1.add( "3" );
		list1.add( "4" );
		List<String> list2 = new ArrayList< String >();
		list2.add( "5" );
		list2.add( "6" );
		list2.add( "7" );
		list2.add( "8" );
		List<String> list3 = new ArrayList< String >();
		list3.add( "9" );
		list3.add( "10" );
		list3.add( "11" );
		list3.add( "12" );
		List<String> list4 = new ArrayList< String >();
		list4.add( "13" );
		list4.add( "14" );
		list4.add( "15" );
		list4.add( "16" );
		List<String> list5 = new ArrayList< String >();
		list5.add( "17" );
		
		map.put( 1, list1 );
		map.put( 2, list2 );
		map.put( 3, list3 );
		map.put( 4, list4 );
		map.put( 5, list5 );
		
		
		assertEquals( map, ListUtil.split( list, 4 ) );
	}
	
	@Test
	public void diff() {

		List<String> list1 = new ArrayList< String >();
		list1.add( "1" );
		list1.add( "2" );
		list1.add( "3" );
		list1.add( "4" );
		List<String> list2 = new ArrayList< String >();
		list2.add( "2" );
		list2.add( "7" );
		list2.add( "4" );
		list2.add( "8" );
		
		
		List<String> list1Have = new ArrayList< String >();
		list1Have.add( "1" );
		list1Have.add( "3" );
		List<String> list2Have = new ArrayList< String >();
		list2Have.add( "7" );
		list2Have.add( "8" );
		List<String> bothHave = new ArrayList< String >();
		bothHave.add( "2" );
		bothHave.add( "4" );		
		
		Map<String, List<String> > diffMap = new HashMap< String, List<String> >();
		diffMap.put( "list1Have", list1Have );
		diffMap.put( "list2Have", list2Have );
		diffMap.put( "bothHave", bothHave );
		
		Map<String, List<String> > diffMap2 = new HashMap< String, List<String> >();
		diffMap2.put( "list1Have", new ArrayList< String >() );
		diffMap2.put( "list2Have", list2 );
		diffMap2.put( "bothHave", new ArrayList< String >() );
		
		Map<String, List<String> > diffMap3 = new HashMap< String, List<String> >();
		diffMap3.put( "list1Have", list1 );
		diffMap3.put( "list2Have", new ArrayList< String >() );
		diffMap3.put( "bothHave", new ArrayList< String >() );
		
		
		
		assertEquals( diffMap, ListUtil.diff( list1, list2 ) );
		assertEquals( diffMap2, ListUtil.diff( null, list2 ) );
		assertEquals( diffMap3, ListUtil.diff( list1, null ) );
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
