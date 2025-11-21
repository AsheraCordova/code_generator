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
package com.ashera.codegen;

import java.lang.reflect.Field;

public class StyleGenerator extends com.ashera.codegen.CodeGenBase{
    public static void main(String[] args) throws Exception{
        Field[] fields = Class.forName("org.eclipse.swt.SWT").getFields();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("        //start - mapping\n");
        for (Field field : fields) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) && field.getType() == int.class) {
                stringBuffer.append("        mapping.put(\""+ field.getName().toLowerCase() + "\",  org.eclipse.swt.SWT." + field.getName() + ");\n");
            }
        }
        stringBuffer.append("        //end - mapping\n");
        writeOrUpdateFile(stringBuffer.toString(), "../../core-javafx-widget\\SWTConveterPlugin\\src\\main\\java\\com\\ashera\\converter\\SWTBitFlagConverter.java", "mapping");
    }
}
