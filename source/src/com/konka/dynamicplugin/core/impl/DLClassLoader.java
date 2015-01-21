package com.konka.dynamicplugin.core.impl;

import java.io.File;

import dalvik.system.DexClassLoader;

public class DLClassLoader extends DexClassLoader {
	private static final String SYSTEM_DEX_PATH = "/data/misc/konka/plugins/dex";
	
	protected DLClassLoader(String dexPath, String optimizedDirectory,
			String libraryPath, ClassLoader parent) {
		super(dexPath, optimizedDirectory, libraryPath, parent);
	}
	
	public static DLClassLoader getClassLoader(String apkPath, String dexPath,
			ClassLoader parent) {
		final String dexDirPath;
		if (!dexPath.isEmpty()) {
			dexDirPath = dexPath.substring(0,
					dexPath.lastIndexOf(File.separator) + 1);
		} else {
			dexDirPath = SYSTEM_DEX_PATH;
		}
		return new DLClassLoader(apkPath, dexDirPath, null, parent);
	}
	
}
