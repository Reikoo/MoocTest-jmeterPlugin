package nju.edu.cn.mooctest.net.plugin.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import nju.edu.cn.mooctest.net.plugin.common.Constants;
import nju.edu.cn.mooctest.net.plugin.util.encryption.EncryptionUtil;

import org.json.JSONObject;

public class FileUtil {
	/**
	 * create folder in case the parent directory does not exist
	 * 
	 * @param userAcount
	 * @param passwd
	 * @return
	 */
	public static void createFolder(String folderName) {
		File folder = new java.io.File(folderName);
		if (!folder.exists()) {
			folder.mkdirs();
			folder.setReadable(true);
			folder.setExecutable(true);
			folder.setWritable(true);
			System.out.println("Can Write? " + folder.canWrite());
		}
	}

	public static void deleteFile(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
	}

	public static String readFileContent(String filePath) {

		String content = "";

		File file = new File(filePath);
		if (!file.exists()) {
			return "";
		}
		FileReader fr = null;
		try {
			fr = new FileReader(file);
			char c;
			while ((c = (char) fr.read()) != (char) -1) {
				content += c;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fr != null) {
				try {
					fr.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return content;
	}

	public static void writeContentToFile(String path, String content) {

		File file = new File(path);
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
			fw.write(content);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void cleanDir(String folderPath) {
		// check if it is a folder
		File file = new File(folderPath);

		if (file.isDirectory()) {
			// clean the directory
			try {
				// remove files in the folder
				delAllFile(folderPath);
				
				// if want to remove the empty directory
				// String filePath = folderPath;
				// filePath = filePath.toString();
				// java.io.File myFilePath = new java.io.File(filePath);
				// myFilePath.delete(); 
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	// remove all files in a directory
		public static boolean delAllFile(String path) {
			boolean flag = false;
			File file = new File(path);
			if (!file.exists()) {
				return flag;
			}
			if (!file.isDirectory()) {
				return flag;
			}
			String[] tempList = file.list();
			File temp = null;
			for (int i = 0; i < tempList.length; i++) {
				if (path.endsWith(File.separator)) {
					temp = new File(path + tempList[i]);
				} else {
					temp = new File(path + File.separator + tempList[i]);
				}
				if (temp.isFile()) {
					temp.delete();
				}
				if (temp.isDirectory()) {
					delAllFile(path + "/" + tempList[i]);
					// delFolder(path + "/" + tempList[i]);
					flag = true;
				}
			}
			return flag;
		}
	

	public static void recordExamResult(String stuStr, String scriptURL,
			JSONObject scoreObject) {
		try {
			// fetch the folder
			String number = EncryptionUtil.decryptDES(stuStr);
			String[] stuStrParts = number.split("_");
			String folderURL = Constants.DOWNLOAD_PATH + stuStrParts[0] + "/"
					+ stuStrParts[1] + "/Test" + System.currentTimeMillis()
					+ "/";
			FileUtil.createFolder(folderURL);

			// get script
			int bytesum = 0;
			int byteread = 0;
			String newPath = folderURL + "script.jmx";
			File oldfile = new File(scriptURL);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(scriptURL); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					// System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}

			// get score.txt
			String scoreURL = folderURL + "score.txt";
			File scoreFile = new File(scoreURL);
			FileWriter writer = new FileWriter(scoreFile);
			writer.write(scoreObject.toString());
			writer.close();
			
			// zip into resultPath
			String resultPath = Constants.DOWNLOAD_PATH + stuStrParts[0] + "/"
					+ stuStrParts[1] + "/results/";
			FileUtil.createFolder(resultPath);
			CompressFileUtil.zip(folderURL, resultPath+"result"+System.currentTimeMillis()+".zip");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
