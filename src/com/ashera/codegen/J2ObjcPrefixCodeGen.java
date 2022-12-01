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
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class J2ObjcPrefixCodeGen extends CodeGenBase {
	public static final List<String> pathsAndPredfixes = new java.util.ArrayList<>(Arrays.asList(new String[] {
			"../../core-widget_library/css_parser/src:CSS",
			"../../core-widget_library/html_parser/src:TS",
			"../../core-widget_library/widget_library/src:AS",
			"../../core-widget_library/Plugin_Converter/src:AS",
			"../../core-widget_library/Plugin_HtmlParser/src:AS",
			"../../core-javafx-widget/SWTAndroid/src/main/java:AD",
			"../../core-javafx-widget/SWTAndroid/src/main/stub:AD",
			"../../core-javafx-widget/SWTAndroidX-core/src/main/java:ADX",
			"../../core-javafx-widget/SWTAndroidX-Gridlayout/src/main/java:ADX",
			"../../core-javafx-widget/SWTAndroidXConstraintLayout/src/main/java:ADX",
			"../..//core-ios-widgets/IOSConverter/src/main/java:AS",
			"../../core-ios-widgets/IOSCorePlugin/src/main/java:AS",
			"../../core-ios-widgets/IOSJSONAdapter/src/main/java:AS",
			"../../core-ios-widgets/ios_widget_library/src/main/java:AS",
			"../../core-ios-widgets/IOSAndroidXGridlayoutPlugin/src/main/java:AS",
			"../../core-ios-widgets/IOSAndroidXConstraintLayoutPlugin/src/main/java:AS",
			"../../core-widget_library/freemarker/src/main/java:FM",
			"../../core-ios-widgets/IOSCSSBorderPlugin/src/main/java:AS",
			"../../core-ios-widgets/IOSIQkeyboardManagerPlugin/src/main/java:AS",
			"../../core-javafx-widget/SWTAndroidXDrawerLayout/src/main/java:ADX",
			"../../core-ios-widgets/IOSNavigationDrawerPlugin/src/main/java:AS",
			"../../core-ios-widgets/IOSRecycleViewPlugin/src/main/java:AS",
			"../../core-javafx-widget/SWTAndroidXRecyclerView/src/main/java:ADX",
			"../../core-javafx-widget/SWTAndroidToolBar/src/main/java:ADX",
			"../../core-ios-widgets/IOSToolbarPlugin/src/main/java:AS",
			"../../ashera-demo-projects/ashera-phonegap-demo-project/demoapp1/custom_plugins/CustomPlugin/src/main/java:AS",
			"D:/Java/github_ashera/AbsoluteLayout/ios/src/main/java:AS"
	}));
	
   
	public static String[] iosNS;
	public static String[] paths;
	static {
		try {
			for (String name : listFilesUsingJavaIO("plugininfo/j2objc")) {
				Properties properties;
				properties = new Properties();
				properties.load(new FileInputStream("plugininfo/j2objc/" + name));
				String plugin = properties.getProperty("test");
				pathsAndPredfixes.add(plugin);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> pathsList = new java.util.ArrayList<String>();
		List<String> iosNSList = new java.util.ArrayList<String>();
		
		for (String pathsAndPredfix : pathsAndPredfixes) {
			String[] pathWithDirAndPrefixSplit = pathsAndPredfix.split("\\:");
			pathsList.add(pathWithDirAndPrefixSplit[0]);
			iosNSList.add(pathWithDirAndPrefixSplit[1]);
		}
		paths = pathsList.toArray(new String[0]);
		iosNS = iosNSList.toArray(new String[0]); 
	}
	

	public static void main(String[] args) throws IOException {
		System.out.println(paths.length);
		System.out.println(iosNS.length);
		List<String> classPaths = Arrays.stream(paths).map(path -> {
			try {
				return new File(path).getCanonicalPath();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}).collect(Collectors.toList());
		AtomicInteger indexer = new AtomicInteger(); 
		List<String> allPrefixes = classPaths.stream().map((path) -> {
			int index = indexer.getAndIncrement();
			List<String> prefixes = getFileStream(path).filter((mypath) -> {
				return mypath.toFile().listFiles() != null && Arrays.stream(mypath.toFile().listFiles())
						.filter((myfile) -> !myfile.isDirectory() && !myfile.getName().equals(".DS_Store")).count() > 0;
			}).map(mypath -> {
				String inferPrefix =  iosNS[index];
				System.out.println(mypath.toFile().getAbsolutePath());
				String packageName = mypath.toFile().getAbsolutePath().replace(path, "").substring(1).replaceAll("/", ".").replaceAll("\\\\", ".");
				
				if (packageName.startsWith("com.ashera")) {
					inferPrefix = "AS";
				}
				
				if (packageName.startsWith("com.ashera") && packageName.endsWith("css")) {
					inferPrefix = "CSS";
				}
				if (packageName.startsWith("com.applidium")) {
					inferPrefix = "AP";
				}
				return packageName + "=" + inferPrefix;
			}).collect(Collectors.toList());
			return prefixes;
		}).flatMap(Collection::stream).collect(Collectors.toList());    
		classPaths.stream().map(classPath -> classPath.substring(0, classPath.indexOf(File.separator + "src"))).forEach(classPath -> {
			String classPathFile = classPath + "/"+ "." + new File(classPath).getName() + "-classpath";
			String prefixFile = classPath + "/"+ "." + new File(classPath).getName() + "-prefixes";
			System.out.println(prefixFile);
			
			try {
				Files.write(Paths.get(classPathFile), (Iterable<String>)classPaths.stream().map((value) -> (value + "=").replaceAll("\\\\", "/").replaceAll("\\:", "\\\\:"))::iterator);
				Files.write(Paths.get(prefixFile), (Iterable<String>)allPrefixes.stream()::iterator);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		

	}

	private static Stream<Path> getFileStream(String path) {
		try {
			return Files.walk(Paths.get(path), FileVisitOption.FOLLOW_LINKS);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
