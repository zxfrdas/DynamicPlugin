package com.konka.dynamicplugin.host.app;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.IBinder;

import com.konka.dynamicplugin.core.IPluginManager;
import com.konka.dynamicplugin.host.IHost;

/**
 * 宿主应用中启动需使用插件视图并依赖于Service的控件的Service请继承此类
 */
public abstract class HostService extends Service implements IHost {

	@Override
	public void onCreate() {
		super.onCreate();
		getPluginManager().setResource(super.getClassLoader(), super.getAssets(),
				super.getResources(), super.getTheme());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public final Resources getResources() {
		Resources resources = getPluginManager().getResources();
		return (null != resources) ? resources : super.getResources();
	}

	@Override
	public final AssetManager getAssets() {
		AssetManager assetManager = getPluginManager().getAssets();
		return (null != assetManager) ? assetManager : super.getAssets();
	}

	@Override
	public final Theme getTheme() {
		Theme theme = getPluginManager().getTheme();
		return (null != theme) ? theme : super.getTheme();
	}

	@Override
	public final ClassLoader getClassLoader() {
		ClassLoader classLoader = getPluginManager().getClassLoader();
		return (null != classLoader) ? classLoader : super.getClassLoader();
	}

	@Override
	public final IPluginManager getPluginManager() {
		return ((IHost) getApplication()).getPluginManager();
	}

	@Override
	public final Context getHostContext() {
		return this;
	}

}
