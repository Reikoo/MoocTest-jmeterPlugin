package nju.edu.cn.mooctest.net.plugin.scriptprocessor.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.json.JSONObject;

import nju.edu.cn.mooctest.net.plugin.common.ActionMode;
import nju.edu.cn.mooctest.net.plugin.common.Constants;
import nju.edu.cn.mooctest.net.plugin.scriptprocessor.ScriptProcessor;

public class ScriptProcessorImpl implements ScriptProcessor {

	@Override
	public JSONObject process(String scriptURL, ActionMode mode)
			throws Exception {
		// TODO Auto-generated method stub
		JSONObject processDataJson = new JSONObject();
		JSONObject scoreJson = new JSONObject();

		// read problem info from c:/mooctest-jmeter/projects/pro.mt
		File proInfoFile = new File(Constants.DOWNLOAD_PATH + "pro.mt");
		BufferedReader bw = new BufferedReader(new FileReader(proInfoFile));
		String proIdStr = bw.readLine();
		String proNameStr = bw.readLine();
		String subIdStr = bw.readLine();
		String evalStandardIdStr = bw.readLine();
		
		scoreJson.put("pro_id", proIdStr);
		scoreJson.put("pro_name", proNameStr);
		scoreJson.put("sub_id", subIdStr);
		scoreJson.put("eval_standard_id", evalStandardIdStr);

		float finalScore = new Float(0.0);
		// caculate score
		finalScore = CaculateScore();
		// get the score by a given strategy of calculation
		scoreJson.put("score", new Float(finalScore).toString());

		processDataJson.put("score", scoreJson);

		return processDataJson;
	}

	private float CaculateScore() {
		// TODO Define a set of strategies to calculate the score
		// NOTE: If this method is simple, delete the method and move the method
		// body to process()
		float finalScore = new Float(0.0);
		finalScore = 60;
		return finalScore;
	}

}
