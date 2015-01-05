package nju.edu.cn.mooctest.jmeter.action;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import nju.edu.cn.mooctest.net.plugin.common.ActionMode;
import nju.edu.cn.mooctest.net.plugin.common.Constants;
import nju.edu.cn.mooctest.net.plugin.common.ExamType;
import nju.edu.cn.mooctest.net.plugin.resources.objectsStructure.ProblemObject;
import nju.edu.cn.mooctest.net.plugin.util.http.HttpUtil;
import nju.edu.cn.mooctest.net.plugin.test.TestUtil;
import nju.edu.cn.mooctest.net.plugin.common.HttpConfig;
import nju.edu.cn.mooctest.net.plugin.util.http.JsonDecoderUtil;
import nju.edu.cn.mooctest.net.plugin.util.http.NetworkUtil;
import nju.edu.cn.mooctest.net.plugin.common.UserMode;
import nju.edu.cn.mooctest.net.plugin.common.AuthToken;
import nju.edu.cn.mooctest.net.plugin.util.http.ValidationUtil;

import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.action.ActionNames;
import org.apache.jmeter.gui.action.Command;
import org.apache.jmeter.gui.util.EscapeDialog;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

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
		/*
		 * JFrame mainFrame = GuiPackage.getInstance().getMainFrame(); if
		 * (submit == null) { submit = new EscapeDialog(mainFrame, "提交结果",
		 * false); submit.addMouseListener(new MouseAdapter() {
		 * 
		 * @Override public void mouseClicked(MouseEvent e) {
		 * submit.setVisible(false); } });
		 * 
		 * Container panel = submit.getContentPane(); panel.setLayout(null);
		 * panel.setSize(width, height);
		 * 
		 * JPanel infoPanel = new JPanel();
		 * //infoPanel.setBackground(Color.white); infoPanel.setLayout(new
		 * BorderLayout()); infoPanel.setBounds(width/18, 0, width*16/18,
		 * height/4); JLabel label = new JLabel("是否提交考试结果？");
		 * label.setHorizontalAlignment(SwingConstants.LEFT);
		 * infoPanel.add(label, BorderLayout.CENTER);
		 * 
		 * JPanel buttonPanel = new JPanel(); buttonPanel.setBounds(0,
		 * panel.getHeight()/2, panel.getWidth(), panel.getHeight()/3);
		 * buttonPanel.setLayout(null); JButton okButton = new JButton("OK");
		 * okButton.setBounds(width*3/7, height/11, width/5, height/6);
		 * okButton.addActionListener(new ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) {
		 * System.out.println("OK");
		 * 
		 * // TODO 在这儿进行上传，如果成功则弹出提示框 } }); JButton cancelButton = new
		 * JButton("Cancel"); cancelButton.setBounds(width*9/13, height/11,
		 * width/5, height/6); cancelButton.addActionListener(new
		 * ActionListener() {
		 * 
		 * @Override public void actionPerformed(ActionEvent e) {
		 * submit.setVisible(false); } }); buttonPanel.add(okButton);
		 * buttonPanel.add(cancelButton);
		 * 
		 * panel.add(infoPanel, BorderLayout.NORTH); panel.add(buttonPanel,
		 * BorderLayout.SOUTH); }
		 * 
		 * // NOTE: these lines center the about dialog in the // current
		 * window. Some older Swing versions have // a bug in
		 * getLocationOnScreen() and they may not // make this behave properly.
		 * Point p = mainFrame.getLocationOnScreen(); Dimension d1 =
		 * mainFrame.getSize(); Dimension d2 = submit.getSize();
		 * submit.setLocation(p.x + (d1.width - d2.width) / 2, p.y + (d1.height
		 * - d2.height) / 2); submit.setSize(width, height); //login.pack();;
		 * submit.setVisible(true);
		 */
		Logger log = LoggingManager.getLoggerForClass();
		log.info("Submit");
		int choice = JOptionPane.showConfirmDialog(null, "是否提交考试结果？", "提交结果",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

		if (choice == JOptionPane.YES_OPTION) {
			// estimate the user is login or not
			AuthToken auth = null;
			try {
				auth = ValidationUtil.isLogin();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Check if in Exam Mode
			if (auth != null
					&& auth.getUserMode() == UserMode.ADVENTURE.ordinal()) {
				// Already logged in as Adventure mode
				JOptionPane.showMessageDialog(null,
						"已经登录通关模式，若需登录考试模式，请重启JMeter！", "警告",
						JOptionPane.WARNING_MESSAGE);
			} else if ((auth != null) && (auth.getToken().length() > 0)
					&& (auth.getUserMode() == UserMode.EXAM.ordinal())) {
				String stuStr = auth.getToken();
				String jsonStr = "";
				try {
					jsonStr = ValidationUtil.getExamProblemJson(HttpConfig.HOST
							+ HttpConfig.APP + "getProblemList", stuStr,
							NetworkUtil.getMACAddress());
					log.info("Submit ---ProblemList: " + jsonStr);
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				int examTimeStatus = JsonDecoderUtil.getExamTimeStatus(jsonStr);

				if (examTimeStatus == 2) {
					JOptionPane.showMessageDialog(null, "此次考试已经结束！", "考试信息",
							JOptionPane.WARNING_MESSAGE);
				} else {
					int examType = JsonDecoderUtil.getExamType(jsonStr);
					log.info("Submit ---examType: " + examType);
					log.info("Submit ---stuStr: " + stuStr);
					submitScript(jsonStr, stuStr, examType);
					JOptionPane.showMessageDialog(null, "成功提交考试结果", "提交结果",
							JOptionPane.PLAIN_MESSAGE);
				}
			} else {
				// not logging in
				JOptionPane.showMessageDialog(null, "尚未登录", "警告",
						JOptionPane.WARNING_MESSAGE);
			}

		} else {
			return;
		}
	}

	/**
	 * Get script's address; run result; submit script and result
	 */
	private void submitScript(final String jsonStr, final String stuStr,
			final int examType) {
		Logger log = LoggingManager.getLoggerForClass();
		JSONObject processDataJson = null;	// result json
		
		HashMap<String, ProblemObject> problemMap = JsonDecoderUtil
				.getProblems(jsonStr);
		Set<String> ques = problemMap.keySet();
		
		// upload the file and get the address
		JFileChooser fileChooser = new JFileChooser();
		int option = fileChooser.showDialog(null, "选择文件");
		if(option == JFileChooser.APPROVE_OPTION){
			try {
				// get script's address
				String scriptURL = fileChooser.getSelectedFile().toString();
				String scriptName = fileChooser.getSelectedFile().getName();
				log.info("SubmitScript ---file address: " + scriptURL);
				
				// run script
				processDataJson = runScript(scriptURL, ActionMode.SUBMIT);
				
				// submit script and result
				String url = HttpConfig.HOST + HttpConfig.APP
						+ "submit";
				String uploadedJsonString = null;
				try {
					uploadedJsonString = HttpUtil
							.submitAnswerWithScore(
									url,
									stuStr,
									scriptURL + "\\",
									scriptName,
									processDataJson.getJSONObject(
											"score").toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
	
	
	/**
	 *  Run script and generate result file
	 */
	private JSONObject runScript(String scriptURL, ActionMode mode){
		// View TestProjectProcessor.process
		// Can new a class called ProcessUtil
		JSONObject processDataJson = null;
		return processDataJson;
	}
}
