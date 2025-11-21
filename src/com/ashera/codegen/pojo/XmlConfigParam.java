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
