package nju.edu.cn.mooctest.net.plugin.common;

import org.apache.jmeter.util.JMeterUtils;

public class Constants {
	public static final String MOOCTEST_ROOT_PATH = "c:/mooctest-jmeter/";
//	public static final String DOWNLOAD_PATH = MOOCTEST_ROOT_PATH + "projects/";
	public static final String DOWNLOAD_PATH = JMeterUtils.getJMeterHome() + "projects/";
	public static final String AUTH_FILE = MOOCTEST_ROOT_PATH + "auth.mt";
	public static final String ADV_FILE = MOOCTEST_ROOT_PATH + "adv.mt";
	
	public static final String PLUGIN_ID = "mooctest.jmeter";
	
	public static final boolean TEST_ENABLED = false;
}
