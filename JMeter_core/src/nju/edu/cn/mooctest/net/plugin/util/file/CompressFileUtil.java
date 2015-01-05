package nju.edu.cn.mooctest.net.plugin.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

import de.innosystec.unrar.Archive;
import de.innosystec.unrar.exception.RarException;
import de.innosystec.unrar.rarfile.FileHeader;

public class CompressFileUtil {
	private static final int BUFFEREDSIZE = 1024;

	/**
	 * ѹ���ļ������ļ�Ŀ¼��ָ����zip����rar��
	 * 
	 * @param inputFilename
	 *            Ҫѹ�����ļ������ļ��У�������ļ��еĻ����Ὣ�ļ����µ������ļ��������ļ��е����ݽ���ѹ��
	 * @param zipFilename
	 *            ���ɵ�zip����rar�ļ�������
	 */
	public synchronized static void zip(String inputFilename, String zipFilename)
			throws IOException {
		zip(new File(inputFilename), zipFilename);
	}

	/**
	 * ѹ���ļ������ļ�Ŀ¼��ָ����zip����rar�����ڲ�����
	 * 
	 * @param inputFile
	 *            ����Ϊ�ļ����͵�Ҫѹ�����ļ������ļ���
	 * @param zipFilename
	 *            ���ɵ�zip����rar�ļ�������
	 * @return void
	 */
	private synchronized static void zip(File inputFile, String zipFilename)
			throws IOException {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
				zipFilename));

		try {
			zip(inputFile, out, "");
		} catch (IOException e) {
			throw e;
		} finally {
			out.close();
		}
	}

	/**
	 * ѹ���ļ������ļ�Ŀ¼��ָ����zip����rar��
	 * 
	 * @param inputFile
	 *            ����Ϊ�ļ����͵�Ҫѹ�����ļ������ļ���
	 * @param out
	 *            �����
	 * @param base
	 *            ���ļ���
	 * @return void
	 */
	private synchronized static void zip(File inputFile, ZipOutputStream out,
			String base) throws IOException {
		if (inputFile.isDirectory()) {
			File[] inputFiles = inputFile.listFiles();
			out.putNextEntry(new ZipEntry(base + "/"));
			base = base.length() == 0 ? "" : base + "/";
			for (int i = 0; i < inputFiles.length; i++) {
				zip(inputFiles[i], out, base + inputFiles[i].getName());
			}

		} else {
			if (base.length() > 0) {
				out.putNextEntry(new ZipEntry(base));
			} else {
				out.putNextEntry(new ZipEntry(inputFile.getName()));
			}

			FileInputStream in = new FileInputStream(inputFile);
			try {
				int c;
				byte[] by = new byte[BUFFEREDSIZE];
				while ((c = in.read(by)) != -1) {
					out.write(by, 0, c);
				}
			} catch (IOException e) {
				throw e;
			} finally {
				in.close();
			}
		}
	}

	/**
	 * ��ѹzip����rar�������ݵ�ָ����Ŀ¼�£����Դ������ļ����°������ļ��е����
	 * 
	 * @param zipFilename
	 *            Ҫ��ѹ��zip����rar���ļ�
	 * @param outputDirectory
	 *            ��ѹ���ŵ�Ŀ¼
	 */
	public synchronized static void unzip(String zipFilename,
			String outputDirectory) throws IOException {
		File outFile = new File(outputDirectory);
		if (!outFile.exists()) {
			outFile.mkdirs();
		}

		ZipFile zipFile = new ZipFile(zipFilename);
		Enumeration<?> en = zipFile.getEntries();
		ZipEntry zipEntry = null;
		while (en.hasMoreElements()) {
			zipEntry = (ZipEntry) en.nextElement();
			if (zipEntry.isDirectory()) {
				// mkdir directory
				String dirName = zipEntry.getName();
				// System.out.println("=dirName is:=" + dirName + "=end=");
				dirName = dirName.substring(0, dirName.length() - 1);
				File f = new File(outFile.getPath() + File.separator + dirName);
				f.mkdirs();
			} else {
				// unzip file
				String strFilePath = outFile.getPath() + File.separator
						+ zipEntry.getName();
				File f = new File(strFilePath);

				// the codes remedified by can_do on 2010-07-02 =begin=
				// /////begin/////
				// �ж��ļ������ڵĻ����ʹ������ļ������ļ��е�Ŀ¼
				if (!f.exists()) {
					String[] arrFolderName = zipEntry.getName().split("/");
					String strRealFolder = "";
					for (int i = 0; i < (arrFolderName.length - 1); i++) {
						strRealFolder += arrFolderName[i] + File.separator;
					}
					strRealFolder = outFile.getPath() + File.separator
							+ strRealFolder;
					File tempDir = new File(strRealFolder);
					// �˴�ʹ��.mkdirs()��������������.mkdir()
					tempDir.mkdirs();
				}
				// /////end///
				// the codes remedified by can_do on 2010-07-02 =end=
				f.createNewFile();
				InputStream in = zipFile.getInputStream(zipEntry);
				FileOutputStream out = new FileOutputStream(f);
				try {
					int c;
					byte[] by = new byte[BUFFEREDSIZE];
					while ((c = in.read(by)) != -1) {
						out.write(by, 0, c);
					}
					// out.flush();
				} catch (IOException e) {
					throw e;
				} finally {
					out.close();
					in.close();
				}
			}
		}
	}

	/**
	 * ��ѹrar��ʽ��ѹ���ļ���ָ��Ŀ¼��
	 * 
	 * @param rarFileName
	 *            ѹ���ļ�
	 * @param extPlace
	 *            ��ѹĿ¼
	 * @throws Exception
	 */
	public static boolean unrar(String rarFileName, String extPlace) {
		if (!extPlace.endsWith("/") && (!extPlace.endsWith("\\"))) {
			extPlace = extPlace + "/";
		}
		boolean flag = false;
		Archive archive = null;
		File out = null;
		File file = null;
		File dir = null;
		FileOutputStream os = null;
		FileHeader fh = null;
		String path, dirPath = "";
		try {
			file = new File(rarFileName);
			archive = new Archive(file);
		} catch (RarException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (file != null) {
				file = null;
			}
		}
		if (archive != null) {
			try {
				fh = archive.nextFileHeader();
				while (fh != null) {
					path = (extPlace + fh.getFileNameString().trim())
							.replaceAll("\\\\", "/");
					int end = path.lastIndexOf("/");
					if (end != -1) {
						dirPath = path.substring(0, end);
					}
					try {
						dir = new File(dirPath);
						if (!dir.exists()) {
							dir.mkdirs();
						}
					} catch (RuntimeException e1) {
						e1.printStackTrace();
					} finally {
						if (dir != null) {
							dir = null;
						}
					}
					if (fh.isDirectory()) {
						fh = archive.nextFileHeader();
						continue;
					}
					out = new File(extPlace + fh.getFileNameString().trim());
					try {
						os = new FileOutputStream(out);
						archive.extractFile(fh, os);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (RarException e) {
						e.printStackTrace();
					} finally {
						if (os != null) {
							try {
								os.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						if (out != null) {
							out = null;
						}
					}
					fh = archive.nextFileHeader();
				}
			} catch (RuntimeException e) {
				e.printStackTrace();
			} finally {
				fh = null;
				if (archive != null) {
					try {
						archive.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			flag = true;
		}
		return flag;
	}

}
