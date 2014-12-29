package nju.edu.cn.mooctest.jmeter.action;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import org.apache.jmeter.exceptions.IllegalUserActionException;
import org.apache.jmeter.gui.action.ActionNames;
import org.apache.jmeter.gui.action.Command;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 * 
 * @author grj
 * Time: 2014/12/16
 * Content: handle with the mooctest_download function
 * modified by I314068 2014/12/29
 */
public class MooctestDownload implements Command{	
	private static final Logger log = LoggingManager.getLoggerForClass();
	
	private static final Set<String> commands = new HashSet<String>();
	
	static {
		commands.add(ActionNames.MOOCTEST_DOWNLOAD);
	}
	
	@Override
	public void doAction(ActionEvent e) throws IllegalUserActionException {	
		int choice = JOptionPane.showConfirmDialog(null, "本次考试为压力测试，是否下载试题", "考试信息",
				JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
		
		if (choice == JOptionPane.YES_OPTION) {
			//TODO download
			JOptionPane.showConfirmDialog(null, "成功下载试题", "下载提示",
					JOptionPane.YES_OPTION);
		} else {
			 return;
		}  
	}

	@Override
	public Set<String> getActionNames() {
		return commands;
	}
}