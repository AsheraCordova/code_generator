package com.ashera.codegen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jsefa.Deserializer;
import net.sf.jsefa.csv.CsvIOFactory;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class CodeGen extends CodeGenBase{
    void start() {
        Deserializer deserializer = CsvIOFactory.createFactory(ClassConfiguration.class).createDeserializer();
        deserializer.open(createFileReader());
        ClassConfigurations classConfigurations = new ClassConfigurations();
        while (deserializer.hasNext()) {
            ClassConfiguration classConfiguration = deserializer.next();
            if (classConfiguration != null) {
            	classConfigurations.addConfigurations(classConfiguration);
            }
        }
        deserializer.close(true);
        
        for (ClassConfiguration configuration : classConfigurations.getConfigurations()) {
        	
    		try {
                //Load template from source folder
                Template template = new Template("name", new FileReader(new File("templates/" + configuration.getTemplateName())),
                        new Configuration());
                
                // Console output
                Map<String, Object> models = new HashMap<>();
    			models.put("myclass", configuration);
    			models.put("extraCode", "");
    			models.put("viewgroup", !configuration.getTemplateName().equals("BaseWidgetTemplate.java"));
    			
                String[] oss =  {"android", "ios", "javafx"};
                String[] androidprefixs =  {"", "r.", "r."};
                String[] paths =  {"AndroidLayouts"/*, "ios_widget_library", "javafx_widget_library"*/};               
                
                for (int i = 0; i < paths.length; i++) {
	    			if (!androidprefixs[i].equals("")) {
		                if (configuration.getClassName().startsWith("android.")) {
		                	configuration.setClassName("r." + configuration.getClassName());	
		                }
	    			}
	    			
	    			List<ClassConfiguration> osConfigs = new java.util.ArrayList<>();
	    			for (ClassConfiguration dependent : configuration.getWidgetAttributes()) {
				        if (dependent.getOs() != null && !dependent.getOs().isEmpty()) {
					        List<String> envs = Arrays.asList(dependent.getOs().split(","));
					        if (!envs.contains(oss[i])) {
					        	continue;
					        }		        	
				        }
				        osConfigs.add(dependent);
					}
	    			((List<ClassConfiguration>)configuration.getWidgetAttributes()).clear();
	    			((List<ClassConfiguration>)configuration.getWidgetAttributes()).addAll(osConfigs);
	    			
                	StringWriter stringWriter = new StringWriter();
                	System.out.println("configuration : " + configuration.getClassName());
					String path = paths[i];
	    			models.put("process", oss[i]);
	    			models.put("androidprefix", androidprefixs[i]);
	    				                template.process(models, stringWriter);
	                stringWriter.flush();
	                String string = stringWriter.toString();	    			
	    			if (!androidprefixs[i].equals("")) {
						string = string.replaceAll(" android\\.", " r.android.");
						string = string.replaceAll("\\(android\\.", "(r.android.");
						string = string.replaceAll("\\$widgetvar", "ASView");
	    			}

	                String pathname = "../" + path + "/src/com/ashera/android/factory/ui/" + configuration.getWidgetName() + ".java";
	                writeOrUpdateFile(string, pathname);	                
				}
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (TemplateException e) {
            	throw new RuntimeException(e);
    		}
		}
        
		
		
    }

    private Reader createFileReader() {
        try {
			return new FileReader(new File("templates/template.csv"));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
    }

    /**
     * Main method.
     * 
     * @param args no args needed.
     */
    public static void main(String[] args) {
        (new CodeGen()).start();
    }
}
