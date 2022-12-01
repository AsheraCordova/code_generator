package com.ashera.codegen;

import java.io.File;

public class CordovaLibCopyFiles {
	public static void main(String[] args) {
		int i = 10;
		for (File x : new File("/Users/ramm/git/core-javafx-widget/SWTCordova/src/main/java/org/apache/cordova").listFiles()) {
			if (x.isFile()) {
			System.out.println(String.format("copy.files.%s=../../ashera-demo-projects/ashera-phonegap-demo-project/ashera-demo/platforms/android/CordovaLib/src/org/apache/cordova/%s:../../core-javafx-widget/SWTCordova/src/main/java/org/apache/cordova/%s",
					i + "", x.getName(), x.getName()));
			i++;
			}
		}
		
		for (File x : new File("/Users/ramm/git/core-javafx-widget/SWTCordova/src/main/java/org/apache/cordova/engine").listFiles()) {
			if (x.isFile()) {
			System.out.println(String.format("copy.files.%s=../../ashera-demo-projects/ashera-phonegap-demo-project/ashera-demo/platforms/android/CordovaLib/src/org/apache/cordova/engine/%s:../../core-javafx-widget/SWTCordova/src/main/java/org/apache/cordova/engine/%s",
					i + "", x.getName(), x.getName()));
			i++;
			}
		}
		
	}
}
