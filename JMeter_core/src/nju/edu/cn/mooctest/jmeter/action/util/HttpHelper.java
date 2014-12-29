package nju.edu.cn.mooctest.jmeter.action.util;

import java.lang.reflect.Method;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public class HttpHelper {
	
	private static ServletContext servletContext;
	
	/** 获取 URL 地址在文件系统的绝对路径,
	 * 
	 * Servlet 2.4 以上通过 request.getServletContext().getRealPath() 获取,
	 * Servlet 2.4 以下通过 request.getRealPath() 获取。
	 *  
	 */
	@SuppressWarnings("deprecation")
	public final static String getRequestRealPath(HttpServletRequest request, String path)
	{
		if(servletContext != null)
			return servletContext.getRealPath(path);
		else
		{
			try
			{
				Method m = request.getClass().getMethod("getServletContext");
				ServletContext sc = (ServletContext)m.invoke(request);
				return sc.getRealPath(path);
			}
			catch(Exception e)
			{
				return request.getRealPath(path);
			}
		}
	}
}
