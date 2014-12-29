package nju.edu.cn.mooctest.jmeter.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nju.edu.cn.mooctest.jmeter.action.util.GeneralHelper;
import nju.edu.cn.mooctest.jmeter.action.util.HttpHelper;
import nju.edu.cn.mooctest.jmeter.action.util.Range;
import nju.edu.cn.mooctest.jmeter.action.util.Result;

import org.apache.jmeter.util.JMeterUtils;

/**
 * 
 * @author I314068
 *
 */
public class FileDownloader {
	/** 默认字节交换缓冲区大小 */
    public static final int DEFAULT_BUFFER_SIZE        = 1024 * 4;
    /** 下载文件的默认 Mime Type */
    public static final String DEFAULT_CONTENT_TYPE    = "application/force-download";
    
    private static final String DEFAULT_AGENT_CHARSET = "UTF-8";
    
    /** 下载文件的 Mime Type，默认：{@link FileDownloader#DEFAULT_CONTENT_TYPE} */
    private String contentType        = DEFAULT_CONTENT_TYPE;
    /** 字节缓冲区大小，默认：{@link FileDownloader#DEFAULT_CONTENT_TYPE} */
    private int bufferSize            = DEFAULT_BUFFER_SIZE;
    
    /** 设置下载文件的路径（包含文件名）
     * filePath    : 文件路径为相对路径<br>
     */
    private static String FILE_PATH = "file:///"
			+ JMeterUtils.getJMeterHome();
    
    /** 显示在下载对话框中的文件名称，默认与  filePath 参数中的文件名一致 */
    private String saveFileName;
    
    /** 获取文件下载失败的原因（文件下载失败时使用） */
    private Throwable cause;

    private String filePath;
    
    public FileDownloader() {
    	
    }
    
    /** 构造函数
	 * 
	 * @param filePath		: 下载文件的路径（包含文件名），可能是相对路径或绝对路径，参考：{@link FileDownloader#setFilePath(String)} 
	 */
	public FileDownloader(String filePath)
	{
		this(filePath, DEFAULT_CONTENT_TYPE);
	}
	
	/** 构造函数
	 * 
	 * @param filePath		: 下载文件的路径（包含文件名），可能是相对路径或绝对路径，参考：{@link FileDownloader#setFilePath(String)} 
	 * @param contentType	: 下载文件的 Mime Type，默认：{@link FileDownloader#DEFAULT_CONTENT_TYPE}
	 */
	public FileDownloader(String filePath, String contentType)
	{
		this(filePath, contentType, new File(filePath).getName());
	}

	/** 构造函数
	 * 
	 * @param filePath		: 下载文件的路径（包含文件名），可能是相对路径或绝对路径，参考：{@link FileDownloader#setFilePath(String)} 
	 * @param contentType	: 下载文件的 Mime Type，默认：{@link FileDownloader#DEFAULT_CONTENT_TYPE}
	 * @param saveFileName	: 显示在浏览器的下载对话框中的文件名称，默认与  filePath 参数中的文件名一致
	 */	
	public FileDownloader(String filePath, String contentType, String saveFileName)
	{
		this(filePath, contentType, saveFileName, DEFAULT_BUFFER_SIZE);
	}
	

	/** 构造函数
	 * 
	 * @param filePath		: 下载文件的路径（包含文件名），可能是相对路径或绝对路径，参考：{@link FileDownloader#setFilePath(String)} 
	 * @param contentType	: 下载文件的 Mime Type，默认：{@link FileDownloader#DEFAULT_CONTENT_TYPE}
	 * @param saveFileName	: 显示在浏览器的下载对话框中的文件名称，默认与  filePath 参数中的文件名一致
	 * @param bufferSize	: 字节缓冲区大小，默认：{@link FileDownloader#DEFAULT_CONTENT_TYPE}
	 */
	public FileDownloader(String filePath, String contentType, String saveFileName, int bufferSize)
	{
		this.filePath		= filePath;
		this.contentType	= contentType;
		this.saveFileName	= saveFileName;
		this.bufferSize		= bufferSize;
	}

	/** 获取下载文件的路径（包含文件名） */
	public String getFilePath()
	{
		return filePath;
	}

	/** 设置下载文件的路径（包含文件名） 
	 * 
	 * @param filePath	: 文件路径，可能是绝对路径或相对路径<br>
	 * 						1) 绝对路径：以根目录符开始（如：'/'、'D:\'），是服务器文件系统的路径<br>
	 * 						2) 相对路径：不以根目录符开始，是相对于 WEB 应用程序 Context 的路径，（如：mydir/myfile 是指
	 * 							'${WEB-APP-DIR}/mydir/myfile'）
	 * 
	 */
	public void setFilePath(String filePath)
	{
		this.filePath	= filePath;
	}
	
	/** 获取下载文件的 Mime Type */
	public String getContentType()
	{
		return contentType;
	}

	/** 设置下载文件的 Mime Type */
	public void setContentType(String contentType)
	{
		this.contentType = contentType;
	}

	/** 获取显示在浏览器的下载对话框中的文件名称 */
	public String getSaveFileName()
	{
		return saveFileName;
	}

	/** 设置显示在浏览器的下载对话框中的文件名称 */
	public void setSaveFileName(String saveFileName)
	{
		this.saveFileName = saveFileName;
	}
	
	/** 获取字节交换缓冲区的大小 */
	public int getBufferSize()
	{
		return bufferSize;
	}

	/** 设置字节交换缓冲区的大小 */
	public void setBufferSize(int bufferSize)
	{
		this.bufferSize = bufferSize;
	}
	
	/** 获取文件下载失败的原因（文件下载失败时使用） */
	public Throwable getCause()
	{
		return cause;
	}
	
	private void reset()
	{
		cause = null;
	}
    
    /** 执行下载
     * 
     * @param request    : {@link HttpServletRequest} 对象
     * @param response    : {@link HttpServletResponse} 对象
     * 
     * @return            : 成功：返回 {@link Result#SUCCESS} ，失败：返回其他结果，
     *                       失败原因通过 {@link FileDownloader#getCause()} 获取
     * 
     */
    public Result downLoad(HttpServletRequest request, HttpServletResponse response)
    {        
    	reset();
    	
        try
        {
            File file = getFile(request);
            downloadFile(request, response, file);
        }
        catch(Exception e)
        {
            cause = e;
            if(e instanceof IOException)
            	return Result.READ_WRITE_FAIL;                                          
            return Result.UNKNOWN_EXCEPTION;
        }
        
        return Result.SUCCESS; 
    }
    
    private void downloadFile(HttpServletRequest request, HttpServletResponse response, File file) throws IOException
   	{
   		int length = (int)file.length();
   		Range<Integer> range = prepareDownload(request, response, length);
   		InputStream is = new BufferedInputStream(new FileInputStream(file));
   		
   		doDownload(response, is, range);
   	}

    // 实际执行下载操作
    private void doDownload(HttpServletResponse response, InputStream is, Range<Integer> range) throws IOException
	{
		int begin		= range.getBegin();
		int end			= range.getEnd();
		
		OutputStream os	= null;
		
		try
		{
			byte[] b	= new byte[bufferSize];
			os			= new BufferedOutputStream(response.getOutputStream());
			
			is.skip(begin);
			for(int i, left = end - begin + 1; left > 0 && ((i = is.read(b, 0, Math.min(b.length, left))) != -1); left -= i)
				os.write(b, 0, i);
			
			os.flush();
		}
		finally
		{
			if(is != null) {try{is.close();} catch(IOException e) {}}
			if(os != null) {try{os.close();} catch(IOException e) {}}
		}
	}
    
    private Range<Integer> prepareDownload(HttpServletRequest request, HttpServletResponse response, int length) throws UnsupportedEncodingException
	{
		String charset			= DEFAULT_AGENT_CHARSET;
		String fileName			= new String(saveFileName.getBytes(charset), "ISO-8859-1");
		Range<Integer> range	= parseDownloadRange(request);
		int begin				= 0;
		int end					= length - 1;
		
		response.setContentType(contentType);
		response.setContentLength(length);
		response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
		
		if(range != null)
		{
			if(range.getBegin()	!= null)
			{
				begin = range.getBegin();
				if(range.getEnd() != null)
					end = range.getEnd();
			}
			else
			{
				if(range.getEnd() != null)
					begin = Math.max(end + range.getEnd() + 1, 0);
			}
			
			String contentRange = String.format("bytes %d-%d/%d", begin, end, length);
			response.setHeader("Accept-Ranges", "bytes");
			response.setHeader("Content-Range", contentRange);
			response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
		}
		
		return new Range<Integer>(begin, end);
	}
    
    private static Range<Integer> parseDownloadRange(HttpServletRequest request)
	{
		Range<Integer> range	= null;
		String header			= request.getHeader("Range");
		
		if(header != null)
		{
			int preIndex = header.indexOf('=');
			if(preIndex != -1)
			{
				header	= header.substring(preIndex + 1).trim();
				int length	= header.length();
				
				if(length > 0)
				{
					int sepIndex	= header.indexOf('-');
					range			= new Range<Integer>();
					
					if(sepIndex >= 0)
					{
						String str	= header.substring(0, sepIndex);
						int begin	= GeneralHelper.str2Int(str, -1);
						
						if(begin != -1)
							range.setBegin(begin);
						
						if(sepIndex < length - 1)
						{
							str		= header.substring(sepIndex + 1, length);
							int end	= GeneralHelper.str2Int(str, -1);
							
							if(end != -1)
								range.setEnd((begin != -1) ? end : -end);
						}
					}
					else
					{
						int point = GeneralHelper.str2Int(header, -1);
						
						if(point != -1)
						{
							range.setBegin(point);
							range.setEnd(point);
						}
					}
				}
			}
		}
		
		return range;
	}
    
    private File getFile(HttpServletRequest request) throws FileNotFoundException
	{
		File file = new File(filePath);
		
		if(!file.isAbsolute())
			file = new File(HttpHelper.getRequestRealPath(request, filePath));
		if(!file.isFile())
			throw new FileNotFoundException(String.format("file '%s' not found or is directory", filePath));
		
		checkDownloadParam(file.getName());
		
		return file;
	}

	private void checkDownloadParam(String defSaveFileName) throws IllegalArgumentException
	{
		if(saveFileName == null)
		{
			if(defSaveFileName != null)
				saveFileName	= defSaveFileName;
			else
				throw new IllegalArgumentException("save file name is null");
		}
		
		if(contentType == null)
			contentType		= DEFAULT_CONTENT_TYPE;
		if(bufferSize <= 0)
			bufferSize		= DEFAULT_BUFFER_SIZE;
	}
    
}
