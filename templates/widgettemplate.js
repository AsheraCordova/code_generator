<#macro createTransformer attrs>
<#if (attrs.converterType?? && attrs.converterType == 'bitflag')>
export class ${attrs.typeVariable}Transformer implements ITranform {
    transform(value: any, obj: any, type: number) : any{
        if (type == 1) {
            return value.toString().replace(",", "|");
        } else {
            let strArray:Array<string> = value.toString().split("|");
            
            let valueArr:Array<${attrs.typeVariable}> = new Array<${attrs.typeVariable}>();
            for (let i =0; i < strArray.length; i++) {
                switch(strArray[i]) {
					<#list attrs.keys as key>
					case "${key}":
						valueArr.push(${attrs.typeVariable}.${key});
                       	break;	
                    </#list>				
                }
                
            }
            return valueArr;
        }
    }
}
</#if>
</#macro>
<#macro setterMethodParams attrs strip_whitespace=true>
<#compress>
<#if attrs.methodParams?has_content>
<#list attrs.methodParams as param>
<@getTypeScriptTypeVarForMethodParam attrs=param></@getTypeScriptTypeVarForMethodParam> : <@getTypeScriptType attrs=param></@getTypeScriptType><#if param?has_next>,</#if>
</#list>
<#else>
<#if attrs.type != 'nil'><@getTypeScriptTypeVar attrs=attrs></@getTypeScriptTypeVar> : <@getTypeScriptType attrs=attrs></@getTypeScriptType><#if attrs.params?has_content>,${attrs.params}</#if></#if>
</#if>
</#compress>
</#macro>

<#macro getTypeScriptTypeVarForMethodParam attrs strip_whitespace=true>
<#compress>
	<#if attrs.type == 'gravity' || (attrs.converterType?? && attrs.converterType == 'bitflag')>
		...${attrs.attribute}
	<#else>	
	${attrs.attribute}
	</#if>
</#compress>
</#macro>


<#macro generateGetterAndSetter attrs alais="">

	<#if attrs.getterMethod?has_content>
	public <#if alais == ''>${attrs.tryGetGetter}<#else>${attrs.getTryGetterForAlias(alais)}</#if>() : T {
		this.resetIfRequired();
		if (this.${attrs.attributeForTs} == null || this.${attrs.attributeForTs} == undefined) {
			this.${attrs.attributeForTs} = new CommandAttr<<@getTypeScriptType attrs=attrs></@getTypeScriptType>>()
		}
		
		this.${attrs.attributeForTs}.setGetter(true);
		this.orderGet++;
		this.${attrs.attributeForTs}.setOrderGet(this.orderGet);
		return this.thisPointer;
	}
	
	public <#if alais == ''>${attrs.getter}<#else>${attrs.getGetterForAlias(alais)}</#if>() : <@getTypeScriptType attrs=attrs></@getTypeScriptType> {
		if (this.${attrs.attributeForTs} == null || this.${attrs.attributeForTs} == undefined) {
			this.${attrs.attributeForTs} = new CommandAttr<<@getTypeScriptType attrs=attrs></@getTypeScriptType>>();
		}
		<@getTransformer attrs=attrs></@getTransformer>
		return this.${attrs.attributeForTs}.getCommandReturnValue();
	}
	</#if>
	<#if attrs.readOnly??  && attrs.readOnly != 'true' && !attrs.codeExtensionOnly>public <#if alais == ''>${attrs.setter}<#else>${attrs.getSetterForAlias(alais)}</#if>(<@setterMethodParams attrs=attrs />) : T {
		this.resetIfRequired();
		if (this.${attrs.attributeForTs} == null || this.${attrs.attributeForTs} == undefined) {
			this.${attrs.attributeForTs} = new CommandAttr<<@getTypeScriptType attrs=attrs></@getTypeScriptType>>();
		}
		
		<#if attrs.methodParams?has_content>
		let wrapper:<@getTypeScriptType attrs=attrs></@getTypeScriptType> = new <@getTypeScriptType attrs=attrs></@getTypeScriptType>();
		<#list attrs.methodParams as param>
		wrapper.${param.attribute} = ${param.attribute};
		</#list>
		this.${attrs.attributeForTs}.setSetter(true);
		this.${attrs.attributeForTs}.setValue(wrapper);	
		<#elseif attrs.params?has_content>
		let wrapper:any = {};
		wrapper["${attrs.params?keep_before(':')}"] = ${attrs.params?keep_before(':')};
		wrapper["data"] = value;
		this.${attrs.attributeForTs}.setSetter(true);
		this.${attrs.attributeForTs}.setValue(wrapper);	
		<#else>
		this.${attrs.attributeForTs}.setSetter(true);
		<#if attrs.type != 'nil'>this.${attrs.attributeForTs}.setValue(value);</#if>
		</#if>
		this.orderSet++;
		this.${attrs.attributeForTs}.setOrderSet(this.orderSet);
		<@getTransformer attrs=attrs></@getTransformer>
		return this.thisPointer;
	}</#if>
		
	<#if attrs.methodParams?has_content && attrs.javascriptWrapperClass??>	
	public ${attrs.setter}With<@getTypeScriptType attrs=attrs></@getTypeScriptType>(...arr: <@getTypeScriptType attrs=attrs></@getTypeScriptType>[]) : T {
		this.resetIfRequired();
		if (this.${attrs.attributeForTs} == null || this.${attrs.attributeForTs} == undefined) {
			this.${attrs.attributeForTs} = new CommandAttr<<@getTypeScriptType attrs=attrs></@getTypeScriptType>[]>();
		}
		
		this.${attrs.attributeForTs}.setSetter(true);
		this.${attrs.attributeForTs}.setValue(arr);	
		this.orderSet++;
		this.${attrs.attributeForTs}.setOrderSet(this.orderSet);
		return this.thisPointer;
	}
	</#if>
</#macro>

<#macro getTypeScriptType attrs strip_whitespace=true>
<#compress>

	<#if attrs.type?starts_with("on")>
		string
	<#elseif attrs.type?starts_with("id")>
    		string
    <#elseif attrs.javascriptWrapperClass??>
    	${attrs.javascriptWrapperClass}		
    <#elseif attrs.type == 'object' && attrs.methodParams?has_content>
    	${myclass.widgetName}_${attrs.trimmedAttribute}
	<#elseif attrs.type == 'flatmap' || attrs.type == 'object'>
		any
	<#elseif attrs.type == 'flatmaps'>
		Array<any>	
	<#elseif attrs.supportIntegerAlso>
	 	string
	<#elseif attrs.type == 'int' || attrs.type == 'float'>
		number
	<#elseif attrs.type == 'image' || attrs.type == 'Image' || attrs.type == 'colorstate' || attrs.type == 'color' || attrs.type == 'String' || attrs.type=="dimension" || attrs.type=="dimensionfloat" || attrs.type=="dimensionsp" || attrs.type=="colorimage" || attrs.type=="dimensionspint" || attrs.type=="dimensionsppxint" || attrs.type=="drawable" || attrs.type == 'resourcestring' || attrs.type == 'template' || attrs.type='font' || attrs.type?contains('constraintReferencedIds') || attrs.type=='style' || attrs.type=='xmlresource'
		|| attrs.type == 'swtbitflag' || attrs.type=="dimensionpx" || attrs.type=="array" || attrs.type=="dimensiondppx">
		string
	<#elseif attrs.type == 'nil'>
		void
	<#elseif attrs.type == 'gravity'>
		Gravity[]
	<#elseif attrs.type == 'boolean' || attrs.type == 'string'>
		${attrs.type}
	<#elseif (attrs.converterType?? && attrs.converterType == 'bitflag')>
		${attrs.typeVariable}[]	
	<#else>	
		${attrs.typeVariable}
	</#if>
</#compress>
</#macro>

<#macro getTypeScriptTypeVar attrs strip_whitespace=true>
<#compress>
	<#if attrs.type == 'gravity' || (attrs.converterType?? && attrs.converterType == 'bitflag')>
		...value
	<#else>	
	value
	</#if>
</#compress>
</#macro>

<#macro getTransformer attrs strip_whitespace=true>
<#compress>
	<#if attrs.type == 'gravity'>
	this.${attrs.attributeForTs}.setTransformer('gravity');
	<#elseif (attrs.converterType?? && attrs.converterType == 'bitflag')>
	this.${attrs.attributeForTs}.setTransformer('${attrs.type}');
	<#else>	
	
	</#if>
</#compress>
</#macro>
// start - imports
<#include "/templates/widgettemplateenum.js">

import CommandAttr from '../../widget/CommandAttr';
import IWidget from '../../widget/IWidget';
import ILayoutParam from '../../widget/ILayoutParam';
import {plainToClass, Type, Exclude, Expose, Transform} from "class-transformer";
import 'babel-polyfill';
import {Gravity} from '../../widget/TypeConstants';
import {ITranform, TransformerFactory} from '../../widget/TransformerFactory';
import {Event} from '../../app/Event';
import {MotionEvent} from '../../app/MotionEvent';
import {DragEvent} from '../../app/DragEvent';
import {KeyEvent} from '../../app/KeyEvent';
import { ScopedObject } from '../../app/ScopedObject';

<#list myclass.allAttributes as attrs>
<#compress>
<#if attrs.methodParams?has_content && !(attrs.javascriptWrapperClass??)>
export class ${myclass.widgetName}_${attrs.trimmedAttribute} {
<#list attrs.methodParams as param>
	@Expose({ name: "${param.trimmedAttribute}" })
	<#if (param.converterType?? && param.converterType == 'bitflag')>
	@Transform(({value, obj, type}) => TransformerFactory.getInstance().transform(value, obj, type, "${param.type}"))
	</#if>
	${param.attributeForTs}!:<@getTypeScriptType attrs=param />;
</#list>
}
<#list attrs.methodParams as param>
<@createTransformer attrs=param />
</#list>
</#if>
</#compress>

<@createTransformer attrs=attrs />
</#list>
<#assign mywidgetName = myclass.widgetName?replace("Impl", "")>
<#assign mysuperwidgetName = myclass.widgetSuperClass?replace("Impl", "")>
<#if myclass.widgetName != 'ViewGroupImpl' && myclass.widgetSuperClass == 'ViewGroupImpl'>import {${myclass.widgetSuperClass}_LayoutParams} from './${myclass.widgetSuperClass}';</#if>
<#if myclass.widgetName == 'ViewImpl'>import {ViewGroup_LayoutParams} from './ViewGroupImpl';</#if>
// end - imports
import {${myclass.widgetSuperClass}} from './${myclass.widgetSuperClass}';
export abstract class ${myclass.widgetName}<T> extends ${myclass.widgetSuperClass}<T>{
	//start - body
	static initialize() {
		<#list myclass.allAttributes as attrs>
		<#if (attrs.converterType?? && attrs.converterType == 'bitflag')>
		TransformerFactory.getInstance().register("${attrs.type}", new ${attrs.typeVariable}Transformer());
		</#if>
		<#if attrs.methodParams?has_content>
		<#list attrs.methodParams as param>
		<#if (param.converterType?? && param.converterType == 'bitflag')>
		TransformerFactory.getInstance().register("${param.type}", new ${param.typeVariable}Transformer());
		</#if>
		</#list>
		</#if>
		</#list>
    }	
	<#list myclass.widgetAttributes as attrs>
	<#if attrs.attributeForTs != 'id' && !attrs.codeExtensionOnly>
	@Type(() => CommandAttr)
	@Expose({ name: "${attrs.trimmedAttribute}" })
	${attrs.attributeForTs}!:CommandAttr<<@getTypeScriptType attrs=attrs></@getTypeScriptType><#if attrs.methodParams?has_content && attrs.javascriptWrapperClass??>|<@getTypeScriptType attrs=attrs></@getTypeScriptType>[]</#if>>| undefined;
	</#if>
	</#list>

	@Exclude()
	protected thisPointer: T;	
	protected abstract getThisPointer(): T;
	<#if myclass.widgetName == 'ViewImpl'>
	@Exclude()
	protected orderGet: number = 0;
	@Exclude()
    protected orderSet: number = 0;
    protected flush = false;
	public markForReset() {
		this.flush = true;
	}
	public resetIfRequired() {
		if (this.flush) {
			this.reset()
		}
	}
	</#if>
	reset() : T {	
	<#if myclass.widgetName != 'ViewImpl'>
		super.reset();
	</#if>
	<#list myclass.widgetAttributes as attrs>
	<#if attrs.attributeForTs != 'id' && !attrs.codeExtensionOnly>
		this.${attrs.attributeForTs} = undefined;
	</#if>
	</#list>
	<#if myclass.widgetName == 'ViewImpl'>
		this.orderGet = 0;
		this.orderSet = 0;
		this.flush = false;
	</#if>
		return this.thisPointer;
	}
	<#if myclass.widgetName == 'ViewImpl'>
	id: string;
	paths: string[];
	event: string;
	@Expose({ name: "layoutParams" })
	layoutParams: any;
	constructor(id: string, paths: string[], event: string) {		
		this.id = id;
		this.paths = paths;
		this.event = event;
		this.thisPointer = this.getThisPointer();
		this.layoutParams = undefined;
	}
	
	setLayoutParams<M extends ILayoutParam>(layoutParams: M) {
		this.resetIfRequired();
		this.layoutParams = layoutParams;
	}
	
	getLayoutParams<M extends ILayoutParam>() : M{
		return this.layoutParams;
	}
	<#else>	
	constructor(id: string, path: string[], event:  string) {
		super(id, path, event);
		this.thisPointer = this.getThisPointer();
	}
	</#if>
	
	<#list myclass.widgetAttributes as attrs>
	<#if attrs.attributeForTs != 'id'>
	<@generateGetterAndSetter attrs=attrs></@generateGetterAndSetter>
	<#list attrs.aliases as alais>
	<@generateGetterAndSetter attrs=attrs alais=alais></@generateGetterAndSetter>
	</#list>	
	</#if>
	</#list>	
	//end - body

}
	
//start - staticinit
<#if myclass.layoutAttributes?has_content>
export abstract class ${myclass.widgetName}_LayoutParams<T> <#if myclass.widgetName != 'ViewGroupImpl'>extends ${myclass.widgetSuperClass}_LayoutParams<T></#if> {
	<#list myclass.layoutAttributes as attrs>
	<#if attrs.attributeForTs != 'id'>
	@Type(() => CommandAttr)
	@Expose({ name: "${attrs.trimmedAttribute}" })
	${attrs.attributeForTs}!:CommandAttr<<@getTypeScriptType attrs=attrs></@getTypeScriptType>>| undefined;
	</#if>
	</#list>
	@Exclude()
	protected thisPointer: T;	
	protected abstract getThisPointer(): T;
	<#if myclass.widgetName == 'ViewGroupImpl'>
	@Exclude()
	protected orderGet: number = 0;
	@Exclude()
    protected orderSet: number = 0;
	</#if>
	reset() : T {	
	<#if myclass.widgetName != 'ViewGroupImpl'>
		super.reset();
	</#if>
	<#list myclass.layoutAttributes as attrs>
	<#if attrs.attributeForTs != 'id'>
		this.${attrs.attributeForTs} = undefined;
	</#if>
	</#list>
	<#if myclass.widgetName == 'ViewGroupImpl'>
		this.orderGet = 0;
		this.orderSet = 0;
	</#if>
		return this.thisPointer;
	}
	<#if myclass.widgetName == 'ViewGroupImpl'>
	constructor() {		
		this.thisPointer = this.getThisPointer();
	}
	<#else>	
	constructor() {
		super();
		this.thisPointer = this.getThisPointer();
	}
	</#if>
	
	<#list myclass.layoutAttributes as attrs>
	<#if attrs.getterMethod?has_content>
	public ${attrs.tryGetGetter}() : T {
		if (this.${attrs.attributeForTs} == null || this.${attrs.attributeForTs} == undefined) {
			this.${attrs.attributeForTs} = new CommandAttr<<@getTypeScriptType attrs=attrs></@getTypeScriptType>>()
		}
		
		this.${attrs.attributeForTs}.setGetter(true);
		this.orderGet++;
		this.${attrs.attributeForTs}.setOrderGet(this.orderGet);
		return this.thisPointer;
	}
	
	public ${attrs.getter}() : <@getTypeScriptType attrs=attrs></@getTypeScriptType> {
		if (this.${attrs.attributeForTs} == null || this.${attrs.attributeForTs} == undefined) {
			this.${attrs.attributeForTs} = new CommandAttr<<@getTypeScriptType attrs=attrs></@getTypeScriptType>>();
		}
		<@getTransformer attrs=attrs></@getTransformer>
		return this.${attrs.attributeForTs}.getCommandReturnValue();
	}
	</#if>
	public ${attrs.setter}(<@getTypeScriptTypeVar attrs=attrs></@getTypeScriptTypeVar> : <@getTypeScriptType attrs=attrs></@getTypeScriptType><#if attrs.params?has_content>,${attrs.params}</#if>) : T {
		if (this.${attrs.attributeForTs} == null || this.${attrs.attributeForTs} == undefined) {
			this.${attrs.attributeForTs} = new CommandAttr<<@getTypeScriptType attrs=attrs></@getTypeScriptType>>();
		}
		<#if attrs.params?has_content>
		<@getTypeScriptTypeVar attrs=attrs></@getTypeScriptTypeVar>["${attrs.params?keep_before(':')}"] = ${attrs.params?keep_before(':')};
		</#if>
		this.${attrs.attributeForTs}.setSetter(true);
		this.${attrs.attributeForTs}.setValue(value);
		this.orderSet++;
		this.${attrs.attributeForTs}.setOrderSet(this.orderSet);
		<@getTransformer attrs=attrs></@getTransformer>
		return this.thisPointer;
	}
	</#list>
}

export class ${mywidgetName}_LayoutParams extends ${myclass.widgetName}_LayoutParams<${mywidgetName}_LayoutParams> implements ILayoutParam {
    getThisPointer(): ${mywidgetName}_LayoutParams {
        return this;
    }

   	constructor() {
		super();	
	}
}
</#if>

export class ${mywidgetName} extends ${myclass.widgetName}<${mywidgetName}> implements IWidget{
    getThisPointer(): ${mywidgetName} {
        return this;
    }
    
   	public getClass() {
		return ${mywidgetName};
	}
	
   	constructor(id: string, path: string[], event: string) {
		super(id, path, event);	
	}
}

${myclass.widgetName}.initialize();
${eventsTsCode}
//end - staticinit
