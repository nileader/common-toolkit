package common.toolkit.java.util.collection;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;

import org.junit.Test;

/**
 * 类说明: 
 * @author 银时 yinshi.nc@taobao.com
 */
public class ArrayUtilTest {

	
	@Test
	public void testetLastNElementOfArray() throws Exception {
		
		File file1 = new File( "file1" );
		File file2 = new File( "file2" );
		File file3 = new File( "file3" );
		File file4 = new File( "file4" );
		File file5 = new File( "file5" );
		File file6 = new File( "file6" );
		File file7 = new File( "file7" );
		File file8 = new File( "file8" );
		File file9 = new File( "file9" );
		File file10 = new File( "file10" );
		
		
		File[] files10 = {file1, file2, file3, file4, file5, file6, file7, file8, file9, file10};
		
		File[] files3Expected  = {file8, file9, file10};
		File[] files3Expected_ = {file7, file9, file10};
		
		File[] files4Expected  = {file7, file8, file9, file10};
		
		File[] files10Expected  = {file1, file2, file3, file4, file5, file6, file7, file8, file9, file10};
		File[] files11Expected  = {file1, file2, file3, file4, file5, file6, file7, file8, file9, file10};
		
		File[] fileArray3 = ( File[] ) ArrayUtil.getLastNElementOfArray( files10, 3 );
		
		File[] fileArray4 = ( File[] ) ArrayUtil.getLastNElementOfArray( files10, 4 );
		
		File[] fileArray10 = ( File[] ) ArrayUtil.getLastNElementOfArray( files10, 10 );
		File[] fileArray11 = ( File[] ) ArrayUtil.getLastNElementOfArray( files10, 11 );

		
		assertArrayEquals( files3Expected, fileArray3 );
		assertNotSame( files3Expected_, fileArray3 );
		
		assertArrayEquals( files4Expected, fileArray4 );
		
		assertArrayEquals( files10Expected, fileArray10 );
		assertArrayEquals( files11Expected, fileArray11 );
		
	}
	
	public void testToArrayList(){
		
		String[] test = { "yinshi.nc","nileader","yinshi.nc@taobao.com","nileader" };
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add( "yinshi.nc" );
		arrayList.add( "nileader" );
		arrayList.add( "yinshi.nc@taobao.com" );
		arrayList.add( "nileader" );
		
		assertTrue( arrayList == ArrayUtil.toArrayList( test ) );
		assertTrue(  new ArrayList<String>() == ArrayUtil.toArrayList( null ) );
		
	}
	
	
	
	
}
