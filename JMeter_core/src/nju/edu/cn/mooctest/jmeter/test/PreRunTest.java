package nju.edu.cn.mooctest.jmeter.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.config.gui.HttpDefaultsGui;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.SearchByClass;

public class PreRunTest {
	public static void main(String[] args) {
		// JMeter Engine
        StandardJMeterEngine jmeter = new StandardJMeterEngine();

        // Initialize Properties, logging, locale, etc.
//        JMeterUtils.loadJMeterProperties(JMeterUtils.getPropDefault("jmeter.properties",""));
//        JMeterUtils.loadJMeterProperties("C:/Users/I314068/Desktop/MoocTest-jmeterPlugin/JMeter_core/src/nju/edu/cn/mooctest/jmeter/test/jmeter.properties");
        JMeterUtils.loadJMeterProperties("E:/apache-jmeter-2.12/apache-jmeter-2.12/bin/jmeter.properties");
//        JMeterUtils.setJMeterHome(JMeterUtils.getJMeterHome());
        JMeterUtils.setJMeterHome("E:/apache-jmeter-2.12/apache-jmeter-2.12/bin/");
        JMeterUtils.initLogging();// you can comment this line out to see extra log messages of i.e. DEBUG level
        JMeterUtils.initLocale();

        // Initialize JMeter SaveService
        try {
			SaveService.loadProperties();
		} catch (IOException e) {
			e.printStackTrace();
		}

        // Load existing .jmx Test Plan
        FileInputStream in = null;
		try {
			in = new FileInputStream("C:/Users/Administrator/Desktop/Zimbio.jmx");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        HashTree testPlanTree = null;
		try {
			testPlanTree = SaveService.loadTree(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
        try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        //add Summarizer output to get test progress in stdout like:
        // summary =      2 in   1.3s =    1.5/s Avg:   631 Min:   290 Max:   973 Err:     0 (0.00%)
        Summariser summer = null;
        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
        if (summariserName.length() > 0) {
            summer = new Summariser(summariserName);
        }


        // Store execution results into a .jtl file
        String logFile = "C:/Users/Administrator/Desktop/example.jtl";
        ResultCollector logger = new ResultCollector(summer);
        logger.setFilename(logFile);
        testPlanTree.add(testPlanTree.getArray()[0], logger);

        // Run JMeter Test
        jmeter.configure(testPlanTree);
        jmeter.run();
	}
}
