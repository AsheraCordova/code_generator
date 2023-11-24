package com.ashera.codegen;

import java.io.File;
import java.io.FileInputStream;
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
	public static void generateCode(String projectBaseDir) throws Exception {
		trustAllHttps();
		QuirkReportDto quirkReportDto = new QuirkReportDto();
		java.util.Map<String, com.ashera.codegen.pojo.Widget> widgetMap = new java.util.HashMap<>();
		List<String> localWidgets = null;
		System.setProperty("basedir", projectBaseDir.endsWith("/") ? projectBaseDir : projectBaseDir + File.separator);
		generateCode(projectBaseDir, widgetMap, quirkReportDto, localWidgets);
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

	}

	private static void generateCode(String projectBaseDir,
			java.util.Map<String, com.ashera.codegen.pojo.Widget> widgetMap, QuirkReportDto quirkReportDto,
			List<String> localWidgets) throws IOException, Exception {
		for (String name : listFilesUsingJavaIO(projectBaseDir + "/codepoacher/config")) {
			if (name.startsWith("config-")) {
				String[] folders = new String[] { "android", "swt", "ios", "browser" };
				String[] envs = new String[] { "android", "swt", "ios", "web" };
				String[] paths = new String[envs.length];
				String trimmedName = name.replace("config-", "").replace(".xml", "");
				System.out.println(trimmedName);

				for (int i = 0; i < folders.length; i++) {
					try(Stream<Path> stream = Files.find(Paths.get(projectBaseDir + folders[i]), 100, (path, attr) -> path.getFileName().toString().equalsIgnoreCase(trimmedName + "plugin.java"))) {
						paths[i] = stream.findAny().get().toFile().getAbsolutePath();
						System.out.println(paths[i]);
					}
				}

				startCodeGen(quirkReportDto, trimmedName, projectBaseDir + "/codepoacher/config/" + name, envs, paths, projectBaseDir + "android", localWidgets, widgetMap, projectBaseDir + "tests/");
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

		QuirkReportDto quirkReportDto = new QuirkReportDto();
		java.util.Map<String, com.ashera.codegen.pojo.Widget> widgetMap = new java.util.HashMap<>();
		String testDir = "../../ashera-demo-projects/ashera-phonegap-demo-project/ashera-demo/";
		startCodeGen(quirkReportDto, "layout", "configimpl/config.xml", new String[] { "android", "swt", "ios", "web" },
				new String[] {
						"../../core-android-widget/AndroidLayouts/src/com/ashera/layout/AndroidLayoutsCordovaPlugin.java",
						"../../core-javafx-widget/javafx_widget_library/src/main/java/com/ashera/layout/AndroidLayoutsCordovaPlugin.java",
						"../../core-ios-widgets/ios_widget_library/src/main/java/com/ashera/layout/LayoutPlugin.java",
						"../../core-web-widget/web-widget-library/src/main/java/com/ashera/layout/LayoutPlugin.java" },

				"../../core-android-widget/AndroidLayouts/", localWidgets, widgetMap, testDir);
		generateCode("D:/Java/github_ashera/AbsoluteLayout/", widgetMap, quirkReportDto, localWidgets);

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
					String.format("../../core-android-widget/Android%s/", pluginName), localWidgets, widgetMap, testDir);

		}

//        startCodeGen(quirkReportDto, "recycleview", "configimpl/config-recycleview.xml", new String[]{"android"},
//                new String[] {
//                        "../../core-android-widget/AndroidRecycleViewPlugin/src/com/ashera/recycleview/RecycleViewPlugin.java"},
//               "../../core-android-widget/AndroidRecycleViewPlugin/", localWidgets, widgetMap);

		startCodeGen(quirkReportDto, "iqkeyboardmanager", "configimpl/config-iqkeyboardmanager.xml",
				new String[] { "ios" },
				new String[] {
						"../../core-ios-widgets/IOSIQkeyboardManagerPlugin/src/main/java/com/ashera/iqkeyboardmanager/IQkeyboardManagerPlugin.java" },
				"../../core-ios-widgets/IOSIQkeyboardManagerPlugin/", localWidgets, widgetMap, testDir);

		startCodeGen(quirkReportDto, "cssborder", "configimpl/config-border.xml", new String[] { "android", "swt" },
				new String[] {
						"../../core-android-widget/AndroidCSSBorderPlugin/src/com/ashera/cssborder/BorderCordovaPlugin.java",
						"../../core-javafx-widget/SWTCSSBorderPlugin/src/main/java/com/ashera/cssborder/BorderCordovaPlugin.java" },
				"../../core-android-widget/AndroidCSSBorderPlugin/", localWidgets, widgetMap, testDir);

		startCodeGen(quirkReportDto, "constraintlayout", "configimpl/config-constraintlayout.xml",
				new String[] { "android", "swt", "ios", "web" },
				new String[] {
						"../../core-android-widget/AndroidXConstraintLayout/src/com/ashera/constraintlayout/AndroidXConstraintLayout.java",
						"../../core-javafx-widget/SWTAndroidXConstraintLayoutPlugin/src/main/java/com/ashera/constraintlayout/AndroidXConstraintLayout.java",
						"../../core-ios-widgets/IOSAndroidXConstraintLayoutPlugin/src/main/java/com/ashera/constraintlayout/ConstraintLayoutPlugin.java",
						"../../core-web-widget/WebAndroidXConstraintLayoutPlugin/src/main/java/com/ashera/constraintlayout/AndroidXConstraintLayoutPlugin.java" },
				"../../core-android-widget/AndroidXConstraintLayout/", localWidgets, widgetMap, testDir);

		startCodeGen(quirkReportDto, "gridlayout", "configimpl/config-gridlayout.xml",
				new String[] { "android", "swt", "ios", "web" },
				new String[] {
						"../../core-android-widget/AndroidXGridLayoutPlugin/src/com/ashera/gridlayout/AndroidXGridLayoutPlugin.java",
						"../../core-javafx-widget/SWTAndroidXGridlayoutPlugin/src/main/java/com/ashera/gridlayout/AndroidXGridLayoutPlugin.java",
						"../../core-ios-widgets/IOSAndroidXGridlayoutPlugin/src/main/java/com/ashera/gridlayout/GridLayoutPlugin.java",
						"../../core-web-widget/WebAndroidXGridlayoutPlugin/src/main/java/com/ashera/gridlayout/AndroidXGridlayoutPlugin.java" },
				"../../core-android-widget/AndroidXGridLayoutPlugin/", localWidgets, widgetMap, testDir);

		
		generateQuirkReport(quirkReportDto, localWidgets);
	}

	static <T> T[] concatWithArrayCopy(T[] array1, T[] array2) {
		T[] result = Arrays.copyOf(array1, array1.length + array2.length);
		System.arraycopy(array2, 0, result, array1.length, array2.length);
		return result;
	}

	private static void startCodeGen(QuirkReportDto quirkReportDto, String packageName, String templateName,
			String[] env, String[] paths, String baseDir, List<String> localWidgets,
			java.util.Map<String, com.ashera.codegen.pojo.Widget> widgetMap, String testDir) throws Exception {
		javax.xml.bind.JAXBContext jaxbContext = javax.xml.bind.JAXBContext
				.newInstance(com.ashera.codegen.pojo.Widgets.class);
		javax.xml.bind.Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		if (!new File(templateName).exists()) {
			return;
		}

		com.ashera.codegen.pojo.Widgets widgets = (com.ashera.codegen.pojo.Widgets) unmarshaller
				.unmarshal(new java.io.FileInputStream(templateName));

		if (widgets.getRef() != null) {
			for (com.ashera.codegen.pojo.Ref ref : widgets.getRef()) {
				com.ashera.codegen.pojo.Widgets refwidgets = (com.ashera.codegen.pojo.Widgets) unmarshaller
						.unmarshal(new java.io.FileInputStream(ref.getRef()));

				widgets.setWidget(concatWithArrayCopy(widgets.getWidget(), refwidgets.getWidget()));
			}
		}

		Widget[] mywidgets = widgets.getWidget();
		if (mywidgets != null) {
			for (com.ashera.codegen.pojo.Widget widget : mywidgets) {

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
								widget.getOs(), widget.getPrefix(), baseDir, testDir).startCodeGeneration(widget);
					}
					if (widget.getOs().equals("swt")) {
						new com.ashera.codegen.templates.SwtCodeGenerator(quirkReportDto, packageName, widget.getOs(),
								widget.getPrefix(), testDir).startCodeGeneration(widget);
					}

					if (widget.getOs().equals("ios")) {
						new com.ashera.codegen.templates.IosCodeGenTemplate(quirkReportDto, packageName, widget.getOs(),
								widget.getPrefix(), testDir).startCodeGeneration(widget);
					}

					if (widget.getOs().equals("web")) {
						new com.ashera.codegen.templates.WebCodeGenTemplate(quirkReportDto, packageName, widget.getOs(),
								widget.getPrefix(), testDir).startCodeGeneration(widget);
					}
				} else {
					System.out.println("ignored " + widget.getName() + " " + localWidgets);
				}
				widgetMap.put(widget.getName() + widget.getOs(), widget);
				
				/*if (widget.getOs().equals("android") && widget.getName().equals("androidx.constraintlayout.motion.widget.MotionLayout")) {
					List<CustomAttribute> customAttributes = new ArrayList<>();
					customAttributes.addAll(widgetMap.get("androidx.constraintlayout.widget.Barrierandroid").getAllAttributes());
					customAttributes.addAll(widgetMap.get("ViewGroupandroid").getAllAttributes());
					customAttributes.addAll(widget.getAllAttributes());
					MotionSceneCodeGenerator.generate(customAttributes);
				}*/
				if (widget.getOs().equals("android") && widget.getXmlConfigs() != null && widget.getXmlConfigs().length > 0) {
					HashMap<String, List<String>> methodNamesMap = new HashMap<>();					
					for (com.ashera.codegen.pojo.XmlConfig xmlConfig : widget.getXmlConfigs()) {
						Map<CustomAttribute, String> customAttributeMap = new HashMap<>();
						for (com.ashera.codegen.pojo.XmlConfigParam xmlConfigParam : xmlConfig.getXmlConfigParams()) {
							if (xmlConfigParam.getType().equals("customattribute")) {
								List<CustomAttribute> customAttributes = widgetMap.get(xmlConfigParam.getClassName() + "android").getAllAttributes();
								
								for (CustomAttribute customAttribute : customAttributes) {
									customAttributeMap.put(customAttribute, xmlConfigParam.getParamName());
								}
							}
						}
						
						XmlResourceCodeGenerator.generate(widget, customAttributeMap, xmlConfig, methodNamesMap);
					}
				}
			}
		}

		if (localWidgets == null) {
			com.ashera.codegen.templates.CodeGenTemplate.generateLayoutsPlugin(env, paths);
			com.ashera.codegen.templates.CodeGenTemplate.generateTestLayoutFiles(testDir);
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
		// package level generation of doc
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
