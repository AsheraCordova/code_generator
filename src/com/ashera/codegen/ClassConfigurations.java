package com.ashera.codegen;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ClassConfigurations {
	private Map<String, ClassConfiguration> configurations = new HashMap<>();
	private Map<String, ClassConfiguration> dependentFields = new HashMap<>();

	public Collection<ClassConfiguration> getConfigurations() {
		return configurations.values();
	}

	public ClassConfiguration addConfigurations(ClassConfiguration classConfiguration) {
		if (classConfiguration.getType() == null) {
			return null;
		}
		if (classConfiguration.getType().indexOf("[") != -1) {
			String key = classConfiguration.getClassName() + "_" + classConfiguration.getType();
			if (dependentFields.containsKey(key)) {
				dependentFields.get(key).addDependentFields(classConfiguration);
				classConfiguration.setDependentAttributes(dependentFields.get(key).getDependentAttributes());
			} else {
				dependentFields.put(key, classConfiguration);
				classConfiguration.addDependentFields(classConfiguration);
			}
		}
		
		if (!configurations.containsKey(classConfiguration.getClassName())) {
			System.out.println(classConfiguration.getClassName() + " " + classConfiguration.getLocalName());
			configurations.put(classConfiguration.getClassName(), classConfiguration);
		}
		ClassConfiguration configuration = getParentConfig(classConfiguration);
		
		if (!classConfiguration.getType().equals("skip")) {
			configuration.addClassAttribute(classConfiguration);
		}
		
		return getParentConfig(classConfiguration);
	}

	public ClassConfiguration getParentConfig(ClassConfiguration classConfiguration) {
		return configurations.get(classConfiguration.getClassName());
	}

	public void removeDuplicate(String name, String className) {
		boolean found = false;
		if (configurations.containsKey(className)) {
			List<ClassConfiguration> fields = configurations.get(className).getWidgetAttributes();
			for (Iterator iterator = fields.iterator(); iterator.hasNext();) {
				ClassConfiguration classConfiguration2 = (ClassConfiguration) iterator.next();
				
				if (classConfiguration2.getAttribute().equals(name)) {
					
					if (found) {
						iterator.remove();
					}
					found = true;
				}
				
			}
		}
		
	}
	
	
	
	
}
