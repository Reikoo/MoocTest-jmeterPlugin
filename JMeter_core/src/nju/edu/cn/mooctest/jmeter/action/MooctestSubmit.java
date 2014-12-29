package nju.edu.cn.mooctest.jmeter.action;

import java.awt.BorderLayout;
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
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.action.ActionNames;
import org.apache.jmeter.gui.action.Command;
import org.apache.jmeter.gui.util.EscapeDialog;

/**
 * 
 * @author grj Time: 2014/12/16 Content: handle with the mooctest_login function
 * 
 */

public class MooctestSubmit implements Command {
	private static final Set<String> commandSet;

    private static JDialog submit;
    
    private static final int height = 200;
	private static final int width = 400;

    static {
        HashSet<String> commands = new HashSet<String>();
        commands.add(ActionNames.MOOCTEST_SUBMIT);
        commandSet = Collections.unmodifiableSet(commands);
    }

    /**
     * Handle the "about" action by displaying the "About Apache JMeter..."
     * dialog box. The Dialog Box is NOT modal, because those should be avoided
     * if at all possible.
     */
    @Override
    public void doAction(ActionEvent e) {
        if (e.getActionCommand().equals(ActionNames.MOOCTEST_SUBMIT)) {
            this.submit();
        }
    }

    /**
     * Provide the list of Action names that are available in this command.
     */
    @Override
    public Set<String> getActionNames() {
        return MooctestSubmit.commandSet;
    }

    /**
     * Called by about button. Raises about dialog. Currently the about box has
     * the product image and the copyright notice. The dialog box is centered
     * over the MainFrame.
     */
    void submit() {
        JFrame mainFrame = GuiPackage.getInstance().getMainFrame();
        if (submit == null) {
            submit = new EscapeDialog(mainFrame, "提交结果", false);
            submit.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    submit.setVisible(false);
                }
            });

            Container panel = submit.getContentPane();
            panel.setLayout(null);
            panel.setSize(width, height);
            
            JPanel infoPanel = new JPanel();
            //infoPanel.setBackground(Color.white);
            infoPanel.setLayout(new BorderLayout());
            infoPanel.setBounds(width/18, 0, width*16/18, height/4);
            JLabel label = new JLabel("是否提交考试结果？");
            label.setHorizontalAlignment(SwingConstants.LEFT); 
            infoPanel.add(label, BorderLayout.CENTER);
            
    		JPanel buttonPanel = new JPanel();
    		buttonPanel.setBounds(0, panel.getHeight()/2, panel.getWidth(), panel.getHeight()/3);
    		buttonPanel.setLayout(null);
    		JButton okButton = new JButton("OK");
    		okButton.setBounds(width*3/7, height/11, width/5, height/6);
    		okButton.addActionListener(new ActionListener() {   			
    			@Override
    			public void actionPerformed(ActionEvent e) {
    				System.out.println("OK");
    				
    				// TODO 在这儿进行上传，如果成功则弹出提示框
    			}
    		});
    		JButton cancelButton = new JButton("Cancel");
    		cancelButton.setBounds(width*9/13, height/11, width/5, height/6);
    		cancelButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					submit.setVisible(false);
				}
			});
    		buttonPanel.add(okButton);
    		buttonPanel.add(cancelButton);
    		
    		panel.add(infoPanel, BorderLayout.NORTH);
    		panel.add(buttonPanel, BorderLayout.SOUTH);
        }

        // NOTE: these lines center the about dialog in the
        // current window. Some older Swing versions have
        // a bug in getLocationOnScreen() and they may not
        // make this behave properly.
        Point p = mainFrame.getLocationOnScreen();
        Dimension d1 = mainFrame.getSize();
        Dimension d2 = submit.getSize();
        submit.setLocation(p.x + (d1.width - d2.width) / 2, p.y + (d1.height - d2.height) / 2);
        submit.setSize(width, height);
        //login.pack();;
        submit.setVisible(true);
    }
}
