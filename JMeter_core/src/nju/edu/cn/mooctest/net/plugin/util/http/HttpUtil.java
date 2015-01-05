package nju.edu.cn.mooctest.net.plugin.util.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import nju.edu.cn.mooctest.net.plugin.common.Constants;
import nju.edu.cn.mooctest.net.plugin.test.TestUtil;
import nju.edu.cn.mooctest.net.plugin.util.file.CompressFileUtil;
import nju.edu.cn.mooctest.net.plugin.util.file.FileUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class HttpUtil {
	public static final int BUFFER_SIZE = 4096;
	 
    /**
     * Downloads a file from a URL
     * @param fileURL HTTP URL of the file to be downloaded
     * @param saveDir path of the directory to save the file
     * @throws IOException
     */
    public static String downloadFile(String baseUrl, String fileUrl , String saveDir)
            throws IOException {
    	String saveFilePath = null;
    	
		CloseableHttpClient httpclient = HttpClients.createDefault();
		URI uri = null;
		try {
			uri = new URIBuilder().setPath(baseUrl)
			.setParameter("fileUrl", fileUrl)
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		try{
		    HttpGet httpGet = new HttpGet(uri);  
	        HttpResponse httpResponse = httpclient.execute(httpGet);  
	  
	        StatusLine statusLine = httpResponse.getStatusLine();  
	        if (statusLine.getStatusCode() == 200) {  
	        	String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1,
	                    fileUrl.length());
	        	saveFilePath = saveDir + "/" + fileName;
	            FileUtil.createFolder(saveDir);
	            // opens an output stream to save into file
	            // if file not exists, FileNotFoundException - Permission denied will be thrown
	            File saveFile = new File(saveFilePath);
	            if(!saveFile.exists())
	            {
	               saveFile.createNewFile();
	            }
	            saveFile.setWritable(true);
	            FileOutputStream outputStream = new FileOutputStream(saveFile);  
	              
	            HttpEntity entity = httpResponse.getEntity();
	            InputStream inputStream = entity.getContent(); 
	              
	            byte buff[] = new byte[4096];  
	            int counts = 0;  
	            while ((counts = inputStream.read(buff)) != -1) {  
	                outputStream.write(buff, 0, counts);  
	            }  
	            outputStream.flush();  
	            outputStream.close();  
	  
	            System.out.println("Download success!");    
	              
	        }  else {
	            System.out.println("No file to download. Server replied HTTP code: " + statusLine.getStatusCode());
	            throw new IOException();
	        } 
			
		}
		finally{
			httpclient.close();
		}
       
        
        
        return saveFilePath;
    }
    
    public static String getProblemNames(String url){
    	String json = null;
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		try {
			HttpPost httppost = new HttpPost(url);

			System.out
					.println("executing request " + httppost.getRequestLine());
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_OK){
					System.err.println("Error Response!");
					System.err.println("----------------------------------------");
					System.err.println(response.getStatusLine());
				} else{
					HttpEntity entity = response.getEntity();
					StringBuilder entityStringBuilder = new StringBuilder();
					if (entity != null) {
		                try {
		                    BufferedReader bufferedReader=new BufferedReader
		                    (new InputStreamReader(entity.getContent(), "UTF-8"), 8*1024);
		                    String line=null;
		                    while ((line=bufferedReader.readLine())!=null) {
		                        entityStringBuilder.append(line+"/n");
		                    }
		                    //resultJsonObject=new JSONObject(entityStringBuilder.toString());
		                } catch (Exception e) {
		                    e.printStackTrace();
		                }
		            }
					// TODO: change to JSON object or something more organized
					json = entityStringBuilder.toString();
					
	
					
					if (statusCode != HttpStatus.SC_OK){
						System.err.println("Error Response!");
						System.err.println("----------------------------------------");
						System.err.println(response.getStatusLine());
					}
				}
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		return json;
    }

    public static String getJsonResponse(String url){
    	String json = null;
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpPost httppost = new HttpPost(url);

			System.out
					.println("executing request " + httppost.getRequestLine());
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_OK){
					System.err.println("Error Response!");
					System.err.println("----------------------------------------");
					System.err.println(response.getStatusLine());
				} else{
					HttpEntity entity = response.getEntity();
					StringBuilder entityStringBuilder = new StringBuilder();
					if (entity != null) {
		                try {
		                    BufferedReader bufferedReader=new BufferedReader
		                    (new InputStreamReader(entity.getContent(), "UTF-8"), 8*1024);
		                    String line=null;
		                    while ((line=bufferedReader.readLine())!=null) {
		                        entityStringBuilder.append(line+"/n");
		                    }
		                    //resultJsonObject=new JSONObject(entityStringBuilder.toString());
		                } catch (Exception e) {
		                    e.printStackTrace();
		                }
		            }
					// TODO: change to JSON object or something more organized
					json = entityStringBuilder.toString();
					
	
					
					if (statusCode != HttpStatus.SC_OK){
						System.err.println("Error Response!");
						System.err.println("----------------------------------------");
						System.err.println(response.getStatusLine());
					}
				}
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		return json;
    }

	public static String submitAnswerWithScore(String url , String stuStr , String curPath , String curName , String scoreJson) throws Exception {
		String jsonResponse = null;
		String zipFilePath = curPath + "../" + curName + "_answer.zip";
		CompressFileUtil.zip(curPath, zipFilePath);
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		// form the uri
		URI uri = null;
		try {
			uri = new URIBuilder().setPath(url)
			.setParameter("stuStr", stuStr)
			.setParameter("macAddress" , NetworkUtil.getMACAddress())
			.build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		try {
			HttpPost httppost = new HttpPost(uri);

			FileBody bin = new FileBody(new File(zipFilePath));
			StringBody scoreBody = new StringBody(scoreJson, ContentType.TEXT_PLAIN);

			HttpEntity reqEntity = MultipartEntityBuilder.create().
					addPart("file", bin).
					addPart("scoreJson", scoreBody).
					build();

			httppost.setEntity(reqEntity);

			System.out
					.println("executing request " + httppost.getRequestLine());
			// for testing
			TestUtil.appendToFile(new File(Constants.MOOCTEST_ROOT_PATH + "test.mt"), String.valueOf(System.currentTimeMillis()) + "       :        " + "executing request " + httppost.getRequestLine());
			
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_OK){
					System.err.println("Error Response!");
					System.err.println("----------------------------------------");
					System.err.println(response.getStatusLine());
				}
				else{
					HttpEntity entity = response.getEntity();
					StringBuilder entityStringBuilder = new StringBuilder();
					if (entity != null) {
		                try {
		                    BufferedReader bufferedReader=new BufferedReader
		                    (new InputStreamReader(entity.getContent(), "UTF-8"), 8*1024);
		                    String line=null;
		                    while ((line=bufferedReader.readLine())!=null) {
		                        entityStringBuilder.append(line+"/n");
		                    }
		                    //resultJsonObject=new JSONObject(entityStringBuilder.toString());
		                } catch (Exception e) {
		                    e.printStackTrace();
		                }
		            }
					
					jsonResponse = entityStringBuilder.toString();
					
					System.out.println("In HttpUtil.submitAnswerWithScore");
					System.out.println("The response returns" + jsonResponse);
					// for testing
					TestUtil.appendToFile(new File(Constants.MOOCTEST_ROOT_PATH + "test.mt"), String.valueOf(System.currentTimeMillis()) + "       :        " + "The response returns" + jsonResponse);
				}
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return jsonResponse;
	
		
	}

	public static String submitAdvAnswerWithScore(String url, Long stuId,
			Long subId, Integer stageNum, String curPath, String curName,
			String scoreJson) throws IOException {
		String jsonResponse = null;
		String zipFilePath = curPath + "../" + curName + "_answer.zip";
		CompressFileUtil.zip(curPath, zipFilePath);
		
		CloseableHttpClient httpclient = HttpClients.createDefault();
		
		// form the uri
		URI uri = null;
		try {
			uri = new URIBuilder().setPath(url)
					.setParameter("stuId", stuId.toString())
					.setParameter("subId", subId.toString())
					.setParameter("stageNum" , stageNum.toString())
					.build();
			
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		
		try {
			HttpPost httppost = new HttpPost(uri);

			FileBody bin = new FileBody(new File(zipFilePath));
			StringBody scoreBody = new StringBody(scoreJson, ContentType.TEXT_PLAIN);

			HttpEntity reqEntity = MultipartEntityBuilder.create().
					addPart("file", bin).
					addPart("scoreJson", scoreBody).
					build();

			httppost.setEntity(reqEntity);

			System.out
					.println("executing request " + httppost.getRequestLine());
			// for testing
			TestUtil.appendToFile(new File(Constants.MOOCTEST_ROOT_PATH + "test.mt"), String.valueOf(System.currentTimeMillis()) + "       :        " + "executing request " + httppost.getRequestLine());
			
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_OK){
					System.err.println("Error Response!");
					System.err.println("----------------------------------------");
					System.err.println(response.getStatusLine());
				}
				else{
					HttpEntity entity = response.getEntity();
					StringBuilder entityStringBuilder = new StringBuilder();
					if (entity != null) {
		                try {
		                    BufferedReader bufferedReader=new BufferedReader
		                    (new InputStreamReader(entity.getContent(), "UTF-8"), 8*1024);
		                    String line=null;
		                    while ((line=bufferedReader.readLine())!=null) {
		                        entityStringBuilder.append(line+"/n");
		                    }
		                    //resultJsonObject=new JSONObject(entityStringBuilder.toString());
		                } catch (Exception e) {
		                    e.printStackTrace();
		                }
		            }
					
					jsonResponse = entityStringBuilder.toString();
					
					System.out.println("In HttpUtil.submitAnswerWithScore");
					System.out.println("The response returns" + jsonResponse);
					// for testing
					TestUtil.appendToFile(new File(Constants.MOOCTEST_ROOT_PATH + "test.mt"), String.valueOf(System.currentTimeMillis()) + "       :        " + "The response returns" + jsonResponse);
				}
			} finally {
				response.close();
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return jsonResponse;
	}


}
