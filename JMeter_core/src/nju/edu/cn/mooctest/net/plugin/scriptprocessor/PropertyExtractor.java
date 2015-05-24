package nju.edu.cn.mooctest.net.plugin.scriptprocessor;

import java.util.List;
import java.util.Map;

import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jorphan.collections.HashTree;

public interface PropertyExtractor {
	ThreadGroup extractThreadGroup(HashTree testPlanTree);
	
	List<HTTPSamplerProxy> extractSamplers(HashTree testPlanTree);
	
	Map<String, String> extractHttpDefaults(HashTree testPlanTree);
	
}
