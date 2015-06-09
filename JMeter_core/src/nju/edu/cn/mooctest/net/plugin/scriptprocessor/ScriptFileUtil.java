package nju.edu.cn.mooctest.net.plugin.scriptprocessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;

import nju.edu.cn.mooctest.net.plugin.common.Constants;
import nju.edu.cn.mooctest.net.plugin.util.encryption.EncryptionUtil;

import org.apache.commons.io.FilenameUtils;
import org.apache.jmeter.JMeter;
import org.apache.jmeter.engine.JMeterEngine;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.exceptions.IllegalUserActionException;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.gui.tree.JMeterTreeNode;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.WorkBench;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.jorphan.util.JOrphanUtils;
import org.apache.log.Logger;

public class ScriptFileUtil {
	public static final String JMX_FILE_EXTENSION = ".jmx";
	
	public static HashTree loadJMX(File scriptFile) throws Exception {
		//JMeter initialization (properties, log levels, locale, etc)
        JMeterUtils.loadJMeterProperties(JMeterUtils.getJMeterBinDir() + "/jmeter.properties");//TODO
        JMeterUtils.initLogging();// you can comment this line out to see extra log messages of i.e. DEBUG level
        JMeterUtils.initLocale();
        
		// Initialize JMeter SaveService
        try {
			SaveService.loadProperties();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        
		InputStream reader = new FileInputStream(scriptFile);
		HashTree tree = SaveService.loadTree(reader);
		if (tree == null) {
            throw new Exception("There was problems loading test plan. " +
            		"Please investigate error messages above.");
        }
		JMeter.convertSubTree(tree); //remove the disabled items
		
		JMeterEngine engine = new StandardJMeterEngine();
		engine.configure(tree);
		return tree;
	}
	
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
}
