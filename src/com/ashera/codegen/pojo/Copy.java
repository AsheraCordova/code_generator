package com.ashera.codegen.pojo;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Copy {
	private String from;
	private String to;
	private ReplaceString[] replaceString;
	
	public String getFrom() {
		return from;
	}
	@XmlAttribute
	public void setFrom(String from) {
		this.from = from;
	}
	public String getTo() {
		return to;
	}
	@XmlAttribute
	public void setTo(String to) {
		this.to = to;
	}
	public ReplaceString[] getReplaceString() {
		return replaceString;
	}
	@XmlElement(name="ReplaceString")
	public void setReplaceString(ReplaceString[] replaceString) {
		this.replaceString = replaceString;
	}
	
}
