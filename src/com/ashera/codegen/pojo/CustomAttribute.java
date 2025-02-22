package com.ashera.codegen.pojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

public class CustomAttribute implements Cloneable{    
	private boolean applyBeforeChildAdd;
	private String name;//attribute
    private String type;
    private String code;//setterMethod
    private String getterCode;//getterMethod
    private String javaType;
	private boolean useMethodName;
    private String params;
    private Integer stylePriority;
	public CustomAttribute nodeElement;
    private String order = "0";
    private String decorator;
    private String converterInfo1;
    private String converterInfo2;
    private String copyDef;
    private boolean seperatedByBar;
    private String xmlTest;
    private String testDesc;
    private String namespace;//packageNamespace
	private String generatorUrl;
	private String bufferStrategy = "BUFFER_STRATEGY_NONE";
	private String updateUiFlag = "UPDATE_UI_NONE";
    private String readOnly = "false";
    private MethodParam[] methodParam;
    private boolean supportIntegerAlso;
    private boolean disposable;
    private String nativeClassType;
    private String nativeClassTypeForGetter;
    private String androidMinVersion;
    private String androidMinVersionForGet;
    private String iosMinVersion;
    private String layoutAttr;
	private List<String> aliases = new ArrayList<>();
	private String converterType;
	private boolean codeExtensionOnly;
	private boolean useSuperViewForAccess;
	private String extraCode = "";
	private String preCode = "";
	private String arrayType;
	private String arrayListToFinalType;
	private String simpleWrapableViewStrategy;
	private boolean ignore;
	private Boolean constructor;
	private boolean inherited = true;
	private String javascriptWrapperClass;
	private String copyDefHint;
	private boolean makeTestCaseVisible;
	private String myconverterType;

	public boolean isMakeTestCaseVisible() {
		return makeTestCaseVisible;
	}

	@XmlAttribute
	public void setMakeTestCaseVisible(boolean makeTestCaseVisible) {
		this.makeTestCaseVisible = makeTestCaseVisible;
	}

	public String getCopyDefHint() {
		return copyDefHint;
	}
	
	@XmlAttribute
	public void setCopyDefHint(String copyDefHint) {
		this.copyDefHint = copyDefHint;
	}
	public String getJavascriptWrapperClass() {
		return javascriptWrapperClass;
	}
	@XmlAttribute
	public void setJavascriptWrapperClass(String javascriptWrapperClass) {
		this.javascriptWrapperClass = javascriptWrapperClass;
	}
	public boolean isInherited() {
		return inherited;
	}
	@XmlAttribute
	public void setInherited(boolean inherited) {
		this.inherited = inherited;
	}
	public Boolean getConstructor() {
		return constructor;
	}
	@XmlAttribute
	public void setConstructor(Boolean constructor) {
		this.constructor = constructor;
	}

	private List<String> listenerMethods;
	private String listenerClassName;

	public String getListenerClassName() {
		return listenerClassName;
	}
	@XmlAttribute()
	public void setListenerClassName(String listenerClassName) {
		this.listenerClassName = listenerClassName;
	}
	public List<String> getListenerMethods() {
		return listenerMethods;
	}
	@XmlElement(name = "ListenerMethod")
	public void setListenerMethods(List<String> listenerMethods) {
		this.listenerMethods = listenerMethods;
	}

	public String getAttribute() {
		return name;
	}
	public String getGetterMethod() {
		return getterCode;
	}
	
	public String getSetterMethod() {
		return code;
	}
	
	public String getPackageNamespace() {
		return namespace;
	}
	
	public boolean isIgnore() {
		return ignore;
	}
	@XmlAttribute
	public void setIgnore(boolean ignore) {
		this.ignore = ignore;
	}
	public String getSimpleWrapableViewStrategy() {
		return simpleWrapableViewStrategy;
	}
	
	@XmlAttribute
	public void setSimpleWrapableViewStrategy(String simpleWrapableViewStrategy) {
		this.simpleWrapableViewStrategy = simpleWrapableViewStrategy;
	}
	public String getArrayListToFinalType() {
		return arrayListToFinalType;
	}
	@XmlAttribute
	public void setArrayListToFinalType(String arrayListToFinalType) {
		this.arrayListToFinalType = arrayListToFinalType;
	}
	public String getArrayType() {
		return arrayType;
	}
	@XmlAttribute
	public void setArrayType(String arrayType) {
		this.arrayType = arrayType;
	}
	public String getPreCode() {
		return preCode;
	}
	@XmlAttribute
	public void setPreCode(String preCode) {
		this.preCode = preCode;
	}
	public boolean isUseSuperViewForAccess() {
		return useSuperViewForAccess;
	}
	@XmlAttribute
	public void setUseSuperViewForAccess(boolean useSuperViewForAccess) {
		this.useSuperViewForAccess = useSuperViewForAccess;
	}

	public String getExtraCode() {
		return extraCode;
	}
	@XmlAttribute
	public void setExtraCode(String extraCode) {
		this.extraCode = extraCode;
	}
	public boolean isCodeExtensionOnly() {
		return codeExtensionOnly;
	}
	@XmlAttribute
	public void setCodeExtensionOnly(boolean codeExtensionOnly) {
		this.codeExtensionOnly = codeExtensionOnly;
	}
	public String getConverterType() {
		return converterType;
	}
	public String getIosMinVersion() {
		return iosMinVersion;
	}

	public void setIosMinVersion(String iosMinVersion) {
		this.iosMinVersion = iosMinVersion;
	}

	public List<String> getAliases() {
		if (name.indexOf("#") != -1) {
			setAliases(name.substring(name.indexOf("#") + 1).split(","));
		}
		return aliases;
	}
	
	@XmlElementWrapper(name="Aliases")
	@XmlElement()
	public void setAliases(List<String> aliases) {
		this.aliases = aliases;
	}
	public void setAliases(String[] aliases) {
		this.aliases = new ArrayList<String>(new LinkedHashSet<String>(Arrays.asList(aliases)));
		
	}

	public String getAndroidMinVersion() {
		return androidMinVersion;
	}

	@XmlAttribute
	public void setAndroidMinVersion(String androidMinVersion) {
		this.androidMinVersion = androidMinVersion;
	}
	
    public boolean isApplyBeforeChildAdd() {
		return applyBeforeChildAdd;
	}
    
    @XmlAttribute
	public void setApplyBeforeChildAdd(boolean applyBeforeChildAdd) {
		this.applyBeforeChildAdd = applyBeforeChildAdd;
	}


	public String getAndroidMinVersionForGet() {
		return androidMinVersionForGet;
	}

	@XmlAttribute
	public void setAndroidMinVersionForGet(String androidMinVersionForGet) {
		this.androidMinVersionForGet = androidMinVersionForGet;
	}

	public String getNativeClassTypeForGetter() {
		return nativeClassTypeForGetter;
	}

	@XmlAttribute
	public void setNativeClassTypeForGetter(String nativeClassTypeForGetter) {
		this.nativeClassTypeForGetter = nativeClassTypeForGetter;
	}
	public String getNativeClassType() {
		return nativeClassType;
	}
	
	@XmlAttribute
	public void setNativeClassType(String nativeClassType) {
		this.nativeClassType = nativeClassType;
	}
	public MethodParam[] getMethodParam() {
		return methodParam;
	}
	
	public MethodParam[] getMethodParams() {
		return methodParam;
	}

	@XmlElement(name = "Param")
	public void setMethodParam(MethodParam[] methodParam) {
		this.methodParam = methodParam;
	}

	public String getReadOnly() {
		return readOnly;
	}

	@XmlAttribute(name="readOnly")
	public void setReadOnly(String readOnly) {
		this.readOnly = readOnly;
	}
	
	public String getUpdateUiFlag() {
		return updateUiFlag;
	}

	@XmlAttribute(name="updateUiFlag")
	public void setUpdateUiFlag(String updateUiFlag) {
		this.updateUiFlag = updateUiFlag;
	}

	public String getBufferStrategy() {
		return bufferStrategy;
	}

	@XmlAttribute(name="bufferStrategy")
	public void setBufferStrategy(String bufferStrategy) {
		this.bufferStrategy = bufferStrategy;
	}

	@XmlAttribute(name="namespace")
    public String getNamespace() {
		return namespace;
	}
	
//	public String getNamespace() {
//		return widgetName.toLowerCase().replaceAll("impl", "");
//	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getGeneratorUrl() {
		return generatorUrl;
	}

    @XmlAttribute(name="generatorUrl")
	public void setGeneratorUrl(String generatorUrl) {
		this.generatorUrl = generatorUrl;
	}

    
    public String getTestDesc() {
        return testDesc;
    }

    @XmlAttribute(name="testDesc")
    public void setTestDesc(String testDesc) {
        this.testDesc = testDesc;
    }

    public String getXmlTest() {
        return xmlTest;
    }

    @XmlAttribute(name="xmlTest")
    public void setXmlTest(String xmlTest) {
        this.xmlTest = xmlTest;
    }

    public String getJavaType() {
        return javaType;
    }


    @XmlAttribute
    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }


    public String getName() {
        return name;
    }


    @XmlAttribute
    public void setName(String name) {
        this.name = name;
    }


    public String getType() {
        return type;
    }


    @XmlAttribute
    public void setType(String type) {
        this.type = type;
    }


    public String getCode() {
        return code;
    }


    @XmlAttribute
    public void setCode(String code) {
        this.code = code;
    }


    public String getGetterCode() {
        return getterCode;
    }


    @XmlAttribute
    public void setGetterCode(String getterCode) {
        this.getterCode = getterCode;
    }


    public boolean isUseMethodName() {
        return useMethodName;
    }


    @XmlAttribute
    public void setUseMethodName(boolean useMethodName) {
        this.useMethodName = useMethodName;
    }


    public String getParams() {
        return params;
    }


    @XmlAttribute
    public void setParams(String params) {
        this.params = params;
    }
    
    public String getOrder() {
        return order;
    }

    @XmlAttribute
    public void setOrder(String order) {
        this.order = order;
    }
    
	public String getDecorator() {
		if ("@null".equals(decorator)) {
			return null;
		}
		if (decorator == null) {
			return "null";
		}
		return "\"" + decorator + "\"";
	}


    @XmlAttribute
    public void setDecorator(String decorator) {
        this.decorator = decorator;
    }


    public String getConverterInfo1() {
        return converterInfo1;
    }

    @XmlAttribute
    public void setConverterInfo1(String converterInfo1) {
        this.converterInfo1 = converterInfo1;
    }

    public String getConverterInfo2() {
        return converterInfo2;
    }

    @XmlAttribute
    public void setConverterInfo2(String converterInfo2) {
        this.converterInfo2 = converterInfo2;
    }

    public boolean isSeperatedByBar() {
        return seperatedByBar;
    }

    @XmlAttribute
    public void setSeperatedByBar(boolean seperatedByBar) {
        this.seperatedByBar = seperatedByBar;
    }

    public String getCopyDef() {
        return copyDef;
    }

    @XmlAttribute
    public void setCopyDef(String copyDef) {
        this.copyDef = copyDef;
    }
    

	public boolean isSupportIntegerAlso() {
		return supportIntegerAlso;
	}

	@XmlAttribute
	public void setSupportIntegerAlso(boolean supportIntegerAlso) {
		this.supportIntegerAlso = supportIntegerAlso;
	}
	
	@XmlAttribute
	public boolean isDisposable() {
		return disposable;
	}

	public void setDisposable(boolean disposable) {
		this.disposable = disposable;
	}
	
    
	public String getLayoutAttr() {
		return layoutAttr;
	}

	@XmlAttribute
	public void setLayoutAttr(String layoutAttr) {
		this.layoutAttr = layoutAttr;
	}
	
	// transient attributes
	private String setterMethodNoCompat = "";
	private String varType;
	private String actualAttributeName;
	private java.util.Properties widgetProperties;
    private String apiLevel = "1";
    private String apiLevelForGet;
	private List<String> keys = new ArrayList<>();
	private List<String> values = new ArrayList<>();
	private List<String> apis = new ArrayList<>();
	public String methodDef;
	
	public String getMethodDef() {
		return methodDef;
	}

	@XmlTransient
	public void setMethodDef(String methodDef) {
		this.methodDef = methodDef;
	}

	public List<String> getApis() {
		return apis;
	}
	public void setApis(List<String> apis) {
		this.apis = apis;
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
	
	@XmlTransient
	public void setKeys(List<String> keys) {
		this.keys = keys;
	}
	public List<String> getValues() {
		return values;
	}
	@XmlTransient
	public void setValues(List<String> values) {
		this.values = values;
	}
	public java.util.Properties getWidgetProperties() {
		return widgetProperties;
	}

	public void setWidgetProperties(java.util.Properties widgetProperties) {
		this.widgetProperties = widgetProperties;
	}

	public String getVarType() {
		return varType;
	}

	@XmlTransient 
	public void setVarType(String varType) {
		this.varType = varType;
	}
	
	public String getSetterMethodNoCompat() {
		return setterMethodNoCompat;
	}

	@XmlTransient 
	public void setSetterMethodNoCompat(String setterMethodNoCompat) {
		this.setterMethodNoCompat = setterMethodNoCompat;
	}

    
    public String getApiLevelForGet() {
		return apiLevelForGet;
	}

    @XmlTransient
	public void setApiLevelForGet(String apiLevelForGet) {
		this.apiLevelForGet = apiLevelForGet;
	}
    
    public String getApiLevel() {
        return apiLevel;
    }


    @XmlTransient
    public void setApiLevel(String apiLevel) {
        this.apiLevel = apiLevel;
    }

	public String getActualAttributeName() {
		return actualAttributeName;
	}
	
	@XmlTransient
	public void setActualAttributeName(String actualAttributeName) {
		this.actualAttributeName = actualAttributeName;
	}


	public String getAllOption() {
		String key = getTrimmedAttribute();
		if (widgetProperties.get(key + ".allOption") != null) {
			return (String) widgetProperties.get(key + ".allOption");
		}
		return null;
	}

	public String getTrimmedAttribute() {
		String localStr = name;
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
		if (code.indexOf("os-") != -1) {
			String[] codes = code.substring("code:".length()).split("###");
			for (int i = 0; i < codes.length; i++) {
				if (codes[i].startsWith("os-" + os + ":")) {
					return codes[i].substring(("os-" + os + ":").length());
				}
			}
			
			return "";
		} else {
			return code;
		}
	}
	
	public String getGetterCode(String os) {
		if (getterCode != null && getterCode.indexOf("os-") != -1) {
			String[] code = getterCode.substring("code:".length()).split("###");
			for (int i = 0; i < code.length; i++) {
				if (code[i].startsWith("os-" + os + ":")) {
					return code[i].substring(("os-" + os + ":").length());
				}
			}
			
			return "";
		} else {
			return getterCode;
		}
	}
	
	public String getTrimmedSetter() {
		if (code.startsWith("code:")) {
			return code;
		}

		if (code.indexOf("#") != -1) {
			return code.substring(0, code.indexOf("#"));
		} else 
		if (code.indexOf("(") != -1) {
			return code.substring(0, code.indexOf("("));
		}
		return code;
	}

	public String getAttributeForTs() {
	    if (useMethodName) {
	        return name + "_";
	    }
	    if (name.indexOf(":") != -1) {
	        return name.substring(name.indexOf(":") + 1);
        }
        return name;
    }
	
	public String getCommandName() {
	    if (name.indexOf(":") != -1) {
	        return name.substring(name.indexOf(":") + 1);
        }
        return name;
    }
	
	public String getTypeVariable() {
	    String type = this.type;
	    if (this.type.contains(".")) {
	        type = type.replaceAll("\\.", "_");
	    }
		return  type.substring(0, 1).toUpperCase() + type.substring(1);	
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
	
	@Override
	public java.lang.String toString() {
		return getName();
	}
	
	@Override
	public Object clone() throws java.lang.CloneNotSupportedException {
		return super.clone();
	}

	@XmlAttribute()
    public Integer getStylePriority() {
		return stylePriority;
	}

	public void setStylePriority(Integer stylePriority) {
		this.stylePriority = stylePriority;
	}
	
	public String getMyconverterType() {
		return myconverterType;
	}

	@XmlAttribute(name="converterType")
	public void setMyconverterType(String myconverterType) {
		this.myconverterType = myconverterType;
	}


}
