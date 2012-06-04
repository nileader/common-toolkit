package common.toolkit.java.util.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;

import common.toolkit.java.constant.EmptyObjectConstant;
import common.toolkit.java.constant.RegExpConstant;
import common.toolkit.java.constant.SymbolConstant;
import common.toolkit.java.util.StringUtil;
import common.toolkit.java.util.collection.CollectionUtil;
import common.toolkit.java.util.number.IntegerUtil;

/**
 * 绫昏鏄� 缃戠粶鏈哄叧宸ュ叿绫�
 * @author 閾舵椂 yinshi.nc@taobao.com
 */
public class NetUtil {

	public static final Pattern PATTERN_OF_IP = Pattern.compile( RegExpConstant.REG_EXP_OF_IP );

	public static final int DEFAULT_CONNECTION_TIMEOUT = 3000;

	/**
	 * 妫�祴鏄惁鏄悎娉曠殑绔彛.
	 * @param int port 绔彛
	 * @return boolean 鏄惁鏄悎娉曠鍙�
	 */
	public static boolean isLegalPort( int port ) {
		if ( port <= 0 || port > 65535 ) {
			return false;
		}
		return true;
	}

	/**
	 * 妫�祴鏄惁鏄悎娉曠殑绔彛.
	 * @param String port 绔彛
	 * @return boolean 鏄惁鏄悎娉曠鍙�
	 */
	public static boolean isLegalPort( String port ) {
		try {
			return isLegalPort( Integer.parseInt( port ) );
		} catch ( NumberFormatException e ) {
			return false;
		}
	}

	/**
	 * 妫�祴鏄惁鏄悎娉曠殑IP.
	 * @param String ip IP
	 * @return boolean 鏄惁鏄悎娉旾P
	 */
	public static boolean isLegalIP( String ip ) {
		Matcher match = PATTERN_OF_IP.matcher( ip );
		return match.matches();
	}

	/**
	 * 妫�煡鏈哄櫒鏄惁寮�惎鎸囧畾绔彛
	 * @param hostIp 鏈哄櫒ip
	 * @param port 鏈哄櫒port
	 * @return 鏄惁寮�惎
	 */
	public static boolean isHostOpenPort( String hostIp, int port ) throws Exception {

		if ( StringUtil.isBlank( hostIp ) ) {
			return false;
		}

		InetAddress address = InetAddress.getByName( hostIp );
		if ( !address.isReachable( 2000 ) ) {
			throw new Exception( "Can't connect host in 2000 ms: " + hostIp );
		}

		Socket socket = null;
		try {
			socket = new Socket( hostIp, port );
			return true;
		} catch ( UnknownHostException e ) {
			throw new Exception( "UnknownHost: " + hostIp );
		} catch ( IOException e ) {
			return false;
		} finally {
			if ( null != socket )
				socket.close();
		}
	}

	public static String getContentOfUrl( String url, int connectionTimeout ) throws HttpException, IOException {

		connectionTimeout = IntegerUtil.defaultIfZero( connectionTimeout, DEFAULT_CONNECTION_TIMEOUT );
		// 鏋勯�HttpClient鐨勫疄渚�
		HttpClient httpClient = new HttpClient();
		// 鍒涘缓GET鏂规硶鐨勫疄渚�
		GetMethod getMethod = new GetMethod( url );
		// 浣跨敤绯荤粺鎻愪緵鐨勯粯璁ょ殑鎭㈠绛栫暐
		getMethod.getParams().setParameter( HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler() );
		HttpConnectionManagerParams managerParams = httpClient.getHttpConnectionManager().getParams();
		// 璁剧疆杩炴帴瓒呮椂鏃堕棿(鍗曚綅姣)
		managerParams.setConnectionTimeout( connectionTimeout );
		// 璁剧疆璇绘暟鎹秴鏃舵椂闂�鍗曚綅姣)
		managerParams.setSoTimeout( 60000 );
		try {
			// 鎵цgetMethod
			int statusCode = httpClient.executeMethod( getMethod );
			if ( statusCode != HttpStatus.SC_OK ) {
				System.err.println( "Method failed: " + getMethod.getStatusLine() );
			}

			return IOUtil.convertInputStream2String( getMethod.getResponseBodyAsStream(), getMethod.getResponseCharSet() );
		} finally {
			// 閲婃斁杩炴帴
			getMethod.releaseConnection();
		}
	}
	
	public static String getContentOfUrlByPostMethod( String url, NameValuePair[] nameValuePair, int connectionTimeout ) throws HttpException, IOException {
		connectionTimeout = IntegerUtil.defaultIfZero( connectionTimeout, DEFAULT_CONNECTION_TIMEOUT );
		// 鏋勯�HttpClient鐨勫疄渚�
		HttpClient httpClient = new HttpClient();
		// 鍒涘缓GET鏂规硶鐨勫疄渚�
		PostMethod postMethod = new PostMethod( url );
		
		postMethod.setRequestBody( nameValuePair );
		
		// 浣跨敤绯荤粺鎻愪緵鐨勯粯璁ょ殑鎭㈠绛栫暐
		postMethod.getParams().setParameter( HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler() );
		HttpConnectionManagerParams managerParams = httpClient.getHttpConnectionManager().getParams();
		// 璁剧疆杩炴帴瓒呮椂鏃堕棿(鍗曚綅姣)
		managerParams.setConnectionTimeout( connectionTimeout );
		// 璁剧疆璇绘暟鎹秴鏃舵椂闂�鍗曚綅姣)
		managerParams.setSoTimeout( 60000 );
		try {
			// 鎵цgetMethod
			int statusCode = httpClient.executeMethod( postMethod );
			if ( statusCode != HttpStatus.SC_OK ) {
				System.err.println( "Method failed: " + postMethod.getStatusLine() );
			}
			return IOUtil.convertInputStream2String( postMethod.getResponseBodyAsStream(), postMethod.getResponseCharSet() );
		} finally {
			// 閲婃斁杩炴帴
			postMethod.releaseConnection();
		}
	}

	public static Map< String, String > getContentOfUrl( Map< String, String > urls, int connectionTimeout ) throws HttpException, IOException {

		Map< String, String > bodyContents = new HashMap< String, String >();

		connectionTimeout = IntegerUtil.defaultIfZero( connectionTimeout, DEFAULT_CONNECTION_TIMEOUT );
		// 鏋勯�HttpClient鐨勫疄渚�
		HttpClient httpClient = new HttpClient();
		// 鍒涘缓GET鏂规硶鐨勫疄渚�
		GetMethod getMethod = new GetMethod();
		// 浣跨敤绯荤粺鎻愪緵鐨勯粯璁ょ殑鎭㈠绛栫暐
		getMethod.getParams().setParameter( HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler() );
		HttpConnectionManagerParams managerParams = httpClient.getHttpConnectionManager().getParams();
		// 璁剧疆杩炴帴瓒呮椂鏃堕棿(鍗曚綅姣)
		managerParams.setConnectionTimeout( connectionTimeout );
		// 璁剧疆璇绘暟鎹秴鏃舵椂闂�鍗曚綅姣)
		managerParams.setSoTimeout( 60000 );
		try {
			for ( String key : urls.keySet() ) {
				if ( StringUtil.isBlank( key ) )
					continue;
				String url = urls.get( key );
				if ( StringUtil.isBlank( url ) )
					continue;
				getMethod.setURI( new URI( StringUtil.trimToEmpty( url ), true, "UTF-8" ) );
				try {
					// 鎵цgetMethod
					int statusCode = httpClient.executeMethod( getMethod );
					if ( statusCode != HttpStatus.SC_OK ) {
						System.err.println( "Method failed: " + getMethod.getStatusLine() );
					}
					String content = IOUtil.convertInputStream2String( getMethod.getResponseBodyAsStream(), getMethod.getResponseCharSet() );
					bodyContents.put( key, content );
					System.out.println( content );
				} catch ( Throwable e ) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return bodyContents;

		} finally {
			// 閲婃斁杩炴帴
			getMethod.releaseConnection();
		}
	}

	public static String getContentOfUrl( String url ) throws HttpException, IOException {
		return NetUtil.getContentOfUrl( url, DEFAULT_CONNECTION_TIMEOUT );
	}

	public static Map< String, String > getContentOfUrl( Map< String, String > urls ) throws HttpException, IOException {

		return NetUtil.getContentOfUrl( urls, DEFAULT_CONNECTION_TIMEOUT );
	}

	/**
	 * 192.168.37.111:51472 -> 192.168.37.111
	 * @param server 192.168.37.111:51472
	 * @return
	 */
	public static String getIpFromServer( String server ) {

		if ( StringUtil.isBlank( server ) ) {
			return EmptyObjectConstant.EMPTY_STRING;
		}
		try {
			return StringUtil.trimToEmpty( StringUtil.splitWithLeastLength( server, SymbolConstant.COLON, 1 )[0] );
		} catch ( Exception e ) {
			return EmptyObjectConstant.EMPTY_STRING;
		}
	}

	/**
	 * 192.168.37.111:51472 -> 192.168.37.111
	 * @param server 192.168.37.111:51472
	 * @return
	 */
	public static List< String > getIpFromServer( List< String > serverList ) {

		List< String > ipList = new ArrayList< String >();

		if ( CollectionUtil.isBlank( serverList ) ) {
			return ipList;
		}

		for ( String server : serverList ) {
			String ip = NetUtil.getIpFromServer( StringUtil.trimToEmpty( server ) );
			if ( StringUtil.isBlank( ip ) )
				continue;
			ipList.add( ip );
		}
		return ipList;
	}

}