package common.toolkit.java.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import common.toolkit.java.entity.DateFormat;
import common.toolkit.java.exception.IllegalParamException;

/**
 * 
 * Description:
 * @author 银时：yinshi.nc / yinshi.nc@taobao.com
 * @Date Jan 9, 2012
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
	public void getTimeMillisToAfterDaysHour() {

		long expectedTimeMillis24 = 1000 * 60 * 60 * 24;
		long expectedTimeMillis23 = 1000 * 60 * 60 * 23;
		long expectedTimeMillis1 = 1000 * 60 * 60 * 1;

		int hour = 11;
		System.err.println( "当前设置的时间是" + hour + ", 例如当前是16:23,那么hour应该设置为:" + 16 );
		long timeMillisToAfterDaysHour1 = 0;
		try {
			timeMillisToAfterDaysHour1 = DateUtil.getTimeMillisToAfterDaysHour( 1, hour );
		} catch ( Exception e ) {
			fail( "不应该执行我，注意修改本方法的hour" );
		}
		assertTrue( expectedTimeMillis23 <= timeMillisToAfterDaysHour1 && timeMillisToAfterDaysHour1 <= expectedTimeMillis24 );

		long timeMillisToAfterDaysHour0 = 0;
		try {
			timeMillisToAfterDaysHour0 = DateUtil.getTimeMillisToAfterDaysHour( 0, hour + 1 );
		} catch ( Exception e ) {
			fail( "不应该执行我，注意修改本方法的hour" );
		}
		assertTrue( 0 <= timeMillisToAfterDaysHour0 && timeMillisToAfterDaysHour0 <= expectedTimeMillis1 );

	}

	@Test
	public void getDaysBefore() {
		List< String > dates = new ArrayList< String >();
		dates.add( "2012-05-17" );
		dates.add( "2012-05-16" );

		System.err.println( "当前dates中设置的时间" + dates + ", 例如当前是2012-03-30,那么hour应该设置为:2012-03-29,2012-03-28" );

		assertEquals( dates, DateUtil.getDaysBefore( 2 ) );
	}
	
	@Test
	public void getNowTime() {
		long time = 1350548132652l;
		assertEquals( "2012-10-18 16:15:32", DateUtil.getNowTime( "yyyy-MM-dd HH:mm:ss", time ) );
		assertEquals( "2012-10-18T16:15:32Z", DateUtil.getNowTime( DateFormat.SolrDateTime, time ) );
		assertEquals( "2012-10-18 16:15:32", DateUtil.getNowTime( DateFormat.DateTime, time ) );
	}

	@Test
	public void getYesterday() {

		String date1 = "2012-05-17";
		String date2 = "2012-04-01";
		String date3 = "2012-03-01";
		String date4 = "2012-01-01";

		assertEquals( "2012-05-16", DateUtil.getYesterday( date1 ) );
		assertEquals( "2012-03-31", DateUtil.getYesterday( date2 ) );
		assertEquals( "2012-02-29", DateUtil.getYesterday( date3 ) );
		assertEquals( "2011-12-31", DateUtil.getYesterday( date4 ) );
		assertEquals( "2012-05-17", DateUtil.getYesterday( "" ) );

	}

	
	@Test
	public void isInAssignHour() throws IllegalParamException{
		
		assertTrue( DateUtil.isInAssignHour( 5, 8 ) );
		assertTrue( !DateUtil.isInAssignHour( 5, 6 ) );
		
	}
	
	
	
	
	
	
	
	
}
