package common.toolkit.java.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import common.toolkit.java.constant.EmptyObjectConstant;

/**
 * Description:
 * @author  nileader / nileader@gmail.com
 * @Date	 2012-3-5
 */
public class JsonUtilTest {

	@Test
	public void checkJsonContent() {
		String originalStr  = "\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n[{&#034;date&#034;:&#034;2012-03-04&#034;,&#034;serviceName&#034;:&#034;com.taobao.member.service.UserService&#034;,&#034;totallCalls&#034;:1067,&#034;errorCalls&#034;:0},{&#034;date&#034;:&#034;2012-03-03&#034;,&#034;serviceName&#034;:&#034;com.taobao.member.service.UserService&#034;,&#034;totallCalls&#034;:1008,&#034;errorCalls&#034;:0},{&#034;date&#034;:&#034;2012-03-02&#034;,&#034;serviceName&#034;:&#034;com.taobao.member.service.UserService&#034;,&#034;totallCalls&#034;:3228,&#034;errorCalls&#034;:0},{&#034;date&#034;:&#034;2012-03-01&#034;,&#034;serviceName&#034;:&#034;com.taobao.member.service.UserService&#034;,&#034;totallCalls&#034;:1426,&#034;errorCalls&#034;:0},{&#034;date&#034;:&#034;2012-02-29&#034;,&#034;serviceName&#034;:&#034;com.taobao.member.service.UserService&#034;,&#034;totallCalls&#034;:1028,&#034;errorCalls&#034;:0}]\r\n\r\n";
		String originalStr2 = "\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n[{&#034;date&#034;:&#034;2012-03-04&#034;,&#034;serviceName&#034;:&#034;com.taobao.member.service.UserService&#034;,&#034;totallCalls&#034;:1067,&#034;errorCalls&#034;:0},{&#034;date&#034;:&#034;2012-03-03&#034;,&#034;serviceName&#034;:&#034;com.taobao.member.service.UserService&#034;,&#034;totallCalls&#034;:1008,&#034;errorCalls&#034;:0},{&#034;date&#034;:&#034;2012-03-02&#034;,&#034;serviceName&#034;:&#034;com.taobao.member.service.UserService&#034;,&#034;totallCalls&#034;:3228,&#034;errorCalls&#034;:0},{&#034;date&#034;:&#034;2012-03-01&#034;,&#034;serviceName&#034;:&#034;com.taobao.member.service.UserService&#034;,&#034;totallCalls&#034;:1426,&#034;errorCalls&#034;:0},{&#034;date&#034;:&#034;2012-02-29&#034;,&#034;serviceName&#034;:&#034;com.taobao.member.service.UserService&#034;,&#034;totallCalls&#034;:1028,&#034;errorCalls&#034;:0}]";
		String originalStr3 = "[{&#034;date&#034;:&#034;2012-03-04&#034;,&#034;serviceName&#034;:&#034;com.taobao.member.service.UserService&#034;,&#034;totallCalls&#034;:1067,&#034;errorCalls&#034;:0},{&#034;date&#034;:&#034;2012-03-03&#034;,&#034;serviceName&#034;:&#034;com.taobao.member.service.UserService&#034;,&#034;totallCalls&#034;:1008,&#034;errorCalls&#034;:0},{&#034;date&#034;:&#034;2012-03-02&#034;,&#034;serviceName&#034;:&#034;com.taobao.member.service.UserService&#034;,&#034;totallCalls&#034;:3228,&#034;errorCalls&#034;:0},{&#034;date&#034;:&#034;2012-03-01&#034;,&#034;serviceName&#034;:&#034;com.taobao.member.service.UserService&#034;,&#034;totallCalls&#034;:1426,&#034;errorCalls&#034;:0},{&#034;date&#034;:&#034;2012-02-29&#034;,&#034;serviceName&#034;:&#034;com.taobao.member.service.UserService&#034;,&#034;totallCalls&#034;:1028,&#034;errorCalls&#034;:0}]\r\n\r\n";
		String originalStr4 = "[{&#034;date&#034;:&#034;2012-03-04&#034;,&#034;serviceName&#034;:&#034;com.taobao.member.service.UserService&#034;,&#034;totallCalls&#034;:1067,&#034;errorCalls&#034;:0},{&#034;date&#034;:&#034;2012-03-03&#034;,&#034;serviceName&#034;:&#034;com.taobao.member.service.UserService&#034;,&#034;totallCalls&#034;:1008,&#034;errorCalls&#034;:0},{&#034;date&#034;:&#034;2012-03-02&#034;,&#034;serviceName&#034;:&#034;com.taobao.member.service.UserService&#034;,&#034;totallCalls&#034;:3228,&#034;errorCalls&#034;:0},{&#034;date&#034;:&#034;2012-03-01&#034;,&#034;serviceName&#034;:&#034;com.taobao.member.service.UserService&#034;,&#034;totallCalls&#034;:1426,&#034;errorCalls&#034;:0},{&#034;date&#034;:&#034;2012-02-29&#034;,&#034;serviceName&#034;:&#034;com.taobao.member.service.UserService&#034;,&#034;totallCalls&#034;:1028,&#034;errorCalls&#034;:0}]";
		
		String expectStr = "[{&#034;date&#034;:&#034;2012-03-04&#034;,&#034;serviceName&#034;:&#034;com.taobao.member.service.UserService&#034;,&#034;totallCalls&#034;:1067,&#034;errorCalls&#034;:0},{&#034;date&#034;:&#034;2012-03-03&#034;,&#034;serviceName&#034;:&#034;com.taobao.member.service.UserService&#034;,&#034;totallCalls&#034;:1008,&#034;errorCalls&#034;:0},{&#034;date&#034;:&#034;2012-03-02&#034;,&#034;serviceName&#034;:&#034;com.taobao.member.service.UserService&#034;,&#034;totallCalls&#034;:3228,&#034;errorCalls&#034;:0},{&#034;date&#034;:&#034;2012-03-01&#034;,&#034;serviceName&#034;:&#034;com.taobao.member.service.UserService&#034;,&#034;totallCalls&#034;:1426,&#034;errorCalls&#034;:0},{&#034;date&#034;:&#034;2012-02-29&#034;,&#034;serviceName&#034;:&#034;com.taobao.member.service.UserService&#034;,&#034;totallCalls&#034;:1028,&#034;errorCalls&#034;:0}]";
		assertEquals( expectStr, JsonUtil.checkJsonContent( originalStr ) );
		assertEquals( expectStr, JsonUtil.checkJsonContent( originalStr2 ) );
		assertEquals( expectStr, JsonUtil.checkJsonContent( originalStr3 ) );
		assertEquals( expectStr, JsonUtil.checkJsonContent( originalStr4 ) );
		assertEquals( EmptyObjectConstant.EMPTY_STRING, JsonUtil.checkJsonContent( null ) );
		assertEquals( EmptyObjectConstant.EMPTY_STRING, JsonUtil.checkJsonContent( EmptyObjectConstant.EMPTY_STRING ) );
		assertEquals( EmptyObjectConstant.EMPTY_STRING, JsonUtil.checkJsonContent( "abc" ) );
		assertEquals( EmptyObjectConstant.EMPTY_STRING, JsonUtil.checkJsonContent( "[abc" ) );
		assertEquals( EmptyObjectConstant.EMPTY_STRING, JsonUtil.checkJsonContent( "abc]" ) );
	}

}
