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
