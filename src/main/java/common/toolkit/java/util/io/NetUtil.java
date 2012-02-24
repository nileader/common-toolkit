package common.toolkit.java.util.io;


import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	 * @param int port	           端口
	 * @return  boolean  是否是合法端口
	 */
	public static boolean isLegalPort( int port ){
		if( port <= 0 || port > 65535 ){
			return false;
		}
		return true;
	}
	
	/**
	 * 检测是否是合法的端口.
	 * @param String port	           端口
	 * @return  boolean  是否是合法端口
	 */
	public static boolean isLegalPort( String port ){
		try {
			return isLegalPort( Integer.parseInt( port ) );
		} catch ( NumberFormatException e ) {
			return false;
		}
	}
	
	
	/**
	 * 检测是否是合法的IP.
	 * @param String ip	        IP
	 * @return  boolean  是否是合法IP
	 */
	public static boolean isLegalIP( String ip ){
		Matcher match = PATTERN_OF_IP.matcher( ip );            
		return match.matches();
	}
	
	
	
	/**
	 * 检查机器是否开启指定端口
	 * @param hostIp	机器ip
	 * @param port		机器port
	 * @return 			是否开启
	 */
	public static boolean isHostOpenPort( String hostIp, int port ) throws Exception{
		
		
		if( StringUtil.isBlank( hostIp ) ){
			return false;
		}
		
		InetAddress address=InetAddress.getByName( hostIp );
		if( !address.isReachable( 2000 ) ){
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
		}finally{
			if( null != socket )
				socket.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}