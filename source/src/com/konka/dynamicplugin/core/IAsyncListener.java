package com.konka.dynamicplugin.core;

import java.util.List;

/**
 * 插件管理异步方法回调接口
 */
public interface IAsyncListener {
	/**
	 * 异步方法执行成功
	 * @param pluginInfos 被方法更改的插件的信息集合
	 */
	void success(List<PluginInfo> pluginInfos);
	/**
	 * 异步方法执行失败
	 * @param reason 失败原因
	 */
	void fail(String reason);
}
