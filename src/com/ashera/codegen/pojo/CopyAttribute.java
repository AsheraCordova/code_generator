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

public class CopyAttribute extends CustomAttribute{
    private String widget;
    private String attribute;
    private boolean onlyForTestCase = true;
    private String os;
    
    public String getOs() {
		return os;
	}

    @XmlAttribute
	public void setOs(String os) {
		this.os = os;
	}

	public boolean isOnlyForTestCase() {
		return onlyForTestCase;
	}

    @XmlAttribute
	public void setOnlyForTestCase(boolean onlyForTestCase) {
		this.onlyForTestCase = onlyForTestCase;
	}

	public String getWidget() {
        return widget;
    }
    
    @XmlAttribute
    public void setWidget(String widget) {
        this.widget = widget;
    }
    
    
    public String getAttribute() {
        return attribute;
    }
    
    @XmlAttribute
    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
}
