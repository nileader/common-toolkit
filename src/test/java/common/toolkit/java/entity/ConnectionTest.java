package common.toolkit.java.entity;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import common.toolkit.java.entity.io.Connection;

/**
 * @author 银时 yinshi.nc@taobao.com
 */
public class ConnectionTest {

	@Test
	public void test() {

		Map< Connection, Connection > test = new HashMap< Connection, Connection >();

		Connection conn1 =  new Connection( "1.1.2.1", "1fd863sd5sfh3456" );
		Connection conn3 =  new Connection( "1.1.2.3", "3fd863sd5sfh3456" );
		test.put( conn1, conn3 );
		test.put( new Connection( "1.1.2.2", "1fd863sd5gh3456" ), new Connection( "1.1.2.2", "2fd863sd5gh3456" ) );
		test.put( conn3, conn1 );
		test.put( new Connection( "1.1.2.4", "1fd863sd5gh3456" ), new Connection( "1.1.2.4", "4fd863sd5gh3456" ) );
		test.put( new Connection( "1.1.2.5", "1fd863sd5gh3456" ), new Connection( "1.1.2.5", "5fd863sd5gh3456" ) );
		
		Connection conn2 =  new Connection( "1.1.2.3", "3fd863sd5sfh3456" );
		
		assertTrue( conn2.equals( conn3 ) );
		assertTrue( test.containsKey( conn1 ) );
		assertTrue( test.containsValue( conn1 ) );
	}

}
