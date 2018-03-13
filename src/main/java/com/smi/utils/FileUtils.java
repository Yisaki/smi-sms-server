package com.smi.utils;

import java.io.File;

import org.springframework.util.ClassUtils;

public class FileUtils {
	/**
	 * 当前项目运行文件所在的Jar包放置的目录，末尾有带路径分隔符
	 */
	private static String jarDirPath;
	
	
	static {
		String jarDir = ClassUtils.getDefaultClassLoader().getResource("").getPath();
		int idx = jarDir.lastIndexOf("!/BOOT-INF");
		if(idx > 0) {
			jarDir = jarDir.substring(0, idx);
		}
		
		StringBuilder tmp = new StringBuilder("/");
		if(System.getProperty("os.name").toLowerCase().contains("win")) {
			tmp.setLength(0);
		}
		
		String[] parts = jarDir.split("/"); //windows和linux返回的分隔符都为"/"
		for(int i = 1; i < parts.length -1; i++) {
			tmp.append(parts[i]).append(File.separator);
		}
		
		jarDirPath = tmp.toString();
	}
	
	/**
	 * 返回当前项目运行文件所在的Jar包放置的目录的路径，末尾有带路径分隔符
	 * @return
	 */
	public static String getJarDirPath() {
		return jarDirPath;
	}
}
