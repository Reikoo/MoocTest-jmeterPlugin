package nju.edu.cn.mooctest.jmeter.action;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.jmeter.exceptions.IllegalUserActionException;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.action.ActionNames;
import org.apache.jmeter.gui.action.Command;
import org.apache.jmeter.gui.util.EscapeDialog;

/**
 * 
 * @author grj Time: 2014/12/16 Content: handle with the mooctest_login function
 * 
 */

public class MooctestLogin implements Command {

	private static final Set<String> commands = new HashSet<String>();
	private static JDialog loginDialog;

	static { 
		commands.add(ActionNames.MOOCTEST_LOGIN); // mooctest login
	}

	/**
	 * handle the mooctest login action asking user to enter the 
	 */
	@Override
	public void doAction(ActionEvent e) throws IllegalUserActionException {
		// TODO Auto-generated method stub
		if(e.getActionCommand().equals(ActionNames.MOOCTEST_LOGIN))
			this.login();
	}

	@Override
	public Set<String> getActionNames() {
		// TODO Auto-generated method stub
		return null;
	}

	private void login() {
		JFrame mainFrame = GuiPackage.getInstance().getMainFrame();
		if(loginDialog == null){
			loginDialog = new EscapeDialog(mainFrame, "Mooctest Login", false);
			loginDialog.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
                    loginDialog.setVisible(false);
                }
			});
			
			JPanel identityVerifi = new JPanel();
		}
		
	}
}
