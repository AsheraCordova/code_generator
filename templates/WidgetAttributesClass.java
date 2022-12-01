<#macro generateConverter attrs>
		<#if !attrs.codeExtensionOnly><#if attrs.keys?has_content>
		<#if attrs.converterType == "enumtoint">
		@SuppressLint("NewApi")
		final static class ${attrs.typeVariable} extends AbstractEnumToIntConverter{
		private Map<String, Integer> mapping = new HashMap<>();
				{
		<#assign x = 0>
		<#list attrs.keys as key>
				mapping.put("${key}",  <#if !attrs.values[x]?is_number && !attrs.values[x]?contains(".") && !attrs.values[x]?contains("-")>0x</#if>${attrs.values[x]});
		<#assign x = x + 1>
		</#list>
				}
		@Override
		public Map<String, Integer> getMapping() {
				return mapping;
				}

		@Override
		public Integer getDefault() {
				return 0;
				}
		<#if attrs.supportIntegerAlso>
		@Override
		public boolean supportsIntAlso() {
			return true;
		}
		</#if>
				}
		</#if>
		<#if attrs.converterType == "stringtoenum">
		@SuppressLint("NewApi")
		final static class ${attrs.typeVariable}  extends AbstractStringToEnumConverter{
		private Map<String, Object> mapping = new HashMap<>();
				{
		<#assign x = 0>
		<#list attrs.keys as key>
		<#if attrs.apis[x] ?has_content>if (Build.VERSION.SDK_INT >= ${attrs.apis[x]}) {</#if>
				mapping.put("${key}", ${attrs.values[x]});
		<#if attrs.apis[x] ?has_content>}</#if>
		<#assign x = x + 1>
		</#list>
				}
		@Override
		public Map<String, Object> getMapping() {
				return mapping;
				}

		@Override
		public Object getDefault() {
				return null;
				}
				}
		</#if>
		<#if attrs.converterType == "bitflag">
		@SuppressLint("NewApi")
		final static class ${attrs.typeVariable}  extends AbstractBitFlagConverter{
		private Map<String, Integer> mapping = new HashMap<>();
				{
		<#assign x = 0>
		<#list attrs.keys as key>
				mapping.put("${key}", ${getBitFlagValue(attrs.values[x])});
		<#assign x = x + 1>
		</#list>
				}
		@Override
		public Map<String, Integer> getMapping() {
				return mapping;
				}

		@Override
		public Integer getDefault() {
				return 0;
				}
		<#if attrs.allOption??>
		@Override
		public java.lang.String getAllOption() {
			return "${attrs.allOption}";
		}
		</#if>
				}
		</#if>
		</#if></#if>
</#macro>
	<#assign seen_enums = []>	
	<#list myclass.widgetAttributes as attrs>
		<#if !seen_enums?seq_contains(attrs.typeVariable)><@generateConverter attrs=attrs></@generateConverter></#if>
		<#assign seen_enums = seen_enums + [attrs.typeVariable]>
		<#if attrs.methodParams?has_content>
		<#list attrs.methodParams as param>
		 <#if !seen_enums?seq_contains(param.typeVariable)><@generateConverter attrs=param></@generateConverter></#if>
		 <#assign seen_enums = seen_enums + [param.typeVariable]>
		</#list>
		</#if>
	</#list>
	<#list myclass.layoutAttributes as attrs>
		<#if !seen_enums?seq_contains(attrs.typeVariable)><@generateConverter attrs=attrs></@generateConverter></#if>
		<#assign seen_enums = seen_enums + [attrs.typeVariable]>
		<#if attrs.methodParams?has_content>
		<#list attrs.methodParams as param>
		 <#if !seen_enums?seq_contains(param.typeVariable)><@generateConverter attrs=param></@generateConverter></#if>
		 <#assign seen_enums = seen_enums + [param.typeVariable]>
		</#list>
		</#if>
	</#list>