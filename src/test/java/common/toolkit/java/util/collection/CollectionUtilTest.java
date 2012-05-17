package common.toolkit.java.util.collection;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.AssertThrows;

import common.toolkit.java.util.StringUtil;

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
	
	
	
}
