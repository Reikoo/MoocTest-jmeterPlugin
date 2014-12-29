package nju.edu.cn.mooctest.jmeter.action;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.action.ActionNames;
import org.apache.jmeter.gui.action.Command;
import org.apache.jmeter.gui.util.EscapeDialog;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 * 
 * @author grj Time: 2014/12/16 Content: handle with the mooctest_login function
 * 
 */

public class MooctestLogin implements Command {
	private static final Set<String> commandSet;

    private static JDialog login;
    
    private static final int heightper = 38;
	private static final int widthper = 100;

    static {
        HashSet<String> commands = new HashSet<String>();
        commands.add(ActionNames.MOOCTEST_LOGIN);
        commandSet = Collections.unmodifiableSet(commands);
    }

    /**
     * Handle the "about" action by displaying the "About Apache JMeter..."
     * dialog box. The Dialog Box is NOT modal, because those should be avoided
     * if at all possible.
     */
    @Override
    public void doAction(ActionEvent e) {
        if (e.getActionCommand().equals(ActionNames.MOOCTEST_LOGIN)) {
            this.login();
        }
    }

    /**
     * Provide the list of Action names that are available in this command.
     */
    @Override
    public Set<String> getActionNames() {
        return MooctestLogin.commandSet;
    }

    /**
     * Called by about button. Raises about dialog. Currently the about box has
     * the product image and the copyright notice. The dialog box is centered
     * over the MainFrame.
     */
    void login() {
        JFrame mainFrame = GuiPackage.getInstance().getMainFrame();
        if (login == null) {
            login = new EscapeDialog(mainFrame, "身份验证", false);
            login.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    login.setVisible(false);
                }
            });

            Container panel = login.getContentPane();
            panel.setLayout(null);
            panel.setSize(widthper*5, heightper*8);
            
            JLabel label1 = new JLabel("请输入您的身份验证字串");
    		label1.setBounds(panel.getWidth()*1/5, panel.getHeight()*1/8, widthper*2, heightper);
    		JLabel label2 = new JLabel("身份验证: ");
    		label2.setBounds(panel.getWidth()*1/5, panel.getHeight()*2/8, widthper*2, heightper);
    		JTextField textField = new JTextField();
    		textField.setBounds(panel.getWidth()*1/5, panel.getHeight()*3/7, widthper*3, 30);
    		JButton okButton = new JButton("OK");
    		okButton.setBounds(panel.getWidth()*4/14, panel.getHeight()*5/8, widthper*5/6, 30);
    		okButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO 调用登录方法
					//Logger log = LoggingManager.getLoggerForClass();
					//log.info("Identify Verification: "+textField.getText());
					login.setVisible(false);
				}
			});
    		JButton cancelButton = new JButton("Cancel");
    		cancelButton.setBounds(panel.getWidth()*8/15, panel.getHeight()*5/8, widthper*5/6, 30);
    		cancelButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					login.setVisible(false);
				}
			});
    		
    		panel.add(label1);
    		panel.add(label2);
    		panel.add(textField);
    		panel.add(okButton);
    		panel.add(cancelButton);
        }

        // NOTE: these lines center the about dialog in the
        // current window. Some older Swing versions have
        // a bug in getLocationOnScreen() and they may not
        // make this behave properly.
        Point p = mainFrame.getLocationOnScreen();
        Dimension d1 = mainFrame.getSize();
        Dimension d2 = login.getSize();
        login.setLocation(p.x + (d1.width - d2.width) / 2, p.y + (d1.height - d2.height) / 2);
        login.setSize(widthper*5, heightper*8);
        //login.pack();;
        login.setVisible(true);
    }
}
