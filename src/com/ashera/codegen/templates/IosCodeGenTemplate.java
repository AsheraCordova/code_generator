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

public class IosCodeGenTemplate extends CodeGenTemplate {
	public IosCodeGenTemplate(QuirkReportDto quirkReportDto, String packageName, String environment, String prefix, String testDir) {
		super(quirkReportDto, testDir, packageName, environment, prefix);
	}

	@Override
	public String getPackagePrefix() {
		return "r.";
	}

	@Override
	public void addMethodDefinitions(Generator generator, Widget widget, CustomAttribute element, Properties widgetProperties) {
		if (element.getActualAttributeName() != null) {
			String iosObject = widgetProperties
					.getProperty(widget.getLocalName() + "." + environment + ".widget");
			String attributeName = element.getActualAttributeName();
			String useName = widgetProperties.getProperty(attributeName + ".useName");
			String readonly = widgetProperties.getProperty(element.getAttribute() + ".readOnly");

			if (attributeName.startsWith("is") && (useName == null || !useName.equals("true"))) {
				attributeName = attributeName.substring(2);
				attributeName = attributeName.substring(0, 1).toLowerCase() + attributeName.substring(1);
			}
			if (widget.getClassName().equals("UIView")) {
				if (readonly == null || !readonly.equals("true")) {
					widget.addMethodDefition("public static native void " + element.getCode() + "(Object nativeWidget, "
							+ "Object value)/*-[\n" + "((UIView*" + ") nativeWidget)." + attributeName + " = "
							+ generateCode(element) + ";\n" + "]-*/;");
				}
				
				widget.addMethodDefition("public static native Object " + element.getGetterCode() + "(Object uiView)/*-[\n" + "return " + generateCode(element, "((UIView*" + ") uiView)." + attributeName)
						+ ";\n  ]-*/;");
			} else {
				if (readonly == null || !readonly.equals("true")) {
					String nativeWidget = "((" + iosObject + "*" + ") nativeWidget).";
					if (generator != null && generator.getNativeclassname() != null && generator.getNativeclassVar() != null) {
						nativeWidget = "((" + generator.getNativeclassname() + "*" + ") " + generator.getNativeclassVar() + ").";
					}
					widget.addMethodDefition("public native void " + element.getCode() + "(Object nativeWidget, "
							+ "Object value)/*-[\n" + nativeWidget + attributeName + " = "
							+ generateCode(element) + ";\n" + "]-*/;");
				}
				String getterVar = "((" + iosObject + "*" + ") uiView_).";
				if (generator != null && generator.getNativeclassname() != null && generator.getNativeclassVar() != null) {
					getterVar = "((" + generator.getNativeclassname() + "*" + ") " + generator.getNativeclassVar() + ").";
				}
				widget.addMethodDefition("public native Object " + element.getGetterCode() + "()/*-[\n" + "return " + generateCode(element, getterVar + attributeName)
						+ ";\n  ]-*/;");
			}
		}
	}

	@Override
	public String getFileName(String url) {
		return "IOS" + url.substring(url.lastIndexOf("/") + 1) + ".html";
	}

	@Override
	public void setSetterMethod(Widget classConfiguration, CustomAttribute element, Properties widgetProperties,
			String url) {
		element.setCode("code:" + element.getCode() + "(nativeWidget, objValue" + ");");
		
		if (classConfiguration.getClassName().equals("UIView")) {
			element.setGetterCode("code:" + element.getGetterCode() + "(nativeWidget)");
		} else {
			element.setGetterCode("code:" + element.getGetterCode() + "()");
		}
	}

	@Override
	public List<CustomAttribute> getNodeElements(String generatorUrl, Document doc, Widget widget, Properties widgetProperties) {
		List<CustomAttribute> nodeElements = new ArrayList<>();
		Elements elements = doc.select("code");
		
		for (int i = 0; i < elements.size(); i++) {
			Element element = elements.get(i);
			String text = element.text();

			if (text.startsWith("var")) {
				text = text.replace("var", "");
				String attributeName = text.substring(0, text.indexOf(":")).trim();
				String name = attributeName.toLowerCase().substring(0, attributeName.toLowerCase().length() - 1);


				

				String varType = text.substring(text.indexOf(":") + 1).replace("?", "").replace("!", "").trim();
				CustomAttribute nodeElement = new CustomAttribute();
				nodeElement.setGeneratorUrl(generatorUrl);
				nodeElement.setVarType(varType);
				String camelCaseName = attributeName.substring(0, 1).toUpperCase() + attributeName.substring(1);
				nodeElement.setName("ios" + camelCaseName);
				nodeElement.setActualAttributeName(attributeName);
				nodeElement.setApiLevel("1");
				nodeElement.setCode("set" + camelCaseName);
				nodeElement.setGetterCode("get" + camelCaseName);
				Object iosMinVersion = widgetProperties.get(nodeElement.getName() + ".iosMinVersion");
				if (iosMinVersion != null) {
					nodeElement.setIosMinVersion((String) iosMinVersion);
				}
				
				
				String converterInfo1 = "";
				String converterInfo2 = "";
				String seperator = "";
				for (int j = 0; j < elements.size(); j++) {
					Element enumArr = elements.get(j);
					String enumText = enumArr.text();
					int index = enumText.toLowerCase().indexOf(name);
					if (index > 0 && enumText.substring(0, index).equals("UI")) {
						converterInfo1 = converterInfo1 + seperator + enumText.toLowerCase();
						converterInfo2 = converterInfo2 + seperator + "LayoutNativeVars." + enumText;
						seperator = ",";
					}
					
				}
				if (!converterInfo1.equals("") && varType.startsWith("UI")) {
					nodeElement.setConverterInfo1(converterInfo1);
					nodeElement.setConverterInfo2(converterInfo2);
					nodeElement.setVarType("Int");
				}

				
				nodeElements.add(nodeElement);
			}
		}
		return nodeElements;
	}

	private String generateCode(CustomAttribute classConfiguration, String string) {
		if (classConfiguration.getNativeClassTypeForGetter() != null && classConfiguration.getNativeClassTypeForGetter().contains("%s")) {
			return String.format(classConfiguration.getNativeClassTypeForGetter(), ":" + string);
		}
		return string;
	}

	private static String generateCode(CustomAttribute classConfiguration) {
		if (classConfiguration.getNativeClassType().contains("%s")) {
			return String.format(classConfiguration.getNativeClassType(), "value");
		}
		return "(" + classConfiguration.getNativeClassType() + ") value";
	}



	@Override
	public String getJavaFileLocation(Widget configuration) {
		String baseDir = "../../core-ios-widgets/ios_widget_library";
		
		if (configuration.getBaseDir() != null) {
		    baseDir = configuration.getBaseDir();
		}
		System.out.println("baseDir" + baseDir);
        return baseDir + "/src/main/java/com/ashera/" + packageName + "/" + configuration.getWidgetName() + ".java";
	}

	@Override
	public String getTsFileLocation(Widget configuration) {
	    String baseDir = "../../core-ios-widgets/ios_widget_library";
        
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