package com.zt.host;

import java.io.FileNotFoundException;
import java.util.List;

import android.content.Intent;
import android.os.IBinder;

import com.konka.dynamicplugin.core.PluginInfo;
import com.konka.dynamicplugin.host.app.HostService;

public class TestService extends HostService {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		try {
			getPluginManager().initPlugins(getApplicationContext());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		List<PluginInfo> allPluginsInfo = getPluginManager().getAllRecordedPlugins();
		for (PluginInfo plugin : allPluginsInfo) {
			getPluginManager().installPlugin(getApplicationContext(), plugin);
			getPluginManager().enablePlugin(plugin);
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new TestDialog(getHostContext()).show();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
