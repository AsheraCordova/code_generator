//start - license
/*
 * Copyright (c) 2025 Ashera Cordova
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
//end - license
package com.ashera.codegen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import com.ashera.codegen.pojo.CustomAttribute;
import com.ashera.codegen.pojo.QuirkAttribute;
import com.ashera.codegen.pojo.QuirkReportDto;
import com.ashera.codegen.pojo.QuirkWidget;
import com.ashera.codegen.pojo.Widget;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class CodeGenFromHtml extends CodeGenBase {
	public final static boolean LOG_TIME = true;
	public final static boolean EXPERRIMENTAL_PARALLEL_EXEC = false;
	public static void generateCode(String projectBaseDir) throws Exception {
		long t0 = System.currentTimeMillis();
		if (LOG_TIME) {
			System.out.println(" start " + new java.util.Date());
		}
		trustAllHttps();
	    ArrayList<String> activities = new ArrayList<>();
	    ArrayList<String> layoutFiles = new ArrayList<>();

		QuirkReportDto quirkReportDto = new QuirkReportDto();
		java.util.Map<String, com.ashera.codegen.pojo.Widget> widgetMap = new java.util.HashMap<>();
		List<String> localWidgets = null;
		System.setProperty("basedir", projectBaseDir.endsWith("/") ? projectBaseDir : projectBaseDir + File.separator);
		generateCode(projectBaseDir, widgetMap, quirkReportDto, localWidgets, activities, layoutFiles);
		generateTestLayoutFiles(projectBaseDir + "tests/", activities, layoutFiles);
		generateQuirkReport(quirkReportDto, localWidgets);
		
		IOFileFilter fileFilter =   FileFilterUtils.asFileFilter(new java.io.FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return !pathname.getName().equals("packagetemplates");
			}
			
		});
		Collection<File> files =  FileUtils.listFiles(new File("quirkreport"),TrueFileFilter.INSTANCE , fileFilter);
		for (File file : files) {
			String relativePath = file.getAbsolutePath().substring(file.getAbsolutePath().indexOf(File.separator + "quirkreport" + File.separator) );
			if (!relativePath.endsWith(".ftl")) {
				FileUtils.copyFile(file, new File(projectBaseDir + relativePath));
			}
		}

		FileUtils.deleteDirectory(new File(projectBaseDir  + "/doc/"));
		move(new File(projectBaseDir  + "/quirkreport/doc/"), projectBaseDir  + "/doc/");
		FileUtils.deleteDirectory(new File(projectBaseDir  + "/quirkreport/"));
		
		if (LOG_TIME) {
			System.out.println(" end " + new java.util.Date() + " " + (System.currentTimeMillis()- t0));
		}
	}

	private static void generateCode(String projectBaseDir,
			java.util.Map<String, com.ashera.codegen.pojo.Widget> widgetMap, QuirkReportDto quirkReportDto,
			List<String> localWidgets, ArrayList<String> activities, ArrayList<String> layoutFiles) throws IOException, Exception {
		for (String name : listFilesUsingJavaIO(projectBaseDir + "/codepoacher/config")) {
			if (name.startsWith("config-")) {
				String[] folders = new String[] { "android", "swt", "ios", "browser" };
				String[] envs = new String[] { "android", "swt", "ios", "web" };
				String[] paths = new String[envs.length];
				String trimmedName = name.replace("config-", "").replace(".xml", "");
				if (DEBUG) {
					System.out.println(trimmedName);
				}

				for (int i = 0; i < folders.length; i++) {
					try(Stream<Path> stream = Files.find(Paths.get(projectBaseDir + folders[i]), 100, (path, attr) -> path.getFileName().toString().equalsIgnoreCase(trimmedName + "plugin.java"))) {
						paths[i] = stream.findAny().get().toFile().getAbsolutePath();
						if (DEBUG) {
							System.out.println(paths[i]);
						}
					}
				}

				startCodeGen(quirkReportDto, trimmedName, projectBaseDir + "/codepoacher/config/" + name, envs, paths, projectBaseDir + "android", localWidgets, widgetMap, projectBaseDir + "tests/", activities, layoutFiles);
			}
		}
	}
	
	public static void move(File sourceFile, String targetFileName) throws Exception {
	    Path sourcePath = sourceFile.toPath();
	    Path targetPath = Paths.get(targetFileName);
	    File file = targetPath.toFile();
	    if(file.isFile()){
	        Files.delete(targetPath);
	    }
	    Files.move(sourcePath, targetPath);
	}

	public static void main(String[] args) throws Exception {
		long t0 = System.currentTimeMillis();
		trustAllHttps();
		if (args == null) {
			args = new String[0];
		}
		List<String> argList = Arrays.asList(args);
		List<String> localWidgets = null;
		if (argList.contains("-runOnly")) {
			int index = argList.indexOf("-runOnly");
			localWidgets = Arrays.asList(argList.get(index + 1).split(","));
		}

	    ArrayList<String> activities = new ArrayList<>();
	    ArrayList<String> layoutFiles = new ArrayList<>();
		QuirkReportDto quirkReportDto = new QuirkReportDto();
		java.util.Map<String, com.ashera.codegen.pojo.Widget> widgetMap = new java.util.HashMap<>();
		String testDir = "../../ashera-demo-projects/ashera-phonegap-demo-project/ashera-demo/";
		startCodeGen(quirkReportDto, "layout", "configimpl/config.xml", new String[] { "android", "swt", "ios", "web" },
				new String[] {
						"../../core-android-widget/AndroidLayouts/src/com/ashera/layout/AndroidLayoutsCordovaPlugin.java",
						"../../core-javafx-widget/javafx_widget_library/src/main/java/com/ashera/layout/AndroidLayoutsCordovaPlugin.java",
						"../../core-ios-widgets/ios_widget_library/src/main/java/com/ashera/layout/LayoutPlugin.java",
						"../../core-web-widget/web-widget-library/src/main/java/com/ashera/layout/LayoutPlugin.java" },

				"../../core-android-widget/AndroidLayouts/", localWidgets, widgetMap, testDir, activities, layoutFiles);
		generateCode("D:/Java/github_ashera/AbsoluteLayout/", widgetMap, quirkReportDto, localWidgets, activities, layoutFiles);

		// generate dynamically
		String[] folderHints = new String[] { "javafx", "android", "ios", "web" };
		String[] envs = new String[] { "swt", "android", "ios", "web" };
		String[] folderMainDir = new String[] { "main/java/", "", "main/java/", "main/java/" };
		String[] widgets = new String[] { "widget", "widget", "widgets", "widget" };
		String[] projectPrefixes = new String[] { "SWT", "Android", "IOS", "Web" };

		for (String name : listFilesUsingJavaIO("plugininfo/codegenhtml")) {
			Properties properties;
			properties = new Properties();
			properties.load(new FileInputStream("plugininfo/codegenhtml/" + name));
			String plugin = properties.getProperty("test");
			String[] strArr = plugin.split("\\:");
			String id = strArr[0];
			String pluginName = strArr[1];

			String[] paths = new String[envs.length];

			for (int i = 0; i < envs.length; i++) {
				paths[i] = String.format("../../core-%s-%s/%s%s/src/%scom/ashera/%s/%s.java", folderHints[i],
						widgets[i], projectPrefixes[i], pluginName, folderMainDir[i], id, pluginName);
			}
			startCodeGen(quirkReportDto, id, String.format("configimpl/config-%s.xml", id), envs, paths,
					String.format("../../core-android-widget/Android%s/", pluginName), localWidgets, widgetMap, testDir, activities, layoutFiles);

		}

//        startCodeGen(quirkReportDto, "recycleview", "configimpl/config-recycleview.xml", new String[]{"android"},
//                new String[] {
//                        "../../core-android-widget/AndroidRecycleViewPlugin/src/com/ashera/recycleview/RecycleViewPlugin.java"},
//               "../../core-android-widget/AndroidRecycleViewPlugin/", localWidgets, widgetMap);

		startCodeGen(quirkReportDto, "iqkeyboardmanager", "configimpl/config-iqkeyboardmanager.xml",
				new String[] { "ios" },
				new String[] {
						"../../core-ios-widgets/IOSIQkeyboardManagerPlugin/src/main/java/com/ashera/iqkeyboardmanager/IQkeyboardManagerPlugin.java" },
				"../../core-ios-widgets/IOSIQkeyboardManagerPlugin/", localWidgets, widgetMap, testDir, activities, layoutFiles);

		startCodeGen(quirkReportDto, "cssborder", "configimpl/config-border.xml", new String[] { "android", "swt" },
				new String[] {
						"../../core-android-widget/AndroidCSSBorderPlugin/src/com/ashera/cssborder/BorderCordovaPlugin.java",
						"../../core-javafx-widget/SWTCSSBorderPlugin/src/main/java/com/ashera/cssborder/BorderCordovaPlugin.java" },
				"../../core-android-widget/AndroidCSSBorderPlugin/", localWidgets, widgetMap, testDir, activities, layoutFiles);

		startCodeGen(quirkReportDto, "constraintlayout", "configimpl/config-constraintlayout.xml",
				new String[] { "android", "swt", "ios", "web" },
				new String[] {
						"../../core-android-widget/AndroidXConstraintLayout/src/com/ashera/constraintlayout/AndroidXConstraintLayout.java",
						"../../core-javafx-widget/SWTAndroidXConstraintLayoutPlugin/src/main/java/com/ashera/constraintlayout/AndroidXConstraintLayout.java",
						"../../core-ios-widgets/IOSAndroidXConstraintLayoutPlugin/src/main/java/com/ashera/constraintlayout/ConstraintLayoutPlugin.java",
						"../../core-web-widget/WebAndroidXConstraintLayoutPlugin/src/main/java/com/ashera/constraintlayout/AndroidXConstraintLayoutPlugin.java" },
				"../../core-android-widget/AndroidXConstraintLayout/", localWidgets, widgetMap, testDir, activities, layoutFiles);

		startCodeGen(quirkReportDto, "gridlayout", "configimpl/config-gridlayout.xml",
				new String[] { "android", "swt", "ios", "web" },
				new String[] {
						"../../core-android-widget/AndroidXGridLayoutPlugin/src/com/ashera/gridlayout/AndroidXGridLayoutPlugin.java",
						"../../core-javafx-widget/SWTAndroidXGridlayoutPlugin/src/main/java/com/ashera/gridlayout/AndroidXGridLayoutPlugin.java",
						"../../core-ios-widgets/IOSAndroidXGridlayoutPlugin/src/main/java/com/ashera/gridlayout/GridLayoutPlugin.java",
						"../../core-web-widget/WebAndroidXGridlayoutPlugin/src/main/java/com/ashera/gridlayout/AndroidXGridlayoutPlugin.java" },
				"../../core-android-widget/AndroidXGridLayoutPlugin/", localWidgets, widgetMap, testDir, activities, layoutFiles);

		
		generateTestLayoutFiles(testDir, activities, layoutFiles);
		generateQuirkReport(quirkReportDto, localWidgets);
		
		if (LOG_TIME) {
			System.out.println(" end " + new java.util.Date() + " " + (System.currentTimeMillis()- t0));
		}

	}

	static <T> T[] concatWithArrayCopy(T[] array1, T[] array2) {
		T[] result = Arrays.copyOf(array1, array1.length + array2.length);
		System.arraycopy(array2, 0, result, array1.length, array2.length);
		return result;
	}

	private static void startCodeGen(QuirkReportDto quirkReportDto, String packageName, String templateName,
			String[] env, String[] paths, String baseDir, List<String> localWidgets,
			java.util.Map<String, com.ashera.codegen.pojo.Widget> widgetMap, String testDir, ArrayList<String> activities, ArrayList<String> layoutFiles) throws Exception {

		if (!new File(templateName).exists()) {
			return;
		}

		if (LOG_TIME) {
			System.out.println(templateName + " -> start " + new java.util.Date());
		}
		// read xml file
		com.ashera.codegen.pojo.Widgets widgets = getWidgets(templateName);
		long t0 = System.currentTimeMillis();

		Map<String, List<Widget>> processedWidgets = new HashMap<>();
		Widget[] mywidgets = widgets.getWidget();
		if (mywidgets != null) {
			if (EXPERRIMENTAL_PARALLEL_EXEC) {
				// 1. Initialise the engine with a max constraint of 10 threads
				WidgetProcessingEngine engine = new WidgetProcessingEngine(widgetMap, 10);
				
				// 2. Queue the unmarshalled array dynamically
				engine.submitWidgets(mywidgets);
				
				// 3. Kick off the parallel engine block
				engine.executePipeline((widget) -> {
					// Lambda block captures your original method context safely across worker threads
					processWidget(quirkReportDto, packageName, baseDir, localWidgets, widgetMap, testDir, widget, processedWidgets, activities, layoutFiles);
				});
			} else {
				for (com.ashera.codegen.pojo.Widget widget : mywidgets) {
					long t1 = System.currentTimeMillis();
					if (LOG_TIME) {
						System.out.println(widget.getName()+widget.getOs() + " -> start " + new java.util.Date());
					}
					processWidget(quirkReportDto, packageName, baseDir, localWidgets, widgetMap, testDir, widget, processedWidgets, activities, layoutFiles);
					if (LOG_TIME) {
						System.out.println( widget.getName()+widget.getOs() + " -> end " + new java.util.Date() + " " + (System.currentTimeMillis()- t1));
					}
				}
			}
		}
		
		if (LOG_TIME) {
			System.out.println( templateName + " -> end " + new java.util.Date() + " " + (System.currentTimeMillis()- t0));
		}

		// post process
		postProcessWidgets(env, paths, localWidgets, testDir, widgets, processedWidgets);
	}
	

    private static void generateTestLayoutFiles(String testDir, ArrayList<String> activities, ArrayList<String> layoutFiles) throws IOException, FileNotFoundException, TemplateException {
        String pathname = testDir
        		+ "platforms/android/app/src/main/tsc/src/FragmentMapper.ts";
        Template template = new Template("name", new FileReader(new File("templates/FragmentMapper.ts")),
                new Configuration());
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> models = new HashMap<>();
        models.put("activities", activities);
        models.put("layoutFiles", layoutFiles);

        template.process(models, stringWriter);
        stringWriter.flush();
        String string = stringWriter.toString();
        writeOrUpdateFile(string, pathname, new HashMap<>());
        
        
        pathname = testDir
        		+ "platforms/android/app/src/main/tsc/src/HomeActivity.ts";
        template = new Template("name", new FileReader(new File("templates/HomeActivity.ts")),
                new Configuration());
        stringWriter = new StringWriter();
        template.process(models, stringWriter);
        stringWriter.flush();
        string = stringWriter.toString();
        writeOrUpdateFile(string, pathname, new HashMap<>());
    }
    

    private static void generateLayoutsPlugin(String[] env,  String[] paths, Map<String, List<Widget>> processedWidgets) throws IOException{
    	if (DEBUG) {
    		System.out.println(processedWidgets);
    	}
        for (int i = 0; i < env.length; i++) {
            StringBuffer stringBuffer = new StringBuffer("//start - widgets\n");
            List<Widget> widgets = processedWidgets.get(env[i]);
            if (widgets != null) {
                for (Widget widget : widgets) {
                	String pathName = widget.getClassname();
                    if (!(pathName.equals("ViewImpl") || pathName.equals("ViewGroupImpl") || pathName.equals("ViewGroupModelImpl"))) {
                    	if (widget.isViewplugin()) {
                    		stringBuffer.append("        com.ashera.widget.WidgetFactory.registerAttributableFor(\"" + widget.getViewPluginFor()  + "\", " +
                    				 "new " + widget.getAdditionalAttributes().get("widgetFullPackage") + "());\n");
                    	} else {
                    		stringBuffer.append("        WidgetFactory.register(new " + widget.getAdditionalAttributes().get("widgetFullPackage") + "());\n");	
                    	}
                    }                            
                }
                
                stringBuffer.append("        //end - widgets\n");
                
                writeOrUpdateFile(stringBuffer.toString(), paths[i], false, "widgets");
            }
        }
        
        processedWidgets.clear();
    }

	private static void postProcessWidgets(String[] env, String[] paths, List<String> localWidgets, String testDir,
			com.ashera.codegen.pojo.Widgets widgets, Map<String, List<Widget>> processedWidgets) throws IOException, FileNotFoundException, TemplateException {
		if (localWidgets == null) {
			generateLayoutsPlugin(env, paths, processedWidgets);
		}

		if (widgets.getCode() != null) {
			for (com.ashera.codegen.pojo.Code code : widgets.getCode()) {
				String fileContent = readFileToString(new java.io.File(code.getUrl()));
				if (code.getCopy() != null) {
					for (com.ashera.codegen.pojo.Copy copy : code.getCopy()) {
						if (copy.getReplaceString() != null) {
							for (com.ashera.codegen.pojo.ReplaceString replaceString : copy.getReplaceString()) {
								fileContent = fileContent.replaceAll(replaceString.getName(),
										replaceString.getReplace());
							}
						}
					}
				}
				writeOrUpdateFile(fileContent, code.getToUrl(), true, new String[0]);

			}
		}
	}

	private static com.ashera.codegen.pojo.Widgets getWidgets(String templateName)
			throws JAXBException, FileNotFoundException {
		javax.xml.bind.JAXBContext jaxbContext = javax.xml.bind.JAXBContext
				.newInstance(com.ashera.codegen.pojo.Widgets.class);
		javax.xml.bind.Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		com.ashera.codegen.pojo.Widgets widgets = (com.ashera.codegen.pojo.Widgets) unmarshaller
				.unmarshal(new java.io.FileInputStream(templateName));

		if (widgets.getRef() != null) {
			for (com.ashera.codegen.pojo.Ref ref : widgets.getRef()) {
				com.ashera.codegen.pojo.Widgets refwidgets = (com.ashera.codegen.pojo.Widgets) unmarshaller
						.unmarshal(new java.io.FileInputStream(ref.getRef()));

				widgets.setWidget(concatWithArrayCopy(widgets.getWidget(), refwidgets.getWidget()));
			}
		}
		return widgets;
	}

	private static void processWidget(QuirkReportDto quirkReportDto, String packageName, String baseDir,
			List<String> localWidgets, java.util.Map<String, com.ashera.codegen.pojo.Widget> widgetMap, String testDir,
			com.ashera.codegen.pojo.Widget widget, Map<String, List<Widget>> processedWidgets, ArrayList<String> activities, ArrayList<String> layoutFiles) throws Exception {
		if (widget.getParentWidget() != null) {
			com.ashera.codegen.pojo.Widget parentWidget = widgetMap
					.get(widget.getParentWidget() + widget.getOs());
			java.util.ArrayList<com.ashera.codegen.pojo.CustomAttribute> customAttributes = new java.util.ArrayList<>();
			java.util.ArrayList<com.ashera.codegen.pojo.CustomAttribute> protocustomAttributes = new java.util.ArrayList<>();

			if (widget.getPrototypeCustomAttribute() != null) {
				protocustomAttributes.addAll(java.util.Arrays.asList(widget.getPrototypeCustomAttribute()));
			}

			if (parentWidget.getPrototypeCustomAttribute() != null) {
				List<CustomAttribute> parentCustomAttributes = java.util.Arrays
						.asList(parentWidget.getPrototypeCustomAttribute());
				protocustomAttributes.addAll(parentCustomAttributes);
			}

			if (widget.getCustomAttribute() != null) {
				customAttributes.addAll(widget.getCustomAttribute());
			}
			if (parentWidget.getCustomAttribute() != null) {
				List<CustomAttribute> parentCustomAttributes = parentWidget.getCustomAttribute();
				for (CustomAttribute customAttribute : parentCustomAttributes) {
					if (customAttribute.isInherited()) {
						com.ashera.codegen.templates.CodeGenTemplate.updateFromProtoCustomAttribute(
								parentWidget, customAttribute, customAttribute.getName());
						customAttributes.add(customAttribute);
					}
				}

				String ignoreParentCustomAttributes = widget.getIgnoreParentCustomAttributes();
				if (ignoreParentCustomAttributes != null) {
					List<String> list = java.util.Arrays.asList(ignoreParentCustomAttributes.split(","));
					customAttributes = new java.util.ArrayList<>(customAttributes.stream()
							.filter((customAttribute) -> !list.contains(customAttribute.getName()))
							.collect(Collectors.toList()));
				}
			}

			widget.setCustomAttribute(customAttributes);

			java.util.ArrayList<com.ashera.codegen.pojo.CustomAttribute> copyAttributes = new java.util.ArrayList<>();
			if (widget.getCopyAttribute() != null) {
				copyAttributes.addAll(java.util.Arrays.asList(widget.getCopyAttribute()));
			}
			if (parentWidget.getCopyAttribute() != null) {
				copyAttributes.addAll(java.util.Arrays.asList(parentWidget.getCopyAttribute()));
			}
			widget.setCopyAttribute(copyAttributes.toArray(new com.ashera.codegen.pojo.CopyAttribute[0]));

			widget.setPrototypeCustomAttribute(
					protocustomAttributes.toArray(new com.ashera.codegen.pojo.CustomAttribute[0]));
			if (widget.isCopyParentCode()) {
				com.ashera.codegen.pojo.Code[] result = new com.ashera.codegen.pojo.Code[widget.getCode().length
						+ parentWidget.getCode().length]; // resultant array of size first array and second
															// array
				System.arraycopy(widget.getCode(), 0, result, 0, widget.getCode().length);
				System.arraycopy(parentWidget.getCode(), 0, result, widget.getCode().length,
						parentWidget.getCode().length);
				widget.setCode(result);
			}

			java.util.ArrayList<com.ashera.codegen.pojo.Generator> generatorsAttributes = new java.util.ArrayList<>();
			if (parentWidget.getGenerator() != null) {
				generatorsAttributes.addAll(java.util.Arrays.asList(parentWidget.getGenerator()));
			}
			if (widget.getGenerator() != null) {
				generatorsAttributes.addAll(java.util.Arrays.asList(widget.getGenerator()));
			}

			widget.setGenerator(generatorsAttributes.toArray(new com.ashera.codegen.pojo.Generator[0]));
		}

		if (localWidgets == null || localWidgets.contains(widget.getName())) {
			if (widget.getOs().equals("android")) {
				new com.ashera.codegen.templates.AndroidCodeGenTemplate(quirkReportDto, packageName,
						widget.getOs(), widget.getPrefix(), baseDir, testDir, processedWidgets, activities, layoutFiles).startCodeGeneration(widget);
			}
			if (widget.getOs().equals("swt")) {
				new com.ashera.codegen.templates.SwtCodeGenerator(quirkReportDto, packageName, widget.getOs(),
						widget.getPrefix(), testDir, processedWidgets, activities, layoutFiles).startCodeGeneration(widget);
			}

			if (widget.getOs().equals("ios")) {
				new com.ashera.codegen.templates.IosCodeGenTemplate(quirkReportDto, packageName, widget.getOs(),
						widget.getPrefix(), testDir, processedWidgets, activities, layoutFiles).startCodeGeneration(widget);
			}

			if (widget.getOs().equals("web")) {
				new com.ashera.codegen.templates.WebCodeGenTemplate(quirkReportDto, packageName, widget.getOs(),
						widget.getPrefix(), testDir, processedWidgets, activities, layoutFiles).startCodeGeneration(widget);
			}
			
			widgetMap.put(widget.getName() + widget.getOs(), widget);
		
			if (/*widget.getOs().equals("android") && */widget.getXmlConfigs() != null && widget.getXmlConfigs().length > 0) {
				HashMap<String, List<String>> methodNamesMap = new HashMap<>();					
				for (com.ashera.codegen.pojo.XmlConfig xmlConfig : widget.getXmlConfigs()) {
					Map<CustomAttribute, String> customAttributeMap = new HashMap<>();
					if ( xmlConfig.getXmlConfigParams() != null) {
						for (com.ashera.codegen.pojo.XmlConfigParam xmlConfigParam : xmlConfig.getXmlConfigParams()) {
							if (xmlConfigParam.getType().equals("customattribute")) {
								List<CustomAttribute> customAttributes = widgetMap.get(xmlConfigParam.getClassName() + widget.getOs()).getAllAttributes();
								
								for (CustomAttribute customAttribute : customAttributes) {
									customAttributeMap.put(customAttribute, xmlConfigParam.getParamName());
								}
							}
						}
					}
					
					XmlResourceCodeGenerator.generate(widget, customAttributeMap, xmlConfig, methodNamesMap);
				}
			}
		} else {
			if (DEBUG) {
				System.out.println("ignored " + widget.getName() + " " + localWidgets);
			}
		}
		
		widgetMap.put(widget.getName() + widget.getOs(), widget);
	}

	private static void generateQuirkReport(QuirkReportDto quirkReportDto, List<String> localWidgets) {
		String[] templates = new String[] { "quirkreport/doc/allclasses-frame.ftl",
				"quirkreport/doc/allclasses-noframe.ftl", "quirkreport/doc/overview-frame.ftl",
				"quirkreport/doc/overview-summary.ftl" };
		String[] htmls = new String[] { "quirkreport/doc/allclasses-frame.html",
				"quirkreport/doc/allclasses-noframe.html", "quirkreport/doc/overview-frame.html",
				"quirkreport/doc/overview-summary.html" };

		Map<String, Object> params = new HashMap<>();
		params.put("report", quirkReportDto);
		if (localWidgets == null) {
			generateHtmlDoc(params, templates, htmls);
			generatePackageLevelDoc(quirkReportDto);
		}
		generateWidgetDoc(quirkReportDto);

	}

	private static void generateWidgetDoc(QuirkReportDto quirkReportDto) {

		Map<String, Map<String, List<QuirkWidget>>> widgetMap = quirkReportDto.getWidgetMap();
		widgetMap.keySet().forEach(packageName -> {
			Map<String, Object> params = new HashMap<>();
			String packageDir = packageName.replaceAll("\\.", "/");
			params.put("packageName", packageName);
			params.put("packageDir", packageDir);
			long count = packageName.chars().filter(num -> num == '.').count();
			String relativeDir = "";
			for (int j = 0; j < count + 1; j++) {
				relativeDir += "../";
			}
			params.put("relativeDir", relativeDir);
			params.put("colors", new String[] { "#DCDCDC", "#D3D3D3", "#C0C0C0", "#A9A9A9", "#808080", "#696969",
					"#778899", "#708090", "#2F4F4F" });

			Map<String, List<QuirkWidget>> widgetGroup = widgetMap.get(packageName);

			widgetGroup.keySet().forEach(groupName -> {
				params.put("group", groupName);
				List<QuirkWidget> quirkWidgets = widgetGroup.get(groupName);
				List<QuirkAttribute> quirkAttributes = quirkWidgets.stream().map(obj -> obj.getAttributes())
						.flatMap(Collection::stream).collect(Collectors.toList());
				Set<QuirkAttribute> quirkAttributeSet = new java.util.TreeSet<>(
						java.util.Comparator.comparing(QuirkAttribute::getAttributeName));
				quirkAttributeSet.addAll(quirkAttributes);
				params.put("attributesMap",
						new ArrayList<>(getAttributesGroupedByGeneratorUrl(quirkAttributeSet).entrySet()));
				params.put("widgets", quirkWidgets);

				Map<String, QuirkAttribute> quirkAttributeMap = new HashMap<>();
				quirkWidgets.forEach((quirkWidget) -> {
					quirkWidget.getAttributes().forEach((obj) -> {
						quirkAttributeMap.put(
								quirkWidget.getName() + quirkWidget.getNativeClassName() + obj.getAttributeName(), obj);
					});
				});
				params.put("attributeMap", quirkAttributeMap);
				generateHtmlDoc(params, "quirkreport/doc/" + packageDir + "/" + groupName + ".html",
						"quirkreport/doc/packagetemplates/widget.ftl");

			});
		});
	}

	public static Map<String, Set<QuirkAttribute>> getAttributesGroupedByGeneratorUrl(Set<QuirkAttribute> attributes) {
		TreeMap<String, Set<QuirkAttribute>> map = new TreeMap<>(new java.util.Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				if (o1.indexOf("android") != -1 && o2.indexOf("android") == -1) {
					return -1;
				}
				if (o1.indexOf("android") == -1 && o2.indexOf("android") != -1) {
					return 1;
				}

				if (o1.indexOf("help-") != -1 && o2.indexOf("help-") == -1) {
					return -1;
				}
				if (o1.indexOf("help-") == -1 && o2.indexOf("help-") != -1) {
					return 1;
				}

				return o1.compareTo(o2);
			}

		});

		for (QuirkAttribute quirkAttribute : attributes) {
			String key = quirkAttribute.getGeneratorUrl();
			if (!map.containsKey(key)) {
				Set<QuirkAttribute> quirkAttributeSet = new java.util.TreeSet<>(
						java.util.Comparator.comparing(QuirkAttribute::getAttributeName));

				map.put(key, quirkAttributeSet);
			}
			map.get(key).add(quirkAttribute);
		}

		return map;
	}

	private static void generateHtmlDoc(Map<String, Object> params, String[] templates, String[] htmls) {
		for (int i = 0; i < htmls.length; i++) {
			String pathname = htmls[i];
			String templateName = templates[i];
			generateHtmlDoc(params, pathname, templateName);
		}
	}

	private static void generateHtmlDoc(Map<String, Object> params, String pathname, String templateName) {
		StringWriter stringWriter = new StringWriter();
		try {
			// Load template from source folder

			Template template = new Template("name", new FileReader(new File(templateName)), new Configuration());

			// Console output
			stringWriter = new StringWriter();
			template.process(params, stringWriter);
			stringWriter.flush();
			String projectBaseDir = System.getProperty("basedir");
			if (projectBaseDir == null) {
				projectBaseDir = "";
			}
			writeToFile(new File(projectBaseDir + pathname), stringWriter.toString());
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (TemplateException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				stringWriter.close();
			} catch (IOException e) {
			}
		}
	}

	private static void generatePackageLevelDoc(QuirkReportDto quirkReportDto) {
		//package level generation of doc
		quirkReportDto.getWidgetMap().keySet().forEach((packageName) -> {
			String[] templates = new String[] { "quirkreport/doc/packagetemplates/package-frame.ftl",
					"quirkreport/doc/packagetemplates/package-summary.ftl" };
			String[] htmls = new String[] { "package-frame.html", "package-summary.html" };

			for (int i = 0; i < htmls.length; i++) {
				Map<String, Object> params = new HashMap<>();
				String packageDir = packageName.replaceAll("\\.", "/");
				params.put("packageName", packageName);
				params.put("packageDir", packageDir);
				long count = packageName.chars().filter(num -> num == '.').count();
				String relativeDir = "";
				for (int j = 0; j < count + 1; j++) {
					relativeDir += "../";
				}

				params.put("relativeDir", relativeDir);
				params.put("widgets", quirkReportDto.getWidgetsInPackage(packageName));

				new File("quirkreport/doc/" + packageDir).mkdirs();
				generateHtmlDoc(params, "quirkreport/doc/" + packageDir + "/" + htmls[i], templates[i]);
			}
		});
	}

	private static void trustAllHttps() throws Exception {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		} };

		// Install the all-trusting trust manager
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

		// Create all-trusting host name verifier
		HostnameVerifier allHostsValid = new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};

		// Install the all-trusting host verifier
		HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	}
}
