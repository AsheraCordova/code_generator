package com.ashera.ui.${process};
<#include "/templates/Macros.java">
//start - imports
import java.util.*;

import android.content.*;
import android.graphics.*;
import android.os.Build;
import android.view.*;

import com.ashera.converter.*;
import com.ashera.widget.BaseWidget;

import android.annotation.SuppressLint;

import com.ashera.core.IFragment;
import com.ashera.widget.bus.*;
import com.ashera.widget.*;
<#if process == 'ios'>
/*-[
#include <UIKit/UIKit.h>
#include "FileSystemImpl.h"
]-*/
import com.google.j2objc.annotations.Property;

</#if>

import static com.ashera.widget.IWidget.*;
//end - imports
@SuppressLint("NewApi")
public class ${myclass.widgetName} extends BaseWidget {
	//start - body
	private View viewStub;
	<#if process == 'swt'>
	private Object pane;
	</#if>
	<#if process == 'ios'>
	protected @Property Object uiView;
	</#if>
	public final static String LOCAL_NAME = "${myclass.localName}"; 
	public final static String GROUP_NAME = "${myclass.group}";
	
	@Override
	public void setChildAttribute(IWidget widget, WidgetAttribute key,
			String strValue, Object value) {
	}


	@Override
	public void loadAttributes(String attributeName) {
		ViewGroupImpl.register(attributeName);
		<#include "/templates/WidgetAttributes.java">
	}
	
	public ${myclass.widgetName}() {
		super(GROUP_NAME, LOCAL_NAME);
	}


	@Override
	public IWidget newInstance() {
		return new ${myclass.widgetName}();
	}
	
	public class ${myclass.localNameWithoutPackage} extends android.widget.FrameLayout {
		public ${myclass.localNameWithoutPackage}(<#if process == 'android'>Context context</#if>) {
			<#if process == 'android'>super(context);</#if>
			<#if (process == 'ios' && viewgroup)>super();</#if>
			<#if (process == 'ios' && !viewgroup)>super(${myclass.widgetName}.this);</#if>
			<#if (process == 'swt' && viewgroup)>super();</#if>
			<#if (process == 'swt' && !viewgroup)>super(${myclass.widgetName}.this);</#if>
		}
	}
	
	
	@SuppressLint("NewApi")
	@Override
	public void create(IFragment fragment, Map<String, Object> params) {
		super.create(fragment, params);

		<#if process == 'ios'>
		viewStub = new View();
		</#if>
		<#if process == 'swt'>
		viewStub = new View();
		</#if>
		<#if process == 'android'>
		viewStub = new ViewStub((Context) fragment.getRootActivity());
		</#if>
		<#if process == 'swt'>
		<#if myclass.widgetName == 'WebViewImpl'>
		nativeSet(createWebView());
		<#else>
		nativeSet(nativeImpl.nativeUIViewCreate(fragment));
		</#if>
		nativeImpl.nativeMakeFrame(asNativeWidget(), 0, 0, 0 ,0);		
		</#if>
		<#if process == 'ios'>
		<#if myclass.widgetName == 'WebViewImpl'>
		nativeSet(createWebView());
		<#else>
		nativeSet(nativeImpl.nativeUIViewCreate(fragment));
		</#if>
		nativeImpl.nativeMakeFrame(asNativeWidget(), 0, 0, 0 ,0);
		</#if>
		<#if process == 'android'>
		
		<#if myclass.widgetName == 'WebViewImpl'>
		viewStub = createWebView();
		</#if>
		</#if>
	}


	@Override
	@SuppressLint("NewApi")
	public void setAttribute(WidgetAttribute key, String strValue, Object objValue, ILifeCycleDecorator decorator) {		
		Object nativeWidget = asNativeWidget();
		ViewImpl.setAttribute(this, key, strValue, objValue, decorator);

		<#if myclass.widgetAttributes?has_content>
		switch (key.getAttributeName()) {
		<#list myclass.widgetAttributes as attrs>
			<#list attrs.aliases as alais>
			case "${alais}":
			</#list>			
			case "${attrs.trimmedAttribute}": {
				<#if attrs.setterMethodNative??>
					<@generateAttrCode attrs=attrs setter=attrs.setterMethodNative></@generateAttrCode>
				</#if>
				
				<@generateAttrCode attrs=attrs setter=attrs.trimmedSetter></@generateAttrCode>
			}
			break;
		</#list>
		default:
			break;
		}
		</#if>
	}
	
	@Override
	@SuppressLint("NewApi")
	public Object getAttribute(WidgetAttribute key, ILifeCycleDecorator decorator) {
		Object attributeValue = ViewImpl.getAttribute(this, key, decorator);
		if (attributeValue != null) {
			return attributeValue;
		}
		Object nativeWidget = asNativeWidget();
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
	@Override
	public Object asWidget() {
		return viewStub;
	}
	
	<#if process == 'android'>
	@Override
	public java.lang.Object asNativeWidget() {
		return viewStub;
	}
	</#if>
	<#if process == 'ios'>
	private native void nativeSet(Object view)/*-[
		self.uiView = view;
	]-*/;
	public native Object asNativeWidget()/*-[
		return self.uiView;
	]-*/;
	</#if>

    
    <#if process == 'swt'>   
    @Override
    public void setVisible(boolean b) {
        ((View)asWidget()).setVisibility(b ? View.VISIBLE : View.GONE);
    }
    </#if>
	
	<#if process == 'swt'>
	private void nativeSet(Object view) {
		this.pane = view;
	}
	public Object asNativeWidget() {
		return this.pane;
	}
	</#if>
	
	<#list myclass.methodDefinitions as methodDefinition>
	${methodDefinition}
	</#list>
	
	${extraCode}
	//end - body
}
