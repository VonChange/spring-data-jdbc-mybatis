package com.vonchange.jdbc.abstractjdbc.util.markdown;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class FileUtils {
	private FileUtils() {
		throw new IllegalStateException("Utility class");
	}
	private static final String ENCODING = "UTF-8";
	private static final   String  URLSEPARATOR="/";
	public static String getEncoding() {
		return ENCODING;
	}
	public static String getFileURLPath(String packageName, String name) {
		String path = packageName.replaceAll("\\.", URLSEPARATOR);
		return  path + URLSEPARATOR+ name;
	}




	/**
	 *  取指定文件的扩展名
	 * @param filePathName
	 *            文件路径
	 * @return 扩展名
	 */
	public static String getFileExt(String filePathName) {
		int pos = 0;
		pos = filePathName.lastIndexOf('.');
		if (pos != -1)
			return filePathName.substring(pos + 1, filePathName.length());
		else
			return "";
	}
	//classpath:com/test/a.md
	public static Resource getResource(String location){
		PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
		return patternResolver.getResource(location);
	}


}