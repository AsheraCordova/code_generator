package com.ashera.codegen.pojo;

import javax.xml.bind.annotation.XmlElement;

public class Code {
	private String url;
	private String toUrl;
	private Copy[] copy;
	private String target;

	public String getTarget() {
        return target;
    }

	@javax.xml.bind.annotation.XmlAttribute
    public void setTarget(String target) {
        this.target = target;
    }

    public Copy[] getCopy() {
		return copy;
	}

	@XmlElement(name = "Copy")
	public void setCopy(Copy[] copy) {
		this.copy = copy;
	}

	public String getUrl() {
		return url;
	}

	@javax.xml.bind.annotation.XmlAttribute
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getToUrl() {
		return toUrl;
	}

	@javax.xml.bind.annotation.XmlAttribute
	public void setToUrl(String toUrl) {
		this.toUrl = toUrl;
	}

}
