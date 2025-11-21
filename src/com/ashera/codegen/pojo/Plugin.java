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

public class Plugin {
	private String name;
	private String gitName;
	private ReplaceString[] replaceString;
	
	public ReplaceString[] getReplaceString() {
		return replaceString;
	}
	@XmlElement(name="ReplaceString")
	public void setReplaceString(ReplaceString[] replaceString) {
		this.replaceString = replaceString;
	}
	public String getName() {
		return name;
	}

	@XmlAttribute
	public void setName(String name) {
		this.name = name;
	}

	private Copy[] copy;
	
	public Copy[] getCopy() {
		return copy;
	}

	@XmlElement(name = "Copy")
	public void setCopy(Copy[] copy) {
		this.copy = copy;
	}
	
	public boolean hasGitName() {
		return gitName != null;
	}
	
	public String getGitName() {
		if (gitName == null) {
			return name;
		}
		return gitName;
	}
	@XmlAttribute
	public void setGitName(String gitName) {
		this.gitName = gitName;
	}
}
