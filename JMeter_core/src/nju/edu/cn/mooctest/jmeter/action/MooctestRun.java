package nju.edu.cn.mooctest.jmeter.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import nju.edu.cn.mooctest.net.plugin.common.ActionMode;
import nju.edu.cn.mooctest.net.plugin.util.http.EvaluationUtil;

import org.apache.jmeter.gui.action.AbstractAction;
import org.apache.jmeter.gui.action.ActionNames;
import org.apache.jmeter.gui.action.LoadRecentProject;
import org.json.JSONObject;

public class MooctestRun extends AbstractAction {

	private static final Set<String> commandSet;

	static {
		HashSet<String> commands = new HashSet<String>();
		commands.add(ActionNames.MOOCTEST_RUN);
		commandSet = Collections.unmodifiableSet(commands);
	}

	@Override
	public void doAction(ActionEvent e) {
		if (e.getActionCommand().equals(ActionNames.MOOCTEST_RUN)) {
			popupShouldSave(e);
			runTest();
		}
	}

	/**
	 * Provide the list of Action names that are available in this command.
	 */
	@Override
	public Set<String> getActionNames() {
		return MooctestRun.commandSet;
	}

    private void runTest() {
    	String path = LoadRecentProject.getRecentFile(0);
		JSONObject testScore = EvaluationUtil.runScript(new File(path), ActionMode.RUN);
		//TODO 界面展示分数
    }
}
