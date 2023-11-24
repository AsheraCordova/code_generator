package com.ashera.codegen;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;

public class CodeGenPluginInvoker extends CodeGenBase {
	public void parse(File file, String writeTo, String keyword) throws IOException {

		final CompilationUnit cu = getCU(file);
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("//start - " + keyword + "\n");
		stringBuffer.append("\t\tswitch (name) {\n");

		cu.accept(new ASTVisitor() {

			public boolean visit(MethodDeclaration m) {	
				
				System.out.println(m);
				stringBuffer.append("\t\tcase \"" + m.getName() + "\":\n");
				if (!m.getReturnType2().toString().equals("void")) {
					stringBuffer.append("\t\t\treturn ");
				} else {
					stringBuffer.append("\t\t\t");
				}
				stringBuffer.append(m.getName() + "(");
				String seperator = "";
				int pos = 0;
				for (Object parameter : m.parameters()) {
                    VariableDeclaration variableDeclaration = (VariableDeclaration) parameter;

                    String type = variableDeclaration.getStructuralProperty(SingleVariableDeclaration.TYPE_PROPERTY)
                            .toString();
					stringBuffer.append(seperator + "(" + type.replaceAll("<T>", "")
							+ ") args[" + pos + "]");
					seperator = ",";
					pos++;
				}

				stringBuffer.append(");\n");
				
				if (m.getReturnType2().toString().equals("void")) {
					stringBuffer.append("\t\t\treturn null;\n");
				}
				return true;
			}
		});
		
		stringBuffer.append("\t\tdefault:\n\t\t\tbreak;\n\t\t}\n\t\tthrow new RuntimeException(\"Unknown method \" + name);");
		stringBuffer.append("\n\t\t//end - " + keyword + "\n");
		writeOrUpdateFile(stringBuffer.toString(), writeTo, keyword);

	}

	
	public void plugininvokerGen(File file, String writeTo, String keyword, String... mask) throws IOException {

		final CompilationUnit cu = getCU(file);
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("//start - " + keyword + "\n");

		cu.accept(new ASTVisitor() {

			public boolean visit(MethodDeclaration m) {	
				String methodCode = m.toString();
				for (String maskStr : mask) {
					methodCode = methodCode.replaceAll(maskStr, "Object");
				}

				
				stringBuffer.append("\tpublic static " + methodCode.replaceAll(";", " {").replaceAll("public ", ""));
				stringBuffer.append("\t\tIPlugin plugin = PluginManager.get(\"" + keyword  + "\");\n");
				if (!m.getReturnType2().toString().equals("void")) {
					stringBuffer.append("\t\treturn ");
				} else {
					stringBuffer.append("\t\t");
				}
				if (!m.getReturnType2().toString().equals("Object") && !m.getReturnType2().toString().equals("void")) {
					String returnType = m.getReturnType2().toString();
					for (String maskStr : mask) {
						returnType = returnType.replaceAll(maskStr, "Object");
					}
					
					stringBuffer.append("(" + returnType + ")");
				}
				stringBuffer.append("plugin.invoke(\"" + m.getName()  + "\"");
				if (!m.parameters().isEmpty()) {
					stringBuffer.append(", ");
				}
				String seperator = "";
				for (Object parameter : m.parameters()) {
                    VariableDeclaration variableDeclaration = (VariableDeclaration) parameter;
					stringBuffer.append(seperator + variableDeclaration.getName().toString());
					seperator = ",";
				}
				stringBuffer.append(");\n");
				stringBuffer.append("\t}\n\n");
				return true;
			}
		});
		
		stringBuffer.append("\t//end - " + keyword + "\n");
		System.out.println(stringBuffer);
		writeOrUpdateFile(stringBuffer.toString(), writeTo, keyword);

	}

	public static void main(String[] args) throws IOException {
		CodeGenPluginInvoker gen = new CodeGenPluginInvoker();
		String x = "IConverter<?,?>";
		System.out.println(x.replaceAll("IConverter<\\?,\\?>", "2"));
		System.out.println(x + "----");

		// general 
		gen.parse(new File("../../core-widget_library/Plugin_HtmlParser/src/com/ashera/parser/html/IHtmlParser.java"), "../../core-widget_library/Plugin_HtmlParser/src/com/ashera/parser/html/HtmlParserPlugin.java", "htmlparser");
		gen.parse(new File("../widget_library/src/com/ashera/converter/Converter.java"), "../Plugin_Converter/src/com/ashera/converter/BaseConverterPlugin.java", "converter");
		gen.plugininvokerGen(new File("../widget_library/src/com/ashera/converter/Converter.java"), "../../core-widget_library/widget_library/src/com/ashera/widget/PluginInvoker.java", "converter", "IConverter<\\?,\\?>", "IConverter<Object,Object>");		
		
		// core plugin
	    gen.parse(new File("../../core-android-widget/AndroidCorePlugin/src/com/ashera/core/ICore.java"), "../../core-android-widget/AndroidCorePlugin/src/com/ashera/core/CorePlugin.java", "core");
	    gen.parse(new File("../../core-javafx-widget/SWTCorePlugin/src/main/java/com/ashera/core/ICore.java"), "../../core-javafx-widget/SWTCorePlugin/src/main/java/com/ashera/core/CorePlugin.java", "core");
	    gen.parse(new File("../../core-ios-widgets/IOSCorePlugin/src/main/java/com/ashera/core/ICore.java"), "../../core-ios-widgets/IOSCorePlugin/src/main/java/com/ashera/core/CorePlugin.java", "core");
	    gen.parse(new File("../../core-web-widget/WebCorePlugin/src/main/java/com/ashera/core/ICore.java"), "../../core-web-widget/WebCorePlugin/src/main/java/com/ashera/core/CorePlugin.java", "core");
		
	    // json plugin
		gen.parse(new File("../../core-android-widget/AndroidJsonAdapter/src/com/ashera/jsonadapter/JSONAdapter.java"), "../../core-android-widget/AndroidJsonAdapter/src/com/ashera/jsonadapter/JSONAdapterImpl.java", "jsonadapter");
		gen.parse(new File("../../core-android-widget/AndroidJsonAdapter/src/com/ashera/jsonadapter/JSONAdapter.java"), "../../core-javafx-widget/SwtJsonAdapter/src/main/java/com/ashera/jsonadapter/JSONAdapterImpl.java", "jsonadapter");
		gen.parse(new File("../../core-android-widget/AndroidJsonAdapter/src/com/ashera/jsonadapter/JSONAdapter.java"), "../../core-ios-widgets/IOSJSONAdapter/src/main/java/com/ashera/jsonadapter/JSONAdapterImpl.java", "jsonadapter");
		gen.parse(new File("../../core-android-widget/AndroidJsonAdapter/src/com/ashera/jsonadapter/JSONAdapter.java"), "../../core-web-widget/WebJsonAdapter/src/main/java/com/ashera/jsonadapter/JSONAdapterImpl.java", "jsonadapter");

		gen.plugininvokerGen(new File("../../core-widget_library/Plugin_HtmlParser/src/com/ashera/parser/html/IHtmlParser.java"), "../../core-widget_library/widget_library/src/com/ashera/widget/PluginInvoker.java", "htmlparser");
	    gen.plugininvokerGen(new File("../../core-android-widget/AndroidCorePlugin/src/com/ashera/core/ICore.java"), "../../core-widget_library/widget_library/src/com/ashera/widget/PluginInvoker.java", "core");
		gen.plugininvokerGen(new File("../../core-android-widget/AndroidJsonAdapter/src/com/ashera/jsonadapter/JSONAdapter.java"), "../../core-widget_library/widget_library/src/com/ashera/widget/PluginInvoker.java", "jsonadapter");
	    
		Properties properties;
		properties = new Properties();
		properties.load(new FileInputStream("plugincodegen/copyfiles.properties"));
		gen.copyFiles(properties, true);
		
		updateFiles(properties);
		
		properties = new Properties();
		properties.load(new FileInputStream("plugincodegen/androidcopyfiles.properties"));
		gen.copyFiles(properties, false);
		
		updateFiles(properties);
		
	}

	private static void updateFiles(Properties properties) throws IOException {
		for (int i = 0; i < 100; i++) {
			if (properties.containsKey("update.files." + i)) {
				String[] keyStr = properties.getProperty("update.files." + i).split("\\:");
				String code = readFileToString(new File(keyStr[0]));
				writeOrUpdateFile(code, keyStr[1], keyStr[2].split(","));

			}
		}
	}

}
