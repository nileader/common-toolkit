package common.toolkit.java.util.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 类说明:
 * @author 银时 yinshi.nc@taobao.com
 */
public class FileUtilTest {

	static String DEFAULT_FILE_PATH = "d:/test.txt";
	static String DEFAULT_FILE_CONTENT = "abc\ndef\nhello world!";

	@BeforeClass
	public static void initialize() {
		File file = new File( DEFAULT_FILE_PATH );
		try {
			if ( !file.createNewFile() ) {
				fail( file.getPath() + " 创建失败" );
			}
		} catch ( Exception e ) {
			fail( file.getPath() + " 创建失败" );
		}
		file.deleteOnExit();

		try {
			FileUtil.write( DEFAULT_FILE_PATH, DEFAULT_FILE_CONTENT, false );
		} catch ( IOException e ) {
			fail( file.getPath() + " 写文件失败" );
		}
	}

	@Test
	public void testFileToListByLine() throws Exception {

		List< String > lineList = FileUtil.fileToListByLine( DEFAULT_FILE_PATH );

		List< String > lineListExpect = new LinkedList< String >();
		lineListExpect.add( "abc" );
		lineListExpect.add( "def" );
		lineListExpect.add( "hello world!" );

		assertEquals( lineList, lineListExpect );

	}

	@Test
	public void writeAndRead() throws IOException {

		FileUtil.write( DEFAULT_FILE_PATH, "中文\n", false );
		FileUtil.write( DEFAULT_FILE_PATH, "bbb\n", true );
		assertEquals( "中文\nbbb\n", FileUtil.readFile( DEFAULT_FILE_PATH ) );

	}

}
