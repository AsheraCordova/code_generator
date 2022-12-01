<#include "/templates/Macros.java">
package com.ashera.ui.${process};
//start - imports
import java.util.*;

import ${androidprefix}android.content.Context;
import ${androidprefix}android.os.Build;
import ${androidprefix}android.view.View;
import android.annotation.SuppressLint;
<#if process == 'android' || process == 'swt'>
import androidx.core.view.*;
</#if>
import ${androidprefix}android.annotation.SuppressLint;

import com.ashera.widget.*;
import com.ashera.converter.*;
<#if process == 'android'>
import android.widget.*;
import android.view.*;
import android.graphics.*;
import android.content.res.*;
</#if>
<#if process == 'swt'>
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;
import static com.ashera.common.DisposeUtil.*;
</#if>

<#if process == 'ios'>
/*-[
#include <UIKit/UIKit.h>
#include "ASUIView.h"
]-*/
import com.google.j2objc.annotations.Property;
import androidx.core.view.*;
</#if>

import static com.ashera.widget.IWidget.*;
//end - imports
public class ${myclass.widgetName} implements com.ashera.widget.IAttributable {
	// start - body
	public final static String LOCAL_NAME = "${myclass.localName}"; 
	private IWidget w;
	private ${myclass.widgetName}(IWidget widget) {
		this.w = widget;
	}
	
	public String getLocalName() {
		return LOCAL_NAME;
	}
	
	public ${myclass.widgetName}() {
	}
	
	@Override
	public com.ashera.widget.IAttributable newInstance(IWidget widget) {
		${myclass.widgetName} newIntance = new ${myclass.widgetName}(widget);
		<#if myclass.createDefault?contains("retainInstance|")>widget.getFragment().addListener(widget, newIntance);</#if>
		return newIntance;
	}
	
	<#include "/templates/WidgetAttributesClass.java">
	
	@SuppressLint("NewApi")
	@Override
	public void loadAttributes(String localName) {
		<#include "/templates/WidgetAttributes.java">
	}

	@SuppressLint("NewApi")
	@Override
	public void setAttribute(WidgetAttribute key, String strValue, Object objValue, ILifeCycleDecorator decorator) {
		View view = (View) w.asWidget();
		<#if process != 'android' && process != 'ios'>
		${myclass.nativeClassName} ${myclass.varName} = (${myclass.nativeClassName}) w.asNativeWidget();
		</#if>
		<#if process == 'ios'>
		Object nativeWidget = w.asNativeWidget();
		</#if>
		<#if myclass.widgetAttributes?has_content>
		switch (key.getAttributeName()) {
		<#list myclass.widgetAttributes as attrs>
		<#list attrs.aliases as alais>
			case "${alais}":
		</#list>		
		<#if attrs.readOnly??  && attrs.readOnly != 'true'>case "${attrs.trimmedAttribute}": {
				<@generateAttrCode attrs=attrs setter=attrs.trimmedSetter></@generateAttrCode>
			}
			break;</#if>
		</#list>
		default:
			break;
		}
		</#if>
	}
	
	@SuppressLint("NewApi")
	@Override
	public Object getAttribute(WidgetAttribute key, ILifeCycleDecorator decorator) {
		View view = (View) w.asWidget();
		<#if process != 'android' && process != 'ios'>
		${myclass.nativeClassName} ${myclass.varName} = (${myclass.nativeClassName}) w.asNativeWidget();
		</#if>
		<#if process == 'ios'>
		Object nativeWidget = w.asNativeWidget();
		</#if>
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
	
	${extraCode}
	<#list myclass.methodDefinitions as methodDefinition>
	${methodDefinition}
	</#list>

    <#include "/templates/WidgetBuilderTemplate.java">
	// end - body
}
