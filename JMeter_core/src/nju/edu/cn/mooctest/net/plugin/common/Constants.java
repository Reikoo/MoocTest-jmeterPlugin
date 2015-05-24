package nju.edu.cn.mooctest.net.plugin.common;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.jmeter.util.JMeterUtils;

public class Constants {
	public final static Map<String, String> ALTER_ROOT_PATHS = new HashMap<String, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = -4994488945890718086L;

		{
			put("Windows", "c:/mooctest/");
			put("Unix-like", System.getProperty("user.home") + "/mooctest/");
		}
	};
	public static final String MOOCTEST_ROOT_PATH = Constants
			.getMooctestRootPath();

	// public static final String DOWNLOAD_PATH = MOOCTEST_ROOT_PATH + "projects/";
	public static final String DOWNLOAD_PATH = JMeterUtils.getJMeterHome()
			+ "/projects/";
	public static final String AUTH_FILE = MOOCTEST_ROOT_PATH + "auth.mt";
	public static final String ADV_FILE = MOOCTEST_ROOT_PATH + "adv.mt";

	public static final String PLUGIN_ID = "mooctest.jmeter";

	public static final boolean TEST_ENABLED = false;

	private static String getMooctestRootPath() {
		String osName = System.getProperty("os.name");
		
		if (osName.indexOf("Windows") >= 0){
			return ALTER_ROOT_PATHS.get("Windows");
		}
		else if (osName.indexOf("Mac") >= 0){
			return ALTER_ROOT_PATHS.get("Unix-like");
		}
		else if (osName.indexOf("Linux") >= 0){
			return ALTER_ROOT_PATHS.get("Unix-like");
		}
		
		// return as Windows like in default
		return ALTER_ROOT_PATHS.get("Windows");
	}

	public static final String LINE_SEPARATOR= System.getProperty("line.separator");
	public static final String SEPARATOR = File.separator; // "\\";
}
