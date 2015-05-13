package nju.edu.cn.mooctest.jmeter.test;

import java.io.IOException;

import jodd.io.StreamGobbler;

import org.apache.jmeter.util.JMeterUtils;

public class NonGuiTest {
	public void runTestInWin(String testFile, String reportFile) {
		String[] cmd = new String[3];
		cmd[0] = "cmd.exe";
		cmd[1] = "/C";
//		cmd[1] = "\""+JMeterUtils.getJMeterBinDir()+"\"";
		cmd[2] = "cd E:/apache-jmeter-2.12/apache-jmeter-2.12/bin/";
		
		try {
			Process process = Runtime.getRuntime().exec(cmd);
			System.out.println("cd e:/apache-jmeter-2.12/apache-jmeter-2.12/bin/");
		} catch (IOException e) {
			e.printStackTrace();
		} 
		cmd[2] = "jmeter -n -t "+ testFile + " -l " + reportFile;
		try {
			Process process = Runtime.getRuntime().exec(cmd);
			System.out.println("The test is running");
			// any error message?  
			StreamGobbler errorGobbler = new   
			StreamGobbler(process.getErrorStream(), "ERROR");  
			// any output?  
			StreamGobbler outputGobbler = new   
			StreamGobbler(process.getInputStream(), "OUTPUT");  
			// kick them off  
			errorGobbler.start();  
			outputGobbler.start();  
			// any error???  
			int exitVal = process.waitFor();  
			System.out.println("ExitValue: " + exitVal);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		NonGuiTest test = new NonGuiTest();
		test.runTestInWin("C:/Users/Administrator/Desktop/Zimbio.jmx", "C:/Users/Administrator/Desktop/Zimbio.jtl");
	}
}
