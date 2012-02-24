package common.toolkit.java.util;


import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;

import common.toolkit.java.BaseTest;
import common.toolkit.java.util.security.CipherUtil;


/**
 * Test of MailUtil
 * @author 银时 yinshi.nc@taobao.com
 */
public class MailUtilTest extends BaseTest {

	
	
	@BeforeClass
	public static void initialize() throws IOException {
		BaseTest.initialize();
	}
	
	
	@Test
	public void testSendTextMail() throws Exception{
		
		String serverHost = CipherUtil.decrypt( BaseTest.getConfigtationValue( "serverHost" ) );
		String serverPort = CipherUtil.decrypt( BaseTest.getConfigtationValue( "serverPort" ) );
		String userName   = CipherUtil.decrypt( BaseTest.getConfigtationValue( "userName" ) );
		String passWord   = CipherUtil.decrypt( BaseTest.getConfigtationValue( "password" ) );
		String fromMail   = "yinshi.nc@taobao.com";
		String toMail     = "yinshi.nc@taobao.com";
		String subject    = "银时进行common-toolkit项目单元测试";
		String content    = "这是一个测试内容";
		
		try {
			MailUtil.sendTextMail( serverHost, serverPort, userName, passWord, fromMail, toMail, subject, content );
		} catch ( Exception e ) {
			fail( e.getMessage() );
		}
	}
	
	
	public static void main( String[] args ) throws Exception {
		System.out.println( CipherUtil.encrypt( "587" ) );
		
	}
	
	
	
}
