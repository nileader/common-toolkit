package common.toolkit.java.util.collection;


import java.io.File;
import java.util.ArrayList;



/**
 * 类说明: 数组相关工具类
 * @author 银时 yinshi.nc@taobao.com
 */
public class ArrayUtil {
	
	public static File[] getLastNElementOfArray( File[] fileArray, int n ){
		
		if( null == fileArray || 0 == fileArray.length ){
			return null;
		}
		//如果要获取的个数大于等于数据长度, 那么返回全部
		if( n >= fileArray.length )
			return fileArray;

		//取最后n个元素返回
		int index = fileArray.length - n;
		File[] fileArray2 = new File[n];
		for( int i = index, j = 0; i < fileArray.length; i++, j++ ){
			fileArray2[j] = fileArray[i];
		}
		return fileArray2;
	}
	
	/**
	 * Convert String[] to ArrayList<String>
	 * @return ArrayList<String>
	 */
	public static ArrayList<String> toArrayList( String[] array ){
		ArrayList<String> arrayList = new ArrayList<String>();
		if( null == array ||  0 == array.length ){
			return arrayList;
		}
		for( int i = 0; i < array.length; i++ ){
			arrayList.add( array[i] );
		}
		return arrayList;
	}
	
	
	
}