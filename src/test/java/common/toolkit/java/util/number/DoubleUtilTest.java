package common.toolkit.java.util.number;


import static org.junit.Assert.*;

import org.junit.Test;



/**
 * @author 银时：yinshi.nc / yinshi.nc@taobao.com
 * @Date Jan 13, 2012
 */
public class DoubleUtilTest {

	private static final double DELTA = 1e-15;
	
	@Test
	public void subtractionToPositive() {
		assertEquals( 0, DoubleUtil.subtractionToPositive( 0, 0 ),DELTA );
		assertEquals( 2, DoubleUtil.subtractionToPositive( 2, 0 ),DELTA );
		assertEquals( 2, DoubleUtil.subtractionToPositive( 0, 2 ),DELTA );
		assertEquals( 1.20, DoubleUtil.subtractionToPositive( 1.15, 2.35 ),DELTA );
	}
}
