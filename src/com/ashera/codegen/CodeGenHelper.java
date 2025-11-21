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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import com.ashera.codegen.pojo.CustomAttribute;
import com.ashera.codegen.pojo.SourceConfig;
import com.ashera.codegen.pojo.WidgetConfig;

public class CodeGenHelper {

	public static String getAttributeWithoutNameSpace(String text) {
		return text.substring(text.indexOf(":") + 1).trim();
	}
	
	public static String getNameSpace(String text) {
		if (text.indexOf(":") == -1) {
			return "app";
		}
		return text.substring(0, text.indexOf(":")).trim();
	}
	

	public static boolean isAndroidAttribute(String text) {
		return text.startsWith("android:") || text.startsWith("android.support.v7.appcompat:")
				|| text.startsWith("android.support.design:") || text.startsWith("app:");
	}


	public static String getMethodName(String methodName) {
		methodName = methodName.substring(0, methodName.indexOf("(")).replaceAll("public ", "").replaceAll("void ", "").replaceAll("boolean ", "").replaceAll("abstract ", "").trim();
		if (methodName.contains(" ")) {
			methodName = methodName.split("\\s")[1];
		}
		return methodName;
	}


	public static boolean isNotSupportedApiLevel(String apiLevelElement) {
		return apiLevelElement.toLowerCase().indexOf("developer preview") != -1 || apiLevelElement.toLowerCase().indexOf("deprecated in api") != -1 || apiLevelElement.toLowerCase().indexOf("level 29") != -1;
	}

	public static boolean isApiElement(String apiLevelElement) {
		return apiLevelElement.toLowerCase().indexOf("added in api level ") != -1;
	}
	

	public static String getSetMethodFromAttr(String var) {
		return "set" +var.substring(0, 1).toUpperCase() + var.substring(1);
	}


	public static String getApiInt(String apiLevelElement) {
		return apiLevelElement.toLowerCase().replace("added in api level ", "").trim();
	}
	

	public static String[] getMethodParams(String methodSignature) {
		String methodParamStr = methodSignature.substring(methodSignature.indexOf("(") + 1, methodSignature.indexOf(")"));
		String[] methodParams = methodParamStr.split(",");
		int i = 0;
		for (String methodParam : methodParams) {
			methodParams[i] = methodParam.trim().split("\\s")[0]; 
			i++;
		}
		return methodParams;
	}
	
	
	public static String getMethodReturnType(String methodSignature) {
		String[] methodReturn = methodSignature.split("\\s");
		if (methodSignature.contains("abstract")) {
			return methodReturn[2];
		} else {
			return methodReturn[1];
		}
	}
	public static String[] getMethodVars(String methodSignature) {
		String methodParamStr = methodSignature.substring(methodSignature.indexOf("(") + 1, methodSignature.indexOf(")"));
		String[] methodParams = methodParamStr.split(",");
		int i = 0;
		for (String methodParam : methodParams) {
			String[] str = methodParam.trim().split("\\s");
			if (str.length > 1) {
				methodParams[i] = str[1]; 
				i++;
			}
		}
		return methodParams;
	}
	
	public static String getAttributeNameFromChangeListener(String methodName) {
		String temp = methodName.replaceAll("Listener", "").replaceAll("Changed", "Change");
		temp = temp.substring(0, 1).toLowerCase() + temp.substring(1);
		if (temp.equals("onScroll")) {
			temp = "onScrollChange";
		}
		return temp;
	}
	
	public static String getCacheFileName(WidgetConfig widgetConfig, SourceConfig sourceConfig) {
		String id = sourceConfig.getId();
		return getCacheFileName(widgetConfig, sourceConfig, id);
	}

	public static String getCacheFileName(WidgetConfig widgetConfig, SourceConfig sourceConfig, String id) {
		return widgetConfig.getName() +"/" + widgetConfig.getName() + id +"."+ sourceConfig.getType();
	}

	static Properties widgetProperties = new Properties();
	static {
		try {
			widgetProperties.load(new FileInputStream("resources/datatype.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean canIgnoreAttribute(String os, CustomAttribute customAttribute) {
		String ignorelist = widgetProperties.getProperty(os + ".ignore");
		boolean ignore = false;
		if (ignorelist != null) {
			ignore = Arrays.asList(ignorelist.split(",")).contains(customAttribute.getName());
			if (ignore) {
				System.out.println(customAttribute.getName());
			}
		}
		return ignore;
		
	}
	public static void setTypeOnCustomAttribute(String os, CustomAttribute customAttribute, String javaType) {
		String key = os + ".datatype." + javaType;
		String name = customAttribute.getName().toLowerCase();
		
		String indexprops = widgetProperties.getProperty(os + ".indexprops");
		if (indexprops != null) {
			String[] arr = indexprops.split(",");
			for (String indexprop : arr) {
				if (name.indexOf(indexprop) != -1) {
					name = indexprop;
					break;
				}
			}
		}
		
		String keyWithprop = os + ".datatype." + name;
		if (widgetProperties.containsKey(keyWithprop)) {
			customAttribute.setType(widgetProperties.getProperty(keyWithprop));
		} else if (widgetProperties.containsKey(key)) {
			customAttribute.setType(widgetProperties.getProperty(key));
		}
	}

}
