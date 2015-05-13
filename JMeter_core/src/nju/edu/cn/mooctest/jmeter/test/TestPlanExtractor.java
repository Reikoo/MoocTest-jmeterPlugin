package nju.edu.cn.mooctest.jmeter.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TestPlanExtractor {
private static final String ON_SAMPLER_ERROR_REGEX = "//stringProp[@name='ThreadGroup.on_sample_error']";
	
	private static final String NUM_THREADS_REGEX = "//stringProp[@name='ThreadGroup.num_threads']";
	
	private static final String RAMP_TIME_REGEX = "//stringProp[@name='ThreadGroup.ramp_time']";
	
	private static final String LOOP_REGEX = "//stringProp[@name='LoopController.loops']";
	
	private static final String HEADER_REGEX = "//HeaderManager/elementProp[@elementType='Header']";
	
	private JSONObject thread_groups;
	private JSONObject header_manager;
	
	private String num_threads;
	private String on_sampler_error;
	private String ramp_time;
	private String loop_controller;
	
	public TestPlanExtractor() {
		thread_groups = new JSONObject();
		header_manager = new JSONObject();
		readTestConfigProperties();
	}
	
	private void readTestConfigProperties() {
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("TestConfig.properties");   
		Properties p = new Properties();   
		try {   
		   p.load(inputStream);   
		} catch (IOException e1) {   
		   e1.printStackTrace();   
		}   
		this.num_threads = p.getProperty("num_threads");
		this.on_sampler_error = p.getProperty("on_sampler_error");
		this.ramp_time = p.getProperty("ramp_time");
		this.loop_controller = p.getProperty("loop_controller");
	}
	
	private void readJMXFile(String filePath) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        InputStream input;

		try {
			builder = factory.newDocumentBuilder();
			input = new FileInputStream(new File(filePath));
			Document document = builder.parse(input);
	        
			//获取测试计划的线程组参数
			parseThreadGroup(document);
			parseHeaderManager(document);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}
	
	private void parseThreadGroup(Document document) {
        XPath xpath = XPathFactory.newInstance().newXPath();  
        try {
			Element on_sampler_error = (Element) xpath.evaluate(ON_SAMPLER_ERROR_REGEX, document, XPathConstants.NODE);
			Element num_threads = (Element) xpath.evaluate(NUM_THREADS_REGEX, document, XPathConstants.NODE);
			Element ramp_time = (Element) xpath.evaluate(RAMP_TIME_REGEX, document, XPathConstants.NODE);
			Element loop_controller = (Element) xpath.evaluate(LOOP_REGEX, document, XPathConstants.NODE);
			
			thread_groups.append("num_threads", num_threads.getTextContent());
			thread_groups.append("on_sampler_error", on_sampler_error.getTextContent());
			thread_groups.append("ramp_time", ramp_time.getTextContent());
			thread_groups.append("loop_controller", loop_controller.getTextContent());
			
        } catch (XPathExpressionException e) {
			e.printStackTrace();
		}
        
	}
	
	private void parseHeaderManager(Document document) {
		XPath xpath = XPathFactory.newInstance().newXPath();  
        try {
			NodeList headerNodes = (NodeList) xpath.evaluate(HEADER_REGEX, document, XPathConstants.NODESET);
			
			for (int i = 0; i < headerNodes.getLength(); i++) {
				Node node = headerNodes.item(i);
				String name = node.getChildNodes().item(0).getTextContent();
				String value = node.getChildNodes().item(1).getTextContent();
				header_manager.append(name, value);
			}
        } catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}
	
	public double calcuateTestPlan() {
		double score = 0.0;
		//TODO
		
		return score;
	}
	
	public static void main(String[] args) {
		TestPlanExtractor tpe = new TestPlanExtractor();
		tpe.readJMXFile("C:/Users/Administrator/Desktop/PerformanceTestPlanMemoryThread.jmx");
	}
	
	public JSONObject getThreadGroups() {
		return this.thread_groups;
	}
	
	public JSONObject getHeaderManager() {
		return this.header_manager;
	}
}
