package com.ashera.codegen.pojo;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Widgets")
public class Widgets
{
	
	private Widget[] Widget;
    private Ref[] ref;
	private Code[] code;
    public Code[] getCode() {
		return code;
	}

    @XmlElement(name = "Code")
	public void setCode(Code[] code) {
		this.code = code;
	}

	public Widget[] getWidget ()
    {
        return Widget;
    }

    @XmlElement(name = "Widget")
    public void setWidget (Widget[] Widget)
    {
        this.Widget = Widget;
    }

    public Ref[] getRef() {
		return ref;
	}

    @XmlElement(name = "Ref")
	public void setRef(Ref[] ref) {
		this.ref = ref;
	}


    
    @Override
    public String toString()
    {
        return "ClassPojo [Widget = "+Widget+"]";
    }
    
	public Widget getWidgetByNameAndOS(String name, String os) {
		for (Widget mywidget : Widget) {
			if (mywidget.getName().equals(name) && mywidget.getOs().equals(os)) {
				return mywidget;
			}
		}
		return null;
	}
}