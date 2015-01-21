package nju.edu.cn.mooctest.net.plugin.scriptprocessor;


import nju.edu.cn.mooctest.net.plugin.common.ActionMode;

import org.json.JSONObject;

public interface ScriptProcessor {
	public JSONObject process(String scriptURL , ActionMode mode) throws Exception;
}
