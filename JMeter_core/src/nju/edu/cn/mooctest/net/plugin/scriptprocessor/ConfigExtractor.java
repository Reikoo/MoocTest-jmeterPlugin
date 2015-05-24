package nju.edu.cn.mooctest.net.plugin.scriptprocessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ConfigExtractor {
	private static final String LOOP_REGEX = "//stringProp[@name='LoopController.loops']";
	
	private static final String FILE_ENCODING = "//CSVDataSet/stringProp[@name='fileEncoding']";
	
	private static final String FILE_NAME = "//CSVDataSet/stringProp[@name='filename']";
	
	private static final String VARIABLE_NAMES = "//CSVDataSet/stringProp[@name='variableNames']";
	
	private static final String SYNC_TIMES = "//SyncTimer/intProp[@name='groupSize']";
	private static final String SYNC_TIME_OUT = "//SyncTimer/longProp[@name='timeoutInMs']";
	
	private static final String SAMPLER_PATH = "//HTTPSamplerProxy/stringProp[@name='HTTPSampler.path']";
	
	private File file;
	public ConfigExtractor(File file) {
		this.file = file;
	}
	
	public Document readJMXFile() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        InputStream input;
        Document document = null;

		try {
			builder = factory.newDocumentBuilder();
			input = new FileInputStream(file);
			document = builder.parse(input);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return document;
	}
	
	public boolean isSyncTimerRight(Document document, int numThreads) {
		XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			NodeList timeOut = (NodeList) xpath.evaluate(SYNC_TIME_OUT, document, 
						XPathConstants.NODESET);
			NodeList groupSize = (NodeList) xpath.evaluate(SYNC_TIMES, document, 
					XPathConstants.NODESET);
			if (timeOut == null || groupSize == null || timeOut.getLength()==0 || groupSize.getLength()==0) {
				return true;
			}
			String content = timeOut.item(0).getTextContent();
			if (Integer.valueOf(content)< 0) {
				return false;
			}else if (numThreads % Integer.valueOf(groupSize.item(0).getTextContent()) !=0
					&& Integer.valueOf(content) == 0) {
				return false;
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public List<CSVFile> parseCSVDataSet(Document document) {
		List<CSVFile> files = new ArrayList<CSVFile>();
        XPath xpath = XPathFactory.newInstance().newXPath();  
        try {
			NodeList nodeList = (NodeList) xpath.evaluate(FILE_ENCODING, document, 
					XPathConstants.NODESET);
			NodeList fileNameList = (NodeList) xpath.evaluate(FILE_NAME, document, 
					XPathConstants.NODESET);
			NodeList variablesList = (NodeList) xpath.evaluate(VARIABLE_NAMES, document, 
					XPathConstants.NODESET);
			if (nodeList == null || fileNameList == null || variablesList == null
					|| nodeList.getLength() == 0 || fileNameList.getLength()==0 
					|| variablesList.getLength()==0) {
				return files;
			}
			for (int i=0; i<fileNameList.getLength(); i++) {
				Node node = nodeList.item(i);
				String fileEncoding = node.getTextContent();
				String fileName = fileNameList.item(i).getTextContent();
				String varStr = variablesList.item(i).getTextContent();
				String[] variables = varStr.split(",");
				CSVFile file = new CSVFile(fileEncoding, fileName, variables);
				files.add(file);
			}
			
        } catch (XPathExpressionException e) {
			e.printStackTrace();
		}
        return files;
	}
	
	public int getLoopsInt(Document document) {
		XPath xpath = XPathFactory.newInstance().newXPath();
		Node node = null;
		try {
			node = (Node) xpath.evaluate(LOOP_REGEX, document, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		if (node == null) {
			return -1;
		}
		return Integer.parseInt(node.getTextContent());
	}
	
	public Map<Integer, List<String>> getSyncTimesSibling(Document document) {
		XPath xpath = XPathFactory.newInstance().newXPath();
		Map<Integer, List<String>> syncTimesMap = new HashMap<Integer, List<String>>();
		try {
			NodeList nodeList = (NodeList) xpath.evaluate(SYNC_TIMES, document, 
					XPathConstants.NODESET);
			if (nodeList == null || nodeList.getLength()==0) {
				return syncTimesMap;
			}
			List<String> samplerPath = new ArrayList<String>();
			NodeList httpSamplers = (NodeList) xpath.evaluate(SAMPLER_PATH, document, 
					XPathConstants.NODESET);
			if (httpSamplers == null || httpSamplers.getLength()==0) {
				return syncTimesMap;
			}
			for (int i=0; i<httpSamplers.getLength(); i++) {
				Node node = httpSamplers.item(i);
				samplerPath.add(node.getTextContent());
			}
			syncTimesMap.put(Integer.valueOf(nodeList.item(0).getTextContent()), samplerPath);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return syncTimesMap;
	}
	
	public class CSVFile {
		private String fileEncoding;
		private String fileName;
		private String[] variables;
		
		public CSVFile(String fileEncoding, String fileName, String[] variables) {
			this.setFileEncoding(fileEncoding);
			this.setFileName(fileName);
			this.setVariables(variables);
		}

		public String getFileEncoding() {
			return fileEncoding;
		}

		public void setFileEncoding(String fileEncoding) {
			this.fileEncoding = fileEncoding;
		}

		public String getFileName() {
			return fileName;
		}

		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		public String[] getVariables() {
			return variables;
		}

		public void setVariables(String[] variables) {
			this.variables = variables;
		}
	}
	
}
