package com.zt.host;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TestReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();
		if ("com.zt.test".equals(action)) {
			context.startService(new Intent("com.zt.service"));
		}
	}

}
