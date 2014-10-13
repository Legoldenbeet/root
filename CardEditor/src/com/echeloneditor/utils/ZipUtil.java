package com.echeloneditor.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.zip.ZipException;

public class ZipUtil {
	public static void pack(File sourceDir, File targetZip) throws ZipException, FileNotFoundException {
				File srcdir = new File(srcPathName);
				if (!srcdir.exists())
					throw new RuntimeException(srcPathName + "不存在！");
				
				Project prj = new Project();
				Zip zip = new Zip();
				zip.setProject(prj);
				zip.setDestFile(zipFile);
				FileSet fileSet = new FileSet();
				fileSet.setProject(prj);
				fileSet.setDir(srcdir);
				//fileSet.setIncludes("**/*.java"); 包括哪些文件或文件夹 eg:zip.setIncludes("*.java");
				//fileSet.setExcludes(...); 排除哪些文件或文件夹
				zip.addFileset(fileSet);
				
				zip.execute();

	}
}
