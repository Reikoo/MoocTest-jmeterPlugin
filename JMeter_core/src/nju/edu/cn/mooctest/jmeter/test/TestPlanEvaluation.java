package nju.edu.cn.mooctest.jmeter.test;

import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jorphan.collections.HashTree;
import org.json.JSONObject;

public class TestPlanEvaluation {
	private HashTree testPlanTree;
	private JSONObject testEvaluation;
	private double error;

	private PropertyExtractor propertyExtractor;

	public TestPlanEvaluation(HashTree testPlanTree, JSONObject testEvaluation) {
		this.setTestPlanTree(testPlanTree);
		this.setTestEvaluation(testEvaluation);
	}

	public double getError() {
		return error;
	}
	
	public void setError(double error) {
		this.error = error;
	}
	
	public HashTree getTestPlanTree() {
		return testPlanTree;
	}
	
	public void setTestPlanTree(HashTree testPlanTree) {
		this.testPlanTree = testPlanTree;
	}
	
	public JSONObject getTestEvaluation() {
		return testEvaluation;
	}
	
	public void setTestEvaluation(JSONObject testEvaluation) {
		this.testEvaluation = testEvaluation;
	}
	
	public JSONObject calcuateScore() {
		JSONObject remark = new JSONObject();
		
		propertyExtractor = new PropertyExtractorImpl();
		//获取线程组基本参数
		ThreadGroup threadGroup = propertyExtractor.extractThreadGroup(testPlanTree);
		
		int numThreads = threadGroup.getNumThreads();
		int value1 = 0;
		if (numThreads >= testEvaluation.getInt("min_num_threads") && 
				numThreads <= testEvaluation.getInt("max_num_threads")) {
			value1 = 20;
		} 
		remark.put("num_threads", value1);
		
		int rampUpTime = threadGroup.getRampUp();
		int value2 = 0;
		if (rampUpTime >= testEvaluation.getInt("min_ramp_up_time") && 
				rampUpTime <= testEvaluation.getInt("max_ramp_up_time")) {
			value2 = 10;
		}
		remark.put("ramp_up", value2);
		
		int value3 = 0;
		boolean onErrorStop = threadGroup.getOnErrorStopTest();
		String isContinue = !onErrorStop ? "continue":"stop" ;
		if (isContinue.equals(testEvaluation.getString("on_sampler_error"))) {
			value3 = 10;
		}
		remark.put("on_error", value3);
		
		int value4 = 0;
		double maxError =testEvaluation.getDouble("max_error");
		if (error <= maxError) {
			value4 = 60;
		}
		remark.put("run_success", value4);
		
		int finalScore = value1 + value2 + value3 + value4;
		remark.put("final_score", finalScore);
		
		return remark;
	}
	
}
