package common.toolkit.java.util.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;

import common.toolkit.java.constant.RegExpConstant;
import common.toolkit.java.util.StringUtil;

/**
 * 类说明: 网络机关工具类
 * @author 银时 yinshi.nc@taobao.com
 */
public class NetUtil {

	public static final Pattern PATTERN_OF_IP = Pattern.compile( RegExpConstant.REG_EXP_OF_IP );

	/**
	 * 检测是否是合法的端口.
	 * @param int port 端口
	 * @return boolean 是否是合法端口
	 */
	public static boolean isLegalPort( int port ) {
		if ( port <= 0 || port > 65535 ) {
			return false;
		}
		return true;
	}

	/**
	 * 检测是否是合法的端口.
	 * @param String port 端口
	 * @return boolean 是否是合法端口
	 */
	public static boolean isLegalPort( String port ) {
		try {
			return isLegalPort( Integer.parseInt( port ) );
		} catch ( NumberFormatException e ) {
			return false;
		}
	}

	/**
	 * 检测是否是合法的IP.
	 * @param String ip IP
	 * @return boolean 是否是合法IP
	 */
	public static boolean isLegalIP( String ip ) {
		Matcher match = PATTERN_OF_IP.matcher( ip );
		return match.matches();
	}

	/**
	 * 检查机器是否开启指定端口
	 * @param hostIp 机器ip
	 * @param port 机器port
	 * @return 是否开启
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

	public static String getContentOfUrl( String url ) throws HttpException, IOException {
		// 构造HttpClient的实例
		HttpClient httpClient = new HttpClient();
		// 创建GET方法的实例
		GetMethod getMethod = new GetMethod( url );
		// 使用系统提供的默认的恢复策略
		getMethod.getParams().setParameter( HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler() );
		HttpConnectionManagerParams managerParams = httpClient.getHttpConnectionManager().getParams();
		// 设置连接超时时间(单位毫秒)
		managerParams.setConnectionTimeout( 3000 );
		// 设置读数据超时时间(单位毫秒)
		managerParams.setSoTimeout( 20000 );
		try {
			// 执行getMethod
			int statusCode = httpClient.executeMethod( getMethod );
			if ( statusCode != HttpStatus.SC_OK ) {
				System.err.println( "Method failed: " + getMethod.getStatusLine() );
			}
			// 读取内容
			byte[] responseBody = getMethod.getResponseBody();
			// 处理内容
			return new String( responseBody );
		} finally {
			// 释放连接
			getMethod.releaseConnection();
		}
	}

}