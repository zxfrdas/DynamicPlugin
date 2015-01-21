package com.konka.dynamicplugin.host;

import android.content.Context;

import com.konka.dynamicplugin.core.IPluginManager;

public interface IHost {
	/**
	 * 获取宿主中提供的插件管理类。
	 * 
	 * @return 插件管理类
	 * @see PluginManager
	 */
	IPluginManager getPluginManager();

	/**
	 * 获取宿主的上下文
	 * 
	 * @return 宿主上下文，而非Application上下文
	 */
	Context getHostContext();
}
