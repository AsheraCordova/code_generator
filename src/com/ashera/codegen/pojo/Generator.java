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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
                 
public class Generator
{
	private String propertyfile;	
	private Url[] parentUrls;
	private String type;	
    private String url;
    private String styleableName;
    private String forceGenerateAttribute;
    private String forceGenerateAttributeGetIgnore;
	private String filterRegEx;
	private String copyToPath;
	private String nativeclassname;
	private String nativeclassVar;

	public String getNativeclassname() {
		return nativeclassname;
	}

	@XmlAttribute(name="nativeclassname")
	public void setNativeclassname(String nativeclassname) {
		this.nativeclassname = nativeclassname;
	}

	public String getNativeclassVar() {
		return nativeclassVar;
	}

	@XmlAttribute(name="nativeclassVar")
	public void setNativeclassVar(String nativeclassVar) {
		this.nativeclassVar = nativeclassVar;
	}

	public String getCopyToPath() {
		return copyToPath;
	}

	@XmlAttribute(name="copyToPath")
	public void setCopyToPath(String copyToPath) {
		this.copyToPath = copyToPath;
	}

	public String getFilterRegEx() {
		return filterRegEx;
	}

	@XmlAttribute(name="filterRegEx")
	public void setFilterRegEx(String filterRegEx) {
		this.filterRegEx = filterRegEx;
	}

	public List<String> getForceGenerateAttributeAsList() {
		if (forceGenerateAttribute == null) {
			return new ArrayList<String>();
		}
		return Arrays.asList(forceGenerateAttribute.split(","));
	}
	
	public List<String> getForceGenerateAttributeGetIgnoreAsList() {
		if (forceGenerateAttributeGetIgnore == null) {
			return new ArrayList<String>();
		}
		return Arrays.asList(forceGenerateAttributeGetIgnore.split(","));
	}
    
    
	public String getForceGenerateAttribute() {
		return forceGenerateAttribute;
	}

	@XmlAttribute(name="forceGenerateAttribute")
	public void setForceGenerateAttribute(String forceGenerateAttribute) {
		this.forceGenerateAttribute = forceGenerateAttribute;
	}

	public String getStyleableName() {
		return styleableName;
	}

	@XmlAttribute(name="styleableName")
	public void setStyleableName(String styleableName) {
		this.styleableName = styleableName;
	}

    public String getType ()
    {
        return type;
    }

    @XmlAttribute()
    public void setType (String type)
    {
        this.type = type;
    }

    public String getUrl ()
    {
        return url;
    }

    @XmlAttribute()
    public void setUrl (String url)
    {
        this.url = url;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [type = "+type+", url = "+url+"]";
    }
        
    public String getPropertyfile() {
		return propertyfile;
	}
    @XmlAttribute()
	public void setPropertyfile(String propertyfile) {
		this.propertyfile = propertyfile;
	}

	public Url[] getParentUrls() {
		return parentUrls;
	}
	@XmlElement(name="Url")
	public void setParentUrls(Url[] parentUrls) {
		this.parentUrls = parentUrls;
	}
    public String getForceGenerateAttributeGetIgnore() {
		return forceGenerateAttributeGetIgnore;
	}

    @XmlAttribute()
	public void setForceGenerateAttributeGetIgnore(String forceGenerateAttributeGetIgnore) {
		this.forceGenerateAttributeGetIgnore = forceGenerateAttributeGetIgnore;
	}
//	public String[] getParentUrlAsArr() {
//		if (parentUrls == null) {
//			return new String[0];
//		}
//		return parentUrls.split(",");
//	}

}
	