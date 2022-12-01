<#include "/templates/Macros.java">
package com.ashera.ui.${process};
//start - imports
import java.util.*;

<#if process == 'android' || process == 'swt'>
import androidx.core.view.*;
</#if>
import ${androidprefix}android.content.Context;
import ${androidprefix}android.os.Build;
import ${androidprefix}android.view.View;
import ${androidprefix}android.view.ViewGroup;

import ${androidprefix}android.annotation.SuppressLint;

import com.ashera.widget.*;
import com.ashera.converter.*;

import static com.ashera.widget.IWidget.*;
//end - imports
public class ${myclass.widgetName} {
	// start - body
	private ${myclass.widgetName}() {
	}
	<#include "/templates/WidgetAttributesClass.java">
	public static void register(String localName) {
		<#if myclass.widgetName == 'ViewGroupImpl'>
		ViewGroupModelImpl.register(localName);
		<#else>
		ViewImpl.register(localName);
		</#if>
		<#include "/templates/WidgetAttributes.java">
		
		<#list myclass.layoutAttributes as attrs>
		<@widgetFactoryRegisterAttribute attrs=attrs name=attrs.trimmedAttribute forChild=true/>
		<#list attrs.aliases as alais>
		<@widgetFactoryRegisterAttribute attrs=attrs name=alais forChild=true/>
		</#list>		
		</#list>
		<#if myclass.widgetName == 'ViewGroupImpl'>
		WidgetFactory.registerAttribute(localName, new WidgetAttribute.Builder().withName("layout_width").withType("dimension").forChild());
		WidgetFactory.registerAttribute(localName, new WidgetAttribute.Builder().withName("layout_height").withType("dimension").forChild());
		</#if>
	}
	
	@SuppressLint("NewApi")
	public static void setAttribute(IWidget w, WidgetAttribute key, String strValue, Object objValue, ILifeCycleDecorator decorator) {
		ViewGroup viewGroup = ((ViewGroup) w.asWidget());
		<#if process != 'android' && process != 'ios'>
		${myclass.nativeClassName} ${myclass.varName} = (${myclass.nativeClassName}) w.asNativeWidget();
		</#if>
		<#if myclass.widgetName == 'ViewGroupImpl'>
		ViewGroupModelImpl.setAttribute(w, key, strValue, objValue, decorator);
		<#else>
		ViewImpl.setAttribute(w, key, strValue, objValue, decorator);
		</#if>
		<#if myclass.widgetAttributes?has_content>
		switch (key.getAttributeName()) {
		<#list myclass.widgetAttributes as attrs>
		<#list attrs.aliases as alais>
			case "${alais}":
		</#list>
			case "${attrs.trimmedAttribute}": {
				<@generateAttrCode attrs=attrs setter=attrs.trimmedSetter></@generateAttrCode>
			}
			break;
		</#list>
		default:
			break;
		}
		</#if>
	}
	@SuppressLint("NewApi")
	public static void setChildAttribute(IWidget w, WidgetAttribute key, Object objValue, Object layoutParams) {
		<#if myclass.layoutAttributes?has_content>
		switch (key.getAttributeName()) {
		<#list myclass.layoutAttributes as attrs>
			case "${attrs.trimmedAttribute}": {
				<#if attrs.androidMinVersion??>if (Build.VERSION.SDK_INT >= ${attrs.androidMinVersion}) {</#if>
					<#if attrs.trimmedSetter?starts_with("code:")>
						<#if attrs.type != 'nil'>			
							${attrs.trimmedSetter?replace("$var", "layoutParams")?replace("code:", "")?replace("$value", "(${attrs.javaType})objValue;")?replace("$strvalue", "value")}
						</#if>
					<#else>
							layoutParams.${attrs.trimmedSetter}<#if attrs.trimmedSetter?index_of("(") == -1>(</#if><#if attrs.type != 'nil'>(${attrs.javaType})objValue</#if>);
					</#if>
				<#if attrs.androidMinVersion??>}</#if>
			}
			break;
		</#list>
		default:
			break;
		}
		</#if>
	}
	
	@SuppressLint("NewApi")
	public static Object getAttribute(IWidget w, WidgetAttribute key, ILifeCycleDecorator decorator) {
		ViewGroup viewGroup = ((ViewGroup) w.asWidget());
		<#if process != 'android' && process != 'ios'>
		${myclass.nativeClassName} ${myclass.varName} = (${myclass.nativeClassName}) w.asNativeWidget();
		</#if>
		<#if myclass.widgetName == 'ViewGroupImpl'>
		Object attributeValue = ViewGroupModelImpl.getAttribute(w, key, decorator);
		<#else>
		Object attributeValue = ViewImpl.getAttribute(w, key, decorator);
		</#if>
		if (attributeValue != null) {
			return attributeValue;
		}
		
		<#if myclass.widgetAttributes?has_content>
		switch (key.getAttributeName()) {
		<#list myclass.widgetAttributes as attrs>
			<#if attrs.getterMethod?has_content>
			<#list attrs.aliases as alais>
			case "${alais}":
			</#list>
			case "${attrs.trimmedAttribute}": {				
				<@generateGetAttrCode attrs=attrs></@generateGetAttrCode>
			}
			</#if>
		</#list>					
		}
		</#if>
		return null;
	}
	
	@SuppressLint("NewApi")
	public static Object getChildAttribute(IWidget w, WidgetAttribute key) {
		View viewGroup = ((View) w.asWidget());
		
		<#if myclass.layoutAttributes?has_content>
		switch (key.getAttributeName()) {
		<#list myclass.layoutAttributes as attrs>
			<#if attrs.getterMethod?has_content>
			<#list attrs.aliases as alais>
			case "${alais}":
			</#list>
			case "${attrs.trimmedAttribute}": {				
				<@generateChildAttrCode attrs=attrs></@generateChildAttrCode>
			}
			</#if>
		</#list>					
		}
		</#if>
		return null;
	}
	
	${extraCode}
	<#list myclass.methodDefinitions as methodDefinition>
	${methodDefinition}
	</#list>
	
	@SuppressLint("NewApi")
	public static boolean isAttributeSupported(WidgetAttribute key) {
		<#if myclass.widgetAttributes?has_content>
		switch (key.getAttributeName()) {
		<#list myclass.widgetAttributes as attrs>
		<#list attrs.aliases as alais>
			case "${alais}":
		</#list>
			case "${attrs.trimmedAttribute}": {
				return true;
			}
		</#list>
		default:
			break;
		}
		</#if>
		return false;
	}

    <#include "/templates/WidgetBuilderTemplate.java">
	// end - body
}
