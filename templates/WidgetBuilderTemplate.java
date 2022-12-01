<#macro setterMethodParams attrs strip_whitespace=true>
<#compress>
<#if attrs.methodParams?has_content>
<#list attrs.methodParams as param>
<@getTypeScriptType attrs=param></@getTypeScriptType> ${param.attribute}<#if param?has_next>,</#if>
</#list>
<#else>
<#if attrs.type != 'nil'><@getTypeScriptType attrs=attrs></@getTypeScriptType> <@getTypeScriptTypeVar attrs=attrs></@getTypeScriptTypeVar><#if attrs.params?has_content>,<@getTypeScriptType attrs=attrs checkparams=true></@getTypeScriptType> ${attrs.params?keep_before(':')}</#if></#if>
</#if>
</#compress>
</#macro>


<#macro invokeSetterBuilderMethod attrs strip_whitespace=true>
<#compress>
<#if attrs.methodParams?has_content>
<#list attrs.methodParams as param>
${param.attribute}<#if param?has_next>,</#if>
</#list>
<#else>
<#if attrs.type != 'nil'>value<#if attrs.params?has_content>,${attrs.params?keep_before(':')}</#if></#if>
</#if>
</#compress>
</#macro>

<#macro getBuilderReturn params=false>
<#compress>
<#if mywidgetName != 'View' && mywidgetName != 'ViewGroup' && mywidgetName != 'ViewGroupModel'>${mywidgetName}Command<#if params>Params</#if>Builder<#else>T</#if>
</#compress>
</#macro>


<#macro generateBeanMethods attrs alais="" params=false>
<#if attrs.getterMethod?has_content>
public Object <#if alais!=''>${attrs.getGetterForAlias(alais)}<#else>${attrs.getter}</#if>(<#if params>IWidget w</#if>) {
	<#if params>
	java.util.Map<String, Object> layoutParams = new java.util.HashMap<String, Object>();
	java.util.Map<String, Object> command = getParamsBuilder().reset().<#if alais!=''>${attrs.getTryGetterForAlias(alais)}><#else>${attrs.tryGetGetter}</#if>().getCommand();
	
	layoutParams.put("layoutParams", command);
	w.executeCommand(layoutParams, null, COMMAND_EXEC_GETTER_METHOD); 
	return getParamsBuilder().<#if alais!=''>${attrs.getGetterForAlias(alais)}<#else>${attrs.getter}</#if>();
	<#else>
	<#if alais!=''>
	return getBuilder().reset().${attrs.getTryGetterForAlias(alais)}().execute(false).${attrs.getGetterForAlias(alais)}();
	<#else>
	return getBuilder().reset().${attrs.tryGetGetter}().execute(false).${attrs.getter}(); 
	</#if>
	</#if>
}
</#if>
<#if attrs.readOnly??  && attrs.readOnly != 'true'>
public void <#if alais!=''>${attrs.getSetterForAlias(alais)}<#else>${attrs.setter}</#if>(<#if params>IWidget w, </#if><@setterMethodParams attrs=attrs />) {
	<#if params>
	java.util.Map<String, Object> layoutParams = new java.util.HashMap<String, Object>();
	layoutParams.put("layoutParams", getParamsBuilder().reset().<#if alais!=''>${attrs.getSetterForAlias(alais)}<#else>${attrs.setter}</#if>(<@invokeSetterBuilderMethod attrs=attrs/>).getCommand());
	w.executeCommand(layoutParams, null, COMMAND_EXEC_SETTER_METHOD);
	w.getFragment().remeasure();
	<#else>
	<#if alais!=''>
	getBuilder().reset().${attrs.getSetterForAlias(alais)}(<@invokeSetterBuilderMethod attrs=attrs/>).execute(true); 
	<#else>
	get<#if params>Params</#if>Builder().reset().${attrs.setter}(<@invokeSetterBuilderMethod attrs=attrs/>).execute(true);
	</#if>
	</#if>
}

</#if>	
</#macro>

<#macro getBuilderReturnThis>
<#compress>
return <#if mywidgetName == 'View' || mywidgetName == 'ViewGroup' || mywidgetName == 'ViewGroupModel'>(T) </#if>this;
</#compress>
</#macro>
<#macro generateGetterAndSetter attrs alais="" params=false>
<#if attrs.getterMethod?has_content>
public <@getBuilderReturn params=params></@getBuilderReturn> <#if alais == ''>${attrs.tryGetGetter}<#else>${attrs.getTryGetterForAlias(alais)}</#if>() {
	Map<String, Object> attrs = initCommand("${attrs.attributeForTs}");
	attrs.put("type", "attribute");
	attrs.put("getter", true);
	attrs.put("orderGet", ++orderGet);
	<@getBuilderReturnThis></@getBuilderReturnThis> 
}

public Object <#if alais == ''>${attrs.getter}<#else>${attrs.getGetterForAlias(alais)}</#if>() {
	Map<String, Object> attrs = initCommand("${attrs.attributeForTs}");
	return attrs.get("commandReturnValue");
}
</#if>
<#if attrs.readOnly??  && attrs.readOnly != 'true'>public <@getBuilderReturn params=params></@getBuilderReturn> <#if alais == ''>${attrs.setter}<#else>${attrs.getSetterForAlias(alais)}</#if>(<@setterMethodParams attrs=attrs />) {
	Map<String, Object> attrs = initCommand(<#if alais == ''>"${attrs.commandName}"<#else>"${alais}"</#if>);
	attrs.put("type", "attribute");
	attrs.put("setter", true);
	attrs.put("orderSet", ++orderSet);

	<#if attrs.methodParams?has_content>
	Map<String, Object> wrapper = new HashMap<>();
	<#list attrs.methodParams as param>
	wrapper.put("${param.attribute}", ${param.attribute});
	</#list>
	attrs.put("value", wrapper);
	<#elseif attrs.params?has_content>
	Map<String, Object> wrapper = new HashMap<>();
	wrapper.put("${attrs.params?keep_before(':')}", ${attrs.params?keep_before(':')});
	wrapper.put("data", value);
	attrs.put("value", wrapper);
	<#else>
	<#if attrs.type != 'nil'>attrs.put("value", value);</#if>
	</#if>
	<@getBuilderReturnThis></@getBuilderReturnThis>
}</#if>
</#macro>
<#macro getTypeScriptTypeVar attrs strip_whitespace=true>
<#compress>
	<#if attrs.type == 'gravity' || (attrs.converterType?? && attrs.converterType == 'bitflag')>
		value
	<#else>	
	value
	</#if>
</#compress>
</#macro>
<#macro getTypeScriptType attrs strip_whitespace=true checkparams=false>
<#compress>
	<#assign attrType = attrs.type>
	<#if checkparams>
	<#assign attrType = attrs.params?keep_after(':')>
	</#if>
	
	<#if attrType?starts_with("on")>
		String
	<#elseif attrType?starts_with("id") || attrType == 'array'>
    		String
	<#elseif attrType == 'flatmap' || attrType == 'object' >
    		${attrs.typeVariable}
	<#elseif attrType == 'flatmaps'>
		Array<any>	
	<#elseif attrs.supportIntegerAlso>
	 	String
	<#elseif attrType == 'int' || attrType == 'float'>
	 	${attrType}
	<#elseif attrType == 'image' || attrType == 'Image' || attrType == 'colorstate' || attrType == 'color' || attrType == 'String' || attrType=="dimension" || attrType=="dimensionfloat" || attrType=="dimensionsp" || attrType=="colorimage" || attrType=="dimensionspint" || attrType=="dimensionsppxint" || attrType=="drawable" || attrType == 'resourcestring' || attrType == 'template' || attrType='font' || attrType?contains('constraintReferencedIds') || attrType=='style' || attrType='xmlresource'
		|| attrType == 'swtbitflag' || attrType=="dimensionpx"  || attrType == 'string' || attrType=="dimensiondppx">
		String
	<#elseif attrType == 'nil'>
		void
	<#elseif attrType == 'gravity'>
		String
	<#elseif attrType == 'boolean'>
		${attrType}
	<#elseif (attrs.converterType?? && (attrs.converterType == 'bitflag' || attrs.converterType == 'enumtoint'))>
		String
	<#else>	
		${attrs.typeVariable}
	</#if>
</#compress>
</#macro>
<#assign mywidgetName = myclass.widgetName?replace("Impl", "")>
	
<#if mywidgetName != 'View' && mywidgetName!= 'ViewGroup' && mywidgetName!= 'ViewGroupModel'>
private ${mywidgetName}CommandBuilder builder;
private ${mywidgetName}Bean bean;
<#if !myclass.viewplugin>
public Object getPlugin(String plugin) {
	return WidgetFactory.getAttributable(plugin).newInstance(this);
}
</#if>
public ${mywidgetName}Bean getBean() {
	if (bean == null) {
		bean = new ${mywidgetName}Bean();
	}
	return bean;
}
public ${mywidgetName}CommandBuilder getBuilder() {
	if (builder == null) {
		builder = new ${mywidgetName}CommandBuilder();
	}
	return builder;
}
</#if>


public <#if mywidgetName == 'View' || mywidgetName == 'ViewGroup' || mywidgetName == 'ViewGroupModel'>static abstract</#if> class ${mywidgetName}CommandBuilder<#if mywidgetName == 'ViewGroupModel'><T> extends com.ashera.layout.ViewImpl.ViewCommandBuilder<T><#elseif mywidgetName == 'ViewGroup'><T> extends com.ashera.layout.ViewGroupModelImpl.ViewGroupModelCommandBuilder<T><#elseif mywidgetName != 'View'> extends <#if myclass.widgetSuperClass == 'ViewGroupImpl'>com.ashera.layout.ViewGroupImpl.ViewGroupCommandBuilder<#else>com.ashera.layout.ViewImpl.ViewCommandBuilder</#if> <${mywidgetName}CommandBuilder><#else><T></#if> {
	<#if mywidgetName == 'View'>
	protected Map<String, Object> command = new HashMap<>();
	protected int orderGet;
    protected int orderSet;
    
    
    protected abstract <@getBuilderReturn></@getBuilderReturn> execute(boolean set);

    public <@getBuilderReturn></@getBuilderReturn> reset() {
    	orderGet = 0;
    	orderSet = 0;
    	command = new HashMap<>();
    	<@getBuilderReturnThis></@getBuilderReturnThis>
    }
    
    public Map<String, Object> initCommand(String attributeName) {
		Map<String, Object> attrs = (Map<String, Object>) command.get(attributeName);
		if (attrs == null) {
			attrs = new HashMap<>();
			command.put(attributeName, attrs);
		}
		return attrs;
	}
    </#if>
    public ${mywidgetName}CommandBuilder() {
	}
	
    <#if mywidgetName != 'View' && mywidgetName!= 'ViewGroup'  && mywidgetName!= 'ViewGroupModel'>
	public <@getBuilderReturn></@getBuilderReturn> execute(boolean setter) {
		if (setter) {
			<#if myclass.viewplugin>w.</#if>executeCommand(command, null, IWidget.COMMAND_EXEC_SETTER_METHOD);
			<#if myclass.viewplugin>w.</#if>getFragment().remeasure();
		}
		<#if myclass.viewplugin>w.</#if>executeCommand(command, null, IWidget.COMMAND_EXEC_GETTER_METHOD);
		<@getBuilderReturnThis></@getBuilderReturnThis>
	}
	</#if>

	<#list myclass.widgetAttributes as attrs>
	<@generateGetterAndSetter attrs=attrs></@generateGetterAndSetter>
	<#if attrs.attributeForTs != 'id'>		
	<#list attrs.aliases as alais>
	<@generateGetterAndSetter attrs=attrs alais=alais></@generateGetterAndSetter>
	</#list>	
	</#if>
	</#list>
}
<#if mywidgetName == 'View' || mywidgetName == 'ViewGroup' || mywidgetName == 'ViewGroupModel'>
static class ${mywidgetName}CommandBuilderInternal extends ${mywidgetName}CommandBuilder<#if mywidgetName == 'View' || mywidgetName == 'ViewGroup' || mywidgetName == 'ViewGroupModel'><${mywidgetName}CommandBuilderInternal></#if> {
	private IWidget widget;
	public ${mywidgetName}CommandBuilderInternal(IWidget widget) {
		this.widget = widget;
	}
	@Override
	protected ${mywidgetName}CommandBuilderInternal execute(boolean setter) {
		if (setter) {
			widget.executeCommand(command, null, IWidget.COMMAND_EXEC_SETTER_METHOD);
			widget.getFragment().remeasure();
		}
		widget.executeCommand(command, null, IWidget.COMMAND_EXEC_GETTER_METHOD);

		return this;
	}
}
</#if>
public <#if mywidgetName == 'View' || mywidgetName == 'ViewGroup' || mywidgetName == 'ViewGroupModel'>static </#if>class ${mywidgetName}Bean <#if mywidgetName != 'View'>extends <#if myclass.widgetSuperClass == 'ViewGroupImpl'>com.ashera.layout.ViewGroupImpl.ViewGroupBean<#elseif mywidgetName == 'ViewGroup'>com.ashera.layout.ViewGroupModelImpl.ViewGroupModelBean<#else>com.ashera.layout.ViewImpl.ViewBean</#if></#if>{
	<#if mywidgetName == 'View' || mywidgetName == 'ViewGroup' || mywidgetName == 'ViewGroupModel'>
	private ${mywidgetName}CommandBuilderInternal commandBuilder;
	public ${mywidgetName}Bean(IWidget widget) {
		<#if  mywidgetName == 'ViewGroup' || mywidgetName == 'ViewGroupModel'>
		super(widget);
		</#if>
		commandBuilder = new ${mywidgetName}CommandBuilderInternal(widget);
	}
	private ${mywidgetName}CommandBuilderInternal getBuilder() {
		return commandBuilder;
	}
	<#elseif myclass.viewplugin>
		public ${mywidgetName}Bean() {
			super(${mywidgetName}Impl.this.w);
		}
	<#else>
		public ${mywidgetName}Bean() {
			super(${myclass.widgetName}.this);
		}
	</#if>
	<#list myclass.widgetAttributes as attrs>
	<#if attrs.attributeForTs != 'id'>		
	<@generateBeanMethods attrs=attrs></@generateBeanMethods>
	<#list attrs.aliases as alais>
	<@generateBeanMethods attrs=attrs alais=alais></@generateBeanMethods>
	</#list>	
	</#if>
	</#list>
}


<#if myclass.widgetSuperClass == 'ViewGroupImpl' || mywidgetName == 'ViewGroup'>
<#if mywidgetName!= 'ViewGroup'>
private ${mywidgetName}CommandParamsBuilder paramsBuilder;
private ${mywidgetName}ParamsBean paramsBean;

public ${mywidgetName}ParamsBean getParamsBean() {
	if (paramsBean == null) {
		paramsBean = new ${mywidgetName}ParamsBean();
	}
	return paramsBean;
}
public ${mywidgetName}CommandParamsBuilder getParamsBuilder() {
	if (paramsBuilder == null) {
		paramsBuilder = new ${mywidgetName}CommandParamsBuilder();
	}
	return paramsBuilder;
}
</#if>



public <#if mywidgetName == 'ViewGroup'>static </#if>class ${mywidgetName}ParamsBean <#if mywidgetName != 'ViewGroup'>extends com.ashera.layout.ViewGroupImpl.ViewGroupParamsBean</#if>{
	<#if mywidgetName == 'ViewGroup'>
	private ${mywidgetName}CommandParamsBuilderInternal commandBuilder;  
	public ${mywidgetName}ParamsBean() {
		commandBuilder = new ${mywidgetName}CommandParamsBuilderInternal();
	}
	
	private ${mywidgetName}CommandParamsBuilderInternal getParamsBuilder() {
		return commandBuilder;
	}
	</#if>
	<#list myclass.layoutAttributes as attrs>
	<@generateBeanMethods attrs=attrs params=true></@generateBeanMethods>
	<#list attrs.aliases as alais>
	<@generateBeanMethods attrs=attrs alais=alais params=true></@generateBeanMethods>
	</#list>	
	</#list>
}



<#if mywidgetName == 'ViewGroup'>
static class ${mywidgetName}CommandParamsBuilderInternal extends ${mywidgetName}CommandParamsBuilder<${mywidgetName}CommandParamsBuilderInternal> {
}
</#if>


public <#if mywidgetName == 'ViewGroup'>static </#if>class ${mywidgetName}CommandParamsBuilder <#if mywidgetName != 'ViewGroup'>extends com.ashera.layout.ViewGroupImpl.ViewGroupCommandParamsBuilder<${mywidgetName}CommandParamsBuilder><#else><T></#if>{
	<#if mywidgetName == 'ViewGroup'>
	protected Map<String, Object> command = new HashMap<>();
	protected int orderGet;
	protected int orderSet;


	public Map<String, Object> getCommand() {
		return command;
	}

	public <@getBuilderReturn></@getBuilderReturn> reset() {
		orderGet = 0;
		orderSet = 0;
		command = new HashMap<>();
		<@getBuilderReturnThis></@getBuilderReturnThis>
	}

	public Map<String, Object> initCommand(String attributeName) {
		Map<String, Object> attrs = (Map<String, Object>) command.get(attributeName);
		if (attrs == null) {
			attrs = new HashMap<>();
			command.put(attributeName, attrs);
		}
		return attrs;
	}
	</#if>
	<#list myclass.layoutAttributes as attrs>
	<@generateGetterAndSetter attrs=attrs params=true></@generateGetterAndSetter>
	<#list attrs.aliases as alais>
	<@generateGetterAndSetter attrs=attrs alais=alais params=true></@generateGetterAndSetter>
	</#list>	
	</#list>
}
</#if>