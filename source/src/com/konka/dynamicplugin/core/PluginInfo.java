package com.konka.dynamicplugin.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.zt.simpledao.Column;
import com.zt.simpledao.Database;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getApkPath() {
		return apkPath;
	}

	public void setApkPath(String apkPath) {
		this.apkPath = apkPath;
	}

	public String getDexPath() {
		return dexPath;
	}

	public void setDexPath(String dexPath) {
		this.dexPath = dexPath;
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getEntryClass() {
		return entryClass;
	}

	public void setEntryClass(String entryClass) {
		this.entryClass = entryClass;
	}
	
	public Drawable getIcon() {
		Drawable drawable = null;
		if (null != this.icon) {
			ByteArrayInputStream is = new ByteArrayInputStream(this.icon);
			drawable = Drawable.createFromStream(is, this.title + ".png");
		}
		return drawable;
	}
	
	public void setIcon(Drawable icon) {
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
	
	public int getVersion() {
		return this.version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}

	public boolean isInstalled() {
		return installed;
	}

	public void setInstalled(boolean installed) {
		this.installed = installed;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getEnableIndex() {
		return enableIndex;
	}

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
