package com.ashera.codegen.templates;

import java.util.List;

import com.ashera.codegen.pojo.CustomAttribute;
import com.ashera.codegen.pojo.Widget;

public class CustomTransformer { 

    public boolean handle(List<CustomAttribute> nodeElements, String attributeWithoutNameSpace, Widget widget, String generatorUrl, String namespace) {
        if (widget.getName().equals("TableRow")) {
            CustomAttribute nodeElement = new CustomAttribute();
            nodeElement.setNamespace(namespace);
            nodeElement.setGeneratorUrl(generatorUrl);		
            nodeElement.setName(attributeWithoutNameSpace);
            nodeElement.setApiLevel("1");

            if (attributeWithoutNameSpace.startsWith("layout_")) {
                String text = attributeWithoutNameSpace.replaceAll("layout_to", "").replaceAll("layout_", "");
                nodeElement.setVarType("int");
                nodeElement.setCode(text + " = ");
                nodeElement.setJavaType("int");
                nodeElements.add(nodeElement);
                return true;
            }
        }

        return false;
    }

    private String code;
    public String getCode() {
        return code;
    }
}
