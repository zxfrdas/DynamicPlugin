package com.konka.dynamicplugin.core.auto;

import android.util.SparseArray;

import com.konka.dynamicplugin.core.PluginInfo;
import com.zt.simpledao.bean.ColumnItem;
import com.zt.simpledao.bean.IBeanProxy;
import com.zt.simpledao.SQLDataType;

public class PluginInfoProxy implements IBeanProxy {
	// com.konka.dynamicplugin.core.PluginInfo
	public static final String apkPath = "apkPath";
	public static final int apkPath_id = 1;
	public static final String dexPath = "dexPath";
	public static final int dexPath_id = 2;
	public static final String enableIndex = "enableIndex";
	public static final int enableIndex_id = 9;
	public static final String enabled = "enabled";
	public static final int enabled_id = 8;
	public static final String entryClass = "entryClass";
	public static final int entryClass_id = 4;
	public static final String icon = "icon";
	public static final int icon_id = 5;
	public static final String installed = "installed";
	public static final int installed_id = 7;
	public static final String packageName = "packageName";
	public static final int packageName_id = 3;
	public static final String title = "title";
	public static final int title_id = 0;
	public static final String version = "version";
	public static final int version_id = 6;
	private static final String DATABASE_NAME = "plugin.db";
	private static final int VERSION = 4;
	private static final String TABLE = "plugins";
	private static final String TABLE_CREATOR = "create table plugins(title TEXT, apkPath TEXT, dexPath TEXT, packageName TEXT, entryClass TEXT, icon BLOB, version INTEGER, installed INTEGER, enabled INTEGER, enableIndex INTEGER, primary key (apkPath));";
	private static final SparseArray<ColumnItem> ALL_COLUMNS = new SparseArray<ColumnItem>(10);
	static {
		Class<PluginInfo> claz = PluginInfo.class;
		try {
			ColumnItem item0 = new ColumnItem(0, "title", SQLDataType.TEXT, false, claz.getDeclaredField("title"));
			ALL_COLUMNS.put(0, item0);
			ColumnItem item1 = new ColumnItem(1, "apkPath", SQLDataType.TEXT, true, claz.getDeclaredField("apkPath"));
			ALL_COLUMNS.put(1, item1);
			ColumnItem item2 = new ColumnItem(2, "dexPath", SQLDataType.TEXT, false, claz.getDeclaredField("dexPath"));
			ALL_COLUMNS.put(2, item2);
			ColumnItem item3 = new ColumnItem(3, "packageName", SQLDataType.TEXT, false, claz.getDeclaredField("packageName"));
			ALL_COLUMNS.put(3, item3);
			ColumnItem item4 = new ColumnItem(4, "entryClass", SQLDataType.TEXT, false, claz.getDeclaredField("entryClass"));
			ALL_COLUMNS.put(4, item4);
			ColumnItem item5 = new ColumnItem(5, "icon", SQLDataType.BLOB, false, claz.getDeclaredField("icon"));
			ALL_COLUMNS.put(5, item5);
			ColumnItem item6 = new ColumnItem(6, "version", SQLDataType.INTEGER, false, claz.getDeclaredField("version"));
			ALL_COLUMNS.put(6, item6);
			ColumnItem item7 = new ColumnItem(7, "installed", SQLDataType.INTEGER, false, claz.getDeclaredField("installed"));
			ALL_COLUMNS.put(7, item7);
			ColumnItem item8 = new ColumnItem(8, "enabled", SQLDataType.INTEGER, false, claz.getDeclaredField("enabled"));
			ALL_COLUMNS.put(8, item8);
			ColumnItem item9 = new ColumnItem(9, "enableIndex", SQLDataType.INTEGER, false, claz.getDeclaredField("enableIndex"));
			ALL_COLUMNS.put(9, item9);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getDataBaseName() {
		return DATABASE_NAME;
	}

	@Override
	public int getDataBaseVersion() {
		return VERSION;
	}

	@Override
	public String getTableName() {
		return TABLE;
	}

	@Override
	public String getTableCreator() {
		return TABLE_CREATOR;
	}

	@Override
	public Class<?> getBeanClass() {
		return PluginInfo.class;
	}

	@Override
	public SparseArray<ColumnItem> getAllColumns() {
		return ALL_COLUMNS;
	}

}