package com.futonredemption.makemotivator.util;

import java.io.File;

public class FileUtils {

	public static File safeCreateFilePath(File baseDir, String fileName) {
		File tryFile = new File(baseDir, fileName);
		int attempt = 1;
		while(tryFile.exists()) {
			String newFileName = String.format("%s-%d.%s", getFileName(fileName), attempt, getFileExtension(fileName));
			tryFile = new File(baseDir, newFileName);
			attempt++;
		}

		return tryFile;
	}

	public static String getFileName(String filePath) {
		return filePath.substring(0, filePath.lastIndexOf("."));
	}

	public static String getFileExtension(String filePath) {
		return filePath.substring(filePath.lastIndexOf(".") + 1);
	}
}
