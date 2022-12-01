export interface ${event}Event extends Event{
    <#assign x = 0>
    <#list params as param>
        <#if  paramTypes[x] == 'int' ||  paramTypes[x] == 'float'  || paramTypes[x] == 'double' || paramTypes[x] == 'long'>
	        <#if param != 'id'>
	        ${param}:number;
	        <#else>
	        idInt:number;
	        </#if>
        <#elseif  paramTypes[x] == 'boolean' || paramTypes[x] == 'string'>
        ${param}:${paramTypes[x]};
        <#else>
        //${param}:${paramTypes[x]};
	        <#if  param == 'event' &&  (paramTypes[x] == 'MotionEvent' || paramTypes[x] == 'DragEvent' || paramTypes[x] == 'KeyEvent')>
        		eventInfo: ${paramTypes[x]};
        	</#if>

        </#if>
        <#assign x = x + 1>
    </#list>            

}