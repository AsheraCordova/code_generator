<#macro registerAttribute attrs registerAttr=true>
		<#if !attrs.codeExtensionOnly><#if attrs.keys?has_content>
		<#if attrs.converterType == "enumtoint">
		ConverterFactory.register("${myclass.localName}.${attrs.type}", new ${attrs.typeVariable}());
		</#if>
		<#if attrs.converterType == "stringtoenum">
		ConverterFactory.register("${myclass.localName}.${attrs.type}", new ${attrs.typeVariable}());
		</#if>
		<#if attrs.converterType == "bitflag">
		ConverterFactory.register("${myclass.localName}.${attrs.type}", new ${attrs.typeVariable}());
		</#if>
		</#if>
		<#if registerAttr>
		<@widgetFactoryRegisterAttribute attrs=attrs name=attrs.trimmedAttribute />
		<#list attrs.aliases as alais>
		<@widgetFactoryRegisterAttribute attrs=attrs name=alais />
		</#list>
		</#if></#if>
</#macro>

	<#list myclass.widgetAttributes as attrs>
		<@registerAttribute attrs=attrs />
		<#if attrs.methodParams?has_content>
		<#list attrs.methodParams as param>
		<@registerAttribute attrs=param registerAttr=false />
		</#list>
		</#if>
	</#list>
	<#list myclass.constructorAttributes as attrs>
	WidgetFactory.registerConstructorAttribute(localName, new WidgetAttribute.Builder().withName("${attrs.trimmedAttribute}").withType("<#if attrs.converterType??>${myclass.localName}.</#if>${attrs.type}")<#if attrs.arrayType??>.withArrayType("${attrs.arrayType}")</#if><#if attrs.arrayListToFinalType??>.withArrayListToFinalType("${attrs.arrayListToFinalType}")</#if><#if attrs.order != "0">.withOrder(${attrs.order})</#if><#if attrs.decorator?? && attrs.decorator != "null">.withDecorator(${attrs.decorator})</#if><#if attrs.bufferStrategy != "BUFFER_STRATEGY_NONE">.withBufferStrategy(${attrs.bufferStrategy})</#if><#if attrs.updateUiFlag != "UPDATE_UI_NONE">.withUiFlag(${attrs.updateUiFlag})</#if><#if attrs.applyBeforeChildAdd>.beforeChildAdd()</#if><#if false>.forChild()</#if><#if attrs.simpleWrapableViewStrategy??>.withSimpleWrapableViewStrategy(${attrs.simpleWrapableViewStrategy})</#if>);
	</#list>