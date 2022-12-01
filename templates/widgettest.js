<#include "/templates/widgettest_recycleview.xml">
<#include "/templates/widgettest_drawerlayout.xml">
<#include "/templates/widgettest_toolbar.xml">
<#include "/templates/widgettest_spinner.xml">
<#include "/templates/widgettest_datepicker.xml">
<#include "/templates/widgettest_textview.xml">
<#include "/templates/widgettest_progressbar.xml">
<#function getWidgetName>
	<#if myclass.localName == 'layout'>
		<#return 'Root'>
	</#if>		
	<#return myclass.localNameWithoutPackage>
</#function>
<#include "/templates/Macros.java">
// start - imports
import {Fragment, Inject} from './app/Fragment';
<#if getWidgetName() != 'View'>import {${getWidgetName()}} from './${process}/widget/${getWidgetName()}Impl';
import * as targetSuperClassView from './android/widget/ViewImpl';</#if>
<#if myclass.layoutAttributes?has_content>
import {${getWidgetName()}_LayoutParams} from './${process}/widget/${getWidgetName()}Impl';
</#if>

import * as targetView from './${process}/widget/${getWidgetName()}Impl';
import {Gravity} from './widget/TypeConstants';
import {View} from './${process}/widget/ViewImpl';
import {NavController, InjectController} from './navigation/NavController';
// end - imports
<#function getTestCases attrs parentAttribute=''>
	<#assign myarray=getTestCases_attrs_TextView(attrs)>
	<#if myarray?has_content>
		<#return myarray>
	</#if>
	<#assign myarray=getTestCases_attrs_Recycleview(attrs)>
	<#if myarray?has_content>
		<#return myarray>
	</#if>
	<#assign myarray=getTestCases_attrs_Toolbar(attrs)>
	<#if myarray?has_content>
		<#return myarray>
	</#if>
	<#assign myarray=getTestCases_attrs_Spinner(attrs, myclass.localName)>
	<#if myarray?has_content>
		<#return myarray>
	</#if>
	<#assign myarray=getTestCases_attrs_DrawerLayout(attrs, myclass.localName)>
	<#if myarray?has_content>
		<#return myarray>
	</#if>	
	
	<#assign myarray=getTestCases_DatePicker_attrs(attrs, myclass.localName)>
	<#if myarray?has_content>
		<#return myarray>
	</#if>
	
	<#assign myarray=getTestCases_ProgressBar_attrs(attrs, myclass.localName)>
	<#if myarray?has_content>
		<#return myarray>
	</#if>
	
	<#assign array=[]> 
	<#if attrs.type == 'dimensionsp'>
		<#assign array = array + ["'20sp'"]>
	<#elseif attrs.trimmedAttribute == 'childXml'>
		<#assign array = array + ["'<TextView text=\"test1114\"></TextView>'"]>		
	<#elseif attrs.trimmedAttribute == 'content'>
		<#assign array = array + ["'@+id/sampleWidget'"]>	
	<#elseif attrs.trimmedAttribute == 'accessibilityTraversalAfter' || attrs.trimmedAttribute == 'accessibilityTraversalBefore'>	
		<#assign array = array + ["'@+id/testSetAndGetTest'"]>		
	<#elseif attrs.trimmedAttribute== 'scaleX' || attrs.trimmedAttribute== 'scaleY'>
		<#assign array = array + ["1.3", "1.2"]>				
	<#elseif attrs.trimmedAttribute == 'alpha'>
		<#assign array = array + ["0.5", "0.3"]>		
	<#elseif attrs.trimmedAttribute?starts_with('next')>
		<#assign array = array + ["'@+id/sampleWidget'"]>
	<#elseif attrs.type?ends_with('.constraintReferencedIds')>
		<#assign array = array + ["'text1'"]>	
	<#elseif attrs.trimmedAttribute == 'layout_constraintHorizontal_bias' || attrs.trimmedAttribute == 'layout_constraintVertical_bias' || attrs.trimmedAttribute?ends_with('Bias')>
		<#assign array = array + ["0.5", "0.3"]>
	<#elseif attrs.trimmedAttribute == 'layout_constraintGuide_percent' || attrs.trimmedAttribute == 'layout_constraintWidth_percent' || attrs.trimmedAttribute == 'layout_constraintHeight_percent'>
		<#assign array = array + ["0.75"]>				
	<#elseif attrs.trimmedAttribute == 'onEditorAction'>
		<#assign array = array + ["'onEditorAction1'"]>	
	<#elseif attrs.trimmedAttribute?starts_with("on")>
		<#assign array = array + ["'" + attrs.trimmedAttribute +"'"]>
	<#elseif attrs.type == 'dimensionsppxint'>
		<#assign array = array + ["'13sp'", "'34px'"]>				
	<#elseif attrs.type == 'xmlresource'>
		<#assign array = array + ["'@xml/extra_data'"]>		
	<#elseif attrs.type == 'dimensionspint'>
		<#assign array = array + ["'20sp'"]>
	<#elseif attrs.trimmedAttribute == 'textAppearance' || attrs.trimmedAttribute == 'switchTextAppearance'>
		<#assign array = array + ["'@style/CustomTextStyle1'"]>		
	<#elseif attrs.trimmedAttribute == 'fontFeatureSettings'>
		<#assign array = array + ["'afrc'"]>	
	<#elseif attrs.trimmedAttribute == 'privateImeOptions'>
		<#assign array = array + ["''"]>
	<#elseif attrs.trimmedAttribute == 'weightSum'>
		<#assign array = array + ["2"]>						
	<#elseif attrs.type == 'float'>
		<#if attrs.trimmedAttribute?ends_with('Radius')>
			<#assign array = array + ["20", "25"]>	
		<#elseif attrs.trimmedAttribute == 'iosMinimumScaleFactor'>
			<#assign array = array + ["0.1", "0.7"]>
		<#else>
			<#assign array = array + ["20", "40"]>
		</#if>		
	<#elseif attrs.keys?has_content>
			
	 	
	 	<#list attrs.keys as key>
	 		<#if attrs.useSuperViewForAccess><#assign myVar1 ="targetSuperClassView."><#else><#assign myVar1 ="targetView."></#if>
			<#assign myVar = myVar1 + attrs.typeVariable + "." + key?replace("-", "_")>
	 		<#assign array = [myVar] + array>
	 	</#list>
	 	
	 	<#if attrs.supportIntegerAlso>
	 		<#assign array = array + ["'2'", "'4'", "'6'"]>
	 	</#if>	
	<#elseif attrs.trimmedAttribute == 'drawablePadding'>
		<#assign array = array + ["'20dp'", "'20dp'"]> 
	<#elseif attrs.trimmedAttribute == 'digits'>
		<#assign array = array + ["'6789'"]> 
	<#elseif attrs.type?starts_with('dimensionpx')>
		<#assign array = array + ["'20px'"]> 
	<#elseif attrs.type?starts_with('dimension')>
		<#if attrs.trimmedAttribute?starts_with('maxHeight') || attrs.trimmedAttribute?starts_with('minHeight') || attrs.trimmedAttribute?starts_with('height')>
			<#assign array = array + ["'100dp'", "'100dp'"]>
		<#elseif attrs.trimmedAttribute?starts_with('minWidth') || attrs.trimmedAttribute?starts_with('maxWidth') || attrs.trimmedAttribute?starts_with('width')>
			<#assign array = array + ["'100dp'"]>
		<#elseif attrs.trimmedAttribute?ends_with('_min')>
			<#assign array = array + ["'180dp'"]>
		<#else>
			<#assign array = array + ["'20dp'"]> 
		</#if>
	<#elseif attrs.trimmedAttribute == 'imageFromUrl'>
		<#assign array = array + ["'https://picsum.photos/200'", "'https://sample-videos.com/img/Sample-jpg-image-50kb.jpg'"]>					
	<#elseif attrs.trimmedAttribute?starts_with('drawable') || attrs.type == 'drawable' || attrs.type == 'image'|| attrs.type?starts_with('colorimage')>
		<#if attrs.trimmedAttribute?ends_with('Tint')>
			<#assign array = array + ["'@color/bg'", "'#FF0000'"]>
		<#else>
			<#if attrs.trimmedAttribute?starts_with('swt') || attrs.type == 'image'>
				<#assign array = array + ["'@drawable/calatrava_cross'"]>
			<#else>
				<#assign array = array + ["'@null'", "'@drawable/calatrava_cross'", "'@drawable/button_selector'", "'@drawable/focused_selector'", "'@drawable/hovered_selector'", "'@drawable/drawable_color_selector'", "'@drawable/shape_line_drawable'"]>  		
			</#if>	
		</#if>
	<#elseif attrs.type == 'colorstate'>
		<#assign array = array + ["'@color/color_state'", "'@color/bg'", "'#FF0000'"]>
	<#elseif attrs.type?starts_with('color') || attrs.trimmedAttribute?ends_with('Tint')>
		<#assign array = array + ["'@color/bg'", "'#FF0000'"]>
	<#elseif attrs.type == 'boolean'>
		<#assign array = array + ["false", "true"]>
	<#elseif attrs.type == 'resourcestring'>
		<#assign array = array + ["'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commod'", "'@string/sample_text'", "'test'"]>		
	<#elseif attrs.trimmedAttribute == 'baselineAlignedChildIndex'>
		<#assign array = array + ["1", "0"]>
	<#elseif attrs.trimmedAttribute == 'layout_span'>
		<#assign array = array + ["2", "1"]>	
	<#elseif attrs.type == 'int'>
		<#assign array = array + ["2", "4", "6"]>
	<#elseif attrs.type?starts_with('font')>
		<#assign array = array + ["'@font/quicksand'); this.${attrs.trimmedAttribute}0.setTextStyle(targetView.TextStyle.italic", "'@font/quicksand'); this.${attrs.trimmedAttribute}1.setTextStyle(targetView.TextStyle.normal", "'@font/quicksand'); this.${attrs.trimmedAttribute}2.setTextStyle(targetView.TextStyle.bold"]>
	 <#elseif attrs.trimmedAttribute == 'foregroundGravity'>
		<#assign array = array + ["Gravity.top", "Gravity.bottom", "Gravity.left", "Gravity.right", "Gravity.center_vertical", "Gravity.fill_vertical", "Gravity.center_horizontal", "Gravity.center", "Gravity.fill", "Gravity.clip_vertical",  "Gravity.clip_horizontal", "Gravity.fill_horizontal"]>
		<#assign array = array?reverse >
	 <#elseif attrs.type == 'gravity'>
		<#assign array = array + ["Gravity.top", "Gravity.bottom", "Gravity.left", "Gravity.right", "Gravity.center_vertical", "Gravity.fill_vertical", "Gravity.center_horizontal", "Gravity.center", "Gravity.fill", "Gravity.clip_vertical",  "Gravity.clip_horizontal", "Gravity.start", "Gravity.end", "Gravity.fill_horizontal"]>
		<#assign array = array?reverse >
	<#elseif attrs.trimmedAttribute == 'swtOrientation' || attrs.trimmedAttribute == 'swtTextDirection'>
		<#assign array = array + ["'right_to_left'", "'left_to_right'"]>
	<#elseif attrs.trimmedAttribute == 'checkedButton'>
		<#assign array = array + ["'@+id/radio1'"]>
	<#elseif (attrs.trimmedAttribute?starts_with('layout_') && attrs.type= 'id') || (attrs.trimmedAttribute == 'ignoreGravity')>
		<#assign array = array + ["'@+id/text2'"]>
	<#elseif attrs.trimmedAttribute == 'collapseColumns' || attrs.trimmedAttribute == 'shrinkColumns' || attrs.trimmedAttribute == 'stretchColumns'>
		<#assign array = array + ["'1'", "'*'"]>		
	<#elseif attrs.attribute == 'layout_constraintDimensionRatio'>
		<#assign array = array + ["'1:2'", "'2:1'"]>	
	<#elseif attrs.trimmedAttribute == 'format'>
		<#assign array = array + ["'test %s'", "'%s test'"]>	
	<#elseif attrs.type == 'object' && attrs.attribute?contains('Index')>
		<#assign array = array + ["{}, 2"]>				
	<#elseif attrs.attribute == 'updateModelData'>
		<#assign array = array + ["{}, ''"]>				
	<#elseif myclass.localName == 'com.ashera.ui.android.Model' || attrs.trimmedAttribute?contains("model")|| attrs.trimmedAttribute?contains("Model")>
		<#assign array = array + ["''"]>
	<#elseif attrs.type == 'template'>		
		<#assign array = array + ["'@layout/modeltest_listitem'"]>
	<#elseif attrs.type == 'object'>
		<#assign array = array + ["{}"]>								
	<#elseif attrs.attribute == 'modelFor' || attrs.attribute == 'removeModelById' || attrs.attribute == 'param'>
		<#assign array = array + ["''"]>
	<#elseif attrs.trimmedAttribute == 'swtBackgroundMode'>
		<#assign array = array + ["'inherit_none'", "'inherit_default'", "'inherit_force'"]>
	<#elseif (attrs.trimmedAttribute?contains('border') || attrs.trimmedAttribute?contains('Border')) && (attrs.trimmedAttribute?contains('Style'))>		
		<#assign array = array + ["'solid'", "'dashed'", "'dotted'"]>		
	<#elseif attrs.trimmedAttribute == 'autofillHints' || attrs.trimmedAttribute == 'transitionName'>	
		<#assign array = array + ["'test'"]>
	<#elseif attrs.trimmedAttribute == 'fileExtension'>
		<#assign array = array + ["'png'"]>		
	<#elseif attrs.trimmedAttribute == 'asDragSource'>
		<#assign array = array + ["'emailIntent=testObj.emailIntent from testObj->view'"]>
	<#elseif attrs.trimmedAttribute == 'autofillHints'>
		<#assign array = array + ["'postAddress'"]>		
	<#elseif attrs.trimmedAttribute == 'layout_constraintTag'>
		<#assign array = array + ["'test'"]>	
	<#elseif attrs.trimmedAttribute == 'circularflow_angles'>
		<#assign array = array + ["'180,90,45,0'"]>
	<#elseif attrs.trimmedAttribute == 'circularflow_radiusInDP'>
		<#assign array = array + ["'60,40,20,10'"]>		
	<#elseif attrs.trimmedAttribute == 'circularflow_viewCenter'>
		<#assign array = array + ["'@+id/viewCenter'"]>
	<#elseif parentAttribute == 'showDialog'>
		<#if attrs.trimmedAttribute?starts_with('tag')>
			<#assign array = array + ["'mydialog'"]>
		</#if>
	<#elseif attrs.trimmedAttribute == 'viewId'>
		<#if parentAttribute?starts_with('update')>
		<#assign array = array + ["'@+id/item_1'"]>
		<#else>
		<#assign array = array + ["'@+id/add_dynamic'"]>
		</#if>
	<#elseif attrs.trimmedAttribute == 'swtAttachEventBubbler'>
		<#assign array = array + ["'mousedown'"]>		
	<#elseif attrs.trimmedAttribute == 'linearGradientBackground'>
		<#assign array = array + ["'#fff,#000'"]>		
	<#elseif attrs.trimmedAttribute == 'autoSizePresetSizes' || attrs.type == 'array'>
		<#assign array = array + ["'@array/autosize_text_sizes1'"]>
	<#elseif attrs.trimmedAttribute == 'validateForm'>	
		<#assign array = array + ["'test'"]>
	<#elseif attrs.trimmedAttribute?starts_with("v_")>
		<#if attrs.trimmedAttribute == 'v_max' || attrs.trimmedAttribute == 'v_min' || attrs.trimmedAttribute == 'v_maxlength' || attrs.trimmedAttribute == 'v_minlength'>
			<#assign array = array + ["'2'"]>
		<#elseif attrs.trimmedAttribute == 'required'>
			<#assign array = array + ["''"]>
		<#elseif attrs.trimmedAttribute == 'pattern'>
			<#assign array = array + ["'[a-z]*'"]>
		<#elseif attrs.trimmedAttribute == 'type '>
			<#assign array = array + ["'email'"]>
		</#if>
	<#elseif attrs.trimmedAttribute == 'style' || attrs.trimmedAttribute == 'errorStyle'>
		<#assign array = array + ["'@style/blackBg'"]>	
	<#elseif attrs.trimmedAttribute == 'webTabIndex'>
		<#assign array = array + ["'1'", "'2'"]>	
	<#elseif attrs.trimmedAttribute == 'expression'>
		<#assign array = array + ["''"]>
	<#elseif attrs.trimmedAttribute == 'webOverflow'>
		<#assign array = array + ["'auto'", "'hidden'"]>
	<#else>
		<#assign array = array + [attrs.type]>
	</#if>
	<#return array>
</#function>


export default class ${getWidgetName()}Activity extends Fragment{
	// start - body
	@Inject({ id : "@+id/sampleWidget"})
	private sampleWidget!: ${getWidgetName()};
	<#if myclass.layoutAttributes?has_content>
	@Inject({ path : ["@+id/sampleWidget", "@+pos/0"]})
	private sampleWidgetChild0!: View;
	@Inject({ path : ["@+id/sampleWidget", "@+pos/1"]})
	private sampleWidgetChild1!: View;
	</#if>
	<#list allAttributes as attrs>
		<#assign testcases = getTestCases(attrs)>
		<#if testcases?? && testcases?has_content>
			<#assign x = 0 >
			<#list testcases as testcase>
				@Inject({ id : "@+id/${attrs.trimmedAttribute}${x}"})
				private ${attrs.trimmedAttribute}${x}!: ${getWidgetName()};
				<#if attrs.trimmedAttribute?starts_with('layout_')>
				@Inject({ path : ["@+id/${attrs.trimmedAttribute}${x}", "@+pos/0"]})
				private ${attrs.trimmedAttribute}${x}Child0!: View;
				</#if>
				<#if myclass.localName=='androidx.drawerlayout.widget.DrawerLayout'>				
				async ${attrs.trimmedAttribute}${x}openDrawer() {
					this.${attrs.trimmedAttribute}${x}.reset().openDrawer(<#if testcase?starts_with("Gravity.")>${testcase}<#else>Gravity.start</#if>);
					await this.executeCommand(this.${attrs.trimmedAttribute}${x});			
				}
				</#if>
				<#assign x = x + 1 >
			</#list>
		</#if>
	</#list>    
	@InjectController({})
    navController!: NavController;
    constructor() {
        super();
    }
	
	<#list allAttributes as attrs>
	<#if attrs.trimmedAttribute != 'attributeUnderTest' && attrs.trimmedAttribute != 'id'>
	async onClick${attrs.trimmedAttribute}(obj:any) {		
		<#assign testcases = getTestCases(attrs)>
		<#if testcases?? && testcases?has_content && attrs.readOnly?? && attrs.readOnly != 'true'>
			<#assign x = 0 >
			this.${attrs.trimmedAttribute}0.reset().setAttributeUnderTest('${myclass.localName},${attrs.trimmedAttribute}');
			await this.executeCommand(this.${attrs.trimmedAttribute}0);
			<#if (!attrs.constructor?? || attrs.constructor != true)>
			<#list testcases as testcase>
				<#if attrs.trimmedAttribute?starts_with("layout_")>
				let ${attrs.trimmedAttribute}${x}Child0LayoutParams: ${getWidgetName()}_LayoutParams = new ${getWidgetName()}_LayoutParams();
				${attrs.trimmedAttribute}${x}Child0LayoutParams.${attrs.setter}(${testcase});			
				this.${attrs.trimmedAttribute}${x}Child0.setLayoutParams(${attrs.trimmedAttribute}${x}Child0LayoutParams);
				await this.executeCommand(this.${attrs.trimmedAttribute}${x}Child0);
				<#elseif attrs.methodParams?has_content>
				this.${attrs.trimmedAttribute}${x}.reset().${attrs.setter}(<#list attrs.methodParams as param>${getTestCases(param, attrs.trimmedAttribute)[0]}<#if param?has_next>,</#if></#list>);
				<#else>
				this.${attrs.trimmedAttribute}${x}.reset().${attrs.setter}(<#if testcase != 'nil'>${testcase}</#if>);
				</#if>
				await this.executeCommand(this.${attrs.trimmedAttribute}${x});	
				
				
				<#assign x = x + 1 >
			</#list>
			</#if>
		</#if>
		
		
	}
	</#if>
	</#list>
	
	delay<T>(ms: number, result?: T ) {
 		return new Promise(resolve => setTimeout(() => resolve(result), ms));
	}

	async testGetAndSet(obj:any) {
		try {
		let errors: string[] = [];
		let value:any = null;
		<#list allAttributes as attrs>
			<#if attrs.trimmedAttribute != 'attributeUnderTest' && attrs.trimmedAttribute != 'id'>
			<#if attrs.getterMethod?has_content && !myclass.excludeFromGetterAndSetterTestCaseAsList?seq_contains(attrs.trimmedAttribute)>
				<#assign testcases = getTestCases(attrs)>
				<#if testcases?? && testcases?has_content && attrs.readOnly?? && attrs.readOnly != 'true'>	
					<#list testcases as testcase>
						<#if attrs.trimmedAttribute?starts_with("layout_")>		
						<#if myclass.group=='RelativeLayout'>
						var sampleWidgetChild1LP = new ${getWidgetName()}_LayoutParams();
						sampleWidgetChild1LP.setLayoutRemoveAllRules(true);
						this.sampleWidgetChild1.reset().setLayoutParams(sampleWidgetChild1LP);
						await this.executeCommand(this.sampleWidgetChild1);
						</#if>
						var sampleWidgetChild0LP = new ${getWidgetName()}_LayoutParams();
						sampleWidgetChild0LP.<#if myclass.group=='RelativeLayout'>setLayoutRemoveAllRules(true).</#if>${attrs.setter}(${testcase}).${attrs.tryGetGetter}();
						this.sampleWidgetChild0.reset().setLayoutParams(sampleWidgetChild0LP);
						await this.executeCommand(this.sampleWidgetChild0);
						<#elseif attrs.trimmedAttribute == "background" ||  attrs.trimmedAttribute == "foreground">
						this.sampleWidget.reset().${attrs.setter}(${testcase});
						await this.executeCommand(this.sampleWidget);
						await this.delay(1000);
						this.sampleWidget.reset().${attrs.tryGetGetter}();
						await this.executeCommand(this.sampleWidget);
						<#else>
						this.sampleWidget.reset().${attrs.setter}(${testcase}).${attrs.tryGetGetter}();
						await this.executeCommand(this.sampleWidget);
						</#if>
						
						<#if attrs.trimmedAttribute?starts_with("layout_")>
						value = this.sampleWidgetChild0.getLayoutParams<${getWidgetName()}_LayoutParams>().${attrs.getter}();
						<#else>
						value = this.sampleWidget.${attrs.getter}();
						</#if>
						
						<#if testcase == "'@color/color_state'">
						if (value != '#3E813D') {
							errors.push('${attrs.getter} ' + value +" " + (${testcase}));
						}
						<#elseif testcase == "'@color/bg'">
						if (value != '#0000FF') {
							errors.push('${attrs.getter} ' + value +" " + (${testcase}));
						}
						<#elseif testcase == "'@drawable/drawable_color_selector'">
						if (value != "#FF0000") {
							errors.push('${attrs.getter} ' + value +" " + (${testcase}));
						}
						<#elseif attrs.type == "image" ||  ((attrs.type == "colorimage" || attrs.type == 'drawable') && (testcase?starts_with("'@drawable/") || testcase?starts_with("'@drawable/")))>
						if (value == null || !value.startsWith("data:")) {
							errors.push('${attrs.getter} ' + value +" " + (${testcase}));
						}
						<#elseif attrs.type == "dimensionsppxint" && testcase == "'30px'">
						if (value != '30sp') {
							errors.push('${attrs.getter} ' + value +" " + (${testcase}));
						}
						<#elseif testcase == "'@string/sample_text1'">
						if (value != 'Sample Text1') {
							errors.push('${attrs.getter} ' + value +" " + (${testcase}));
						}
						<#elseif testcase == "'@string/sample_text'">
						if (value != 'Sample Text') {
							errors.push('${attrs.getter} ' + value +" " + (${testcase}));
						}
						<#elseif attrs.type == "gravity">
						if (this.checkGravity(value, ${testcase})) {
							errors.push('${attrs.getter} ' + value +" " + (${testcase}));
						}
						<#elseif attrs.trimmedAttribute == "layoutDirection">
						if (this.checkLayoutDirection(value, ${testcase})) {
							errors.push('${attrs.getter} ' + value +" " + (${testcase}));
						}
						<#elseif attrs.trimmedAttribute == "textAlignment">
						if (this.checkTextAlignment(value, ${testcase})) {
							errors.push('${attrs.getter} ' + value +" " + (${testcase}));
						}
						<#elseif attrs.trimmedAttribute == "textDirection">
						if (this.checkTextDirection(value, ${testcase})) {
							errors.push('${attrs.getter} ' + value +" " + (${testcase}));
						}
						<#else>
						if (value != ${testcase}) {
							errors.push('${attrs.getter} ' + value +" " + (${testcase}));
						}
						</#if>
					</#list>
				</#if>
			</#if>
			</#if>
		</#list>
		
		if (errors.length > 0) {
			alert(errors);
		} else {
			alert("Success!!");
		}
		
		} catch (e) {
			alert(e);
		}
	}
	
	checkTextDirection(value: string, testcase: string) {
		if (testcase == 'inherit' && value == 'firstStrong') {
			return false;
		} 

		return value != testcase;
	}
	checkTextAlignment(value: string, testcase: string) {
		if (testcase == 'inherit' && value == 'gravity') {
			return false;
		} 

		return value != testcase;
	}
	checkLayoutDirection(value: string, testcase: string) {
		if ((testcase == 'locale' || testcase == 'inherit')  && value == 'ltr') {
			return false;
		} 
		
		return value != testcase;
	}
	checkGravity(value: string, testcase: string) {
		if (testcase == 'fill_horizontal' && value == 'top|fill_horizontal') {
			return false;
		} 
		if (testcase == 'end' && (value == 'top|right' || value == 'right')) {
			return false;
		}
		if (testcase == 'start' && (value == 'top|left' || value == 'left')) {
			return false;
		} 
		if (testcase == 'clip_horizontal' && value == 'top|left|clip_horizontal') {
			return false;
		}
		
		if (testcase == 'clip_vertical' && (value == 'top|left' || value == 'top|left|clip_vertical')) {
			return false;
		} 
		
		if (testcase == 'fill' && value == 'fill_vertical|fill_horizontal') {
			return false;
		} 
		
		if (testcase == 'center' && value == 'center_vertical|center_horizontal') {
			return false;
		}
		
		if (testcase == 'center_horizontal' && value == 'top|center_horizontal') {
			return false;
		}
		
		if (testcase == 'fill_vertical' && value == 'fill_vertical|left') {
			return false;
		}
		
		if (testcase == 'center_vertical' && value == 'center_vertical|left') {
			return false;
		}
		
		if (testcase == 'right' && value == 'top|right') {
			return false;
		}
		
		if (testcase == 'left' && value == 'top|left') {
			return false;
		}
		
		if (testcase == 'bottom' && value == 'bottom|left') {
			return false;
		}
		
		if (testcase == 'top' && value == 'top|left') {
			return false;
		}
		
		if (testcase == 'clip_vertical' && value == '') {
			return false;
		}
		return value != testcase;
	}
	${extraCode}
	
	    
    <#list allAttributes as attrs>
	<#if attrs.trimmedAttribute?starts_with('on')>
	async ${attrs.trimmedAttribute}(obj:any) {		
		<#if attrs.trimmedAttribute?starts_with('onDrag') || attrs.trimmedAttribute?starts_with('onTouch') ||  attrs.trimmedAttribute?starts_with('onScrollChange') || attrs.trimmedAttribute?starts_with('onChronometerTick')>
		console.log("${attrs.trimmedAttribute}" + JSON.stringify(obj));
		<#else>
		alert("${attrs.trimmedAttribute}" + JSON.stringify(obj));
		</#if>		
				
	}
	</#if>
	</#list>	
	// end - body
}