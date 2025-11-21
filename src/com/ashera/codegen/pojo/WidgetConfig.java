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

public class WidgetConfig {
	private String name;
	private String prefixHint;

	public String getPrefixHint() {
		return prefixHint;
	}

	@XmlAttribute(name="prefixHint")
	public void setPrefixHint(String prefixHint) {
		this.prefixHint = prefixHint;
	}

	private SourceConfig[] sources;
	
	public String getName() {
		return name;
	}

	@XmlAttribute(name="name")
	public void setName(String name) {
		this.name = name;
	}

	public SourceConfig[] getSources() {
		return sources;
	}

	@XmlElement(name = "Source")
	public void setSources(SourceConfig[] sources) {
		this.sources = sources;
	}
}
