package com.konka.dynamicplugin.core.tools;

import java.io.FileNotFoundException;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;

public class DLUtils {

	public static PackageInfo getPackageInfo(Context context, String apkFilepath)
			throws FileNotFoundException {
		PackageManager pm = context.getPackageManager();
		PackageInfo pkgInfo = null;
		try {
			pkgInfo = pm.getPackageArchiveInfo(apkFilepath,
					PackageManager.GET_ACTIVITIES | PackageManager.GET_PERMISSIONS);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pkgInfo;
	}

	public static Drawable getAppIcon(Context context, String apkFilepath)
			throws FileNotFoundException {
		PackageManager pm = context.getPackageManager();
		PackageInfo pkgInfo = getPackageInfo(context, apkFilepath);
		if (pkgInfo == null) {
			return null;
		}
		ApplicationInfo appInfo = addSourceDir(apkFilepath, pkgInfo);
		return pm.getApplicationIcon(appInfo);
	}

	public static CharSequence getAppLabel(Context context, String apkFilepath)
			throws FileNotFoundException {
		PackageManager pm = context.getPackageManager();
		PackageInfo pkgInfo = getPackageInfo(context, apkFilepath);
		if (pkgInfo == null) {
			return "";
		}
		ApplicationInfo appInfo = addSourceDir(apkFilepath, pkgInfo);
		return pm.getApplicationLabel(appInfo);
	}
	
	public static int getAppLabelID(Context context, String apkFilePath)
			throws FileNotFoundException {
		int id = 0;
		PackageInfo pkgInfo = getPackageInfo(context, apkFilePath);
		if (null != pkgInfo) {
			ApplicationInfo appInfo = addSourceDir(apkFilePath, pkgInfo);
			if (null != appInfo) {
				id = appInfo.labelRes;
			}
		}
		return id;
	}

	public static CharSequence getAppPackageName(Context context, String apkFilepath)
			throws FileNotFoundException {
		PackageInfo pkgInfo = getPackageInfo(context, apkFilepath);
		if (pkgInfo == null) {
			return "";
		}
		ApplicationInfo appInfo = addSourceDir(apkFilepath, pkgInfo);
		return appInfo.packageName;
	}

	public static CharSequence getAppDescription(Context context, String apkFilepath)
			throws FileNotFoundException {
		PackageManager pm = context.getPackageManager();
		PackageInfo pkgInfo = getPackageInfo(context, apkFilepath);
		if (pkgInfo == null) {
			return "";
		}
		ApplicationInfo appInfo = addSourceDir(apkFilepath, pkgInfo);
		return appInfo.loadDescription(pm);
	}

	// Workaround for http://code.google.com/p/android/issues/detail?id=9151
	private static ApplicationInfo addSourceDir(String apkFilepath,
			PackageInfo pkgInfo) {
		ApplicationInfo appInfo = pkgInfo.applicationInfo;
		if (Build.VERSION.SDK_INT >= 8) {
			appInfo.sourceDir = apkFilepath;
			appInfo.publicSourceDir = apkFilepath;
		}
		return appInfo;
	}
	
	public static int getAppVersion(Context context, String apkFilePath)
			throws FileNotFoundException {
		// PackageManager pm = context.getPackageManager();
		PackageInfo pkgInfo = getPackageInfo(context, apkFilePath);
		if (pkgInfo == null) {
			return 1;
		}
		// ApplicationInfo appInfo = addSourceDir(apkFilepath, pkgInfo);
		return pkgInfo.versionCode;
	}
	
	public static String[] getAppPermissions(Context context, String apkFilePath)
			throws FileNotFoundException {
		PackageInfo pkgInfo = getPackageInfo(context, apkFilePath);
		if (pkgInfo == null) {
			return new String[]{};
		}
		return pkgInfo.requestedPermissions;
	}
}
