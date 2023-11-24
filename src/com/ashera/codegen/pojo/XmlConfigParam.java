package com.ashera.codegen.pojo;

import javax.xml.bind.annotation.XmlAttribute;

public class XmlConfigParam {
	private String type;
	private String location;
	private String className;
	private String paramName;
	private boolean topLevelClass;
	
	public boolean isTopLevelClass() {
		return topLevelClass;
	}

	@XmlAttribute(name="topLevelClass")
	public void setTopLevelClass(boolean topLevelClass) {
		this.topLevelClass = topLevelClass;
	}

	public String getType() {
		return type;
	}
	
	@XmlAttribute(name="type")
	public void setType(String type) {
		this.type = type;
	}
	public String getLocation() {
		return location;
	}
	
	@XmlAttribute(name="location")
	public void setLocation(String location) {
		this.location = location;
	}
	public String getClassName() {
		return className;
	}
	
	@XmlAttribute(name="classname")
	public void setClassName(String className) {
		this.className = className;
	}
	public String getParamName() {
		return paramName;
	}
	
	@XmlAttribute(name="paramname")
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
}
