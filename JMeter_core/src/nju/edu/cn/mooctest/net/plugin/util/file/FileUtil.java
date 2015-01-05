package nju.edu.cn.mooctest.net.plugin.util.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

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

}
