package com.ashera.codegen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import net.sf.jsefa.csv.annotation.CsvDataType;
import net.sf.jsefa.csv.annotation.CsvField;

@CsvDataType()
class ClassConfiguration implements java.io.Serializable{
	@CsvField(pos = 1)
	private String className;
	@CsvField(pos = 2)
	private String attribute;
	@CsvField(pos = 3)
	private String layoutAttr;
	@CsvField(pos = 4)
	private String order;
	@CsvField(pos = 5)
	private String decorator;
	@CsvField(pos = 6)
	private String setterMethod;
	@CsvField(pos = 7)
	private String nativeClassName;
	@CsvField(pos = 8)
	private String type;
	@CsvField(pos = 9)
	private String javaType;
	@CsvField(pos = 10)
	private String androidMinVersion;
	@CsvField(pos = 11)
	private String templateName;
	@CsvField(pos = 12)
	private String localName;
	@CsvField(pos = 13)
	private String os;
	@CsvField(pos = 14)
	private String baseClass;
	@CsvField(pos = 15)
	private String getterMethod;
	private boolean useMethodName;
	private String params;
	private boolean listView;
	private String createDefault;
	private String baseDir;
	private String widgetClassname;
	private String generatorUrl;
	private String xmlTest;
    private String testDesc;
    private String group;
    private boolean disposable;
    private String androidMinVersionForGet;
    private String nativeClassTypeForGetter;
    private String packageNamespace;
    private String iosMinVersion;
    private java.util.Properties widgetProperties;
    private String bufferStrategy = "BUFFER_STRATEGY_NONE";
    private String updateUiFlag = "UPDATE_UI_NONE";
    private String readOnly;
    private List<ClassConfiguration> methodParams = new ArrayList<ClassConfiguration>();

	public List<ClassConfiguration> getMethodParams() {
		return methodParams;
	}


	public void setMethodParams(List<ClassConfiguration> methodParams) {
		this.methodParams = methodParams;
	}


	public String getReadOnly() {
		if (readOnly == null) {
			return "false";
		}
		return readOnly;
	}


	public void setReadOnly(String readOnly) {
		this.readOnly = readOnly;
	}


	public String getUpdateUiFlag() {
		return updateUiFlag;
	}


	public void setUpdateUiFlag(String updateUiFlag) {
		this.updateUiFlag = updateUiFlag;
	}


	public String getBufferStrategy() {
		return bufferStrategy;
	}


	public void setBufferStrategy(String bufferStrategy) {
		this.bufferStrategy = bufferStrategy;
	}


	public java.util.Properties getWidgetProperties() {
		return widgetProperties;
	}


	public void setWidgetProperties(java.util.Properties widgetProperties) {
		this.widgetProperties = widgetProperties;
	}
	
	public String getAllOption() {
		String key = getTrimmedAttribute();
		if (widgetProperties.get(key + ".allOption") != null) {
			return (String) widgetProperties.get(key + ".allOption");
		}
		return null;
	}

	public String getPackageNamespace() {
		return packageNamespace;
	}


	public void setPackageNamespace(String packageNamespace) {
		this.packageNamespace = packageNamespace;
	}


	public String getNativeClassTypeForGetter() {
		return nativeClassTypeForGetter;
	}


	public void setNativeClassTypeForGetter(String nativeClassTypeForGetter) {
		this.nativeClassTypeForGetter = nativeClassTypeForGetter;
	}


	public String getAndroidMinVersionForGet() {
		return androidMinVersionForGet;
	}

    
    public String getTrimmedGroup() {

        String localStr = group;
        if (localStr.indexOf(".") != -1) {
            localStr = localStr.substring(localStr.lastIndexOf(".") + 1);
        }
        return localStr;
    }
	public void setAndroidMinVersionForGet(String androidMinVersionForGet) {
		this.androidMinVersionForGet = androidMinVersionForGet;
	}


	public boolean isDisposable() {
		return disposable;
	}


	public void setDisposable(boolean disposable) {
		this.disposable = disposable;
	}


	public String getGroup() {
        return group;
    }


    public void setGroup(String group) {
        this.group = group;
    }


    public String getTestDesc() {
        return testDesc;
    }


    public void setTestDesc(String testDesc) {
        this.testDesc = testDesc;
    }
    public String getXmlTest() {
        return xmlTest;
    }
    public void setXmlTest(String xmlTest) {
        this.xmlTest = xmlTest;
    }
    public String getGeneratorUrl() {
        return generatorUrl;
    }
    public void setGeneratorUrl(String generatorUrl) {
        this.generatorUrl = generatorUrl;
    }
    public String getWidgetClassname() {
        return widgetClassname;
    }
    public void setWidgetClassname(String widgetClassname) {
        this.widgetClassname = widgetClassname;
    }
    public String getBaseDir() {
        return baseDir;
    }
    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }
    public String getCreateDefault() {
        return createDefault;
    }
    public void setCreateDefault(String createDefault) {
        this.createDefault = createDefault;
    }
    public boolean isListView() {
        return listView;
    }
    public void setListView(boolean listView) {
        this.listView = listView;
    }
    public String getParams() {
        return params;
    }
    public void setParams(String params) {
        this.params = params;
    }
    public boolean isUseMethodName() {
        return useMethodName;
    }
    public void setUseMethodName(boolean useMethodName) {
        this.useMethodName = useMethodName;
    }
    public String getBaseClass() {
		return baseClass;
	}
	public void setBaseClass(String baseClass) {
		this.baseClass = baseClass;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	public String getLocalName() {
		return localName;
	}
	public void setLocalName(String localName) {
		this.localName = localName;
	}
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public String getAndroidMinVersion() {
		return androidMinVersion;
	}
	public void setAndroidMinVersion(String androidMinVersion) {
		this.androidMinVersion = androidMinVersion;
	}
	public String getLayoutAttr() {
		return layoutAttr;
	}
	public void setLayoutAttr(String layoutAttr) {
		this.layoutAttr = layoutAttr;
	}
	public String getVarName() {
		String str = className.substring(className.lastIndexOf(".") + 1);
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}
	
   public String getNativeClassVarName() {
        String str = nativeClassName.substring(nativeClassName.lastIndexOf(".") + 1);
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }
   
   public String getWidgetClassVarName() {
       String str = widgetClassname.substring(widgetClassname.lastIndexOf(".") + 1);
       return str.substring(0, 1).toLowerCase() + str.substring(1);
   }

	
	public String getClassShortName() {
		String str = className.substring(className.lastIndexOf(".") + 1);
		return str;
	}
	
	public String getWidgetName() {
		return widgetName;
	}


	public String getClassName() {
		return className.trim();
	}
	public void setClassName(String className) {
		this.className = className.trim();
	}

	public String getSetterMethod() {
		return setterMethod;
	}
	public void setSetterMethod(String setterMethod) {
		this.setterMethod = setterMethod;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	private List<ClassConfiguration> widgetAttributes = new ArrayList<>();
	private List<ClassConfiguration> layoutAttributes = new ArrayList<>();
	private List<ClassConfiguration> dependentAttributes = new ArrayList<>();
	private List<ClassConfiguration> composites = new ArrayList<>();
	
	public List<ClassConfiguration> getAllAttributes() {
        List<ClassConfiguration> allAttributes = new ArrayList<>(this.widgetAttributes);
        allAttributes.addAll(layoutAttributes);
		return allAttributes;
    }

	public List<ClassConfiguration> getComposites() {
        return composites;
    }
    public void setComposites(List<ClassConfiguration> composites) {
        this.composites = composites;
    }
    public List<ClassConfiguration> getDependentAttributes() {
		return dependentAttributes;
	}
	public void setDependentAttributes(List<ClassConfiguration> dependentAttributes) {
		this.dependentAttributes = dependentAttributes;
	}
	public List<ClassConfiguration> getWidgetAttributes() {
		return widgetAttributes;
	}
	public List<ClassConfiguration> getLayoutAttributes() {
		return layoutAttributes;
	}
	
	public void addComposite(ClassConfiguration classConfiguration) {
	    composites.add(classConfiguration);
    }
	public void addClassAttribute(ClassConfiguration classConfiguration) {
		if ("yes".equals(classConfiguration.getLayoutAttr())) {
			layoutAttributes.add(classConfiguration);
		} else {
			widgetAttributes.add(classConfiguration);
		}
	}
	
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	
	public String getTrimmedAttribute() {
		String localStr = attribute;
		if (localStr.indexOf("#") != -1) {
			localStr = localStr.substring(0, localStr.indexOf("#"));
		}
		if (localStr.indexOf("android:") != -1) {
			return localStr.substring(localStr.indexOf(":") + 1);
		}
		if (localStr.indexOf("css:") != -1) {
			return localStr.substring(localStr.indexOf(":") + 1);
		}
		return localStr;
	}
	
	
	
	public String getCode(String os) {
		if (setterMethod.indexOf("os-") != -1) {
			String[] code = setterMethod.substring("code:".length()).split("###");
			for (int i = 0; i < code.length; i++) {
				if (code[i].startsWith("os-" + os + ":")) {
					return code[i].substring(("os-" + os + ":").length());
				}
			}
			
			return "";
		} else {
			return setterMethod;
		}
	}
	
	public String getGetterCode(String os) {
		if (getterMethod != null && getterMethod.indexOf("os-") != -1) {
			String[] code = getterMethod.substring("code:".length()).split("###");
			for (int i = 0; i < code.length; i++) {
				if (code[i].startsWith("os-" + os + ":")) {
					return code[i].substring(("os-" + os + ":").length());
				}
			}
			
			return "";
		} else {
			return getterMethod;
		}
	}
	
	public String getTrimmedSetter() {
		if (setterMethod.startsWith("code:")) {
			return setterMethod;
		}

		if (setterMethod.indexOf("#") != -1) {
			return setterMethod.substring(0, setterMethod.indexOf("#"));
		} else 
		if (setterMethod.indexOf("(") != -1) {
			return setterMethod.substring(0, setterMethod.indexOf("("));
		}
		return setterMethod;
	}

	public String getJavaType() {
		return javaType;
	}
	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}
	public void addDependentFields(ClassConfiguration classConfiguration) {
		dependentAttributes.add(classConfiguration);
		
	}

	public String getNativeClassName() {
		return nativeClassName;
	}
	public void setNativeClassName(String nativeClassName) {
		this.nativeClassName = nativeClassName;
	}
	
	private List<String> keys = new ArrayList<>();
	private List<String> values = new ArrayList<>();
	private List<String> apis = new ArrayList<>();
	public List<String> getApis() {
		return apis;
	}
	public void setApis(List<String> apis) {
		this.apis = apis;
	}

	private String converterType;
	private boolean supportIntegerAlso;
	public boolean isSupportIntegerAlso() {
		return supportIntegerAlso;
	}


	public void setSupportIntegerAlso(boolean supportIntegerAlso) {
		this.supportIntegerAlso = supportIntegerAlso;
	}


	public void setConverterInfo(String converterKeys1, String converterKeys2,
			String type, boolean supportIntegerAlso) {
		keys = new ArrayList<String>(Arrays.asList(converterKeys1.split(",")));
		values = Arrays.asList(converterKeys2.split(","));
		this.converterType = type;
		this.supportIntegerAlso = supportIntegerAlso;
	}
	
	public void setConverterInfo(List<String> keys,
			List<String> values, String type,
			List<String> apis) {
		this.keys = keys;
		this.values = values;
		this.converterType = type;
		this.apis = apis;
	} 

	public void setConverterInfo(String converterKeys1, String converterKeys2,
			String type, String apis) {
		keys = Arrays.asList(converterKeys1.split(","));
		values = Arrays.asList(converterKeys2.split(","));
		this.converterType = type;
		this.apis = Arrays.asList(apis.split(","));
	}
	
	public List<String> getKeys() {
		return keys;
	}
	public void setKeys(List<String> keys) {
		this.keys = keys;
	}
	public List<String> getValues() {
		return values;
	}
	public void setValues(List<String> values) {
		this.values = values;
	}
	public String getConverterType() {
		return converterType;
	}
	public void setConverterType(String converterType) {
		this.converterType = converterType;
	}
	private List<String> aliases = new ArrayList<>();
	public List<String> getAliases() {
		if (attribute.indexOf("#") != -1) {
			setAliases(attribute.substring(attribute.indexOf("#") + 1).split(","));
		}
		return aliases;
	}
	public void setAliases(List<String> aliases) {
		this.aliases = aliases;
	}
	public void setAliases(String[] aliases) {
		this.aliases = new ArrayList<String>(new LinkedHashSet<String>(Arrays.asList(aliases)));
		
	}
	
	private List<String> methodDefinitions = new ArrayList<>();
	public List<String> getMethodDefinitions() {
		return methodDefinitions;
	}
	public void setMethodDefinitions(List<String> methodDefinitions) {
		this.methodDefinitions = methodDefinitions;
	}
	public void addMethodDefition(String methodDefinition) {
		methodDefinitions.add(methodDefinition);
		
	}
	private String nativeClassType;

	public String getNativeClassType() {
		return nativeClassType;
	}
	public void setNativeClassType(String nativeClassType) {
		this.nativeClassType = nativeClassType;
	}
	
	private List<String> nativeImports = new ArrayList<>();
	
	public List<String> getNativeImports() {
		return nativeImports;
	}
	public void setNativeImports(List<String> nativeImports) {
		this.nativeImports = nativeImports;
	}
	public void setNativeImports(String[] property) {
		nativeImports= Arrays.asList(property);
		
	}
	
	public String getDecorator() {
		if (decorator == null) {
			return "null";
		}
		return "\"" + decorator + "\"";
	}
	public void setDecorator(String decorator) {
		this.decorator = decorator;
	}

	public String getOrder() {
		if (order == null || order.equals("")) {
			return "0";
		}
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	private String widgetName;
	public void setWidgetName(String widgetName) {
		this.widgetName = widgetName;
	}

	public String getGetter() {
		String trimmedAttribute = getTrimmedAttribute();
		String getterPrefix = "get";
		if (type.equals("boolean")) {
			getterPrefix = "is";
		}
		
		if (trimmedAttribute.startsWith("layout_")) {
			trimmedAttribute = trimmedAttribute.substring("layout_".length());
			trimmedAttribute = "layout" + trimmedAttribute.substring(0, 1).toUpperCase() + trimmedAttribute.substring(1);
		}
		return getterPrefix + trimmedAttribute.substring(0, 1).toUpperCase() + trimmedAttribute.substring(1);
	}
	
	public String getTryGetGetter() {
		String trimmedAttribute = getTrimmedAttribute();
		if (trimmedAttribute.startsWith("layout_")) {
			trimmedAttribute = trimmedAttribute.substring("layout_".length());
			trimmedAttribute = "layout" + trimmedAttribute.substring(0, 1).toUpperCase() + trimmedAttribute.substring(1);
		}
		return "tryGet" + trimmedAttribute.substring(0, 1).toUpperCase() + trimmedAttribute.substring(1);
	}
	
	public String getTryGetterForAlias(String trimmedAttribute) {
		return "tryGet" + trimmedAttribute.substring(0, 1).toUpperCase() + trimmedAttribute.substring(1);
	}
	
	public String getGetterForAlias(String trimmedAttribute) {
		return "get" + trimmedAttribute.substring(0, 1).toUpperCase() + trimmedAttribute.substring(1);
	}
	
	public String getSetterForAlias(String trimmedAttribute) {
		return "set" + trimmedAttribute.substring(0, 1).toUpperCase() + trimmedAttribute.substring(1);
	}
	
	public String getSetter() {
	    if (useMethodName) {
	        return getTrimmedAttribute();
	    }
		String trimmedAttribute = getTrimmedAttribute();
		if (trimmedAttribute.startsWith("layout_")) {
			trimmedAttribute = trimmedAttribute.substring("layout_".length());
			trimmedAttribute = "layout" + trimmedAttribute.substring(0, 1).toUpperCase() + trimmedAttribute.substring(1);
		}
		
		return "set" + trimmedAttribute.substring(0, 1).toUpperCase() + trimmedAttribute.substring(1);
	}
	public void setGetterMethod(String getterMethod) {
		this.getterMethod = getterMethod;
		
	}
	public String getGetterMethod() {
		return getterMethod;
	}
	
	public String getTypeVariable() {
	    String type = this.type;
	    if (this.type.contains(".")) {
	        type = type.replaceAll("\\.", "_");
	    }
		return  type.substring(0, 1).toUpperCase() + type.substring(1);	
	}
	
	public String getNamespace() {
		return widgetName.toLowerCase().replaceAll("impl", "");
	}

	public String getLocalNameWithoutPackage() {
		if (!localName.contains(".")) {
			return localName;
		}
		return localName.substring(localName.lastIndexOf(".") + 1);
	}
	
	public String getAttributeForTs() {
	    if (useMethodName) {
	        return attribute + "_";
	    }
	    if (attribute.indexOf(":") != -1) {
	        return attribute.substring(attribute.indexOf(":") + 1);
        }
        return attribute;
    }
	
	public String getCommandName() {
	    if (attribute.indexOf(":") != -1) {
	        return attribute.substring(attribute.indexOf(":") + 1);
        }
        return attribute;
    }
	
	public String getWidgetSuperClass() {
		if (templateName.indexOf("BaseHasWidgetsTemplate.java") != -1) {
			return "ViewGroupImpl";
		}
		return "ViewImpl";
	}


	public String getIosMinVersion() {
		return iosMinVersion;
	}

	public void setIosMinVersion(String iosMinVersion) {
		this.iosMinVersion = iosMinVersion;
	}
	
	public String getNativeIosWidgetName(boolean viewGroup) {
		if (!viewGroup) {
			if (nativeClassName.contains(".") ) {
				return "ASUIView";
			}
			return nativeClassName;
		}
		
		if (widgetName.contains("Scroll")) {
			return "ASUIScrollView";
		}
		
		if (widgetName.contains("ListView")) {
			return "ASUITableView";
		}
		
		return "ASUIView";
	}
}
