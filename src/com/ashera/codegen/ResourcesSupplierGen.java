package com.ashera.codegen;

import java.io.File;

public class ResourcesSupplierGen extends CodeGenBase {
	public static void main(String[] args) throws Exception{
		StringBuffer buf = new StringBuffer();
		buf.append("start - body\n");
		String[] paths = {"../../ashera-demo-projects/ashera-phonegap-demo-project/ashera-demo/platforms/browser/app/src/main/resources/www/layout", "../../ashera-demo-projects/ashera-phonegap-demo-project/ashera-demo/platforms/browser/app/src/main/resources/font", "../../ashera-demo-projects/ashera-phonegap-demo-project/ashera-demo/platforms/browser/app/src/main/resources/res/xml/",
				"../../ashera-demo-projects/ashera-phonegap-demo-project/ashera-demo/platforms/browser/app/src/main/resources/res/anim/", "../../ashera-demo-projects/ashera-phonegap-demo-project/ashera-demo/platforms/browser/app/src/main/resources/res/animator/",
				"../../ashera-demo-projects/ashera-phonegap-demo-project/ashera-demo/platforms/browser/app/src/main/resources/navigation/"};
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
		writeOrUpdateFile(buf.toString(), "../../ashera-demo-projects/ashera-phonegap-demo-project/ashera-demo/platforms/browser/app/src/main/java/com/ashera/ResourcesSupplier.java", "body");

		
	}
}
