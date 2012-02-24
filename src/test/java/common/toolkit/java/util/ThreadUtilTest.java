package common.toolkit.java.util;

import org.junit.Test;

/**
 * @author 银时 yinshi.nc@taobao.com
 */
public class ThreadUtilTest {

	@Test
	public void testStartThreadRunnableInt() {
		System.out.println( System.currentTimeMillis( ) );
		ThreadUtil.startThread( new Job(), 20 );
	}
	
	
	@Override
	protected void finalize() throws Throwable {
		System.out.println( System.currentTimeMillis( ) );
		super.finalize();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}



class Job implements Runnable{

	@Override
	public void run() {
		System.out.println( Thread.currentThread().getName() + System.nanoTime() );
	}
	
}
