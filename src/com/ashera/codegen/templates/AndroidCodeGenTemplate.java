package com.ashera.codegen.templates;

import static com.ashera.codegen.CodeGenHelper.getApiInt;
import static com.ashera.codegen.CodeGenHelper.getAttributeNameFromChangeListener;
import static com.ashera.codegen.CodeGenHelper.getAttributeWithoutNameSpace;
import static com.ashera.codegen.CodeGenHelper.getMethodName;
import static com.ashera.codegen.CodeGenHelper.getMethodParams;
import static com.ashera.codegen.CodeGenHelper.getNameSpace;
import static com.ashera.codegen.CodeGenHelper.getSetMethodFromAttr;
import static com.ashera.codegen.CodeGenHelper.isAndroidAttribute;
import static com.ashera.codegen.CodeGenHelper.isApiElement;
import static com.ashera.codegen.CodeGenHelper.isNotSupportedApiLevel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ashera.codegen.CodeGenHelper;
import com.ashera.codegen.pojo.CustomAttribute;
import com.ashera.codegen.pojo.Generator;
import com.ashera.codegen.pojo.QuirkReportDto;
import com.ashera.codegen.pojo.Url;
import com.ashera.codegen.pojo.Widget;

public class AndroidCodeGenTemplate extends CodeGenTemplate {
	private static CustomTransformer customTransformer = new CustomTransformer();
	private final String codeBaseDir;
	public AndroidCodeGenTemplate(QuirkReportDto quirkReportDto, String packageName, String environment, String prefix, String codeBaseDir, String testDir) {
		super(quirkReportDto, testDir, packageName, environment, prefix);
		this.codeBaseDir = codeBaseDir;
	}

	@Override
	public void addMethodDefinitions(Generator generator, Widget widget, CustomAttribute customAttribute, Properties widgetProperties) {
		if (customAttribute.methodDef != null) {
			widget.addMethodDefition(customAttribute.methodDef);
		}
	}

	@Override
	public String getFileName(String url) {
	    url = url.replace("/src/main/res/values/attrs.xml?format=TEXT", "");
		return "AD" + url.substring(url.lastIndexOf("/") + 1);
	}

	@Override
	public void setSetterMethod(Widget widget, CustomAttribute customAttribute, Properties widgetProperties,
			String url) {
		String widgetPackage = url.substring(url.lastIndexOf("/") + 1).replaceFirst("\\.html", "");
		// System.out.println(widgetPackage);
		if (widgetProperties.containsKey(widgetPackage + ".var")) {
			String varName = widgetProperties.getProperty(widgetPackage + ".var", "");
			String setterMethod = customAttribute.getCode().substring(0, customAttribute.getCode().indexOf("("));
			customAttribute.setCode(
					"code:" + varName + "." + setterMethod + "((" + customAttribute.getJavaType() + ") objValue);");
		} else {
//			customAttribute.setCode(element.getCode());
		}

		if (customAttribute.getGetterCode() != null) {
			if (customAttribute.getGetterCode().startsWith("code")) {
//				classConfiguration.setGetterMethod(element.getGetterCode());
			} else {
				customAttribute.setGetterCode(customAttribute.getGetterCode() + "()");
			}
		}

	}

	@Override
	public List<CustomAttribute> getNodeElements(String generatorUrl, Document doc, Widget widget, Properties widgetProperties) {
		Elements elements = doc.select("h3");
		List<CustomAttribute> nodeElements = new ArrayList<>();
		
		for (int i = 0; i < elements.size(); i++) {
			Element element = elements.get(i);
			String text = element.text();

			if (isAndroidAttribute(text)) {
				String namespace = getNameSpace(text);
				String attributeWithoutNameSpace = getAttributeWithoutNameSpace(text);
				boolean useCompat = false;
				boolean useGetCompat = false;
				boolean seperatedByBar = false;
				Element nextSibLing = element.nextElementSibling();
				String methodSignature = "";
				String converterKeys1 = "";
				String converterKeys2 = "";
				String apiLevel = "1";
				boolean supportIntegerAlso = false;
				while (!nextSibLing.tagName().equals("h3")) {
					String text2 = nextSibLing.text();
					
					if (attributeWithoutNameSpace.equals("marqueeRepeatLimit")) {
						if (text2.indexOf("May be an integer value") != -1) {
							supportIntegerAlso = true;
						}
					}
					if (text2.indexOf("|") != -1) {
						seperatedByBar = true;
					}

					if (nextSibLing.tagName().equals("table")) {
						Elements table = nextSibLing.select("tr > td:eq(0)");
						for (Element tds : table) {
							converterKeys1 += tds.text() + ",";
							converterKeys2 += tds.nextElementSibling().text() + ",";
						}
					}
					nextSibLing = nextSibLing.nextElementSibling();

					if (nextSibLing == null) {
						break;
					}
					if (nextSibLing.previousElementSibling().text().equals("Related methods:")) {
						methodSignature = nextSibLing.text();
						String methodName = getMethodName(methodSignature);

						String[] methodNameAndApi = getMethodNameAndApiLevel(doc, methodName);
						if (methodNameAndApi != null) {
							apiLevel = methodNameAndApi[0];
							if (methodNameAndApi[1].equals("skip")) {
								methodSignature = methodNameAndApi[1];
							}

							Elements methodElementsCompat = doc
									.select("h3:contains(" + widget.getName() + "Compat." + methodName + ")");
							if (methodElementsCompat.size() > 0) {
								for (Element element2 : methodElementsCompat) {
									if (element2.text().equals(widget.getName() + "Compat." + methodName)) {
										useCompat = true;
										break;
									}
								}
							}
							break;
						}

					}
				}

				if (methodSignature.equals("")) {
					if (attributeWithoutNameSpace.startsWith("layout_")) {
						String publicSttrName =  attributeWithoutNameSpace.substring("layout_".length());
						String[] methodNameAndApi = getMethodNameAndApiLevelForLayoutAttr(doc, publicSttrName);
						if (methodNameAndApi != null) {
							apiLevel = methodNameAndApi[0];
							methodSignature = methodNameAndApi[1];
						}
						
					} else {					
						// try direct method name
						String[] methodNameAndApi = getMethodNameAndApiLevel(doc,
								getSetMethodFromAttr(attributeWithoutNameSpace));
						if (methodNameAndApi != null) {
							apiLevel = methodNameAndApi[0];
							methodSignature = methodNameAndApi[1];
						}
					}
				}

				String setMethodHint = widgetProperties.getProperty(attributeWithoutNameSpace + ".setMethodHint");

				if (setMethodHint != null) {
					methodSignature = setMethodHint;
				}

				if (attributeWithoutNameSpace.startsWith("layout_")) {
					
					String[] method = methodSignature.split(" ");
					if (method.length == 3 && method[0].equals("public")) {
						CustomAttribute nodeElement = new CustomAttribute();
						nodeElement.setNamespace(namespace);
						nodeElement.setGeneratorUrl(generatorUrl);
						nodeElement.setConverterInfo1(converterKeys1);
						nodeElement.setName(attributeWithoutNameSpace);
						nodeElement.setConverterInfo2(converterKeys2);
						nodeElement.setCode(method[2] + " = ");
						nodeElement.setGetterCode("code:$var." + method[2]);
						nodeElement.setVarType(method[1]);
						nodeElement.setApiLevel(apiLevel);
						nodeElement.setApiLevelForGet(apiLevel);
						nodeElements.add(nodeElement);
						continue;
					}
				}  
				if (!methodSignature.equals("skip") && methodSignature.indexOf("(") != -1
						&& methodSignature.indexOf(")") != -1) {
					String[] methodsParams = getMethodParams(methodSignature);
					String paramhint = widgetProperties.getProperty(attributeWithoutNameSpace + ".paramhint");
					String wrapMethod = widgetProperties.getProperty(attributeWithoutNameSpace + ".wrapMethod");

					if (methodsParams.length == 1 || paramhint != null) {
						CustomAttribute nodeElement = new CustomAttribute();
						nodeElement.setGeneratorUrl(generatorUrl);
						nodeElement.setConverterInfo1(converterKeys1);
						nodeElement.setConverterInfo2(converterKeys2);
						nodeElement.setSupportIntegerAlso(supportIntegerAlso);
						nodeElement.setNamespace(namespace);
						if (methodsParams.length == 1) {
							nodeElement.setVarType(methodsParams[0]);
						} else {
							// check the order and decide the var type
							nodeElement.setVarType(methodsParams[Arrays.asList(paramhint.split(","))
									.indexOf(attributeWithoutNameSpace)]);
						}
						nodeElement.setName(attributeWithoutNameSpace);
						nodeElement.setApiLevel(apiLevel);
						String methodName = getMethodName(methodSignature);
						if (methodsParams.length == 1) {
							if (!useCompat) {
								nodeElement.setCode(methodName);
							} else {
								nodeElement.setApiLevel("1");
								nodeElement.setCode("code:" + widget.getName() + "Compat." + methodName
										+ "($var, " + "(" + nodeElement.getVarType() + ") objValue);");
								nodeElement.setSetterMethodNoCompat(methodName);
							}
						} else {
							if (useCompat) {
								nodeElement.setApiLevel("1");
								nodeElement.setCode("code:" + widget.getName() + "Compat." + methodName
										+ "($var, ");
								String seperator = "";

								int paramHintCounter = 0;
								for (String paramhintStr : paramhint.split(",")) {
									nodeElement.setCode(nodeElement.getCode() + seperator);

									if (wrapMethod != null && wrapMethod.split(",").length > paramHintCounter) {
										nodeElement.setCode(nodeElement.getCode() + wrapMethod.split(",")[paramHintCounter] + "(");
									}
									if (paramhintStr.startsWith("constant:")) {
										nodeElement.setCode(nodeElement.getCode() + paramhintStr.replaceAll("constant\\:", ""));
									} else if (paramhintStr.equals(attributeWithoutNameSpace)) {
										nodeElement.setCode(nodeElement.getCode() + "(" + nodeElement.getVarType() + ") objValue");
									} else {
										nodeElement.setCode(nodeElement.getCode() + widget.getName() + "Compat.get"
												+ paramhintStr.substring(0, 1).toUpperCase() + paramhintStr.substring(1)
												+ "($var)");
									}
									if (wrapMethod != null && wrapMethod.split(",").length > paramHintCounter) {
										nodeElement.setCode(nodeElement.getCode() + ")");
									}

									seperator = ",";
									paramHintCounter++;
								}
								nodeElement.setCode(nodeElement.getCode() + ");");
							} else {
								nodeElement.setCode("code:$var." + methodName + "(");
								String seperator = "";
								for (String paramhintStr : paramhint.split(",")) {
									nodeElement.setCode(nodeElement.getCode() + seperator);
									if (paramhintStr.startsWith("constant:")) {
										nodeElement.setCode(nodeElement.getCode() + paramhintStr.replaceAll("constant\\:", ""));
									} else if (paramhintStr.equals(attributeWithoutNameSpace)) {
										nodeElement.setCode(nodeElement.getCode() + "(" + nodeElement.getVarType() + ") objValue");
									} else {
										nodeElement.setCode(nodeElement.getCode() + "$var.get"
												+ paramhintStr.substring(0, 1).toUpperCase() + paramhintStr.substring(1)
												+ "()");
									}

									seperator = ",";
								}
								nodeElement.setCode(nodeElement.getCode() + ");");
							}
						}
						nodeElement.setSeperatedByBar(seperatedByBar);

						Elements methodElements = doc.select("a:contains(" + text + ")");
						// try one using linked xml attribute
						for (Element methodElement : methodElements) {
							Element previousElementSibling = methodElement.parent().parent().previousElementSibling();
							if (previousElementSibling != null) {
								String relatedXmlAttr = previousElementSibling.text();
								if (relatedXmlAttr.equals("Related XML Attributes:")
										&& methodElement.text().equals(text)) {
									String getterMethod = previousElementSibling.firstElementSibling().text();
									if ((getterMethod.indexOf("is") != -1 || getterMethod.indexOf("get") != -1) && getterMethod.toLowerCase()
											.indexOf(attributeWithoutNameSpace.toLowerCase()) != -1) {
										Elements methodElementsCompat = doc
												.select("h3:contains(" + widget.getName() + "Compat." + getterMethod + ")");
										for (Element element2 : methodElementsCompat) {
											if (element2.text().equals(widget.getName() + "Compat." + getterMethod)) {
												useGetCompat = true;
												break;
											}
										}

										if (useGetCompat) {
											nodeElement.setApiLevelForGet("1");
											nodeElement.setGetterCode("code:" + widget.getName() + "Compat."
													+ getterMethod + "($var)");
										} else {
											nodeElement.setGetterCode(getterMethod);
										}
										break;
									}
								}
							}
						}
						String apiLevelForGet = apiLevel;
						if (nodeElement.getGetterCode() == null && (nodeElement.getCode().startsWith("set") || nodeElement.getSetterMethodNoCompat().startsWith("set"))) {
							String nSetterMethod = nodeElement.getCode().startsWith("set") ? nodeElement.getCode() : nodeElement.getSetterMethodNoCompat();
							int index = nSetterMethod.length();
							if (nSetterMethod.indexOf("(") != -1) {
								index = nSetterMethod.indexOf("(");
							}

							String getterMethod = "g" + nSetterMethod.substring(1, index);
							apiLevelForGet = setGetterMethodOnNode(doc, nodeElement, getterMethod);

							if (nodeElement.getGetterCode() == null && nodeElement.getVarType().equals("boolean")) {
								getterMethod = "is" + nSetterMethod.substring(3, index);
								apiLevelForGet = setGetterMethodOnNode(doc, nodeElement, getterMethod);
							}
						}
						
						if (!useGetCompat) {
							nodeElement.setApiLevelForGet(getApiInt(apiLevelForGet));
						}


						String getMethodHint = widgetProperties
								.getProperty(attributeWithoutNameSpace + ".getMethodHint");

						if (getMethodHint != null) {
							nodeElement.setGetterCode(getMethodHint);
						}
						if (nodeElement.getGetterCode() == null) {
							System.out.println("Getter method not found (0) : " + text + " " + apiLevelForGet);
						}

						CustomAttribute customAttribute = widget.getCustomAttribute(attributeWithoutNameSpace);
						if (customAttribute != null) {
							customAttribute.nodeElement = nodeElement;
						} else {
							nodeElements.add(nodeElement);
						}
					} else {
						CustomAttribute customAttribute = widget.getCustomAttribute(attributeWithoutNameSpace);
						if (customAttribute == null) {
		                    ignoredAttributes.add(text);
							System.out.println("Ignored (0) : " + text);
						} else {
							CustomAttribute nodeElement = new CustomAttribute();
							nodeElement.setNamespace(namespace);
							nodeElement.setGeneratorUrl(generatorUrl);

							customAttribute.nodeElement = nodeElement;
							
						}
					}
				} else {
					CustomAttribute customAttribute = widget.getCustomAttribute(attributeWithoutNameSpace);
					if (customAttribute != null) {
						CustomAttribute nodeElement = new CustomAttribute();
						nodeElement.setNamespace(namespace);
						nodeElement.setGeneratorUrl(generatorUrl);
						customAttribute.nodeElement = nodeElement;
					}
					if (customAttribute == null) {
						if (!customTransformer.handle(nodeElements, attributeWithoutNameSpace, widget, generatorUrl, namespace)) {
						    ignoredAttributes.add(text);
							System.out.println("Ignored (-1) : " + text + " " + apiLevel);
						}
					}
				}

			} else {
				if (text.startsWith("setOn") || (text.startsWith("addText") && text.endsWith("Listener"))) {
					handleSetonMethods(text, generatorUrl, element, nodeElements, widget);
				}
				// System.out.println("Ignored (-2) : " + text);
			}
		}
		
		// save the node elements to file
		return nodeElements;
	}


	private void handleSetonMethods(String setOnText, String generatorUrl, Element element, List<CustomAttribute> nodeElements, Widget widget) {
		try {
			Element apiLevelSibmling = element.nextElementSibling();
			String apiLevelElement = element.nextElementSibling().text();
			Element methodSigElement = apiLevelSibmling.nextElementSibling();
			String methodSignature = methodSigElement.text();
			Element hrefElement = methodSigElement.getElementsByTag("a").get(0).getElementsByAttribute("href").get(0);
			String url = hrefElement.attr("href");
			
			if (!url.startsWith("http")) {
				url = "https://developer.android.com" + url;
			}

			String fileCache = getFileName(url);
			Document doc = getDocument(url, fileCache, new Url[0]);
			Elements elements = doc.select("h3");
			java.util.List<String> classNames = new java.util.ArrayList<>();
			for (int i = 0; i < elements.size(); i++) {
				Element methodelement = elements.get(i);
				String text = methodelement.text();

				if (text.startsWith("on") || text.startsWith("before") || text.startsWith("after")) {
					Element setOnMethodSigElement = methodelement.nextElementSibling().nextElementSibling();

					String methodSignatureListener = setOnMethodSigElement.text();
					String methodname = CodeGenHelper.getMethodName(methodSignatureListener);
					CustomAttribute nodeElement = new CustomAttribute();
					if (!CodeGenHelper.isNotSupportedApiLevel(apiLevelElement)) {
						nodeElement.setApiLevel(CodeGenHelper.getApiInt(apiLevelElement));
						nodeElement.setName(getAttributeNameFromChangeListener(methodname));
						if (!nodeElement.getName().startsWith("on")) {
							nodeElement.setName("on" + nodeElement.getName());
						}
						nodeElement.setVarType("String");
						nodeElement.setConverterInfo1("");
						nodeElement.setConverterInfo2("");
						nodeElement.setGeneratorUrl(generatorUrl);
						String methodName2 = CodeGenHelper.getMethodName(methodSignature);
						String className = methodName2.substring(3);

						String localVar = "this";
						String action = "";
						String removeListenerIfNeeded = "";
						if (setOnText.startsWith("addText") && setOnText.endsWith("Listener")) {
							action = ", \""  + nodeElement.getName() + "\"";
							removeListenerIfNeeded = String.format("remove%sIfNeeded(%s);", setOnText, action.substring(2));
						}
						if (widget.getTemplate().equals("ViewGroupTemplate.java")
								|| widget.getTemplate().equals("ViewTemplate.java")) {
							localVar = "w";

						}

						nodeElement.setCode(String.format("code:%sif (objValue instanceof String) {$var.%s(new %s(%s, strValue%s));} else {%s($var, objValue);}", removeListenerIfNeeded, methodName2, className, localVar, action, methodName2));

						if (!classNames.contains(className)) {
							String interfaceName = hrefElement.text();
							String methodDef = generateEventClass(nodeElement, methodSignatureListener,
									className, interfaceName, widget);
							nodeElement.methodDef  = methodDef;
						} else {
							CustomAttribute prevNode = nodeElements.get(nodeElements.size() - classNames.size());
							// .methodDef
							String methodDef = generateEventMethodDef(methodSignatureListener, nodeElement, widget);
							prevNode.methodDef = prevNode.methodDef.replaceFirst("#####", "\n" + methodDef);
						}
						classNames.add(className);
						
						CustomAttribute customAttribute = widget.getCustomAttribute(nodeElement.getName());
						if (customAttribute == null) {
							nodeElements.add(nodeElement);
						} else {
							customAttribute.nodeElement = nodeElement;
						}

					} else {
						System.out.println("Ignored Set Method (0) : " + text);
						ignoredAttributes.add(text);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	

	private String[] getMethodNameAndApiLevel(Document doc, String methodStr) {
		Elements methodElements = doc.select("h3:contains(" + methodStr + ")");
		
		if (methodElements.size() > 0) {
			for (Element element2 : methodElements) {
				Element apiLevelSibmling = element2.nextElementSibling();
				String apiLevelElement = element2.nextElementSibling().text();
				String methodSignature = apiLevelSibmling.nextElementSibling().text();
				if (methodSignature.indexOf((methodStr + " (")) != -1
						|| methodSignature.indexOf((methodStr + "Enabled (")) != -1) {

					if (isNotSupportedApiLevel(apiLevelElement)) {
						String methodName = "skip";
						String apiLevel = getApiInt(apiLevelElement);
						return new String[] { apiLevel, methodName };
					}
					if (isApiElement(apiLevelElement)) {
						String apiLevel = getApiInt(apiLevelElement);
						String methodName = apiLevelSibmling.nextElementSibling().text();
						return new String[] { apiLevel, methodName };
					}
				}
			}
		}

		return null;
	}
	

	private String[] getMethodNameAndApiLevelForLayoutAttr(Document doc, String publicSttrName) {
		Elements methodElements = doc.select("h3:contains(" + publicSttrName + ")");
		
		if (methodElements.size() > 0) {
			for (Element element2 : methodElements) {
				Element apiLevelSibmling = element2.nextElementSibling();
				String apiLevelElement = element2.nextElementSibling().text();
				String methodSignature = apiLevelSibmling.nextElementSibling().text();
				if (methodSignature.endsWith(" " + publicSttrName)) {
					String apiLevel = getApiInt(apiLevelElement);
					return new String[] { apiLevel, methodSignature };
				}
			}
		}
		return null;
	}

	private String setGetterMethodOnNode(Document doc, CustomAttribute nodeElement, String getterMethod) {
		String apiLevelElement = "";
		Elements getterMethodElements = doc.select("h3:contains(" + getterMethod + ")");
		if (getterMethodElements.size() > 0) {
			for (Element element2 : getterMethodElements) {
				if (element2.text().equals(getterMethod)) {
					Element apiLevelSibmling = element2.nextElementSibling();
					apiLevelElement = apiLevelSibmling.text();
					if (!CodeGenHelper.isNotSupportedApiLevel(apiLevelElement)) {
						nodeElement.setGetterCode(getterMethod);
					}
					break;
				}
			}
		}

		return apiLevelElement;
	}

	@Override
	public String getPackagePrefix() {
		return "";
	}

	@Override
	public String getJavaFileLocation(Widget configuration) {
		return this.codeBaseDir + "/src/com/ashera/" + packageName + "/" + configuration.getWidgetName() + ".java";
	}

	@Override
	public String getTsFileLocation(Widget configuration) {
		return this.codeBaseDir + "/tsc/src/" + environment + "/widget/" + configuration.getWidgetName() + ".ts";
	}

	@Override
	public String getAttrFileLocation(Widget configuration) {
		return this.codeBaseDir +  "/res/values/" + configuration.getLocalNameWithoutPackage().toLowerCase()  + "_attrs.xml";
	}
}