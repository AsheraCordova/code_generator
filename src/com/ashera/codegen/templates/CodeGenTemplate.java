package com.ashera.codegen.templates;

import static com.ashera.codegen.CodeGenHelper.getAttributeWithoutNameSpace;
import static com.ashera.codegen.CodeGenHelper.getMethodParams;
import static com.ashera.codegen.CodeGenHelper.getMethodReturnType;
import static com.ashera.codegen.CodeGenHelper.getMethodVars;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.ashera.codegen.ClassConfigurations;
import com.ashera.codegen.CodeGenBase;
import com.ashera.codegen.pojo.Code;
import com.ashera.codegen.pojo.Copy;
import com.ashera.codegen.pojo.CustomAttribute;
import com.ashera.codegen.pojo.Generator;
import com.ashera.codegen.pojo.MethodParam;
import com.ashera.codegen.pojo.QuirkAttribute;
import com.ashera.codegen.pojo.QuirkReportDto;
import com.ashera.codegen.pojo.QuirkWidget;
import com.ashera.codegen.pojo.ReplaceString;
import com.ashera.codegen.pojo.Url;
import com.ashera.codegen.pojo.Widget;
import com.ashera.model.TestCase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public abstract class CodeGenTemplate extends CodeGenBase{
	String environment;
	private ArrayList<String> processedAttributes = new ArrayList<>();
	private ClassConfigurations classConfigurations = new ClassConfigurations();
	private static Map<String, List<Widget>> processedWidgets = new HashMap<>();
    private static ArrayList<String> activities = new ArrayList<>();
    private static ArrayList<String> layoutFiles = new ArrayList<>();
    private String prefix;
    protected List<String> ignoredAttributes = new ArrayList<>();
    
    private QuirkReportDto quirkReportDto = new QuirkReportDto();
	protected final String packageName;
	protected String events = "";
	private String testDir;
    
    private static Map<String, Widget> processedClassConfigurations = new HashMap<>();
//    private TestCase testcase;

    public void resetActivities() {
        CodeGenTemplate.activities = new ArrayList<>();
        CodeGenTemplate.layoutFiles = new ArrayList<>();
    }

    public CodeGenTemplate(QuirkReportDto quirkReportDto, String testDir, String packageName, String enviroment, String prefix) {
		this.environment = enviroment;
		this.prefix = prefix;
		this.quirkReportDto = quirkReportDto;
		this.packageName = packageName;
		this.testDir = testDir;

	}
	
	public void startCodeGeneration(Widget widget) throws Exception{
		Properties widgetProperties = new Properties();
		widgetProperties.load(new FileInputStream("configimpl/common.properties"));
		List<String> knowTypes = new ArrayList<>(Arrays.asList(widgetProperties.getProperty("knowTypes").split(",")));
		if (widgetProperties.containsKey(environment + ".knowTypes")) {
			knowTypes.addAll(Arrays.asList(widgetProperties.getProperty(environment + ".knowTypes").split(",")));
		}

		String prefixForAttributes = prefix.equals("") ? "" : prefix + "_";
		

		Generator[] generators = widget.getGenerator();
		StringBuffer extraCode = new StringBuffer("");

		if (generators != null) {
			for (Generator generator : generators) {
				Properties viewProperties = new Properties();
				File file = new File("configimpl/" + generator.getPropertyfile());
				
				if (file.exists()) {
					viewProperties.load(new FileInputStream(file));
					for (Map.Entry<Object, Object> keyStr : viewProperties.entrySet()) {
						if (widgetProperties.containsKey(keyStr.getKey())) {
							widgetProperties.put(keyStr.getKey().toString(), widgetProperties.get(keyStr.getKey()).toString() + "," +keyStr.getValue().toString());
						} else {
							widgetProperties.put(keyStr.getKey().toString(), keyStr.getValue().toString());
						}
					}
				}
				
				for (int i = 0; i < 50; i++) {
					String copyFileFromUrl = widgetProperties.getProperty(generator.getType() + ".dependent.files." + i);
					if (copyFileFromUrl != null) {
						String httpUrl =  copyFileFromUrl.split("##")[0];
						String fileToCopy = copyFileFromUrl.split("##")[1];
						readHttpUrlAsString(httpUrl, fileToCopy, "", false);
					} else {
						break;
					}
				}
			}
			String widgetName = widget.getClassname();
	        List<CustomAttribute> nodeElements = new ArrayList<>();
	        
			for (Generator generator : generators) {
				if (generator.getUrl() == null) {
					continue;
				}
				String url = generator.getUrl();
				//String key = widget;
				//System.out.println("configimpl/" + generator.getPropertyfile());
				String ignoreMe = widgetProperties.getProperty("ignoreList." + environment);
				String[] ignoreMeArr = new String[0];
				if (ignoreMe != null) {
					ignoreMeArr = ignoreMe.split(",");
				}
				List<String> ignoreList = new ArrayList<>(Arrays.asList(ignoreMeArr));
				
				String ignoreforClassNameList = widgetProperties.getProperty(widgetName + ".ignoreList." + environment);
				if (ignoreforClassNameList != null && !ignoreforClassNameList.equals("")) {
					ignoreList.addAll(Arrays.asList(ignoreforClassNameList.split(",")));
				}
				
				String fileCache = getFileName(url);
				Document doc = null;
				
				if (generator.getType().equals("docurl") ) {
					doc = getDocument(url, fileCache, generator.getParentUrls());
				}
				
				if (generator.getType().equals("attrxml") ) {
					Url[] urls = generator.getParentUrls();
					
					handleCustomAttribute(generator, widget, generateAttributesFromAttrsXml(widget, knowTypes, generator, getContentOfUrls(null, urls), widgetProperties, urls), classConfigurations, widgetProperties);
				}
				
				List<CustomAttribute> elements = new ArrayList<>();
				if (!widget.isOnlyChildWidgets()) {
					if (generator.getType().equals("docurl") ) {
						elements = getNodeElements(generator.getUrl(), doc, widget, widgetProperties);
					}
					if (generator.getType().equals("java") ) {
						elements = getNodeElementsForJava(generator, widget, widgetProperties);
					}
				    nodeElements.addAll(elements);
				    if (widget.getCustomAttribute() != null) {
					    for (CustomAttribute customAttribute :widget.getCustomAttribute() ) {
					        if (customAttribute.nodeElement != null) {
					            nodeElements.add(customAttribute.nodeElement);
					        }
					    }
				    }
				}

				for (int i = 0; i < elements.size(); i++) {
					CustomAttribute customAttribute = elements.get(i);
					customAttribute.setWidgetProperties(widgetProperties);
					
					String readOnly = widgetProperties.getProperty(customAttribute.getName() + ".readOnly");
					String updateUiFlag = widgetProperties.getProperty(customAttribute.getName() + "." + environment + ".updateUiFlag");
					if (updateUiFlag != null) {
						customAttribute.setUpdateUiFlag(updateUiFlag);
					}
					
					String bufferStrategy = widgetProperties.getProperty(customAttribute.getName() + "." + environment + ".bufferStrategy");
					if (bufferStrategy != null) {
						customAttribute.setBufferStrategy(bufferStrategy);
					}
					if (readOnly != null) {
						customAttribute.setReadOnly(readOnly);
					}
					
					if (readOnly != null) {
						customAttribute.setReadOnly(readOnly);
					}
					
					if (widgetProperties.getProperty(customAttribute.getName() + ".noGetterMethod") != null) {
						customAttribute.setGetterCode(null);						
					}
					
					String attrbuteNameWithoutNameSpace = customAttribute.getName();
					String attributeName = environment + ":" + customAttribute.getName();
					String varType = customAttribute.getVarType();

					if (widgetProperties.get(attrbuteNameWithoutNameSpace + ".applyBeforeChildAdd") != null) {
						customAttribute.setApplyBeforeChildAdd(true);
					}

					if (varType.equalsIgnoreCase("int")) {
						//System.out.println("Int");
					}
					
					String javaType = customAttribute.getJavaType() == null ? customAttribute.getVarType() : customAttribute.getJavaType();

					if (widgetProperties.containsKey(environment + ".datatype." + varType)) {
						String[] property = widgetProperties.getProperty(environment + ".datatype." + varType).split(":");
						customAttribute.setNativeClassType(property[1]);
						varType = property[0];
						javaType = property[1];
						if (property.length >  2) {
							customAttribute.setNativeClassTypeForGetter(property[2]);
						}
					}

					if (widgetProperties.containsKey(environment + ".order." + customAttribute.getName())) {
						customAttribute.setOrder(widgetProperties.getProperty(environment + ".order." + customAttribute.getName()));
					}
					if (widgetProperties.containsKey(environment + ".props")) {
						String[] properties = widgetProperties.getProperty(environment + ".props").split(",");
						for (String property : properties) {
							if (customAttribute.getName().toLowerCase().indexOf(property.toLowerCase()) != -1) {
								if (widgetProperties.containsKey(environment + ".datatype." + varType + "." + property)) {
									String[] vartypes = widgetProperties.getProperty(environment + ".datatype." + varType + "." + property).split(":");
									customAttribute.setNativeClassType(vartypes[1]);
									varType = vartypes[0];	
									javaType = vartypes[1];
									if (vartypes.length >  2) {
										customAttribute.setNativeClassTypeForGetter(vartypes[2]);
									}
								}
								break;
							}
						}
					}						


					String apiLevel = customAttribute.getApiLevel();
					String apiLevelForGet = customAttribute.getApiLevelForGet();

					if (apiLevelForGet != null && !apiLevelForGet.isEmpty() && Integer.parseInt(apiLevelForGet) > 8) {
						customAttribute.setAndroidMinVersionForGet(apiLevelForGet);
					}
					if (widgetProperties.containsKey(prefixForAttributes + attrbuteNameWithoutNameSpace + ".alias")) {
						customAttribute.setAliases(widgetProperties.getProperty(prefixForAttributes + attrbuteNameWithoutNameSpace + ".alias").split(","));
					}
					if (!apiLevel.equals("") && Integer.parseInt(apiLevel) > 8) {
						customAttribute.setAndroidMinVersion(apiLevel);
					}
					
					if (!ignoreList.contains(attrbuteNameWithoutNameSpace)) {
						customAttribute.setLayoutAttr(attrbuteNameWithoutNameSpace.startsWith("layout_") ? "yes" : "no");

						if (widget.getLocalName() == null) {
							throw new RuntimeException();
						}
						String setterMethod = getSetterMethodFromAttribute(attrbuteNameWithoutNameSpace);

						if (processedAttributes.contains(setterMethod)) {
							continue;
						}
						
						processedAttributes.add(setterMethod);
												
						if (knowTypes.contains(varType)) {
							String keyPrefixForConverter = widget.getPrefix();

							String converterKeys1 = customAttribute.getConverterInfo1();
							String converterKeys2 = customAttribute.getConverterInfo2();
							boolean seperatedByBar = customAttribute.isSeperatedByBar();
							
							if (widgetProperties.containsKey("enum." + customAttribute.getVarType())) {
								handleStringToEnumConversion(widgetProperties, customAttribute, customAttribute);
								String seperator = ".";
								if (keyPrefixForConverter.equals("")) {
									seperator = "";
								}
								customAttribute.setType(keyPrefixForConverter + seperator +attrbuteNameWithoutNameSpace);
								widget.addClassAttribute(customAttribute);
							
							} else if (widgetProperties.containsKey(attrbuteNameWithoutNameSpace + ".converter")) {
								customAttribute.setJavaType(varType);
								customAttribute.setType(widgetProperties.getProperty(attrbuteNameWithoutNameSpace + ".converter"));
								widget.addClassAttribute(customAttribute);
							} else if (converterKeys1 == null || converterKeys1.equals("")) {
								customAttribute.setJavaType(javaType);
								customAttribute.setType(varType);
								widget.addClassAttribute(customAttribute);
							} else if (varType.equals("int")) {
								customAttribute.setConverterInfo(converterKeys1, converterKeys2, seperatedByBar ? "bitflag" : "enumtoint", customAttribute.isSupportIntegerAlso());
								customAttribute.setJavaType(varType);
								String seperator = ".";
								if (keyPrefixForConverter.equals("")) {
									seperator = "";
								}
								customAttribute.setType(keyPrefixForConverter + seperator +attrbuteNameWithoutNameSpace);
								widget.addClassAttribute(customAttribute);							
							} else {
								System.out.println("ignored (1): " + attributeName + " " + varType);
								ignoredAttributes.add(attributeName);
							}
						} else {
							System.out.println("ignored (2): " + attributeName + " " + varType);
							ignoredAttributes.add(attributeName);
						}

						if (widget.containsCustomAttrbute(customAttribute)) {							
							addMethodDefinitions(generator, widget, customAttribute, widgetProperties);
							if (widgetProperties.containsKey(widget.getLocalName() + "."
									+ environment + ".imports")) {
								widget.setNativeImports(widgetProperties.getProperty(widget.getLocalName() + "."
										+ environment + ".imports").split(","));
							}
							
							if (widgetProperties.containsKey("templateName")) {
								widget.setTemplate(widgetProperties.getProperty("templateName"));
							}
						}
						
						setSetterMethod(widget, customAttribute, widgetProperties, url);
					} else {
						ignoredAttributes.add(attributeName);
						System.out.println("supressed: " + attributeName + " " + varType);
					}
					
					updateFromProtoCustomAttribute(widget, customAttribute, attrbuteNameWithoutNameSpace);
				}	
			}
			
		    File jsonCache = new File("jsoncache/" + environment + widget.getName() + ".json");

		    if (/*!jsonCache.exists() &&*/ widget.getCustomAttribute() != null) {
			    Gson gson = new Gson();					    
	            writeToFile(jsonCache,gson.toJson(nodeElements));
		    }
		}

        if (widget.getCopyAttribute() != null) {
            for (com.ashera.codegen.pojo.CopyAttribute copyAttribute : widget.getCopyAttribute()) {
            	if (!copyAttribute.isOnlyForTestCase()) {
            		System.out.println(copyAttribute.getWidget());
                    Widget classConfiguration = processedClassConfigurations.get((copyAttribute.getOs() == null ? widget.getOs() : copyAttribute.getOs()) + "." + copyAttribute.getWidget());
                    List<CustomAttribute> classConfigurations = classConfiguration.getAllAttributes();
                    
                    for (CustomAttribute attribute : classConfigurations) {
                        if (attribute.getAttribute().matches(copyAttribute.getAttribute())) {
                        	CustomAttribute cloneAttribute = (CustomAttribute) attribute.clone();
                        	addMethodDefFromListenerMethods(widget, cloneAttribute);
                        	if (attribute.getName().startsWith("layout_")) {
                        		widget.getLayoutAttributes().add(cloneAttribute);
                        	}
                        	else {
                        		widget.getWidgetAttributes().add(cloneAttribute);
                        	}
                        	
                        	if (copyAttribute.getUpdateUiFlag() != null && !copyAttribute.getUpdateUiFlag().equals("UPDATE_UI_NONE")) {
                        		cloneAttribute.setUpdateUiFlag(copyAttribute.getUpdateUiFlag() );
                        	}
                        	if (copyAttribute.getBufferStrategy() != null && !copyAttribute.getBufferStrategy().isEmpty()) {
                        		cloneAttribute.setBufferStrategy(copyAttribute.getBufferStrategy() );
                        	}
                        	
                        	if (copyAttribute.getCode() != null && !copyAttribute.getCode().isEmpty() && cloneAttribute != null && cloneAttribute.getCode() != null && !cloneAttribute.getCode().isEmpty()) {
                        		cloneAttribute.setCode(copyAttribute.getCode() );
                        	}
                        	
                        	if (copyAttribute.getGetterCode() != null && !copyAttribute.getGetterCode().isEmpty() && cloneAttribute.getGetterCode() != null && !cloneAttribute.getGetterCode().isEmpty()) {
                        		cloneAttribute.setGetterCode(copyAttribute.getGetterCode());
                        	}
                        	
                            String copyDef = cloneAttribute.getCopyDef();
                            String name = cloneAttribute.getName();

                            if (copyDef != null) {
                            	copyDef(widget, cloneAttribute, copyDef, name);
                            }

                        }
                    }
            	}
                
            }
        }

		if (widget.getCustomAttribute() != null) {
			handleCustomAttribute(null, widget, widget.getCustomAttribute(), classConfigurations, widgetProperties);
		}

    	StringWriter stringWriter = new StringWriter();
		try {
            //Load template from source folder
            Template template = new Template("name", new FileReader(new File("templates/" + widget.getTemplate())),
                    new Configuration());
            
            // Console output
            Map<String, Object> models = new HashMap<>();
            stringWriter = new StringWriter();
            models.put("myclass", widget);
            models.put("process", environment);
            models.put("viewgroup", widget.getTemplate().equals("BaseHasWidgetsTemplate.java"));
            models.put("eventsTsCode", events);


            StringBuffer codeCopyMap = getExtraCode(widget, null, false);

			models.put("extraCode", extraCode.toString() + codeCopyMap.toString());	                
        	models.put("androidprefix", getPackagePrefix());
//            	if (widget.getBaseDirectory() != null) {
//            		configuration.setBaseDir(widget.getBaseDirectory());
//            	}
//        	widget.setClassname(getPackagePrefix() + widget.getClassName());	
            
            //System.out.println("configuration : " + configuration.getClassName());
            template.process(models, stringWriter);
            stringWriter.flush();

			String string = stringWriter.toString(); 
			string = string.replaceAll(" android\\.", String.format(" %sandroid.", getPackagePrefix()));
			string = string.replaceAll("\\(android\\.", String.format("(%sandroid.", getPackagePrefix()));
			string = replaceString("java", string, widget);					
			
            String pathname = getJavaFileLocation(widget);
            //if (!widget.isOnlyChildWidgets()) {
                writeOrUpdateFile(string, pathname, new HashMap<>());
                if (!processedWidgets.containsKey(environment)) {
                    processedWidgets.put(environment, new ArrayList<>());
                }
                widget.getAdditionalAttributes().put("widgetFullPackage", "com.ashera." + packageName +"." +widget.getClassname());
                processedWidgets.get(environment).add(widget);
                processedClassConfigurations.put(environment+ "." + widget.getWidgetName(), widget);
            //}
                
            if (widget.getGenerateIosClass() != null) {
            	generateWrapperMAndHFiles(widget);
            }
            
			
			pathname = getTsFileLocation(widget);
			template = new Template("name", new FileReader(new File("templates/" + widget.getJstemplate())),
                    new Configuration());
			stringWriter = new StringWriter();
			template.process(models, stringWriter);
			stringWriter.flush();
			string = stringWriter.toString();
			string = replaceString("ts", string, widget);
			writeOrUpdateFile(string, pathname, new HashMap<>());
			
			if (widget.getAttrstemplate() != null) {
				pathname = getAttrFileLocation(widget);
				template = new Template("name", new FileReader(new File("templates/" + widget.getAttrstemplate())),
                        new Configuration());
				stringWriter = new StringWriter();
				template.process(models, stringWriter);
				stringWriter.flush();
				string = stringWriter.toString();
				writeOrUpdateFile(string, pathname, true);
			}
            String nativeClassName = widget.getNativeclassname();
			if (widget.getWidgets() != null) {
				for (Widget widgetTemp : widget.getWidgets()) {
					template = new Template("name", new FileReader(new File("templates/" + widget.getTemplate())),
	                        new Configuration());
					widget.setCreateDefault(widgetTemp.getCreateDefault());
					widget.setWidgetclassname(widgetTemp.getWidgetclassname());
					
					if (widget.getAllAttributes() != null) {
						
						for (CustomAttribute customAttribute : widget.getAllAttributes()) {
							updateFromProtoCustomAttribute(widgetTemp, customAttribute, customAttribute.getName());
						}
					}
					
					codeCopyMap = getExtraCode(widgetTemp, null, false);
					
					CodeGenTemplate codeGenTemplate = null;
					if (widgetTemp.getOs().equals("swt")) {
						codeGenTemplate = new SwtCodeGenerator(this.quirkReportDto, packageName, "swt", "", testDir);
					}
					
					if (widgetTemp.getOs().equals("ios")) {
						codeGenTemplate = new IosCodeGenTemplate(this.quirkReportDto, packageName, "ios", "", testDir);
					}
					
					if (widgetTemp.getOs().equals("web")) {
						codeGenTemplate = new WebCodeGenTemplate(this.quirkReportDto, packageName, "web", "", testDir);
					}
					if (widgetTemp.getNativeclassname() != null) {
						widget.setNativeclassname(widgetTemp.getNativeclassname());
					}

					List<CustomAttribute> widgetAttributes = new ArrayList<>(widget.getWidgetAttributes());
					if (widgetTemp.getCustomAttribute() != null) {
						widget.getWidgetAttributes().addAll(widgetTemp.getCustomAttribute());
					}
					
					for (CustomAttribute customAttribute : widgetAttributes) {
						String updateUiFlag = widgetProperties.getProperty(customAttribute.getName() + "." + widgetTemp.getOs() + ".updateUiFlag");
						if (updateUiFlag != null) {
							customAttribute.setUpdateUiFlag(updateUiFlag);
						}
						
						String bufferStrategy = widgetProperties.getProperty(customAttribute.getName() + "." + widgetTemp.getOs() + ".bufferStrategy");
						if (bufferStrategy != null) {
							customAttribute.setBufferStrategy(bufferStrategy);
						}
					}
					
					filterWidgetAttributes(widget, widgetTemp);
					models.put("androidprefix", codeGenTemplate.getPackagePrefix());
					models.put("viewgroup", widget.getWidgetSuperClass().equals("ViewGroupImpl"));
					models.put("process", widgetTemp.getOs());
					models.put("extraCode", extraCode.toString() + codeCopyMap.toString());
					stringWriter = new StringWriter();
					template.process(models, stringWriter);
	                stringWriter.flush();
	                string = stringWriter.toString(); 
					string = string.replaceAll(" android\\.", String.format(" %sandroid.", codeGenTemplate.getPackagePrefix()));
					string = string.replaceAll("\\(android\\.", String.format("(%sandroid.", codeGenTemplate.getPackagePrefix()));
					string = string.replaceAll("\\		android\\.", String.format("		%sandroid.", codeGenTemplate.getPackagePrefix()));
					
					string = replaceString("java", string, widget);
					
					widget.setBaseDirectory(widgetTemp.getBaseDirectory());
					pathname = codeGenTemplate.getJavaFileLocation(widget);
					writeOrUpdateFile(string, pathname, new HashMap<>());
					
					
					pathname = codeGenTemplate.getTsFileLocation(widget);
                    template = new Template("name", new FileReader(new File("templates/" + widget.getJstemplate())),
                            new Configuration());
                    stringWriter = new StringWriter();
                    template.process(models, stringWriter);
                    stringWriter.flush();
                    string = stringWriter.toString();
                    string = replaceString("ts", string, widget);	
                    writeOrUpdateFile(string, pathname, new HashMap<>());
                    
                    widget.getWidgetAttributes().clear();
                    widget.getWidgetAttributes().addAll(widgetAttributes);
					if (!processedWidgets.containsKey(widgetTemp.getOs())) {
                        processedWidgets.put(widgetTemp.getOs(), new ArrayList<>());
                    }
					widgetTemp.getAdditionalAttributes().put("widgetFullPackage", "com.ashera." + packageName +"." +widget.getClassname());
                    processedWidgets.get(widgetTemp.getOs()).add(widget);
                    processedClassConfigurations.put(widgetTemp.getOs()+ "." + widget.getWidgetName(), widget);
                    
                    if (widgetTemp.getGenerateIosClass() != null) {
                    	generateWrapperMAndHFiles(widgetTemp);
                    }
				}
			}

			widget.setNativeclassname(nativeClassName);
			events = "";
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
        	throw new RuntimeException(e);
		} finally {
			try {
				stringWriter.close();
			} catch (IOException e) {
			}
		}

        generateTestCase(widget, environment);
        generateReport(widget, environment);
        
       if (widget.getWidgets() != null) {
    	   for (Widget childWidget : widget.getWidgets()) {
    		   childWidget.setPackageName(widget.getPackageName());
    		   childWidget.setGroup(widget.getGroup());
    		   childWidget.setName(widget.getName());
    		   childWidget.setTemplate(widget.getTemplate());
    		   childWidget.setNativeclassname(widget.getNativeclassname());
    		   childWidget.setLayoutAttributes(widget.getLayoutAttributes());
    		   childWidget.setWidgetAttributes(widget.getWidgetAttributes());
    		   
				if (childWidget.getCustomAttribute() != null) {
					childWidget.getWidgetAttributes().addAll(childWidget.getCustomAttribute());
				}
    		   generateTestCase(childWidget, childWidget.getOs());
    		   generateReport(childWidget, childWidget.getOs());
    	   }
       }
    }

	private void generateWrapperMAndHFiles(Widget widget) throws Exception{
		String[] keyWords = {"imports", "body", "props"};

        Map<String, Object> models = new HashMap<>();
        StringWriter stringWriter = new StringWriter();
        String className = widget.getGenerateIosClass().split("\\:")[0];
		models.put("className", className);
        models.put("parent_className", widget.getGenerateIosClass().split("\\:")[1]);
		models.put("createDefault", widget.getCreateDefault());

        Template template = new Template("name", new FileReader(new File("templates/ViewTemplate.h")),
                new Configuration());
		stringWriter = new StringWriter();
		template.process(models, stringWriter);
        stringWriter.flush();
		String code = stringWriter.toString(); 
		writeOrUpdateFile(code, widget.getIosBaseDirectory()  + className + ".h", false, new HashMap<>(), keyWords);		
		
		
		template = new Template("name", new FileReader(new File("templates/ViewTemplate.m")),
                new Configuration());
		stringWriter = new StringWriter();
		template.process(models, stringWriter);
        stringWriter.flush();
		code = stringWriter.toString(); 
		writeOrUpdateFile(code, widget.getIosBaseDirectory()  + className + ".m", false, new HashMap<>(), keyWords);		
	}

	private Map<String, TestCase> testcaseMap = new HashMap<>();
	private TestCase getTestCase(Widget widget)  {
		try {
			String environment = widget.getOs();
			File testCaseFile = new File("testcasedata/" + widget.getName() + environment + ".json");
			
			TestCase testcase = testcaseMap.get(environment);
			if (testcase == null && testCaseFile.exists()) {
			    testcase = new Gson().fromJson(com.ashera.utils.FileUtils.readFileToString(testCaseFile), TestCase.class);
			}
			
			if (testcase == null) {
			    testcase = new TestCase();
			}
			testcaseMap.put(environment, testcase);
			return testcase;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	private void filterWidgetAttributes(Widget widget) {
		filterWidgetAttributes(widget, widget);
	}
	private void filterWidgetAttributes(Widget widget, Widget widgetTemp) {
		String ignoreParentCustomAttributes = widgetTemp.getIgnoreParentCustomAttributes();
		if (ignoreParentCustomAttributes != null) {
		    List<String> list = java.util.Arrays.asList(ignoreParentCustomAttributes.split(","));
			List<CustomAttribute> filteredAttr = widget.getWidgetAttributes().stream().filter((customAttribute) -> !list.contains(customAttribute.getAttribute())).collect(Collectors.toList());
			widget.getWidgetAttributes().clear();
			widget.getWidgetAttributes().addAll(filteredAttr);
			
			filteredAttr = widget.getLayoutAttributes().stream().filter((customAttribute) -> !list.contains(customAttribute.getAttribute())).collect(Collectors.toList());
			widget.getLayoutAttributes().clear();
			widget.getLayoutAttributes().addAll(filteredAttr);

		}
	}
	
	private List<CustomAttribute> filterWidgetAttributes(List<CustomAttribute> classConfigurations, Widget widget) {
		String ignoreParentCustomAttributes = widget.getIgnoreParentCustomAttributes();
		if (ignoreParentCustomAttributes != null) {
		    List<String> list = java.util.Arrays.asList(ignoreParentCustomAttributes.split(","));
		    classConfigurations = classConfigurations.stream().filter((customAttribute) -> !list.contains(customAttribute.getAttribute())).collect(Collectors.toList());

		}
		
		return classConfigurations;
	}


	private List<CustomAttribute> getNodeElementsForJava(Generator generator, Widget widget, Properties widgetProperties) throws IOException{
		String url = generator.getUrl();
		if (url.startsWith("http") && generator.getCopyToPath() != null) {
			if (!new File(generator.getCopyToPath()).exists()) {
				readHttpUrlAsString(url, generator.getCopyToPath());
			}
			url = generator.getCopyToPath();
		}
		List<CustomAttribute> elements = new ArrayList<>();
		final CompilationUnit cu = getCU(new File(url));
		cu.accept(new ASTVisitor() {
			Map<String, CustomAttribute> nodeElementMap = new HashMap<>();
			Map<String, String> getterMap = new HashMap<>();

			public boolean visit(MethodDeclaration m) {	
				String methodName = m.getName().toString();
				String attributeName = environment + methodName.substring(3);
				if (methodName.startsWith("is")) {
					attributeName = environment + methodName.substring(2);
				}

				if (methodName.startsWith("get") || methodName.startsWith("is")) {
					if (nodeElementMap.containsKey(attributeName)) {
						nodeElementMap.get(attributeName).setGetterCode(methodName);
					} else {
						getterMap.put(attributeName, methodName);
					}
				}
				if (methodName.startsWith("set")) {
					if (m.parameters().size() == 1) {
						CustomAttribute nodeElement = new CustomAttribute();
						String varType = ((SingleVariableDeclaration)m.parameters().get(0)).getType().toString();
						nodeElement.setVarType(varType);
						if (varType.indexOf(".") != -1) {
							nodeElement.setVarType(varType.substring(varType.lastIndexOf(".") + 1));
						}
						nodeElement.setApiLevel("1");
						nodeElement.setCode(methodName + "("); 
						nodeElement.setName(attributeName);
						nodeElement.setGeneratorUrl(generator.getUrl());
						
						if (getterMap.containsKey(attributeName)) {
							nodeElement.setGetterCode(getterMap.get(attributeName));
						}
						elements.add(nodeElement);
						nodeElementMap.put(nodeElement.getName(), nodeElement);
					}
				}
				
				return true;
			}
		});
		return elements;
	}
	
	private String replaceString(String target, String code, Widget widget) {
		String replacedStr = code;
		ReplaceString[] replace = widget.getReplaceString();
		if (replace != null) {
			for (ReplaceString replaceString : replace) {
				if (replaceString.getTarget().equals(target)) {
					replacedStr = replacedStr.replaceAll(replaceString.getName(), replaceString.getReplace());
				}
			}
		}
		return replacedStr;
	}


	private void handleStringToEnumConversion(Properties widgetProperties, CustomAttribute element, CustomAttribute customAttribute) {
		List<String> filteredconverterKeys1 = new ArrayList<>(Arrays.asList(element.getConverterInfo1().split(",")));
		List<String> filteredconverterKeys2 = new ArrayList<>();
		String property = widgetProperties.getProperty("enum." + element.getVarType());
		List<String> apis = new ArrayList<>();

		for (Iterator<String> iterator = filteredconverterKeys1.iterator(); iterator.hasNext();) {
			String converterKey1 = iterator.next();											
			String converterKey2 = property+ "." +converterKey1.toUpperCase() ;
			
			String keyPrefix = "enum." + element.getVarType() + "." +converterKey1.toUpperCase()  + ".";
			
			if (!widgetProperties.containsKey(keyPrefix + "skip")) {
				if (widgetProperties.containsKey(keyPrefix + "api")) {
					apis.add(widgetProperties.getProperty(keyPrefix  + "api"));
				} else {
					apis.add("");
				}
				
				if (widgetProperties.containsKey(keyPrefix + "value")) {
					filteredconverterKeys2.add(widgetProperties.getProperty(keyPrefix + "value"));
				}
				else if (widgetProperties.containsKey(keyPrefix + "name")) {
					filteredconverterKeys2.add(property+ "." +widgetProperties.getProperty(keyPrefix + "name"));
				} else {
					filteredconverterKeys2.add(converterKey2);
				}
			} else {
				iterator.remove();
			}
			
		}
		
		customAttribute.setConverterInfo(filteredconverterKeys1, filteredconverterKeys2, "stringtoenum", apis);
		customAttribute.setJavaType(property);
	}

    private void generateTestCase(Widget widget, String environment) throws Exception{
        if (widget.isGenerateXmlTestCase()) {
            TestCase testcase = getTestCase(widget);

            
            List<CustomAttribute> allAttributes = new ArrayList<>();
            allAttributes.addAll(widget.getAllAttributes());
            allAttributes = filterWidgetAttributes(allAttributes, widget);
			
			for (CustomAttribute classConfiguration : widget.getAllAttributes()) {
				if (testcase.getTestCaseData() != null) {
			        classConfiguration.setXmlTest(testcase.getTestCaseData().get(classConfiguration.getTrimmedAttribute()));
			        classConfiguration.setTestDesc(testcase.getTestCaseData().get(classConfiguration.getTrimmedAttribute() + "Desc"));
				}
			}
			List<CustomAttribute> widgetAttributes = new ArrayList<>(widget.getWidgetAttributes());

			if (widget.getCopyAttribute() != null) {
                for (com.ashera.codegen.pojo.CopyAttribute copyAttribute : widget.getCopyAttribute()) {
                	if (copyAttribute.isOnlyForTestCase()) {
	                	Widget classConfiguration = processedClassConfigurations.get(widget.getOs() + "." + copyAttribute.getWidget());
	                    
	                    List<CustomAttribute> classConfigurations = classConfiguration.getAllAttributes();
	                    
	                    for (CustomAttribute attribute : classConfigurations) {
	                        if (attribute.getAttribute().matches(copyAttribute.getAttribute())) {
	                            if (testcase.getTestCaseData() != null) {
	                            	attribute.setXmlTest(testcase.getTestCaseData().get(attribute.getTrimmedAttribute()));
	                            	attribute.setTestDesc(testcase.getTestCaseData().get(attribute.getTrimmedAttribute() + "Desc"));
	                            }
	                            
	                            allAttributes.add(attribute);
	                        }
	                    }
                	}
                }
            }
			allAttributes.sort(java.util.Comparator.comparing(CustomAttribute::getAttribute));
            Map<String, Object> models = new HashMap<>();
            models.put("myclass", widget);
            models.put("process", environment);
            models.put("allAttributes", allAttributes);
            models.put("viewstub", widget.getTemplate().equals("BaseWidgetStubTemplate.java"));

            String testFileName = widget.getLocalNameWithoutPackage().toLowerCase() + environment + "_test.xml";

            String pathname = testDir
            		+ "platforms/android/app/src/main/res/layout/" + testFileName;
            Template template = new Template("name", new FileReader(new File("templates/widgettest.xml")),
                    new Configuration());
            StringWriter stringWriter = new StringWriter();
            StringBuffer extraCode = getExtraCode(widget, "xmltest", true);
            models.put("extraCode", "");
            if (!extraCode.toString().trim().equals("")) {
                models.put("extraCode", "<!-- " + extraCode + " -->");    
            }
            
            template.process(models, stringWriter);
            stringWriter.flush();
            String code = stringWriter.toString();
            writeOrUpdateXmlFile(code, pathname, false, new HashMap<>(), "body");
            
            String activityName = widget.getLocalName() + environment.substring(0, 1).toUpperCase() + environment.substring(1) + "Activity";
            if (activityName.contains(".")) {
            	activityName = activityName.substring(activityName.lastIndexOf(".") + 1);
            }
            
            pathname = testDir
            		+ "platforms/android/app/src/main/tsc/src/" + activityName + ".ts";
            template = new Template("name", new FileReader(new File("templates/widgettest.js")),
                    new Configuration());
            stringWriter = new StringWriter();
            extraCode = getExtraCode(widget, "tstest", true);
            models.put("extraCode", "");
            if (!extraCode.toString().equals("")) {
                models.put("extraCode", extraCode);    
            }

            template.process(models, stringWriter);
            stringWriter.flush();
            String string = stringWriter.toString();
            string = replaceString("tstest", string, widget);
        	writeOrUpdateFile(string, pathname, new HashMap<>());

            activities.add(activityName);
            layoutFiles.add(testFileName);


        }
    }

    public static void generateTestLayoutFiles(String testDir) throws IOException, FileNotFoundException, TemplateException {
        String pathname = testDir
        		+ "platforms/android/app/src/main/tsc/src/FragmentMapper.ts";
        Template template = new Template("name", new FileReader(new File("templates/FragmentMapper.ts")),
                new Configuration());
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> models = new HashMap<>();
        models.put("activities", activities);
        models.put("layoutFiles", layoutFiles);

        template.process(models, stringWriter);
        stringWriter.flush();
        String string = stringWriter.toString();
        writeOrUpdateFile(string, pathname, new HashMap<>());
        
        
        pathname = testDir
        		+ "platforms/android/app/src/main/tsc/src/HomeActivity.ts";
        template = new Template("name", new FileReader(new File("templates/HomeActivity.ts")),
                new Configuration());
        stringWriter = new StringWriter();
        template.process(models, stringWriter);
        stringWriter.flush();
        string = stringWriter.toString();
        writeOrUpdateFile(string, pathname, new HashMap<>());
    }

    private void generateReport(Widget widget, String environment) {
    	if (!widget.isExcludeFromReport()) {
			TestCase testcase = getTestCase(widget);
			for (CustomAttribute classConfiguration : widget.getAllAttributes()) {
				if (testcase.getTestCaseData() != null) {
			        classConfiguration.setXmlTest(testcase.getTestCaseData().get(classConfiguration.getTrimmedAttribute()));
			        classConfiguration.setTestDesc(testcase.getTestCaseData().get(classConfiguration.getTrimmedAttribute() + "Desc"));
				}
			} 
            QuirkWidget quirkWidget = new QuirkWidget();
            quirkWidget.setName(widget.getLocalName());
			List<CustomAttribute> widgetAttributes = new ArrayList<>(widget.getWidgetAttributes());						
//			filterWidgetAttributes(configuration, widget);

			for (CustomAttribute classConfiguration : widget.getAllAttributes()) {
                QuirkAttribute quirkAttribute = createQuirkAttribute(classConfiguration, widget);
                quirkWidget.getAttributes().add(quirkAttribute);
                
                List<String> alaises = classConfiguration.getAliases();
                
                for (String alais : alaises) {
                    quirkAttribute = (QuirkAttribute) quirkAttribute.clone();
                    quirkAttribute.setAttributeName(alais);
                    quirkWidget.getAttributes().add(quirkAttribute);
                }
            }
            
            for (CustomAttribute classConfiguration : widget.getLayoutAttributes()) {
                QuirkAttribute quirkAttribute = createQuirkAttribute(classConfiguration, widget);
                quirkWidget.getAttributes().add(quirkAttribute);
            }
            
            for (CustomAttribute classConfiguration : widget.getConstructorAttributes()) {
                QuirkAttribute quirkAttribute = createQuirkAttribute(classConfiguration, widget);
                quirkWidget.getAttributes().add(quirkAttribute);
            }
            
            if (widget.getCopyAttribute() != null) {
                for (com.ashera.codegen.pojo.CopyAttribute copyAttribute : widget.getCopyAttribute()) {
                	if (copyAttribute.isOnlyForTestCase()) {
	                    Widget classConfiguration = processedClassConfigurations.get(widget.getOs() + "." + copyAttribute.getWidget());
	                    
	                    List<CustomAttribute> classConfigurations = classConfiguration.getAllAttributes();
	                    
	                    for (CustomAttribute attribute : classConfigurations) {
	                        if (attribute.getAttribute().matches(copyAttribute.getAttribute())) {
	                            QuirkAttribute quirkAttribute = createQuirkAttribute(attribute, widget);
	                            quirkWidget.getAttributes().add(quirkAttribute);
	                        }
	                    }
                	}
                    
                }
            }

            String packageName = widget.getPackageName();
            String group = widget.getTrimmedGroup();
            quirkWidget.setPackageName(packageName);
            quirkWidget.setNativeClassName(widget.getNativeClassName().substring(widget.getNativeClassName().lastIndexOf(".") + 1) + " (" + environment + ")");
            quirkWidget.setGroup(group);
            quirkWidget.setIgnoredAttributes(ignoredAttributes);
            quirkReportDto.addWidget(packageName, group, quirkWidget);
            
            widget.getWidgetAttributes().clear();
            widget.getWidgetAttributes().addAll(widgetAttributes);
    	}
    }

    private QuirkAttribute createQuirkAttribute(CustomAttribute classConfiguration, Widget widget) {
        QuirkAttribute quirkAttribute = new QuirkAttribute();
        quirkAttribute.setXmlTest(classConfiguration.getXmlTest());
        quirkAttribute.setDescription(classConfiguration.getTestDesc());
        quirkAttribute.setAttributeName(classConfiguration.getAttribute());
        quirkAttribute.setNamespace("default".equals(classConfiguration.getPackageNamespace()) ? null : classConfiguration.getPackageNamespace());
        quirkAttribute.setGetterMethod(classConfiguration.getGetterMethod());
        quirkAttribute.setConstructor(classConfiguration.getConstructor());
        
        if (classConfiguration.getReadOnly() == null || !classConfiguration.getReadOnly().equals("true")) {
        	quirkAttribute.setSetterMethod(classConfiguration.getSetterMethod());
        }
        quirkAttribute.setGeneratorUrl(classConfiguration.getGeneratorUrl());
        
        if (classConfiguration.getGeneratorUrl() == null || classConfiguration.getGeneratorUrl().equals("")) {
        	quirkAttribute.setGeneratorUrl(widget.getDefaultGeneratorUrl());
        }
        return quirkAttribute;
    }

	private void handleCustomAttribute(Generator generator, com.ashera.codegen.pojo.Widget widget, List<com.ashera.codegen.pojo.CustomAttribute> customAttributes, ClassConfigurations classConfigurations, Properties widgetProperties) throws IOException{
		
		for (com.ashera.codegen.pojo.CustomAttribute customAttribute : customAttributes) {
			if (customAttribute.getConstructor() != null && customAttribute.getConstructor()) {
				widget.addConstructorAttribute(customAttribute);
				continue;
			}
			if (customAttribute.getType() == null || (customAttribute.getCode() == null && customAttribute.getGetterCode() == null) || customAttribute.isIgnore()) {
				continue;
			}
			
			customAttribute.setWidgetProperties(widgetProperties);
			customAttribute.setLayoutAttr(customAttribute.getName().startsWith("layout_") ? "yes" : "no");
			
			MethodParam[] methodParams = customAttribute.getMethodParam();
			handleMethodParams(widget, customAttribute, methodParams, widgetProperties);
			
			if (customAttribute.getConverterInfo1() != null) {
				if (widgetProperties.containsKey("enum." + customAttribute.getJavaType())) {
					customAttribute.setVarType(customAttribute.getJavaType());
					handleStringToEnumConversion(widgetProperties, customAttribute, customAttribute);
				} else {
					customAttribute.setConverterInfo(customAttribute.getConverterInfo1(), customAttribute.getConverterInfo2(), customAttribute.isSeperatedByBar() ? "bitflag" : "enumtoint", false);
				}
			}

			if (widgetProperties.containsKey(customAttribute.getName() + ".converter")) {
				customAttribute.setType(widgetProperties.getProperty(customAttribute.getName() + ".converter"));
			} else {
				CustomAttribute nodeElement = customAttribute.nodeElement;
				if (nodeElement != null) {
					customAttribute.setGeneratorUrl(nodeElement.getGeneratorUrl());
					customAttribute.setNamespace(nodeElement.getNamespace());
				}
				if (nodeElement != null && !customAttribute.getType().equals("boolean")) {
					if (widgetProperties.containsKey("enum." + nodeElement.getVarType())) {
						handleStringToEnumConversion(widgetProperties, nodeElement, customAttribute);
					} else if (nodeElement.getConverterInfo1() != null && !nodeElement.getConverterInfo1().equals("") && nodeElement.getConverterInfo2() != null) {
						customAttribute.setConverterInfo(nodeElement.getConverterInfo1(), nodeElement.getConverterInfo2(), nodeElement.isSeperatedByBar() ? "bitflag" : "enumtoint", 
								nodeElement.isSupportIntegerAlso());
					}
					
					addMethodDefinitions(generator, widget, nodeElement, widgetProperties);
				}
			}
			
			addMethodDefFromListenerMethods(widget, customAttribute);

//			if (Integer.parseInt(customAttribute.getApiLevel()) > 8) {
//				customAttribute.setAndroidMinVersion(customAttribute.getApiLevel() + "");
//			}
	        
			widget.addClassAttribute(customAttribute);
            String copyDef = customAttribute.getCopyDef();
            String name = customAttribute.getName();

			copyDef(widget, customAttribute, copyDef, name);
            
			widget.removeDuplicate(customAttribute.getName());
		 }
		
		// removed ignore parentAttributes
		filterWidgetAttributes(widget);
	}

	private void addMethodDefFromListenerMethods(com.ashera.codegen.pojo.Widget widget,
			com.ashera.codegen.pojo.CustomAttribute customAttribute) {
		if (customAttribute.getListenerMethods() != null) {
			String listenerClassName = customAttribute.getListenerClassName();
			if (listenerClassName.indexOf(".") != -1) {
				listenerClassName = listenerClassName.substring(listenerClassName.indexOf(".") + 1);
			}
			String methodDef = generateEventClass(customAttribute, null, listenerClassName, customAttribute.getListenerClassName(), widget);
			widget.addMethodDefition(methodDef);
		}
	}
	
	private void handleMethodParams(Widget widget, CustomAttribute customAttribute, MethodParam[] methodParams, Properties widgetProperties) throws IOException {
		if (methodParams != null) {
			for (com.ashera.codegen.pojo.MethodParam methodParam : methodParams) {
				if (methodParam.getConverterInfo1() != null) {
					methodParam.setConverterInfo(methodParam.getConverterInfo1(), methodParam.getConverterInfo2(), methodParam.isSeperatedByBar() ? "bitflag" : "enumtoint", false);
				}
				methodParam.setWidgetProperties(widgetProperties);
				copyDef(widget, customAttribute, methodParam.getCopyDef(), methodParam.getName());
			}
		}
	}

	private void copyDef(Widget widget, CustomAttribute customAttribute, String copyDef, String name) throws IOException {
		if (copyDef != null) {
			if (customAttribute.getCopyDefHint() != null) {
				name = customAttribute.getCopyDefHint();
			}
		    String jsonArray = readFileToString(new File(copyDef));
		    Type listType = new TypeToken<ArrayList<CustomAttribute>>(){}.getType();
		    List<CustomAttribute> nodeElements = new Gson().fromJson(jsonArray, listType);
		    for (CustomAttribute nodeElement: nodeElements) {
				if (nodeElement.getName() != null && name.equals(nodeElement.getName())) {
		        	if (nodeElement.getName().startsWith("on")) {
		        		if (nodeElement.methodDef != null) {
		        			widget.addMethodDefition(nodeElement.methodDef);
		        		}
		        	} else {
		        		if ("android.graphics.PorterDuff.Mode".equals(customAttribute.getJavaType())) {
		        			customAttribute.setSupportIntegerAlso(nodeElement.isSupportIntegerAlso());
							customAttribute.setConverterInfo(nodeElement.getKeys(), nodeElement.getValues(), nodeElement.getConverterType(), nodeElement.getApis());
		        		} else {
		        			customAttribute.setConverterInfo(nodeElement.getConverterInfo1(), nodeElement.getConverterInfo2(), nodeElement.isSeperatedByBar() ? "bitflag" : "enumtoint", nodeElement.isSupportIntegerAlso());
		        		}
		        	}
		            break;
		        }
		    }
		}
	}
	
	private List<CustomAttribute> generateAttributesFromAttrsXml(com.ashera.codegen.pojo.Widget widget, List<String> knowTypes, Generator generator, String documentStr, Properties widgetProperties, Url[] generatorUrl) throws javax.xml.bind.JAXBException, java.io.FileNotFoundException {
		String url = generator.getUrl();
		java.io.File file = readHttpUrlAsString(url, widget.getName() + "_attrs.xml");
		javax.xml.bind.JAXBContext jaxbContext = javax.xml.bind.JAXBContext.newInstance(com.ashera.codegen.pojo.attrs.Resources.class);
		javax.xml.bind.Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		List<CustomAttribute> customAttributes = new java.util.ArrayList<>();
		com.ashera.codegen.pojo.attrs.Resources resources = (com.ashera.codegen.pojo.attrs.Resources) unmarshaller.unmarshal(new java.io.FileInputStream(file));
		for (com.ashera.codegen.pojo.attrs.Resources.DeclareStyleable styleable: resources.getDeclareStyleable()) {
			String widgetName = widget.getName().substring(widget.getName().lastIndexOf(".") + 1);

			if (styleable.getName().equals(generator.getStyleableName())) {
				if (widget.getName().indexOf("Carousel") != -1) {
					System.out.println("aaa");
				}
				for (com.ashera.codegen.pojo.attrs.Resources.Attr attr: styleable.getAttr()) {
					CustomAttribute attribute = new CustomAttribute();
					attribute.setGeneratorUrl(generatorUrl[0].getUrl());
					String attrWithoutNameSpace = getAttributeWithoutNameSpace(attr.getName());
					
					
					if (generator.getFilterRegEx() != null) {
						Pattern p = Pattern.compile(generator.getFilterRegEx());//. represents single character
						Matcher m = p.matcher(attrWithoutNameSpace);
						boolean b = m.find();
						
						if (!b) {
							continue;
						}
					}

					List<String> ignoreList = new ArrayList<>(Arrays.asList(widgetProperties.getProperty("ignoreList." + environment).split(",")));
					String ignoreforClassNameList = widgetProperties.getProperty(widgetName + ".ignoreList." + environment);
					if (ignoreforClassNameList != null && !ignoreforClassNameList.equals("")) {
						ignoreList.addAll(Arrays.asList(ignoreforClassNameList.split(",")));
					}

					if (ignoreList.contains(attrWithoutNameSpace)) {
						continue;
					}
					
					boolean forceGenerate = generator.getForceGenerateAttributeAsList().contains(attrWithoutNameSpace);
					boolean forceGenerateAttributeGetIgnore = generator.getForceGenerateAttributeGetIgnoreAsList().contains(attrWithoutNameSpace);

					if (attrWithoutNameSpace.indexOf("_") != -1 && !attrWithoutNameSpace.startsWith("layout_")) {
						attrWithoutNameSpace = attrWithoutNameSpace.substring(attrWithoutNameSpace.indexOf("_") + 1);
					}
					attribute.setName(attr.getName());
					if (!attr.getName().contains(":")) {
						attribute.setNamespace("app");
					}
					
					updateFromProtoCustomAttribute(widget, attribute, attrWithoutNameSpace);
					attribute.setApiLevel("1");
					
					String setterMethod = getSetterMethodFromAttribute(attrWithoutNameSpace);
					String getterMethod = getGetterMethodFromAttribute(attrWithoutNameSpace);
					String regex = String.format("public\\s*void\\s*%s\\s*\\((.*)\\)", setterMethod);
					
					
					Pattern p = Pattern.compile(regex);//. represents single character
					Matcher m = p.matcher(documentStr);
					boolean b = m.find();
					
					if (forceGenerate) {
						String layoutParams = "";
						if (attrWithoutNameSpace.startsWith("layout_")) {
							layoutParams = "layoutParams, ";
						}
						attribute.setCode("code: " + setterMethod + "(" + layoutParams + "objValue);");

						if (!forceGenerateAttributeGetIgnore) {
							attribute.setGetterCode("code: " + getterMethod + "(" + layoutParams.trim().replaceAll(",", "")+ ")");
						}
						setCustomAttrType(resources, attr, attribute, widgetName, widgetProperties);
						customAttributes.add(attribute);
					} else {
					
						if (b) {	
							String[] params = m.group(1).split(",");
							attribute.setCode(setterMethod + "()");
							
							
							handleGetterMethod(documentStr, attribute, getterMethod);
							
							setCustomAttrType(resources, attr, attribute, widgetName, widgetProperties);
							
							if (params.length == 1 && knowTypes.contains(params[0].split("\\s")[0])) {
								customAttributes.add(attribute);
							} else {
								ignoredAttributes.add(attr.getName());
							}
						} else {
							List<String> matchStrings = new ArrayList<>();
	
							String layoutAttr = attrWithoutNameSpace.replaceAll("layout_constraint", "").replaceAll("layout_", "");
							layoutAttr = camelCase(layoutAttr);
							matchStrings.add(layoutAttr);
							
							if (layoutAttr.endsWith("Of")) {
								matchStrings.add(layoutAttr.substring(0, layoutAttr.length() - 2));
			                }
							boolean b1 = false;
							
							String mappedAttr = widgetProperties.getProperty("attr.map." + attrWithoutNameSpace);
							
							if (mappedAttr != null) {
								attribute.setCode(mappedAttr + " = ");
								attribute.setGetterCode("code:$var." + mappedAttr);
								setCustomAttrType(resources, attr, attribute, widgetName, widgetProperties);
								b1 = true;
								customAttributes.add(attribute);
							} else {
								for (String matchString : matchStrings) {
									b1 = trySetLayoutAttr(documentStr, widgetProperties, customAttributes, resources, widgetName, attr,
											attribute, matchString);
									if (b1) {
										break;
									}
								}
							}
							
							if (!b1) {
								ignoredAttributes.add(attr.getName());
							} else {
								if (!attribute.getName().startsWith("layout_")) {
									attribute.setName("layout_" + attrWithoutNameSpace);
								}
							}
							
						}
					}
				}
			}
		}

		return customAttributes;
	}

	public static void updateFromProtoCustomAttribute(com.ashera.codegen.pojo.Widget widget, CustomAttribute attribute,
			String attrWithoutNameSpace) {
		CustomAttribute proto = widget.getProtoTypeCustomAttribute(attrWithoutNameSpace);
		if (proto != null) {
			if (proto.getUpdateUiFlag() != null) {
				attribute.setUpdateUiFlag(proto.getUpdateUiFlag());
			}
			
			if (proto.getCode() != null) {
				attribute.setExtraCode(proto.getCode());
			}
			if (proto.getPreCode() != null) {
				attribute.setPreCode(proto.getPreCode());
			}
			if (proto.isApplyBeforeChildAdd()) {
				attribute.setApplyBeforeChildAdd(true);
			}
			
			if (proto.getOrder() != null) {
				attribute.setOrder(proto.getOrder());
			}
		}
	}

	private boolean  trySetLayoutAttr(String documentStr, Properties widgetProperties,
			List<CustomAttribute> customAttributes, com.ashera.codegen.pojo.attrs.Resources resources,
			String widgetName, com.ashera.codegen.pojo.attrs.Resources.Attr attr,
			CustomAttribute attribute, String layoutAttr) {
		String childregex = String.format("public\\s*(.*)\\s*%s<", layoutAttr);
		Pattern p1 = Pattern.compile(childregex);//. represents single character  
		Matcher m1 = p1.matcher(documentStr);  
		boolean b1 = m1.find();
		
		if (b1) {
			attribute.setCode(layoutAttr + " = ");
			attribute.setGetterCode("code:$var." + layoutAttr);
			setCustomAttrType(resources, attr, attribute, widgetName, widgetProperties);
			customAttributes.add(attribute);
		}
		
		return b1;
	}

	private void handleGetterMethod(String documentStr, CustomAttribute attribute, String getterMethod) {
		String getregex = String.format("public\\s*(.*)%s\\s*\\(\\s*\\)", getterMethod);
		Pattern p1 = Pattern.compile(getregex);//. represents single character  
		Matcher m1 = p1.matcher(documentStr);  
		boolean b1 = m1.find();

		if (b1) {
			attribute.setGetterCode(getterMethod + "()");
		}
	}

	private void setCustomAttrType(com.ashera.codegen.pojo.attrs.Resources resources, com.ashera.codegen.pojo.attrs.Resources.Attr myattr, com.ashera.codegen.pojo.CustomAttribute attribute, String widgetName, Properties widgetProperties) {
		String name = myattr.getName();

		if (name.toLowerCase().indexOf("width") != -1 || name.toLowerCase().indexOf("height") != -1) {
			attribute.setType("dimension");
			attribute.setJavaType("int");
		}
		
		if (attribute.getName().equals("android:orientation")) {
			attribute.setType("orientation");
			attribute.setJavaType("int");
			attribute.setConverterInfo1("horizontal,vertical");
			attribute.setConverterInfo2("0,1");
		}
		
		for (com.ashera.codegen.pojo.attrs.Resources.Attr attr: resources.getAttr()) {
			if (attr.getName().equals(name)) {
				setTypeInternal(name, attribute, attr);
			}
		}
		
		setTypeInternal(name, attribute, myattr);
	}

	private void setTypeInternal(String name, com.ashera.codegen.pojo.CustomAttribute attribute,
			com.ashera.codegen.pojo.attrs.Resources.Attr attr) {
		if (attr.getContent() != null && attr.getContent().size() > 0) {
			String converterInfo1 = "";
			String converterInfo2 = "";
			boolean seperatedByBar = false;
			String seperator = "";
			for (java.io.Serializable element : attr.getContent()) {

				if (element instanceof javax.xml.bind.JAXBElement) {
					javax.xml.bind.JAXBElement jaxBElement = (javax.xml.bind.JAXBElement) element;

					Object enumOrFlag = jaxBElement.getValue();
					seperatedByBar = jaxBElement.getName().equals("flag");
					if (enumOrFlag instanceof com.ashera.codegen.pojo.attrs.Resources.Attr.Enum) {
						converterInfo1 += seperator + ((com.ashera.codegen.pojo.attrs.Resources.Attr.Enum) enumOrFlag).getName();
						converterInfo2 += seperator + ((com.ashera.codegen.pojo.attrs.Resources.Attr.Enum) enumOrFlag).getValueAttribute();
						seperator = ",";
					}

					if (enumOrFlag instanceof com.ashera.codegen.pojo.attrs.Resources.Attr.Flag) {
						converterInfo1 += seperator + ((com.ashera.codegen.pojo.attrs.Resources.Attr.Flag) enumOrFlag).getName();
						converterInfo2 += seperator + ((com.ashera.codegen.pojo.attrs.Resources.Attr.Flag) enumOrFlag).getValueAttribute();
						seperator = ",";
					}
				}
			}

			if (converterInfo1.indexOf(",") != -1) {
				attribute.setConverterInfo1(converterInfo1);
				attribute.setConverterInfo2(converterInfo2);
				attribute.setSeperatedByBar(seperatedByBar);
				attribute.setType(name);
				attribute.setJavaType("int");
			} else {
				if (converterInfo1.equals("parent")) {
				    // TODO : make this generic
					attribute.setCode("code:$var." + attribute.getCode() + "((int) objValue);" +
							"\n\t\t\t\t\t\t\tif (strValue.equals(\"parent\")) {\n" +
							"\t\t\t\t\t\t\t\tlayoutParams." + attribute.getCode() + " androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID;\n" +
							"\t\t\t\t\t\t\t}");
				}
			}
		}
		if ("boolean".equals(attr.getFormat())) {
			attribute.setType("boolean");
			attribute.setJavaType("boolean");
		}

		if ("float".equals(attr.getFormat())) {
			attribute.setType("float");
			attribute.setJavaType("float");
		}

		if ("string".equals(attr.getFormat())) {
			attribute.setType("string");
			attribute.setJavaType("String");
		}

		if ("integer".equals(attr.getFormat())) {
		    attribute.setType("int");
		    attribute.setJavaType("int");
		}

		if ("reference|enum".equals(attr.getFormat()) || "reference".equals(attr.getFormat())) {
			attribute.setType("id");
			attribute.setJavaType("int");
		}
		if ("dimension|enum".equals(attr.getFormat()) || "dimension".equals(attr.getFormat())) {
			attribute.setType("dimension");
			attribute.setJavaType("int");
		}
	}

//	public void populateCompositeData(Widget widget, ClassConfiguration classConfiguration) {
//        if (widget.getComposites() != null) {
//            for (Composite composite : widget.getComposites()) {
//                ClassConfiguration classConfComposite = new ClassConfiguration();
//                classConfComposite.setClassName(composite.getName());
//                classConfiguration.addComposite(classConfComposite);
//            }
//        }
//    }

    public StringBuffer getExtraCode(Widget widget, String target, boolean xml) throws IOException {
        StringBuffer codeCopyMap = new StringBuffer("");
        if (widget.getCode() != null) {
        	for (Code code : widget.getCode()) {
        	    if ((target == null && code.getTarget() == null) || (target != null && target.equals(code.getTarget()))) {
            		String originalFile = readFileToString(new File(code.getUrl()));
					if (code.getCopy() != null) {
						for (Copy copy : code.getCopy()) {
							String[] keywords = copy.getFrom().split(",");

							for (String keyword : keywords) {
								String start = "start - " + keyword;
								int startOrig = originalFile.indexOf(start);
								String end = "end - " + keyword;
								int endOrig = originalFile.indexOf(end);

								String codeCopied = originalFile.substring(startOrig + start.length(), endOrig - 2);

								if (copy.getReplaceString() != null) {
									for (ReplaceString replaceString : copy.getReplaceString()) {
										String name = replaceString.getName();
										String replace = replaceString.getReplace();
										String nativeclassname = widget.getNativeclassname();
										if (nativeclassname != null) {
											String varName = nativeclassname
													.substring(nativeclassname.lastIndexOf(".") + 1);
											replace = replace.replaceAll("\\$varname",
													varName.substring(0, 1).toLowerCase() + varName.substring(1));
											replace = replace.replaceAll("\\$name", widget.getName());
										}
										codeCopied = codeCopied.replaceAll(name, replace);
									}
								}
								codeCopyMap.append("\n").append(codeCopied).append("\n");
							}
						}
					}
        	    }
        	}
        	
        }
        return codeCopyMap;
    }

	private String getSetterMethodFromAttribute(
			String attrbuteNameWithoutNameSpace) {
		return "set" +  attrbuteNameWithoutNameSpace.substring(0, 1).toUpperCase() + attrbuteNameWithoutNameSpace.substring(1);
	}
	
	private String getGetterMethodFromAttribute(
			String attrbuteNameWithoutNameSpace) {
		return "get" +  attrbuteNameWithoutNameSpace.substring(0, 1).toUpperCase() + attrbuteNameWithoutNameSpace.substring(1);
	}

	public abstract String getPackagePrefix();
	public abstract String getJavaFileLocation(Widget configuration);
	public abstract String getTsFileLocation(Widget configuration);
	public abstract String getAttrFileLocation(Widget configuration);

	public abstract void addMethodDefinitions(Generator generator, Widget parentConfig, CustomAttribute element, Properties widgetProperties); 
	public abstract String getFileName(String url); 
	public abstract void setSetterMethod(Widget classConfiguration,
			CustomAttribute element, Properties widgetProperties, String url); 
	public abstract List<CustomAttribute> getNodeElements(String genertorUrl, Document doc, Widget widget, Properties widgetProperties); 
	
	protected Document getDocument(String url, String fileCache, Url[] parentUrls)
			throws IOException {
		String content = getContentOfUrls(url, parentUrls);
		return Jsoup.parse(content);
	}

	public String getContentOfUrls(String url, Url[] parentUrls) throws IOException {
		ArrayList<String> parentUrlsStr = new ArrayList<String>();
		if (parentUrls != null) {
			for (Url url2 : parentUrls) {
				parentUrlsStr.add(url2.getUrl());
			}
		}
		String[] urls = combine(new String[] {url}, parentUrlsStr.toArray(new String[0]));
		cacheFiles(urls);
		
		String content = readFileContents(url, parentUrls);
		return content;
	}
	
    public String[] combine(String[] a, String[] b){
        int length = a.length + b.length;
        String[] result = new String[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    private String readFileContents(String url, Url[] parentUrls) throws IOException{
    	StringBuilder data = new StringBuilder("");
    	
    	if (url != null) {
	    	File file = new File("cache/" + getFileName(url));
	    	if (file.exists()) {
				String fileContents = readFileToString(file);
				fileContents = fileContents.replaceAll("&nbsp;", " ");	
				data.append(fileContents);
			}
    	} else {
    		data.append("<div></div>\n");
    	}
    	if (parentUrls != null) {
	    	for (Url parentUrl : parentUrls) {
	    		String parentData = readFileContents(parentUrl.getUrl(), new Url[0]);
	    		if (parentUrl.getReplaceStrings() != null) {
		    		for (ReplaceString replaceString : parentUrl.getReplaceStrings()) {
		    			parentData = parentData.replaceAll(replaceString.getName(), replaceString.getReplace());	    					
					}
	    		}
	    		int firstIndex = parentData.indexOf("<div");
	    		int lastIndex = parentData.lastIndexOf("</div>");
	    		data.insert(data.lastIndexOf("</div>") + "</div>".length(), parentData.substring(firstIndex, lastIndex + "</div>".length()));
			}
    	}
    	
    	return data.toString();
    }
	private void cacheFiles(String[] urls) throws IOException{
		for (String url : urls) {
			if (url != null) {
				File file = new File("cache/" + getFileName(url));
				if (!file.exists()) {
					Document doc = Jsoup.connect(url).maxBodySize(0)
						       .timeout(10000).get();
					String htmlString = doc.html();
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(htmlString.getBytes("UTF-8"));
					fos.close();
				}
			}
		}
	}

    public static void generateLayoutsPlugin(String[] env,  String[] paths) throws IOException{
        System.out.println(processedWidgets);
        for (int i = 0; i < env.length; i++) {
            StringBuffer stringBuffer = new StringBuffer("//start - widgets\n");
            List<Widget> widgets = processedWidgets.get(env[i]);
            if (widgets != null) {
                for (Widget widget : widgets) {
                	String pathName = widget.getClassname();
                    if (!(pathName.equals("ViewImpl") || pathName.equals("ViewGroupImpl") || pathName.equals("ViewGroupModelImpl"))) {
                    	if (widget.isViewplugin()) {
                    		stringBuffer.append("        com.ashera.widget.WidgetFactory.registerAttributableFor(\"" + widget.getViewPluginFor()  + "\", " +
                    				 "new " + widget.getAdditionalAttributes().get("widgetFullPackage") + "());\n");
                    	} else {
                    		stringBuffer.append("        WidgetFactory.register(new " + widget.getAdditionalAttributes().get("widgetFullPackage") + "());\n");	
                    	}
                    }                            
                }
                
                stringBuffer.append("        //end - widgets\n");
                
                writeOrUpdateFile(stringBuffer.toString(), paths[i], false, "widgets");
            }
        }
        
        processedWidgets.clear();
    }
    
    public String generateEventClass(CustomAttribute nodeElement, String methodSignatureListener,
			String className, String interfaceName, Widget widget) {
		String methodDef = "@SuppressLint(\"NewApi\")\n" + "private static class " + className
				+ " implements " + interfaceName + ", com.ashera.widget.IListener{\n"
				+ "private IWidget w; private View view; private String strValue; private String action;\n"
				+ "public String getAction() {return action;}\n"
				+ "public "+ className + "(IWidget w, String strValue)  {\n" + "this.w = w; this.strValue = strValue;\n" + "}\n"
				+ "public "+ className + "(IWidget w, String strValue, String action)  {\n" + "this.w = w; this.strValue = strValue;this.action=action;\n" + "}\n";
		if (methodSignatureListener != null) {
			methodDef += generateEventMethodDef(methodSignatureListener, nodeElement, widget);
		} else if (nodeElement.getListenerMethods() != null && !nodeElement.getListenerMethods().isEmpty()) {
			for (String listenerMethod : nodeElement.getListenerMethods()) {
				methodDef += generateEventMethodDef("public " + listenerMethod, listenerMethod.split("\\s|\\(")[1], widget);
			}
		}
		methodDef += "\n}\n";
		return methodDef;
	}

    public String generateEventMethodDef(String methodSignatureListener, CustomAttribute nodeElement, Widget widget) {
    	String name = nodeElement.getName();
    	return generateEventMethodDef(methodSignatureListener, name, widget);
	}

	private String generateEventMethodDef(String methodSignatureListener, String name, Widget widget) {
		StringWriter stringWriter = null;
		try {
			Template template = new Template("name", new FileReader(new File("templates/AndroidNativeListener.java")),
					new Configuration());
			String methodDef = methodSignatureListener.replace("abstract ", "");
			String[] params = getMethodVars(methodSignatureListener);
			String[] paramsType = getMethodParams(methodSignatureListener);
			String returnType = getMethodReturnType(methodSignatureListener);
			// Console output
			Map<String, Object> models = new HashMap<>();
			stringWriter = new StringWriter();
			models.put("action", name.substring(2).toLowerCase());
			models.put("event", name);
			models.put("eventGet", name.substring(0, 1).toUpperCase() + name.substring(1));
			models.put("methodSignatureListener", methodDef);
			models.put("params", Arrays.asList(params));
			models.put("paramTypes", Arrays.asList(paramsType));
			models.put("myclass", widget);
			
			String paramsWithTypes = "";
			String paramsStr = "";
			String seperator = "";
			for (int i = 0; i < paramsType.length; i++) {
				paramsWithTypes +=  seperator + paramsType[i] + " " + params[i];
				paramsStr +=  seperator + params[i];
				seperator = ",";
			}
			models.put("paramsWithTypes", paramsWithTypes);
			models.put("paramsStr", String.join (",",paramsStr));
			models.put("returnType", returnType);

			template.process(models, stringWriter);
			stringWriter.flush();
			
			
			models.put("event", name.substring(0 ,1).toUpperCase() + name.substring(1));
			template = new Template("name", new FileReader(new File("templates/EventTemplate.java")),
					new Configuration());
			StringWriter stringWriter1 = new StringWriter();
			template.process(models, stringWriter1);
			stringWriter1.flush();
			events += stringWriter1.toString() + "\n";
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		String string = stringWriter.toString();
		return string;
	}
}