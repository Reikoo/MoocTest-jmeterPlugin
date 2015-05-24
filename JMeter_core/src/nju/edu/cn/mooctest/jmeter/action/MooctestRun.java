package nju.edu.cn.mooctest.jmeter.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import nju.edu.cn.mooctest.net.plugin.common.ActionMode;
import nju.edu.cn.mooctest.net.plugin.common.AuthToken;
import nju.edu.cn.mooctest.net.plugin.util.file.FileUtil;
import nju.edu.cn.mooctest.net.plugin.util.http.EvaluationUtil;
import nju.edu.cn.mooctest.net.plugin.util.http.ValidationUtil;

import org.apache.jmeter.gui.action.AbstractAction;
import org.apache.jmeter.gui.action.ActionNames;
import org.apache.jmeter.gui.action.LoadRecentProject;
import org.json.JSONObject;

public class MooctestRun extends AbstractAction {

	private static final Set<String> commandSet;

	static {
		HashSet<String> commands = new HashSet<String>();
		commands.add(ActionNames.MOOCTEST_RUN);
		commandSet = Collections.unmodifiableSet(commands);
	}

	@Override
	public void doAction(ActionEvent e) {
		if (e.getActionCommand().equals(ActionNames.MOOCTEST_RUN)) {
			popupShouldSave(e);
			runTest();
		}
	}

	/**
	 * Provide the list of Action names that are available in this command.
	 */
	@Override
	public Set<String> getActionNames() {
		return MooctestRun.commandSet;
	}

    private void runTest() {
    	String path = LoadRecentProject.getRecentFile(0);
		JSONObject testScore = EvaluationUtil.runScript(new File(path), ActionMode.RUN);
		AuthToken auth = null;
		try {
			auth = ValidationUtil.isLogin();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String stuStr = auth.getToken();
		FileUtil.recordExamResult(stuStr, path, testScore);
		//TODO 界面展示分数
		JTableTest table = new JTableTest(testScore);
    }
    
    class JTableTest extends JFrame {
    	public JTableTest(JSONObject testScore) {  
            intiComponent(testScore);  
        }  
      
        /** 
         * 初始化窗体组件 
         */  
        private void intiComponent(JSONObject scoreJson) {  
        	String[] columnNames = {"总分","错误率","线程数","启动时间","循环次数","集结点","参数化"};
        	
        	scoreJson = scoreJson.getJSONObject("score");
        	double score = scoreJson.getDouble("score");
        	double numThreadsScore = scoreJson.getDouble("num_threads");
        	double rampUpScore = scoreJson.getDouble("ramp_up");
        	double loopScore = scoreJson.getDouble("loops");
        	double syncScore = scoreJson.getDouble("sync_timer");
        	double parameterScore = scoreJson.getDouble("parameter");
        	double errorScore = scoreJson.getDouble("max_error");
        	
        	Object[][] obj = new Object[1][7];
        	obj[0][0] = score;
        	obj[0][1] = errorScore;
        	obj[0][2] = numThreadsScore;
        	obj[0][3] = rampUpScore;
        	obj[0][4] = loopScore;
        	obj[0][5] = syncScore;
        	obj[0][6] = parameterScore;
        	
        	JTable table = new JTable(obj, columnNames);  
        	
        	/* 
             * 设置JTable的列默认的宽度和高度 
             */  
            TableColumn column = null;  
            int colunms = table.getColumnCount();  
            for(int i = 0; i < colunms; i++)  
            {  
                column = table.getColumnModel().getColumn(i);  
                /*将每一列的默认宽度设置为100*/  
                column.setPreferredWidth(100);  
            }  
            /* 
             * 设置JTable自动调整列表的状态，此处设置为关闭 
             */  
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);  
              
            /*用JScrollPane装载JTable，这样超出范围的列就可以通过滚动条来查看*/  
            JScrollPane scroll = new JScrollPane(table);  
            scroll.setSize(300, 200);  
              
            add(scroll);  
            this.setVisible(true);  
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
            this.pack();  
        }
    }
}
