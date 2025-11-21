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

import com.ashera.codegen.pojo.CustomAttribute;
import com.ashera.codegen.pojo.Generator;
import com.ashera.codegen.pojo.QuirkReportDto;
import com.ashera.codegen.pojo.Widget;
public class WebCodeGenTemplate extends CodeGenTemplate{

		public WebCodeGenTemplate(QuirkReportDto quirkReportDto, String packageName, String environment, String prefix, String testDir) {
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
			return "Web" + url.substring(url.lastIndexOf("/") + 1) + ".html";
		}

		@Override
		public void setSetterMethod(Widget widget, CustomAttribute element, Properties widgetProperties, String url) {
			
		}

		@Override
		public List<CustomAttribute> getNodeElements(String generatorUrl, Document doc, Widget widget, Properties widgetProperties) {
			List<CustomAttribute> nodeElements = new ArrayList<>();
			return nodeElements;
		}


		@Override
		public String getJavaFileLocation(Widget configuration) {
		    
			String baseDir = "../../core-web-widget/web-widget-library";
			
			if (configuration.getBaseDir() != null) {
			    baseDir = configuration.getBaseDir();
			}
			System.out.println("baseDir" + baseDir);
            return baseDir + "/src/main/java/com/ashera/" + packageName + "/" + configuration.getWidgetName() + ".java";
		}

		@Override
		public String getTsFileLocation(Widget configuration) {
			String baseDir = "../../core-web-widget/web-widget-library";
            
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