package common.toolkit.java.util.io;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * 类说明: 
 * @author 银时 yinshi.nc@taobao.com
 */
public class NetUtilTest {

	@Test
	public void testIsHostOpenPort() throws Exception {
		NetUtil.isHostOpenPort( "1.2.11.140", 2181 );
	}

	@Test
	public void isLegalIP() throws Exception {
		assertTrue( NetUtil.isLegalIP( "1.2.11.140" ) );
		assertTrue( NetUtil.isLegalIP( "1.1.1.1" ) );
		assertTrue( NetUtil.isLegalIP( "127.0.0.1" ) );
		assertFalse( NetUtil.isLegalIP( "1.2.011.25" ) );
		assertFalse( NetUtil.isLegalIP( "1.2.11.256" ) );
	}
	
	
}
