package com.zt.host;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.konka.dynamicplugin.core.IPluginManager;
import com.konka.dynamicplugin.core.PluginInfo;
import com.konka.dynamicplugin.host.IHost;

public class TestDialog extends Dialog {
	private LinearLayout mContainer;
	private IHost mHost;

	public TestDialog(Context context) {
		super(context, R.style.dialog);
		setContentView(R.layout.dialog);
		mHost = (IHost) context;
		getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		getWindow().setGravity(Gravity.FILL_HORIZONTAL|Gravity.BOTTOM);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContainer = (LinearLayout) findViewById(R.id.container);
		IPluginManager pluginManager = mHost.getPluginManager();
		final List<PluginInfo> enabledPluginsInfo = pluginManager.getEnablePlugins();
		for (PluginInfo plugin : enabledPluginsInfo) {
			View v = pluginManager.getPluginView(mHost.getHostContext(), plugin);
			mContainer.addView(v);
		}
	}

}
