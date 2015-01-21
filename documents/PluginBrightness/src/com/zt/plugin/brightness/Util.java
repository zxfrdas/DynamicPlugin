package com.zt.plugin.brightness;

import iapp.eric.utils.base.Trace;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


import android.R.integer;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.XmlResourceParser;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Xml;
import android.view.KeyEvent;


public class Util {
	private static final long DAYTIME = 86400000L;
	private static Map<String, Integer> map = new HashMap<String, Integer>();



	// 结束进程
	public static void endProcess(Context context, String packageName) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		am.killBackgroundProcesses(packageName);
	}

	// 获取文件大小
	public static float getFileSize(String sourceDir) {
		// TODO Auto-generated method stub
		File file = new File(sourceDir);
		if (file.exists()) {
			long tmp = file.length();
			return tmp;
		}
		return -1;
	}

	public static void deleteData(File file) {
		File flist[] = file.listFiles();
		if (flist != null) {
			for (int i = 0; i < flist.length; i++) {
				if (flist[i].isDirectory()) {
					deleteData(flist[i]);
				} else {
					flist[i].delete();
					Trace.Debug(flist[i].getName() + " delete");
				}
			}
		}
	}

	public static long getFileSize(File file) throws Exception {
		long size = 0;
		if (file.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(file);
			size = fis.available();
		} else {
			return 0;
		}
		return size;
	}

	public static int getFileCount(File f) throws Exception {
		File flist[] = f.listFiles();
		return flist.length;
	}

	public static long getFileSizes(File f) throws Exception {
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			Trace.Debug(flist[i].getName());
			if (flist[i].isDirectory()) {
				size = size + getFileSizes(flist[i]);
			} else {
				size = size + getFileSize(flist[i]);
			}
		}
		return size;
	}

	public static String formetFileSize(long fileS) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS == 0) {
			fileSizeString = "0";
		} else if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	// 动态加载数据
	public static String format(int formatString, String tmpString, Context c) {
		String format = c.getString(formatString);
		return format.format(format, tmpString);
	}

	public static String format(int formatString, String tmpString1,
			String tmpString2, Context c) {
		String format = c.getString(formatString);
		return format.format(format, tmpString1, tmpString2);

	}

	public static long getDuration(String strLast, String strNow) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date last = null;
		try {
			last = df.parse(strLast);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Date now = null;
		try {
			now = df.parse(strNow);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final long duration = now.getTime() - last.getTime();
		return duration;
	}

	public static long getDurationDay(String last, String now) {
		long duration = getDuration(last, now);
		long day = duration / (60 * 60 * 24 * 1000);
		return day;
	}

	// 获取系统日期
	public static String getNowStr() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date now = new Date();
		return df.format(now);
	}

	public static Instrumentation mInstrumentation = new Instrumentation();
	private static boolean injectThread = false;

	public static boolean runRootCommand(String command) {
		// command = "echo test";
		Process process = null;
		DataOutputStream os = null;
		try {
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());

			Trace.Debug("commandss = " + command);

			os.writeBytes(command + "\n");
			os.writeBytes("exit\n");
			Trace.Debug("command2 = " + command);
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {
			}
		}
		return true;
	}

	public static String exec(final String cmd) {

		String s = null;
//		new Thread() {
//			public void run() {
				try {
					Process process;
					process = Runtime.getRuntime().exec(cmd);
					Trace.Debug("hello ===========cmd:" + cmd);
					BufferedReader buff = new BufferedReader(
							new InputStreamReader(process.getInputStream()));
					process.waitFor();
					s = buff.readLine();
					Trace.Debug("s:" + s);
					buff.close();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
//			};
//		}.start();

		return s;
	}

	public static void injectVirtualKey(final int key) {
		new Thread() {
			public void run() {
				synchronized (mInstrumentation) {
					Trace.Debug("into synchronize!");
					injectThread = true;
					mInstrumentation.sendKeySync(new KeyEvent(
							KeyEvent.ACTION_DOWN, key));
					mInstrumentation.sendKeySync(new KeyEvent(
							KeyEvent.ACTION_UP, key));
					Trace.Debug("out synchronize!");
					injectThread = false;
				}
			};
		}.start();

	}

	static boolean injectStat() {
		return injectThread;
	}



	public static int contains2(List<String> list, String pkg) {
		if (list == null/* || list.size() == 0*/)
			return -2;
		int size = list.size();
		for (int i = size - 1; i >= 0; i--) {
			String data2 = list.get(i);
			if (data2.equals(pkg)) {
//				Trace.Info("###yby package = " + pkg);
				return i;
			}
		}
		return -1;
	}

	public static int containsAndRemove(List<String> list, String pkg) {
		if (list == null/* || list.size() == 0*/)
			return -2;
		int size = list.size();
		for (int i = size - 1; i >= 0; i--) {
			String data2 = list.get(i);
			if (data2.equals(pkg)) {
				Trace.Info("###yby package = " + pkg);
				list.remove(data2);
				return i;
			}
		}
		return -1;
	}

	@SuppressLint("SimpleDateFormat")
	public static String DateDistance(Date msgTime, long currentTime) {
		long time = msgTime.getTime();
		long between = (currentTime - time) / 1000;
		if (between < 0) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			return sdf.format(msgTime);
		} else if (between < 60)
			return between + "秒前";
		else if (between < 60 * 60)
			return between / 60 + "分钟前";
		else if (between < 60 * 60 * 24)
			return between / 3600 + "小时前";
		else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			return sdf.format(msgTime);
		}
	}

	/**
	 * @brief 将毫秒数格式化成00:00的时间格式
	 * @author Eric
	 * @param ms
	 *            数值，单位毫秒
	 * @return 形如08:08的字符串
	 */
	public static String formatMusicDuration(int ms) {
		long s = ms / 1000;

		if (s <= 0) {
			return ("00:00");
		}

		long min = s / 60 % 60;
		long sec = s % 60;

		return (String.format("%02d:%02d", min, sec));
	}



	/**
	 * @brief 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 * @param context
	 *            上下文
	 * @param dpValue
	 *            设备独立像素dip
	 * @return 像素值
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		// final float scale = 1f;
		// Trace.Info("缩放比例-->"+scale);
		return (int) (dpValue * scale + 0.5f);
	}







	public static boolean isPkgInstalled(Context context, String packageName) {
		PackageInfo pkgInfo = null;
		try {
			pkgInfo = context.getPackageManager().getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (pkgInfo == null){
			Trace.Debug("###" + packageName + " Not installed");
			return false;
		} else {
			Trace.Debug("###" + packageName + " Installed");
			return true;
		}
	}



}
