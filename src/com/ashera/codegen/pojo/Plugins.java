package com.ashera.codegen.pojo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Plugins")
public class Plugins {
	private Plugin[] plugin;
	
	public Plugin[] getPlugin() {
		return this.plugin;
	}

	@XmlElement(name = "Plugin")
	public void setPlugin(Plugin[] plugin) {
		this.plugin = plugin;
	}


}
