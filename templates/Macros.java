<#function getBitFlagValue val>
	<#assign returnVal='0x${val}'>
	
	<#if val?starts_with('-')>
		<#assign returnVal='-0x${val?replace("-", "")}'>
	</#if>
	
	<#if val?contains(".")>
		<#assign returnVal='${val}'>
	</#if>
	
	<#return returnVal>
</#function>
<#macro generateAttrCode attrs setter quickConvertPrefix=true>
	<#if attrs.methodParams?has_content>
		if (objValue instanceof Map) {
			Map<String, Object> data = ((Map<String, Object>) objValue);
		<#list attrs.methodParams as param>
		Object ${param.attribute} = <#if quickConvertPrefix>w.</#if>quickConvert(data.get("${param.attribute}"), "<#if param.converterType?? && (param.converterType == 'enumtoint' || param.converterType == 'stringtoenum' || param.converterType == 'bitflag')>${myclass.localName}.</#if>${param.type}");
		</#list>
	</#if>
	<@generateAttrCode_setter attrs=attrs setter=setter quickConvertPrefix=quickConvertPrefix></@generateAttrCode_setter>
<#if attrs.methodParams?has_content>}</#if>
<#if attrs.methodParams?has_content>
if (objValue instanceof java.util.List) {
	java.util.List<Object> list = (java.util.List<Object>) objValue;
	for (Object object : list) {
		Map<String, Object> data = PluginInvoker.getMap(object);
		<#list attrs.methodParams as param>
		Object ${param.attribute} = <#if quickConvertPrefix>w.</#if>quickConvert(data.get("${param.attribute}"), "<#if param.converterType?? && (param.converterType == 'enumtoint' || param.converterType == 'stringtoenum' || param.converterType == 'bitflag')>${myclass.localName}.</#if>${param.type}");
		</#list>
		<@generateAttrCode_setter attrs=attrs setter=setter quickConvertPrefix=quickConvertPrefix></@generateAttrCode_setter>
	}
}
</#if>	
</#macro>

<#macro generateAttrCode_setter attrs setter quickConvertPrefix=true>
<#if attrs.androidMinVersion??>if (Build.VERSION.SDK_INT >= ${attrs.androidMinVersion}) {</#if><#if attrs.iosMinVersion??>if (checkIosVersion("${attrs.iosMinVersion}")) {</#if>
${attrs.preCode}
<#if attrs.disposable && attrs.getterMethod?has_content && process = 'swt'>	
		disposeAll(<@generateGetAttrCode attrs=attrs returnStr="" semiColon=""></@generateGetAttrCode>);
</#if>
<#if setter?starts_with("code:")>
		${setter?replace("$var", "${myclass.varName}")?replace("code:", "")?replace("$value", "(${attrs.javaType})objValue;")
				?replace("$strvalue", "getAttributeValue(\"${attrs.trimmedAttribute}\")")}
<#else>
	${myclass.varName}.${setter}<#if setter?index_of("(") == -1>(</#if><#if attrs.type != 'nil'>(${attrs.javaType})objValue</#if>);
</#if>${attrs.extraCode}
<#if attrs.androidMinVersion?? || attrs.iosMinVersion??>}</#if>
</#macro>

<#macro initAndroidWidget myclass>
Context context = (Context) fragment.getRootActivity();
<#if myclass.createDefault?contains("ExcludeStyle|")>
	${myclass.varName} = new <@getWidgetClassNameShortName myclass=myclass></@getWidgetClassNameShortName>Ext(context);
<#else>
	Object systemStyle = params.get("systemStyle");
	Object systemAndroidAttrStyle = params.get("systemAndroidAttrStyle");
	
	if (systemStyle == null && systemAndroidAttrStyle == null) {
		${myclass.varName} = new <@getWidgetClassNameShortName myclass=myclass></@getWidgetClassNameShortName>Ext(context);
	} else {
		int defStyleAttr = 0;
		int defStyleRes = 0;
		
		if (systemStyle != null) {
			defStyleRes = context.getResources().getIdentifier((String) systemStyle, "style", context.getPackageName());	
		}
		
		if (systemAndroidAttrStyle != null) {
			defStyleAttr = context.getResources().getIdentifier((String) systemAndroidAttrStyle, "attr", "android");	
		}
		
		if (defStyleRes == 0) {
			${myclass.varName} = new <@getWidgetClassNameShortName myclass=myclass></@getWidgetClassNameShortName>Ext(context, null, defStyleAttr);	
		} else {
			<#if !myclass.createDefault?contains("ExcludeDefStyleRes|")>
			${myclass.varName} = new <@getWidgetClassNameShortName myclass=myclass></@getWidgetClassNameShortName>Ext(context, null, defStyleAttr, defStyleRes);
			</#if>
		}
		
	}
</#if>

</#macro>

<#macro generateGetAttrCode attrs returnStr="return" semiColon=";">
<#compress>
<#if attrs.androidMinVersionForGet??>if (Build.VERSION.SDK_INT >= ${attrs.androidMinVersionForGet}) {</#if><#if attrs.iosMinVersion??>if (checkIosVersion("${attrs.iosMinVersion}")) {</#if>
	<#if attrs.getterMethod?starts_with("code:")>
			${returnStr} ${attrs.getGetterCode(process)?replace("$var", "${myclass.varName}")?replace("code:", "")?replace("$value", "(${attrs.javaType})objValue;")
					?replace("$strvalue", "getAttributeValue(\"${attrs.trimmedAttribute}\")")}${semiColon}
	<#else>
	${returnStr} ${myclass.varName}.${attrs.getterMethod}<#if attrs.getterMethod?index_of("()") == -1>()</#if>${semiColon}
	</#if>
<#if attrs.androidMinVersionForGet?? || attrs.iosMinVersion??>}
break;</#if>
</#compress>
</#macro>

<#macro generateChildAttrCode attrs returnStr="return" semiColon=";">
<#compress>
<#if attrs.androidMinVersionForGet??>if (Build.VERSION.SDK_INT >= ${attrs.androidMinVersionForGet}) {</#if>
	<#if attrs.getterMethod?starts_with("code:")>
			${returnStr} ${attrs.getGetterCode(process)?replace("$var", "layoutParams")?replace("code:", "")?replace("$value", "(${attrs.javaType})objValue;")
					?replace("$strvalue", "value)")}${semiColon}
	<#else>
	${returnStr} layoutParams.${attrs.getterMethod}${semiColon}
	</#if>
<#if attrs.androidMinVersionForGet??>}
break;</#if>
</#compress>
</#macro>

<#macro getClassName myclass>
<#compress>
<#if process == 'android'>${myclass.className}</#if>
<#if process == 'ios'>
<#if myclass.nativeClassName??>${myclass.nativeClassName}<#else>${myclass.className}</#if>
</#if>
<#if process == 'swt'>
<#if myclass.nativeClassName??>${myclass.nativeClassName}<#else>${myclass.className}</#if>
</#if>
</#compress>
</#macro>

<#macro getWidgetClassName myclass>
<#compress>
<#if process == 'android'>${myclass.className}</#if>
<#if process == 'ios'><#if viewgroup == true>${myclass.nativeClassName}<#else>${myclass.widgetClassname}</#if></#if>
<#if (process == 'swt' || process == 'web')><#if viewgroup == true>${myclass.nativeClassName}<#else>${myclass.widgetClassname}</#if></#if>
</#compress>
</#macro>

<#macro getWidgetClassNameShortName myclass>
<#compress>
${myclass.widgetName?replace("Impl", "")}
</#compress>
</#macro>
<#macro widgetFactoryRegisterAttribute attrs name forChild=false>
		WidgetFactory.registerAttribute(localName, new WidgetAttribute.Builder().withName("${name}").withType("<#if attrs.converterType??>${myclass.localName}.</#if>${attrs.type}")<#if attrs.arrayType??>.withArrayType("${attrs.arrayType}")</#if><#if attrs.arrayListToFinalType??>.withArrayListToFinalType("${attrs.arrayListToFinalType}")</#if><#if attrs.order != "0">.withOrder(${attrs.order})</#if><#if attrs.decorator?? && attrs.decorator != "null">.withDecorator(${attrs.decorator})</#if><#if attrs.bufferStrategy != "BUFFER_STRATEGY_NONE">.withBufferStrategy(${attrs.bufferStrategy})</#if><#if attrs.updateUiFlag != "UPDATE_UI_NONE">.withUiFlag(${attrs.updateUiFlag})</#if><#if attrs.applyBeforeChildAdd>.beforeChildAdd()</#if><#if forChild>.forChild()</#if><#if attrs.simpleWrapableViewStrategy??>.withSimpleWrapableViewStrategy(${attrs.simpleWrapableViewStrategy})</#if>);
</#macro>