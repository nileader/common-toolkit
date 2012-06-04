package common.toolkit.java.util.collection;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * @author  nileader / nileader@gmail.com
 * @Date	 2012-5-17
 */
public class MapUtilTest {

	@Test
	public void addValue() {
		
		Map<String,Long> map1 = new HashMap< String, Long >();
		Map<String,Long> map2 = new HashMap< String, Long >();
		Map<String,Long> map3 = new HashMap< String, Long >();
		
		map1.put( "a", 12l );
		map1.put( "b", 1l );
		map1.put( "c", 12l );
		map1.put( "d", 13l );
		map1.put( "e", 14l );
		
		map2.put( "a", 12l );
		map2.put( "b", 1l );
		map2.put( "h", 12l );
		map2.put( "d", 13l );
		map2.put( "i", 14l );
		
		
		map3.put( "a", 24l );
		map3.put( "b", 2l );
		map3.put( "c", 12l );
		map3.put( "d", 26l );
		map3.put( "e", 14l );
		map3.put( "h", 12l );
		map3.put( "i", 14l );
		
		assertEquals( map3, MapUtil.addValue( map1, map2 ) );
		
	}
	
	
	
}
