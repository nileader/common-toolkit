package common.toolkit.java.util.io;

import static common.toolkit.java.constant.EmptyObjectConstant.EMPTY_STRING;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import common.toolkit.java.constant.EmptyObjectConstant;
import common.toolkit.java.constant.HttpConstant;
import common.toolkit.java.constant.SymbolConstant;
import common.toolkit.java.exception.CanNotEncodeException;
import common.toolkit.java.exception.IllegalParamException;
import common.toolkit.java.util.ObjectUtil;

/**
 * Description: Test ServletUtil by esaymock
 * 
 * @author 银时：yinshi.nc / yinshi.nc@taobao.com
 * @Date Jan 1, 2012
 */
public class ServletUtilTest {

	private static String DEFAULT_PROTOCOL = "http";
	private static int DEFAULT_SERVER_PORT = 8081;
	private static String DEFAULT_SERVER_NAME = "ops.jm.taobao.net";
	private static String DEFAULT_DOMAIN = ".jm.taobao.net";

	private static String DEFAULT_REQUEST_URI = "/common/test.do";
	private static String DEFAULT_REQUEST_CONTEXT_PATH = "/common";

	private static String DEFAULT_REQUEST_QUERYSTRING = "method=test" + SymbolConstant.AND_SIGN + "userName=yinshi.nc" + SymbolConstant.AND_SIGN
			+ "displayName=银时" + SymbolConstant.AND_SIGN + "staffNo=41521";

	private MockHttpServletRequest request = null;
	private MockHttpServletResponse response = null;

	@Before
	public void initialize() {
		this.reset();
	}

	@Test
	public void encodeParamsForRequestURL() throws UnsupportedEncodingException {

		String originalRequestURL_WITH_PARAMS_1 = "http://ops.jm.taobao.net:9999/taokeeper/index.do?method=test&userName=银时&passowrd=*#K=4@T6gW";
		String expectedEncodedRequestURL_GBK_1 = "http://ops.jm.taobao.net:9999/taokeeper/index.do?method="
				+ java.net.URLEncoder.encode( "test", "GBK" ) + "&userName=" + java.net.URLEncoder.encode( "银时", "GBK" ) + "&passowrd="
				+ java.net.URLEncoder.encode( "*#K=4@T6gW", "GBK" );
		String expectedEncodedRequestURL_ISO8859_1 = "http://ops.jm.taobao.net:9999/taokeeper/index.do?method="
				+ java.net.URLEncoder.encode( "test", "ISO8859-1" ) + "&userName=" + java.net.URLEncoder.encode( "银时", "ISO8859-1" ) + "&passowrd="
				+ java.net.URLEncoder.encode( "*#K=4@T6gW", "ISO8859-1" );
		String expectedEncodedRequestURL_UTF8_1 = "http://ops.jm.taobao.net:9999/taokeeper/index.do?method="
				+ java.net.URLEncoder.encode( "test", "UTF-8" ) + "&userName=" + java.net.URLEncoder.encode( "银时", "UTF-8" ) + "&passowrd="
				+ java.net.URLEncoder.encode( "*#K=4@T6gW", "UTF-8" );

		String originalRequestURL_WITH_PARAMS_2 = "http://ops.jm.taobao.net:9999/taokeeper/index.do?method=test&userName=银时&passowrd=*#K4@T6gW";
		String expectedEncodedRequestURL_GBK_2 = "http://ops.jm.taobao.net:9999/taokeeper/index.do?method="
				+ java.net.URLEncoder.encode( "test", "GBK" ) + "&userName=" + java.net.URLEncoder.encode( "银时", "GBK" ) + "&passowrd="
				+ java.net.URLEncoder.encode( "*#K4@T6gW", "GBK" );
		String expectedEncodedRequestURL_ISO8859_2 = "http://ops.jm.taobao.net:9999/taokeeper/index.do?method="
				+ java.net.URLEncoder.encode( "test", "ISO8859-1" ) + "&userName=" + java.net.URLEncoder.encode( "银时", "ISO8859-1" ) + "&passowrd="
				+ java.net.URLEncoder.encode( "*#K4@T6gW", "ISO8859-1" );

		// 以下4个应该原样返回
		String originalRequestURL_WITH_PARAMS_3 = "http://ops.jm.taobao.net:9999/taokeeper/index.do";
		String originalRequestURL_WITH_PARAMS_4 = "http://ops.jm.taobao.net:9999/taokeeper/index.do?";
		String originalRequestURL_WITH_PARAMS_5 = "http://ops.jm.taobao.net:9999/taokeeper/index.do?method=";

		try {
			assertEquals( expectedEncodedRequestURL_GBK_1, ServletUtil.encodeParamsForRequestURL( originalRequestURL_WITH_PARAMS_1, "GBK" ) );
		} catch ( Exception e ) {
			fail( "不应该执行我" );
		}
		try {
			assertEquals( expectedEncodedRequestURL_ISO8859_1, ServletUtil.encodeParamsForRequestURL( originalRequestURL_WITH_PARAMS_1, "ISO8859-1" ) );
		} catch ( Exception e ) {
			fail( "不应该执行我" );
		}
		try {
			assertEquals( expectedEncodedRequestURL_UTF8_1, ServletUtil.encodeParamsForRequestURL( originalRequestURL_WITH_PARAMS_1, "" ) );
		} catch ( Exception e ) {
			fail( "不应该执行我" );
		}
		try {
			assertEquals( expectedEncodedRequestURL_UTF8_1, ServletUtil.encodeParamsForRequestURL( originalRequestURL_WITH_PARAMS_1, null ) );
		} catch ( Exception e ) {
			fail( "不应该执行我" );
		}

		try {
			assertEquals( expectedEncodedRequestURL_ISO8859_1, ServletUtil.encodeParamsForRequestURL( originalRequestURL_WITH_PARAMS_1, "YINSHI" ) );
			fail( "不应该执行我" );
		} catch ( CanNotEncodeException e ) {
		} catch ( Exception e ) {
			fail( "不应该执行我" );
		}

		try {
			assertEquals( expectedEncodedRequestURL_GBK_2, ServletUtil.encodeParamsForRequestURL( originalRequestURL_WITH_PARAMS_2, "GBK" ) );
		} catch ( Exception e ) {
			fail( "不应该执行我" );
		}
		try {
			assertEquals( expectedEncodedRequestURL_ISO8859_2, ServletUtil.encodeParamsForRequestURL( originalRequestURL_WITH_PARAMS_2, "ISO8859-1" ) );
		} catch ( Exception e ) {
			fail( "不应该执行我" );
		}
		try {
			assertEquals( expectedEncodedRequestURL_ISO8859_2, ServletUtil.encodeParamsForRequestURL( originalRequestURL_WITH_PARAMS_2, "YINSHI.NC" ) );
			fail( "不应该执行我" );
		} catch ( CanNotEncodeException e ) {
		} catch ( Exception e ) {
			fail( "不应该执行我" );
		}

		// 以下都返回原值
		try {
			assertEquals( originalRequestURL_WITH_PARAMS_3, ServletUtil.encodeParamsForRequestURL( originalRequestURL_WITH_PARAMS_3, "GBK" ) );
		} catch ( Exception e ) {
			fail( "不应该执行我" );
		}
		try {
			assertEquals( originalRequestURL_WITH_PARAMS_4, ServletUtil.encodeParamsForRequestURL( originalRequestURL_WITH_PARAMS_4, "123" ) );
		} catch ( Exception e ) {
			fail( "不应该执行我" );
		}

		try {
			assertEquals( originalRequestURL_WITH_PARAMS_5, ServletUtil.encodeParamsForRequestURL( originalRequestURL_WITH_PARAMS_5, "UTF-8" ) );
		} catch ( Exception e ) {
			fail( "不应该执行我" );
		}

	}

	@Test
	public void getRequestURI() {
		this.reset();
		if ( ObjectUtil.isBlank( request, response ) ) {
			throw new RuntimeException( "Not init request and response success" );
		}

		// 测试 GET 方法的请求
		request.setMethod( HttpConstant.REQUEST_METHOD_GET );
		request.setQueryString( DEFAULT_REQUEST_QUERYSTRING );
		request.setRequestURI( DEFAULT_REQUEST_URI );
		assertEquals( DEFAULT_REQUEST_URI, ServletUtil.getRequestURI( request ) );
		assertEquals( EMPTY_STRING, ServletUtil.getRequestURI( null ) );

		// 测试 POST 方法的请求
		this.reset();
		request.setMethod( HttpConstant.REQUEST_METHOD_POST );
		request.setRequestURI( DEFAULT_REQUEST_URI );
		assertEquals( DEFAULT_REQUEST_URI, ServletUtil.getRequestURI( request ) );
		assertEquals( EMPTY_STRING, ServletUtil.getRequestURI( null ) );
	}

	@Test
	public void getRequestURIAndQueryString() {
		this.reset();
		if ( ObjectUtil.isBlank( request, response ) ) {
			throw new RuntimeException( "Not init request and response success" );
		}

		// 测试 GET 方法的请求,有query string
		request.setMethod( HttpConstant.REQUEST_METHOD_GET );
		request.setQueryString( DEFAULT_REQUEST_QUERYSTRING );
		request.setRequestURI( DEFAULT_REQUEST_URI );
		assertEquals( DEFAULT_REQUEST_URI + SymbolConstant.QUESTION_SIGN + DEFAULT_REQUEST_QUERYSTRING,
				ServletUtil.getRequestURIAndQueryString( request ) );
		assertEquals( EMPTY_STRING, ServletUtil.getRequestURIAndQueryString( null ) );

		// 测试 GET 方法的请求， 无 query string
		this.reset();
		request.setMethod( HttpConstant.REQUEST_METHOD_GET );
		request.setRequestURI( DEFAULT_REQUEST_URI );
		assertEquals( DEFAULT_REQUEST_URI, ServletUtil.getRequestURIAndQueryString( request ) );
		assertEquals( EMPTY_STRING, ServletUtil.getRequestURIAndQueryString( null ) );

		// 测试 POST 方法的请求,有query string
		this.reset();
		request.setMethod( HttpConstant.REQUEST_METHOD_POST );
		request.setRequestURI( DEFAULT_REQUEST_URI );
		this.setParameter();
		assertEquals( DEFAULT_REQUEST_URI + SymbolConstant.QUESTION_SIGN + DEFAULT_REQUEST_QUERYSTRING,
				ServletUtil.getRequestURIAndQueryString( request ) );
		assertEquals( EMPTY_STRING, ServletUtil.getRequestURIAndQueryString( null ) );

		// 测试 POST 方法的请求,无query string
		this.reset();
		request.setMethod( HttpConstant.REQUEST_METHOD_POST );
		request.setRequestURI( DEFAULT_REQUEST_URI );
		assertEquals( DEFAULT_REQUEST_URI, ServletUtil.getRequestURIAndQueryString( request ) );
		assertEquals( EMPTY_STRING, ServletUtil.getRequestURIAndQueryString( null ) );

	}

	/**
	 * 测试获取ContextPath
	 */
	@Test
	public void getContextPath() {
		this.reset();
		request.setMethod( HttpConstant.REQUEST_METHOD_POST );
		request.setContextPath( DEFAULT_REQUEST_CONTEXT_PATH );
		assertEquals( DEFAULT_REQUEST_CONTEXT_PATH, ServletUtil.getContextPathFromRequest( request ) );
		assertEquals( EMPTY_STRING, ServletUtil.getContextPathFromRequest( null ) );

		/** 以下测试 getContextPathFromRequestURI */
		assertEquals( EMPTY_STRING, ServletUtil.getContextPathFromRequestURI( null ) );
		assertEquals( EMPTY_STRING, ServletUtil.getContextPathFromRequestURI( EmptyObjectConstant.EMPTY_STRING ) );
		assertEquals( EMPTY_STRING, ServletUtil.getContextPathFromRequestURI( SymbolConstant.SLASH ) );

		assertEquals( DEFAULT_REQUEST_CONTEXT_PATH, ServletUtil.getContextPathFromRequestURI( DEFAULT_REQUEST_URI ) );
		assertEquals( DEFAULT_REQUEST_CONTEXT_PATH,
				ServletUtil.getContextPathFromRequestURI( DEFAULT_REQUEST_URI + SymbolConstant.QUESTION_SIGN + DEFAULT_REQUEST_QUERYSTRING ) );

	}

	/**
	 * 测试获取域名domain( ip:port)
	 * @throws IllegalParamException
	 */
	@Test
	public void getDomain() throws IllegalParamException {
		this.reset();

		String expectedDomain = DEFAULT_DOMAIN;

		assertEquals( expectedDomain, ServletUtil.getDomainFromRequest( request ) );
		assertEquals( EmptyObjectConstant.EMPTY_STRING, ServletUtil.getDomainFromRequest( null ) );

		/** 以下测试 getServerOfRequestURL */
		assertEquals( EmptyObjectConstant.EMPTY_STRING, ServletUtil.getDomainFromRequestURL( null ) );
		assertEquals( EmptyObjectConstant.EMPTY_STRING, ServletUtil.getDomainFromRequestURL( EmptyObjectConstant.EMPTY_STRING ) );
		try {
			assertEquals( EmptyObjectConstant.EMPTY_STRING, ServletUtil.getDomainFromRequestURL( "yinshi.nc" ) );
			fail( "不应该执行我" );
		} catch ( IllegalParamException e ) {
		} catch ( Exception e ) {
			fail( "不应该执行我" );
		}
		assertEquals( expectedDomain, ServletUtil.getDomainFromRequestURL( "https://ops.jm.taobao.net/taokeeper-monitor/test.do" ) );

	}

	/** 获取 QueryString */
	@Test
	public void getQueryString() {
		this.reset();
		if ( ObjectUtil.isBlank( request, response ) ) {
			throw new RuntimeException( "Not init request and response success" );
		}

		// 测试 GET 方法的请求,有query string
		request.setMethod( HttpConstant.REQUEST_METHOD_GET );
		request.setQueryString( DEFAULT_REQUEST_QUERYSTRING );
		request.setRequestURI( DEFAULT_REQUEST_URI );
		assertEquals( DEFAULT_REQUEST_QUERYSTRING, ServletUtil.getQueryStringFromRequest( request ) );
		assertEquals( EMPTY_STRING, ServletUtil.getQueryStringFromRequest( null ) );

		// 测试 GET 方法的请求， 无 query string
		this.reset();
		request.setMethod( HttpConstant.REQUEST_METHOD_GET );
		request.setRequestURI( DEFAULT_REQUEST_URI );
		assertEquals( EMPTY_STRING, ServletUtil.getQueryStringFromRequest( request ) );
		assertEquals( EMPTY_STRING, ServletUtil.getQueryStringFromRequest( null ) );

		// 测试 POST 方法的请求,有query string
		this.reset();
		request.setMethod( HttpConstant.REQUEST_METHOD_POST );
		request.setRequestURI( DEFAULT_REQUEST_URI );
		this.setParameter();
		assertEquals( DEFAULT_REQUEST_QUERYSTRING, ServletUtil.getQueryStringFromRequest( request ) );
		assertEquals( EMPTY_STRING, ServletUtil.getQueryStringFromRequest( null ) );

		// 测试 POST 方法的请求,无query string
		this.reset();
		request.setMethod( HttpConstant.REQUEST_METHOD_POST );
		request.setRequestURI( DEFAULT_REQUEST_URI );
		assertEquals( EMPTY_STRING, ServletUtil.getQueryStringFromRequest( request ) );
		assertEquals( EMPTY_STRING, ServletUtil.getQueryStringFromRequest( null ) );

	}

	/**
	 * 测试获取Server( ip:port)
	 * @throws IllegalParamException
	 */
	@Test
	public void getServer() throws IllegalParamException {
		this.reset();

		String expectedServer = DEFAULT_SERVER_NAME + SymbolConstant.COLON + DEFAULT_SERVER_PORT;

		assertEquals( expectedServer, ServletUtil.getServerFromRequest( request ) );
		assertEquals( EmptyObjectConstant.EMPTY_STRING, ServletUtil.getServerFromRequest( null ) );

		/** 以下测试 getServerOfRequestURL */
		assertEquals( EmptyObjectConstant.EMPTY_STRING, ServletUtil.getServerFromRequestURL( null ) );
		assertEquals( EmptyObjectConstant.EMPTY_STRING, ServletUtil.getServerFromRequestURL( EmptyObjectConstant.EMPTY_STRING ) );
		try {
			assertEquals( EmptyObjectConstant.EMPTY_STRING, ServletUtil.getServerFromRequestURL( SymbolConstant.SLASH ) );
			fail( "不应该执行我" );
		} catch ( IllegalParamException e ) {
		} catch ( Exception e ) {
			fail( "不应该执行我" );
		}
		assertEquals( expectedServer, ServletUtil.getServerFromRequestURL( "https://ops.jm.taobao.net:8081/taokeeper-monitor/test.do" ) );

	}

	@Test
	public void getSimpleURLFromRequest() {
		this.reset();
		if ( ObjectUtil.isBlank( request, response ) ) {
			throw new RuntimeException( "Not init request and response success" );
		}

		String expectedSimpleURL = HttpConstant.HTTP_PREFIX + DEFAULT_SERVER_NAME + SymbolConstant.COLON + DEFAULT_SERVER_PORT + DEFAULT_REQUEST_URI;

		// 测试 GET 方法的请求,有query string
		request.setMethod( HttpConstant.REQUEST_METHOD_GET );
		request.setQueryString( DEFAULT_REQUEST_QUERYSTRING );
		request.setRequestURI( DEFAULT_REQUEST_URI );
		assertEquals( expectedSimpleURL, ServletUtil.getSimpleURLFromRequest( request ) );
		assertEquals( EMPTY_STRING, ServletUtil.getSimpleURLFromRequest( null ) );

		// 测试 GET 方法的请求， 无 query string
		this.reset();
		request.setMethod( HttpConstant.REQUEST_METHOD_GET );
		request.setRequestURI( DEFAULT_REQUEST_URI );
		assertEquals( expectedSimpleURL, ServletUtil.getSimpleURLFromRequest( request ) );
		assertEquals( EMPTY_STRING, ServletUtil.getSimpleURLFromRequest( null ) );

		// 测试 POST 方法的请求,有query string
		this.reset();
		request.setMethod( HttpConstant.REQUEST_METHOD_POST );
		request.setRequestURI( DEFAULT_REQUEST_URI );
		this.setParameter();
		assertEquals( expectedSimpleURL, ServletUtil.getSimpleURLFromRequest( request ) );
		assertEquals( EMPTY_STRING, ServletUtil.getSimpleURLFromRequest( null ) );

		// 测试 POST 方法的请求,无query string
		this.reset();
		request.setMethod( HttpConstant.REQUEST_METHOD_POST );
		request.setRequestURI( DEFAULT_REQUEST_URI );
		assertEquals( expectedSimpleURL, ServletUtil.getSimpleURLFromRequest( request ) );
		assertEquals( EMPTY_STRING, ServletUtil.getSimpleURLFromRequest( null ) );

	}

	@Test
	public void getFullURLFromRequest() {
		this.reset();
		if ( ObjectUtil.isBlank( request, response ) ) {
			throw new RuntimeException( "Not init request and response success" );
		}

		String expectedFullURL_WITH_QUERYSTRING = HttpConstant.HTTP_PREFIX + DEFAULT_SERVER_NAME + SymbolConstant.COLON + DEFAULT_SERVER_PORT
				+ DEFAULT_REQUEST_URI + SymbolConstant.QUESTION_SIGN + DEFAULT_REQUEST_QUERYSTRING;
		String expectedFullURL_WITHOUT_QUERYSTRING = HttpConstant.HTTP_PREFIX + DEFAULT_SERVER_NAME + SymbolConstant.COLON + DEFAULT_SERVER_PORT
				+ DEFAULT_REQUEST_URI;

		// 测试 GET 方法的请求,有query string
		request.setMethod( HttpConstant.REQUEST_METHOD_GET );
		request.setQueryString( DEFAULT_REQUEST_QUERYSTRING );
		request.setRequestURI( DEFAULT_REQUEST_URI );
		assertEquals( expectedFullURL_WITH_QUERYSTRING, ServletUtil.getFullURLFromRequest( request ) );
		assertEquals( EMPTY_STRING, ServletUtil.getFullURLFromRequest( null ) );

		// 测试 GET 方法的请求， 无 query string
		this.reset();
		request.setMethod( HttpConstant.REQUEST_METHOD_GET );
		request.setRequestURI( DEFAULT_REQUEST_URI );
		assertEquals( expectedFullURL_WITHOUT_QUERYSTRING, ServletUtil.getFullURLFromRequest( request ) );
		assertEquals( EMPTY_STRING, ServletUtil.getFullURLFromRequest( null ) );

		// 测试 POST 方法的请求,有query string
		this.reset();
		request.setMethod( HttpConstant.REQUEST_METHOD_POST );
		request.setRequestURI( DEFAULT_REQUEST_URI );
		this.setParameter();
		assertEquals( expectedFullURL_WITH_QUERYSTRING, ServletUtil.getFullURLFromRequest( request ) );
		assertEquals( EMPTY_STRING, ServletUtil.getFullURLFromRequest( null ) );

		// 测试 POST 方法的请求,无query string
		this.reset();
		request.setMethod( HttpConstant.REQUEST_METHOD_POST );
		request.setRequestURI( DEFAULT_REQUEST_URI );
		assertEquals( expectedFullURL_WITHOUT_QUERYSTRING, ServletUtil.getFullURLFromRequest( request ) );
		assertEquals( EMPTY_STRING, ServletUtil.getFullURLFromRequest( null ) );

	}

	

	@Test
	public void paraseIpAndPortFromServer() {

		String server1 = "1.2.37.111:8080";
		String server2 = "localhost:8080";
		String server3 = "1.2.37.111";

		String[] array1 = new String[] { "1.2.37.111", "8080" };
		String[] array2 = new String[] { "localhost", "8080" };
		String[] array3 = new String[] { "1.2.37.111", "" };

		assertArrayEquals( array1, ServletUtil.paraseIpAndPortFromServer( server1 ) );
		assertArrayEquals( array2, ServletUtil.paraseIpAndPortFromServer( server2 ) );
		assertArrayEquals( array3, ServletUtil.paraseIpAndPortFromServer( server3 ) );

	}
	
	
	
	// 重置request response
	private void reset() {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		if ( ObjectUtil.isBlank( request, response ) ) {
			throw new RuntimeException( "Not init request and response success" );
		}
		request.setProtocol( DEFAULT_PROTOCOL );
		request.setServerName( DEFAULT_SERVER_NAME );
		request.setServerPort( DEFAULT_SERVER_PORT );
	}

	// 设置request参数
	private void setParameter() {
		if ( ObjectUtil.isBlank( request, response ) ) {
			throw new RuntimeException( "Not init request and response success" );
		}
		request.setParameter( "method", "test" );
		request.setParameter( "userName", "yinshi.nc" );
		request.setParameter( "displayName", "银时" );
		request.setParameter( "staffNo", "41521" );
	}

}
