package common.toolkit.java.util;

import static common.toolkit.java.constant.EmptyObjectConstant.EMPTY_STRING;

import java.io.StringReader;

import org.json.JSONException;
import org.json.JSONObject;

import com.sdicons.json.mapper.JSONMapper;
import com.sdicons.json.mapper.MapperException;
import com.sdicons.json.model.JSONValue;
import com.sdicons.json.parser.JSONParser;

/**
 * @author 银时 yinshi.nc@taobao.com
 */
public class JSONObjectUtil {

	/**
	 * 把json格式字符串解析成JSONObject对象，注意，本方法不会抛出任何异常。
	 */
	public static JSONObject paraseFromStringWithOutException( String jsonString ) {
		try {
			return new JSONObject( jsonString );
		} catch ( Exception e ) {
			return null;
		}
	}

	public static JSONObject paraseFromString( String jsonString ) throws JSONException {
		return new JSONObject( jsonString );
	}

	/**
	 * 这个方法执行的就是 jsonObject.getString( String key ), 只是这个不会抛出异常的。
	 */
	public static String getStringFromJSONObjectWithOutException( JSONObject jsonObject, String key ) {
		try {
			return jsonObject.getString( key );
		} catch ( JSONException e ) {
			return EMPTY_STRING;
		}
	}

	/**
	 * 这个方法执行的就是 jsonObject.getString( String key ), 会抛出异常的。
	 * 
	 * @throws JSONException
	 */
	public static String getStringFromJSONObject( JSONObject jsonObject, String key ) throws JSONException {
		return jsonObject.getString( key );
	}

	
	/**
	 * 将一个对象转换为JSON格式的串
	 * @param object 要转换的 Object 对象
	 * @return 转换后的字符串
	 */
	public static String convertVO2String( Object object ) throws Exception {
		try {
			return JSONMapper.toJSON( object ).render( false );
		} catch ( MapperException e ) {
			throw new Exception( "把对象【" + object + "】转换为JSON字符串的时候出现问题了", e );
		}
	}

	/**
	 * 将一个JSON格式的字符串转换为Java对象
	 * 
	 * @param jsonStr 要转换的JSON格式的字符串
	 * @param destClass 要将这个JSON格式的字符串转换为什么类型的对象
	 * @return 转换之后的Java对象
	 */
	public static Object convertString2VO( String jsonStr, Class< ? > destClass ) throws Exception {
		try {
			// 先解释字符串为一个JSONValue
			JSONValue value = new JSONParser( new StringReader( jsonStr ) ).nextValue();
			return JSONMapper.toJava( value, destClass );
		} catch ( Exception e ) {
			throw new Exception( "在把字符串【" + jsonStr + "】转换为【" + destClass + "】类型的对象时，出现异常" + "，可能是你的字符串格式不对，请修正！", e );
		}
	}

}
