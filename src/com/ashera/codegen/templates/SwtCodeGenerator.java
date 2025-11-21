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
package com.ashera.codegen.templates;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ashera.codegen.pojo.CustomAttribute;
import com.ashera.codegen.pojo.Generator;
import com.ashera.codegen.pojo.QuirkReportDto;
import com.ashera.codegen.pojo.Widget;
public class SwtCodeGenerator extends CodeGenTemplate{

		public SwtCodeGenerator(QuirkReportDto quirkReportDto, String packageName, String environment, String prefix, String testDir) {
			super(quirkReportDto, testDir, packageName, environment, prefix);
		}

		@Override
		public String getPackagePrefix() {
			return "r.";
		}

		@Override
		public void addMethodDefinitions(Generator generator, Widget widget,
				CustomAttribute customAttribute,
				Properties widgetProperties) {
		}

		@Override
		public String getFileName(String url) {
			return "JavaFX" + url.substring(url.lastIndexOf("/") + 1) + ".html";
		}

		@Override
		public void setSetterMethod(Widget widget, CustomAttribute element, Properties widgetProperties, String url) {
			String methodName = element.getCode();
			String widgetPackage = null;
			
			if (url.indexOf("api/") != -1) {
				widgetPackage = url.substring(url.lastIndexOf("api/")).replace('/', '.').replaceFirst("\\.html", "").replaceFirst("api\\.", "");
			}
			
			String varName = "$var";
			if (widgetProperties.containsKey(widgetPackage + ".var")) {
				varName = widgetProperties.getProperty(widgetPackage + ".var", "");
			}
			String javaType = element.getVarType();
			if (widgetProperties.containsKey("javafx.datatype." + element.getVarType())) {
				String[] property = widgetProperties.getProperty("javafx.datatype." + element.getVarType()).split(":");
				javaType = property[1];
			}
			String methodSubStr = methodName.substring(0, methodName.indexOf("("));
			String setterMethod = methodSubStr +"(" +"(" + javaType  +")" + "objValue);";
			element.setCode("code: "  + varName + "." + "" +setterMethod);
			
			String getterCode = element.getGetterCode();
			if (getterCode != null) {
				if (!getterCode.startsWith("code")) {
					element.setGetterCode(getterCode + "()");
				}
				// System.out.println(classConfiguration.getAttribute() + " " +
				// classConfiguration.getGetterMethod());

			}
			
		}

		@Override
		public List<CustomAttribute> getNodeElements(String generatorUrl, Document doc, Widget widget, Properties widgetProperties) {
			Elements elements = doc.select("code");
			List<CustomAttribute> nodeElements = new ArrayList<>();
			
			for (int i = 0; i < elements.size(); i++) {
				Element element = elements.get(i);
				String text = element.text();

				if (text.startsWith("set") && text.indexOf("(") != -1) {					
					String attributeName = text.substring(3, 4).toLowerCase() + text.substring(4, text.indexOf("(")).trim();
					String[] methodParmeters = text.substring(text.indexOf("(") + 1).trim().replace(")", "").split(" ");
					String varType = methodParmeters[0];
					if (methodParmeters.length == 2) {
						String methodName = text;
						
						CustomAttribute nodeElement = new CustomAttribute();
						nodeElement.setGeneratorUrl(generatorUrl);
						nodeElement.setName("swt" + attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1));
						nodeElement.setVarType(varType);					
						nodeElement.setApiLevel("1");
						nodeElement.setCode(methodName);
						nodeElements.add(nodeElement);
						
						String disposableStr = (String) widgetProperties.get("swt.disposables");
						List<String> disposables = java.util.Arrays.asList(disposableStr.split(","));
						if (disposables.contains(nodeElement.getVarType())) {
							nodeElement.setDisposable(true);
						}
						
						String apiLevelForGet = "";
						if (nodeElement.getGetterCode() == null && nodeElement.getCode().startsWith("set")) {
							int index = nodeElement.getCode().length();
							if (nodeElement.getCode().indexOf("(") != -1) {
								index = nodeElement.getCode().indexOf("(");
							}

							String getterMethod = "g" + nodeElement.getCode().substring(1, index);
							apiLevelForGet = setGetterMethodOnNode(doc, nodeElement, getterMethod);

							if (nodeElement.getGetterCode() == null && nodeElement.getVarType().equals("boolean")) {
								getterMethod = "is" + nodeElement.getCode().substring(3, index);
								apiLevelForGet = setGetterMethodOnNode(doc, nodeElement, getterMethod);
							}

						}

						String getMethodHint = widgetProperties.getProperty(attributeName + ".getMethodHint");

						if (getMethodHint != null) {
							nodeElement.setGetterCode(getMethodHint);
						}
						if (nodeElement.getGetterCode() == null) {
							System.out.println("Getter method not found (0) : " + text + " " + apiLevelForGet);
						}
					}
				}
			}
			return nodeElements;
		}

		private String setGetterMethodOnNode(Document doc, CustomAttribute nodeElement, String getterMethod) {
			String apiLevelElement = "";
			Elements getterMethodElements = doc.select("h4:contains(" + getterMethod + ")");
			if (getterMethodElements.size() > 0) {
				for (Element element2 : getterMethodElements) {
					if (element2.text().equals(getterMethod)) {
						nodeElement.setGetterCode(getterMethod);
						apiLevelElement = "1";
						break;
					}
				}
			}
			return apiLevelElement;
		}

		@Override
		public String getJavaFileLocation(Widget configuration) {
		    
			String baseDir = "../../core-javafx-widget/javafx_widget_library";
			
			if (configuration.getBaseDir() != null) {
			    baseDir = configuration.getBaseDir();
			}
			System.out.println("baseDir" + baseDir);
            return baseDir + "/src/main/java/com/ashera/" + packageName + "/" + configuration.getWidgetName() + ".java";
		}

		@Override
		public String getTsFileLocation(Widget configuration) {
		    String baseDir = "../../core-javafx-widget/javafx_widget_library";
            
            if (configuration.getBaseDir() != null) {
                baseDir = configuration.getBaseDir();
            }
			return baseDir + "/tsc/src/" + environment + "/widget/" + configuration.getWidgetName() + ".ts";
		}

		@Override
		public String getAttrFileLocation(Widget configuration) {
			return null;
		}
		
		
	}