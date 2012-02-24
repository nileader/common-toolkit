package common.toolkit.java.util;

import static common.toolkit.java.constant.EmptyObjectConstant.EMPTY_STRING;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import common.toolkit.java.constant.BaseConstant;
import common.toolkit.java.constant.EmptyObjectConstant;
import common.toolkit.java.entity.DateFormat;

/**
 * Description: String util class.
 * 
 * @author 银时 yinshi.nc@taobao.com
 */
public class DateUtil {

	
	/**
	 * Convert java.util.Date to String<br>
	 * 
	 * @return like format yyyy-MM-dd HH:mm:ss.
	 */
	public static String convertDate2String( Date date ) {
		if ( null == date )
			return EMPTY_STRING;
		long seconds = date.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
		Date dt = new Date( seconds );
		String dateString = sdf.format( dt );
		return StringUtil.trimToEmpty( dateString );
	}
	
	/**
	 * @return a formated date:"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd"
	 */
	public static String getNowTime( DateFormat dateFormat ) {

		String format = EmptyObjectConstant.EMPTY_STRING;

		switch (dateFormat) {
		case DateTime: {
			format = DateFormat.DateTime.getFormat();
			break;
		}
		case Date: {
			format = DateFormat.Date.getFormat();
			break;
		}
		default:
			format = DateFormat.Date.getFormat();
		}
		SimpleDateFormat sdf = new SimpleDateFormat( StringUtil.defaultIfBlank( format, DateFormat.Date.getFormat() ) );
		Date dt = new Date();
		String dateString = sdf.format( dt );
		return StringUtil.trimToEmpty( dateString );
	}
	
	
	/**
	 * @return a formated yesterday date:"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd"
	 */
	public static String getYesterdayTime( DateFormat dateFormat ) {

		String format = EmptyObjectConstant.EMPTY_STRING;

		switch (dateFormat) {
		case DateTime: {
			format = DateFormat.DateTime.getFormat();
			break;
		}
		case Date: {
			format = DateFormat.Date.getFormat();
			break;
		}
		default:
			format = DateFormat.Date.getFormat();
		}
		SimpleDateFormat sdf = new SimpleDateFormat( StringUtil.defaultIfBlank( format, DateFormat.Date.getFormat() ) );
		Date dt = new Date( System.currentTimeMillis() - BaseConstant.MILLISECONDS_OF_ONE_DAY );
		String dateString = sdf.format( dt );
		return StringUtil.trimToEmpty( dateString );
	}
	
	
	
	
	
	
	

	public static long getTimeMillisToAfterDaysHour( int days, int hourOfTomorrow ) throws Exception {

		if ( 0 == hourOfTomorrow )
			hourOfTomorrow = 2;

		Calendar calendar = Calendar.getInstance();

		int yearOfToday = calendar.get( Calendar.YEAR );
		int monthOfToday = calendar.get( Calendar.MONTH ) + 1;
		int dayOfToday = calendar.get( Calendar.DAY_OF_MONTH );

		calendar.set( Calendar.DAY_OF_MONTH, dayOfToday + days );
		if ( 31 == dayOfToday && days >=1 ) {
			calendar.set( Calendar.MONTH, monthOfToday + 1 );
		}
		if ( 12 == monthOfToday && 31 == dayOfToday && days>=1 ) {
			calendar.set( Calendar.YEAR, yearOfToday + 1 );
		}

		int dayOfTomorrow  = calendar.get( Calendar.DAY_OF_MONTH );
		int monthOfTomorrow  = calendar.get( Calendar.MONTH );
		int yearOfTomorrow = calendar.get( Calendar.YEAR );

		Calendar calendarOfTomorrow = new GregorianCalendar( yearOfTomorrow, monthOfTomorrow, dayOfTomorrow, hourOfTomorrow, 0, 0 );
		long startTimeMillis = System.currentTimeMillis();
		
		
		long timeMillisToAfterDaysHour = calendarOfTomorrow.getTime().getTime() - startTimeMillis; 
		
		if( 0 > timeMillisToAfterDaysHour )
			throw new Exception( "时间差为负数，可能设置有误" );
		
		return timeMillisToAfterDaysHour;

	}
	
	
	public static long getTimeMillisToTodayHour( int hourOfTomorrow ) throws Exception {
		return DateUtil.getTimeMillisToAfterDaysHour( 0, hourOfTomorrow );
	}
	
	
	
	
	
	
	
	
	
	
	

}
