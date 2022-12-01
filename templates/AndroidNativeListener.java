${methodSignatureListener}{
    <#if returnType == 'boolean'>
    boolean result = true;
    <#elseif returnType == 'int'>
    int result = 0; 
    <#elseif returnType == 'WindowInsets'>
    WindowInsets result = insets;
    <#elseif returnType == 'void'>
    <#else>
    ${returnType} result = null;
    </#if>
    <#assign s = 'pre'><#assign s += eventGet><#assign s += '|'>
    <#if myclass.createDefault?contains(s)>
    	((${myclass.widgetName})w).pre${eventGet}();
    </#if>
    
	if (action == null || action.equals("${event}")) {
		// populate the data from ui to pojo
		w.syncModelFromUiToPojo("${event}");
	    java.util.Map<String, Object> obj = get${eventGet}EventObj(${paramsStr});
	    String commandName =  (String) obj.get(EventExpressionParser.KEY_COMMAND_NAME);
	    
	    // execute command based on command type
	    String commandType = (String)obj.get(EventExpressionParser.KEY_COMMAND_TYPE);
		switch (commandType) {
		case "+":
		case ":":
		    if (EventCommandFactory.hasCommand(commandName)) {
		    	 <#if returnType != 'void'>Object commandResult = </#if>EventCommandFactory.getCommand(commandName).executeCommand(w, obj<#if paramsStr!=''>, </#if>${paramsStr});
		    	 <#if returnType != 'void'>
		    	 if (commandResult != null) {
		    		 result = (${returnType}) commandResult;
		    	 }
		    	 </#if>
		    }
		    if (commandType.equals(":")) {
		    	<#if returnType=='void'>return;<#else>return result;</#if>
		    }
			
			break;
		default:
			break;
		}
		
		if (obj.containsKey("refreshUiFromModel")) {
			Object widgets = obj.remove("refreshUiFromModel");
			com.ashera.layout.ViewImpl.refreshUiFromModel(w, widgets, true);
		}
		if (w.getModelUiToPojoEventIds() != null) {
			com.ashera.layout.ViewImpl.refreshUiFromModel(w, w.getModelUiToPojoEventIds(), true);
		}
		if (strValue != null && !strValue.isEmpty()) {
		    com.ashera.core.IActivity activity = (com.ashera.core.IActivity)w.getFragment().getRootActivity();
		    activity.sendEventMessage(obj);
		}
	}
    <#if returnType=='void'>return;<#else>return result;</#if>
}//#####

public java.util.Map<String, Object> get${eventGet}EventObj(${paramsWithTypes}) {
	java.util.Map<String, Object> obj = com.ashera.widget.PluginInvoker.getJSONCompatMap();
    obj.put("action", "action");
    obj.put("eventType", "${action}");
    obj.put("fragmentId", w.getFragment().getFragmentId());
    obj.put("actionUrl", w.getFragment().getActionUrl());
    
    if (w.getComponentId() != null) {
    	obj.put("componentId", w.getComponentId());
    }
    
    PluginInvoker.putJSONSafeObjectIntoMap(obj, "id", w.getId());
     
    <#assign x = 0>
    <#list params as param>
        <#if  paramTypes[x] == 'int' ||  paramTypes[x] == 'boolean' || paramTypes[x] == 'String' || paramTypes[x] == 'float'  || paramTypes[x] == 'double' || paramTypes[x] == 'List<Integer>'>
        PluginInvoker.putJSONSafeObjectIntoMap(obj, "${param}", ${param});
        </#if>
        <#if  paramTypes[x] == 'CharSequence' >
        PluginInvoker.putJSONSafeObjectIntoMap(obj, "${param}", String.valueOf(${param}));
        </#if>
        <#if param == 'event'>
        ViewImpl.addEventInfo(obj, event);
        </#if>
        <#assign s = param>
        <#assign s1 = param>
        <#assign s += 'EventParam|'>
        <#assign s1 += 'ContextEventParam|'>
        <#if myclass.createDefault?contains(s)>
        ViewImpl.addEventInfo(obj, ${param}<#if myclass.createDefault?contains(s1)>, w.getFragment()</#if>);
        </#if>
        <#assign x = x + 1>
    </#list>
    
    // parse event info into the map
    EventExpressionParser.parseEventExpression(strValue, obj);
    
    // update model data into map
    w.updateModelToEventMap(obj, "${event}", (String)obj.get(EventExpressionParser.KEY_EVENT_ARGS));
    return obj;
}