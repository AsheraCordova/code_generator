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
