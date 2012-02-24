package common.toolkit.java.util.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Test;

/**
 * Test of IOUtil
 * @author 银时 yinshi.nc@taobao.com
 */
public class IOUtilTest extends TestCase {

	@Test
	public void testConvertStringAndInputStream() throws IOException{
		
		String str = "我是一个中文\n";
		ByteArrayInputStream inputstream = new ByteArrayInputStream( str.getBytes( "GBK" ) );

		try {
			assertEquals( str, IOUtil.convertInputStream2String( inputstream, "GBK" ) );
		} catch ( IOException e ) {
		}
	}
	
	
	
	
	
	
	
	
}
