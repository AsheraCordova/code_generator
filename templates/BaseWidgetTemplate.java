package com.ashera.ui.${process};
//start - imports
<#include "/templates/Macros.java">

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import android.content.Context;

<#if process == 'android'>
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.widget.*;
import androidx.core.view.*;
import android.view.*;
import android.graphics.drawable.*;
</#if>

<#if process == 'swt'>
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.graphics.*;
import androidx.core.view.*;
import static com.ashera.common.DisposeUtil.*;
</#if>
<#if process == 'web'>
import org.teavm.jso.dom.html.HTMLElement;
</#if>

import android.os.Build;
import android.view.View;
import android.text.*;

import com.ashera.core.IFragment;
import com.ashera.converter.*;

import android.annotation.SuppressLint;

import com.ashera.layout.*;
import com.ashera.plugin.*;
import com.ashera.widget.bus.*;
import com.ashera.widget.*;
import com.ashera.widget.bus.Event.*;
import com.ashera.widget.IWidgetLifeCycleListener.EventId;
import com.ashera.widget.IWidgetLifeCycleListener.EventId.*;

<#if process == 'ios'>
/*-[
#include "java/lang/Integer.h"
#include "java/lang/Float.h"
#include "java/lang/Boolean.h"
#include <UIKit/UIKit.h>
#include "HasLifeCycleDecorators.h"
<#list myclass.nativeImports as nativeImport>
#include "${nativeImport}"
</#list>
<#if myclass.createDefault?contains("simpleWrapableView|")>
#include "ASUIScrollView.h"
#include "ASUIView.h"
</#if>
]-*/

import com.google.j2objc.annotations.Property;
</#if>

import static com.ashera.widget.IWidget.*;
//end - imports
@SuppressLint("NewApi")
public class ${myclass.widgetName} extends BaseWidget {
	//start - body
	public final static String LOCAL_NAME = "${myclass.localName}"; 
	public final static String GROUP_NAME = "${myclass.group}";

	<#if myclass.createDefault?contains("compositeWidget|")>
	private IWidget compositeWidget;
	<#else>
	<#if process == 'ios'>
	protected @Property Object uiView;
	protected ${myclass.widgetClassname} ${myclass.widgetClassVarName};		
	</#if>
	<#if process == 'android'>
	protected <@getClassName myclass=myclass></@getClassName> ${myclass.varName};
	</#if>
	<#if process == 'swt' || process == 'web'>
	protected ${myclass.nativeClassName} ${myclass.nativeClassVarName};
	protected ${myclass.widgetClassname} ${myclass.widgetClassVarName};	
	</#if>	
	</#if>
	<#if myclass.createDefault?contains("createcanvas|")>
	private r.android.graphics.Canvas canvas;
	</#if>
	
	<#if myclass.createDefault?contains("deallochandler|")>
	private String DELLOC_EVENT = com.ashera.widget.bus.Event.StandardEvents.dealloc.toString();
	@com.google.j2objc.annotations.WeakOuter
	class DallocHandler extends com.ashera.widget.bus.EventBusHandler {

		public DallocHandler(String type) {
			super(type);
		}

		@Override
		protected void doPerform(Object payload) {
			releaseResource();
		}
		
	}
	private void addDellocHandler() {
		fragment.getEventBus().on(DELLOC_EVENT, new DallocHandler(DELLOC_EVENT));
	}
	</#if>	
	<#include "/templates/WidgetAttributesClass.java">
	
	@Override
	public void loadAttributes(String attributeName) {
		ViewImpl.register(attributeName);

		<#include "/templates/WidgetAttributes.java">
		<#if myclass.createDefault?contains("loadAttributes|")>
		loadCustomAttributes(attributeName);
		</#if>
	}
	
	public ${myclass.widgetName}() {
		super(GROUP_NAME, LOCAL_NAME);
	}

	<#if !myclass.createDefault?contains("compositeWidget|")>
	<#include "/templates/WidgetExt.java">
	
	public void updateMeasuredDimension(int width, int height) {
	    <#if process == 'android'>
	    ((<@getWidgetClassNameShortName myclass=myclass></@getWidgetClassNameShortName>Ext) ${myclass.varName}).updateMeasuredDimension(width, height);
	    </#if>
	    <#if process == 'swt' || process == 'ios'>
		((<@getWidgetClassNameShortName myclass=myclass></@getWidgetClassNameShortName>Ext) ${myclass.widgetClassVarName}).updateMeasuredDimension(width, height);
		</#if>
	}
	</#if>

	@Override
	public IWidget newInstance() {
		return new ${myclass.widgetName}();
	}
	
	@SuppressLint("NewApi")
	@Override
	public void create(IFragment fragment, Map<String, Object> params) {
		super.create(fragment, params);
		<#if myclass.createDefault?contains("compositeWidget|")>
		Object compositeWidgetParam = params.get("compositeWidget");
		compositeWidget = WidgetFactory.get(compositeWidgetParam != null ? (String) compositeWidgetParam : DEFAULT_COMPOSITE_WIDGET, false);
		compositeWidget.setParent(getParent());
		compositeWidget.create(fragment, params);
		nativeCreate(params);
		<#else>
		<#if process == 'android'>
		<@initAndroidWidget myclass=myclass></@initAndroidWidget>
		</#if>
		<#if process == 'swt' || process == 'web'>
		${myclass.widgetClassVarName} = new <@getWidgetClassNameShortName myclass=myclass></@getWidgetClassNameShortName>Ext();
		</#if>
		<#if process == 'ios'>
		${myclass.widgetClassVarName} = new <@getWidgetClassNameShortName myclass=myclass></@getWidgetClassNameShortName>Ext();
		</#if>
		<#if myclass.createDefault?contains("simpleWrapableView|")>
		createSimpleWrapableView();
		</#if>
		nativeCreate(params);	
		ViewImpl.registerCommandConveter(this);
		<#if process == 'ios'>
		setWidgetOnNativeClass();
		</#if>		
		</#if>
		<#if myclass.createDefault?contains("deallochandler|")>
		addDellocHandler();
		</#if>
	}
	<#if !myclass.createDefault?contains("compositeWidget|")>
	<#if process == 'ios'>
	private native void setWidgetOnNativeClass() /*-[
		((${myclass.getNativeIosWidgetName(false)}*) self.uiView).widget = self;
	]-*/;
	</#if>
	</#if>

	@Override
	@SuppressLint("NewApi")
	public void setAttribute(WidgetAttribute key, String strValue, Object objValue, ILifeCycleDecorator decorator) {
		Object nativeWidget = <#if myclass.createDefault?contains("simpleWrapableView|")>simpleWrapableView.getWrappedView()<#else>asNativeWidget()</#if>;
		<#if myclass.createDefault?contains("compositeWidget|")>
		//##set
		compositeWidget.setAttribute(<#if myclass.createDefault?contains("simpleWrapableView|")>simpleWrapableView,</#if> key, strValue, objValue, decorator);
		<#else>
		ViewImpl.setAttribute(this, <#if myclass.createDefault?contains("simpleWrapableView|")>simpleWrapableView,</#if> key, strValue, objValue, decorator);
		</#if>
		<#if myclass.createDefault?contains("preSetAttribute|")>objValue = preSetAttribute(key, strValue, objValue, decorator);</#if>
		<#if myclass.widgetAttributes?has_content>
		switch (key.getAttributeName()) {
		<#list myclass.widgetAttributes as attrs>
			<#list attrs.aliases as alais>
			case "${alais}":
			</#list>			
			case "${attrs.trimmedAttribute}": {
				<#if attrs.setterMethodNative??>
					<@generateAttrCode attrs=attrs setter=attrs.setterMethodNative quickConvertPrefix=false></@generateAttrCode>
				</#if>
				
				<#if attrs.readOnly??  && attrs.readOnly != 'true'><@generateAttrCode attrs=attrs setter=attrs.trimmedSetter quickConvertPrefix=false></@generateAttrCode></#if>
			}
			break;
		</#list>
		default:
			break;
		}
		</#if><#if myclass.createDefault?contains("postSetAttribute|")>postSetAttribute(key, strValue, objValue, decorator);</#if>
	}
	
	@Override
	@SuppressLint("NewApi")
	public Object getAttribute(WidgetAttribute key, ILifeCycleDecorator decorator) {
		Object nativeWidget = <#if myclass.createDefault?contains("simpleWrapableView|")>simpleWrapableView.getWrappedView()<#else>asNativeWidget()</#if>;
		<#if myclass.createDefault?contains("compositeWidget|")>
		//##get
		Object attributeValue = compositeWidget.getAttribute(key, decorator);
		<#else>
		Object attributeValue = ViewImpl.getAttribute(this, nativeWidget, key, decorator);
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
	
	@Override
	public Object asWidget() {
		<#if myclass.createDefault?contains("compositeWidget|")>
		return compositeWidget.asWidget();
		<#else>
		return <#if process == 'android'>${myclass.varName};</#if><#if process == 'swt' || process == 'web'>${myclass.widgetClassVarName};</#if><#if process == 'ios'>${myclass.widgetClassVarName};</#if>
		</#if>
	}

	${extraCode}
	<#list myclass.methodDefinitions as methodDefinition>
	${methodDefinition}
	</#list>
	
	   <#if myclass.createDefault?contains("asnativewidget|")>
	   	<#if myclass.createDefault?contains("compositeWidget|")>
	   	@Override
	    public Object asNativeWidget() {
		   return compositeWidget.asNativeWidget();
	    }
	   	<#else>
	   	<#if process == 'ios'>
		@Override
	    public Object asNativeWidget() {
	        return uiView;
	    }
		<#else>
	    @Override
	    public Object asNativeWidget() {
	        return ${myclass.nativeClassVarName};
	    }
	    </#if>  
	    </#if>
	    </#if>  
	    <#if myclass.createDefault?contains("nativecreate|")>

	    private void nativeCreate(Map<String, Object> params) {
	    }
	    </#if>
	    <#if process == 'ios'>
	    public native boolean checkIosVersion(String v) /*-[
			return ([[[UIDevice currentDevice] systemVersion] compare:v options:NSNumericSearch] == NSOrderedDescending);
		]-*/;
	    </#if> 
	@Override
	public void setId(String id){
		if (id != null && !id.equals("")){
			super.setId(id);
			<#if myclass.createDefault?contains("compositeWidget|")>
			((View)compositeWidget.asWidget()).setId(IdGenerator.getId(id));
			<#else>
			${myclass.widgetClassVarName}.setId(IdGenerator.getId(id));
			</#if>
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
 
    @Override
    public void requestLayout() {
    	if (isInitialised()) {
    		<#if myclass.createDefault?contains("compositeWidget|")>
    		compositeWidget.requestLayout();
    		<#else>
    		ViewImpl.requestLayout(this, asNativeWidget());
			</#if>
    		
    		<#if myclass.createDefault?contains("nativeRequestLayout|")>
    		nativeRequestLayout();
    		</#if> 
    	}
    }
    
    @Override
    public void invalidate() {
    	if (isInitialised()) {
    		<#if myclass.createDefault?contains("compositeWidget|")>
    		compositeWidget.invalidate();
    		<#else>
			ViewImpl.invalidate(this, asNativeWidget());
			</#if>
			<#if myclass.createDefault?contains("simpleWrapableView|")>
			if (isViewWrapped()) {
				ViewImpl.invalidate(this, simpleWrapableView.getWrappedView());
				if (simpleWrapableView.getForeground() != null) {
					ViewImpl.invalidate(this, simpleWrapableView.getForeground());
				}
			}			</#if>
			<#if myclass.createDefault?contains("nativeInvalidate|")>
			nativeInvalidate();
    		</#if> 
    	}
    }
    
    <#include "/templates/WidgetBuilderTemplate.java">
    
    <#if myclass.createDefault?contains("simpleWrapableView|")>
    private SimpleWrapableView simpleWrapableView;
    
    private void createSimpleWrapableView() {
		boolean wrapViewFeature = hasFeature("enableFeatures", "decorator");
		int viewType = -1;
	
		if (wrapViewFeature) {
			boolean hscroll = hasFeature("enableFeatures", "hscroll");
			boolean vscroll = hasFeature("enableFeatures", "vscroll");
			
			viewType = 1;
			if (hscroll) viewType = 2;
			if (vscroll) viewType = 3;
		}
		
		simpleWrapableView = new SimpleWrapableView(this, viewType);
    }
    
	private boolean hasScrollView() {
		return isViewWrapped() && (simpleWrapableView.getViewtype() == 2 || simpleWrapableView.getViewtype() == 3);
	}

	private boolean isViewWrapped() {
		return simpleWrapableView.isViewWrapped();
	}
	
	@Override
	public void addForegroundIfNeeded() {
		if (isViewWrapped() && !simpleWrapableView.isDisableForeground()) {
			if (simpleWrapableView.getForeground() == null) {
				Object foreground = nativeAddForeGround(this);
				ViewGroupImpl.nativeAddView(simpleWrapableView.asNativeWidget(), foreground);
				simpleWrapableView.setForeground(foreground);
			}
		}
	}

	@Override
	public Object getForeground() {
		return simpleWrapableView.getForeground();
	}

	private void setForegroundFrame(int l, int t, int r, int b) {
		Object foreground = simpleWrapableView.getForeground();
		if (foreground != null) {
			ViewImpl.nativeMakeFrame(foreground, 0, 0, r - l, b - t);
		}
	}

	

	@Override
	public Object asNativeWidget() {
       return simpleWrapableView.asNativeWidget();
	}

	
	<#if process != 'web'>
    private void invalidateWrapViewHolder() {
    	if (isViewWrapped()) {
    		ViewImpl.nativeInvalidate(simpleWrapableView.getWrapperViewHolder());
    	}
	}
    </#if>
    
    <#if process == 'swt'>
	@Override
	public Object createWrapperViewHolder(int viewType) {
		Composite parent = (Composite) ViewImpl.getParent(this);
		Composite wrapperComposite = new Composite(parent, getStyle(params, fragment));
        wrapperComposite.setLayout(new org.eclipse.nebula.widgets.layout.AbsoluteLayout());

        return wrapperComposite;	
	}
    </#if>
    <#if process == 'ios'>
	@Override
	public Object createWrapperView(Object wrapperParent, int viewtype) {
		uiView = nativeCreateView(viewtype);
		ViewGroupImpl.nativeAddView(ViewImpl.getFirstChildOrSelf(wrapperParent), uiView);
		return uiView;
	}


	@Override
	public Object createWrapperViewHolder(int viewType) {
		return createWrapperViewHolderNative(viewType);
	}
	public native Object nativeAddForeGround(IWidget w) /*-[
		ASUIView* uiView = [ASUIView new];
		uiView.widget = w;
		uiView.commandRegex  = AS${myclass.widgetName}_FOREGROUND_REGEX; 
		uiView.backgroundColor = [UIColor clearColor];
		return uiView;
	]-*/;
	 public native Object createWrapperViewHolderNative(int viewType)/*-[
	 	if (viewType == 1) {
			ASUIView* uiView = [ASUIView new];
			uiView.widget = self;
			uiView.commandRegex  = AS${myclass.widgetName}_VIEW_HOLDER_REGEX; 
			uiView.backgroundColor = [UIColor clearColor];
			
			return uiView;
		}
		
		if (viewType == 2 || viewType == 3) {
			ASUIView* uiView = [ASUIView new];
			uiView.widget = self;
			uiView.backgroundColor = [UIColor clearColor];
			uiView.commandRegex  = AS${myclass.widgetName}_VIEW_HOLDER_REGEX; 

			ASUIScrollView* scrollview = [ASUIScrollView new];
			scrollview.scrollEnabled=YES;
			scrollview.bounces=NO;
			scrollview.preventAutoScroll=YES;
	    	scrollview.delaysContentTouches=YES;
	    	scrollview.userInteractionEnabled=YES;
			scrollview.widget = self;
			scrollview.backgroundColor = [UIColor clearColor];
			scrollview.commandRegex  = @"none";
			[uiView addSubview:scrollview];
			return uiView;
		}
		
		return nil;
	]-*/;
	 
	private native Object getScrollView() /*-[
		UIView* uiview = (UIView*)[self->simpleWrapableView_ getWrapperViewHolder];
		return uiview.subviews[0];
	]-*/;
	 </#if>
	 </#if>
	//end - body

}
