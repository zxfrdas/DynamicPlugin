package com.zt.host;

import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.konka.dynamicplugin.core.IAsyncListener;
import com.konka.dynamicplugin.core.PluginInfo;
import com.konka.dynamicplugin.host.app.HostActivity;

public class MainActivity extends HostActivity implements OnClickListener {
	private ListView mPluginList;
	private PluginAdapter mAdapter;
	private Button mOpenPlugin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		findPlugin();
	}

	private void initView() {
		mPluginList = (ListView) findViewById(R.id.plugin_list);
		mPluginList.setItemsCanFocus(true);
		mAdapter = new PluginAdapter(getApplicationContext(), getPluginManager());
		mPluginList.setAdapter(mAdapter);
		mOpenPlugin = (Button) findViewById(R.id.open_plugin);
		mOpenPlugin.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		if (R.id.open_plugin == id) {
			new TestDialog(getHostContext()).show();
			// startService(new Intent(this, TestService.class));
		}
	}

	private void findPlugin() {
		getPluginManager().asyncInitPlugins(getApplicationContext(),
				new IAsyncListener() {

			@Override
			public void success(List<PluginInfo> pluginInfos) {
				Log.d("PluginManager", "init plugins success");
				List<PluginInfo> allPluginsInfo = getPluginManager()
						.getAllRecordedPlugins();
				mAdapter.setData(allPluginsInfo);
				mAdapter.notifyDataSetChanged();
				mPluginList.setSelection(0);
			}

			@Override
			public void fail(String reason) {
				Log.d("PluginManager", "init plugins fail reason = "
						+ reason);
			}
		});
	}

}
