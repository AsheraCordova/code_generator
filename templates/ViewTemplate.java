<#include "/templates/Macros.java">
package com.ashera.ui.${process};
//start - imports
import java.util.*;

import ${androidprefix}android.content.Context;
import ${androidprefix}android.os.Build;
import ${androidprefix}android.view.View;
<#if process == 'android' || process == 'swt' || process='web'>
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
<#if process == 'web'>
import org.teavm.jso.dom.html.HTMLElement;
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
public class ${myclass.widgetName} {
	// start - body
	private ${myclass.widgetName}() {
	}
	
	<#include "/templates/WidgetAttributesClass.java">
	
	@SuppressLint("NewApi")
	public static void register(String localName) {
		<#include "/templates/WidgetAttributes.java">
		
		java.util.List<IAttributable> attributables = WidgetFactory.getAttributables("View", localName);
		if (attributables != null) {
			for (IAttributable attributable : attributables) {
				attributable.loadAttributes(localName);
			}
		}
	}

	@SuppressLint("NewApi")
	public static void setAttribute(IWidget w, WidgetAttribute key, String strValue, Object objValue, ILifeCycleDecorator decorator) {
		setAttribute(w, w.asNativeWidget(), key, strValue, objValue, decorator);
	}
	@SuppressLint("NewApi")
	public static void setAttribute(IWidget w, SimpleWrapableView wrapperView, WidgetAttribute key, String strValue, Object objValue, ILifeCycleDecorator decorator) {
		if (wrapperView.isViewWrapped() && key.getSimpleWrapableViewStrategy() != 0) {
			if ((key.getSimpleWrapableViewStrategy() & IWidget.APPLY_TO_VIEW_WRAPPER) != 0) {
				setAttribute(w, wrapperView.getWrappedView(), key, strValue, objValue, decorator);
			}
			
			if ((key.getSimpleWrapableViewStrategy() & IWidget.APPLY_TO_VIEW_HOLDER) != 0) {
				setAttribute(w, wrapperView.getWrapperViewHolder(), key, strValue, objValue, decorator);
			}
			
			if (((key.getSimpleWrapableViewStrategy() & IWidget.APPLY_TO_FOREGROUND) != 0) && wrapperView.getForeground() != null) {
				setAttribute(w, wrapperView.getForeground(), key, strValue, objValue, decorator);
			}
		} else {
			setAttribute(w, w.asNativeWidget(), key, strValue, objValue, decorator);
		}
	}
	@SuppressLint("NewApi")
	private static void setAttribute(IWidget w, Object nativeWidget, WidgetAttribute key, String strValue, Object objValue, ILifeCycleDecorator decorator) {
		View view = (View) w.asWidget();
		<#if process != 'android' && process != 'ios'>
		${myclass.nativeClassName} ${myclass.varName} = (${myclass.nativeClassName}) nativeWidget;
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
			java.util.List<IAttributable> attributables = WidgetFactory.getAttributables("View", w.getLocalName());
			if (attributables != null) {
				for (IAttributable attributable : attributables) {
					attributable.newInstance(w).setAttribute(key, strValue, objValue, decorator);
				}
			}
			break;
		}
		</#if>
	}
	
	@SuppressLint("NewApi")
	public static Object getAttribute(IWidget w, WidgetAttribute key, ILifeCycleDecorator decorator) {
		return getAttribute(w, w.asNativeWidget(), key, decorator);
	}
	
	@SuppressLint("NewApi")
	public static Object getAttribute(IWidget w, Object nativeWidget, WidgetAttribute key, ILifeCycleDecorator decorator) {
		View view = (View) w.asWidget();
		<#if process != 'android' && process != 'ios'>
		${myclass.nativeClassName} ${myclass.varName} = (${myclass.nativeClassName}) nativeWidget;
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
		
		java.util.List<IAttributable> attributables = WidgetFactory.getAttributables("View", w.getLocalName());
		if (attributables != null) {
			for (IAttributable attributable : attributables) {
				Object value = attributable.newInstance(w).getAttribute(key, decorator);
				
				if (value != null) {
					return value;
				}
			}
		}
		return null;
	}
	<#if process == 'ios'>
    public static native boolean checkIosVersion(String v) /*-[
		return ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] == NSOrderedDescending);
	]-*/;
    </#if>
	${extraCode}
	<#list myclass.methodDefinitions as methodDefinition>
	${methodDefinition}
	</#list>

    <#include "/templates/WidgetBuilderTemplate.java">
	// end - body
}
