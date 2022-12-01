package com.ashera.ui.${process};
<#include "/templates/Macros.java">
// start - imports
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
import androidx.core.view.*;
import android.view.*;
</#if>
<#if process == 'swt'>
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
<#if process == 'web'>
import org.teavm.jso.dom.html.HTMLElement;
</#if>
<#if process == 'ios'>
/*-[
#include <UIKit/UIKit.h>
#include "ASUIView.h"
#include "HasLifeCycleDecorators.h"
]-*/
import com.google.j2objc.annotations.Property;
import androidx.core.view.*;
</#if>

import static com.ashera.widget.IWidget.*;
//end - imports

public class ${myclass.widgetName} extends BaseHasWidgets {
	//start - body
	<#if process == 'ios'>
	private @Property Object uiView;
	</#if>
	<#if process == 'swt'>
	private Object pane;
	</#if>
	<#if process == 'web'>
	private HTMLElement htmlElement;
	</#if>
	<#if myclass.createDefault?contains("createcanvas|")>
	private r.android.graphics.Canvas canvas;
	</#if>
	public final static String LOCAL_NAME = "${myclass.localName}"; 
	public final static String GROUP_NAME = "${myclass.group}";
	private ${myclass.className} ${myclass.varName};
	
	<#if myclass.createDefault?contains("premeasurehandler|")>
	private String PREMEASURE_EVENT = com.ashera.widget.bus.Event.StandardEvents.preMeasure.toString();
	@com.google.j2objc.annotations.WeakOuter
	class PreMeasureHandler extends com.ashera.widget.bus.EventBusHandler {

		public PreMeasureHandler(String type) {
			super(type);
		}

		@Override
		protected void doPerform(Object payload) {
			handlePreMeasure(payload);
		}
		
	}
	private void addPremeasureHandler() {
		fragment.getEventBus().on(PREMEASURE_EVENT, new PreMeasureHandler(PREMEASURE_EVENT));
	}
	</#if>
	<#if myclass.createDefault?contains("deallochandler|")>
	private final static String DELLOC_EVENT = com.ashera.widget.bus.Event.StandardEvents.dealloc.toString();
	@com.google.j2objc.annotations.WeakOuter
	class DallocHandler extends com.ashera.widget.bus.EventBusHandler {

		public DallocHandler(String type) {
			super(type);
		}

		@Override
		protected void doPerform(Object payload) {
			${myclass.varName}.release();
		}
		
	}
	private void addDellocHandler() {
		fragment.getEventBus().on(DELLOC_EVENT, new DallocHandler(DELLOC_EVENT));
	}
	</#if>

	
	<#include "/templates/WidgetAttributesClass.java">
	@Override
	public void loadAttributes(String localName) {
		<#if (myclass.baseClass?has_content)>
		super.loadAttributes(localName);
		</#if>
		ViewGroupImpl.register(localName);
		<#include "/templates/WidgetAttributes.java">
	
	<#list myclass.layoutAttributes as attrs>
	<#if attrs.keys?has_content>
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
	<@widgetFactoryRegisterAttribute attrs=attrs name=attrs.trimmedAttribute forChild=true/>
	</#list>
	<#if myclass.createDefault?contains("loadAttributes|")>
	loadCustomAttributes(localName);
	</#if>
	}
	
	public ${myclass.widgetName}() {
		super(GROUP_NAME, LOCAL_NAME);
	}
	public  ${myclass.widgetName}(String localname) {
		super(GROUP_NAME, localname);
	}
	public  ${myclass.widgetName}(String groupName, String localname) {
		super(groupName, localname);
	}

	@Override
	public IWidget newInstance() {
		return new ${myclass.widgetName}();
	}
	
	@SuppressLint("NewApi")
	@Override
	public void create(IFragment fragment, Map<String, Object> params) {
		super.create(fragment, params);
		<#if process == 'android'>
		<@initAndroidWidget myclass=myclass></@initAndroidWidget>
		<#else>
		${myclass.varName} = new <@getWidgetClassNameShortName myclass=myclass></@getWidgetClassNameShortName>Ext();
		</#if>
		
		nativeCreate(params);
		<#if myclass.createDefault?contains("createcanvas|")>
        createCanvas();
        </#if>
		<#if myclass.createDefault?contains("deallochandler|")>
		addDellocHandler();
		</#if>
		<#if myclass.createDefault?contains("premeasurehandler|")>
		addPremeasureHandler();
		</#if>
		
		<#if myclass.widgetName == 'RootImpl'>
		if (ViewImpl.isRTLLayout(this)) {
			ViewImpl.setLayoutDirection(this, View.LAYOUT_DIRECTION_RTL);
		} else {
			ViewImpl.setLayoutDirection(this, View.LAYOUT_DIRECTION_LTR);
		}
		</#if>
		
		ViewGroupImpl.registerCommandConveter(this);
		<#if process == 'ios'>
		setWidgetOnNativeClass();
		</#if>
	}
	<#if process == 'ios'>
	private native void setWidgetOnNativeClass() /*-[
		((${myclass.getNativeIosWidgetName(true)}*) [self asNativeWidget]).widget = self;
	]-*/;
	</#if>

	@Override
	public Object asWidget() {
		return ${myclass.varName};
	}

	<#if !myclass.createDefault?contains("skipRemoveMethods|")>
	@Override
	public boolean remove(IWidget w) {
		boolean remove = super.remove(w);
		${myclass.varName}.removeView((View) w.asWidget());
		 <#if process == 'swt' || process == 'ios' || process == 'web'>
         ViewGroupImpl.nativeRemoveView(w);            
         </#if>
		return remove;
	}
	
	@Override
    public boolean remove(int index) {
		IWidget widget = widgets.get(index);
        boolean remove = super.remove(index);

        if (index + 1 <= ${myclass.varName}.getChildCount()) {
            ${myclass.varName}.removeViewAt(index);
            <#if process == 'swt' || process == 'ios' || process == 'web'>
            ViewGroupImpl.nativeRemoveView(widget);            
            </#if>
        }    
        return remove;
    }
	</#if>
	
	@Override
	public void add(IWidget w, int index) {
		if (index != -2) {
			View view = (View) w.asWidget();
			createLayoutParams(view);
			<#if myclass.widgetName == 'TabLayoutImpl'&& process == 'android'>
			<#elseif myclass.widgetName == 'DrawerLayoutImpl'>
			handleChildAddition(w, index, view);
			<#else>
			    if (index == -1) {
			        ${myclass.varName}.addView(view);
			    } else {
			        ${myclass.varName}.addView(view, index);
			    }
			</#if>
		}
		
		<#if !myclass.createDefault?contains("skipNativeAddView|")>
		ViewGroupImpl.nativeAddView(asNativeWidget(), w.asNativeWidget());
		</#if>
		super.add(w, index);
	}
	
	private void createLayoutParams(View view) {
		${myclass.className}.LayoutParams layoutParams = (${myclass.className}.LayoutParams) view.getLayoutParams();
		
		<#if myclass.varName == 'gridLayout'>
		layoutParams = (${myclass.className}.LayoutParams) view.getLayoutParams();
		if (layoutParams == null) {
			layoutParams = new ${myclass.className}.LayoutParams();
			view.setLayoutParams(layoutParams);
		}  else {
			layoutParams.height = -2;
			layoutParams.width = -2;
		}
		<#else>
		layoutParams = (${myclass.className}.LayoutParams) view.getLayoutParams();
		if (layoutParams == null) {
			layoutParams = new ${myclass.className}.LayoutParams(-2, -2);
			view.setLayoutParams(layoutParams);
		}  else {
			layoutParams.height = -2;
			layoutParams.width = -2;
		}
		</#if>
	}
	
	private ${myclass.className}.LayoutParams getLayoutParams(View view) {
		return (${myclass.className}.LayoutParams) view.getLayoutParams();		
	}
	
	@SuppressLint("NewApi")
	@Override
	public void setChildAttribute(IWidget w, WidgetAttribute key, String strValue, Object objValue) {
		View view = (View) w.asWidget();
		${myclass.className}.LayoutParams layoutParams = getLayoutParams(view);
		ViewGroupImpl.setChildAttribute(w, key, objValue, layoutParams);		
		
		switch (key.getAttributeName()) {
		case "layout_width":
			layoutParams.width = (int) objValue;
			break;	
		case "layout_height":
			layoutParams.height = (int) objValue;
			break;
		<#if myclass.layoutAttributes?has_content>
				<#list myclass.layoutAttributes as attrs>
				<#list attrs.aliases as alais>
			case "${alais}":
				</#list>
			case "${attrs.trimmedAttribute}": {
				<#if attrs.androidMinVersion??>if (Build.VERSION.SDK_INT >= ${attrs.androidMinVersion}) {</#if>
					<#if attrs.trimmedSetter?starts_with("code:")>
						<#if attrs.type != 'nil'>			
							${attrs.trimmedSetter?replace("$var", "layoutParams")?replace("code:", "")?replace("$value", "(${attrs.javaType})objValue;")?replace("$strvalue", "value")}
						</#if>
					<#else>
							layoutParams.${attrs.trimmedSetter?replace("$androidprefix", "${androidprefix}")}<#if attrs.trimmedSetter?index_of("(") == -1>(</#if><#if attrs.type != 'nil'>(${attrs.javaType})objValue</#if>);
					</#if>
				<#if attrs.androidMinVersion??>}</#if>
			}
			break;
		</#list>
		</#if>
		default:
			break;
		}
		
		<#if myclass.createDefault?contains("setChildAttributesPostCreate|")>
		setChildAttributesPostCreate(layoutParams);
		</#if>
		
		view.setLayoutParams(layoutParams);		
	}
	
	@SuppressLint("NewApi")
	@Override
	public Object getChildAttribute(IWidget w, WidgetAttribute key) {
		Object attributeValue = ViewGroupImpl.getChildAttribute(w, key);		
		if (attributeValue != null) {
			return attributeValue;
		}
		View view = (View) w.asWidget();
		${myclass.className}.LayoutParams layoutParams = getLayoutParams(view);

		switch (key.getAttributeName()) {
		case "layout_width":
			return layoutParams.width;
		case "layout_height":
			return layoutParams.height;
		<#if myclass.layoutAttributes?has_content>
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
		</#if>
		}
		
		return null;

	}
	
	<#include "/templates/WidgetExt.java">

	
	public void updateMeasuredDimension(int width, int height) {
		((<@getWidgetClassNameShortName myclass=myclass></@getWidgetClassNameShortName>Ext) ${myclass.varName}).updateMeasuredDimension(width, height);
	}
	

	@SuppressLint("NewApi")
	@Override
	public void setAttribute(WidgetAttribute key, String strValue, Object objValue, ILifeCycleDecorator decorator) {
		<#if (myclass.baseClass?has_content)>
		super.setAttribute(key, strValue, objValue, decorator);
		</#if>
		ViewGroupImpl.setAttribute(this, key, strValue, objValue, decorator);
		Object nativeWidget = asNativeWidget();
		<#if myclass.widgetAttributes?has_content>
		switch (key.getAttributeName()) {
		<#list myclass.widgetAttributes as attrs>
			<#if attrs.readOnly??  && attrs.readOnly != 'true'>
			case "${attrs.trimmedAttribute}": {
				<@generateAttrCode attrs=attrs setter=attrs.trimmedSetter quickConvertPrefix=false></@generateAttrCode>
			}
			break;
			</#if>
		</#list>
		default:
			break;
		}
		</#if><#if myclass.createDefault?contains("postSetAttribute|")>postSetAttribute(key, strValue, objValue, decorator);</#if>
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


	<#if myclass.createDefault?contains("asnativewidget|")>
	<#if process == 'android'>
	@Override
    public Object asNativeWidget() {
        return ${myclass.varName};
    }
	</#if>	
	<#if process == 'swt'>
	@Override
    public Object asNativeWidget() {
        return pane;
    }
	</#if>	
	<#if process == 'web'>
	@Override
    public Object asNativeWidget() {
        return htmlElement;
    }
	</#if>	
	<#if process == 'ios'>
	@Override
    public Object asNativeWidget() {
        return uiView;
    }
	</#if>	
	</#if>
	 <#if process == 'ios'>
    public native boolean checkIosVersion(String v) /*-[
		return ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] == NSOrderedDescending);
	]-*/;
    </#if> 
	<#if myclass.createDefault?contains("nativecreate|")>
	<#if process == 'android'>
    private void nativeCreate(Map<String, Object> params) {
    }
    </#if>	
    <#if process == 'swt'>
    private void nativeCreate(Map<String, Object> params) {
        pane = new org.eclipse.swt.widgets.Composite((org.eclipse.swt.widgets.Composite)ViewImpl.getParent(this), getStyle(params, fragment));
        ((org.eclipse.swt.widgets.Composite)pane).setLayout(new org.eclipse.nebula.widgets.layout.AbsoluteLayout());
    }
    </#if>
    <#if process == 'web'>
    private void nativeCreate(Map<String, Object> params) {
    	htmlElement = org.teavm.jso.dom.html.HTMLDocument.current().createElement("div");
    	htmlElement.getStyle().setProperty("box-sizing", "border-box");
    }
    </#if>
    <#if process == 'ios'>
    public native void nativeCreate(Map<String, Object> params)/*-[
		ASUIView* uiView = [ASUIView new];
		uiView.backgroundColor = [UIColor clearColor];
		uiView_ = uiView;
	]-*/;
	</#if>
    </#if>  
    
    @Override
    public void requestLayout() {
    	if (isInitialised()) {
    		ViewImpl.requestLayout(this, asNativeWidget());
    		<#if myclass.createDefault?contains("nativeRequestLayout|")>
    		nativeRequestLayout();
    		</#if>  
    	}
    }
    
    @Override
    public void invalidate() {
    	if (isInitialised()) {
    		ViewImpl.invalidate(this, asNativeWidget());
    	}
    }
    
	${extraCode}
	<#list myclass.methodDefinitions as methodDefinition>
	${methodDefinition}
	</#list>

	@Override
	public void setId(String id){
		<#if myclass.createDefault?contains("iddefault|")>
		if (id == null) {
			id = UUID.randomUUID().toString();
		}
		</#if>
		if (id != null && !id.equals("")){
			super.setId(id);
			${myclass.varName}.setId(IdGenerator.getId(id));
		}
	}
	
    
    <#if process == 'swt'>   
    @Override
    public void setVisible(boolean b) {
        ((View)asWidget()).setVisibility(b ? View.VISIBLE : View.GONE);
    }
    public int getStyle(String key, int initStyle, Map<String, Object> params, IFragment fragment) {
    	if (params == null) {
    		return initStyle;
    	}
    	Object style = params.get(key);
		if (style == null) {
			return initStyle;
		}
		int convertFrom = (int) ConverterFactory.get("swtbitflag").convertFrom(style.toString(), null, fragment);
		return convertFrom;
	}
	
	public int getStyle(Map<String, Object> params, IFragment fragment) {
		return getStyle("swtStyle", org.eclipse.swt.SWT.NONE, params, fragment);
	}
	
	public int getStyle(int initStyle, Map<String, Object> params, IFragment fragment) {
		return getStyle("swtStyle", initStyle, params, fragment);
	}
    </#if>

    <#include "/templates/WidgetBuilderTemplate.java">

	//end - body
}
