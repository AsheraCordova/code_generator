package com.ashera.codegen.pojo;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class WidgetConfig {
	private String name;

	private SourceConfig[] sources;
	
	public String getName() {
		return name;
	}

	@XmlAttribute(name="name")
	public void setName(String name) {
		this.name = name;
	}

	public SourceConfig[] getSources() {
		return sources;
	}

	@XmlElement(name = "Source")
	public void setSources(SourceConfig[] sources) {
		this.sources = sources;
	}
}
