package nju.edu.cn.mooctest.net.plugin.util.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import nju.edu.cn.mooctest.net.plugin.common.ActionMode;
import nju.edu.cn.mooctest.net.plugin.common.Constants;
import nju.edu.cn.mooctest.net.plugin.common.HttpConfig;
import nju.edu.cn.mooctest.net.plugin.scriptprocessor.PlanTreeSearcher;
import nju.edu.cn.mooctest.net.plugin.scriptprocessor.ScriptFileUtil;
import nju.edu.cn.mooctest.net.plugin.scriptprocessor.TestPlanCheck;
import nju.edu.cn.mooctest.net.plugin.scriptprocessor.XPathUtil;
import nju.edu.cn.mooctest.net.plugin.scriptprocessor.XPathUtil.CSVFile;
import nju.edu.cn.mooctest.net.plugin.util.encryption.EncryptionUtil;
import nju.edu.cn.mooctest.net.plugin.util.file.FileUtil;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.timers.SyncTimer;
import org.apache.jorphan.collections.HashTree;
import org.json.JSONObject;


public class EvaluationUtil {
	private static JSONObject jsonEvaluation;
	private static HashTree testPlanTree;
	private static Map<String, Integer> stats;
	private static String scriptFilePath;
//	private static StandardJMeterEngine engine;
	
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

		try {
			String stuStr = ValidationUtil.isLogin().getToken();
			String number = EncryptionUtil.decryptDES(stuStr);
			String[] stuStrParts = number.split("_");
			
			String folder = Constants.DOWNLOAD_PATH
					+ stuStrParts[0] + "/" + stuStrParts[1] + "/";
			File proInfoFile = new File(folder + "pro.mt");
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
		scoreJson.put("score", remark.get("score").toString());
		scoreJson.put("num_threads", remark.get("num_threads").toString());
		scoreJson.put("ramp_up", remark.get("ramp_up").toString());
		scoreJson.put("loops", remark.get("loops").toString());
		scoreJson.put("sync_timer", remark.get("sync_timer").toString());
		scoreJson.put("parameter", remark.get("parameter").toString());
		scoreJson.put("max_error", remark.get("max_error").toString());

		processDataJson.put("score", scoreJson);

		return processDataJson;
	}
	
	private static JSONObject runScript(File scriptFile) {
		JSONObject jsonScore = new JSONObject();
		scriptFilePath = scriptFile.getAbsolutePath();
//		ConfigExtractor configExtractor = new ConfigExtractor(scriptFile);
//		boolean flag = false;

        // Load existing .jmx Test Plan
        try {
			testPlanTree = ScriptFileUtil.loadJMX(scriptFile);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
        
        TestPlanCheck checker = new TestPlanCheck();
        stats = checker.getStats(testPlanTree);
        System.out.println(stats);
//        Summariser summer = null;
//    	//add Summarizer output to get test progress in stdout like:
//        // summary =      2 in   1.3s =    1.5/s Avg:   631 Min:   290 Max:   973 Err:     0 (0.00%)
//        try {
//	        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
//	        if (summariserName.length() > 0) {
//	            summer = new Summariser(summariserName);
//	        }
//	        flag = configExtractor.isSyncTimerRight(configExtractor.readJMXFile(), JMeterContextService.getTotalThreads());
//	
//	//      // Store execution results into a .jtl file
//	//        String logFile = Constants.DOWNLOAD_PATH + "results.jtl";
//	        ResultCollector logger = new ResultCollector(summer);
//	//        logger.setFilename(logFile);
//	        testPlanTree.add(testPlanTree.getArray()[0], logger);
//	        engine.configure(testPlanTree);
//	        if (flag) {
//	        	engine.runTest();
//	        }
//		} catch (JMeterEngineException e) {
//            JOptionPane.showMessageDialog(GuiPackage.getInstance().getMainFrame(), e.getMessage(), 
//                    JMeterUtils.getResString("error_occurred"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
//        }
//        ThreadCounts tc = JMeterContextService.getThreadCounts();
//        while (engine.isActive()) {
//        	
//        }
//        System.out.println("test plan after cloning and running test is running version: "
//                + ((TestPlan) testPlanTree.getArray()[0]).isRunningVersion());
//        double errorPercentage = summer.getErrorPercent();
//        System.out.println(errorPercentage);
//        try {
//        	JSONObject maxErrorJson= getJsonEvaluation().getJSONObject("max_error");
//    		double maxError =maxErrorJson.getDouble("max");
//    		if (errorPercentage > maxError || tc.finishedThreads == 0) {
//    			flag = false;
//    		}
			jsonScore = calculateScore();
			System.out.println(jsonScore.toString());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
        return jsonScore;
	}
	
	private static JSONObject calculateScore() {
		JSONObject remark = new JSONObject();
		jsonEvaluation = getJsonEvaluation();
		ThreadGroup threadGroup = PlanTreeSearcher.searchThreadGroup(testPlanTree);
		if (threadGroup == null || stats.get("Thread Groups") == 0
				|| stats.get("Samplers") == 0) {
			remark.put("num_threads", 0);
			remark.put("ramp_up", 0);
			remark.put("loops", 0);
			remark.put("max_error", 0);
			remark.put("parameter", 0);
			remark.put("sync_timer", 0);
			remark.put("score", 0);
			return remark;
		}
		Map<String, HTTPSamplerProxy> httpSamplers = PlanTreeSearcher.searchHTTPSamplers(testPlanTree);
		
		double value1 = getThreadScore(threadGroup);
		remark.put("num_threads", value1);
		
		double value2 = getRampUpScore(threadGroup);
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
		
		XPathUtil xPathUtil = null;
        Map<String, CSVFile> paraFiles = null;
		try {
			System.out.println(scriptFilePath);
			xPathUtil = new XPathUtil(new File(scriptFilePath));
			paraFiles = xPathUtil.getCSVDataSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		int loops = xPathUtil.getLoopsInt();
		double value3 = getLoopScore(loops);
		remark.put("loops", value3);
		
		Map<String, Double> scores = getParameterHTTPScore(paraFiles, httpSamplers);
		double value4 = scores.get("parameter");
		double value5 = scores.get("http");
		remark.put("max_error", value5);
		remark.put("parameter", value4);
		
		SyncTimer syncTimer = PlanTreeSearcher.searchSyncTimer(testPlanTree);
		double value6 = 0;
		if (syncTimer != null) {
			int groupSize = xPathUtil.getSyncTimerGroup();
			value6 = getSyncTimerScore(groupSize, threadGroup.getNumThreads(), httpSamplers);
		}
		remark.put("sync_timer", value6);
		
		double finalScore = value1 + value2 + value3 + value4 + value5 + value6;
		remark.put("score", finalScore);
		
		return remark;
	}
	
	/**
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
	*/
	
	private static double sendHTTPRequests(Map<String, HTTPSamplerProxy> samplers, 
			Map<String, CSVFile> dataFiles) {
		Iterator it=samplers.keySet().iterator();
		double success = 0;
		int totalNum = 0;
		int successNum = 0;
		while(it.hasNext()){    
		     String key;    
		     HTTPSamplerProxy sampler;    
		     key=it.next().toString();    
		     sampler = samplers.get(key);
		     if (dataFiles == null || dataFiles.isEmpty()) {
		    	String urlStr = null;
				try {
					urlStr = sampler.getUrl().toString();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				urlStr = preProcess(urlStr);
				URL url = null;
				try {
					url = new URL(urlStr);
					HttpClient client = new HttpClient();
					HttpMethod method = null;
					if (sampler.getMethod().toLowerCase().equals("get")) {
						method = new GetMethod(url.toString());
						method.addRequestHeader("Content-Type", "text/html; charset=UTF-8");
					} else {
						PostMethod post = new PostMethod(url.toString());
						//TODO
					}
					client.executeMethod(method);
					int responseCode = method.getStatusCode();
					if (HttpStatus.SC_OK == responseCode) {// 连接成功
						successNum ++;
					}
				}catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (HttpException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
		     }
		     
		     try {
				Iterator iter = dataFiles.keySet().iterator();
				while (iter.hasNext()) {
					String name = iter.next().toString();
					CSVFile file = dataFiles.get(name);
					List<Map<String, String>> vars = file.getVariables();
					for (int i=0; i<vars.size(); i++) {
						totalNum ++;
						String urlStr = sampler.getUrl().toString();
						urlStr = preProcess(urlStr);
						Map<String, String> varPairs = vars.get(i);
						Iterator iter2 = varPairs.keySet().iterator();
						while (iter2.hasNext()) {
							String varName = iter2.next().toString();
							String varValue = varPairs.get(varName);
							System.out.print("参数："+ varValue);
							urlStr = urlStr.replaceAll("\\$\\{"+varName+"\\}", varValue);
						}
						System.out.println(urlStr);
						URL url = null;
						try {
							url = new URL(urlStr);
							HttpClient client = new HttpClient();
							HttpMethod method = null;
							if (sampler.getMethod().toLowerCase().equals("get")) {
								method = new GetMethod(url.toString());
								method.addRequestHeader("Content-Type", "text/html; charset=UTF-8");
							} else {
								PostMethod post = new PostMethod(url.toString());
								//TODO
							}
							client.executeMethod(method);
							int responseCode = method.getStatusCode();
							if (HttpStatus.SC_OK == responseCode) {// 连接成功
								successNum ++;
							}
						}catch (IllegalArgumentException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		success = 1.0 * successNum / totalNum;
		return success;
	}
	
	private static String preProcess(String urlStr) {
		Pattern p = Pattern.compile("\\$\\{__CSVRead(.+?)\\}");
		Matcher m = p.matcher(urlStr);
	    while (m.find()) {// 遍历找到的所有大括号
	       String param = m.group(1);//.replaceAll("\\{\\}", "");// 去掉括号
	       String s = param.substring(1, param.length()-1);
	       String[] str = s.split(",");
	       BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(str[0]));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	       String line = null;
		try {
			line = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	       String[] values = line.split(",");
	       int index = Integer.valueOf(str[1]);
	       urlStr = urlStr.replace("${__CSVRead"+param+"}", values[index]);
	    }
	    return urlStr;
	}
	
	private static double getThreadScore(ThreadGroup tg) {
		int numThreads = tg.getNumThreads();
		double value = 0;
		JSONObject threadNumJson = jsonEvaluation.getJSONObject("num_threads");
		if (numThreads >= threadNumJson.getInt("min") && 
				numThreads <= threadNumJson.getInt("max")) {
			value = 100*threadNumJson.getDouble("rate");
		} 
		return value;
	}
	
	private static double getRampUpScore(ThreadGroup tg) {
		double rampUpTime = tg.getRampUp();
		double value = 0;
		JSONObject rampUpJson = jsonEvaluation.getJSONObject("ramp_up");
		if (rampUpTime >= rampUpJson.getDouble("min") && 
				rampUpTime <= rampUpJson.getDouble("max")) {
			value = 100*rampUpJson.getDouble("rate");
		}
		return value;
	}
	
	private static double getLoopScore(int loops) {
		double value = 0;
		JSONObject loopJson = jsonEvaluation.getJSONObject("loops");
		if (loops >= loopJson.getInt("min") && 
				loops <= loopJson.getInt("max")) {
			value = 100*loopJson.getDouble("rate");
		}
		return value;
	}
	
	private static Map<String, Double> getParameterHTTPScore(Map<String, CSVFile> dataFiles, 
			Map<String, HTTPSamplerProxy> samplers) {
		List<String> urlList = new ArrayList<String>();
		Iterator it=samplers.keySet().iterator();
		double success = 0;
		int totalNum = 0;
		int successNum = 0;
		while(it.hasNext()){    
		     String key;    
		     HTTPSamplerProxy sampler;    
		     key=it.next().toString();    
		     sampler = samplers.get(key);
		     if (dataFiles == null || dataFiles.isEmpty()) {
		    	String urlStr = null;
				try {
					urlStr = sampler.getUrl().toString();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				urlStr = preProcess(urlStr);
				URL url = null;
				try {
					urlList.add(urlStr);
					url = new URL(urlStr);
					HttpClient client = new HttpClient();
					HttpMethod method = null;
					if (sampler.getMethod().toLowerCase().equals("get")) {
						method = new GetMethod(url.toString());
						method.addRequestHeader("Content-Type", "text/html; charset=UTF-8");
					} else {
						PostMethod post = new PostMethod(url.toString());
						method = new PostMethod(url.toString());
						method.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=gbk"); 
						String queryString =sampler.getQueryString();
						String[] paras = queryString.split("&");
						for (String str : paras) {
							String[] pair = str.split("=");
							method.getParams().setParameter(pair[0], URLEncoder.encode(pair[1]));
						}
					}
					client.executeMethod(method);
					int responseCode = method.getStatusCode();
					if (HttpStatus.SC_OK == responseCode) {// 连接成功
						successNum ++;
					}
				}catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (HttpException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
		     }
		     else {
			     try {
					Iterator iter = dataFiles.keySet().iterator();
					while (iter.hasNext()) {
						String name = iter.next().toString();
						CSVFile file = dataFiles.get(name);
						List<Map<String, String>> vars = file.getVariables();
						for (int i=0; i<vars.size(); i++) {
							totalNum ++;
							String urlStr = sampler.getUrl().toString();
							urlStr = preProcess(urlStr);
							Map<String, String> varPairs = vars.get(i);
							Iterator iter2 = varPairs.keySet().iterator();
							while (iter2.hasNext()) {
								String varName = iter2.next().toString();
								String varValue = varPairs.get(varName);
								urlStr = urlStr.replaceAll("\\$\\{"+varName+"\\}", varValue);
							}
							System.out.println(urlStr);
							
							URL url = null;
							try {
								url = new URL(urlStr);
								HttpClient client = new HttpClient();
								HttpMethod method = null;
								if (sampler.getMethod().toLowerCase().equals("get")) {
									method = new GetMethod(url.toString());
									method.addRequestHeader("Content-Type", "text/html; charset=UTF-8");
									urlList.add(urlStr);
								} else {
									method = new PostMethod(url.toString());
									method.setRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=gbk"); 
									String queryString =sampler.getQueryString();
									Iterator iter3 = varPairs.keySet().iterator();
									while (iter3.hasNext()) {
										String varName = iter3.next().toString();
										String varValue = varPairs.get(varName);
										queryString = queryString.replaceAll("\\$\\{"+varName+"\\}", varValue);
									}
									String[] paras = queryString.split("&");
									for (String str : paras) {
										String[] pair = str.split("=");
										method.getParams().setParameter(pair[0], URLEncoder.encode(pair[1]));
									}
									urlList.add(queryString);
								}
								client.executeMethod(method);
								int responseCode = method.getStatusCode();
								if (HttpStatus.SC_OK == responseCode) {// 连接成功
//									System.out.println(method.getResponseBodyAsString());
									successNum ++;
								}
							}catch (IllegalArgumentException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
		     }
		}
		success = 1.0 * successNum / totalNum;
		Map<String, Double> scores = new HashMap<String, Double>();
		double value1 = 0;
		double value2 = 0;
		
		JSONObject parameterJson = jsonEvaluation.getJSONObject("parameter");
		JSONObject maxErrorJson= jsonEvaluation.getJSONObject("max_error");
		String key_words = parameterJson.getString("key_words");
		String[] keyWords = key_words.split(",");
		if (stats.get("Config Items") == 0 ||dataFiles == null || dataFiles.isEmpty()) {
			value1 = 0;
		} else {
			if (keyWords == null || keyWords.length == 0)
				value1 =  100*parameterJson.getDouble("rate");
			else {
				int num = 0;
				for (String word : keyWords) {
					for (String url : urlList) {
						System.out.println(url + "\t" + word);
						if (url.toLowerCase().contains(word.trim().toLowerCase())) {
							num++;
							break;
						}
					}
				}
				value1 = 100*parameterJson.getDouble("rate")*num/keyWords.length;
			}
		}
		
		double minSuccess = 1 - maxErrorJson.getDouble("max");
		if (success >= minSuccess) {
			value2 = success * maxErrorJson.getDouble("rate") * 100;
		}
		scores.put("parameter", value1);
		scores.put("http", value2);
		return scores;
	}
	
	private static double getSyncTimerScore(int groupSize, int threadGroup, 
			Map<String, HTTPSamplerProxy> samplers) {
		double value = 0;
		JSONObject syncJson = jsonEvaluation.getJSONObject("sync_timer");
		if (stats.get("Timers") == 0) {
			return value;
		}
		if (groupSize > 0 && threadGroup%groupSize == 0) {
			value = 0.5 * 100*syncJson.getDouble("rate");
		}
		double fullMark = 0.5 * 100*syncJson.getDouble("rate");
		String keyword = syncJson.getString("path");
		String[] words = keyword.split(",");
		double urlScore = 0;
		if (words == null || keyword.equals("")) {
			urlScore = fullMark;
		} else {
		Iterator it=samplers.keySet().iterator();    
			while(it.hasNext()){    
			     String key=it.next().toString();    
			     for (String word : words) {
			    	 if (!word.equals("") && key.toLowerCase().
			    			 contains(word.trim().toLowerCase())) {
			    		 urlScore = fullMark;
			    		 break;
			    	 }
			     }
			}
		}
		value += urlScore;
		return value;
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
		FileUtil.createFolder(downloadDest);
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
	
}
