package nju.edu.cn.mooctest.jmeter.action;

import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JDialog;

import nju.edu.cn.mooctest.net.plugin.common.ActionMode;

import org.apache.jmeter.gui.action.ActionNames;
import org.apache.jmeter.gui.action.Command;
import org.json.JSONObject;

public class MooctestRun implements Command {

	private static final Set<String> commandSet;

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
			this.run();
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
	 * Called by about button. Raises about dialog. Currently the about box has
	 * the product image and the copyright notice. The dialog box is centered
	 * over the MainFrame.
	 */
	void run() {
		JSONObject processDataJson = null;	// result json
		
		processDataJson = runScript(ActionMode.RUN);
	}

	/**
	 *  Run script and generate result file
	 */
	private JSONObject runScript(ActionMode mode){
		// View TestProjectProcessor.process
		// Can new a class called ProcessUtil
		JSONObject processDataJson = null;
		return processDataJson;
	}
}
