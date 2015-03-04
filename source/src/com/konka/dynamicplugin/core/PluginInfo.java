package com.konka.dynamicplugin.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.konka.dynamicplugin.core.tools.DLUtils;
import com.zt.simpledao.Column;
import com.zt.simpledao.Database;
import com.zt.simpledao.PropMethod;
import com.zt.simpledao.PropMethodType;
import com.zt.simpledao.SQLDataType;
import com.zt.simpledao.Table;

@Database(name = "plugin.db", version = 4)
@Table(name="plugins")
public class PluginInfo {
	@Column(index = 0, type=SQLDataType.TEXT)
	private String title;
	
	@Column(index = 1, type=SQLDataType.TEXT, primary=true)
	private String apkPath;
	
	@Column(index = 2, type=SQLDataType.TEXT)
	private String dexPath;
	
	@Column(index = 3, type=SQLDataType.TEXT)
	private String packageName;
	
	@Column(index = 4, type=SQLDataType.TEXT)
	private String entryClass;
	
	@Column(index = 5, type=SQLDataType.BLOB)
	private byte[] icon;
	
	@Column(index = 6, type=SQLDataType.INTEGER)
	private int version;
	
	@Column(index = 7, type=SQLDataType.INTEGER)
	private boolean installed;
	
	@Column(index = 8, type=SQLDataType.INTEGER)
	private boolean enabled;
	
	@Column(index = 9, type=SQLDataType.INTEGER)
	private int enableIndex;
	
	public PluginInfo() {
		title = "";
		apkPath = "";
		dexPath = "";
		packageName = "";
		entryClass = "";
		icon = new byte[0];
		version = 1;
		installed = false;
		enabled = false;
		enableIndex = -1;
	}

	@PropMethod(name = "title", type=PropMethodType.GET)
	public String getTitle() {
		return title;
	}
	
	public String getTitle(Context context) {
		String title = this.title;
		try {
			AssetManager assetManager = AssetManager.class.newInstance();
			Method addAssetPath = assetManager.getClass().getMethod("addAssetPath",
					String.class);
			addAssetPath.invoke(assetManager, apkPath);
			Resources resources = new Resources(assetManager, context.getResources()
					.getDisplayMetrics(), context.getResources().getConfiguration());
			final int titleID = DLUtils.getAppLabelID(context, apkPath);
			title = resources.getString(titleID);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return title;
	}

	@PropMethod(name = "title", type=PropMethodType.SET)
	public void setTitle(String title) {
		this.title = title;
	}

	@PropMethod(name = "apkPath", type=PropMethodType.GET)
	public String getApkPath() {
		return apkPath;
	}

	@PropMethod(name = "apkPath", type=PropMethodType.SET)
	public void setApkPath(String apkPath) {
		this.apkPath = apkPath;
	}

	@PropMethod(name = "dexPath", type=PropMethodType.GET)
	public String getDexPath() {
		return dexPath;
	}

	@PropMethod(name = "dexPath", type=PropMethodType.SET)
	public void setDexPath(String dexPath) {
		this.dexPath = dexPath;
	}
	
	@PropMethod(name = "packageName", type=PropMethodType.GET)
	public String getPackageName() {
		return packageName;
	}
	
	@PropMethod(name = "packageName", type=PropMethodType.SET)
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	@PropMethod(name = "entryClass", type=PropMethodType.GET)
	public String getEntryClass() {
		return entryClass;
	}

	@PropMethod(name = "entryClass", type=PropMethodType.SET)
	public void setEntryClass(String entryClass) {
		this.entryClass = entryClass;
	}
	
	@PropMethod(name = "icon", type=PropMethodType.GET)
	public byte[] getIcon() {
		return icon;
	}
	
	@PropMethod(name = "icon", type=PropMethodType.SET)
	public void setIcon(byte[] icon) {
		this.icon = icon;
	}
	
	public Drawable getIconDrawable() {
		Drawable drawable = null;
		if (null != this.icon) {
			ByteArrayInputStream is = new ByteArrayInputStream(this.icon);
			drawable = Drawable.createFromStream(is, this.title + ".png");
		}
		return drawable;
	}
	
	public void setIconDrawable(Drawable icon) {
		if (null != icon) {
			final BitmapDrawable bDrawable = (BitmapDrawable) icon;
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			bDrawable.getBitmap().compress(CompressFormat.PNG, 100, os);
			this.icon = os.toByteArray();
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@PropMethod(name = "version", type=PropMethodType.GET)
	public int getVersion() {
		return this.version;
	}
	
	@PropMethod(name = "version", type=PropMethodType.SET)
	public void setVersion(int version) {
		this.version = version;
	}

	@PropMethod(name = "installed", type=PropMethodType.GET)
	public boolean isInstalled() {
		return installed;
	}

	@PropMethod(name = "installed", type=PropMethodType.SET)
	public void setInstalled(boolean installed) {
		this.installed = installed;
	}

	@PropMethod(name = "enabled", type=PropMethodType.GET)
	public boolean isEnabled() {
		return enabled;
	}

	@PropMethod(name = "enabled", type=PropMethodType.SET)
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@PropMethod(name = "enableIndex", type=PropMethodType.GET)
	public int getEnableIndex() {
		return enableIndex;
	}

	@PropMethod(name = "enableIndex", type=PropMethodType.SET)
	public void setEnableIndex(int enableIndex) {
		this.enableIndex = enableIndex;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("title = ").append(this.title).append("\n");
		sb.append("apkPath = ").append(this.apkPath).append("\n");
		sb.append("dexPath = ").append(this.dexPath).append("\n");
		sb.append("packageName = ").append(this.packageName).append("\n");
		sb.append("entryClass = ").append(this.entryClass).append("\n");
		sb.append("version = ").append(this.version).append("\n");
		sb.append("installed = ").append(this.installed).append("\n");
		sb.append("enabled = ").append(this.enabled).append("\n");
		sb.append("enableIndex = ").append(this.enableIndex).append("\n");
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof PluginInfo) {
			return ((PluginInfo) o).getApkPath().equals(this.apkPath);
		}
		return super.equals(o);
	}
	
}
