package com.ashera.codegen;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CopyFilesToDemoGen extends CodeGenBase{
	static java.util.Map<String, String> iosMap = new java.util.HashMap<>();
	static {
		iosMap.put("core-ios-widgets/IOSAndroidXConstraintLayoutPlugin/ios", "../../../../ios/${iosProjectName}/Plugins/com.ashera.constraintlayout.ios/ConstraintLayoutPlugin/ios");
		iosMap.put("core-ios-widgets/IOSAndroidXConstraintLayoutPlugin/library", "../../../../ios/${iosProjectName}/Plugins/com.ashera.constraintlayout.ios/ConstraintLayoutPlugin");
		iosMap.put("core-ios-widgets/IOSAndroidXGridlayoutPlugin/ios", "../../../../ios/${iosProjectName}/Plugins/com.ashera.gridlayout.ios/GridLayoutPlugin/ios");
		iosMap.put("core-ios-widgets/IOSAndroidXGridlayoutPlugin/library", "../../../../ios/${iosProjectName}/Plugins/com.ashera.gridlayout.ios/GridLayoutPlugin");

		iosMap.put("core-ios-widgets/IOSConverter/ios", "../../../../ios/${iosProjectName}/Plugins/com.ashera.converter.ios/ConverterPlugin/ios");
		iosMap.put("core-ios-widgets/IOSConverter/library", "../../../../ios/${iosProjectName}/Plugins/com.ashera.converter.ios/ConverterPlugin");

		iosMap.put("core-ios-widgets/IOSCorePlugin/ios", "../../../../ios/${iosProjectName}/Plugins/com.ashera.core.ios/CorePlugin/ios");
		iosMap.put("core-ios-widgets/IOSCorePlugin/library", "../../../../ios/${iosProjectName}/Plugins/com.ashera.core.ios/CorePlugin");
		
		iosMap.put("core-ios-widgets/IOSJSONAdapter/ios", "../../../../ios/${iosProjectName}/Plugins/com.ashera.json.ios/JSONCore/ios");
		
//		iosMap.put("core-ios-widgets/IOSNavigatorPlugin/ios", "../../../../ios/${iosProjectName}/Plugins/com.ashera.navigation.ios/NavigatorPlugin/ios");
//		iosMap.put("core-ios-widgets/IOSNavigatorPlugin/library", "../../../../ios/${iosProjectName}/Plugins/com.ashera.navigation.ios/NavigatorPlugin");

		iosMap.put("core-ios-widgets/ios_widget_library/ios", "../../../../ios/${iosProjectName}/Plugins/com.ashera.layout.ios/LayoutPlugin/ios");
		iosMap.put("core-ios-widgets/ios_widget_library/ios_ext", "../../../../ios/${iosProjectName}/Plugins/com.ashera.layout.ios/LayoutPlugin/ios_ext");
		iosMap.put("core-ios-widgets/ios_widget_library/library", "../../../../ios/${iosProjectName}/Plugins/com.ashera.layout.ios/LayoutPlugin");
		
		iosMap.put("core-ios-widgets/IOSCSSBorderPlugin/ios", "../../../../ios/${iosProjectName}/Plugins/com.ashera.cssborder.ios/CSSBorderPlugin/ios");
		iosMap.put("core-ios-widgets/IOSCapInsetsPlugin/ios", "../../../../ios/${iosProjectName}/Plugins/com.ashera.capinsets.ios/CapInsetsPlugin/ios");
		iosMap.put("core-ios-widgets/IOSIQkeyboardManagerPlugin/ios", "../../../../ios/${iosProjectName}/Plugins/com.ashera.iqkeyboardmanager.ios/IQkeyboardManagerPlugin/ios");
		
		iosMap.put("core-ios-widgets/IOSNavigationDrawerPlugin/ios", "../../../../ios/${iosProjectName}/Plugins/com.ashera.drawerlayout.ios/NavigationDrawerPlugin/ios");
		iosMap.put("core-ios-widgets/IOSNavigationDrawerPlugin/library", "../../../../ios/${iosProjectName}/Plugins/com.ashera.drawerlayout.ios/AndroidXDrawerLayout");
		
		iosMap.put("core-ios-widgets/IOSRecycleViewPlugin/ios", "../../../../ios/${iosProjectName}/Plugins/com.ashera.recycleview.ios/RecycleViewPlugin/ios");
		iosMap.put("core-ios-widgets/IOSRecycleViewPlugin/library", "../../../../ios/${iosProjectName}/Plugins/com.ashera.recycleview.ios/AndroidXRecyclerView");
		
		iosMap.put("core-ios-widgets/IOSToolbarPlugin/ios", "../../../../ios/${iosProjectName}/Plugins/com.ashera.toolbar.ios/ToolbarPlugin/ios");
		iosMap.put("core-ios-widgets/IOSToolbarPlugin/library", "../../../../ios/${iosProjectName}/Plugins/com.ashera.toolbar.ios/AndroidToolbarPlugin");
		
		iosMap.put("core-ios-widgets/IOSDateTimePlugin/ios", "../../../../ios/${iosProjectName}/Plugins/com.ashera.datetime.ios/DateTimePlugin/ios");
		iosMap.put("core-ios-widgets/IOSFreeMarkerPlugin/ios", "../../../../ios/${iosProjectName}/Plugins/com.ashera.freemarker.ios/FreeMarkerPlugin/ios");
		iosMap.put("core-ios-widgets/IOSFreeMarkerPlugin/library", "../../../../ios/${iosProjectName}/Plugins/com.ashera.freemarker.ios/CorePlugin");
		iosMap.put("core-ios-widgets/IOSShutterBugPlugin/ios", "../../../../ios/${iosProjectName}/Plugins/com.ashera.shutterbug.ios/IOSShutterBugPlugin/ios");
		iosMap.put("core-ios-widgets/IOSSDWebImagePlugin/ios", "../../../../ios/${iosProjectName}/Plugins/com.ashera.sdwebimage.ios/IOSSDWebImagePlugin/ios");
		iosMap.put("core-ios-widgets/IOSCustomWidgetExtension/ios", "../../../../ios/${iosProjectName}/Plugins/com.ashera.customwidgetextension.ios/CustomWidgetExtension/ios");
		
		iosMap.put("core-ios-widgets/IOSCoordinatorLayoutPlugin/ios", "../../../../ios/${iosProjectName}/Plugins/com.ashera.coordinatorlayout.ios/CoordinatorLayout/ios");
		iosMap.put("core-ios-widgets/IOSCoordinatorLayoutPlugin/library", "../../../../ios/${iosProjectName}/Plugins/com.ashera.coordinatorlayout.ios/CoordinatorLayout/AndroidXCoordinatorLayout");
		
		iosMap.put("core-ios-widgets/IOSSnackbarPlugin/ios", "../../../../ios/${iosProjectName}/Plugins/com.ashera.snackbar.ios/Snackbar/ios");
		iosMap.put("core-ios-widgets/IOSSnackbarPlugin/library", "../../../../ios/${iosProjectName}/Plugins/com.ashera.snackbar.ios/Snackbar");

		iosMap.put("core-ios-widgets/IOSViewPagerPlugin/ios", "../../../../ios/${iosProjectName}/Plugins/com.ashera.viewpager.ios/ViewPager/ios");
		
		iosMap.put("core-ios-widgets/IOSTextInputLayoutPlugin/ios", "../../../../ios/${iosProjectName}/Plugins/com.ashera.textinputlayout.ios/TextInputLayout/ios");
		iosMap.put("core-widget_library/CustomPlugin/ios", "../../../../ios/${iosProjectName}/Plugins/com.ashera.custom/CustomPlugin/ios");
		

	}

	

	public static void main(String[] args) throws IOException {
		List<File> classPaths = getAbsPath(new String[] { "../../core-android-widget", "../../core-widget_library"} );
		String finalResult = classPaths.stream().filter((f) -> f.isDirectory() && new File(f.getAbsolutePath(), "plugin.xml").exists()).sorted((a, b) -> a.getName().compareTo(b.getName())).map((f) -> {
			String [] subdirectorPaths = {"src", "res", "tsc", "gentool"};
			String [] copyPaths = {"java", "res", "tsc", "gentool"};
			String finalTeplate = generateAntString(f, subdirectorPaths, copyPaths, false);
			return finalTeplate;
		}).collect(Collectors.joining(""));
		writeOrUpdateXmlFile(String.format("<!-- start %s -->\n%s\n     <!-- %s end -->", "copy", finalResult, "copy"), "build.xml", false, new java.util.HashMap<>(), "copy");
		
		classPaths = getAbsPath(new String[] { "../../core-javafx-widget", "../../core-ios-widgets", "../../core-web-widget"} );
		finalResult = "";
		
		finalResult = classPaths.stream().filter((f) -> f.isDirectory()).sorted((a, b) -> a.getName().compareTo(b.getName())).map((f) -> {
			String [] subdirectorPaths = {"tsc"};
			String [] copyPaths = {"tsc"};
			String finalTeplate = generateAntString(f, subdirectorPaths, copyPaths, false);
			return finalTeplate;
		}).collect(Collectors.joining(""));
		writeOrUpdateXmlFile(String.format("<!-- start %s -->\n%s\n     <!-- %s end -->", "javafx", finalResult, "javafx"), "build.xml", false, new java.util.HashMap<>(), "javafx");

		
		classPaths = getAbsPath(new String[] { "../../core-ios-widgets", "../../core-widget_library"} );
		
		finalResult = classPaths.stream().filter((f) -> f.isDirectory()).sorted((a, b) -> a.getName().compareTo(b.getName())).map((f) -> {
			String [] subdirectorPaths = {"ios", "ios_ext", "library"};
			String [] copyPaths = {"../../../../ios/${iosProjectName}/Plugins", "../../../../ios/${iosProjectName}/Plugins", "../../../../ios/${iosProjectName}/Plugins"};
			String finalTeplate = generateAntString(f, subdirectorPaths, copyPaths, true);
			return finalTeplate;
		}).collect(Collectors.joining(""));
		writeOrUpdateXmlFile(String.format("<!-- start %s -->\n%s\n     <!-- %s end -->", "ios", finalResult, "ios"), "build.xml", false, new java.util.HashMap<>(), "ios");

	}

	private static List<File> getAbsPath(String[] paths) {
		List<File> classPaths = Arrays.stream(paths).map(path -> {
			return Arrays.asList(new File(path).listFiles());
		}).flatMap((list) -> list.stream()).collect(Collectors.toList());
		return classPaths;
	}

	private static String generateAntString(File f, String[] subdirectorPaths, String[] copyPaths, boolean useMap) {
		String finalTeplate = "";
		int i = 0;
		try {
			for (String subdirectorPath : subdirectorPaths) {
				String pathname = f.getAbsolutePath() + "/" + subdirectorPath;
				File file = new File(pathname);
				if (file.exists()) {
					String sampleFileName = null;
					// find first file in the directory to check before copy
					Object[] path = Files.walk(Paths.get(pathname)).filter(Files::isRegularFile).filter((p) -> (p + "").indexOf(".DS_Store") == -1 && (p + "").indexOf("applidium") == -1).toArray();
					if (path.length > 0) {
						String firstFile = path[0] + "";
						sampleFileName = firstFile.substring(firstFile.lastIndexOf(subdirectorPath)).replaceAll("\\\\", "/").substring(subdirectorPath.length() + 1);
						String overrite = "overwrite";
						if (copyPaths[i].equals("res")) {
							overrite = "overwriteres";
						}
						String template = 
								"		<if>\n" +
					            "		<available file=\"${demoDir}/%s\" type=\"file\" />\n" +
					            "		<then>\n" +
					            "    		<echo>Copied %s</echo>\n" +
								"			<copy todir=\"${demoDir}/%s\" overwrite=\"${%s}\">  \n" + 
								"  				<fileset dir=\"../../%s\" includes=\"**\"/>  \n" + 
								"			</copy>\n" +
								"		</then>\n" +
								"		<else>\n" +
								"    		<echo>Ignored %s</echo>\n" +
								"		</else>\n" +
					            "		</if>\n";
						String finalPath = f.getParentFile().getName() + "/" + f.getName() + "/" + subdirectorPath;
						
						if (finalPath.toLowerCase().contains("freemarker")) {
							continue;
						}
						String copyPathStr = copyPaths[i];
						if (useMap) {
							copyPathStr = iosMap.get(finalPath);
							if (copyPathStr == null) {
								throw new RuntimeException(finalPath);
//								continue;
							}
						}
						sampleFileName = copyPathStr + "/" + sampleFileName;
						if (sampleFileName.indexOf("java/main/java/") != -1) {
							sampleFileName = sampleFileName.replace("java/main/java/", "java/");
							finalPath += "/main/java";
						}
						finalTeplate +=  String.format(template, sampleFileName, sampleFileName, copyPathStr, overrite, finalPath, sampleFileName);
					} else {
						System.out.println("ignored" + f.getName() + "/" + subdirectorPath);
					}
				}
				i++;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return finalTeplate;
	}
}
