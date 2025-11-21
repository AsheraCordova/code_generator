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
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Widgets")
public class Widgets
{
	
	private Widget[] Widget;
    private Ref[] ref;
	private Code[] code;
    public Code[] getCode() {
		return code;
	}

    @XmlElement(name = "Code")
	public void setCode(Code[] code) {
		this.code = code;
	}

	public Widget[] getWidget ()
    {
        return Widget;
    }

    @XmlElement(name = "Widget")
    public void setWidget (Widget[] Widget)
    {
        this.Widget = Widget;
    }

    public Ref[] getRef() {
		return ref;
	}

    @XmlElement(name = "Ref")
	public void setRef(Ref[] ref) {
		this.ref = ref;
	}


    
    @Override
    public String toString()
    {
        return "ClassPojo [Widget = "+Widget+"]";
    }
    
	public Widget getWidgetByNameAndOS(String name, String os) {
		for (Widget mywidget : Widget) {
			if (mywidget.getName().equals(name) && mywidget.getOs().equals(os)) {
				return mywidget;
			}
		}
		return null;
	}
}