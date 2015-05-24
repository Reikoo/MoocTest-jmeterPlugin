package nju.edu.cn.mooctest.net.plugin.util.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import nju.edu.cn.mooctest.net.plugin.common.ActionMode;
import nju.edu.cn.mooctest.net.plugin.common.Constants;
import nju.edu.cn.mooctest.net.plugin.common.HttpConfig;
import nju.edu.cn.mooctest.net.plugin.scriptprocessor.ConfigExtractor;
import nju.edu.cn.mooctest.net.plugin.scriptprocessor.ConfigExtractor.CSVFile;
import nju.edu.cn.mooctest.net.plugin.scriptprocessor.PropertyExtractor;
import nju.edu.cn.mooctest.net.plugin.scriptprocessor.PropertyExtractorImpl;
import nju.edu.cn.mooctest.net.plugin.util.encryption.EncryptionUtil;

import org.apache.commons.io.FilenameUtils;
import org.apache.jmeter.engine.JMeterEngineException;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.exceptions.IllegalUserActionException;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.WorkBench;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.JMeterContextService.ThreadCounts;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.jorphan.util.JOrphanUtils;
import org.apache.log.Logger;
import org.json.JSONObject;
import org.w3c.dom.Document;


public class EvaluationUtil {
	private static JSONObject jsonEvaluation;
	private static HashTree testPlanTree;
	private static StandardJMeterEngine engine;
	
	public static JSONObject getJsonEvaluation() {
		if (jsonEvaluation == null) {
			long proId = 0;
			try {
				proId = getProId();
			} catch (Exception e) {
				e.printStackTrace();
			}
			jsonEvaluation = requestTestEvaluation(proId);
		}
		return jsonEvaluation;
	}
	
	public static JSONObject runScript(File scriptFile, ActionMode mode) {
		JSONObject processDataJson = new JSONObject();
		JSONObject scoreJson = new JSONObject();

		// read problem info from c:/mooctest-jmeter/projects/pro.mt
		
		try {
			File proInfoFile = new File(Constants.DOWNLOAD_PATH + "pro.mt");
			BufferedReader bw = new BufferedReader(new FileReader(proInfoFile));
			String proIdStr = bw.readLine();
			String proNameStr = bw.readLine();
			String subIdStr = bw.readLine();
			
			scoreJson.put("pro_id", proIdStr);
			scoreJson.put("pro_name", proNameStr);
			scoreJson.put("sub_id", subIdStr);

		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONObject remark = runScript(scriptFile);
		
		// get the score by a given strategy of calculation
		scoreJson.put("score", remark.get("score"));
		scoreJson.put("num_threads", remark.get("num_threads"));
		scoreJson.put("ramp_up", remark.get("ramp_up"));
		scoreJson.put("loops", remark.get("loops"));
		scoreJson.put("sync_timer", remark.get("sync_timer"));
		scoreJson.put("parameter", remark.get("parameter"));
		scoreJson.put("max_error", remark.get("max_error"));

		processDataJson.put("score", scoreJson);

		return processDataJson;
	}
	
	private static JSONObject runScript(File scriptFile) {
		 //JMeter Engine
		JSONObject jsonScore = new JSONObject();
		ConfigExtractor configExtractor = new ConfigExtractor(scriptFile);
		boolean flag = false;
        engine = new StandardJMeterEngine();

        //JMeter initialization (properties, log levels, locale, etc)
        JMeterUtils.loadJMeterProperties(JMeterUtils.getJMeterBinDir() + "/jmeter.properties");
        JMeterUtils.initLogging();// you can comment this line out to see extra log messages of i.e. DEBUG level
        JMeterUtils.initLocale();

        // Initialize JMeter SaveService
        try {
			SaveService.loadProperties();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        // Load existing .jmx Test Plan
        FileInputStream in = null;
		try {			
			in = new FileInputStream(scriptFile);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
        try {
			testPlanTree = SaveService.loadTree(in);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
        try {
			in.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        
        Summariser summer = null;
    	//add Summarizer output to get test progress in stdout like:
        // summary =      2 in   1.3s =    1.5/s Avg:   631 Min:   290 Max:   973 Err:     0 (0.00%)
        try {
	        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
	        if (summariserName.length() > 0) {
	            summer = new Summariser(summariserName);
	        }
	        flag = configExtractor.isSyncTimerRight(configExtractor.readJMXFile(), JMeterContextService.getTotalThreads());
	
	//      // Store execution results into a .jtl file
	//        String logFile = Constants.DOWNLOAD_PATH + "results.jtl";
	        ResultCollector logger = new ResultCollector(summer);
	//        logger.setFilename(logFile);
	        testPlanTree.add(testPlanTree.getArray()[0], logger);
	        engine.configure(testPlanTree);
	        if (flag) {
	        	engine.runTest();
	        }
		} catch (JMeterEngineException e) {
            JOptionPane.showMessageDialog(GuiPackage.getInstance().getMainFrame(), e.getMessage(), 
                    JMeterUtils.getResString("error_occurred"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
        }
        ThreadCounts tc = JMeterContextService.getThreadCounts();
        while (engine.isActive()) {
        	
        }
//        System.out.println("test plan after cloning and running test is running version: "
//                + ((TestPlan) testPlanTree.getArray()[0]).isRunningVersion());
        double errorPercentage = summer.getErrorPercent();
        System.out.println(errorPercentage);
        try {
        	JSONObject maxErrorJson= getJsonEvaluation().getJSONObject("max_error");
    		double maxError =maxErrorJson.getDouble("max");
    		if (errorPercentage > maxError || tc.finishedThreads == 0) {
    			flag = false;
    		}
			jsonScore = calculateScore(flag, configExtractor);
			System.out.println(jsonScore.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
        return jsonScore;
	}
	
	private static JSONObject calculateScore(boolean flag, 
			ConfigExtractor configExtractor) {
		JSONObject remark = new JSONObject();
		Document document = configExtractor.readJMXFile();
		PropertyExtractor propertyExtractor = new PropertyExtractorImpl();
		jsonEvaluation = getJsonEvaluation();
		ThreadGroup threadGroup = propertyExtractor.extractThreadGroup(testPlanTree);
		if (threadGroup == null) {
			remark.put("num_threads", 0);
			remark.put("ramp_up", 0);
			remark.put("loops", 0);
			remark.put("max_error", 0);
			remark.put("parameter", 0);
			remark.put("sync_timer", 0);
			remark.put("score", 0);
			return remark;
		}
		int numThreads = threadGroup.getNumThreads();
		double value1 = 0;
		JSONObject threadNumJson = jsonEvaluation.getJSONObject("num_threads");
		if (numThreads >= threadNumJson.getInt("min") && 
				numThreads <= threadNumJson.getInt("max")) {
			value1 = 100*threadNumJson.getDouble("rate");
		} 
		remark.put("num_threads", value1);
		
		double rampUpTime = threadGroup.getRampUp();
		double value2 = 0;
		JSONObject rampUpJson = jsonEvaluation.getJSONObject("ramp_up");
		if (rampUpTime >= rampUpJson.getDouble("min") && 
				rampUpTime <= rampUpJson.getDouble("max")) {
			value2 = 100*rampUpJson.getDouble("rate");
		}
		remark.put("ramp_up", value2);
		
		/**
		double value3 = 0;
		boolean onErrorStop = threadGroup.getOnErrorStopTest();
		JSONObject onErrorJson = jsonEvaluation.getJSONObject("on_error");
		String isContinue = !onErrorStop ? "continue":"stop" ;
		if (isContinue.equals(onErrorJson.getString("is_continue"))) {
			value3 = 100*onErrorJson.getDouble("rate");
		}
		remark.put("on_error", value3);
		*/
		double value3 = 0;
		int loops = configExtractor.getLoopsInt(document);
		JSONObject loopJson = jsonEvaluation.getJSONObject("loops");
		if (loops >= loopJson.getInt("min") && 
				loops <= loopJson.getInt("max")) {
			value3 = 100*loopJson.getDouble("rate");
		}
		remark.put("loops", value3);
		
		double value4 = 0;
		JSONObject maxErrorJson= jsonEvaluation.getJSONObject("max_error");
		if (flag) {
			value4 = 100*maxErrorJson.getDouble("rate");
		}
		remark.put("max_error", value4);
		
		double value5 = 0;
		JSONObject parameterJson = jsonEvaluation.getJSONObject("parameter");	
		List<String> keywords = new ArrayList<String>();
		String[] words = parameterJson.getString("key_words").split(",");
		for (int i=0; i<words.length; i++) {
			keywords.add(words[i]);
		}
		if (isParametered(configExtractor, document, keywords)) {
			value5 = 100*parameterJson.getDouble("rate");
		}
		remark.put("parameter", value5);
		
		double value6 = 0;
		JSONObject syncJson = jsonEvaluation.getJSONObject("sync_timer");
		String domain = syncJson.getString("path");
		Map<Integer, List<String>> syncSet = configExtractor.getSyncTimesSibling(document);
		Iterator<Integer> iterator = syncSet.keySet().iterator();
		while(iterator.hasNext()) {
			int key = iterator.next();
			if (numThreads % key != 0) {
				break;
			}
			List<String> path = syncSet.get(key);
			for (int i=0; i<path.size(); i++) {
				if (path.get(i).contains(domain)) {
					value6 = 100*syncJson.getDouble("rate");
					break;
				}
			}
		}
		remark.put("sync_timer", value6);
		
		double finalScore = value1 + value2 + value3 + value4 + value5 + value6;
		remark.put("score", finalScore);
		
		return remark;
	}
	
	private static boolean isParametered(ConfigExtractor configExtractor, 
			Document document, List<String> keywords) {
		List<CSVFile> csvFiles = configExtractor.parseCSVDataSet(document);
		for (CSVFile file : csvFiles) {
			String fileEncoding = file.getFileEncoding();
			if (!fileEncoding.equalsIgnoreCase("utf-8")) {
				break;
			} else {
				File file1 = new File(file.getFileName());
				if (!file1.exists()) {
					break;
				}
				BufferedReader br = null;
				try {
					br = new BufferedReader(new FileReader(file1));
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				String line = null;
				try {
					while ((line = br.readLine())!=null) {
						String[] vars = line.split(",");
						if (vars.length != file.getVariables().length) {
							return false;
						}
						List<String> var = new ArrayList<String>();
						for (int i=0; i<vars.length; i++) {
							var.add(vars[i]);
						}
						int index = -1;
						for (int i=0; i<keywords.size(); i++) {
							if (var.contains(keywords.get(i))) {
								index = i;
								break;
							}
						}
						if (index >= 0)
							keywords.remove(index);
					}
					if (keywords.size() == 0) {
						return true;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	/**
	 * 和服务器端通信，获取测试评分标准
	 * @param proId
	 * @return
	 */
	private static JSONObject requestTestEvaluation(long proId) {
		String jsonResponse = ValidationUtil.getExamEvaluationJson(
				HttpConfig.HOST + HttpConfig.APP + "getJmeterProCritern",
				String.valueOf(proId));
		System.out.println(jsonResponse);
		JSONObject testEvaluation = null;
		// fail to connect to server
		if (jsonResponse.equals("CONNECTION_FAILED")) {
			JOptionPane.showMessageDialog(null, "网络连接失败", "连接错误",
					JOptionPane.ERROR_MESSAGE);
		} else {
//			testEvaluation = JsonDecoderUtil.getTestEvaluation(jsonResponse);
			testEvaluation = new JSONObject(jsonResponse);
		}
//		String line = null;
//		try {
//			BufferedReader br = new BufferedReader(new FileReader(
//					new File("c:/Users/Administrator/Desktop/test.txt")));
//			line = br.readLine();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		testEvaluation = new JSONObject(line);
		return testEvaluation;
	}

	/**
	 * 	获取当前考试proId
	 * @return
	 * @throws Exception 
	 */
	private static long getProId() throws Exception {
		String stuStr = ValidationUtil.isLogin().getToken();
		String number = EncryptionUtil.decryptDES(stuStr);
		String[] stuStrParts = number.split("_");
		
		String downloadDest = Constants.DOWNLOAD_PATH
				+ stuStrParts[0] + "/" + stuStrParts[1] + "/";
		File proInfoFile = new File(downloadDest + "pro.mt");
		try {
			BufferedReader br = new BufferedReader(new FileReader(proInfoFile));
			String proId = br.readLine();
			return Long.valueOf(proId);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static final String JMX_FILE_EXTENSION = ".jmx";

	public static String SaveScript(String stuStr) {
		Logger log = LoggingManager.getLoggerForClass();
		try {
			// get the whole testPlan as a hashTree
			HashTree subTree = null;
			HashTree testPlan = GuiPackage.getInstance().getTreeModel()
					.getTestPlan();
			// If saveWorkBench
			JMeterTreeNode workbenchNode = (JMeterTreeNode) ((JMeterTreeNode) GuiPackage
					.getInstance().getTreeModel().getRoot()).getChildAt(1);
			if (((WorkBench) workbenchNode.getUserObject()).getSaveWorkBench()) {
				HashTree workbench = GuiPackage.getInstance().getTreeModel()
						.getWorkBench();
				testPlan.add(workbench);
			}
			subTree = testPlan;

			// get the savePath of testPlan
			String number = EncryptionUtil.decryptDES(stuStr);
			String[] stuStrParts = number.split("_");
			String updateFile = Constants.DOWNLOAD_PATH + stuStrParts[0] + "/"
					+ stuStrParts[1] + "/" + "Test"
					+ System.currentTimeMillis() + JMX_FILE_EXTENSION;

			// Make sure the file ends with proper extension
			if (FilenameUtils.getExtension(updateFile).equals("")) {
				updateFile = updateFile + JMX_FILE_EXTENSION;

				// Check if the user is trying to save to an existing file
				/*
				 * File f = new File(updateFile); if (f.exists()) { int response
				 * = JOptionPane .showConfirmDialog(
				 * GuiPackage.getInstance().getMainFrame(), JMeterUtils
				 * .getResString("save_overwrite_existing_file"), // $NON-NLS-1$
				 * JMeterUtils.getResString("save?"), // $NON-NLS-1$
				 * JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE); if
				 * (response == JOptionPane.CLOSED_OPTION || response ==
				 * JOptionPane.NO_OPTION) { return; // Do not save, user does
				 * not want to overwrite } }
				 */
			}

			try {
				convertSubTree(subTree);
			} catch (Exception err) {
				log.warn("Error converting subtree " + err);
			}

			FileOutputStream ostream = null;
			try {
				ostream = new FileOutputStream(updateFile);
				SaveService.saveTree(subTree, ostream);
			} catch (Throwable ex) {
				log.error("Error saving tree:", ex);
				if (ex instanceof Error) {
					throw (Error) ex;
				}
				if (ex instanceof RuntimeException) {
					throw (RuntimeException) ex;
				}
				throw new IllegalUserActionException(
						"Couldn't save test plan to file: " + updateFile, ex);
			} finally {
				JOrphanUtils.closeQuietly(ostream);
			}
			GuiPackage.getInstance().updateCurrentGui();

			return updateFile;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// package protected to allow access from test code
	private static void convertSubTree(HashTree tree) {
		Iterator<Object> iter = new LinkedList<Object>(tree.list()).iterator();
		while (iter.hasNext()) {
			JMeterTreeNode item = (JMeterTreeNode) iter.next();
			convertSubTree(tree.getTree(item));
			TestElement testElement = item.getTestElement(); // requires
																// JMeterTreeNode
			tree.replace(item, testElement);
		}
	}

}
