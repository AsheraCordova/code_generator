package com.ashera.codegen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PluginPathGen extends CodeGenBase {
	public static void main(String[] args) throws IOException {
		List<String> pathWithDirAndPrefixes = new java.util.ArrayList<>(Arrays.asList(new String []{
				"../../core-android-widget/AndroidCorePlugin/src:src::",
				"../../core-android-widget/AndroidCorePlugin/res:res::",
				"../../core-android-widget/AndroidToolsPlugin/gentool:gentool::",
				"../../core-android-widget/AndroidCorePlugin/tsc:tsc::",
				"../../core-android-widget/AndroidConverterPlugin/src:src::",
				"../../core-android-widget/AndroidJsonAdapter/src:src::",
				"../../core-android-widget/AndroidXConstraintLayout/src:src::",
				"../../core-android-widget/AndroidXConstraintLayout/tsc:tsc::",
				"../../core-android-widget/AndroidLayouts/src:src::",
				"../../core-android-widget/AndroidLayouts/tsc:tsc::",
				"../../core-android-widget/AndroidLayouts/res:res::",
				"../../core-widget_library/widget_library/AsheraHelloWorld/res:res::",
				"../../core-widget_library/widget_library/AsheraHelloWorld/tsc:tsc::",
				"../../core-android-widget/AndroidXGridLayoutPlugin/src:src::",
				"../../core-android-widget/AndroidXGridLayoutPlugin/tsc:tsc::",
				"../../core-ios-widgets/IOSCorePlugin/ios/src:ios::CorePlugin/",
				"../../core-ios-widgets/IOSFreeMarkerPlugin/library/freemarker/src:freemarker:library/:CorePlugin/",
				"../../core-ios-widgets/IOSCorePlugin/library/css_parser/src:css_parser:library/:CorePlugin/",
				"../../core-ios-widgets/IOSCorePlugin/library/html_parser/src:html_parser:library/:CorePlugin/",
				"../../core-ios-widgets/IOSCorePlugin/library/Plugin_HtmlParser/src:Plugin_HtmlParser:library/:CorePlugin/",
				"../../core-ios-widgets/IOSCorePlugin/library/widget_library/src:widget_library:library/:CorePlugin/",
				"../../core-ios-widgets/IOSCorePlugin/library/AndroidCore/src:AndroidCore:library/:CorePlugin/",
				"../../core-ios-widgets/IOSCorePlugin/library/AndroidXCore/src:AndroidXCore:library/:CorePlugin/",
				"../../core-ios-widgets/IOSCorePlugin/library/AndroidJMaterial/src:AndroidJMaterial:library/:CorePlugin/",
				"../../core-ios-widgets/IOSJSONAdapter/ios/src:ios::JSONCore/",
				"../../core-ios-widgets/IOSConverter/ios/src:ios::ConverterPlugin/",
				"../../core-ios-widgets/IOSConverter/library/Plugin_Converter/src:Plugin_Converter:library/:ConverterPlugin/",
				"../../core-ios-widgets/ios_widget_library/ios/src:ios::LayoutPlugin/",
				"../../core-ios-widgets/ios_widget_library/ios_ext/src:ios_ext::LayoutPlugin/",
				"../../core-ios-widgets/IOSAndroidXGridlayoutPlugin/ios/src:ios::GridLayoutPlugin/",
				"../../core-ios-widgets/IOSAndroidXGridlayoutPlugin/library/AndroidX-Gridlayout/src:AndroidX-Gridlayout:library/:GridLayoutPlugin/",
				"../../core-ios-widgets/IOSAndroidXGridlayoutPlugin/tsc/src:tsc::",
				"../../core-ios-widgets/IOSAndroidXConstraintLayoutPlugin/library/AndroidXConstraintLayout/src:AndroidXConstraintLayout:library/:ConstraintLayoutPlugin/",
				"../../core-ios-widgets/IOSAndroidXConstraintLayoutPlugin/ios:ios::ConstraintLayoutPlugin/",
				"../../core-ios-widgets/IOSAndroidXConstraintLayoutPlugin/tsc:tsc::",
				"../../core-ios-widgets/ios_widget_library/tsc/src:tsc::",
				"../../core-android-widget/AndroidCSSBorderPlugin/src:src::",
				"../../core-android-widget/AndroidCSSBorderPlugin/tsc:tsc::",
				"../../core-ios-widgets/IOSCSSBorderPlugin/ios:ios::CSSBorderPlugin/",
				"../../core-ios-widgets/IOSCSSBorderPlugin/tsc/src:tsc::",
				"../../core-ios-widgets/IOSNavigationDrawerPlugin/library/AndroidXDrawerLayout/src:AndroidXDrawerLayout:library/:AndroidXDrawerLayout/",
				"../../core-ios-widgets/IOSRecycleViewPlugin/library/AndroidXRecyclerView/src:AndroidXRecyclerView:library/:AndroidXRecyclerView/",
				"../../core-ios-widgets/IOSToolbarPlugin/library/AndroidToolbarPlugin/src:AndroidToolbarPlugin:library/:AndroidToolbarPlugin/",
				"../../ashera-demo-projects/ashera-phonegap-demo-project/demoapp1/custom_plugins/CustomPlugin/ios/src/:ios::CustomPlugin/",
				"../../ashera-demo-projects/ashera-phonegap-demo-project/demoapp1/custom_plugins/CustomPlugin/src/main/java:src\\main\\java:::",
				"../../core-ios-widgets/IOSSnackbarPlugin/library/AndroidJSnackbar/src:AndroidJSnackbar:library/:Snackbar/",
				"../../core-ios-widgets/IOSCoordinatorLayoutPlugin/library/AndroidXCoordinatorLayout/src:AndroidXCoordinatorLayout:library/:CoordinatorLayout/",
				"../../core-ios-widgets/IOSNavigationViewPlugin/library/NavigationView/src:NavigationView:library/:NavigationView/",
				"../../core-ios-widgets/IOSTabLayoutPlugin/library/AndroidJTablayout/src:AndroidJTablayout:library/:TabLayout/",
				"../../core-ios-widgets/IOSViewPagerPlugin/library/AndroidXJViewPager/src:AndroidXJViewPager:library/:ViewPager/"
		}));
		
		String[] dirs = {"../../core-javafx-widget", "../../core-web-widget"};
		
		for (String dir : dirs) {
			for (File file : new File(dir).listFiles()) {
				if (file.isDirectory() && (new File(file, "core.gradle").exists() || new File(file, "layout.gradle").exists()) &&  new File(file, "tsc").exists()) {
					String str = dir + "/" + file.getName()  + "/tsc/src:tsc::";
					pathWithDirAndPrefixes.add(str);
				}
			}
		}
		generatePluginXml(pathWithDirAndPrefixes);    


	}

	public static void generatePluginXml(List<String> pathWithDirAndPrefixes)
			throws IOException, FileNotFoundException {
		Set<String> myFiles = listFilesUsingJavaIO("plugininfo/pluginpathgen");
		if (myFiles != null) {
			for (String name : myFiles) {
				PomFileSync gen = new PomFileSync();
				Properties properties;
				properties = new Properties();
				properties.load(new FileInputStream("plugininfo/pluginpathgen/" + name));
				gen.copyFiles(properties, true);
	
				for (int i = 0; i < 100; i++) {
					if (properties.containsKey("update.files." + i)) {
						pathWithDirAndPrefixes.add(properties.getProperty("update.files." + i));
					}
				}
			}
		}

		List<String> pathsList = new java.util.ArrayList<String>();
		List<String> srcDirList = new java.util.ArrayList<String>();
		List<String> targetDirPrefixList = new java.util.ArrayList<String>();
		List<String> sourceDirPrefixList = new java.util.ArrayList<String>();
		
		for (String pathWithDirAndPrefix : pathWithDirAndPrefixes) {
			String[] pathWithDirAndPrefixSplit = pathWithDirAndPrefix.split("\\:");
			pathsList.add(pathWithDirAndPrefixSplit[0]);
			srcDirList.add(pathWithDirAndPrefixSplit[1]);
			if (pathWithDirAndPrefixSplit.length > 2) {
				sourceDirPrefixList.add(pathWithDirAndPrefixSplit[2]);
			} else {
				sourceDirPrefixList.add("");
			}
			if (pathWithDirAndPrefixSplit.length > 3) {
				targetDirPrefixList.add(pathWithDirAndPrefixSplit[3]);
			} else {
				targetDirPrefixList.add("");
			}
		}
		
		String[] paths = pathsList.toArray(new String[0]);
		String[] srcDir = srcDirList.toArray(new String[0]); 
		String[] targetDirPrefix =  targetDirPrefixList.toArray(new String[0]);
		String[] sourceDirPrefix =  sourceDirPrefixList.toArray(new String[0]);

		System.out.println(paths.length  + " " + srcDir.length + " " + targetDirPrefix.length + " " + sourceDirPrefix.length);
		List<String> classPaths = Arrays.stream(paths).map(path -> {
			try {
				return new File(path).getCanonicalPath();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}).collect(Collectors.toList());
		AtomicInteger indexer = new AtomicInteger(); 
		AtomicInteger indexClassPaths = new AtomicInteger(); 
		classPaths.stream().map((path) -> {			
			int index = indexer.getAndIncrement();
			List<String> prefixes = getFileStream(path).filter((mypath) -> {
				return mypath.toFile().isFile();
			}).map(mypath -> {
				String directoryToIndex = srcDir[index] + File.separator;
				String targetDirPrefixDir = targetDirPrefix[index];
				String sourceDirPrefixxDir = sourceDirPrefix[index];
				
				if (mypath.toString().contains(".DS_Store")) {
					return "";		
				}
				
				String targetDirtectory = mypath.getParent().toString().substring(mypath.toString().lastIndexOf(directoryToIndex));
				String sourceFile = mypath.toString().substring(mypath.toString().lastIndexOf(directoryToIndex));
				String sourceFieStr = "     <source-file src=\"%s\" target-dir=\"%s\"/>";
				if (sourceFile.endsWith(".m")) {
					if( mypath.toFile().getAbsolutePath().indexOf(File.separator + "library" + File.separator) != -1) {
						sourceFieStr = "     <source-file src=\"%s\" target-dir=\"%s\" compiler-flags=\"-fno-objc-arc\"/>";
					} else {
						sourceFieStr = "     <source-file src=\"%s\" target-dir=\"%s\" compiler-flags=\"-fobjc-arc-exceptions\"/>";						
					}
				}
				if (sourceFile.endsWith(".h")) {
					sourceFieStr = "     <header-file src=\"%s\" target-dir=\"%s\"/>";
				}
				String pluginStr = String.format(sourceFieStr,
						sourceDirPrefixxDir + sourceFile.replace('\\', '/'), targetDirPrefixDir + targetDirtectory.replace('\\', '/'));
				
				return pluginStr;
			}).collect(Collectors.toList());
			return prefixes;
		}).forEach(list -> {
			String data = list.stream().collect(Collectors.joining("\n"));
			int index = indexClassPaths.getAndIncrement();
			String directoryToIndex = paths[index] + "/";
			try {
				directoryToIndex = directoryToIndex.substring(0, directoryToIndex.lastIndexOf("/" + srcDir[index]));
			} catch (Exception e) {
				
			}
			
			File pluginXml = new File(directoryToIndex + "/plugin.xml");
			while(!pluginXml.exists()) {
				if (pluginXml.getParentFile() == null || pluginXml.getParentFile().getParentFile() == null) {
					System.out.println("ignored " + pluginXml);
					break;
				}
				pluginXml = new File(pluginXml.getParentFile().getParentFile().getAbsolutePath() + "/plugin.xml");

			}

//			System.out.println(data);
			try {
				String replaceStr = srcDir[index].replace(File.separator.charAt(0), '#');
				writeOrUpdateXmlFile(String.format("<!-- start %s -->\n%s\n     <!-- %s end -->", replaceStr, data, replaceStr), pluginXml.getAbsolutePath(), false, new java.util.HashMap<>(), 
						replaceStr);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		});
	}

	private static Stream<Path> getFileStream(String path) {
		try {
			return Files.walk(Paths.get(path), FileVisitOption.FOLLOW_LINKS).sorted();
		} catch (Exception e) {
			return Stream.empty();
		}
	}
}
