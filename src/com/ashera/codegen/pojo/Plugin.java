package com.ashera.codegen.pojo;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class Plugin {
	private String name;
	private String gitName;
	private ReplaceString[] replaceString;
	
	public ReplaceString[] getReplaceString() {
		return replaceString;
	}
	@XmlElement(name="ReplaceString")
	public void setReplaceString(ReplaceString[] replaceString) {
		this.replaceString = replaceString;
	}
	public String getName() {
		return name;
	}

	@XmlAttribute
	public void setName(String name) {
		this.name = name;
	}

	private Copy[] copy;
	
	public Copy[] getCopy() {
		return copy;
	}

	@XmlElement(name = "Copy")
	public void setCopy(Copy[] copy) {
		this.copy = copy;
	}
	
	public boolean hasGitName() {
		return gitName != null;
	}
	
	public String getGitName() {
		if (gitName == null) {
			return name;
		}
		return gitName;
	}
	@XmlAttribute
	public void setGitName(String gitName) {
		this.gitName = gitName;
	}
}
