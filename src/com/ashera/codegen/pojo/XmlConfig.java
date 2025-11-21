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
package com.ashera.codegen.pojo;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class XmlConfig {
	private String updateLocation;
	private String xml;
	private String cachekey;
	private String ignoreRegEx;
	private String tag;
	private XmlConfigParam[] xmlConfigParams = new XmlConfigParam[0];
	private ReplaceString[] replaceString;
	private String generateCustomSetter;
	private String def;
	private String setValueHint;
	
	public String getSetValueHint() {
		return setValueHint;
	}

	@XmlAttribute(name="setValueHint")
	public void setSetValueHint(String setValueHint) {
		this.setValueHint = setValueHint;
	}

	public String getDef() {
		return def;
	}

	@XmlAttribute(name="def")
	public void setDef(String def) {
		this.def = def;
	}

	public String getGenerateCustomSetter() {
		return generateCustomSetter;
	}

	@XmlAttribute(name="generateCustomSetter")
	public void setGenerateCustomSetter(String generateCustomSetter) {
		this.generateCustomSetter = generateCustomSetter;
	}

	public ReplaceString[] getReplaceString() {
		return replaceString;
	}

	@XmlElement(name = "ReplaceString")
	public void setReplaceString(ReplaceString[] replaceString) {
		this.replaceString = replaceString;
	}

	public XmlConfigParam[] getXmlConfigParams() {
		return xmlConfigParams;
	}

	@XmlElement(name = "XmlConfigParam")
	public void setXmlConfigParams(XmlConfigParam[] xmlConfigParams) {
		this.xmlConfigParams = xmlConfigParams;
	}

	public String getUpdateLocation() {
		return updateLocation;
	}

	@XmlAttribute(name="updatelocation")
	public void setUpdateLocation(String updateLocation) {
		this.updateLocation = updateLocation;
	}

	public String getXml() {
		return xml;
	}
	
	@XmlAttribute(name="xml")
	public void setXml(String xml) {
		this.xml = xml;
	}
	public String getCachekey() {
		return cachekey;
	}
	
	@XmlAttribute(name="cachekey")
	public void setCachekey(String cachekey) {
		this.cachekey = cachekey;
	}
	public String getIgnoreRegEx() {
		return ignoreRegEx;
	}
	
	@XmlAttribute(name="ignore")
	public void setIgnoreRegEx(String ignoreRegEx) {
		this.ignoreRegEx = ignoreRegEx;
	}
	public String getTag() {
		return tag;
	}
	
	@XmlAttribute(name="tag")
	public void setTag(String tag) {
		this.tag = tag;
	}
	
}
