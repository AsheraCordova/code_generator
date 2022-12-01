package com.ashera.codegen.pojo;

import javax.xml.bind.annotation.XmlAttribute;

public class CopyAttribute extends CustomAttribute{
    private String widget;
    private String attribute;
    private boolean onlyForTestCase = true;
    private String os;
    
    public String getOs() {
		return os;
	}

    @XmlAttribute
	public void setOs(String os) {
		this.os = os;
	}

	public boolean isOnlyForTestCase() {
		return onlyForTestCase;
	}

    @XmlAttribute
	public void setOnlyForTestCase(boolean onlyForTestCase) {
		this.onlyForTestCase = onlyForTestCase;
	}

	public String getWidget() {
        return widget;
    }
    
    @XmlAttribute
    public void setWidget(String widget) {
        this.widget = widget;
    }
    
    
    public String getAttribute() {
        return attribute;
    }
    
    @XmlAttribute
    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
}
