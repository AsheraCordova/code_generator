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

public class QuirkAttribute implements Cloneable{
    private String attributeName;
    private String getterMethod;
    private String setterMethod;
    private String description;
    private String generatorUrl;
    private String xmlTest;
    private String namespace;
	private Boolean constructor;
	private boolean aliased;
    
    public boolean isAliased() {
		return aliased;
	}
	public void setAliased(boolean aliased) {
		this.aliased = aliased;
	}
	public Boolean getConstructor() {
		return constructor;
	}
	public void setConstructor(Boolean constructor) {
		this.constructor = constructor;
	}
	public String getNamespace() {
    	if (namespace != null) {
    		return namespace + ":";
    	}
		return "";
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getXmlTest() {
        return xmlTest;
    }
    public void setXmlTest(String xmlTest) {
        this.xmlTest = xmlTest;
    }
    public String getGeneratorUrl() {
    	if (generatorUrl == null) {
    		return "https://github.com/AsheraCordova/Core";
    	}
        return generatorUrl;
    }
    public void setGeneratorUrl(String generatorUrl) {
        this.generatorUrl = generatorUrl;
    }
    public String getAttributeName() {
        return attributeName;
    }
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }
    public String getGetterMethod() {
        return getterMethod;
    }
    public void setGetterMethod(String getterMethod) {
        this.getterMethod = getterMethod;
    }
    public String getSetterMethod() {
        return setterMethod;
    }
    public void setSetterMethod(String setterMethod) {
        this.setterMethod = setterMethod;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public Object clone()  {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        
    }
}
