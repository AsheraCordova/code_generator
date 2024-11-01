package com.ashera.codegen.pojo;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class WidgetConfig {
	private String name;
	private String prefixHint;

	public String getPrefixHint() {
		return prefixHint;
	}

	@XmlAttribute(name="prefixHint")
	public void setPrefixHint(String prefixHint) {
		this.prefixHint = prefixHint;
	}

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
