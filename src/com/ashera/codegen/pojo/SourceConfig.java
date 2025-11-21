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

public class SourceConfig {
	private String url;
	private String type;
	private String id;
	private Url[] parentUrls;
	private ReplaceString[] replaceStrings;

	public String getUrl() {
		return url;
	}
	
	@XmlAttribute(name="url")    
	public void setUrl(String url) {
		this.url = url;
	}
	public String getType() {
		return type;
	}
	
	@XmlAttribute(name="type")    
	public void setType(String type) {
		this.type = type;
	}
	

	public Url[] getParentUrls() {
		return parentUrls;
	}
	@XmlElement(name="Url")
	public void setParentUrls(Url[] parentUrls) {
		this.parentUrls = parentUrls;
	}
	
	public String getId() {
		return id;
	}

	@XmlAttribute(name="id")
	public void setId(String id) {
		this.id = id;
	}
	
	public ReplaceString[] getReplaceStrings() {
		return replaceStrings;
	}
	@XmlElement(name="ReplaceString")
	public void setReplaceStrings(ReplaceString[] replaceStrings) {
		this.replaceStrings = replaceStrings;
	}

}
