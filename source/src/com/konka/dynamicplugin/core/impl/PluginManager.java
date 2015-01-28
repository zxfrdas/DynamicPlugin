package com.konka.dynamicplugin.core.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import com.konka.dynamicplugin.core.IAsyncListener;
import com.konka.dynamicplugin.core.IPluginManager;
import com.konka.dynamicplugin.core.PluginInfo;
import com.konka.dynamicplugin.core.auto.PluginInfoDAO;
import com.konka.dynamicplugin.core.auto.PluginInfoProxy;
import com.konka.dynamicplugin.core.impl.PostToUI.Task;
import com.konka.dynamicplugin.core.impl.ResourceController.Dependence;
import com.konka.dynamicplugin.core.tools.DLUtils;
import com.konka.dynamicplugin.plugin.IPlugin;
import com.zt.simpledao.condition.Condition;
import com.zt.simpledao.dao.IDAO;

/**
 * 插件管理类。提供插件的安装/卸载/更新，启用/禁用，获取视图等操作方法。
 * 插件的基本生命周期为：未记录->未安装->已安装->未启用->已启动->被宿主获取并显示。
 * <p>
 * 未记录：指插件APK存在于插件目录下，没有添加进宿主的插件数据库。
 * <p>
 * 未安装：指插件APK存在于插件目录下，已经添加进插件数据库。
 * <p>
 * 已安装：指插件APK已经被导出了对应dex文件。
 * <p>
 * 未启动：指已安装但未标记为启用的插件。宿主UI不应获取其视图显示。
 * <p>
 * 已启动：指已安装并且标记为启用的插件。宿主UI应当获取其视图显示。
 * <p>
 * 一个标准的流程为：应用感知到新（或新版本）插件到来，用户选择安装、启用。 当下次宿主页面呈现出来时，此新插件提供的视图应该显示在宿主页面中。
 */
public final class PluginManager implements IPluginManager {
	private static final String TAG = PluginManager.class.getSimpleName();
	private static volatile PluginManager sInstance;
	private IDAO<PluginInfo> mPluginDB;
	private ResourceController mResController;
	private LocalPluginChecker mChecker;
	private ExecutorService mThreads;
	private PostToUI mPostToUI;
	
	public static PluginManager getInstance(Context context) {
		if (null == sInstance) {
			synchronized (PluginManager.class) {
				if (null == sInstance) {
					sInstance = new PluginManager(context);
				}
			}
		}
		return sInstance;
	}

	private PluginManager(Context context) {
		mChecker = LocalPluginChecker.getInstance();
		mPluginDB = PluginInfoDAO.getInstance(context);
		mThreads = Executors.newFixedThreadPool(2);
		mPostToUI = new PostToUI();
	}

	@Override
	public void setResource(ClassLoader loader, AssetManager asset, Resources res,
			Theme theme) {
		if (null == mResController) {
			mResController = new ResourceController();
		}
		mResController.setDependence(new Dependence(loader, asset, res, theme));
	}

	@Override
	public void initPlugins(Context context) throws FileNotFoundException {
		Log.d(TAG, "initPlugins");
		mChecker.initChecker(context, mPluginDB);
		final List<PluginInfo> existPlugins = parseAllExistPluginsInfo(context,
				mChecker.getLocalExistPlugins());
		if (mChecker.isRecordEmpty()) {
			Log.d(TAG, "initPlugins, record is empty");
			mChecker.syncExistPluginToRecorded(context, existPlugins, null);
		} else if (mChecker.isNeedSync(context)) {
			Log.d(TAG, "plugin dir has been modified");
			final List<PluginInfo> recordedPlugins = getAllRecordedPlugins();
			mChecker.syncExistPluginToRecorded(context, existPlugins,
					recordedPlugins);
		}
	}

	@Override
	public void asyncInitPlugins(final Context context, final IAsyncListener listener) {
		Log.d(TAG, "asyncInitPlugins");
		synchronized (mChecker) {
			mThreads.execute(new Runnable() {

				@Override
				public void run() {
					try {
						initPlugins(context);
						mPostToUI.post(listener,
								Task.success(new ArrayList<PluginInfo>()));
					} catch (FileNotFoundException e) {
						mPostToUI.post(listener, Task.fail(e.toString()));
					}
				}
			});
		}
	}

	private List<PluginInfo> parseAllExistPluginsInfo(Context context, File[] apks)
			throws FileNotFoundException {
		List<PluginInfo> pluginInfos = new ArrayList<PluginInfo>(apks.length);
		for (File apk : apks) {
			PluginInfo info = parsePluginInfo(context, apk);
			// add list
			pluginInfos.add(info);
		}
		return pluginInfos;
	}

	private PluginInfo parsePluginInfo(Context context, File apk)
			throws FileNotFoundException {
		PluginInfo info = new PluginInfo();
		// apk path
		final String apkPath = apk.getAbsolutePath();
		info.setApkPath(apkPath);
		// apk title
		final String title = DLUtils.getAppLabel(context, apkPath).toString();
		info.setTitle(title);
		// apk package name
		final String packageName = DLUtils.getAppPackageName(context, apkPath)
				.toString();
		info.setPackageName(packageName);
		// apk entry class
		final String pluginClassName = DLUtils.getAppDescription(context, apkPath)
				.toString();
		info.setEntryClass(pluginClassName);
		// apk dex path
		final String apkName = apkPath.substring(
				apkPath.lastIndexOf(File.separator) + 1, apkPath.lastIndexOf("."));
		String dexPath = context.getDir("dex", Context.MODE_PRIVATE)
				.getAbsolutePath();
		info.setDexPath(dexPath + File.separator + apkName + ".dex");
		// apk icon
		Drawable icon = DLUtils.getAppIcon(context, apkPath);
		info.setIcon(icon);
		// apk version
		int version = DLUtils.getAppVersion(context, apkPath);
		info.setVersion(version);
		return info;
	}

	@Override
	public List<PluginInfo> getAllRecordedPlugins() {
		Log.d(TAG, "getAllRecordedPlugins");
		return mPluginDB.queryAll();
	}

	@Override
	public boolean installPlugin(Context context, PluginInfo pluginInfo) {
		boolean installed = false;
		final String apkPath = pluginInfo.getApkPath();
		final String dexPath = pluginInfo.getDexPath();
		// query database
		List<PluginInfo> info = mPluginDB.query(whereApkPath(apkPath));
		if (!info.isEmpty() && info.get(0).isInstalled()) {
			// check for update
			PluginInfo recorded = info.get(0);
			final int recordAppVersion = recorded.getVersion();
			int currentAppVersion = pluginInfo.getVersion();
			if (currentAppVersion > recordAppVersion) {
				new File(pluginInfo.getDexPath()).delete();
				// 需要升级重新安装，不过依然记忆enable的状态
				pluginInfo.setEnabled(recorded.isEnabled());
				pluginInfo.setEnableIndex(recorded.getEnableIndex());
				mPluginDB.update(pluginInfo, whereApkPath(apkPath));
				installPlugin(context, pluginInfo);
			}
		} else {
			// install the apk, output dex
			mResController.installClassLoader(apkPath, dexPath);
			PluginInfo plugin = null;
			if (info.isEmpty()) {
				// new apk, preInstall it
				try {
					plugin = parsePluginInfo(context, new File(apkPath));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				plugin.setInstalled(true);
				mChecker.addRecord(context, plugin);
			} else {
				plugin = info.get(0);
				plugin.setInstalled(true);
				mChecker.updateRecord(context, plugin);
			}
			installed = true;
		}
		return installed;
	}

	private Condition whereApkPath(String apkPath) {
		return mPluginDB.buildCondition().where(PluginInfoProxy.apkPath)
				.equal(apkPath).buildDone();
	}

	@Override
	public void asyncInstallPlugin(final Context context,
			final PluginInfo pluginInfo, final IAsyncListener listener) {
		mThreads.execute(new Runnable() {

			@Override
			public void run() {
				boolean success = installPlugin(context, pluginInfo);
				if (success) {
					List<PluginInfo> info = mPluginDB.query(whereApkPath(pluginInfo
							.getApkPath()));
					mPostToUI.post(listener, Task.success(info));
				} else {
					mPostToUI.post(listener,
							Task.fail(pluginInfo.getTitle() + " already installed"));
				}
			}
		});
	}

	@Override
	public void asyncInstallPlugins(final Context context,
			final List<PluginInfo> pluginInfos, final IAsyncListener listener) {
		mThreads.execute(new Runnable() {

			@Override
			public void run() {
				boolean success = false;
				List<PluginInfo> result = new ArrayList<PluginInfo>();
				for (PluginInfo pluginInfo : pluginInfos) {
					success = installPlugin(context, pluginInfo);
					if (!success) {
						mPostToUI.post(
								listener,
								Task.fail(pluginInfo.getTitle()
										+ " already installed"));
						break;
					}
					List<PluginInfo> info = mPluginDB.query(whereApkPath(pluginInfo
							.getApkPath()));
					result.add(info.get(0));
				}
				if (success) {
					mPostToUI.post(listener, Task.success(result));
				}
			}
		});
	}

	@Override
	public boolean uninstallPlugin(Context context, PluginInfo pluginInfo) {
		boolean uninstalled = false;
		final String apkPath = pluginInfo.getApkPath();
		final String dexPath = pluginInfo.getDexPath();
		// query database
		List<PluginInfo> info = mPluginDB.query(whereApkPath(apkPath));
		if (!info.isEmpty() && info.get(0).isInstalled()) {
			// uninstall
			new File(dexPath).delete();
			mResController.uninstallClassLoader(apkPath);
			// reset info
			PluginInfo plugin = info.get(0);
			plugin.setInstalled(false);
			plugin.setEnabled(false);
			plugin.setEnableIndex(-1);
			plugin.setVersion(1);
			mPluginDB.update(plugin, whereApkPath(apkPath));
			uninstalled = true;
		}
		return uninstalled;
	}

	@Override
	public void asyncUninstallPlugin(final Context context,
			final PluginInfo pluginInfo, final IAsyncListener listener) {
		mThreads.execute(new Runnable() {

			@Override
			public void run() {
				boolean success = uninstallPlugin(context, pluginInfo);
				if (success) {
					List<PluginInfo> info = mPluginDB.query(whereApkPath(pluginInfo
							.getApkPath()));
					mPostToUI.post(listener, Task.success(info));
				} else {
					mPostToUI.post(
							listener,
							Task.fail(pluginInfo.getTitle()
									+ " has not been installed"));
				}
			}
		});
	}

	@Override
	public void asyncUninstallPlugins(final Context context,
			final List<PluginInfo> pluginInfos, final IAsyncListener listener) {
		mThreads.execute(new Runnable() {

			@Override
			public void run() {
				boolean success = false;
				List<PluginInfo> result = new ArrayList<PluginInfo>();
				for (PluginInfo pluginInfo : pluginInfos) {
					success = uninstallPlugin(context, pluginInfo);
					if (!success) {
						mPostToUI.post(
								listener,
								Task.fail(pluginInfo.getTitle()
										+ " has not been installed"));
						break;
					}
					List<PluginInfo> info = mPluginDB.query(whereApkPath(pluginInfo
							.getApkPath()));
					result.add(info.get(0));
				}
				if (success) {
					mPostToUI.post(listener, Task.success(result));
				}
			}
		});
	}

	@Override
	public List<PluginInfo> getInstalledPlugins() {
		Log.d(TAG, "getInstalledPlugins");
		return mPluginDB.query(mPluginDB.buildCondition()
				.where(PluginInfoProxy.installed).equal(true).buildDone());
	}

	@Override
	public List<PluginInfo> getEnablePlugins() {
		Log.d(TAG, "getEnablePlugins");
		List<PluginInfo> enablePlugins = mPluginDB.query(mPluginDB.buildCondition()
				.where(PluginInfoProxy.enabled).equal(true)
				.orderby(PluginInfoProxy.enableIndex).ascend()
				.buildDone());
		loadPluginsResource(enablePlugins);
		return enablePlugins;
	}

	@Override
	public boolean enablePlugin(PluginInfo plugin) {
		boolean enabled = false;
		final String apkPath = plugin.getApkPath();
		List<PluginInfo> infos = mPluginDB.query(whereApkPath(apkPath));
		if (!infos.isEmpty()) {
			PluginInfo info = infos.get(0);
			if (!info.isEnabled()) {
				info.setEnabled(true);
				// check for already enable
				List<PluginInfo> enabledInfos = getEnablePlugins();
				if (enabledInfos.isEmpty()) {
					info.setEnableIndex(0);
				} else {
					final int lastEnableIndex = enabledInfos.get(
							enabledInfos.size() - 1).getEnableIndex();
					info.setEnableIndex(lastEnableIndex + 1);
				}
				mPluginDB.update(info, whereApkPath(apkPath));
				mResController.loadPluginResource(info);
				enabled = true;
			}
		}
		return enabled;
	}

	@Override
	public void asyncEnablePlugin(final PluginInfo plugin,
			final IAsyncListener listener) {
		mThreads.execute(new Runnable() {

			@Override
			public void run() {
				boolean success = enablePlugin(plugin);
				if (success) {
					List<PluginInfo> info = mPluginDB.query(whereApkPath(plugin
							.getApkPath()));
					mPostToUI.post(listener, Task.success(info));
				} else {
					mPostToUI.post(listener,
							Task.fail(plugin.getTitle() + " has already enabled"));
				}
			}
		});
	}

	@Override
	public void asyncEnablePlugins(final List<PluginInfo> plugins,
			final IAsyncListener listener) {
		mThreads.execute(new Runnable() {

			@Override
			public void run() {
				boolean success = false;
				List<PluginInfo> result = new ArrayList<PluginInfo>();
				for (PluginInfo plugin : plugins) {
					success = enablePlugin(plugin);
					if (!success) {
						mPostToUI.post(listener, Task.fail(plugin.getTitle()
								+ " has already enabled"));
						break;
					}
					List<PluginInfo> info = mPluginDB.query(whereApkPath(plugin
							.getApkPath()));
					result.add(info.get(0));
				}
				if (success) {
					mPostToUI.post(listener, Task.success(result));
				}
			}
		});
	}

	@Override
	public boolean disablePlugin(PluginInfo plugin) {
		boolean disabled = false;
		final String apkPath = plugin.getApkPath();
		List<PluginInfo> infos = mPluginDB.query(whereApkPath(apkPath));
		if (!infos.isEmpty()) {
			PluginInfo info = infos.get(0);
			info.setEnabled(false);
			info.setEnableIndex(-1);
			mPluginDB.update(info, whereApkPath(apkPath));
			mResController.unloadPluginResource(info);
			disabled = true;
		}
		return disabled;
	}

	@Override
	public void asyncDisablePlugin(final PluginInfo plugin,
			final IAsyncListener listener) {
		mThreads.execute(new Runnable() {

			@Override
			public void run() {
				boolean success = disablePlugin(plugin);
				if (success) {
					List<PluginInfo> info = mPluginDB.query(whereApkPath(plugin
							.getApkPath()));
					mPostToUI.post(listener, Task.success(info));
				} else {
					mPostToUI.post(listener,
							Task.fail(plugin.getTitle() + " has not been enable"));
				}
			}
		});
	}

	@Override
	public void asyncDisablePlugins(final List<PluginInfo> plugins,
			final IAsyncListener listener) {
		mThreads.execute(new Runnable() {

			@Override
			public void run() {
				boolean success = false;
				List<PluginInfo> result = new ArrayList<PluginInfo>();
				for (PluginInfo plugin : plugins) {
					success = disablePlugin(plugin);
					if (!success) {
						mPostToUI.post(listener, Task.fail(plugin.getTitle()
								+ " has already enabled"));
						break;
					}
					List<PluginInfo> info = mPluginDB.query(whereApkPath(plugin
							.getApkPath()));
					result.add(info.get(0));
				}
				if (success) {
					mPostToUI.post(listener, Task.success(result));
				}
			}
		});
	}

	private void loadPluginsResource(List<PluginInfo> pluginInfos) {
		for (PluginInfo info : pluginInfos) {
			mResController.loadPluginResource(info);
		}
	}

	@Override
	public View getPluginView(Context context, PluginInfo pluginInfo) {
		mResController.holdPluginResource(pluginInfo);
		IPlugin plugin = launchPlugin(context, pluginInfo);
		View view = plugin.getPluginView();
		mResController.releasePluginResource(pluginInfo);
		return view;
	}

	private IPlugin launchPlugin(Context context, PluginInfo pluginInfo) {
		IPlugin plugin = null;
		try {
			Class<?> localClass = mResController.getClassLoader().loadClass(
					pluginInfo.getEntryClass());
			Object instance = localClass.newInstance();
			plugin = (IPlugin) instance;
			plugin.setContext(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return plugin;
	}

	@Override
	public AssetManager getAssets() {
		return (null != mResController) ? mResController.getAssets() : null;
	}

	@Override
	public Resources getResources() {
		return (null != mResController) ? mResController.getResources() : null;
	}

	@Override
	public Theme getTheme() {
		return (null != mResController) ? mResController.getTheme() : null;
	}

	@Override
	public ClassLoader getClassLoader() {
		return (null != mResController) ? mResController.getClassLoader() : null;
	}

}
