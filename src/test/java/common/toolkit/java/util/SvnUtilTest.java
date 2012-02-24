package common.toolkit.java.util;

import static common.toolkit.java.constant.EmptyObjectConstant.EMPTY_STRING;
import static org.junit.Assert.*;

import org.junit.Test;

public class SvnUtilTest {
@Test
public void testParseFileProjectPathFromSvnPath() {

	String svnPath = "http://svn.common.taobao.net/repos/plugins/trunk/reviewboardtest/pagecache-webx3-ga-example/pagecache-webx3-ga-example-web/src/main/java/com/alibaba/webx/pagecache/app1/module/screen/Cached.java";
	String svnPath2 = "http://svn.common.taobao.net/repos/plugins/branches/reviewboardtest/pagecache-webx3-ga-example/pagecache-webx3-ga-example-web/src/main/java/com/alibaba/webx/pagecache/app1/module/screen/Cached.java";
	String svnPath3 = "http://svn.common.taobao.net/repos/plugins/branches/reviewboardtest/pagecache-webx3-ga-example/pagecache-webx3-ga-example-web/src/main/trunk/com/alibaba/webx/pagecache/app1/module/screen/Cached.java";

	String expected = "reviewboardtest/pagecache-webx3-ga-example/pagecache-webx3-ga-example-web/src/main/java/com/alibaba/webx/pagecache/app1/module/screen/Cached.java";
	String expected2 = "reviewboardtest/pagecache-webx3-ga-example/pagecache-webx3-ga-example-web/src/main/trunk/com/alibaba/webx/pagecache/app1/module/screen/Cached.java";

	try {
		assertEquals( expected, SvnUtil.parseFileProjectPathFromSvnPath( svnPath ) );
	} catch ( Exception e ) {
		fail( "Here should not throw a exception: " + e.getMessage() );
	}

	try {
		assertEquals( expected, SvnUtil.parseFileProjectPathFromSvnPath( svnPath2 ) );
	} catch ( Exception e ) {
		fail( "Here should not throw a exception: " + e.getMessage() );
	}

	try {
		assertEquals( expected2, SvnUtil.parseFileProjectPathFromSvnPath( svnPath3 ) );
	} catch ( Exception e ) {
		fail( "Here should not throw a exception: " + e.getMessage() );
	}

	try {
		assertEquals( EMPTY_STRING, SvnUtil.parseFileProjectPathFromSvnPath( "" ) );
	} catch ( Exception e ) {
		fail( "Here should not throw a exception: " + e.getMessage() );
	}

	try {
		SvnUtil.parseFileProjectPathFromSvnPath( "nileader" );
		fail( "Here should throw a exception" );
	} catch ( Exception e ) {
	}

}

@Test
public void testParseFileProjectPathFromSvnPathWithSvnPrefix() {

	String svnPath = "http://svn.common.taobao.net/repos/plugins/trunk/reviewboardtest/pagecache-webx3-ga-example/pagecache-webx3-ga-example-web/src/main/java/com/alibaba/webx/pagecache/app1/module/screen/Cached.java";
	String svnPath2 = "http://svn.common.taobao.net/repos/plugins/branches/reviewboardtest/pagecache-webx3-ga-example/pagecache-webx3-ga-example-web/src/main/java/com/alibaba/webx/pagecache/app1/module/screen/Cached.java";
	String svnPath3 = "http://svn.common.taobao.net/repos/plugins/branches/reviewboardtest/pagecache-webx3-ga-example/pagecache-webx3-ga-example-web/src/main/trunk/com/alibaba/webx/pagecache/app1/module/screen/Cached.java";

	String expected = "trunk/reviewboardtest/pagecache-webx3-ga-example/pagecache-webx3-ga-example-web/src/main/java/com/alibaba/webx/pagecache/app1/module/screen/Cached.java";
	String expected2 = "branches/reviewboardtest/pagecache-webx3-ga-example/pagecache-webx3-ga-example-web/src/main/trunk/com/alibaba/webx/pagecache/app1/module/screen/Cached.java";
	String expected3 = "branches/reviewboardtest/pagecache-webx3-ga-example/pagecache-webx3-ga-example-web/src/main/java/com/alibaba/webx/pagecache/app1/module/screen/Cached.java";
	try {
		assertEquals( expected, SvnUtil.parseFileProjectPathFromSvnPathWithSvnPrefix( svnPath ) );
	} catch ( Exception e ) {
		fail( "Here should not throw a exception: " + e.getMessage() );
	}

	try {
		assertEquals( expected3, SvnUtil.parseFileProjectPathFromSvnPathWithSvnPrefix( svnPath2 ) );
	} catch ( Exception e ) {
		fail( "Here should not throw a exception: " + e.getMessage() );
	}

	try {
		assertEquals( expected2, SvnUtil.parseFileProjectPathFromSvnPathWithSvnPrefix( svnPath3 ) );
	} catch ( Exception e ) {
		fail( "Here should not throw a exception: " + e.getMessage() );
	}

	try {
		assertEquals( EMPTY_STRING, SvnUtil.parseFileProjectPathFromSvnPathWithSvnPrefix( "" ) );
	} catch ( Exception e ) {
		fail( "Here should not throw a exception: " + e.getMessage() );
	}

	try {
		SvnUtil.parseFileProjectPathFromSvnPath( "nileader" );
		fail( "Here should throw a exception" );
	} catch ( Exception e ) {
	}

}

}
