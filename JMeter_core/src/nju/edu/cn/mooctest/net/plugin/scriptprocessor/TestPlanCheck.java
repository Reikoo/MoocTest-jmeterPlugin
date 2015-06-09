package nju.edu.cn.mooctest.net.plugin.scriptprocessor;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.jmeter.assertions.Assertion;
import org.apache.jmeter.config.ConfigElement;
import org.apache.jmeter.control.Controller;
import org.apache.jmeter.processor.PostProcessor;
import org.apache.jmeter.processor.PreProcessor;
import org.apache.jmeter.reporters.AbstractListenerElement;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.testelement.WorkBench;
import org.apache.jmeter.threads.AbstractThreadGroup;
import org.apache.jmeter.timers.Timer;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.HashTreeTraverser;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

public class TestPlanCheck {
	private static final Logger log = LoggingManager.getLoggerForClass();

	private class TreeDumper implements HashTreeTraverser {
        private int indent = 0;

        @Override
        public void addNode(Object o, HashTree hashTree) {
            if (o instanceof TestElement) {
                TestElement el = (TestElement) o;
                log.info(StringUtils.repeat(" ", indent) + "[" + el.getClass().getSimpleName() + "] " + el.getName());
            } else {
                log.info(StringUtils.repeat(" ", indent) + o);
            }
            indent++;
        }

        @Override
        public void subtractNode() {
            indent--;
        }

        @Override
        public void processPath() {
        }
    }

	public void dumpTree(HashTree testTree) {
        System.out.println("Dumping tree structure:");
        testTree.traverse(new TreeDumper());

    }

    public Map<String, Integer> getStats(HashTree testTree) {
        System.out.println("Element stats goes below:");
        StatsCollector stats = new StatsCollector();
        testTree.traverse(stats);
        return stats.getStats();
    }
	
    private class StatsCollector implements HashTreeTraverser {
        private int tGroups = 0;
        private int controllers = 0;
        private int samplers = 0;
        private int listeners = 0;
        private int others = 0;
        private int preProc = 0;
        private int postProc = 0;
        private int assertions = 0;
        private int timers = 0;
        private int configs = 0;

        @Override
        public void addNode(Object node, HashTree subTree) {
            if (node instanceof AbstractThreadGroup) {
                tGroups++;
            } else if (node instanceof Controller) {
                controllers++;
            } else if (node instanceof Sampler) {
                samplers++;
            } else if (node instanceof AbstractListenerElement) {
                listeners++;
            } else if (node instanceof PreProcessor) {
                preProc++;
            } else if (node instanceof PostProcessor) {
                postProc++;
            } else if (node instanceof Assertion) {
                assertions++;
            } else if (node instanceof Timer) {
                timers++;
            } else if (node instanceof ConfigElement) {
                configs++;
            } else if (node instanceof TestPlan) {
                log.debug("Ok, we got the root of test plan");
            } else if (node instanceof WorkBench) {
                log.debug("Ok, we got the root of test plan");
            } else {
                log.warn("Strange object in tree: " + node);
                others++;
            }
        }
        
        public Map<String, Integer> getStats() {
        	Map<String, Integer> stats = new HashMap<String, Integer>();
        	stats.put("Thread Groups", tGroups);
        	stats.put("Controllers", controllers);
        	stats.put("Config Items", configs);
        	stats.put("Samplers", samplers);
        	stats.put("Listeners", listeners);
        	stats.put("Timers", timers);
        	stats.put("Assertions", assertions);
        	stats.put("Pre-processors", preProc);
        	stats.put("Post-processors", postProc);
        	return stats;
        }

		@Override
		public void processPath() {
			
		}

		@Override
		public void subtractNode() {
			
		}
    }
}
