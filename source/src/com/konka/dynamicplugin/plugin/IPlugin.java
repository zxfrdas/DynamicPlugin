package com.konka.dynamicplugin.plugin;

import android.content.Context;
import android.view.View;

/**
 * 插件接口，实现此接口即可将插件APK的视图提供给宿主使用。
 * <p>
 * 插件可以使用XML定义的资源，自定义的主题，自定义的控件
 * <p>
 * 自定义控件逻辑需封装在插件内部，因宿主应用中并没有插件中自定义的控件类。
 */
public interface IPlugin {
	/**
	 * 宿主初始化插件时传入的宿主之上下文。
	 * 
	 * @param context
	 */
	void setContext(Context context);

	/**
	 * 获取插件视图
	 * 
	 * @return 插件为宿主提供的视图
	 */
	View getPluginView();
}
