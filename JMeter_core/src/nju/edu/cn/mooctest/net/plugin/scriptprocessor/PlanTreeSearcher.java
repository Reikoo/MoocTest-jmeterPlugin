package nju.edu.cn.mooctest.net.plugin.scriptprocessor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.jmeter.config.CSVDataSet;
import org.apache.jmeter.control.Controller;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.timers.SyncTimer;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.SearchByClass;

public class PlanTreeSearcher {
	
	public static Map<String, HTTPSamplerProxy> searchHTTPSamplers(HashTree testPlanTree) {
		SearchByClass searcher = new SearchByClass(HTTPSamplerProxy.class);
        testPlanTree.traverse(searcher);
        Map<String, HTTPSamplerProxy> httpSamplers = new HashMap<String, HTTPSamplerProxy>();
        Iterator iter = searcher.getSearchResults().iterator();
        while (iter.hasNext()) {
        	HTTPSamplerProxy sampler = (HTTPSamplerProxy) iter.next();
        	HashTree subHashTree = searcher.getSubTree(sampler);
        	String str = subHashTree.keySet().toString();
        	httpSamplers.put(str.substring(1, str.length()-1), sampler);
        }
		return httpSamplers;
	}
	
	public static Map<String, CSVDataSet> searchCSVDataSet(HashTree testPlanTree) {
		SearchByClass searcher = new SearchByClass(CSVDataSet.class);
        testPlanTree.traverse(searcher);
        Map<String,CSVDataSet> csvDataSets = new HashMap<String, CSVDataSet>();
        Iterator iter = searcher.getSearchResults().iterator();
        while (iter.hasNext()) {
        	CSVDataSet config = (CSVDataSet) iter.next();
        	HashTree subHashTree = searcher.getSubTree(config);
        	String str = subHashTree.keySet().toString();
        	csvDataSets.put(str.substring(1, str.length()-1), config);
        }
		return csvDataSets;
	}
	
	public static ThreadGroup searchThreadGroup(HashTree testPlanTree) {
		SearchByClass<ThreadGroup> threadGroup = new SearchByClass<ThreadGroup>(ThreadGroup.class);
        testPlanTree.traverse(threadGroup);
        Object[] group = threadGroup.getSearchResults().toArray();
        if (group.length == 0) {
        	return null; //Could not find the ThreadGroup class!
        }
        ThreadGroup tg = (ThreadGroup) group[0];
		return tg;
	}
	
	public static SyncTimer searchSyncTimer(HashTree testPlanTree) {
		SearchByClass<SyncTimer> syncTimer = new SearchByClass<SyncTimer>(SyncTimer.class);
        testPlanTree.traverse(syncTimer);
        Object[] timers = syncTimer.getSearchResults().toArray();
        if (timers.length == 0) {
        	return null; //Could not find the ThreadGroup class!
        }
        SyncTimer timer = (SyncTimer) timers[0];
		return timer;
	}
}
