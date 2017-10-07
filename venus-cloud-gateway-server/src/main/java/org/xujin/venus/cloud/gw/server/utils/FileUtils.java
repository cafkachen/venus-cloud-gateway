package org.xujin.venus.cloud.gw.server.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileUtils {
	public static void persistent(String path, String json) throws IOException {
		// clear
		deleteFile(path);
		File file = getFile(path);
		org.apache.commons.io.FileUtils.writeStringToFile(file, json);
	}

	// 删除文件
	public static void deleteFile(String path) throws IOException {
		File file = new File(path);
		org.apache.commons.io.FileUtils.deleteQuietly(file);
	}

	// 获取文件
	public static File getFile(String path) throws IOException {
		File file = new File(path);
		return file;
	}

	public static List<String> readLines(String path) throws IOException {
		File file = FileUtils.getFile(path);
		return org.apache.commons.io.FileUtils.readLines(file, "UTF-8");
	}

	public static void writeAppendLines(String path, List<String> lines) throws IOException {
		File file = FileUtils.getFile(path);
		org.apache.commons.io.FileUtils.writeLines(file, "UTF-8", lines, true);
	}

	public static String readFileToString(String path) throws IOException {
		File file = FileUtils.getFile(path);
		return org.apache.commons.io.FileUtils.readFileToString(file);
	}

	public static void writeStringToFile(String path, String data) throws IOException {
		File file = FileUtils.getFile(path);
		org.apache.commons.io.FileUtils.writeStringToFile(file, data);
	}

}
