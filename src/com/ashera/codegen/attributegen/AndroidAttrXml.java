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
package com.ashera.codegen.attributegen;

import java.io.Serializable;
import java.util.List;

import com.ashera.codegen.CodeGenBase;
import com.ashera.codegen.CodeGenHelper;
import com.ashera.codegen.pojo.CustomAttribute;
import com.ashera.codegen.pojo.SourceConfig;
import com.ashera.codegen.pojo.Widget;
import com.ashera.codegen.pojo.WidgetConfig;
import com.ashera.codegen.pojo.attrs.Resources;

public class AndroidAttrXml extends CodeGenBase {

	public void updateAttribute(WidgetConfig widgetConfig, SourceConfig sourceConfig, Widget widget) throws Exception {
		java.io.File file = readHttpUrlAsString(sourceConfig.getUrl(), CodeGenHelper.getCacheFileName(widgetConfig, sourceConfig));
		javax.xml.bind.JAXBContext jaxbContext = javax.xml.bind.JAXBContext
				.newInstance(Resources.class);
		javax.xml.bind.Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		Resources resources = (Resources) unmarshaller
				.unmarshal(new java.io.FileInputStream(file));
		for (Resources.DeclareStyleable styleable : resources.getDeclareStyleable()) {
			String widgetName = widget.getName().substring(widget.getName().lastIndexOf(".") + 1);

			if (styleable.getName().equals(widgetName + "_Layout") || (styleable.getName().equals(widgetName))) {
				for (Resources.Attr attr : styleable.getAttr()) {
					CustomAttribute attribute = new CustomAttribute();
					attribute.setName(attr.getName());
					CodeGenHelper.setTypeOnCustomAttribute("android", attribute, attr.getName());
					attribute.setGeneratorUrl(sourceConfig.getUrl());
					
					// update converterinfo and type
					setCustomAttrType(resources, attr, attribute, widgetName);
					if (!attribute.getName().startsWith("__")) {
						widget.addOrUpdateAttribute(attribute);
					}
				}
			}
		}

	
	}

	private void setCustomAttrType(Resources resources,
			Resources.Attr attr, CustomAttribute attribute,
			String widgetName) {
		String name = attr.getName();

		if (attribute.getName().equals("android:orientation")) {
			attribute.setType("orientation");
			attribute.setConverterInfo1("horizontal,vertical");
			attribute.setConverterInfo2("0,1");
		}

		updateAttributeInfo(attribute, attr);

		for (Resources.Attr attr1 : resources.getAttr()) {
			if (attr1.getName().equals(name)) {
				updateAttributeInfo(attribute, attr1);

			}
		}
	}

	private void updateAttributeInfo(CustomAttribute attribute,
			Resources.Attr attr) {
		String name = attr.getName();
		List<Serializable> content = attr.getContent();
		String format = attr.getFormat();
		
		CodeGenHelper.setTypeOnCustomAttribute("android", attribute, format);

		if (content != null && content.size() > 0 && !attribute.getName().equals("gravity")) {
			String converterInfo1 = "";
			String converterInfo2 = "";
			String seperator = "";
			for (java.io.Serializable element : content) {

				if (element instanceof javax.xml.bind.JAXBElement) {
					javax.xml.bind.JAXBElement jaxBElement = (javax.xml.bind.JAXBElement) element;

					Object enumOrFlag = jaxBElement.getValue();

					if (enumOrFlag instanceof Resources.Attr.Enum) {
						String enumName = ((Resources.Attr.Enum) enumOrFlag).getName();
						String enumValue = ((Resources.Attr.Enum) enumOrFlag)
								.getValueAttribute();
						converterInfo1 += seperator + enumName;
						converterInfo2 += seperator + enumValue;
						seperator = ",";
					}

					if (enumOrFlag instanceof Resources.Attr.Flag) {
						attribute.setSeperatedByBar(true);
						String flagName = ((Resources.Attr.Flag) enumOrFlag).getName();
						String flagValue = ((Resources.Attr.Flag) enumOrFlag)
								.getValueAttribute();

						converterInfo1 += seperator + flagName;
						converterInfo2 += seperator + flagValue;
						seperator = ",";
					}
				}
			}

			if (converterInfo1.indexOf(",") != -1) {
				attribute.setConverterInfo1(converterInfo1);
				attribute.setConverterInfo2(converterInfo2);
				CodeGenHelper.setTypeOnCustomAttribute("android", attribute, name);
			} else {
				if (converterInfo1.equals("parent")) {
					// TODO : make this generic
					attribute.setCode("code:$var." + attribute.getCode() + "((int) objValue);"
							+ "\n\t\t\t\t\t\t\tif (strValue.equals(\"parent\")) {\n" + "\t\t\t\t\t\t\t\tlayoutParams."
							+ attribute.getCode()
							+ " androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;\n"
							+ "\t\t\t\t\t\t\t}");
				}
			}
		}

	}

}
