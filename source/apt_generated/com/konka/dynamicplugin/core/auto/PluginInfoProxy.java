package com.konka.dynamicplugin.core.auto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import com.konka.dynamicplugin.core.PluginInfo;
import com.zt.simpledao.bean.IBeanProxy;

public class PluginInfoProxy implements IBeanProxy<PluginInfo> {
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
	private static final HashMap<String, String> CACHE_UPDATE = new HashMap<String, String>();
	private static final HashMap<String, String> CACHE_DELETE = new HashMap<String, String>();
	private static final String INSERT = "insert into plugins (title,apkPath,dexPath,packageName,entryClass,icon,version,installed,enabled,enableIndex) values(?,?,?,?,?,?,?,?,?,?);";
	private static final String UPDATE = "update plugins set title=?, apkPath=?, dexPath=?, packageName=?, entryClass=?, icon=?, version=?, installed=?, enabled=?, enableIndex=? ";
	private static final String DELETE = "delete from plugins ";

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
	public Class<PluginInfo> getBeanClass() {
		return PluginInfo.class;
	}

	@Override
	public List<PluginInfo> convertDatabaseToBean(Cursor cursor) {
		List<PluginInfo> beans = new ArrayList<PluginInfo>();
		if (null != cursor) {
			while(cursor.moveToNext()) {
				try {
					PluginInfo item = getBeanClass().newInstance();
					item.setTitle(cursor.getString(0));
					item.setApkPath(cursor.getString(1));
					item.setDexPath(cursor.getString(2));
					item.setPackageName(cursor.getString(3));
					item.setEntryClass(cursor.getString(4));
					item.setIcon(cursor.getBlob(5));
					item.setVersion(cursor.getInt(6));
					item.setInstalled(cursor.getInt(7) == 1 ? true : false);
					item.setEnabled(cursor.getInt(8) == 1 ? true : false);
					item.setEnableIndex(cursor.getInt(9));
					beans.add(item);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
			cursor.close();
		}
		return beans;
	}

	@Override
	public SQLiteStatement createInsertSQL(SQLiteDatabase database,PluginInfo bean) {
		SQLiteStatement sqLiteStatement = database.compileStatement(INSERT);
		bindBeanArg(sqLiteStatement, bean);
		return sqLiteStatement;
	}

	@Override
	public SQLiteStatement createUpdateSQL(SQLiteDatabase database,PluginInfo bean, String whereClause, String[] whereArgs) {
		final int argCount = (null == whereArgs) ? 10 : (10 + whereArgs.length);
		String sql = CACHE_UPDATE.get(whereClause);
		if (null == sql) {
			StringBuilder sb = new StringBuilder(UPDATE);
			if (!TextUtils.isEmpty(whereClause)) {
				sb.append(" where ").append(whereClause);
			}
			sql = sb.toString();
			CACHE_UPDATE.put(whereClause, sql);
		}
		SQLiteStatement statement = database.compileStatement(sql);
		bindBeanArg(statement, bean);
		for (int i = 10; i < argCount; i ++) {
			statement.bindString(i + 1, whereArgs[i - 10]);
		}
		return statement;
	}

	private void bindBeanArg(SQLiteStatement statement, PluginInfo bean) {
		statement.bindString(1, null == bean.getTitle() ? "" : bean.getTitle().toString());
		statement.bindString(2, null == bean.getApkPath() ? "" : bean.getApkPath().toString());
		statement.bindString(3, null == bean.getDexPath() ? "" : bean.getDexPath().toString());
		statement.bindString(4, null == bean.getPackageName() ? "" : bean.getPackageName().toString());
		statement.bindString(5, null == bean.getEntryClass() ? "" : bean.getEntryClass().toString());
		statement.bindBlob(6, bean.getIcon());
		statement.bindLong(7, bean.getVersion());
		statement.bindLong(8, bean.isInstalled() ? 1 : 0);
		statement.bindLong(9, bean.isEnabled() ? 1 : 0);
		statement.bindLong(10, bean.getEnableIndex());
	}

	@Override
	public SQLiteStatement createDeleteSQL(SQLiteDatabase database, String whereClause, String[] whereArgs) {
		String sql = CACHE_DELETE.get(whereClause);
		if (null == sql) {
			StringBuilder sb = new StringBuilder(DELETE);
			if (!TextUtils.isEmpty(whereClause)) {
				sb.append(" where ").append(whereClause);
			}
			CACHE_DELETE.put(whereClause, sql = sb.toString());
		}
		SQLiteStatement statement = database.compileStatement(sql);
		if (null != whereArgs) {
			int index = 0;
			for (String s : whereArgs) {
				statement.bindString(index + 1, s);
				index++;
			}
		}
		return statement;
	}

}