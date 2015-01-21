package nju.edu.cn.mooctest.jmeter.action;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JOptionPane;

import nju.edu.cn.mooctest.net.plugin.common.AuthToken;
import nju.edu.cn.mooctest.net.plugin.common.Constants;
import nju.edu.cn.mooctest.net.plugin.common.HttpConfig;
import nju.edu.cn.mooctest.net.plugin.common.UserMode;
import nju.edu.cn.mooctest.net.plugin.resources.objectsStructure.ProblemObject;
import nju.edu.cn.mooctest.net.plugin.util.encryption.EncryptionUtil;
import nju.edu.cn.mooctest.net.plugin.util.file.CompressFileUtil;
import nju.edu.cn.mooctest.net.plugin.util.http.HttpUtil;
import nju.edu.cn.mooctest.net.plugin.util.http.JsonDecoderUtil;
import nju.edu.cn.mooctest.net.plugin.util.http.NetworkUtil;
import nju.edu.cn.mooctest.net.plugin.util.http.ValidationUtil;

import org.apache.jmeter.exceptions.IllegalUserActionException;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.action.ActionNames;
import org.apache.jmeter.gui.action.Command;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 * 
 * @author grj, jia.liu
 * Time: 2014/12/16
 * Content: handle with the mooctest_download function
 * modified by I314068 2014/12/29
 */
public class MooctestDownload implements Command{	
	private final static int WORKLOAD_LOGIN = 1;
	private final static int WORKLOAD_FETCH_LIST = 5;
	private final static int WORKLOAD_DOWNLOAD = 10;
	private final static int WORKLOAD_UNZIP = 5;
	private final static int WORKLOAD_IMPORT = 3;
	private final static String BASE_URL = HttpConfig.HOST + HttpConfig.APP
			+ "download";
	
	private static final Logger log = LoggingManager.getLoggerForClass();
	
	private static final Set<String> commands = new HashSet<String>();
	
	static {
		commands.add(ActionNames.MOOCTEST_DOWNLOAD);
	}
	
	@Override
	public void doAction(ActionEvent e) throws IllegalUserActionException {	
		try {
			// estimate the user is login or not
			AuthToken auth = ValidationUtil.isLogin();
			
			// Check if in Exam Mode
			if (ValidationUtil.isLogin() != null && ValidationUtil.isLogin().getUserMode() == UserMode.ADVENTURE.ordinal()){
				// Already logged in as Adventure mode
				JOptionPane.showMessageDialog(null, "已经登录通关模式，若需登录考试模式，请重启JMeter！", "登录提示", 
						JOptionPane.YES_OPTION);
			}
			else if ((auth != null) && (auth.getToken().length() > 0) && (auth.getUserMode() == UserMode.EXAM.ordinal())) {
				String stuStr = auth.getToken();
				String jsonResponse = "";

				// TODO when called several times, the validateExam process
				// seems to be much slower
				jsonResponse = ValidationUtil.getExamProblemJson(
						HttpConfig.HOST + HttpConfig.APP + "getProblemList",
						stuStr, NetworkUtil.getMACAddress());

				// fail to connect to server
				if (jsonResponse.equals("CONNECTION_FAILED")) {
					JOptionPane.showMessageDialog(null, "网络连接失败", "下载错误", 
							JOptionPane.ERROR_MESSAGE);
				} else {
					String examName = JsonDecoderUtil.getExamName(jsonResponse);
					String examBeginTime = JsonDecoderUtil
							.getExamEndTime(jsonResponse);
					String examEndTime = JsonDecoderUtil
							.getExamEndTime(jsonResponse);
					int examTimeStatus = JsonDecoderUtil
							.getExamTimeStatus(jsonResponse);

					if (examTimeStatus == 2) {
						JOptionPane.showMessageDialog(null, "此次考试已经结束！", "考试信息", 
								JOptionPane.YES_OPTION);
					} else if (examTimeStatus == 1 && (jsonResponse != null)) {
						// The exam has started
						int choice = JOptionPane.showConfirmDialog(null, "本次考试为"+ examName +"，是否下载试题？", "考试信息",
								JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);

						if (choice == JOptionPane.YES_OPTION) {
							String result = fetchProjects(jsonResponse);
							showDownloadResult(result);
						}

					} else {
						// The exam has not started yet
						JOptionPane.showMessageDialog(null, "考试尚未开始！ 最近进行的考试是 " + examName + " 于 "
										+ examBeginTime + " 开始并将于"
										+ examEndTime + "结束，请耐心等待", "考试信息", 
										JOptionPane.YES_OPTION);
					}
				}
			} else {
				// not logged in
				JOptionPane.showMessageDialog(null, "尚未登录", "登录提示", 
						JOptionPane.YES_OPTION);
			}
		} catch (Exception err) {
			System.err.println("catch Exception " + err.getClass());
			err.printStackTrace();
			if ((err.getMessage() == null)
					|| (err.getMessage().indexOf("Connection refused") >= 0)) {
				JOptionPane.showMessageDialog(null, "网络异常", "下载错误", 
						JOptionPane.ERROR_MESSAGE);
			} else {
				//TODO
			}
		}
	}

	@Override
	public Set<String> getActionNames() {
		return commands;
	}
	
	@SuppressWarnings({ "unused" })
	private static boolean hideLoginButton() {
		GuiPackage guiPackage = GuiPackage.getInstance();
		guiPackage.getMainFrame().setMooctestLoginEnabled(false);		
		return true;
	}
	
	private void showDownloadResult(final String resultMessage) {
		JOptionPane.showMessageDialog(null, resultMessage, "下载提示", 
				JOptionPane.PLAIN_MESSAGE);
	}
	
	private String fetchProjects(final String jsonResponse) {
		String resultMessage;
		HashMap<String, ProblemObject> problems = new HashMap<String, ProblemObject>();
		
		// get download address list from server
//TODO		int numOfProblem = JsonDecoderUtil
//				.getNumOfProblems(jsonResponse);
//		int examType = JsonDecoderUtil.getExamType(jsonResponse);
//		int workload = calWorkload(numOfProblem);

		problems = JsonDecoderUtil.getProblems(jsonResponse);

		// Step 2: download the problems from server
		Set<String> problemNames = problems.keySet();
		Iterator<String> nameIte = problemNames.iterator();

		ArrayList<ProblemObject> problemObjects = new ArrayList<ProblemObject>();

		String downloadDest = null;
		try {
			String stuStr = ValidationUtil.isLogin().getToken();
			String number = EncryptionUtil.decryptDES(stuStr);
			String[] stuStrParts = number.split("_");
			
			downloadDest = Constants.DOWNLOAD_PATH
					+ stuStrParts[0] + "/" + stuStrParts[1] + "/";
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		while (nameIte.hasNext()) {
			String problemName = nameIte.next();
			// TODO download the problems from server
			ProblemObject currentProblem = problems.get(problemName);
			String problemUrl = currentProblem.getProLoc();
			try {

				System.out.println("Downloading " + BASE_URL
						+ problemUrl + " to " + downloadDest);
				// TODO the paths here needs to be rearranged like such
				// 'downloadDest + "/problems"'
				String problemPath = HttpUtil.downloadFile(BASE_URL,
						problemUrl, downloadDest + "/problems");
				if (problemPath != null) {
					problemObjects.add(currentProblem);
				}
			} catch (IOException e) {
				System.err.println("catch IOException!");
				e.printStackTrace();
				resultMessage = "网络错误或下载文件不存在";
				return resultMessage;
			}	
		}

		// Step 3: unzip downloaded projects
		for (ProblemObject problemObject : problemObjects) {
			String problemPath = downloadDest + "/problems/"
					+ getFileName(problemObject.getProLoc());
			if (problemPath.endsWith("rar")) {
				CompressFileUtil.unrar(problemPath, downloadDest);
			} else {
				try {
					CompressFileUtil.unzip(problemPath, downloadDest);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		resultMessage = "成功下载试题至C:/mooctest-jmeter/projects";
		return resultMessage;
	}
	
	private void sleepFor(Integer waitTime) {
		try {
			Thread.sleep(waitTime);
		} catch (Throwable t) {
			System.out.println("Wait time interrupted");
		}
	}
	
	private int calWorkload(int numOfProblem) {
		return WORKLOAD_LOGIN
				+ (WORKLOAD_FETCH_LIST + WORKLOAD_DOWNLOAD + WORKLOAD_UNZIP)
				* numOfProblem + WORKLOAD_IMPORT;
	}

	private String getFileName(String proLoc) {
		int pos1 = proLoc.lastIndexOf("\\");
		int pos2 = proLoc.lastIndexOf("/");
		if (pos1 > pos2) {
			return proLoc.substring(pos1 + 1, proLoc.length());
		} else {
			return proLoc.substring(pos2 + 1, proLoc.length());
		}
	}
}