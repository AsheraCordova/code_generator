package com.ashera.codegen.pojo;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Url {
	private String url;
	private String type;
	private String id;
	public String getId() {
		return id;
	}
	@XmlAttribute()
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	@XmlAttribute()
	public void setType(String type) {
		this.type = type;
	}
	private ReplaceString[] replaceStrings;
	public String getUrl() {
		return url;
	}
	@XmlAttribute()
	public void setUrl(String url) {
		this.url = url;
	}
	
	public ReplaceString[] getReplaceStrings() {
		return replaceStrings;
	}
	@XmlElement(name="ReplaceString")
	public void setReplaceStrings(ReplaceString[] replaceStrings) {
		this.replaceStrings = replaceStrings;
	}
}
