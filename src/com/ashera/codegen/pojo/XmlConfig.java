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
