package com.ashera.ui.${process};
<#include "/templates/Macros.java">


//start - imports
import java.util.*;

import android.content.*;
import android.graphics.*;
import android.os.Build;
import android.view.*;

import com.ashera.core.IFragment;
import com.ashera.converter.*;
import android.annotation.SuppressLint;

import com.ashera.widget.bus.*;
import com.ashera.widget.*;

<#if process == 'ios'>
/*-[
#include <UIKit/UIKit.h>
#include "ASUIView.h"
]-*/
import com.google.j2objc.annotations.Property;
</#if>
<#if process == 'swt'>
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.*;
</#if>

import static com.ashera.widget.IWidget.*;
<#if process == 'web'>
import org.teavm.jso.dom.html.HTMLElement;
</#if>
//end - imports
@SuppressLint("NewApi")
public class ${myclass.widgetName} extends BaseWidget {
	//start - body
	private View viewStub;
	<#if process == 'swt'>
	private Object pane;
	</#if>
	<#if process == 'web'>
	private HTMLElement htmlElement;
	</#if>
	<#if process == 'ios'>
	protected @Property Object uiView;
	</#if>
	public final static String LOCAL_NAME = "${myclass.localName}"; 
	public final static String GROUP_NAME = "${myclass.group}";
	
	@Override
	public void loadAttributes(String attributeName) {
		ViewImpl.register(attributeName);

		<#include "/templates/WidgetAttributes.java">

	}
	
	public ${myclass.widgetName}() {
		super(LOCAL_NAME, LOCAL_NAME);
	}	

	@Override
	public IWidget newInstance() {
		return new ${myclass.widgetName}();
	}
	
	@SuppressLint("NewApi")
	@Override
	public void create(IFragment fragment, Map<String, Object> params) {
		super.create(fragment, params);
		
		<#if process == 'swt'>
        viewStub = new View();
        pane = new Composite(ViewImpl.getParent(this), SWT.NONE);
        ViewImpl.nativeMakeFrame(pane, 0, 0, 0, 0);
        </#if>
        <#if process == 'ios'>
        viewStub = new View();
        createView();
        ViewImpl.nativeMakeFrame(uiView, 0, 0, 0, 0);
        </#if>
        <#if process == 'web'>
        viewStub = new View();
        createView();
        ViewImpl.nativeMakeFrame(htmlElement, 0, 0, 0, 0);
        </#if>
        <#if process == 'android'>
        viewStub = new ViewStub((Context) fragment.getRootActivity());
        </#if>
		nativeCreate(fragment, params);	
	}
	

    <#if process == 'ios'>
    public native void createView()/*-[
		ASUIView* uiView = [ASUIView new];
		uiView.backgroundColor = [UIColor clearColor];
		uiView_ = uiView;
	]-*/;
    </#if>  
    
    <#if process == 'web'>
    private void createView() {
    	htmlElement = org.teavm.jso.dom.html.HTMLDocument.current().createElement("div");
    	htmlElement.getStyle().setProperty("overflow", "hidden");
    	htmlElement.getStyle().setProperty("box-sizing", "border-box");
    }
    </#if>

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
	public Object asWidget() {
		return viewStub;
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
	<#list myclass.methodDefinitions as methodDefinition>
	${methodDefinition}
	</#list>
	
	${extraCode}

    <#if process == 'swt'>   
    @Override
    public void setVisible(boolean b) {
        ((View)asWidget()).setVisibility(b ? View.VISIBLE : View.GONE);
    }
    </#if>
    <#if myclass.createDefault?contains("asnativewidget|")>
    @Override
    public Object asNativeWidget() {
        <#if process == 'android'>
        return ${myclass.varName};
        </#if>
        <#if process == 'swt'>
        return pane;
        </#if>
        <#if process == 'ios'>
        return uiView;
        </#if>
        <#if process == 'web'>
        return htmlElement;
        </#if>
        
    }
    </#if>  
    <#if myclass.createDefault?contains("nativecreate|")>
    private void nativeCreate(IFragment fragment, Map<String, Object> params) {
    }
    
    </#if> 
	//end - body
}
