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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class CodeGenIosCode {
    void start(String pathname, String extension) throws IOException{
    	Properties properties = new Properties();
		properties.load(new FileInputStream("ios_template/files.properties"));
		Set<Object> keys = properties.keySet();
		for (Object key : keys) {
			String keyStr = (String)key;
			String value = properties.getProperty(keyStr);
			String[] values = value.split(",");
			
			
        	StringWriter stringWriter = new StringWriter();
    		try {
                //Load template from source folder
                
				Template template = new Template("name", new FileReader(new File(pathname)),
                        new Configuration());
                
                // Console output
                Map<String, Object> models = new HashMap<>();
                
    			models.put("className", key);
    			for (int i = 0; i < values.length; i++) {
    				String[] map = values[i].split("\\:");
    				System.out.println(map[0]);
    				models.put(map[0], map[1]);
				}
                template.process(models, stringWriter);
                stringWriter.flush();
                
				FileWriter fis = new FileWriter(new File("../iOSDemoProject/customwidgets/src/" + keyStr + extension));
                fis.write(stringWriter.toString());
                fis.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (TemplateException e) {
            	throw new RuntimeException(e);
    		} finally {
    			try {
    				stringWriter.close();
    			} catch (IOException e) {
    			}
    		}
		}
    }

   /**
     * Main method.
     * 
     * @param args no args needed.
     */
    public static void main(String[] args) throws IOException{
        (new CodeGenIosCode()).start("ios_template/ViewTemplate.h", ".h");
        (new CodeGenIosCode()).start("ios_template/ViewTemplate.m", ".m");
    }
}
