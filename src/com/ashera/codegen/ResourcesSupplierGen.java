package com.ashera.codegen;

import java.io.File;

public class ResourcesSupplierGen extends CodeGenBase {
	public static void main(String[] args) throws Exception{
		StringBuffer buf = new StringBuffer();
		buf.append("start - body\n");

		String[] paths = {"../../core-web-widget/WebStarter/src/main/resources/www/layout", "../../core-web-widget/WebStarter/src/main/resources/font", "../../core-web-widget/WebStarter/src/main/resources/res/xml/",
				"../../core-web-widget/WebStarter/src/main/resources/res/anim/", "../../core-web-widget/WebStarter/src/main/resources/res/animator/",
				"../../core-web-widget/WebStarter/src/main/resources/navigation/"};
		String[] prefix = {"www/layout/", "font/", "res/xml/", "res/anim/", "res/animator/", "navigation/"};
		int i = 0;
		for (String path : paths) {
			File f = new java.io.File(path);
			String[] fileNames = f.list();
			
			for (String fileName : fileNames) {
				buf.append("\"").append(prefix[i]).append(fileName).append("\",\n");
			}
			i++;
		}
		buf.append("//end - body");
		
		writeOrUpdateFile(buf.toString(), "../../core-web-widget/WebStarter/src/main/java/com/ashera/ResourcesSupplier.java", "body");

		
	}
}
