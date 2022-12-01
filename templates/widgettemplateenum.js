<#macro addEnum attrs>
		<#if !attrs.codeExtensionOnly><#if attrs.keys?has_content>
		<#if attrs.converterType == "enumtoint">
		export const enum ${attrs.typeVariable} {
			<#assign x = 0>
			<#list attrs.keys as key>		
			<#if attrs.values[x]?has_content>
			${key?replace("-", "_")} =  "${key}",	
			</#if>
			<#assign x = x + 1>
			</#list>
		}
 		</#if>
		<#if attrs.converterType == "stringtoenum">
		export const enum ${attrs.typeVariable} {
			<#assign x = 0>
			<#list attrs.keys as key>		
			${key} =  "${key}",	
			<#assign x = x + 1>
			</#list>
		}		 
		</#if>
		<#if attrs.converterType == "bitflag">
		export const enum ${attrs.typeVariable} {
			<#assign x = 0>
			<#list attrs.keys as key>
			${key} =  "${key}",	
			<#assign x = x + 1>
			</#list>
		}			 
		</#if>
		</#if></#if>
</#macro>

<#compress>
	<#assign seen_enums = []>	
	<#list myclass.allAttributes as attrs>
		 <#if !seen_enums?seq_contains(attrs.typeVariable)><@addEnum attrs=attrs></@addEnum></#if>
		<#assign seen_enums = seen_enums + [attrs.typeVariable]>
		<#if attrs.methodParams?has_content>
			<#list attrs.methodParams as param>
			 <#if !seen_enums?seq_contains(param.typeVariable)><@addEnum attrs=param></@addEnum></#if>
			<#assign seen_enums = seen_enums + [param.typeVariable]>
			</#list>
		</#if>
	</#list>
</#compress>	