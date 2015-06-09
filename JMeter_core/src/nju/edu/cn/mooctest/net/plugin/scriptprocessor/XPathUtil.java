package nju.edu.cn.mooctest.net.plugin.scriptprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
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

public class XPathUtil {
	private Document document;
	private static File file;
	
	public XPathUtil(File file) {
		this.file = file;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        InputStream input;

		try {
			builder = factory.newDocumentBuilder();
			input = new FileInputStream(file);
			this.document = builder.parse(input);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}
	
//	public static double sendHTTPRequests(Map<String, HTTPSamplerProxy> samplers, 
//			Map<String, CSVFile> dataFiles) {
//		Iterator it=samplers.keySet().iterator();
//		double success = 0;
//		int totalNum = 0;
//		int successNum = 0;
//		while(it.hasNext()){    
//		     String key;    
//		     HTTPSamplerProxy sampler;    
//		     key=it.next().toString();    
//		     sampler = samplers.get(key);
//		     
//		     if (dataFiles == null || dataFiles.isEmpty()) {
//		    	String urlStr = null;
//				try {
//					urlStr = sampler.getUrl().toString();
//				} catch (MalformedURLException e) {
//					e.printStackTrace();
//				}
//				urlStr = preProcess(urlStr);
//				URL url = null;
//				try {
//					url = new URL(urlStr);
//					HttpClient client = new HttpClient();
//					HttpMethod method = null;
//					if (sampler.getMethod().toLowerCase().equals("get")) {
//						method = new GetMethod(url.toString());
//						method.addRequestHeader("Content-Type", "text/html; charset=UTF-8");
//					} else {
//						PostMethod post = new PostMethod(url.toString());
//						//TODO
//					}
//					client.executeMethod(method);
//					int responseCode = method.getStatusCode();
//					if (HttpStatus.SC_OK == responseCode) {// 连接成功
//						successNum ++;
//					}
//				}catch (IllegalArgumentException e) {
//					e.printStackTrace();
//				}
//		     }
//		     
//		     //TODO
//		     try {
//				Iterator iter = dataFiles.keySet().iterator();
//				while (iter.hasNext()) {
//					String name = iter.next().toString();
//					CSVFile file = dataFiles.get(name);
//					List<Map<String, String>> vars = file.getVariables();
//					for (int i=0; i<vars.size(); i++) {
//						totalNum ++;
//						String urlStr = sampler.getUrl().toString();
//						urlStr = preProcess(urlStr);
//						Map<String, String> varPairs = vars.get(i);
//						Iterator iter2 = varPairs.keySet().iterator();
//						while (iter2.hasNext()) {
//							String varName = iter2.next().toString();
//							String varValue = varPairs.get(varName);
//							System.out.print("参数："+ varValue);
//							urlStr = urlStr.replaceAll("\\$\\{"+varName+"\\}", varValue);
//						}
//						System.out.println(urlStr);
//						URL url = null;
//						try {
//							url = new URL(urlStr);
//							HttpClient client = new HttpClient();
//							HttpMethod method = null;
//							if (sampler.getMethod().toLowerCase().equals("get")) {
//								method = new GetMethod(url.toString());
//								method.addRequestHeader("Content-Type", "text/html; charset=UTF-8");
//							} else {
//								PostMethod post = new PostMethod(url.toString());
//								//TODO
//							}
//							client.executeMethod(method);
//							int responseCode = method.getStatusCode();
//							if (HttpStatus.SC_OK == responseCode) {// 连接成功
//								successNum ++;
//							}
//						}catch (IllegalArgumentException e) {
//							e.printStackTrace();
//						}
//					}
//				}
//			} catch (MalformedURLException e) {
//				e.printStackTrace();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		success = 1.0 * successNum / totalNum;
//		return success;
//	}
	
//	public XPathUtil(String scriptFilePath) throws Exception { 
//		this.scriptFilePath = scriptFilePath;
//	    SAXReader saxReader = new SAXReader();
//	    this.document = saxReader.read(new File(scriptFilePath));
//	}
	
//	private Document readJMXFile() {
//		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder builder;
//        InputStream input;
//        Document document = null;
//
//		try {
//			builder = factory.newDocumentBuilder();
//			input = new FileInputStream(file);
//			document = builder.parse(input);
//		} catch (ParserConfigurationException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (SAXException e) {
//			e.printStackTrace();
//		}
//		return document;
//	}
	
	private static final String DELIMITER = "//CSVDataSet/stringProp[@name='delimiter']";
	private static final String FILE_NAME = "//CSVDataSet/stringProp[@name='filename']";
	private static final String VARIABLE_NAMES = "//CSVDataSet/stringProp[@name='variableNames']";
	
	private static String getAbsolutePath(String path) throws MalformedURLException{
		File csvfile = new File(path);
		if (csvfile.exists()) {
			return path;
		} 
//		try {
			URL url = new URL("file:///" + file.getAbsolutePath());
			URL absoluteURL = new URL(url, path);
			return absoluteURL.getPath();
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		}
//		return null;
	}
	
	private static final String LOOP_REGEX = "//stringProp[@name='LoopController.loops']";
	public int getLoopsInt() {
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
	
	private static final String SYNC_TIMES = "//SyncTimer/intProp[@name='groupSize']";
	public int getSyncTimerGroup() {
		XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			Node node =  (Node) xpath.evaluate(SYNC_TIMES, document, 
					XPathConstants.NODE);
			if (node == null ) {
				return 0;
			} else {
				return Integer.valueOf(node.getTextContent());
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
//	public Map<String, CSVFile> getCSVDataSet() throws IOException {
//		String dataSetRegex = "//CSVDataSet";
//		Map<String, CSVFile> dataSets = new HashMap<String, CSVFile>();
//		List list = document.selectNodes(dataSetRegex);
//		for(Iterator iterator=list.iterator();iterator.hasNext();){
//			Element e = (Element) iterator.next();
//			CSVFile csvData = new CSVFile();
//			String varStr = null;
//			for (Iterator iter=e.elementIterator(); iter.hasNext();) {
//				Element subElement = (Element) iter.next();
//				String elementText = subElement.getTextTrim();
//				String attrValue = subElement.attributeValue("name");
//				if (attrValue.equals("filename")) {
//					csvData.setFileName(getAbsolutePath(elementText));
//				}
//				if (attrValue.equals("delimiter")) {
//					csvData.setDelimiter(elementText);
//				}
//				if (attrValue.equals("variableNames")) {
//					varStr = elementText;
//				}
//			}
//			if (varStr == null) {
//				dataSets.put(csvData.getFileName(), csvData);
//				break;
//			}
//			String[] variables = varStr.split(",");
//			List<Map<String, String>> vars = new ArrayList<Map<String, String>>();
//			BufferedReader br = new BufferedReader(new FileReader(csvData.getFileName()));
//			String data = null;
//			while( (data=br.readLine())!=null){  
//			      Map<String, String> varPair = new HashMap<String, String>();
//			      String[] values = data.split(csvData.getDelimiter());
//			      for (int i=0; i<variables.length; i++) {
//			    	  varPair.put(variables[i], values[i].trim());
//			      }
//			      vars.add(varPair);
//			}
//			csvData.setVariables(vars);
//			dataSets.put(csvData.getFileName(), csvData);
//		}
//		return dataSets;
//	}
	
	public Map<String, CSVFile> getCSVDataSet() throws IOException {
		Map<String, CSVFile> dataSets = new HashMap<String, CSVFile>();
		XPath xpath = XPathFactory.newInstance().newXPath();
		try {
			NodeList nodeList = (NodeList) xpath.evaluate(DELIMITER, document, 
					XPathConstants.NODESET);
			NodeList fileNameList = (NodeList) xpath.evaluate(FILE_NAME, document, 
					XPathConstants.NODESET);
			NodeList variablesList;
				variablesList = (NodeList) xpath.evaluate(VARIABLE_NAMES, document, 
						XPathConstants.NODESET);
			if (nodeList == null || fileNameList == null || variablesList == null
					|| nodeList.getLength() == 0 || fileNameList.getLength()==0 
					|| variablesList.getLength()==0) {
				return dataSets;
			}
			for (int i=0; i<fileNameList.getLength(); i++) {
				CSVFile csvData = new CSVFile();
				Node node = nodeList.item(i);
				String delimiter = node.getTextContent();
				String fileName = fileNameList.item(i).getTextContent();
				String varStr = variablesList.item(i).getTextContent();
				String[] variables = varStr.split(delimiter);
				String absolutePath = null;
				try {
					absolutePath = getAbsolutePath(fileName);
				} catch (MalformedURLException e) {
					
				} finally {
					csvData.setFileName(absolutePath);
					csvData.setDelimiter(delimiter);
					List<Map<String, String>> vars = new ArrayList<Map<String, String>>();
					File file = null;
					if (csvData.getFileName() != null) {
						file = new File(csvData.getFileName());
					}
					if (file == null || !file.exists()) {
						csvData.setVariables(null);
					} else {
						BufferedReader br = new BufferedReader(new FileReader(csvData.getFileName()));
						String data = null;
						while( (data=br.readLine())!=null){  
						      Map<String, String> varPair = new HashMap<String, String>();
						      String[] values = data.split(csvData.getDelimiter());
						      for (int j=0; j<variables.length; j++) {
						    	  varPair.put(variables[j], values[j].trim());
						      }
						      vars.add(varPair);
						}
						csvData.setVariables(vars);
					}
					dataSets.put(csvData.getFileName(), csvData);
				}
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
//		for(Iterator iterator=list.iterator();iterator.hasNext();){
//			Element e = (Element) iterator.next();
//			CSVFile csvData = new CSVFile();
//			String varStr = null;
//			for (Iterator iter=e.elementIterator(); iter.hasNext();) {
//				Element subElement = (Element) iter.next();
//				String elementText = subElement.getTextTrim();
//				String attrValue = subElement.attributeValue("name");
//				if (attrValue.equals("filename")) {
//					csvData.setFileName(getAbsolutePath(elementText));
//				}
//				if (attrValue.equals("delimiter")) {
//					csvData.setDelimiter(elementText);
//				}
//				if (attrValue.equals("variableNames")) {
//					varStr = elementText;
//				}
//			}
//			if (varStr == null) {
//				dataSets.put(csvData.getFileName(), csvData);
//				break;
//			}
//			String[] variables = varStr.split(",");
//			List<Map<String, String>> vars = new ArrayList<Map<String, String>>();
//			BufferedReader br = new BufferedReader(new FileReader(csvData.getFileName()));
//			String data = null;
//			while( (data=br.readLine())!=null){  
//			      Map<String, String> varPair = new HashMap<String, String>();
//			      String[] values = data.split(csvData.getDelimiter());
//			      for (int i=0; i<variables.length; i++) {
//			    	  varPair.put(variables[i], values[i].trim());
//			      }
//			      vars.add(varPair);
//			}
//			csvData.setVariables(vars);
//			dataSets.put(csvData.getFileName(), csvData);
//		}
		return dataSets;
	}
	
//	private static String preProcess(String urlStr) {
//		Pattern p = Pattern.compile("\\$\\{__CSVRead(.+?)\\}");
//		Matcher m = p.matcher(urlStr);
//	    while (m.find()) {// 遍历找到的所有大括号
//	       String param = m.group(1);//.replaceAll("\\{\\}", "");// 去掉括号
//	       String s = param.substring(1, param.length()-1);
//	       String[] str = s.split(",");
//	       BufferedReader br = null;
//		try {
//			br = new BufferedReader(new FileReader(str[0]));
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//			return null;
//		}
//	       String line = null;
//		try {
//			line = br.readLine();
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		}
//	       String[] values = line.split(",");
//	       int index = Integer.valueOf(str[1]);
//	       urlStr = urlStr.replace("${__CSVRead"+param+"}", values[index]);
//	    }
//	    return urlStr;
//	}
	
//	public List<SyncTimer> getSyncTimers() {
//		List<SyncTimer> syncTimers = new ArrayList<SyncTimer>();
//		String syncTimerRegex = "//SyncTimer";
//		List list = document.selectNodes(syncTimerRegex);
//		for (Iterator iter = list.iterator(); iter.hasNext();) {
//			Element ele = (Element) iter.next();
//			String sizeStr = ((Element)ele.element("intProp")).getTextTrim();
//			String timeStr = ((Element)ele.element("longProp")).getTextTrim();
//			int groupSize = Integer.valueOf(sizeStr);
//			long timeoutInMs = Long.valueOf(timeStr);
//			SyncTimer syncTimer = new SyncTimer(groupSize, timeoutInMs);
//			syncTimers.add(syncTimer);
//		}
//		return syncTimers;
//	}
	
   public class CSVFile {
	   private String fileName;
	   private String delimiter;
	   private List<Map<String, String>> variables;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getDelimiter() {
		return delimiter;
	}
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}
	public List<Map<String, String>> getVariables() {
		return variables;
	}
	public void setVariables(List<Map<String, String>> variables) {
		this.variables = variables;
	}
   }
   
//   public class SyncTimer {
//	   private int groupSize;
//	   private long timeoutInMs;
//	   
//	public SyncTimer(int groupSize, long timeoutInMs) {
//		this.groupSize = groupSize;
//		this.timeoutInMs = timeoutInMs;
//	}
//	   
//	public int getGroupSize() {
//		return groupSize;
//	}
//	public void setGroupSize(int groupSize) {
//		this.groupSize = groupSize;
//	}
//	public long getTimeoutInMs() {
//		return timeoutInMs;
//	}
//	public void setTimeoutInMs(long timeoutInMs) {
//		this.timeoutInMs = timeoutInMs;
//	}
//   }
}
