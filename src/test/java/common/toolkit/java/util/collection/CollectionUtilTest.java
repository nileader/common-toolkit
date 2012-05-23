package common.toolkit.java.util.collection;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author  nileader / nileader@gmail.com
 * @Date	 2012-5-17
 */
public class CollectionUtilTest {

	@Test
	public void frequency() {
		
		List< String > list = new ArrayList< String >();
		list.add( "1.2.3.4" );
		list.add( "1.3.3.4" );
		list.add( "1.4.3.4" );
		list.add( "1.2.3.4" );
		list.add( "1.5.3.4" );
		list.add( "1.4.3.4" );
		list.add( "1.9.3.4" );
		Map< String, Integer > map = new HashMap< String, Integer >();
		map.put( "1.2.3.4", 2 );
		map.put( "1.3.3.4", 1 );
		map.put( "1.4.3.4", 2 );
		map.put( "1.5.3.4", 1 );
		map.put( "1.9.3.4", 1 );
		Assert.assertTrue( map.equals( CollectionUtil.frequency( list ) ) );
	}
	
	@Test
	public void subtract() {
		
		
		List<String> list1 = new ArrayList< String >();
		list1.add( "1" );
		list1.add( "2" );
		list1.add( "3" );
		list1.add( "4" );
		List<String> list2 = new ArrayList< String >();
		list2.add( "1" );
		list2.add( "3" );
		list2.add( "7" );
		list2.add( "8" );
		List<String> list3 = new ArrayList< String >();
		list3.add( "2" );
		list3.add( "4" );
		List<String> list4 = new ArrayList< String >();
		list4.add( "7" );
		list4.add( "8" );
		
		assertEquals( list3, CollectionUtil.subtract( list1, list2 ) );
		assertEquals( list4, CollectionUtil.subtract( list2, list1 ) );
		
		assertEquals( new ArrayList< String >(), CollectionUtil.subtract( list4, list2 ) );
		assertEquals( new ArrayList< String >(), CollectionUtil.subtract( null, list2 ) );
		assertEquals( list4, CollectionUtil.subtract( list4, null ) );
		
	}
	
}
