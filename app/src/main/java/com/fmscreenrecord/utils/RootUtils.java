package com.fmscreenrecord.utils;

import android.util.Log;

import com.stericson.RootTools.RootTools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA. User: Ryan Date: 13-3-9 Time: 下午10:14
 */

public class RootUtils {
	private static String LOG_TAG = RootUtils.class.getName();

	boolean isDeviceRooted() {
		if (checkRootMethod1()) {
			return true;
		}
		if (checkRootMethod2()) {
			return true;
		}
		if (checkRootMethod3()) {
			return true;
		}
		if (RootTools.isRootAvailable()) {
			return true;
		}
		return false;
	}

	boolean checkRootMethod1() {
		String buildTags = android.os.Build.TAGS;

		if (buildTags != null && buildTags.contains("test-keys")) {
			return true;
		}
		return false;
	}

	boolean checkRootMethod2() {
		try {
			File file = new File("/system/app/Superuser.apk");
			if (file.exists()) {
				return true;
			}
		} catch (Exception e) {
		}

		return false;
	}

	boolean checkRootMethod3() {
		if (new ExecShell().executeCommand(SHELL_CMD.check_su_binary) != null) {
			return true;
		} else {
			return false;
		}
	}

	public static enum SHELL_CMD {
		check_su_binary(new String[] { "/system/xbin/which", "su" }), ;

		String[] command;

		SHELL_CMD(String[] command) {
			this.command = command;
		}
	}

	class ExecShell {

		public ArrayList<String> executeCommand(SHELL_CMD shellCmd) {
			String line = null;
			ArrayList<String> fullResponse = new ArrayList<String>();
			Process localProcess = null;

			try {
				localProcess = Runtime.getRuntime().exec(shellCmd.command);
			} catch (Exception e) {
				return null;
				// e.printStackTrace();
			}

			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					localProcess.getOutputStream()));
			BufferedReader in = new BufferedReader(new InputStreamReader(
					localProcess.getInputStream()));

			try {
				while ((line = in.readLine()) != null) {
					Log.d(LOG_TAG, "--> Line received: " + line);
					fullResponse.add(line);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			Log.d(LOG_TAG, "--> Full response was: " + fullResponse);

			return fullResponse;
		}

	}

	/*** 尝试获取权限,成功返回true，否则返回false **/
	public static boolean appRoot1() {
//		char[] arrayOfChar = new char[1024];
//		try {
//			int j = new InputStreamReader(Runtime.getRuntime().exec("su -c ls")
//					.getErrorStream()).read(arrayOfChar);
//			if (j == -1) {
//				return true;
//			}
//		} catch (IOException ignored) {
//		}
//		return false;
		// 检测是否ROOT过
		DataInputStream stream;
		boolean flag = false;
		try {
			stream = Terminal("ls /data/");
			// 目录哪都行，不一定要需要ROOT权限的
			if (stream.readLine() != null)
				flag = true;
			// 根据是否有返回来判断是否有root权限
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

		}

		return flag;
	}
    public static DataInputStream Terminal(String command) throws Exception  
    {  
        Process process = Runtime.getRuntime().exec("su");  
        //执行到这，Superuser会跳出来，选择是否允许获取最高权限  
        OutputStream outstream = process.getOutputStream();  
        DataOutputStream DOPS = new DataOutputStream(outstream);  
        InputStream instream = process.getInputStream();  
        DataInputStream DIPS = new DataInputStream(instream);  
        String temp = command + "\n";  
        //加回车  
        DOPS.writeBytes(temp);  
        //执行  
        DOPS.flush();  
        //刷新，确保都发送到outputstream  
        DOPS.writeBytes("exit\n");  
        //退出  
        DOPS.flush();  
        process.waitFor();  
        return DIPS;  
    } 
	static boolean appRoot() {
		File file = new File("/system/bin/rec_s_c_temp");
		InputStream is = null;
		OutputStream os = null;
		try {
			boolean succ = RootTools.remount("/system/bin", "rw");
			if (succ) {
				Runtime runtime = Runtime.getRuntime();
				Process process = runtime.exec("su");
				os = process.getOutputStream();
				DataOutputStream dos = new DataOutputStream(os);
				boolean exist = file.exists();
				String command;
				if (exist) {
					command = "rm " + file.getAbsolutePath();
				} else {
					command = "touch " + file.getAbsolutePath();
				}
				dos.writeBytes(command + "\n");
				dos.writeBytes("exit\n");
				dos.flush();
				process.waitFor();
				dos.close();
				if (exist) {
					return !file.exists();
				} else {
					return file.exists();
				}
			}
			return false;
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(os);
		}
	}
}
