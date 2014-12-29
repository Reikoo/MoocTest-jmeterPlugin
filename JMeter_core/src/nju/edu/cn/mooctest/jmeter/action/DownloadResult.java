package nju.edu.cn.mooctest.jmeter.action;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

/**
 * @author I314068
 */
public class DownloadResult extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private static JDialog resultDialog;

	private static final Logger log = LoggingManager.getLoggerForClass();
	
	private final Map<String, List<Integer>> data = new ConcurrentHashMap<String, List<Integer>>();

	/**
	 * Initialize a download result panel
	 * @param result
	 */
	public void init(String result) {
		
	}
}
