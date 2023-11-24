package com.ashera.codegen.pojo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import com.ashera.codegen.CodeGenHelper;

public class Widget
{
    
    public String getClassName() {
		return getPrefix() + nativeclassname.trim();
	}
    
    public String getTemplateName() {
		return template;
	}
    
    public String getWidgetName() {
		return classname;
	}
    
    public String getLocalName() {
		return name;
	}
    
    public String getNativeClassName() {
		return nativeclassname;
	}
    
    public String getBaseDir() {
		return baseDirectory;
	}
    
    public String getWidgetClassname() {
		return widgetclassname;
	}
	
    @javax.xml.bind.annotation.XmlTransient
    private Map<String, String> additionalAttributes = new HashMap<String, String>();
	private List<String> methodDefinitions = new ArrayList<>();
	private String os;
	private String template;//templateName
	private String jstemplate;
	private String attrstemplate;
	private String name;//widgetName//localName
    private String classname;//className;
    private String nativeclassname;//nativeClassName
    private boolean listView;
    private String parentWidget;
	private String prefix;
    private List<CustomAttribute> customAttribute;
    private CustomAttribute[] prototypeCustomAttribute;
	private Code[] code;
    private String createDefault;
    private Widget[] widgets;
    private Composite[] composites;
    private boolean onlyChildWidgets;
    private String baseClass;
    private String baseDirectory;//baseDir
    private String widgetclassname;//widgetClassname
    private String unsupportedValues;
    private String group;
    private String packageName;
    private boolean generateXmlTestCase;
    private String ignoreParentCustomAttributes; 
    private boolean copyParentCode; 
    private ReplaceString[] replaceString;
    private CopyAttribute[] copyAttribute;
    private boolean excludeFromReport;
    private List<String> nativeImports = new ArrayList<>();
	private String generateIosClass; 
    private String iosBaseDirectory;
    private boolean viewplugin;
    private String viewPluginFor;
    private String excludeFromGetterAndSetterTestCase;
    private String explicitVarName;
    private boolean makeTestCaseVisible;
	private String defaultGeneratorUrl;
	private XmlConfig[] xmlConfigs;
	
	public XmlConfig[] getXmlConfigs() {
		return xmlConfigs;
	}

	@XmlElement(name = "XmlConfig")
	public void setXmlConfigs(XmlConfig[] xmlConfigs) {
		this.xmlConfigs = xmlConfigs;
	}

	public String getDefaultGeneratorUrl() {
		return defaultGeneratorUrl;
	}

	@XmlAttribute(name="defaultGeneratorUrl")
	public void setDefaultGeneratorUrl(String defaultGeneratorUrl) {
		this.defaultGeneratorUrl = defaultGeneratorUrl;
	}

	public boolean isMakeTestCaseVisible() {
		return makeTestCaseVisible;
	}

	@XmlAttribute(name="makeTestCaseVisible")
	public void setMakeTestCaseVisible(boolean makeTestCaseVisible) {
		this.makeTestCaseVisible = makeTestCaseVisible;
	}

	public String getExplicitVarName() {
		return explicitVarName;
	}

	@XmlAttribute(name="explicitVarName")
	public void setExplicitVarName(String explicitVarName) {
		this.explicitVarName = explicitVarName;
	}

	public String getExcludeFromGetterAndSetterTestCase() {
		return excludeFromGetterAndSetterTestCase;
	}
	
	public List<String> getExcludeFromGetterAndSetterTestCaseAsList() {
		if (excludeFromGetterAndSetterTestCase == null) {
			return new ArrayList<String>();
		}
		return Arrays.asList(excludeFromGetterAndSetterTestCase.split(","));
	}

	@XmlAttribute(name="excludeFromGetterAndSetterTestCase")
	public void setExcludeFromGetterAndSetterTestCase(String excludeFromGetterAndSetterTestCase) {
		this.excludeFromGetterAndSetterTestCase = excludeFromGetterAndSetterTestCase;
	}

	public String getViewPluginFor() {
		return viewPluginFor;
	}

	@XmlAttribute(name="viewPluginFor")
	public void setViewPluginFor(String viewPluginFor) {
		this.viewPluginFor = viewPluginFor;
	}

	public boolean isViewplugin() {
		return viewplugin;
	}

    @XmlAttribute(name="viewplugin")
	public void setViewplugin(boolean viewplugin) {
		this.viewplugin = viewplugin;
	}

	public String getGenerateIosClass() {
		return generateIosClass;
	}

	@XmlAttribute(name="generateIosClass")
	public void setGenerateIosClass(String generateIosClass) {
		this.generateIosClass = generateIosClass;
	}

	public String getIosBaseDirectory() {
		return iosBaseDirectory;
	}

	@XmlAttribute(name="iosBaseDirectory")
	public void setIosBaseDirectory(String iosBaseDirectory) {
		this.iosBaseDirectory = iosBaseDirectory;
	}

	public boolean isExcludeFromReport() {
		return excludeFromReport;
	}

    @XmlAttribute(name="excludeFromReport")
	public void setExcludeFromReport(boolean excludeFromReport) {
		this.excludeFromReport = excludeFromReport;
	}

	public CopyAttribute[] getCopyAttribute() {
        return copyAttribute;
    }

    @XmlElement(name = "CopyAttribute")
    public void setCopyAttribute(CopyAttribute[] copyAttribute) {
        this.copyAttribute = copyAttribute;
    }

    public ReplaceString[] getReplaceString() {
		return replaceString;
	}

    @XmlElement(name = "ReplaceString")
	public void setReplaceString(ReplaceString[] replaceString) {
		this.replaceString = replaceString;
	}

	public boolean isCopyParentCode() {
		return copyParentCode;
	}

    @XmlAttribute(name="copyParentCode")
	public void setCopyParentCode(boolean copyParentCode) {
		this.copyParentCode = copyParentCode;
	}

	public String getIgnoreParentCustomAttributes() {
        return ignoreParentCustomAttributes;
    }

    @XmlAttribute(name="ignoreParentCustomAttributes")
    public void setIgnoreParentCustomAttributes(String ignoreParentCustomAttributes) {
        this.ignoreParentCustomAttributes = ignoreParentCustomAttributes;
    }

    public boolean isGenerateXmlTestCase() {
        return generateXmlTestCase;
    }

    @XmlAttribute(name="generateXmlTestCase")
    public void setGenerateXmlTestCase(boolean generateXmlTestCase) {
        this.generateXmlTestCase = generateXmlTestCase;
    }

    public String getPackageName() {
        return packageName;
    }

    @XmlAttribute(name="packageName")
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getGroup() {
        return group;
    }
    
    public String getTrimmedGroup() {

        String localStr = group;
        if (localStr.indexOf(".") != -1) {
            localStr = localStr.substring(localStr.lastIndexOf(".") + 1);
        }
        return localStr;
    }
    
    @XmlAttribute(name="group")
    public void setGroup(String group) {
        this.group = group;
    }

    public String getUnsupportedValues() {
        return unsupportedValues;
    }

    public void setUnsupportedValues(String unsupportedValues) {
        this.unsupportedValues = unsupportedValues;
    }

    public String getWidgetclassname() {
        return widgetclassname;
    }

    @XmlAttribute(name="widgetclassname")
    public void setWidgetclassname(String widgetclassname) {
        this.widgetclassname = widgetclassname;
    }

    public String getBaseDirectory() {
        return baseDirectory;
    }

    @XmlAttribute(name="baseDirectory")
    public void setBaseDirectory(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }


    public String getBaseClass() {
        return baseClass;
    }

    @XmlAttribute(name="baseClass")
    public void setBaseClass(String baseClass) {
        this.baseClass = baseClass;
    }

    public boolean isOnlyChildWidgets() {
        return onlyChildWidgets;
    }

    @XmlAttribute(name="onlyChildWidgets")
    public void setOnlyChildWidgets(boolean onlyChildWidgets) {
        this.onlyChildWidgets = onlyChildWidgets;
    }

    @XmlElement(name = "Composite")
    public Composite[] getComposites() {
        return composites;
    }

    public void setComposites(Composite[] composites) {
        this.composites = composites;
    }

    public Widget[] getWidgets() {
		return widgets;
	}

    @XmlElement(name = "Widget")
	public void setWidgets(Widget[] widgets) {
		this.widgets = widgets;
	}

	public Code[] getCode() {
		return code;
	}

    @XmlElement(name = "Code")
	public void setCode(Code[] code) {
		this.code = code;
	}

	public List<CustomAttribute> getCustomAttribute ()
    {
        return customAttribute;
    }
    
    public String getPrefix() {
		return prefix;
	}
    
    public CustomAttribute[] getPrototypeCustomAttribute() {
		return prototypeCustomAttribute;
	}

    @XmlElement(name = "PrototypeCustomAttribute")
	public void setPrototypeCustomAttribute(CustomAttribute[] protoTypeCustomAttribute) {
		this.prototypeCustomAttribute = protoTypeCustomAttribute;
	}
    

    @XmlElement(name = "CustomAttribute")
    public void setCustomAttribute (List<CustomAttribute> customAttribute)
    {
        this.customAttribute = customAttribute;
    }

    @XmlAttribute(name="prefix")
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}


	private Generator[] Generator;

    public String getTemplate ()
    {
        return template;
    }

    @XmlAttribute(name="template")
    public void setTemplate (String template)
    {
        this.template = template;
    }

    public String getName ()
    {
        return name;
    }

    @XmlAttribute(name="name")
    public void setName (String name)
    {
        this.name = name;
    }

    public String getClassname ()
    {
        return classname;
    }

    @XmlAttribute(name="classname")
    public void setClassname (String classname)
    {
        this.classname = classname;
    }

    public Generator[] getGenerator ()
    {
        return Generator;
    }

	@XmlElement(name="Generator")
    public void setGenerator (Generator[] Generator)
    {
        this.Generator = Generator;
    }
    public String getOs() {
		return os;
	}

    @XmlAttribute(name="os")
	public void setOs(String os) {
		this.os = os;
	}
    public String getNativeclassname() {
		return nativeclassname;
	}
    @XmlAttribute(name="nativeclassname")
	public void setNativeclassname(String nativeclassname) {
		this.nativeclassname = nativeclassname;
	}

    public String getJstemplate() {
		return jstemplate;
	}

    @XmlAttribute(name="jstemplate")
	public void setJstemplate(String jstemplate) {
		this.jstemplate = jstemplate;
	}

	public String getAttrstemplate() {
		return attrstemplate;
	}

	@XmlAttribute(name="attrstemplate")
	public void setAttrstemplate(String attrstemplate) {
		this.attrstemplate = attrstemplate;
	}

    
    @Override
    public String toString()
    {
        return "ClassPojo [template = "+template+", name = "+name+", classname = "+classname+", Generator = "+Generator+"]";
    }

	public CustomAttribute getCustomAttribute(String attrName) {
	    if (customAttribute != null) {
    		for (CustomAttribute customAttr : customAttribute) {
    			if (customAttr.getName().equals(attrName)) {
    				return customAttr;
    			}
    		}
	    }
		return null;
	}
	


    public boolean isListView() {
        return listView;
    }


    @XmlAttribute
    public void setListView(boolean listView) {
        this.listView = listView;
    }
    

	public String getParentWidget() {
		return parentWidget;
	}

	@XmlAttribute
	public void setParentWidget(String parentWidget) {
		this.parentWidget = parentWidget;
	}
	

    public String getCreateDefault() {
        return createDefault;
    }

    @XmlAttribute
    public void setCreateDefault(String createDefault) {
        this.createDefault = createDefault;
    }
    
	public List<String> getMethodDefinitions() {
		return methodDefinitions;
	}

	@XmlElement(name = "methodDefinitions")
	public void setMethodDefinitions(List<String> methodDefinitions) {
		this.methodDefinitions = methodDefinitions;
	}
	
	public void addMethodDefition(String methodDefinition) {
		if (!methodDefinitions.contains(methodDefinition)) {
			methodDefinitions.add(methodDefinition);
		}
	}
	
//	public void addCustomAttribute(CustomAttribute customAttribute) {
//		List<CustomAttribute> customAttributes = new ArrayList<CustomAttribute>();
//		if (this.customAttribute != null) {
//			customAttributes = new ArrayList<>(Arrays.asList(this.customAttribute));
//		}
//		customAttributes.add(customAttribute);
//		this.customAttribute = customAttributes.toArray(new CustomAttribute[customAttributes.size()]);
//	}
	
	public List<String> getNativeImports() {
		return nativeImports;
	}
	public void setNativeImports(List<String> nativeImports) {
		this.nativeImports = nativeImports;
	}
	public void setNativeImports(String[] property) {
		nativeImports= Arrays.asList(property);
		
	}

	public boolean containsCustomAttrbute(CustomAttribute customAttribute) {
		return getAllAttributes().contains(customAttribute);
	}

	
	public String getVarName() {
		if (explicitVarName != null) {
			return explicitVarName;
		}
		String str = getClassName().substring( getClassName().lastIndexOf(".") + 1);
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}
	
   public String getNativeClassVarName() {
        String str = nativeclassname.substring(nativeclassname.lastIndexOf(".") + 1);
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }
   
   public String getWidgetClassVarName() {
       String str = widgetclassname.substring(widgetclassname.lastIndexOf(".") + 1);
       return str.substring(0, 1).toLowerCase() + str.substring(1);
   }

	
	public String getClassShortName() {
		String str = getClassName().substring(getClassName().lastIndexOf(".") + 1);
		return str;
	}

	public String getNativeIosWidgetName(boolean viewGroup) {
		if (!viewGroup) {
			if (nativeclassname.contains(".") ) {
				return "ASUIView";
			}
			return nativeclassname;
		}
		
		if (name.contains("Scroll")) {
			return "ASUIScrollView";
		}
		
		if (name.contains("ListView")) {
			return "ASUITableView";
		}
		
		return "ASUIView";
	}
	
	public String getWidgetSuperClass() {
		if (template.indexOf("BaseHasWidgetsTemplate.java") != -1) {
			return "ViewGroupImpl";
		}
		return "ViewImpl";
	}
	
	public String getLocalNameWithoutPackage() {
		if (!getLocalName().contains(".")) {
			return getLocalName();
		}
		return getLocalName().substring(getLocalName().lastIndexOf(".") + 1);
	}
	
	private List<CustomAttribute> widgetAttributes = new ArrayList<>();
	private List<CustomAttribute> constructorAttributes = new ArrayList<>();
	private List<CustomAttribute> layoutAttributes = new ArrayList<>();
	private List<CustomAttribute> dependentAttributes = new ArrayList<>();

	public void setWidgetAttributes(List<CustomAttribute> widgetAttributes) {
		this.widgetAttributes = widgetAttributes;
	}

	public void setLayoutAttributes(List<CustomAttribute> layoutAttributes) {
		this.layoutAttributes = layoutAttributes;
	}
	
	public List<CustomAttribute> getConstructorAttributes() {
		return constructorAttributes;
	}

	public List<CustomAttribute> getAllAttributes() {
        List<CustomAttribute> allAttributes = new ArrayList<>(this.widgetAttributes);
        allAttributes.addAll(layoutAttributes);
        allAttributes.addAll(constructorAttributes);
		return allAttributes;
    }

    public List<CustomAttribute> getDependentAttributes() {
		return dependentAttributes;
	}
	public void setDependentAttributes(List<CustomAttribute> dependentAttributes) {
		this.dependentAttributes = dependentAttributes;
	}
	public List<CustomAttribute> getWidgetAttributes() {
		return widgetAttributes;
	}
	public List<CustomAttribute> getLayoutAttributes() {
		return layoutAttributes;
	}
	public void addClassAttribute(CustomAttribute classConfiguration) {
		if ("yes".equals(classConfiguration.getLayoutAttr())) {
			layoutAttributes.add(classConfiguration);
		} else {
			widgetAttributes.add(classConfiguration);
		}
	}
	
	public void removeDuplicate(String name) {
		boolean found = false;
		List<CustomAttribute> fields = getWidgetAttributes();
		for (Iterator<CustomAttribute> iterator = fields.iterator(); iterator.hasNext();) {
			CustomAttribute classConfiguration2 = iterator.next();

			if (classConfiguration2.getAttribute().equals(name)) {

				if (found) {
					iterator.remove();
				}
				found = true;
			}

		}
	}

	public Map<String, String> getAdditionalAttributes() {
		return additionalAttributes;
	}

	public void setAdditionalAttributes(Map<String, String> additionalAttributes) {
		this.additionalAttributes = additionalAttributes;
	}

	public CustomAttribute getProtoTypeCustomAttribute(String name) {
		if (prototypeCustomAttribute != null) {
			for (CustomAttribute proto : prototypeCustomAttribute) {
				if (proto.getName().equals(name)) {
					return proto;
				}
				
			}
		}
		return null;
	}

    public CustomAttribute addOrUpdateAttribute(CustomAttribute customAttribute) {
    	if (CodeGenHelper.canIgnoreAttribute(os, customAttribute)) {		
    		return null;
    	}
    	if (this.customAttribute == null) {
    		this.customAttribute = new ArrayList<CustomAttribute>();
    	}
    	
    	Optional<CustomAttribute> optCustomAttribute = this.customAttribute.stream().filter((attribute) -> attribute.getName().equals(customAttribute.getName())).findFirst();
    	
    	if (!optCustomAttribute.isPresent()) {
    		this.customAttribute.add(customAttribute); 
    	} else {
    		CustomAttribute existingCustomAttribute = optCustomAttribute.get();
    		mergeObjects(existingCustomAttribute, customAttribute);
    		return existingCustomAttribute;
    	}
    	
    	return customAttribute;
    }
    
    public static <T> void mergeObjects(T first, T second)  {
        try {
			Class<?> clazz = first.getClass();
			Field[] fields = clazz.getDeclaredFields();
			Object returnValue = first;
			for (Field field : fields) {
			    field.setAccessible(true);
			    Object value1 = field.get(first);
			    Object value2 = field.get(second);
			    Object value = (value1 != null) ? value1 : value2;
			    field.set(returnValue, value);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }

	public void addConstructorAttribute(CustomAttribute customAttribute) {
		constructorAttributes.add(customAttribute);
	}
}