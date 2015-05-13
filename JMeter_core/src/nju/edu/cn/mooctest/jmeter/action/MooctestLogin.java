package nju.edu.cn.mooctest.jmeter.action;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import nju.edu.cn.mooctest.net.plugin.common.Constants;
import nju.edu.cn.mooctest.net.plugin.util.file.FileUtil;
import nju.edu.cn.mooctest.net.plugin.util.http.JsonDecoderUtil;
import nju.edu.cn.mooctest.net.plugin.common.HttpConfig;
import nju.edu.cn.mooctest.net.plugin.util.http.NetworkUtil;
import nju.edu.cn.mooctest.net.plugin.common.UserMode;
import nju.edu.cn.mooctest.net.plugin.util.http.ValidationUtil;

import org.apache.jmeter.gui.action.ActionNames;
import org.apache.jmeter.gui.action.Command;
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
		String inputID = (String) JOptionPane.showInputDialog(null,
				"请输入您的身份验证字串：\n", "身份验证", JOptionPane.PLAIN_MESSAGE, null,
				null, "");

		if (inputID != null && !inputID.trim().equals("")) {
			// Have already entered non-null string
			try {
				Logger log = LoggingManager.getLoggerForClass();
				log.info("Login as " + ValidationUtil.isLogin());
				log.info("Login ID: " + inputID);

				// Check if in Exam Mode
				if (ValidationUtil.isLogin() != null
						&& ValidationUtil.isLogin().getUserMode() == UserMode.ADVENTURE
								.ordinal()
						&& ValidationUtil.isLogin().getToken().equals(inputID)) {
					// Already logged in as Adventure mode
					JOptionPane.showMessageDialog(null,
							"已经登录通关模式，若需登录考试模式，请重启JMeter！", "警告",
							JOptionPane.WARNING_MESSAGE);
				}

				else if (ValidationUtil.isLogin() != null
						&& (ValidationUtil.isLogin().getUserMode() == UserMode.EXAM
								.ordinal())
						&& ValidationUtil.isLogin().getToken().equals(inputID)) {
					// Already login
					JOptionPane.showMessageDialog(null, "已经登录，无需再次登录！", "警告",
							JOptionPane.WARNING_MESSAGE);
				}

				else {
					// Validate the student identity string
					// if success, get the token and save it in workspace

					// get the MAC Address
					String macAddress = null;
					macAddress = NetworkUtil.getMACAddress();

					// do the login action
					String jsonResponse = "";

					jsonResponse = ValidationUtil.validateLogin(HttpConfig.HOST
							+ HttpConfig.APP + "login", inputID, macAddress);

					if (jsonResponse.equals("CONNECTION_FAILED")) {
						// fail to connect to server
						JOptionPane.showMessageDialog(null, "网络连接失败", "错误",
								JOptionPane.ERROR_MESSAGE);
					} else {
						// parse valid jsonResponse
						boolean loginFlag = JsonDecoderUtil
								.getLoginSuccess(jsonResponse);

						if (loginFlag && (jsonResponse != null)) {
							String examName = JsonDecoderUtil
									.getExamName(jsonResponse);
							String examBeginTime = JsonDecoderUtil
									.getExamBeginTime(jsonResponse);
							String examEndTime = JsonDecoderUtil
									.getExamEndTime(jsonResponse);
							int examTimeStatus = JsonDecoderUtil
									.getExamTimeStatus(jsonResponse);

							if (examTimeStatus == 1) {
								// The exam has started(status == 1)
								JOptionPane.showMessageDialog(null,
										"登录成功！ 本次考试为" + examName, "考试信息",
										JOptionPane.PLAIN_MESSAGE);
								// save the token into root folder of mooctest
								writeToken(UserMode.EXAM, inputID);

							} else if (examTimeStatus == 0) {
								// have upcoming exam(status == 0)
								JOptionPane.showMessageDialog(null,
										"考试尚未开始！ 最近将要开始的考试是 " + examName
												+ " 于 " + examBeginTime
												+ " 开始，于" + examEndTime
												+ "结束，请耐心等待", "考试信息",
										JOptionPane.WARNING_MESSAGE);

							} else {
								// don't have upcoming exam(status == 2)
								JOptionPane.showMessageDialog(null,
										"您输入的字串所对应的考试已经结束！", "考试信息",
										JOptionPane.WARNING_MESSAGE);

							}

						} else {
							JOptionPane.showMessageDialog(null,
									"身份验证字串错误，登录失败！", "错误",
									JOptionPane.ERROR_MESSAGE);
						}
					}

				}
			} catch (Exception e) {
				Logger log = LoggingManager.getLoggerForClass();
				log.error("catch Exception: " + e.getClass());
				if ((e.getMessage() == null)
						|| (e.getMessage().indexOf("Connection refused") >= 0)) {
					JOptionPane.showMessageDialog(null, "网络异常！", "错误",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		} else {
			// Enter nothing
			return;
		}
	}

	/**
	 * write token into mooctest root folder to judge whether one is login
	 * 
	 * @param stuStr
	 * @throws IOException
	 */
	private void writeToken(UserMode mode, String stuStr) throws IOException {
		File file = new File(Constants.AUTH_FILE);
		FileUtil.createFolder(Constants.MOOCTEST_ROOT_PATH);
		if (!file.exists()) {
			file.createNewFile();
		}

		BufferedWriter writer = new BufferedWriter(new FileWriter(file));

		// write User Mode (Exam - 0 / Adventure - 1)
		writer.append(new Integer(mode.ordinal()).toString());
		writer.append("\n");
		writer.append(stuStr);

		writer.flush();
		writer.close();
	}
}
