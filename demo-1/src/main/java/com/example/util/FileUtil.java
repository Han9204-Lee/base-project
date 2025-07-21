package com.example.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;

public class FileUtil {
	public static String normalizeFileName(String originalName) {
		String os = System.getProperty("os.name").toLowerCase();

		// MacOS는 NFD, 나머지는 NFC로 정규화
		if (os.contains("mac")) {
			return Normalizer.normalize(originalName, Normalizer.Form.NFC);
		} else {
			return originalName;
		}
	}

	public static Path getUploadPath(String uploadDir, String savedName) {
		return Paths.get(uploadDir, savedName);
	}
}