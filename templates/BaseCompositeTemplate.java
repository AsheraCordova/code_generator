package com.ashera.ui.${process};
<#include "/templates/Macros.java">
//start - imports
import java.util.*;

import android.annotation.SuppressLint;
import ${androidprefix}android.content.Context;
import ${androidprefix}android.os.Build;
import ${androidprefix}android.view.*;
import ${androidprefix}android.widget.*;
import ${androidprefix}android.view.View.*;

import com.ashera.widget.BaseHasWidgets;

import ${androidprefix}android.annotation.SuppressLint;

import com.ashera.core.IFragment;
import com.ashera.widget.bus.*;
import com.ashera.converter.*;
import com.ashera.widget.bus.Event.*;
import com.ashera.widget.*;
import com.ashera.widget.IWidgetLifeCycleListener.*;
import com.ashera.layout.*;

<#if process == 'android'>
import android.graphics.Canvas;
import android.widget.*;
import android.view.*;
import androidx.core.view.*;
</#if>
<#macro getClassName myclass><#if process == 'android'>${myclass.className}</#if>
<#if process == 'ios'>
<#if myclass.nativeClassName??>${myclass.nativeClassName}<#else>${myclass.className}</#if>
</#if>
<#if process == 'javafx'>
<#if myclass.nativeClassName??>${myclass.nativeClassName}<#else>${myclass.className}</#if>
</#if>
</#macro>
<#if process == 'ios'>

import com.ashera.widget.factory.IWidgetLifeCycleListener.EventId;
/*-[
#include <UIKit/UIKit.h>
#include "ASUIView.h"
#include "HasLifeCycleDecorators.h"
]-*/
import com.google.j2objc.annotations.Property;
</#if>
//end - imports

public class ${myclass.widgetName} extends ${myclass.baseClass} {
    //start - body
    public final static String LOCAL_NAME = "${myclass.localName}"; 
    public final static String GROUP_NAME = "${myclass.group}";       
    <#if (myclass.composites?has_content)>
    <#list myclass.composites as composite>
    private ${composite.className} ${composite.varName};
    </#list>
    </#if>
    
    public ${myclass.widgetName}() {
        super(GROUP_NAME, LOCAL_NAME);
    }

    @Override
    public void create(
    		IFragment fragment,
            java.util.Map<java.lang.String, java.lang.Object> params) {
        super.create(fragment, params);
        nativeCreate(params);
    }

    @Override
    public IWidget newInstance() {
        return new ${myclass.widgetName}();
    }
    
    <#include "/templates/WidgetAttributesClass.java">

    @Override
    public void loadAttributes(String localName) {
        super.loadAttributes(localName);
        <#if (myclass.composites?has_content)>
        <#list myclass.composites as composite>
            new ${composite.className}().loadAttributes(localName);
        </#list>    
        </#if>
        <#include "/templates/WidgetAttributes.java">
    }
    @Override
    public void setAttribute(WidgetAttribute key, String strValue,
            Object objValue, ILifeCycleDecorator decorator) {
        <#if (myclass.baseClass?has_content)>
        super.setAttribute(key, strValue, objValue, decorator);
        </#if>
        Object nativeWidget = asNativeWidget();
        <#if myclass.widgetAttributes?has_content>
        switch (key.getAttributeName()) {
        <#list myclass.widgetAttributes as attrs>
            case "${attrs.trimmedAttribute}": {
                <#if attrs.androidMinVersion??>if (Build.VERSION.SDK_INT >= ${attrs.androidMinVersion}) {</#if>
                    <#if attrs.trimmedSetter?starts_with("code:")>
                        <#if attrs.type != 'nil'>           
                            ${attrs.trimmedSetter?replace("$var", "${myclass.varName}")?replace("code:", "")?replace("$value", "ConverterUtils.convertStringTo${attrs.type}(entry.getValue());")
                                    ?replace("$strvalue", "getAttributeValue(\"${attrs.trimmedAttribute}\")")}
                        </#if>
                    <#else>
                        ${myclass.varName}.${attrs.trimmedSetter}<#if attrs.trimmedSetter?index_of("(") == -1>(</#if><#if attrs.type != 'nil'>(${attrs.javaType})objValue</#if>);
                    </#if>
                <#if attrs.androidMinVersion??>}</#if>
            }
            break;
        </#list>
        default:
            break;
        }
        </#if>

        this.setAttributesOnComposite(key, strValue, objValue, decorator);
    }

	@Override
	@SuppressLint("NewApi")
	public Object getAttribute(WidgetAttribute key, ILifeCycleDecorator decorator) {
		Object attributeValue = ViewGroupImpl.getAttribute(this, key, decorator);
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
	
    <#if process == 'swt'>   
    @Override
    public void setVisible(boolean b) {
        ((View)asWidget()).setVisibility(b ? View.VISIBLE : View.GONE);
    }
    </#if>
    
    ${extraCode}
    <#list myclass.methodDefinitions as methodDefinition>
    ${methodDefinition}
    </#list>
    //end - body
}
