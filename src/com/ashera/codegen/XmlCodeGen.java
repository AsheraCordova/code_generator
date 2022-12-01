package com.ashera.codegen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.ashera.codegen.attributegen.AndroidAttrXml;
import com.ashera.codegen.attributegen.AndroidDoc;
import com.ashera.codegen.attributegen.AndroidJavaSource;
import com.ashera.codegen.pojo.SourceConfig;
import com.ashera.codegen.pojo.Widget;
import com.ashera.codegen.pojo.WidgetConfig;
import com.ashera.codegen.pojo.WidgetConfigs;
import com.ashera.codegen.pojo.Widgets;

public class XmlCodeGen extends com.ashera.codegen.CodeGenBase {
	public static void main(String[] args) throws Exception {
		String path = null;
		if (args.length > 0) {
			path = args[0];
		}
		XmlCodeGen xmlCodeGen = new XmlCodeGen();
		xmlCodeGen.startCodeGen(path);
	}


	private void startCodeGen(String path) throws Exception {
		javax.xml.bind.JAXBContext jaxbContext = javax.xml.bind.JAXBContext.newInstance(WidgetConfigs.class);
		javax.xml.bind.Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		WidgetConfigs widgetConfigs = (WidgetConfigs) unmarshaller.unmarshal(new java.io.FileInputStream(
				getCPDir() + "codegen/WidgetConfig.xml"));
		Widgets widgets = new Widgets();
		
		
		
		for (WidgetConfig widgetConfig : widgetConfigs.getWidgetConfigs()) {
			List<Widget> widgetList = new ArrayList<>();
			javax.xml.bind.JAXBContext jaxbContext1 = javax.xml.bind.JAXBContext.newInstance(Widgets.class);
			Unmarshaller unmarshaller1 = jaxbContext1.createUnmarshaller();
			String name = widgetConfig.getName();
			if (name.contains(".")) {
				name= name.substring(name.lastIndexOf(".") + 1);
			}
			String basePath = "../code_generator/configimpl/";
			if (path != null) {
				basePath = path;
			}
			File xmlFile = new File(basePath + "config-" + name.toLowerCase() + ".xml");
			
			Widget widget = new Widget();
			if (xmlFile.exists()) {
				java.io.FileInputStream fis = new java.io.FileInputStream(xmlFile);
				Widgets widgets1 = (Widgets) unmarshaller1.unmarshal(fis);
				widget = widgets1.getWidgetByNameAndOS(widgetConfig.getName(), "android");
			} else {
				widget.setName(widgetConfig.getName());
			}
			widgetList.add(widget);
			AndroidJavaSource androidJavaSource = new AndroidJavaSource(); 
			AndroidAttrXml androidAttrXml = new AndroidAttrXml(); 
			AndroidDoc androidDoc = new AndroidDoc(); 
			
			for (SourceConfig sourceConfig : widgetConfig.getSources()) {
				switch (sourceConfig.getType()) {
				case "androiddocurl":
					androidDoc.updateAttribute(widgetConfig, sourceConfig, widget);
					break;
					
				case "attrxml":
					androidAttrXml.updateAttribute(widgetConfig, sourceConfig, widget);
					break;
				case "javasource":
					androidJavaSource.updateAttribute(widgetConfig, sourceConfig, widget);
					break;
				default:
					break;
				}
			}

			
			if (widget.getAdditionalAttributes().isEmpty()) {
				widget.setAdditionalAttributes(null);
			}
			if (widget.getCustomAttribute() != null) {
				for (com.ashera.codegen.pojo.CustomAttribute customAttribute : widget.getCustomAttribute()) {
					if (customAttribute.getAliases().size() == 0) {
						customAttribute.setAliases((List<String>) null);
					}
					
					if (customAttribute.getDecorator().equals("null") || customAttribute.getDecorator().equals("\"null\"")) {
						customAttribute.setDecorator("@null");
					}
				}
			}
			widgets.setWidget(widgetList.toArray(new Widget[0]));
			javax.xml.bind.Marshaller marshaller = jaxbContext1.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			
			java.io.StringWriter stringWriter = new java.io.StringWriter();
			marshaller.marshal(widgets, stringWriter);
			String str = stringWriter.toString();
			String[] replace = "viewplugin=\"false\" onlyChildWidgets=\"false\" makeTestCaseVisible=\"false\" listView=\"false\" excludeFromReport=\"false\" copyParentCode=\"false\" applyBeforeChildAdd=\"false\" bufferStrategy=\"BUFFER_STRATEGY_NONE\" codeExtensionOnly=\"false\" disposable=\"false\" extraCode=\"\" ignore=\"false\" order=\"0\" preCode=\"\" readOnly=\"false\" seperatedByBar=\"false\" supportIntegerAlso=\"false\" updateUiFlag=\"UPDATE_UI_NONE\" useMethodName=\"false\" useSuperViewForAccess=\"false\"".split(" ");
			for (String x : replace) {
				str = str.replaceAll(x + " ", "");
				str = str.replaceAll(x + ">", ">");
				str = str.replaceAll(x + "/", "/");
			}
			
			writeToFile(xmlFile, str);
		}

	}
}
