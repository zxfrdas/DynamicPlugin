package com.konka.dynamicplugin.core.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.konka.dynamicplugin.core.PluginInfo;
import com.konka.dynamicplugin.core.auto.PluginInfoProxy;
import com.konka.dynamicplugin.core.tools.MD5FileUtil;
import com.zt.simpledao.condition.Condition;
import com.zt.simpledao.dao.IDAO;

public class LocalPluginChecker {
	private static final String TAG = PluginManager.class.getSimpleName();
	private static final String SYSTEM_PLUGIN_PATH = "/data/misc/konka/plugin_apk";
	private static final long GAP = 10 * 1000; // 10s
	private File mLocalPluginPath;
	private IDAO<PluginInfo> mPluginDB;

	private static final class InstanceHolder {
		private static final LocalPluginChecker sInstance = new LocalPluginChecker();
	}

	public static LocalPluginChecker getInstance() {
		return InstanceHolder.sInstance;
	}

	private LocalPluginChecker() {
	}

	public void initChecker(Context context, IDAO<PluginInfo> database) {
		mLocalPluginPath = checkStoragePathExist(context);
		Log.d(TAG, "mLocalPluginPath = " + mLocalPluginPath);
		mPluginDB = database;
	}

	private File checkStoragePathExist(Context context) {
		File systemPluginPath = new File(SYSTEM_PLUGIN_PATH);
		if (!systemPluginPath.exists()) {
			systemPluginPath.mkdir();
			systemPluginPath.setReadable(true, false);
			systemPluginPath.setWritable(true, false);
		}
		return systemPluginPath;
	}
	
	public boolean isRecordEmpty() {
		return (0 == mPluginDB.getCount());
	}

	public File[] getLocalExistPlugins() {
		Log.d(TAG, "getLocalExistPlugins, mLocalPluginPath = " + mLocalPluginPath);
		Log.d(TAG, "getLocalExistPlugins, files = " + mLocalPluginPath.listFiles());
		return mLocalPluginPath.listFiles();
	}

	public boolean isNeedSync(Context context) {
		final long lastModify = getFileLastModified(mLocalPluginPath);
		final long recLastModify = getRecordedLastModify(context);
		return (lastModify - recLastModify >= GAP);
	}
	
	private long getFileLastModified(File f) {
		if (f.isFile()) {
			return f.lastModified();
		} else {
			// 比较目录下所有文件和目录自身的修改时间，找出最晚的为准。
			long last = 0;
			File[] files = f.listFiles();
			for (File file : files) {
				last = (file.lastModified() > last) ? file.lastModified() : last;
			}
			last = (f.lastModified() > last) ? f.lastModified() : last;
			return last;
		}
	}

	private long getRecordedLastModify(Context context) {
		SharedPreferences sp = context.getSharedPreferences("modifyrecorder",
				Context.MODE_PRIVATE);
		return sp.getLong("lastModify", 0);
	}

	private void updateRecordedLastModify(Context context, long lastModified) {
		SharedPreferences sp = context.getSharedPreferences("modifyrecorder",
				Context.MODE_PRIVATE);
		sp.edit().putLong("lastModify", lastModified).apply();
	}

	public void syncExistPluginToRecorded(Context context,
			List<PluginInfo> existPlugins, List<PluginInfo> recordedPlugins) {
		updateRecordedLastModify(context, getFileLastModified(mLocalPluginPath));
		final List<PluginInfo> recordedAndExist = findIntersection(recordedPlugins,
				existPlugins);
		final int intersectionCount = recordedAndExist.size();
		if (0 == intersectionCount) {
			// 本地数据库记录和实际APK文件无交集，完全不同。清空数据库重新插入
			clearRecord();
			addRecord(context, existPlugins);
		} else {
			final int recordCount = recordedPlugins.size();
			final int existCount = existPlugins.size();
			if (intersectionCount < recordCount && intersectionCount == existCount) {
				Log.d(TAG, "exist = intersection < record");
				if (recordedPlugins.removeAll(recordedAndExist)) {
					// 删除有记录但不存在APK的数据库条目
					removeRecord(context, recordedPlugins);
				}
			} else if (intersectionCount < existCount
					&& intersectionCount == recordCount) {
				Log.d(TAG, "record = intersection < exist");
				if (existPlugins.removeAll(recordedAndExist)) {
					// 增加存在APK但没有数据库条目的
					addRecord(context, existPlugins);
				}
			} else if (intersectionCount < existCount
					&& intersectionCount < recordCount) {
				Log.d(TAG, "intersection < exist != record");
				if (recordedPlugins.removeAll(recordedAndExist)) {
					removeRecord(context, recordedPlugins);
				}
				if (existPlugins.removeAll(recordedAndExist)) {
					addRecord(context, existPlugins);
				}
			}
			Log.d(TAG, "do with intersection");
			// 处理交集部分。逐一比较MD5值判断是否有更改。
			List<PluginInfo> changed = checkChangedPlugins(context, recordedAndExist);
			if (!changed.isEmpty()) {
				// 更新有更改的插件APK数据库记录
				updateRecord(context, changed);
			}
		}
	}

	private List<PluginInfo> findIntersection(List<PluginInfo> whereToFind,
			List<PluginInfo> useToFind) {
		List<PluginInfo> result = new ArrayList<PluginInfo>();
		if (null == whereToFind || null == useToFind) {
			return result;
		}
		for (PluginInfo f : whereToFind) {
			final String apkPath = f.getApkPath();
			for (PluginInfo info : useToFind) {
				if (apkPath.equals(info.getApkPath())) {
					result.add(info);
					break;
				}
			}
		}
		return result;
	}

	private void clearRecord() {
		// delete all recorded and uninstall, insert new
		List<PluginInfo> recorded = mPluginDB.queryAll();
		for (PluginInfo info : recorded) {
			Log.d(TAG, "delete outdate recorded plugin = " + info.getApkPath());
			new File(info.getDexPath()).delete();
		}
		mPluginDB.deleteAll();
	}

	private void addRecord(Context context, List<PluginInfo> plugins) {
		Log.d(TAG, "addRecord");
		for (PluginInfo info : plugins) {
			Log.d(TAG, "addRecord, plugin = " + info.getApkPath());
		}
		mPluginDB.insert(plugins);
		updateMD5(context, plugins);
	}

	public void addRecord(Context context, PluginInfo plugin) {
		Log.d(TAG, "addRecord, plugin = " + plugin.getApkPath());
		mPluginDB.insert(plugin);
		updateMD5(context, plugin);
		updateRecordedLastModify(context, getFileLastModified(mLocalPluginPath));
	}

	private void updateRecord(Context context, List<PluginInfo> plugins) {
		Log.d(TAG, "updateRecorded");
		Map<PluginInfo, Condition> updates = new HashMap<PluginInfo, Condition>();
		for (PluginInfo info : plugins) {
			Condition condition = Condition.build()
					.where(PluginInfoProxy.apkPath).equal(info.getApkPath())
					.buildDone();
			updates.put(info, condition);
			Log.d(TAG, "updateRecorded, condition = " + condition);
		}
		mPluginDB.update(updates);
		updateMD5(context, plugins);
	}

	public void updateRecord(Context context, PluginInfo plugin) {
		Condition condition = Condition.build()
				.where(PluginInfoProxy.apkPath).equal(plugin.getApkPath())
				.buildDone();
		Log.d(TAG, "updateRecorded, condition = " + condition);
		mPluginDB.update(plugin, condition);
		updateMD5(context, plugin);
		updateRecordedLastModify(context, getFileLastModified(mLocalPluginPath));
	}

	private void updateMD5(Context context, List<PluginInfo> plugins) {
		SharedPreferences sp = context.getSharedPreferences("modifyrecorder",
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		for (PluginInfo plugin : plugins) {
			editor.putString(plugin.getApkPath(),
					MD5FileUtil.getFileMD5String(new File(plugin.getApkPath())));
		}
		editor.apply();
	}

	private void updateMD5(Context context, PluginInfo plugin) {
		SharedPreferences sp = context.getSharedPreferences("modifyrecorder",
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(plugin.getApkPath(),
				MD5FileUtil.getFileMD5String(new File(plugin.getApkPath())));
		editor.apply();
	}

	private void removeRecord(Context context, List<PluginInfo> plugins) {
		Log.d(TAG, "removeRecord");
		List<Condition> conditions = new ArrayList<Condition>();
		for (PluginInfo info : plugins) {
			Condition c = Condition.build().where(PluginInfoProxy.apkPath)
					.equal(info.getApkPath()).buildDone();
			conditions.add(c);
			Log.d(TAG, "removeRecord, condition = " + c);
		}
		mPluginDB.delete(conditions);
		removeMD5(context, plugins);
	}

	private void removeMD5(Context context, List<PluginInfo> plugins) {
		SharedPreferences sp = context.getSharedPreferences("modifyrecorder",
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		for (PluginInfo plugin : plugins) {
			editor.remove(plugin.getApkPath());
		}
		editor.apply();
	}

	private List<PluginInfo> checkChangedPlugins(Context context,
			List<PluginInfo> plugins) {
		List<PluginInfo> changed = new ArrayList<PluginInfo>();
		SharedPreferences sp = context.getSharedPreferences("modifyrecorder",
				Context.MODE_PRIVATE);
		for (PluginInfo plugin : plugins) {
			final String recordMD5 = sp.getString(plugin.getApkPath(), "");
			final String nowMD5 = MD5FileUtil.getFileMD5String(new File(plugin
					.getApkPath()));
			Log.d(TAG, "plugin = " + plugin.getApkPath());
			Log.d(TAG, "record MD5 = " + recordMD5);
			Log.d(TAG, "now MD5 = " + nowMD5);
			if (nowMD5.equals(recordMD5)) {
				Log.d(TAG, "MD5 same");
			} else {
				Log.d(TAG, "MD5 not same");
				changed.add(plugin);
			}
		}
		return changed;
	}

}
