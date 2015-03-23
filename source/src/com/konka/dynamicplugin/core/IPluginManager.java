package com.konka.dynamicplugin.core;

import java.io.FileNotFoundException;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.view.View;

public interface IPluginManager {
	/**
	 * 设置宿主资源，供进行宿主/插件资源切换
	 * 
	 * @param loader
	 * @param asset
	 * @param res
	 * @param theme
	 */
	void setResource(ClassLoader loader, AssetManager asset, Resources res,
			Theme theme);

	/**
	 * 构建宿主应用的插件数据库。
	 * <p>
	 * 如果无数据，则在指定路径下查找插件APK文件并解析插入数据库。
	 * <p>
	 * 路径1:{@code /data/misc/konka/plugin_apk/}
	 * <p>
	 * 路径2:{@code /data/data/packageName/app_plugins/}
	 * <p>
	 * 如果本地无插件，则从宿主assets/plugins/目录下释放默认自带插件至本地目录。
	 * 
	 * @param context
	 *            {@code getApplicationContext()}即可
	 * @throws FileNotFoundException
	 */
	void initPlugins(Context context) throws FileNotFoundException;
	/**
	 * 异步构建宿主应用的插件数据库。
	 * <p>
	 * 如果无数据，则在指定路径下查找插件APK文件并解析插入数据库。
	 * <p>
	 * 路径1:{@code /data/misc/konka/plugins/plugin/}
	 * <p>
	 * 路径2:{@code /data/data/packageName/app_plugins/}
	 * <p>
	 * 如果本地无插件，则从宿主assets/plugins/目录下释放默认自带插件至本地目录。
	 * 
	 * @param context
	 *            {@code getApplicationContext()}即可
	 * @param listener
	 *            异步方法结果回调函数
	 */
	void asyncInitPlugins(Context context, IAsyncListener listener);

	/**
	 * 获取目前被记录的所有插件，不论是否安装、是否启用。
	 * 
	 * @return 所有已被记录的插件APK信息。无则返回空列表。
	 */
	List<PluginInfo> getAllRecordedPlugins();

	/**
	 * 安装指定插件
	 * 
	 * @param context
	 *            {@code getApplicationContext()}即可
	 * @param pluginInfo
	 *            指定插件的插件信息
	 */
	boolean installPlugin(Context context, PluginInfo pluginInfo);

	/**
	 * 异步安装指定插件
	 * 
	 * @param context
	 *            {@code getApplicationContext()}即可
	 * @param pluginInfo
	 *            指定插件的信息
	 * @param listener
	 *            异步方法结果回调函数
	 */
	void asyncInstallPlugin(final Context context, final PluginInfo pluginInfo,
			final IAsyncListener listener);

	/**
	 * 异步安装指定插件集合
	 * 
	 * @param context
	 *            {@code getApplicationContext()}即可
	 * @param pluginInfos
	 *            指定插件集合的信息
	 * @param listener
	 *            异步方法结果回调函数
	 */
	void asyncInstallPlugins(final Context context,
			final List<PluginInfo> pluginInfos, final IAsyncListener listener);

	/**
	 * 卸载指定插件
	 * 
	 * @param context
	 *            {@code getApplicationContext()}即可
	 * @param pluginInfo
	 *            指定插件的插件信息
	 */
	boolean uninstallPlugin(Context context, PluginInfo pluginInfo);

	/**
	 * 异步卸载指定插件
	 * 
	 * @param context
	 *            {@code getApplicationContext()}即可
	 * @param pluginInfo
	 *            指定插件的插件信息
	 * @param listener
	 *            异步方法结果回调函数
	 */
	void asyncUninstallPlugin(final Context context, final PluginInfo pluginInfo,
			final IAsyncListener listener);

	/**
	 * 异步卸载指定插件集合
	 * 
	 * @param context
	 *            {@code getApplicationContext()}即可
	 * @param pluginInfos
	 *            指定插件集合的信息
	 * @param listener
	 *            异步方法结果回调函数
	 */
	void asyncUninstallPlugins(final Context context,
			final List<PluginInfo> pluginInfos, final IAsyncListener listener);

	/**
	 * 获取所有已经被宿主安装的插件
	 * 
	 * @return 所有已安装的插件。无则返回空列表。
	 */
	List<PluginInfo> getInstalledPlugins();

	/**
	 * 启用指定插件
	 * 
	 * @param plugin
	 *            指定插件的信息
	 */
	boolean enablePlugin(PluginInfo plugin);

	/**
	 * 异步启用指定插件
	 * 
	 * @param context
	 *            {@code getApplicationContext()}即可
	 * @param plugin
	 *            指定插件的信息
	 * @param listener
	 *            异步方法结果回调函数
	 */
	void asyncEnablePlugin(final PluginInfo plugin, final IAsyncListener listener);

	/**
	 * 异步启用指定插件集合
	 * 
	 * @param context
	 *            {@code getApplicationContext()}即可
	 * @param plugins
	 *            指定插件集合的信息
	 * @param listener
	 *            异步方法结果回调函数
	 */
	void asyncEnablePlugins(final List<PluginInfo> plugins,
			final IAsyncListener listener);

	/**
	 * 禁用指定插件
	 * 
	 * @param plugin
	 *            指定插件的信息
	 */
	boolean disablePlugin(PluginInfo plugin);

	/**
	 * 异步禁用指定插件
	 * 
	 * @param context
	 *            {@code getApplicationContext()}即可
	 * @param plugin
	 *            指定插件的信息
	 * @param listener
	 *            异步方法结果回调函数
	 */
	void asyncDisablePlugin(final PluginInfo plugin, final IAsyncListener listener);

	/**
	 * 异步禁用指定插件集合
	 * 
	 * @param context
	 *            {@code getApplicationContext()}即可
	 * @param plugins
	 *            指定插件集合的信息
	 * @param listener
	 *            异步方法结果回调函数
	 */
	void asyncDisablePlugins(final List<PluginInfo> plugins,
			final IAsyncListener listener);

	/**
	 * 获取所有已经启用，可供宿主获取视图显示的插件
	 * <p>
	 * 插件根据用户启用的顺序在返回列表中正序排列。
	 * 
	 * @return 所有已启用的插件。无则返回空列表。
	 */
	List<PluginInfo> getEnablePlugins();
	
	/**
	 * 更新插件信息类
	 * @param plugin 指定插件
	 * @return 更新后的信息类
	 */
	PluginInfo updatePlugin(PluginInfo plugin);

	/**
	 * 获取指定插件提供的视图
	 * 
	 * @param context
	 *            {@code getHostContext()}
	 * @param pluginInfo
	 *            指定插件的信息
	 * @return {@code View} 指定插件提供的视图。如果插件APK未根据要求实现，则返回{@code null}
	 */
	View getPluginView(Context context, PluginInfo pluginInfo);

	/**
	 * 获取当前{@code AssetManager}
	 * 
	 * @return 未找到则返回{@code null}
	 */
	AssetManager getAssets();

	/**
	 * 获取当前{@code Resources}
	 * 
	 * @return 未找到则返回{@code null}
	 */
	Resources getResources();

	/**
	 * 获取当前{@code Theme}
	 * 
	 * @return 未找到则返回{@code null}
	 */
	Theme getTheme();

	/**
	 * 获取当前{@code ClassLoader}
	 * 
	 * @return 未找到则返回{@code null}
	 */
	ClassLoader getClassLoader();
}
