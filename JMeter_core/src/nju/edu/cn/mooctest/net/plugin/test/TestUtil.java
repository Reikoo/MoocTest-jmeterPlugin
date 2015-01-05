package nju.edu.cn.mooctest.net.plugin.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import nju.edu.cn.mooctest.net.plugin.common.Constants;


public class TestUtil {
	public static void appendToFile(File file , String content) throws IOException{
		if (!Constants.TEST_ENABLED)
			return;
		if (!file.exists()){
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file , true);
		fw.write(content);
		fw.write("\n");
		
		fw.flush();
		fw.close();
	}
	
	public static void writeToFile(File file , String content) throws IOException{
		if (!Constants.TEST_ENABLED)
			return;
		if (!file.exists()){
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file);
		fw.write(content);
		fw.write('\n');
		
		fw.flush();
		fw.close();
	}
}
