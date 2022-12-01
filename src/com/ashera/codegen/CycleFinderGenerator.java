package com.ashera.codegen;

import java.io.File;
import java.io.IOException;

public class CycleFinderGenerator {
	public static void main(String[] args) {
		java.util.ArrayList<String> files = new java.util.ArrayList<>();
		
		displayDirectoryContents( new File("/Users/ramm/git/core-widget_library/widget_library/src"), files);
		displayDirectoryContents( new File("/Users/ramm/git/core-widget_library/css_parser/src"), files);
		displayDirectoryContents( new File("/Users/ramm/git/core-widget_library/html_parser/src"), files);
		displayDirectoryContents( new File("/Users/ramm/git/core-widget_library/Plugin_HtmlParser/src"), files);
		displayDirectoryContents( new File("/Users/ramm/git/core-widget_library/Plugin_Navigator/src"), files);
		displayDirectoryContents( new File("/Users/ramm/git/core-widget_library/Plugin_Converter/src"), files);

		displayDirectoryContents( new File("/Users/ramm/git/core-ios-widgets/ios_widget_library/src/main/java"), files);
		displayDirectoryContents( new File("/Users/ramm/git/core-javafx-widget/SWTAndroid/src/main/stub"), files);

		displayDirectoryContents( new File("/Users/ramm/git/core-javafx-widget/SWTAndroid/src/main/java"), files);
		displayDirectoryContents( new File("/Users/ramm/git/core-javafx-widget/SWTAndroidX-core/src/main/java"), files);
		displayDirectoryContents( new File("/Users/ramm/git/core-javafx-widget/SWTAndroidX-Gridlayout/src/main/java"), files);
		displayDirectoryContents( new File("/Users/ramm/git/core-javafx-widget/SWTAndroidXConstraintLayout/src/main/java"), files);

		displayDirectoryContents( new File("/Users/ramm/git/core-ios-widgets/IOSAndroidXConstraintLayoutPlugin/src/main/java"), files);
		displayDirectoryContents( new File("/Users/ramm/git/core-ios-widgets/IOSAndroidXGridlayoutPlugin/src/main/java"), files);
		displayDirectoryContents( new File("/Users/ramm/git/core-ios-widgets/IOSConverter/src/main/java"), files);
		displayDirectoryContents( new File("/Users/ramm/git/core-ios-widgets/IOSCorePlugin/src/main/java"), files);
		displayDirectoryContents( new File("/Users/ramm/git/core-ios-widgets/IOSJSONAdapter/src/main/java"), files);
		displayDirectoryContents( new File("/Users/ramm/git/core-ios-widgets/IOSNavigatorPlugin/src"), files);

		
		StringBuffer stringBuffer = new StringBuffer("java -jar cycle_finder.jar -w /Users/ramm/git/core-widget_library/code_generator/whitelist.txt -classpath /Users/ramm/git_bitbucket/asheralayout/code_generator/lib/j2objc_annotations.jar ");
		
		for (String file : files) {
			stringBuffer.append(file + " ");			
		}
		
		stringBuffer.append(" > /Users/ramm/git/core-widget_library/code_generator/cycles.txt");
		System.out.println(stringBuffer.toString());

	}

	public static void displayDirectoryContents(File dir, java.util.ArrayList<String> filesList) {
		try {
			File[] files = dir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					//System.out.println("directory:" + file.getCanonicalPath());
					displayDirectoryContents(file, filesList);
				} else {
					if (file.getCanonicalPath().endsWith(".java")) {
						filesList.add(file.getCanonicalPath());
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
