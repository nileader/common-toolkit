package common.toolkit.java.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.Test;

/**
 * 
 * Description:
 * @author   银时：yinshi.nc / yinshi.nc@taobao.com
 * @Date	 Jan 9, 2012
 */
public class DateUtilTest {

	
	@Test
	public void convertDate2String() {
		@SuppressWarnings("deprecation")
		Date date = new Date( 111, 7, 21, 19, 40, 35 );
		String dateFormat = DateUtil.convertDate2String( date );

		assertEquals( "2011-08-21 19:40:35", dateFormat );
	}
	
	@Test
	public void getTimeMillisToAfterDaysHour(){
		
		long expectedTimeMillis24 = 1000 * 60 * 60 * 24;
		long expectedTimeMillis23 = 1000 * 60 * 60 * 23;
		long expectedTimeMillis1  = 1000 * 60 * 60 * 1;
		
		int hour = 11;
		System.out.println( "当前设置的时间是" + hour + ", 例如当前是16:23,那么hour应该设置为:" + 16 );
		long timeMillisToAfterDaysHour1 = 0 ;
		try {
			timeMillisToAfterDaysHour1 = DateUtil.getTimeMillisToAfterDaysHour( 1, hour );
		} catch ( Exception e ) {
			fail( "不应该执行我，注意修改本方法的hour" );
		}
		assertTrue( expectedTimeMillis23 <= timeMillisToAfterDaysHour1 && timeMillisToAfterDaysHour1 <= expectedTimeMillis24  );
		
		long timeMillisToAfterDaysHour0 = 0 ;
		try {
			timeMillisToAfterDaysHour0 = DateUtil.getTimeMillisToAfterDaysHour( 0, hour+1 );
		} catch ( Exception e ) {
			fail( "不应该执行我，注意修改本方法的hour" );
		}
		assertTrue( 0 <= timeMillisToAfterDaysHour0 && timeMillisToAfterDaysHour0 <= expectedTimeMillis1  );
		
		
		
		
		
		
	}
	
}
