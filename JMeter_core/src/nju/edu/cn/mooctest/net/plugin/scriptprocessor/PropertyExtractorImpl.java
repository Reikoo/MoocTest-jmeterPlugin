package nju.edu.cn.mooctest.net.plugin.scriptprocessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.SearchByClass;

public class PropertyExtractorImpl implements PropertyExtractor{

	@Override
	public ThreadGroup extractThreadGroup(HashTree testPlanTree) {
		SearchByClass<ThreadGroup> threadGroup = new SearchByClass<ThreadGroup>(ThreadGroup.class);
        testPlanTree.traverse(threadGroup);
        Object[] group = threadGroup.getSearchResults().toArray();
        if (group.length == 0) {
        	return null;
//        	throw new RuntimeException("Could not find the ThreadGroup class!");
        }
        ThreadGroup tg = (ThreadGroup) group[0];
		return tg;
	}

	@Override
	public List<HTTPSamplerProxy> extractSamplers(HashTree testPlanTree) {
		SearchByClass searcher = new SearchByClass(HTTPSamplerProxy.class);
        testPlanTree.traverse(searcher);
        List<HTTPSamplerProxy> httpSamplers = new ArrayList<HTTPSamplerProxy>();
        Iterator iter = searcher.getSearchResults().iterator();
        while (iter.hasNext()) {
        	HTTPSamplerProxy sampler = (HTTPSamplerProxy) iter.next();
        	httpSamplers.add(sampler);
        }
		return httpSamplers;
	}

	@Override
	public Map<String, String> extractHttpDefaults(HashTree testPlanTree) {
		SearchByClass<ConfigTestElement> configTestElement = new SearchByClass<ConfigTestElement>(ConfigTestElement.class);
		testPlanTree.traverse(configTestElement);
		Object[] configs = configTestElement.getSearchResults().toArray();
		if (configs.length == 0) {
			return null;
		}
		Map<String, String> httpDefaults = new HashMap<String, String>();
		for (int i=0; i<configs.length; i++) {
			ConfigTestElement cte = (ConfigTestElement) configs[i];
			if (cte.getPropertyAsString("HTTPSampler.domain")!=null) {
				httpDefaults.put("domain", cte.getPropertyAsString("HTTPSampler.domain"));
				httpDefaults.put("protocol", cte.getPropertyAsString("HTTPSampler.protocol"));
				httpDefaults.put("path", cte.getPropertyAsString("HTTPSampler.path"));
				httpDefaults.put("implementation", cte.getPropertyAsString("HTTPSampler.implementation"));
				break;
			}
		}
		return httpDefaults;
	}

}
