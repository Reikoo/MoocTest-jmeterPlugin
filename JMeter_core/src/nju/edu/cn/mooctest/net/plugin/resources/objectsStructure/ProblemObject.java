package nju.edu.cn.mooctest.net.plugin.resources.objectsStructure;

public class ProblemObject {
	private Long proId;
	private String proName; 
	private String proLoc;
	private Long subId;
	
	public ProblemObject(Long proId , String proName , String proLoc , Long subId){
		this.proId = proId; 
		this.proName = proName;
		this.proLoc = proLoc;
		this.subId = subId;
	}
	
	public Long getProId() {
		return proId;
	}
	public void setProId(Long proId) {
		this.proId = proId;
	}
	public String getProName() {
		return proName;
	}
	public void setProName(String proName) {
		this.proName = proName;
	}
	public String getProLoc() {
		return proLoc;
	}
	public void setProLoc(String proLoc) {
		this.proLoc = proLoc;
	}
	public Long getSubId() {
		return subId;
	}
	public void setSubId(Long subId) {
		this.subId = subId;
	}
	
	
}
