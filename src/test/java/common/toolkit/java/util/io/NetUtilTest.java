package common.toolkit.java.util.io;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpException;
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

	@Test
	public void testGetContentOfUrl() throws HttpException, IOException {
		Map< String, String > map = new HashMap< String, String >();

		map.put( "taobao", "http://www.taobao.com" );
		map.put( "baidu", "http://www.baidu.com" );
		map.put( "qq", "http://www.qq.com" );

		System.out.println( NetUtil.getContentOfUrl( map, 5000 ) );
	}

	@Test
	public void getIpFromServer() {

		String server = "192.168.5.24:54712";
		String ip = "192.168.5.24";

		assertTrue( ip.equalsIgnoreCase( NetUtil.getIpFromServer( server ) ) );
		assertTrue( "".equalsIgnoreCase( NetUtil.getIpFromServer( "" ) ) );

		List< String > serverList = new ArrayList< String >();
		serverList.add( "1.2.3.4:1254" );
		serverList.add( "1.2.3.4:1754" );
		serverList.add( "" );
		serverList.add( "1.2.3.5:1254" );
		serverList.add( null );

		List< String > ipList = new ArrayList< String >();
		ipList.add( "1.2.3.4" );
		ipList.add( "1.2.3.4" );
		ipList.add( "1.2.3.5" );

		assertTrue( ipList.equals( NetUtil.getIpFromServer( serverList ) ) );

	}

}
