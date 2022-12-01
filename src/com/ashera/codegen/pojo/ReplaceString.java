package com.ashera.codegen.pojo;

import javax.xml.bind.annotation.XmlAttribute;

public class ReplaceString {
	private String name;
	private String replace;
	private String target;
	
	public String getTarget() {
		return target;
	}
	@XmlAttribute()
	public void setTarget(String target) {
		this.target = target;
	}
	public String getName() {
		return name;
	}
	@XmlAttribute()
	public void setName(String name) {
		this.name = name;
	}
	public String getReplace() {
		return replace;
	}
	@XmlAttribute()
	public void setReplace(String replace) {
		this.replace = replace;
	}
}
