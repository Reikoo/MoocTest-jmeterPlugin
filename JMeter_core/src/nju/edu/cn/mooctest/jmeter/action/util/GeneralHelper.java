package nju.edu.cn.mooctest.jmeter.action.util;

public class GeneralHelper {
	
	/** String -> Integer，如果转换不成功则返回 null */
	public final static Integer str2Int(String s)
	{
		Integer returnVal;
		try {
			returnVal = Integer.decode(safeTrimString(s));
		} catch(Exception e) {
			returnVal = null;
		}
		return returnVal;
	}
	
	/** String -> int，如果转换不成功则返回默认值 d */
	public final static int str2Int(String s, int d)
	{
		int returnVal;
		try {
			returnVal = Integer.parseInt(safeTrimString(s));
		} catch(Exception e) {
			returnVal = d;
		}
		return returnVal;
	}
	
	/** 把参数 obj 转换为安全字符串：如果 obj = null，则把它转换为空字符串 */
	public final static String safeString(Object obj)
	{
		if(obj == null)
			return "";
		
		return obj.toString();
	}

	/** 把参数 str 转换为安全字符串并执行去除前后空格：如果 str = null，则把它转换为空字符串 */
	public final static String safeTrimString(String str)
	{
		return safeString(str).trim();
	}
}
