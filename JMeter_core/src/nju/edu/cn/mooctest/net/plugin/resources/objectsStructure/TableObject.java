package nju.edu.cn.mooctest.net.plugin.resources.objectsStructure;

public class TableObject {
	private String projectName , statementCov , branchCov , loopCov , finalScore;// , strictConditionCov , finalResult;

	public TableObject(){
		
	}
	
	public TableObject(String pro , String sta , String bran , String loop , String finalScore){
		this.setProjectName(pro);
		this.statementCov = sta;
		this.branchCov = bran;
		this.loopCov = loop;
		this.finalScore = finalScore;
	}
	
	public String getBranchCov() {
		return branchCov;
	}

	public void setBranchCov(String branchCov) {
		this.branchCov = branchCov;
	}


	public String getFinalScore() {
		return finalScore;
	}

	public void setFinalScore(String finalScore) {
		this.finalScore = finalScore;
	}

	public String getLoopCov() {
		return loopCov;
	}

	public void setLoopCov(String loopCov) {
		this.loopCov = loopCov;
	}

	public String getStatementCov() {
		return statementCov;
	}

	public void setStatementCov(String statementCov) {
		this.statementCov = statementCov;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	
	
}
