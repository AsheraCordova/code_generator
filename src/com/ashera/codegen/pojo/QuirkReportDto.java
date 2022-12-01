package com.ashera.codegen.pojo;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class QuirkReportDto {
    private Map<String, Map<String, List<QuirkWidget>>> widgets = new java.util.TreeMap<>();

    public Map<String, Map<String, List<QuirkWidget>>> getWidgetMap() {
        return widgets;
    }

    public Set<QuirkWidget> getWidgetList() {
        List<QuirkWidget> quirkWidgets = widgets.values().stream().map((obj) -> obj.values()).flatMap(Collection::stream).flatMap(Collection::stream).collect(Collectors.toList());
        TreeSet<QuirkWidget> treeSet = new java.util.TreeSet<>(Comparator.comparing(QuirkWidget::getGroup));
        treeSet.addAll(quirkWidgets); 
        return treeSet;
    }
    
    public Collection<Map<String, List<QuirkWidget>>> getWidgetGroups() {
        return widgets.values();
    }
    
    public Set<QuirkWidget> getWidgetsInPackage(String packageName) {
        List<QuirkWidget> quirkWidgets = widgets.get(packageName).values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        TreeSet<QuirkWidget> treeSet = new java.util.TreeSet<>(Comparator.comparing(QuirkWidget::getGroup));
        treeSet.addAll(quirkWidgets); 
        return treeSet;
    }
    
    public Set<String> getPackages() {
        return widgets.keySet();
    }

    public void addWidget(String packageName, String group, QuirkWidget quirkWidget) {
        Map<String, List<QuirkWidget>> widgetMap = widgets.get(packageName);
        if (widgetMap == null) {
            widgetMap = new java.util.TreeMap<>();
            widgets.put(packageName, widgetMap);
        }
        
        
        List<QuirkWidget> widgets = widgetMap.get(group);
        
        if (widgets == null) {
            widgets = new java.util.ArrayList<QuirkWidget>();
            widgetMap.put(group, widgets);
        } else {
        	quirkWidget.setParentAttributes(widgets.get(0).getAttributes());
        }
        widgets.add(quirkWidget);
    }
}
