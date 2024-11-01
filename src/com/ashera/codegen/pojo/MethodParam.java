package com.ashera.codegen.pojo;

import javax.xml.bind.annotation.XmlAttribute;

public class MethodParam extends CustomAttribute{
	private String nullable;

	public String getNullable() {
		return nullable;
	}
	@XmlAttribute(name="nullable")
	public void setNullable(String nullable) {
		this.nullable = nullable;
	}
}
