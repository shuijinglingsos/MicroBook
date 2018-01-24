package com.lidong.daymoney;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileOperation {

	public static void copyDatabase(File fromFile, File toFile)
			throws IOException {

		File dir = toFile.getParentFile();
		if (!dir.exists())
			dir.mkdirs();

		FileInputStream fis = new FileInputStream(fromFile);
		FileOutputStream fos = new FileOutputStream(toFile);

		byte[] buffer = new byte[10 * 1024];

		int count = 0;
		while ((count = fis.read(buffer)) > 0) {
			fos.write(buffer, 0, count);
			System.out.println("4");
		}
		fos.close();
		fis.close();
	}
}
