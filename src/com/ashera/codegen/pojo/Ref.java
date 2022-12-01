package com.ashera.codegen.pojo;

import javax.xml.bind.annotation.XmlAttribute;

public class Ref {
	private String ref;

	public String getRef() {
		return ref;
	}

	@XmlAttribute
	public void setRef(String ref) {
		this.ref = ref;
	}
}
