package com.konka.dynamicplugin.core.auto;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.zt.simpledao.bean.IBeanProxy;
import com.zt.simpledao.dao.SQLite3DAO;
import com.konka.dynamicplugin.core.PluginInfo;
import com.konka.dynamicplugin.core.auto.PluginInfoProxy;

public class PluginInfoDAO extends SQLite3DAO<PluginInfo> {
	// com.konka.dynamicplugin.core.PluginInfo
	private static PluginInfoDAO sInstance;

	public synchronized static PluginInfoDAO getInstance(Context context) {
		if (null == sInstance) {
			sInstance = new PluginInfoDAO(context, new PluginInfoProxy());
		}
		return sInstance;
	}

	private PluginInfoDAO(Context context, IBeanProxy<PluginInfo> proxy) {
		super(context, proxy);
	}

	@Override
	protected void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion, IBeanProxy<PluginInfo> proxy) {
		db.execSQL("DROP TABLE IF EXISTS " + proxy.getTableName());
		db.execSQL(proxy.getTableCreator());
	}

}