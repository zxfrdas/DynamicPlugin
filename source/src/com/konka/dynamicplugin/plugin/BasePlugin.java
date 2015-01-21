package com.konka.dynamicplugin.plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;

/**
 * 插件基类，对{@code IPlugin}接口做了通用实现。建议插件继承此基类而非直接实现接口。
 * <p>
 * 如果插件中需要用到自定义控件，则务必继承此基类，因为自定义控件的热插拔实现有一些特殊的逻辑。
 */
public abstract class BasePlugin implements IPlugin, OnAttachStateChangeListener {
	private Context mContext;
	private LayoutInflater mInflater;

	/**
	 * 插件被宿主创建，请在此进行初始化的操作。
	 */
	public abstract void onCreate();

	/**
	 * 插件显示在宿主时回调，可以在此进行{@code Service},{@code BroadcastReceiver}等的绑定
	 */
	public abstract void onAttach();

	/**
	 * 插件从宿主中消失时回调，可以在此进行{@code Service},{@code BroadcastReceiver}等的解绑
	 */
	public abstract void onDetach();

	@Override
	public final void onViewAttachedToWindow(View v) {
		onAttach();
	}

	@Override
	public final void onViewDetachedFromWindow(View v) {
		onDetach();
	}

	@Override
	public final void setContext(Context context) {
		mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		onCreate();
	}

	/**
	 * 获取上下文
	 * @return 宿主传递来的{@code Context}
	 */
	public final Context getContext() {
		return mContext;
	}

	/**
	 * 获取资源
	 * @return 插件的资源
	 */
	public final Resources getResources() {
		return mContext.getResources();
	}

	/**
	 * 解析插件布局视图
	 * @param layout 插件的XML布局文件ID
	 * @return 插件视图
	 */
	public final View inflateRootView(int layout) {
		View root = mInflater.inflate(layout, null);
		root.addOnAttachStateChangeListener(this);
		return root;
	}

	/**
	 * 获取父控件中指定ID的子控件
	 * @param parent 父控件
	 * @param id 视图获取的子控件
	 * @return 子控件
	 */
	public final View findViewById(View parent, int id) {
		final View v = parent.findViewById(id);
		final String viewName = v.getClass().getName();
		if (!isAndroidNativeView(viewName)) {
			// 删除LayoutInflate中保存的每个view的构造函数，这是支持动态安装删除插件功能
			// 如果不删除LayoutInflate中缓存的构造函数，重装此插件后，不重启APP
			// LayoutInflate中保存的构造函数的classloader和重装后的不同，会导致ClassCastException
			// android.widget开头的class都是由BootClassloader载入并不因重装而不同，因此不用去除缓存。
			clearCustomizeViewCache(viewName);
		}
		return v;
	}

	private boolean isAndroidNativeView(String name) {
		return name.contains("android.widget");
	}

	@SuppressWarnings("unchecked")
	private void clearCustomizeViewCache(String name) {
		try {
			Field f = LayoutInflater.class.getDeclaredField("sConstructorMap");
			f.setAccessible(true);
			HashMap<String, Constructor<? extends View>> map = (HashMap<String, Constructor<? extends View>>) f
					.get(mInflater);
			Log.d("PluginManager", "sCustomizeViews view = " + name);
			map.remove(name);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
