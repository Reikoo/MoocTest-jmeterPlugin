package nju.edu.cn.mooctest.net.plugin.util.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Iterator;
import java.util.LinkedList;

import nju.edu.cn.mooctest.net.plugin.common.ActionMode;
import nju.edu.cn.mooctest.net.plugin.common.Constants;
import nju.edu.cn.mooctest.net.plugin.util.encryption.EncryptionUtil;

import org.apache.commons.io.FilenameUtils;
import org.apache.jmeter.exceptions.IllegalUserActionException;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.WorkBench;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.jorphan.util.JOrphanUtils;
import org.apache.log.Logger;
import org.json.JSONObject;


public class EvaluationUtil {
	public static final String JMX_FILE_EXTENSION = ".jmx";

	public static String SaveScript(String stuStr) {
		Logger log = LoggingManager.getLoggerForClass();
		try {
			// get the whole testPlan as a hashTree
			HashTree subTree = null;
			HashTree testPlan = GuiPackage.getInstance().getTreeModel()
					.getTestPlan();
			// If saveWorkBench
			JMeterTreeNode workbenchNode = (JMeterTreeNode) ((JMeterTreeNode) GuiPackage
					.getInstance().getTreeModel().getRoot()).getChildAt(1);
			if (((WorkBench) workbenchNode.getUserObject()).getSaveWorkBench()) {
				HashTree workbench = GuiPackage.getInstance().getTreeModel()
						.getWorkBench();
				testPlan.add(workbench);
			}
			subTree = testPlan;

			// get the savePath of testPlan
			String number = EncryptionUtil.decryptDES(stuStr);
			String[] stuStrParts = number.split("_");
			String updateFile = Constants.DOWNLOAD_PATH + stuStrParts[0] + "/"
					+ stuStrParts[1] + "/" + "Test"
					+ System.currentTimeMillis() + JMX_FILE_EXTENSION;

			// Make sure the file ends with proper extension
			if (FilenameUtils.getExtension(updateFile).equals("")) {
				updateFile = updateFile + JMX_FILE_EXTENSION;

				// Check if the user is trying to save to an existing file
				/*
				 * File f = new File(updateFile); if (f.exists()) { int response
				 * = JOptionPane .showConfirmDialog(
				 * GuiPackage.getInstance().getMainFrame(), JMeterUtils
				 * .getResString("save_overwrite_existing_file"), // $NON-NLS-1$
				 * JMeterUtils.getResString("save?"), // $NON-NLS-1$
				 * JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE); if
				 * (response == JOptionPane.CLOSED_OPTION || response ==
				 * JOptionPane.NO_OPTION) { return; // Do not save, user does
				 * not want to overwrite } }
				 */
			}

			try {
				convertSubTree(subTree);
			} catch (Exception err) {
				log.warn("Error converting subtree " + err);
			}

			FileOutputStream ostream = null;
			try {
				ostream = new FileOutputStream(updateFile);
				SaveService.saveTree(subTree, ostream);
			} catch (Throwable ex) {
				log.error("Error saving tree:", ex);
				if (ex instanceof Error) {
					throw (Error) ex;
				}
				if (ex instanceof RuntimeException) {
					throw (RuntimeException) ex;
				}
				throw new IllegalUserActionException(
						"Couldn't save test plan to file: " + updateFile, ex);
			} finally {
				JOrphanUtils.closeQuietly(ostream);
			}
			GuiPackage.getInstance().updateCurrentGui();

			return updateFile;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// package protected to allow access from test code
	private static void convertSubTree(HashTree tree) {
		Iterator<Object> iter = new LinkedList<Object>(tree.list()).iterator();
		while (iter.hasNext()) {
			JMeterTreeNode item = (JMeterTreeNode) iter.next();
			convertSubTree(tree.getTree(item));
			TestElement testElement = item.getTestElement(); // requires
																// JMeterTreeNode
			tree.replace(item, testElement);
		}
	}

	public static JSONObject runScript(String fileURL, ActionMode mode) {
		JSONObject processDataJson = new JSONObject();
		JSONObject scoreJson = new JSONObject();

		// read problem info from c:/mooctest-jmeter/projects/pro.mt
		
		try {
			File proInfoFile = new File(Constants.DOWNLOAD_PATH + "pro.mt");
			BufferedReader bw = new BufferedReader(new FileReader(proInfoFile));
			String proIdStr = bw.readLine();
			String proNameStr = bw.readLine();
			String subIdStr = bw.readLine();
			
			scoreJson.put("pro_id", proIdStr);
			scoreJson.put("pro_name", proNameStr);
			scoreJson.put("sub_id", subIdStr);
			//scoreJson.put("eval_standard_id", evalStandardIdStr);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		float finalScore = new Float(0.0);
		float score_1 = new Float(0.0);
		// caculate score
		finalScore = 83;
		score_1 = 20;
		
		// get the score by a given strategy of calculation
		scoreJson.put("score", new Float(finalScore).toString());
		scoreJson.put("score_1", new Float(score_1).toString());

		processDataJson.put("score", scoreJson);

		return processDataJson;
	}
}
