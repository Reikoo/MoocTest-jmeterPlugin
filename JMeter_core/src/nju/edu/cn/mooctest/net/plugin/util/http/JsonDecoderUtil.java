package nju.edu.cn.mooctest.net.plugin.util.http;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import nju.edu.cn.mooctest.net.plugin.resources.objectsStructure.ProblemObject;
import nju.edu.cn.mooctest.net.plugin.resources.objectsStructure.TableObject;

public class JsonDecoderUtil {
	private static String pre(String jsonStr){
		String validStr = jsonStr;
		if ((jsonStr.indexOf("{}/n") > 0)){
			validStr = jsonStr.replace("{}/n", "");
		}
		
		if (validStr.endsWith("/n")){
			validStr = validStr.substring(0, validStr.length() - 2); 
		}
		// TODO remove System.out
		//System.out.println("valid json is: " + validStr);
		
		return validStr;
	}
	// TODO, change the type of functions into XXX(String jsonStr , String fieldName), returns different types of data
	@Deprecated
	public static String getToken(String jsonStr){
		String tokenStr = null;
		
			
		JSONObject responseJson = new JSONObject(jsonStr);

		//System.out.println("Field \"token\"");
		String token = responseJson.get("token").toString();
		//System.out.println(responseJson.get("token"));
		
		tokenStr = token;

		return tokenStr;
	}
	
	public static boolean getLoginSuccess(String jsonStr){
		jsonStr = pre(jsonStr);
		boolean success = false;
		
		JSONObject responseJson = new JSONObject(jsonStr);
		
		int login_success_json = (Integer) responseJson.get("login_success");
		if (login_success_json == 1){
			success = true;
		}
		else{
			success = false;
		}

		return success;
	}
	
	public static boolean getFlag(String jsonStr , String fieldName){
		jsonStr = pre(jsonStr);
		boolean success = false;
		
		JSONObject responseJson = new JSONObject(jsonStr);
			
		Integer login_success_json = (Integer) responseJson.get(fieldName);
		if (login_success_json == 1){
			success = true;
		}
		else{
			success = false;
		}
		
		return success;
	}
	
	public static int getExamTimeStatus(String jsonStr){
		jsonStr = pre(jsonStr);
		int examTimeStatus = -1;
		JSONObject responseJson = new JSONObject(jsonStr);
			
		Integer longExamTimeStatus = (Integer) responseJson.get("exam_time_status");
		examTimeStatus = longExamTimeStatus.intValue();

		return examTimeStatus;
	}
	
	public static String getExamName(String jsonStr){
		jsonStr = pre(jsonStr);
		String examName = null;
		JSONObject responseJson = new JSONObject(jsonStr);
			
		String exam_name_json = (String) responseJson.get("exam_name");
		
		examName = exam_name_json;

		return examName;
	}
	
	public static String getExamBeginTime(String jsonStr){
		jsonStr = pre(jsonStr);
		String examTime = null;
		
		JSONObject responseJson = new JSONObject(jsonStr);
			
		String exam_time_json = (String) responseJson.get("exam_begin_time");
		
		examTime = exam_time_json;

		return examTime;
		
	}
	
	public static String getExamEndTime(String jsonStr){
		jsonStr = pre(jsonStr);
		String examTime = null;
		
		JSONObject responseJson = new JSONObject(jsonStr);
			
		String exam_time_json = (String) responseJson.get("exam_end_time");
		
		examTime = exam_time_json;

		return examTime;
		
	}
	
	
	public static HashMap<String , ProblemObject> getProblems(String jsonStr){
		jsonStr = pre(jsonStr);
		HashMap<String , ProblemObject> problemMap = new HashMap<String , ProblemObject>();
		
		JSONObject responseJson = new JSONObject(jsonStr);
			
		JSONArray problems = (JSONArray) responseJson.get("problems");
		//System.out.println("Field \"problems\"");
		//System.out.println(problems);
		
		for (int idxQue = 0 ; idxQue < problems.length() ; idxQue++){
			JSONObject problem = (JSONObject) problems.get(idxQue);
			Integer proId = (Integer) problem.get("pro_id");
			String proName = problem.get("pro_name").toString();
			String proLoc = problem.get("pro_loc").toString();
			Integer subId = (Integer) problem.get("sub_id");
			
			Long proIdLong = proId.longValue();
			Long subIdLong = subId.longValue();
			
			ProblemObject proObj = new ProblemObject(proIdLong , proName , proLoc , subIdLong);
			problemMap.put(proName, proObj);
		}
			
		return problemMap;
	}
	
	public static int getNumOfProblems(String jsonStr){
		jsonStr = pre(jsonStr);
		int num = 0;
		
		JSONObject responseJson = new JSONObject(jsonStr);
			
		JSONArray problems = (JSONArray) responseJson.get("problems");
		//System.out.println("Field \"problems\"");
		//System.out.println(problems);
		
		num = problems.length();
			
		return num;
	}
	
	public static JSONObject getTestEvaluation(String jsonStr) {
		jsonStr = pre(jsonStr);
		
		JSONObject responseJson = new JSONObject(jsonStr);
		return responseJson.getJSONObject("detail");
	}
	
	public static ArrayList<TableObject> getResults(String jsonStr){
		jsonStr = pre(jsonStr);
		ArrayList<TableObject> objects = new ArrayList<TableObject>();
		
		JSONObject responseJson = new JSONObject(jsonStr);
			
		JSONArray results = (JSONArray) responseJson.get("results");
		//System.out.println("Field \"problems\"");
		//System.out.println(problems);
		for (int idxQue = 0 ; idxQue < results.length() ; idxQue++){
			JSONObject result = (JSONObject) results.get(idxQue);
			String proName = result.get("project_name").toString();
			String statement = result.get("statement").toString();
			String branch = result.get("branch").toString();
			String loop = result.get("loop").toString();
			@SuppressWarnings("unused")
			String strictCondition = result.get("strict_condition").toString();
			String finalResult = result.get("final_result").toString();
			//System.out.println("----------");
			//System.out.println("Name : " + name + "       url : " + url);
			TableObject object = new TableObject(proName , statement , branch , loop , finalResult);
			objects.add(object);
		}
		
		return objects;
	}

	public static String getStr(String jsonStr, String fieldName) {
		jsonStr = pre(jsonStr);
		String result = null;
		JSONObject responseJson = new JSONObject(jsonStr);
			
		String exam_name_json = (String) responseJson.get(fieldName);
		
		result = exam_name_json;

		return result;
	}
	public static int getExamType(String jsonStr) {
		jsonStr = pre(jsonStr);
		
		JSONObject responseJson = new JSONObject(jsonStr);
			
		int examType = (Integer) responseJson.get("exam_type");
		//System.out.println("Field \"problems\"");
		//System.out.println(problems);
		
			
		return examType;
	}
	public static long getStuId(String jsonStr) {
		jsonStr = pre(jsonStr);
		
		JSONObject responseJson = new JSONObject(jsonStr);
			
		long stuId = ((Integer) responseJson.get("stu_id")).longValue();
		
		return stuId;
	}
	public static String getStuName(String jsonStr) {
		jsonStr = pre(jsonStr);
		
		JSONObject responseJson = new JSONObject(jsonStr);
			
		String stuName = (String) responseJson.get("stu_name");
		
		return stuName;
	}
	public static HashMap<String, HashMap<String, String>> advGetSubjectList(
			String jsonStr) {
		HashMap<String , HashMap<String , String>> subjects = new HashMap<String , HashMap<String , String>>();
		
		JSONObject responseJson = new JSONObject(jsonStr);
		
		JSONArray subjectArray = responseJson.getJSONArray("subjects");
		
		for(int subjectIdx = 0 ; subjectIdx < subjectArray.length() ; subjectIdx++){
			JSONObject subjectObj = (JSONObject) subjectArray.get(subjectIdx);
			
			HashMap<String , String> subjectMap = new HashMap<String , String>();
			JSONArray stageArray = subjectObj.getJSONArray("stages");
			for (int stageIdx = 0 ; stageIdx < stageArray.length() ; stageIdx++){
				JSONObject stageObj = (JSONObject) stageArray.get(stageIdx);
				subjectMap.put(stageObj.getInt("stage_num") + ". " + stageObj.getString("stage_name"), stageObj.getString("stage_str"));
			}
			
			subjects.put(subjectObj.getString("sub_name"), subjectMap);
		}
		
		
		return subjects;
	}
	public static String getProName(String jsonStr) {
		jsonStr = pre(jsonStr);
		
		JSONObject responseJson = new JSONObject(jsonStr);
			
		String proName = (String) responseJson.get("pro_name");
		
		return proName;
	}
	public static Long getProId(String jsonStr) {
		jsonStr = pre(jsonStr);
		
		JSONObject responseJson = new JSONObject(jsonStr);
			
		long proId = ((Integer) responseJson.get("pro_id")).longValue();
		
		return proId;
	}
	public static Long getSubId(String jsonStr) {
		jsonStr = pre(jsonStr);
		
		JSONObject responseJson = new JSONObject(jsonStr);
			
		long subId = ((Integer) responseJson.get("sub_id")).longValue();
		
		return subId;
	}
	public static String getProLoc(String jsonStr) {
		jsonStr = pre(jsonStr);
		
		JSONObject responseJson = new JSONObject(jsonStr);
			
		String proLoc = (String) responseJson.get("pro_loc");
		
		return proLoc;
	}
	public static String getSubName(String jsonStr) {
		jsonStr = pre(jsonStr);
		
		JSONObject responseJson = new JSONObject(jsonStr);
			
		String subName = (String) responseJson.get("sub_name");
		
		return subName;
	}
	public static int getStageNum(String jsonStr) {
		jsonStr = pre(jsonStr);
		
		JSONObject responseJson = new JSONObject(jsonStr);
			
		int stageNum = (Integer) responseJson.get("stage_num");
		
		return stageNum;
	}
	public static String getEvalStandardId(String jsonStr){
		jsonStr = pre(jsonStr);
		
		JSONObject responseJson = new JSONObject(jsonStr);
		
		String evalStandardId = (String) responseJson.get("eval_standard_id");
		
		return evalStandardId;
	}

}
