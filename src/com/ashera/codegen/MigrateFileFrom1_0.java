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

import java.io.File;
import java.io.FileWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MigrateFileFrom1_0 extends CodeGenBase{ 
    public static void main(String[] args) throws Exception{
        File file = new File("D:\\Java\\git\\asheralayout\\wallstreetlounge\\myApp\\platforms\\android\\assets\\www\\register.html");
        //File file = new File("/Users/ramm/git_bitbucket/asheralayout/iOSDemoProject/assets/www/imageview.html");
        String fileContent = readFileToString(file);
        fileContent = fileContent.replaceAll("<form", "<RelativeLayout");
        fileContent = fileContent.replaceAll("drawer-layout", "androidx.drawerlayout.widget.DrawerLayout");
        fileContent = fileContent.replaceAll("form>", "RelativeLayout>");
        fileContent = fileContent.replaceAll("<toolbar", "<FrameLayout");
        fileContent = fileContent.replaceAll("toolbar>", "FrameLayout>");
        fileContent = fileContent.replaceAll("aspectFit", "centerInside");
        fileContent = fileContent.replaceAll("drawable-padding", "android:drawablePadding");
        fileContent = fileContent.replaceAll("layout_", "android:layout_");
        fileContent = fileContent.replaceAll("linear\\-layout", "LinearLayout");
        fileContent = fileContent.replaceAll("relative\\-layout", "RelativeLayout");
        fileContent = fileContent.replaceAll("label", "TextView");
        fileContent = fileContent.replaceAll("scalingFactor=\"1\" ios_use_default_navigation_bar", "");
        fileContent = fileContent.replaceAll("orientation", "android:orientation");
        fileContent = fileContent.replaceAll("class=\"black\"", "style=\"@style/blackBg\"");
        fileContent = fileContent.replaceAll("id=\"", "android:id=\"@+id/");
        fileContent = fileContent.replaceAll("textColor", "android:textColor");
        fileContent = fileContent.replaceAll("padding=", "android:padding=");
        fileContent = fileContent.replaceAll("padding-left", "android:paddingLeft");
        fileContent = fileContent.replaceAll("padding-right", "android:paddingRight");
        fileContent = fileContent.replaceAll("padding-top", "android:paddingTop");
        fileContent = fileContent.replaceAll("padding-bottom", "android:paddingBottom");
        fileContent = fileContent.replaceAll("textbox", "EditText");
        fileContent = fileContent.replaceAll("columnCount", "android:columnCount");
        fileContent = fileContent.replaceAll("adjustViewBounds", "android:adjustViewBounds");
        fileContent = fileContent.replaceAll("render-event=\"(.)*\"", "");
        fileContent = fileContent.replaceAll("animation-(.)*=\"(.)*\"", "");
        fileContent = fileContent.replaceAll("ios_isUserInteractionEnabled=\"(.)*\"", "");
        fileContent = fileContent.replaceAll("ad_drawablePadding=\"(.)*\"", "");
        fileContent = fileContent.replaceAll("decorator=\"(.)*\"", "");
        fileContent = fileContent.replaceAll("\\sgravity=", " android:gravity=");
        fileContent = fileContent.replaceAll("font-size", " android:textSize");
        
        fileContent = fileContent.replaceAll("placeholder=", "android:hint=");
        fileContent = fileContent.replaceAll("drawable-left", "android:drawableLeft");
        fileContent = fileContent.replaceAll("validator", "validation");
        fileContent = fileContent.replaceAll("type=\"phone\"", "android:inputType=\"phone\"");
        fileContent = fileContent.replaceAll("type=\"password\"", "android:inputType=\"textPassword\"");
        fileContent = fileContent.replaceAll("yellow", "#ff0");
        
        
        
        fileContent = fileContent.replaceAll("class=\"(.*)\"", "style=\"@style/$1\"");
        
        fileContent = fileContent.replaceAll("grid-layout", "androidx.gridlayout.widget.GridLayout");
        fileContent = fileContent.replaceAll("max-height", "android:maxHeight");
        fileContent = fileContent.replaceAll("min-width", "android:minWidth");
        fileContent = fileContent.replaceAll("max-width", "android:maxWidth");
        fileContent = fileContent.replaceAll("min-height", "android:minHeight");
        fileContent = fileContent.replaceAll("padding-bottom", "android:paddingBottom");
        fileContent = fileContent.replaceAll("table-row", "TableRow");
        fileContent = fileContent.replaceAll("scroll-view", "ScrollView");
        fileContent = fileContent.replaceAll("img", "ImageView");
        fileContent = fileContent.replaceAll("src", "android:src");
        fileContent = fileContent.replaceAll("content-mode", "android:scaleType");
        fileContent = fileContent.replaceAll(" gravity", " android:gravity");
        fileContent = fileContent.replaceAll("button", "Button");
        fileContent = fileContent.replaceAll("text-overflow", "android:ellipsize");
        fileContent = fileContent.replaceAll("table-layout", "TableLayout");
        fileContent = fileContent.replaceAll("max-lines", "android:maxLines");
        fileContent = fileContent.replaceAll("@style/TextView-s", "@style/textViewS");
        
        fileContent = textToTextAttr(fileContent, "TextView");
        fileContent = textToTextAttr(fileContent, "Button");
        fileContent = textToTextAttr(fileContent, "EditText");
        
        
        fileContent = fileContent.replaceAll("<html>", "<layout xmlns:android=\"http://schemas.android.com/apk/res/android\"\r\n" + 
                "    xmlns:app=\"http://schemas.android.com/apk/res-auto\"\r\n" + 
                "    xmlns:tools=\"http://schemas.android.com/tools\">\r\n" + 
                "");
        
        fileContent = fileContent.replaceAll("</html>", "</layout>");
        fileContent = fileContent.replaceAll("<body(.)*>", "<data></data>");
        fileContent = fileContent.replaceAll("</body>", "");
        fileContent = fileContent.replaceAll(" <link (.)*>", "");
        fileContent = fileContent.replaceAll("<head>", "");
        fileContent = fileContent.replaceAll("</head>", "");
        fileContent = fileContent.replaceAll("@\\+id/@\\+id/", "@+id/");
        fileContent = fileContent.replaceAll(" color=", " android:textColor=");
        fileContent = fileContent.replaceAll("background-color", "android:background");
        fileContent = fileContent.replaceAll("\"white\"", "\"#fff\"");
        fileContent = fileContent.replaceAll("ImageView/", "@drawable/");
        fileContent = fileContent.replaceAll(".png", "");
        fileContent = fileContent.replaceAll("fillViewport", "android:fillViewport");
        fileContent = fileContent.replaceAll("font-weight", "android:textStyle");
        
        System.out.println(fileContent);
        FileWriter fis = new FileWriter(new File("D:\\Java\\git\\ashera-demo-projects\\ashera-phonegap-demo-project\\demoapp2\\platforms\\android\\app\\src\\main\\res\\layout\\" +
//        FileWriter fis = new FileWriter(new File("/Users/ramm/git/ashera-demo-projects/ashera-phonegap-demo-project/ashera-demo/platforms/android/res/layout/" + 
                file.getName().substring(0, file.getName().indexOf(".")).toLowerCase() + ".xml"));
        fis.write(fileContent);
        fis.close();

    }

    public static String textToTextAttr(String fileContent, String tag) {
        Matcher m = Pattern.compile(String.format("(%s([^>])*)>(([a-z]|[0-9]|[A-Z]|\\s|\\\\|,|\\-|\\?|,|\\.|&|_|\\[|=|\\]|>)*)(</%s>)", tag, tag), Pattern.DOTALL).matcher(fileContent);

        StringBuffer sb = new StringBuffer();
        boolean found = false;
        while (m.find()) {
            m.appendReplacement(sb, m.group(1) + " android:text=\"" + m.group(3).replaceAll("&", "&amp;").trim()  + "\">" + m.group(5));
            found = true;
        }
        
        if (!found) {
        	return fileContent;
        }
        
        String str = String.format("</%s>", tag);
        sb.append(fileContent.substring(fileContent.lastIndexOf(str) + str.length()));
        return sb.toString();
    }
}
