//start - license
/*
 * Copyright (c) 2025 Ashera Cordova
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
//end - license
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
