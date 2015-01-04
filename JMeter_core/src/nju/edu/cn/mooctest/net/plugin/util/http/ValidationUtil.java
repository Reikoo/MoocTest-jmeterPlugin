package nju.edu.cn.mooctest.net.plugin.util.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import nju.edu.cn.mooctest.net.plugin.common.AdvStage;
import nju.edu.cn.mooctest.net.plugin.common.AuthToken;
import nju.edu.cn.mooctest.net.plugin.common.Constants;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class ValidationUtil {
	public static String validateLogin(String url, String stuStr,
			String macAddress) throws HttpHostConnectException {
		String jsonResponse = null;

		CloseableHttpClient httpclient = HttpClients.createDefault();
		URI uri = null;
		try {
			uri = new URIBuilder().setPath(url).setParameter("stuStr", stuStr)
					.setParameter("macAddress", macAddress).build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		try {
			HttpPost httppost = new HttpPost(uri);

			System.out
					.println("executing request " + httppost.getRequestLine());
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {

				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_OK) {
					System.err.println("Error Response!");
					System.err
							.println("----------------------------------------");
					System.err.println(response.getStatusLine());
					jsonResponse = "CONNECTION_FAILED";
				} else {
					HttpEntity entity = response.getEntity();
					StringBuilder entityStringBuilder = new StringBuilder();
					if (entity != null) {
						try {
							BufferedReader bufferedReader = new BufferedReader(
									new InputStreamReader(entity.getContent(),
											"UTF-8"), 8 * 1024);
							String line = null;
							while ((line = bufferedReader.readLine()) != null) {
								entityStringBuilder.append(line + "/n");
							}
							// resultJsonObject=new
							// JSONObject(entityStringBuilder.toString());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					jsonResponse = entityStringBuilder.toString();

					System.out.println("The response returns" + jsonResponse);
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

		return jsonResponse;
	}

	public static String getExamProblemJson(String url, String stuStr,
			String macAddress) {
		String jsonResponse = null;

		CloseableHttpClient httpclient = HttpClients.createDefault();

		URI uri = null;
		try {
			uri = new URIBuilder().setPath(url).setParameter("stuStr", stuStr)
					.setParameter("macAddress", macAddress).build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		try {
			HttpPost httppost = new HttpPost(uri);

			System.out
					.println("executing request " + httppost.getRequestLine());
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {

				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_OK) {
					System.err.println("Error Response!");
					System.err
							.println("----------------------------------------");
					System.err.println(response.getStatusLine());
					jsonResponse = "CONNECTION_FAILED";
				} else {
					HttpEntity entity = response.getEntity();
					StringBuilder entityStringBuilder = new StringBuilder();
					if (entity != null) {
						try {
							BufferedReader bufferedReader = new BufferedReader(
									new InputStreamReader(entity.getContent(),
											"UTF-8"), 8 * 1024);
							String line = null;
							while ((line = bufferedReader.readLine()) != null) {
								entityStringBuilder.append(line + "/n");
							}
							// resultJsonObject=new
							// JSONObject(entityStringBuilder.toString());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					jsonResponse = entityStringBuilder.toString();
					System.out.println("The response returns " + jsonResponse);
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

		return jsonResponse;
	}

	public static AuthToken isLogin() throws Exception {
		// use auth.mt in MOOCTEST_ROOTPATH to judge whether one is Logged onto
		// the server
		AuthToken auth = new AuthToken();
		
		File file = new File(Constants.AUTH_FILE);
		if (!file.exists()) {
			return null;
		} else {
			BufferedReader br = new BufferedReader(new FileReader(file));
			
			// Read user mode, 0 for exam, 1 for adventure
			String str = br.readLine();
			auth.setUserMode(Integer.parseInt(str));
			
			// Read user token, stuStr for exam, stuId for adventure
			str = br.readLine();
			auth.setToken(str);
			
			br.close();

			return auth;
		}

	}
	
	public static AdvStage getAdvProblem() throws Exception {
		// check if adv.mt in MOOCTEST_ROOTPATH to judge current adventure problem
		AdvStage advSta = new AdvStage();
		
		File file = new File(Constants.ADV_FILE);
		if (!file.exists()) {
			return null;
		} else {
			BufferedReader br = new BufferedReader(new FileReader(file));
			
			// The sequence of adv.mt is subId, stageNum, proId, subName, proName, proLoc
			
			// Read subId
			String str = br.readLine();
			advSta.setSubId(Long.parseLong(str));
			
			// Read stageNumber
			str = br.readLine();
			advSta.setStageNum(Integer.parseInt(str));
			
			// Read proId
			str = br.readLine();
			advSta.setProId(Long.parseLong(str));
			
			// Read subName
			str = br.readLine();
			advSta.setSubName(str);
			
			// Read proName
			str = br.readLine();
			advSta.setProName(str);
			
			// Read proLoc
			str = br.readLine();
			advSta.setProLoc(str);
			
			br.close();

			return advSta;
		}
	}

	public static void updateAdvProblem(long subId, int stageNum , long proId , String subName , String proName , String proLoc) throws IOException {
		File file = new File(Constants.ADV_FILE);
		
		if (!file.exists()){
			file.createNewFile();
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));

		// write subId
		writer.append(new Long(subId).toString());
		writer.append("\n");
		
		// write stageNum
		writer.append((new Integer(stageNum)).toString());
		writer.append("\n");
		
		// write proId
		writer.append((new Long(proId)).toString());
		writer.append("\n");
		

		// write subName
		writer.append(subName);
		writer.append("\n");
		

		// write proName
		writer.append(proName);
		writer.append("\n");
		

		// write proLoc
		writer.append(proLoc);
		writer.append("\n");

		writer.flush();
		writer.close();
		
	}
	
	public static String validateAdvLogin(String url, String stuAccount,
			String password) {
		String jsonResponse = null;

		CloseableHttpClient httpclient = HttpClients.createDefault();
		URI uri = null;
		try {
			uri = new URIBuilder().setPath(url).setParameter("stuAccount", stuAccount)
					.setParameter("password", password).build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		try {
			HttpPost httppost = new HttpPost(uri);

			System.out
					.println("executing request " + httppost.getRequestLine());
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {

				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_OK) {
					System.err.println("Error Response!");
					System.err
							.println("----------------------------------------");
					System.err.println(response.getStatusLine());
					jsonResponse = "CONNECTION_FAILED";
				} else {
					HttpEntity entity = response.getEntity();
					StringBuilder entityStringBuilder = new StringBuilder();
					if (entity != null) {
						try {
							BufferedReader bufferedReader = new BufferedReader(
									new InputStreamReader(entity.getContent(),
											"UTF-8"), 8 * 1024);
							String line = null;
							while ((line = bufferedReader.readLine()) != null) {
								entityStringBuilder.append(line + "/n");
							}
							// resultJsonObject=new
							// JSONObject(entityStringBuilder.toString());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					jsonResponse = entityStringBuilder.toString();

					System.out.println("The response returns" + jsonResponse);
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

		return jsonResponse;
	}

	public static String getAdvSubjectListJson(String url, Long stuId) {
		String jsonResponse = null;

		CloseableHttpClient httpclient = HttpClients.createDefault();
		URI uri = null;
		try {
			uri = new URIBuilder().setPath(url).setParameter("stuId", new Long(stuId).toString()).build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		try {
			HttpPost httppost = new HttpPost(uri);

			System.out
					.println("executing request " + httppost.getRequestLine());
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {

				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_OK) {
					System.err.println("Error Response!");
					System.err
							.println("----------------------------------------");
					System.err.println(response.getStatusLine());
					jsonResponse = "CONNECTION_FAILED";
				} else {
					HttpEntity entity = response.getEntity();
					StringBuilder entityStringBuilder = new StringBuilder();
					if (entity != null) {
						try {
							BufferedReader bufferedReader = new BufferedReader(
									new InputStreamReader(entity.getContent(),
											"UTF-8"), 8 * 1024);
							String line = null;
							while ((line = bufferedReader.readLine()) != null) {
								entityStringBuilder.append(line + "/n");
							}
							// resultJsonObject=new
							// JSONObject(entityStringBuilder.toString());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					jsonResponse = entityStringBuilder.toString();

					System.out.println("The response returns" + jsonResponse);
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

		return jsonResponse;
	}

	public static String getAdvProblemJson(String url, String stageStr) {
		String jsonResponse = null;

		CloseableHttpClient httpclient = HttpClients.createDefault();
		URI uri = null;
		try {
			uri = new URIBuilder().setPath(url).setParameter("stageStr", stageStr).build();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		try {
			HttpPost httppost = new HttpPost(uri);

			System.out
					.println("executing request " + httppost.getRequestLine());
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {

				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_OK) {
					System.err.println("Error Response!");
					System.err
							.println("----------------------------------------");
					System.err.println(response.getStatusLine());
					jsonResponse = "CONNECTION_FAILED";
				} else {
					HttpEntity entity = response.getEntity();
					StringBuilder entityStringBuilder = new StringBuilder();
					if (entity != null) {
						try {
							BufferedReader bufferedReader = new BufferedReader(
									new InputStreamReader(entity.getContent(),
											"UTF-8"), 8 * 1024);
							String line = null;
							while ((line = bufferedReader.readLine()) != null) {
								entityStringBuilder.append(line + "/n");
							}
							// resultJsonObject=new
							// JSONObject(entityStringBuilder.toString());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					jsonResponse = entityStringBuilder.toString();

					System.out.println("The response returns" + jsonResponse);
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

		return jsonResponse;
	}

}
