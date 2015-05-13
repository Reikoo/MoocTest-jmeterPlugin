package nju.edu.cn.mooctest.jmeter.action;

import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import nju.edu.cn.mooctest.jmeter.test.TestPlanEvaluation;
import nju.edu.cn.mooctest.net.plugin.common.ActionMode;
import nju.edu.cn.mooctest.net.plugin.scriptprocessor.ScriptProcessor;
import nju.edu.cn.mooctest.net.plugin.scriptprocessor.impl.ScriptProcessorImpl;

import org.apache.jmeter.engine.JMeterEngineException;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.engine.TreeCloner;
import org.apache.jmeter.engine.TreeClonerNoTimer;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.action.AbstractAction;
import org.apache.jmeter.gui.action.ActionNames;
import org.apache.jmeter.gui.action.LoadRecentProject;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.timers.Timer;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.json.JSONObject;

public class MooctestRun extends AbstractAction {

	private static final Set<String> commandSet;
	private StandardJMeterEngine engine;

	static {
		HashSet<String> commands = new HashSet<String>();
		commands.add(ActionNames.MOOCTEST_RUN);
		commandSet = Collections.unmodifiableSet(commands);
	}

	/**
	 * Handle the "about" action by displaying the "About Apache JMeter..."
	 * dialog box. The Dialog Box is NOT modal, because those should be avoided
	 * if at all possible.
	 */
	@Override
	public void doAction(ActionEvent e) {
		if (e.getActionCommand().equals(ActionNames.MOOCTEST_RUN)) {
			popupShouldSave(e);
            startEngine(false);
		}
	}

	/**
	 * Provide the list of Action names that are available in this command.
	 */
	@Override
	public Set<String> getActionNames() {
		return MooctestRun.commandSet;
	}

	/**
     * Start JMeter engine
     * @param noTimer ignore timers 
     */
    private void startEngine(boolean ignoreTimer) {
    	 //JMeter Engine
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
        HashTree testPlanTree = null;
		try {
			String path = LoadRecentProject.getRecentFile(0);
			in = new FileInputStream(path);
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
    	
//        GuiPackage gui = GuiPackage.getInstance();
//        HashTree testTree = gui.getTreeModel().getTestPlan();
//        JMeter.convertSubTree(testTree);
//        testTree.add(testTree.getArray()[0], gui.getMainFrame());
//        System.out.println("test plan before cloning is running version: "
//                + ((TestPlan) testTree.getArray()[0]).isRunningVersion());
//        TreeCloner cloner = cloneTree(testTree, ignoreTimer);
//        engine = new StandardJMeterEngine();
//        engine.configure(cloner.getClonedTree());
        Summariser summer = null;
        try {
        	//add Summarizer output to get test progress in stdout like:
            // summary =      2 in   1.3s =    1.5/s Avg:   631 Min:   290 Max:   973 Err:     0 (0.00%)

            String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
            if (summariserName.length() > 0) {
                summer = new Summariser(summariserName);
            }


            // Store execution results into a .jtl file
            String logFile = "C:/Users/Administrator/Desktop/example.jtl";
            ResultCollector logger = new ResultCollector(summer);
            logger.setFilename(logFile);
            testPlanTree.add(testPlanTree.getArray()[0], logger);
            engine.configure(testPlanTree);
            engine.runTest();
            
        } catch (JMeterEngineException e) {
            JOptionPane.showMessageDialog(GuiPackage.getInstance().getMainFrame(), e.getMessage(), 
                    JMeterUtils.getResString("error_occurred"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
        }
        System.out.println("test plan after cloning and running test is running version: "
                + ((TestPlan) testPlanTree.getArray()[0]).isRunningVersion());
        while (engine.isActive()) {
        	
        }
        double errorPercentage = summer.getErrorPercent();
        System.out.println(errorPercentage);
        
        //TODO
        JSONObject testEvaluation = requestTestEvaluation("", 1);
        TestPlanEvaluation testPlanEvaluation = new TestPlanEvaluation(testPlanTree, testEvaluation);
        testPlanEvaluation.setError(errorPercentage);
        try {
			JSONObject testScore = runScript(ActionMode.RUN, testPlanEvaluation);
			System.out.println(testScore.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
    
    /**
     * Create a Cloner that ignores {@link Timer} if removeTimers is true
     * @param testTree {@link HashTree}
     * @param removeTimers boolean remove timers 
     * @return {@link TreeCloner}
     */
    private TreeCloner cloneTree(HashTree testTree, boolean removeTimers) {
        TreeCloner cloner = null;
        if(removeTimers) {
            cloner = new TreeClonerNoTimer(false);
        } else {
            cloner = new TreeCloner(false);     
        }
        testTree.traverse(cloner);
        return cloner;
    }
    

	/**
	 *  Run script and generate result file
	 */
	private JSONObject runScript(ActionMode mode, TestPlanEvaluation testPlanEvaluation) throws Exception{
		// View TestProjectProcessor.process
		// Can new a class called ProcessUtil
		ScriptProcessor processor = new ScriptProcessorImpl(testPlanEvaluation);
		String scriptURL = "";
		JSONObject processDataJson = processor.process(scriptURL,
				ActionMode.RUN);

		return processDataJson;
	}
	
	//TODO add request api
	private JSONObject requestTestEvaluation(String url, long proId) {
		JSONObject testEvaluation = new JSONObject();
		testEvaluation.put("min_num_threads", 5);
		testEvaluation.put("max_num_threads", 50);
		testEvaluation.put("domain", "baidu.com");
		testEvaluation.put("min_ramp_up_time", 0);
		testEvaluation.put("max_ramp_up_time", 5);
		testEvaluation.put("on_sampler_error", "continue");
		testEvaluation.put("max_error", 0.5);
		return testEvaluation;
	}
	
}
