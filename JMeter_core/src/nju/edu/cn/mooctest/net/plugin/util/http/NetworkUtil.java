package nju.edu.cn.mooctest.net.plugin.util.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;

public class NetworkUtil {
	/**
	 * ��ȡ��ǰ����ϵͳ����. return ����ϵͳ���� ����:windows xp,linux ��.
	 */
	public static String getOSName() {
		return System.getProperty("os.name").toLowerCase();
	}

	/**
	 * ��ȡunix������mac��ַ. ��windows��ϵͳĬ�ϵ��ñ�������ȡ. ���������ϵͳ����������µ�ȡmac��ַ����.
	 * 
	 * @return mac��ַ
	 */
	private static String getUnixMACAddress() {
		String mac = null;
		BufferedReader bufferedReader = null;
		Process process = null;
		try {
			// linux�µ����һ��ȡeth0��Ϊ����������
			process = Runtime.getRuntime().exec("ifconfig eth0");
			// ��ʾ��Ϣ�а�����mac��ַ��Ϣ
			bufferedReader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line = null;
			int index = -1;
			while ((line = bufferedReader.readLine()) != null) {
				// Ѱ�ұ�ʾ�ַ���[hwaddr]
				index = line.toLowerCase().indexOf("hwaddr");
				if (index >= 0) {// �ҵ���
					// ȡ��mac��ַ��ȥ��2�߿ո�
					mac = line.substring(index + "hwaddr".length() + 1).trim();
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			bufferedReader = null;
			process = null;
		}
		return mac;
	}

	/**
	 * ��ȡwidnows������mac��ַ.
	 * 
	 * @return mac��ַ
	 */
	private static String getWindowsMACAddress() {
		String mac = null;
		BufferedReader bufferedReader = null;
		Process process = null;
		try {
			// windows�µ������ʾ��Ϣ�а�����mac��ַ��Ϣ
			process = Runtime.getRuntime().exec("ipconfig /all");
			bufferedReader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line = null;
			int index = -1;
			while ((line = bufferedReader.readLine()) != null) {
				System.out.println(line);
				// Ѱ�ұ�ʾ�ַ���[physical
				index = line.toLowerCase().indexOf("physical address");

				if (index >= 0) {// �ҵ���
					index = line.indexOf(":");// Ѱ��":"��λ��
					if (index >= 0) {
						System.out.println(mac);
						// ȡ��mac��ַ��ȥ��2�߿ո�
						mac = line.substring(index + 1).trim();
					}
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			bufferedReader = null;
			process = null;
		}

		return mac;
	}

	/**
	 * windows 7 ר�� ��ȡMAC��ַ
	 * 
	 * @return
	 * @throws Exception
	 */
	private static String getWin7MACAddress() throws Exception {

		// ��ȡ����IP����
		InetAddress ia = InetAddress.getLocalHost();
		// �������ӿڶ��󣨼������������õ�mac��ַ��mac��ַ������һ��byte�����С�
		byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();

		// ��������ǰ�mac��ַƴװ��String
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < mac.length; i++) {
			if (i != 0) {
				sb.append("-");
			}
			// mac[i] & 0xFF ��Ϊ�˰�byteת��Ϊ������
			String s = Integer.toHexString(mac[i] & 0xFF);
			sb.append(s.length() == 1 ? 0 + s : s);
		}

		// ���ַ�������Сд��ĸ��Ϊ��д��Ϊ�����mac��ַ������
		return sb.toString().toUpperCase();
	}

	/**
	 * ��ȡMAC������mac��ַ.
	 * 
	 * @return mac��ַ
	 */
	public static String getMacOSMACAddress() {
		String mac = null;
		BufferedReader bufferedReader = null;
		Process process = null;
		try {
			/**
			 * Unix�µ����һ��ȡeth0��Ϊ���������� ��ʾ��Ϣ�а�����mac��ַ��Ϣ
			 */
			process = Runtime.getRuntime().exec("ifconfig");
			bufferedReader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			String line = null;
			int index = -1;
			while ((line = bufferedReader.readLine()) != null) {
				/**
				 * Ѱ�ұ�ʾ�ַ���[ether]
				 */
				index = line.toLowerCase().indexOf("ether");
				/**
				 * �ҵ���
				 */
				if (index != -1) {
					/**
					 * ȡ��mac��ַ��ȥ��2�߿ո�
					 */
					mac = line.substring(index + "ether".length() + 1).trim();
					break;
				}
				// System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			bufferedReader = null;
			process = null;
		}

		return mac;
	}

	/**
	 * �����õ�main����.
	 * 
	 * @param argc
	 *            ���в���.
	 * @throws Exception
	 */
	public static String getMACAddress() throws Exception {
		String os = getOSName();
		// System.out.println(os);
		if (os.equals("windows 7")) {
			String mac = getWin7MACAddress();
			return mac;
		} else if (os.startsWith("windows")) {
			// ������windows
			String mac = getWindowsMACAddress();
			return mac;
		} else if (os.startsWith("mac")) {
			// �����Ƿ�windowsϵͳ һ�����unix
			String mac = getMacOSMACAddress();
			return mac;
		} else {
			String mac = getUnixMACAddress();
			return mac;
		}
	}
}
