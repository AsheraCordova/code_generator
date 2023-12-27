package com.ashera.codegen;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import com.ashera.codegen.pojo.CustomAttribute;
import com.ashera.codegen.pojo.ReplaceString;
import com.ashera.codegen.pojo.XmlConfig;
import com.ashera.codegen.pojo.attrs.Resources;
import com.ashera.codegen.pojo.attrs.Resources.Attr;
import com.ashera.codegen.pojo.attrs.Resources.Attr.Enum;

public class XmlResourceCodeGenerator extends CodeGenBase{

	public static void generate(com.ashera.codegen.pojo.Widget w, Map<CustomAttribute, String> customAttributeMap, XmlConfig xmlConfig, Map<String, List<String>> methodNamesMap) throws Exception{
		String environment = w.getOs();
		File file = readHttpUrlAsString(xmlConfig.getXml(), xmlConfig.getCachekey());
		Properties widgetProperties = new Properties();
		widgetProperties.load(new FileInputStream("configimpl/common.properties"));
		List<String> knowTypes = new ArrayList<>(Arrays.asList(widgetProperties.getProperty("knowTypes").split(",")));
		javax.xml.bind.JAXBContext jaxbContext = javax.xml.bind.JAXBContext.newInstance(Resources.class);
		javax.xml.bind.Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		Resources resources = (Resources) unmarshaller.unmarshal(new java.io.FileInputStream(file));
		
		for (Resources.DeclareStyleable styleable : resources.getDeclareStyleable()) {
			if (styleable.getName().equals(xmlConfig.getTag())) {
				FieldInfoHolder holder = getFieldInfo(xmlConfig);
				String methodParamsStr = "IWidget w, ";
				String methodParamsNoTypeStr = "w, ";
				for (String methodParams : new java.util.HashSet<>(holder.paramClassMap.keySet())) {
					methodParamsStr += holder.paramClassMap.get(methodParams) + " " + methodParams + ", ";
					methodParamsNoTypeStr += methodParams + ", ";
				}

				HashMap<String, String> defhints = new HashMap<>();
				String def= xmlConfig.getDef();
				if (def != null) {
					String[] defs = def.split(";");
					for (String myDef : defs) {
						String[] myDefs = myDef.split("\\:");
						defhints.put(myDefs[0], myDefs[1]);
					}
				}
				String ignoreRegEx = xmlConfig.getIgnoreRegEx();
				StringBuffer code = new StringBuffer();
				String tagName = styleable.getName();
				code.append("//start - " + tagName);
				StringBuffer methods = new StringBuffer();
//				methods.append("//start - methods" + tagName);
				code.append("\nprivate void parse" + tagName + "(" + methodParamsStr + " org.xml.sax.Attributes atts" + ") {\n");
				code.append("for (int i = 0; i < atts.getLength(); i++) {\r\n"
						+ "					String name = atts.getLocalName(i);\r\n"
						+ "					String value = ViewImpl.getValue(name, atts);\r\n"
						+ "					switch (atts.getLocalName(i)) {");
				for (Resources.Attr attr : styleable.getAttr()) {
					//if (attr.getFormat() == null) {
						String name = getName(attr);
						System.out.println("name " + name);
						if (ignoreRegEx != null && name.matches(ignoreRegEx)) {
							System.out.println("Ignored " + name);
							continue;
						}
						code.append("\ncase \"" + name + "\":\n");
						CustomAttribute customAttribute = getCustomAttribute(name, customAttributeMap.keySet());
						
						boolean isEnum = false;
						boolean isEnumInt = false;
						if (customAttribute == null) {
							isEnum = handleEnum(xmlConfig, methodNamesMap, methods, attr, isEnum);
							if (isEnum) {
								isEnumInt = isEnumAllowsInt(attr);
							}
							if (!isEnum) {
								for (Resources.Attr enumAttr : resources.getAttr()) {
									if (enumAttr.getName().equals(name)) {
										isEnum = handleEnum(xmlConfig, methodNamesMap, methods, enumAttr, isEnum);
										if (isEnum) {
											isEnumInt = isEnumAllowsInt(enumAttr);
											break;
										}
									}
								}
							}
							
							if (!isEnum && defhints.containsKey(name)) {
								for (Resources.DeclareStyleable lstyleable : resources.getDeclareStyleable()) {
									if (defhints.get(name).equals(lstyleable.getName())) {
										for (Resources.Attr enumAttr : lstyleable.getAttr()) {
											if (enumAttr.getName().equals(name)) {
												isEnum = handleEnum(xmlConfig, methodNamesMap, methods, enumAttr, isEnum);
												if (isEnum) {
													isEnumInt = isEnumAllowsInt(enumAttr);
													break;
												}
											}
										}
									}
								}
							}
							
							
						}
						
						if (isFlag(attr)) {
							String enumcode = getFlagCode(attr, methodNamesMap.get(xmlConfig.getUpdateLocation()));
							methods.append(enumcode);
						}
						
						if (name.startsWith("layout_")) {
							name = name.substring("layout_".length());
						}
						
						String methodName = getSetter(name);
						code.append(methodName + "(" + methodParamsNoTypeStr + "value);\n");
						if (xmlConfig.getGenerateCustomSetter() != null) {
							String[] customCodeAttr = xmlConfig.getGenerateCustomSetter().split(",");
							if (Arrays.asList(customCodeAttr).contains(getName(attr))) {
								code.append(methodName + "Additional(" + methodParamsNoTypeStr + "value);\n");
							}
						}
						code.append("break;");
						//code.append("params."+ name  + " = (int) w.quickConvert(value, \"int\");");\
						if (customAttribute != null) {
							String methodCode = "\nprivate void " + methodName + "("
									+ methodParamsStr //"androidx.constraintlayout.widget.Constraints.LayoutParams params, "
									+ "String strValue) {"
									+ "\n"
									+ "Object objValue = w.quickConvert(strValue, \"" + ((customAttribute.getConverterType() == null || customAttribute.getType().indexOf(".") != -1)  ? "" : w.getLocalName() + ".") + customAttribute.getType() + "\");\n"
									+ getCode(customAttribute)
									//+ " = (" + customAttribute.getJavaType() + ") widget.quickConvert(strValue, \"" + customAttribute.getType() + "\");\n"
									+ "\n}\n";
							methods.append(methodCode);
						} else if (holder.fieldMap.containsKey(name) || holder.fieldMap.containsKey(getClassVarName(name))) {
							String fieldName = name;
							if (holder.fieldMap.containsKey(getClassVarName(name))) {
								name = getClassVarName(name);
							}
							String varType = holder.fieldMap.get(name);
							String paramName = holder.fieldParamMap.get(name);

							String converterType = getConverterType(attr, environment, widgetProperties, resources, name,
									fieldName, varType);

							String methodCode = "\nprivate void " + methodName + "("
									+ methodParamsStr //"androidx.constraintlayout.widget.Constraints.LayoutParams params, "
									+ "String strValue) {"
									+ "\n"
									+ paramName
									+ "." + name + " = "
									+ (isEnum ? getGetter(fieldName) + "(strValue);\n" : "(" + varType + ") w.quickConvert(strValue, \"" + converterType + "\");\n")
									+ "}\n";
							methods.append(methodCode);
							
							//"name: " + name + " " + infoMap.get(name)
						} else if ( holder.methodParamMap.containsKey(getSetter(name))) {
							String fieldName = name;
							String varType = holder.methodParamMap.get(getSetter(name));
							String paramName = holder.fieldParamMap.get(getSetter(name));
							
							if (varType.equals("long")) {
								varType = "int";
							}

							String converterType = getConverterType(attr, environment, widgetProperties, resources, name,
									fieldName, varType);
							if (converterType != null) {
								String methodCode = "\nprivate void " + methodName + "("
										+ methodParamsStr //"androidx.constraintlayout.widget.Constraints.LayoutParams params, "
										+ "String strValue) {"
										+ "\n"
										+ paramName
										+ "." + getSetter(name) + "("
										+ (isEnum ? getGetter(fieldName) + "(strValue" + (isEnumInt ? ", w" : "")
												+ "));\n" : "(" + varType + ") w.quickConvert(strValue, \"" + converterType + "\"));\n")
										+ "}\n";
								methods.append(methodCode);
							}
							
							//"name: " + name + " " + infoMap.get(name)
						
						}else {
							System.out.println("ignored " + name);
						}
					//}
				}
				code.append("}\n");
				code.append("}\n");
				code.append("}\n");
				code.append(methods);
				
				code.append("\n//end - " + tagName);
				String codeStr = code.toString();
				ReplaceString[] replace = xmlConfig.getReplaceString();
				if (replace != null) {
					for (ReplaceString replaceString : replace) {
						codeStr = codeStr.replaceAll(replaceString.getName(), replaceString.getReplace());
					}
				}
				writeOrUpdateFile(codeStr, xmlConfig.getUpdateLocation(), false, tagName);
			}
			
		}
	}

	private static boolean handleEnum(XmlConfig xmlConfig, Map<String, List<String>> methodNamesMap,
			StringBuffer methods, Resources.Attr attr, boolean isEnum) {
		if ((isEnum(attr) || (attr.getFormat() != null && ("enum".equals(attr.getFormat()) || attr.getFormat().indexOf("|enum") != -1)))) {
			if (!methodNamesMap.containsKey(xmlConfig.getUpdateLocation())) {
				methodNamesMap.put(xmlConfig.getUpdateLocation(), new ArrayList<>());
			}
			String enumcode = getEnumCode(attr, methodNamesMap.get(xmlConfig.getUpdateLocation()));
			isEnum = true;
			methods.append(enumcode);
		}
		return isEnum;
	}

	private static String getFlagCode(Attr attr, List<String> list) {
		String flagCode = "		final static class " + getClassName(attr.getName()) + "Converter"
				+ "  extends AbstractBitFlagConverter{\r\n"
				+ "		private Map<String, Integer> mapping = new HashMap<>();\r\n"
				+ "				{\r\n"
				+ "				";
		for (int i = 0; i < attr.getContent().size(); i++) {
			if (attr.getContent().get(i) instanceof javax.xml.bind.JAXBElement) {
				javax.xml.bind.JAXBElement element = (javax.xml.bind.JAXBElement) attr.getContent().get(i);

				if (element.getValue() instanceof com.ashera.codegen.pojo.attrs.Resources.Attr.Flag) {
					com.ashera.codegen.pojo.attrs.Resources.Attr.Flag flag = (com.ashera.codegen.pojo.attrs.Resources.Attr.Flag) element.getValue();
					flagCode += "mapping.put(\"" + flag.getName() + "\", " + flag.getValueAttribute() + ");\r\n";
				}
			}
		}
		
		flagCode += ""
				+ "				}\r\n"
				+ "		@Override\r\n"
				+ "		public Map<String, Integer> getMapping() {\r\n"
				+ "				return mapping;\r\n"
				+ "				}\r\n"
				+ "\r\n"
				+ "		@Override\r\n"
				+ "		public Integer getDefault() {\r\n"
				+ "				return 0;\r\n"
				+ "				}\r\n"
				+ "				}\r\n"
				+ "";
		flagCode += "\nstatic {\r\n"
				+ "        ConverterFactory.register(\"" + attr.getName() + ".flag" + "\", new " + getClassName(attr.getName()) + "Converter"
						+ "());\r\n"
				+ "    }\n";
		return flagCode;
	}

	private static String getConverterType(Resources.Attr attr, String environment, Properties widgetProperties, Resources resources,
			String name, String fieldName, String varType) {
		String converterType = varType;
//							if (customAttribute != null) {
//								converterType = customAttribute.getType();
//							}
		
		if (name.equals("visibility")) {
			return "View.visibility";
		}
		
		if (name.equals("orientation")) {
			return "LinearLayout.orientation";
		}
		
		com.ashera.codegen.pojo.attrs.Resources.Attr attrRes = resources.getAttr().stream().filter((x) -> x.getName().equals(fieldName)).findFirst().orElse(null);
		if (attrRes != null && attrRes.getFormat() != null && attrRes.getFormat().equals("reference")) {
			converterType = "id";
		}
		if (attrRes != null && attrRes.getFormat() != null && attrRes.getFormat().equals("float")) {
			converterType = "float";
		}
		if (attrRes != null && attrRes.getFormat() != null && attrRes.getFormat().equals("dimension")) {
			converterType = "dimension";
		}

		if (attr != null && attr.getFormat() != null && attr.getFormat().equals("float")) {
			converterType = "float";
		}
		if (attr != null && attr.getFormat() != null && attr.getFormat().equals("reference")) {
			converterType = "id";
		}

		if (attr != null && attr.getFormat() != null && attr.getFormat().equals("dimension")) {
			converterType = "dimension";
		}

		if(isFlag(attr)) {
			return attr.getName() + ".flag";
		}
		
		if (widgetProperties.containsKey(environment + ".datatype." + varType + "." + fieldName)) {
			converterType = widgetProperties.getProperty(environment + ".datatype." + varType + "." + fieldName).split(":")[0];
		}
		
		if (widgetProperties.containsKey(environment + ".datatype." + varType)) {
			converterType = widgetProperties.getProperty(environment + ".datatype." + varType).split(":")[0];
		}
		
		String knowTypes = widgetProperties.getProperty("knowTypes");
		
		return Arrays.asList(knowTypes.split(",")).contains(converterType) ? converterType : null;
	}

	private static String getCode(CustomAttribute customAttribute) {
		if (!customAttribute.getCode().startsWith("code:") ) {
			return "layoutParams." + customAttribute.getCode()  + " (" + customAttribute.getJavaType() + ") objValue;";
		}
		return customAttribute.getCode().replace("code:", "").replace("$var", "layoutParams");
	}

	private static CustomAttribute getCustomAttribute(String name, Set<CustomAttribute> customAttributes) {
		for (CustomAttribute customAttribute : customAttributes) {
			if (customAttribute.getName().equals(name)) {
				return customAttribute;
			}
		}
		return null;
	}

	private static String getEnumCode(Resources.Attr enumAttr, List<String> methodNames) {
		List<java.io.Serializable> contents = enumAttr.getContent();
		// enum + int
		boolean isInt =  isEnumAllowsInt(enumAttr);
		
		String methodName = "private int " + getGetter(enumAttr.getName()) + "(String value" + (isInt ? ", IWidget w" : "" ) + ") ";
		if (methodNames.contains(methodName)) {
			return "";
		}
		methodNames.add(methodName);
		String enumcode = "\n" + methodName + "{\r\n"
				+ "	switch (value) {\r\n";
		
		for (int i = 0; i < contents.size(); i++) {
			if (contents.get(i) instanceof javax.xml.bind.JAXBElement) {
				javax.xml.bind.JAXBElement element = (javax.xml.bind.JAXBElement) contents.get(i);
				Enum enum1 = (com.ashera.codegen.pojo.attrs.Resources.Attr.Enum) element.getValue();
				System.out.println(enum1.getValueAttribute() + " " + enum1.getName());
				
						enumcode +=  "case \"" + enum1.getName() + "\":\r\n"
						+ "		return " + enum1.getValueAttribute()
						+ ";\r\n";
						
			}
		}
		
		enumcode += "default:\r\n"
				+ "		break;\r\n"
				+ "}\r\n"
				+ "	return " + (isInt ? "(int) w.quickConvert(value, \"int\")" : "0") 
				+ ";\r\n"
				+ "}\n";
		return enumcode;
	}

	private static boolean isEnumAllowsInt(Resources.Attr enumAttr) {
		return "integer".equals(enumAttr.getFormat());
	}

	private static String getType(String motionLayoutStr, String name) {
		String regex = ".*withName\\(\"" + name + "\"\\)\\.withType\\(\"(.*)\"\\).*";
		Pattern p = Pattern.compile(regex, Pattern.MULTILINE);
		Matcher m = p.matcher(motionLayoutStr);
		String type = null;
		if (m.find( )) {
			type = m.group(1);
		}
		return type;
	}
	
	static class FieldInfoHolder  {
		HashMap<String, String> fieldMap = new HashMap<>();
		HashMap<String, String> fieldParamMap = new HashMap<>();
		HashMap<String, String> methodParamMap = new HashMap<>();
		HashMap<String, String> paramClassMap = new HashMap<>();
	}

	private static FieldInfoHolder getFieldInfo(XmlConfig xmlConfig) throws IOException {
		FieldInfoHolder holder = new FieldInfoHolder();
		/*String[] params = {				
				"../../core-javafx-widget/SWTAndroidXConstraintLayout/src/main/java/androidx/constraintlayout/widget/ConstraintSet.java#androidx.constraintlayout.widget.ConstraintSet.PropertySet#propertySet",
				"../../core-javafx-widget/SWTAndroid/src/main/java/r/android/view/ViewGroup.java#androidx.constraintlayout.widget.Constraints.LayoutParams#layoutParams",
				"../../core-javafx-widget/SWTAndroidXConstraintLayout/src/main/java/androidx/constraintlayout/widget/ConstraintLayout.java#androidx.constraintlayout.widget.Constraints.LayoutParams#layoutParams",
				"../../core-javafx-widget/SWTAndroidXConstraintLayout/src/main/java/androidx/constraintlayout/widget/Constraints.java#androidx.constraintlayout.widget.Constraints.LayoutParams#layoutParams",
				"../../core-javafx-widget/SWTAndroidXConstraintLayout/src/main/java/androidx/constraintlayout/widget/ConstraintSet.java#androidx.constraintlayout.widget.ConstraintSet.Motion#motion",
				"../../core-javafx-widget/SWTAndroidXConstraintLayout/src/main/java/androidx/constraintlayout/widget/Barrier.java#androidx.constraintlayout.widget.Barrier#barrier"};*/
		for (com.ashera.codegen.pojo.XmlConfigParam param : xmlConfig.getXmlConfigParams()) {
			if (!param.getType().equals("java")) {
				continue;
			}
			String paramFile = param.getLocation();
			String paramClass = param.getClassName();
			String paramName = param.getParamName();
			holder.paramClassMap.put(paramName, paramClass);
			
			CompilationUnit c = getCU(new File(paramFile));
			
			c.accept(new ASTVisitor() {
				@Override
				public boolean visit(org.eclipse.jdt.core.dom.FieldDeclaration node) {
					if (param.isTopLevelClass()) {
						populateHolder(node, holder, paramName);
					}
					return super.visit(node);
				}
				
				@Override
				public boolean visit(org.eclipse.jdt.core.dom.MethodDeclaration node) {
					String name = node.getName().toString();
					
					if (name.startsWith("set") && node.parameters().size() == 1) {
						String type = ((org.eclipse.jdt.core.dom.SingleVariableDeclaration) node.parameters().get(0)).getType().toString();
						holder.methodParamMap.put(name, type);
						holder.fieldParamMap.put(name, paramName);
					}
					
					return super.visit(node);
				}
				
				@Override
				public boolean visit(TypeDeclaration typeDeclarationStatement) {
					
				    if (!typeDeclarationStatement.isPackageMemberTypeDeclaration()) {
				    	if (paramClass.endsWith("." + typeDeclarationStatement.getName())) {
				            FieldDeclaration[] fields = typeDeclarationStatement.getFields();
				            
				            for (FieldDeclaration field : fields) {
								populateHolder(field, holder, paramName);
							}
				    	}
				            // Get more details from the type declaration.
				    }

				    return true;
				}
				private void populateHolder(FieldDeclaration field, FieldInfoHolder holder, String paramName) {
					List fragments = field.fragments();
					if (fragments != null && fragments.size() > 0) {
						Object firstFragment = fragments.get(0);
						if (firstFragment instanceof VariableDeclarationFragment) {
							String name = ((VariableDeclarationFragment) firstFragment).getName().toString();
							String type = field.getType().toString();
							holder.fieldMap.put(name, type);
							holder.fieldParamMap.put(name, paramName);
						}
					}
				}
			});
		}
		return holder;
	}

	private static String getSetter(String name) {
		return "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
	}
	
	private static String getClassName(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
	
	private static String getGetter(String name) {
		return "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
	}
	
	private static String getClassVarName(String name) {
		return "m" + name.substring(0, 1).toUpperCase() + name.substring(1);
	}
	
	private static String getSetterName(String name) {
		return "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	private static String getVarName(String tagName) {
		return tagName.substring(0, 1).toLowerCase() + tagName.substring(1);
	}

	private static String getName(Resources.Attr attr) {
		return attr.getName().replaceAll("android\\:", "");
	}
	
	private static String getConstraintName(String name) {
		name = name.replace("constraint", "");
		if (name.startsWith("_") ) {
			name = name.substring(1);
		}
		if (name.contains("_")) {
			name = Arrays.stream(name.split("_")).map((x) -> x.substring(0, 1).toUpperCase() + x.substring(1)).collect(java.util.stream.Collectors.joining(""));
		}
		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}

	private static boolean isFlag(com.ashera.codegen.pojo.attrs.Resources.Attr attr) {
		if (attr.getContent() != null && attr.getContent().size() > 0) {
			for (int i = 0; i < attr.getContent().size(); i++) {
				if (attr.getContent().get(i) instanceof javax.xml.bind.JAXBElement) {
					javax.xml.bind.JAXBElement element = (javax.xml.bind.JAXBElement) attr.getContent().get(i);

					if (element.getValue() instanceof com.ashera.codegen.pojo.attrs.Resources.Attr.Flag) {
						return true;
					}
				}
			}
		}

		return false;
	}
	
	private static boolean isEnum(com.ashera.codegen.pojo.attrs.Resources.Attr attr) {
		if (attr.getContent() != null && attr.getContent().size() > 0) {
			for (int i = 0; i < attr.getContent().size(); i++) {
				if (attr.getContent().get(i) instanceof javax.xml.bind.JAXBElement) {
					javax.xml.bind.JAXBElement element = (javax.xml.bind.JAXBElement) attr.getContent().get(i);

					if (element.getValue() instanceof com.ashera.codegen.pojo.attrs.Resources.Attr.Enum) {
						return true;
					}
				}
			}
		}

		return false;
	}
}
