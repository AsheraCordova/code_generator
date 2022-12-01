package com.ashera.codegen.pojo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "WidgetConfigs")
public class WidgetConfigs {
	private WidgetConfig[] widgetConfigs;

	public WidgetConfig[] getWidgetConfigs() {
		return widgetConfigs;
	}

	@XmlElement(name = "WidgetConfig")
	public void setWidgetConfigs(WidgetConfig[] widgetConfigs) {
		this.widgetConfigs = widgetConfigs;
	}
}
