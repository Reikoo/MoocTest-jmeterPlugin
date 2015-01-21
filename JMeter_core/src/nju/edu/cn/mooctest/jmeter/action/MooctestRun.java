package nju.edu.cn.mooctest.jmeter.action;

import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import nju.edu.cn.mooctest.net.plugin.common.ActionMode;
import nju.edu.cn.mooctest.net.plugin.scriptprocessor.ScriptProcessor;
import nju.edu.cn.mooctest.net.plugin.scriptprocessor.impl.ScriptProcessorImpl;

import org.apache.jmeter.JMeter;
import org.apache.jmeter.engine.JMeterEngineException;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.engine.TreeCloner;
import org.apache.jmeter.engine.TreeClonerNoTimer;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.action.ActionNames;
import org.apache.jmeter.gui.action.ActionRouter;
import org.apache.jmeter.gui.action.Command;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.timers.Timer;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.collections.ListedHashTree;
import org.json.JSONObject;
import org.apache.jmeter.gui.action.AbstractAction;

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
        GuiPackage gui = GuiPackage.getInstance();
        HashTree testTree = gui.getTreeModel().getTestPlan();
        JMeter.convertSubTree(testTree);
        testTree.add(testTree.getArray()[0], gui.getMainFrame());
        System.out.println("test plan before cloning is running version: "
                + ((TestPlan) testTree.getArray()[0]).isRunningVersion());
        TreeCloner cloner = cloneTree(testTree, ignoreTimer);
        engine = new StandardJMeterEngine();
        engine.configure(cloner.getClonedTree());
        try {
            engine.runTest();
            
            // test 
            
            System.out.println("table content: " );
            
        } catch (JMeterEngineException e) {
            JOptionPane.showMessageDialog(gui.getMainFrame(), e.getMessage(), 
                    JMeterUtils.getResString("error_occurred"), JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
        }
        System.out.println("test plan after cloning and running test is running version: "
                + ((TestPlan) testTree.getArray()[0]).isRunningVersion());
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
	private JSONObject runScript(ActionMode mode) throws Exception{
		// View TestProjectProcessor.process
		// Can new a class called ProcessUtil
		ScriptProcessor processor = new ScriptProcessorImpl();
		String scriptURL = "";
		JSONObject processDataJson = processor.process(scriptURL,
				ActionMode.RUN);

		return processDataJson;
	}
}
