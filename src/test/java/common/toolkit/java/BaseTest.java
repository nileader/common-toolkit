package common.toolkit.java;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class BaseTest {

	private static Properties globalProperties = new Properties();

	public static void initialize() throws IOException {
		InputStream inputStream = BaseTest.class.getResourceAsStream( "/config-test.properties" );
		globalProperties.load( inputStream );
	}

	public static String getConfigtationValue( String keyName ) {
		return globalProperties.getProperty( keyName );
	}

}
