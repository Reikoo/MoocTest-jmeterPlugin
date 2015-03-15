package nju.edu.cn.mooctest.jmeter.action;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import org.apache.jmeter.gui.action.ActionNames;
import org.apache.jmeter.gui.action.ActionRouter;
import org.apache.jmeter.gui.action.Command;
import org.apache.jmeter.gui.util.EscapeDialog;
import org.apache.jmeter.gui.util.FileDialoger;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.ComponentUtil;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.jorphan.util.JOrphanUtils;
import org.apache.log.Logger;

public class MooctestViewProblem implements Command{
	private static final Logger log = LoggingManager.getLoggerForClass();
	
	private static final Set<String> commands = new HashSet<String>();
	
//	private static final String PROBLEM_DOCS = JMeterUtils.getJMeterHome()
//			+ "/projects/";  // $NON-NLS-1$
//
//	private static final String PROBLEM_FILE = PROBLEM_DOCS + "test.txt";
	
	private static JDialog problemWindow;
	
	private static final JTextPane problemDoc;
	
	private static final JScrollPane scroller;
	
    static {
        commands.add(ActionNames.MOOCTEST_VIEW_PROBLEM);
        problemDoc = new JTextPane();
        scroller = new JScrollPane(problemDoc);
        problemDoc.setEditable(false);
    }
    
    public MooctestViewProblem() {
    	
    }
    
    /**
     * Gets the ActionNames attribute of the action
     *
     * @return the ActionNames value
     */
    @Override
    public Set<String> getActionNames() {
        return commands;
    }

    /**
     * This method performs the actual command processing.
     *
     * @param e
     *            the generic UI action event
     */
    @Override
    public void doAction(ActionEvent e) {
    	final JFileChooser chooser = FileDialoger.promptToOpenFile(new String[] { ".txt" }); //$NON-NLS-1$
        if (chooser == null) {
            return;
        }
        final File selectedFile = chooser.getSelectedFile();
        if(selectedFile != null) {
            loadTestProblem(e, selectedFile);
        }
    }
    
    static void loadTestProblem(final ActionEvent e, final File f) {
    	ActionRouter.getInstance().doActionNow(new ActionEvent(e.getSource(), e.getID(), ActionNames.STOP_THREAD));
    	
    	if (f != null) {
    		if (problemWindow == null) {
	    		problemWindow = new EscapeDialog(new Frame(),// independent frame to
	                    									// allow it to be overlaid
	                    									// by the main frame 
	    				JMeterUtils.getResString("mooctest_view_problem"),
	    				false);
	    		problemWindow.getContentPane().setLayout(new GridLayout(1, 1));
	    		problemWindow.getContentPane().removeAll();
	    		problemWindow.getContentPane().add(scroller);
	    		ComponentUtil.centerComponentInWindow(problemWindow, 60);
	    	}
	    	problemWindow.setVisible(true); // set the window visible immediately
	    	
    		BufferedReader reader = null;
    		StringBuffer sb = new StringBuffer();
    			
    		try {
    			reader = new BufferedReader(new FileReader(f));
    			String line = null;
    			
    			while ((line = reader.readLine()) != null) {
    				sb.append(line + "\r\n");
    			}
    			problemDoc.setContentType("text/plain");
    	        problemDoc.setText(sb.toString());
    		} catch (IOException ex) {
                reportError("Error reading file: ", ex, false);
            } catch (Exception ex) {
                reportError("Unexpected error", ex, true);
            } finally {
                JOrphanUtils.closeQuietly(reader);
            }
    	}
	}
    
    // Helper method to simplify code
    private static void reportError(final String reason, final Throwable ex, final boolean stackTrace) {
        if (stackTrace) {
            log.warn(reason, ex);
        } else {
            log.warn(reason + ex);
        }
        String msg = ex.getMessage();
        if (msg == null) {
            msg = "Unexpected error - see log for details";
        }
        JMeterUtils.reportErrorToUser(msg);
    }

}
