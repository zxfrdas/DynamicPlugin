package com.konka.dynamicplugin.host.app;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.util.Log;

import com.konka.dynamicplugin.core.IPluginManager;
import com.konka.dynamicplugin.core.impl.PluginManager;
import com.konka.dynamicplugin.host.IHost;

/**
 * 宿主应用中需使用插件视图，或启动需使用插件视图并依赖于Activity的控件的Acitivity请继承此类
 */
public abstract class HostActivity extends Activity implements IHost {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getPluginManager().setResource(super.getClassLoader(), super.getAssets(),
				super.getResources(), super.getTheme());
	}

	@Override
	protected void onStart() {
		Log.d(PluginManager.class.getSimpleName(), "onStart");
		super.onStart();
	}

	@Override
	protected void onStop() {
		Log.d(PluginManager.class.getSimpleName(), "onStop");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.d(PluginManager.class.getSimpleName(), "onDestroy");
		super.onDestroy();
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
