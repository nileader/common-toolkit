package common.toolkit.java.util.collection;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * 类说明: 
 * @author 银时 yinshi.nc@taobao.com
 */
public class ArrayListUtilTest {

	@Test
	public void testSelect(){
	
		List<String> arrayList = new ArrayList< String >();
		arrayList.add( "yinshi1" );
		arrayList.add( "yinshi2" );
		arrayList.add( "yinshi3" );
		arrayList.add( "yinshi4" );
		arrayList.add( "yinshi5" );
		arrayList.add( "yinshi6" );
		arrayList.add( "yinshi7" );
		arrayList.add( "yinshi8" );
		arrayList.add( "yinshi9" );
		arrayList.add( "yinshi10" );
		

		//每隔2个取一个
		List<String> arrayList2 = new ArrayList< String >();
		arrayList2.add( "yinshi1" );
		arrayList2.add( "yinshi3" );
		arrayList2.add( "yinshi5" );
		arrayList2.add( "yinshi7" );
		arrayList2.add( "yinshi9" );
		
		//每隔3个取一个
		List<String> arrayList3 = new ArrayList< String >();
		arrayList3.add( "yinshi1" );
		arrayList3.add( "yinshi4" );
		arrayList3.add( "yinshi7" );
		arrayList3.add( "yinshi10" );		
		
		
		//每隔9个取一个
		List<String> arrayList9 = new ArrayList< String >();
		arrayList9.add( "yinshi1" );
		arrayList9.add( "yinshi10" );
		
		//每隔10个取一个
		List<String> arrayList10 = new ArrayList< String >();
		arrayList10.add( "yinshi1" );
		
		//每隔11个取一个
		List<String> arrayList11 = new ArrayList< String >();
		arrayList11.add( "yinshi1" );
		
		
		assertTrue( arrayList.equals( ArrayListUtil.select( (ArrayList<String>) arrayList, 0 ) ) );
		assertTrue( arrayList.equals( ArrayListUtil.select( (ArrayList<String>) arrayList, 1 ) ) );
		assertTrue( arrayList.equals( ArrayListUtil.select( (ArrayList<String>) arrayList, -1 ) ) );
		assertTrue( arrayList2.equals( ArrayListUtil.select( (ArrayList<String>) arrayList, 2 ) ) );
		assertTrue( arrayList3.equals( ArrayListUtil.select( (ArrayList<String>) arrayList, 3 ) ) );
		assertTrue( arrayList9.equals( ArrayListUtil.select( (ArrayList<String>) arrayList, 9 ) ) );
		assertTrue( arrayList11.equals( ArrayListUtil.select( (ArrayList<String>) arrayList, 11 ) ) );
	}
	
	
	
}
