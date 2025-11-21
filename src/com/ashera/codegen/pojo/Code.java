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

import javax.xml.bind.annotation.XmlElement;

public class Code {
	private String url;
	private String toUrl;
	private Copy[] copy;
	private String target;

	public String getTarget() {
        return target;
    }

	@javax.xml.bind.annotation.XmlAttribute
    public void setTarget(String target) {
        this.target = target;
    }

    public Copy[] getCopy() {
		return copy;
	}

	@XmlElement(name = "Copy")
	public void setCopy(Copy[] copy) {
		this.copy = copy;
	}

	public String getUrl() {
		return url;
	}

	@javax.xml.bind.annotation.XmlAttribute
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getToUrl() {
		return toUrl;
	}

	@javax.xml.bind.annotation.XmlAttribute
	public void setToUrl(String toUrl) {
		this.toUrl = toUrl;
	}

}
