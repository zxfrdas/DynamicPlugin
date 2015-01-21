package com.konka.dynamicplugin.core.impl;

import java.util.List;

import android.os.Handler;
import android.os.Looper;

import com.konka.dynamicplugin.core.IAsyncListener;
import com.konka.dynamicplugin.core.PluginInfo;

public class PostToUI implements Runnable {
	private Handler uiHandler;
	private IAsyncListener listener;
	private Task task;

	public static class Task {
		public boolean success;
		public String reason;
		public List<PluginInfo> pluginInfos;

		private Task(boolean success, String reason, List<PluginInfo> pluginInfos) {
			this.success = success;
			this.reason = reason;
			this.pluginInfos = pluginInfos;
		}

		public static Task success(List<PluginInfo> pluginInfos) {
			return new Task(true, "", pluginInfos);
		}

		public static Task fail(String reason) {
			return new Task(false, reason, null);
		}

	}

	public PostToUI() {
		uiHandler = new Handler(Looper.getMainLooper());
	}

	public void post(IAsyncListener listener, Task task) {
		this.listener = listener;
		this.task = task;
		uiHandler.post(this);
	}

	@Override
	public void run() {
		if (null != listener) {
			if (task.success) {
				listener.success(task.pluginInfos);
			} else {
				listener.fail(task.reason);
			}
		}
	}
}
