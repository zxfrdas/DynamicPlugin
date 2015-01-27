package com.konka.dynamicplugin.core.impl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;

import com.konka.dynamicplugin.core.PluginInfo;

/**
 * 控制插件/宿主的{@code AssetManager},{@code Resources},{@code Theme},
 * {@code ClassLoader}四个基本资源类动态切换
 * 
 * @see AssetManager
 * @see Resources
 * @see Theme
 * @see ClassLoader
 */
public final class ResourceController {
	private Dependence mDependence;
	private Map<String, AssetManager> mAssetMap;
	private Map<String, Resources> mResourcesMap;
	private Map<String, Theme> mThemeMap;
	private Map<String, ClassLoader> mClassLoaderMap;
	private Map<String, PluginInfo> mLoadedPluginMap;
	private String mCurrentPluginApk;

	public static final class Dependence {
		public ClassLoader mSuperClassLoader;
		public AssetManager mSuperAssetManager;
		public Resources mSuperResources;
		public Theme mSuperTheme;

		public Dependence(ClassLoader loader, AssetManager asset, Resources res,
				Theme theme) {
			mSuperClassLoader = loader;
			mSuperAssetManager = asset;
			mSuperResources = res;
			mSuperTheme = theme;
		}

	}

	public ResourceController() {
		mClassLoaderMap = new HashMap<String, ClassLoader>();
		mAssetMap = new HashMap<String, AssetManager>();
		mResourcesMap = new HashMap<String, Resources>();
		mThemeMap = new HashMap<String, Resources.Theme>();
		mLoadedPluginMap = new HashMap<String, PluginInfo>();
	}

	public void setDependence(Dependence dependence) {
		mDependence = dependence;
	}

	public void installClassLoader(String apkPath, String dexPath) {
		DLClassLoader.getClassLoader(apkPath, dexPath, getSuperClassLoader());
	}

	public void uninstallClassLoader(String apkPath) {
		mClassLoaderMap.remove(apkPath);
	}

	public void loadPluginResource(PluginInfo pluginInfo) {
		try {
			final String apkPath = pluginInfo.getApkPath();
			final String dexPath = pluginInfo.getDexPath();
			// classloader
			if (null == mClassLoaderMap.get(apkPath)) {
				ClassLoader cl = DLClassLoader.getClassLoader(apkPath, dexPath,
						getSuperClassLoader());
				mClassLoaderMap.put(apkPath, cl);
			}
			// asset
			AssetManager assetManager = AssetManager.class.newInstance();
			Method addAssetPath = assetManager.getClass().getMethod("addAssetPath",
					String.class);
			addAssetPath.invoke(assetManager, apkPath);
			mAssetMap.put(apkPath, assetManager);
			// resource
			Resources resources = new Resources(assetManager, getSuperResources()
					.getDisplayMetrics(), getSuperResources().getConfiguration());
			mResourcesMap.put(apkPath, resources);
			// theme
			Theme theme = resources.newTheme();
			theme.setTo(getSuperTheme());
			mThemeMap.put(apkPath, theme);
			// set loaded
			final String packageName = pluginInfo.getPackageName();
			mLoadedPluginMap.put(packageName, pluginInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void unloadPluginResource(PluginInfo pluginInfo) {
		final String apkPath = pluginInfo.getApkPath();
		final String packageName = pluginInfo.getPackageName();
		mAssetMap.remove(apkPath);
		mResourcesMap.remove(apkPath);
		mThemeMap.remove(apkPath);
		mLoadedPluginMap.remove(packageName);
	}

	public void holdPluginResource(PluginInfo pluginInfo) {
		mCurrentPluginApk = pluginInfo.getApkPath();
	}

	public void releasePluginResource(PluginInfo pluginInfo) {
		mCurrentPluginApk = "";
	}

	public ClassLoader getClassLoader() {
		ClassLoader classLoader = mClassLoaderMap.get(getCurrentCallerPlugin());
		if (null == classLoader) {
			classLoader = getSuperClassLoader();
		}
		return classLoader;
	}

	private ClassLoader getSuperClassLoader() {
		return mDependence.mSuperClassLoader;
	}

	public AssetManager getAssets() {
		AssetManager assetManager = mAssetMap.get(getCurrentCallerPlugin());
		if (null == assetManager) {
			assetManager = getSuperAssets();
		}
		return assetManager;
	}

	private AssetManager getSuperAssets() {
		return mDependence.mSuperAssetManager;
	}

	public Resources getResources() {
		Resources resources = mResourcesMap.get(getCurrentCallerPlugin());
		if (null == resources) {
			resources = getSuperResources();
		}
		return resources;
	}

	private Resources getSuperResources() {
		return mDependence.mSuperResources;
	}

	public Theme getTheme() {
		Theme theme = mThemeMap.get(getCurrentCallerPlugin());
		if (null == theme) {
			theme = getSuperTheme();
		}
		return theme;
	}

	private Theme getSuperTheme() {
		return mDependence.mSuperTheme;
	}

	private String getCurrentCallerPlugin() {
		final StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		final int size = elements.length;
		PluginInfo info = null;
		String mainClassName;
		for (int i = 0; i < size; i++) {
			String className = elements[i].getClassName();
			if (className.contains("$")) {
				// 如果是在内部类中调用，依然考察主类名。
				mainClassName = className.substring(0, className.lastIndexOf("$"));
			} else {
				mainClassName = className;
			}
			for (String name : mLoadedPluginMap.keySet()) {
				if (mainClassName.contains(name)) {
					info = mLoadedPluginMap.get(name);
					break;
				}
			}
			if (null != info) {
				break;
			}
		}
		return (null != info) ? info.getApkPath() : mCurrentPluginApk;
	}

}
