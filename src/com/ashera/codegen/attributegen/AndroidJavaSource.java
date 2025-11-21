//start - license
/*
 * Copyright (c) 2025 Ashera Cordova
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
//end - license
package com.ashera.codegen.attributegen;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.ashera.codegen.CodeGenHelper;
import com.ashera.codegen.pojo.CustomAttribute;
import com.ashera.codegen.pojo.SourceConfig;
import com.ashera.codegen.pojo.Widget;
import com.ashera.codegen.pojo.WidgetConfig;

public class AndroidJavaSource extends com.ashera.codegen.CodeGenBase {
	private Set<String> setOnItems = new HashSet<>();
	
	private CompilationUnit getCU(String source) throws IOException {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(source.toCharArray());
		parser.setCompilerOptions(getCompilerOptions());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		return cu;
	}

	private Map<String, String> getCompilerOptions() {
		Map<String, String> defaultOptions = new HashMap<>();
		defaultOptions.put(JavaCore.COMPILER_LOCAL_VARIABLE_ATTR,
				JavaCore.GENERATE);
		defaultOptions.put(JavaCore.COMPILER_PB_UNUSED_PRIVATE_MEMBER,
				JavaCore.IGNORE);
		defaultOptions.put(JavaCore.COMPILER_PB_LOCAL_VARIABLE_HIDING,
				JavaCore.WARNING);
		defaultOptions.put(JavaCore.COMPILER_PB_FIELD_HIDING, JavaCore.WARNING);
		defaultOptions.put(
				JavaCore.COMPILER_PB_POSSIBLE_ACCIDENTAL_BOOLEAN_ASSIGNMENT,
				JavaCore.WARNING);
		defaultOptions.put(JavaCore.COMPILER_PB_SYNTHETIC_ACCESS_EMULATION,
				JavaCore.WARNING);
		defaultOptions.put(JavaCore.COMPILER_PB_SYNTHETIC_ACCESS_EMULATION,
				JavaCore.WARNING);
		defaultOptions.put(JavaCore.COMPILER_CODEGEN_UNUSED_LOCAL,
				JavaCore.PRESERVE);
		defaultOptions.put(JavaCore.COMPILER_PB_UNNECESSARY_ELSE,
				JavaCore.WARNING);
		defaultOptions.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_7);
		defaultOptions.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM,
				JavaCore.VERSION_1_7);
		defaultOptions.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_7);
		return defaultOptions;
	}
	public void updateAttribute(WidgetConfig widgetConfig, SourceConfig sourceConfig, Widget widget) throws Exception {
		File file = readHttpUrlAsString(sourceConfig.getUrl(), CodeGenHelper.getCacheFileName(widgetConfig, sourceConfig));
		String javasource = readFileToString(file);
		final CompilationUnit cu = getCU(javasource);
		List<TypeDeclaration> allClasses = new  java.util.ArrayList<>();
		cu.accept(new ASTVisitor() {
			private List<String> typeDeclarations = new java.util.ArrayList<>();
			@Override
			public boolean visit(TypeDeclaration t) {
				String name = t.getName().toString();
				typeDeclarations.add(name);
				allClasses.add(t);
				return true;
			}
			@Override
			public boolean visit(MethodDeclaration m) {
				String methodName = m.getName().toString();
				if (m.getParent() instanceof TypeDeclaration ) {
					String name = ((TypeDeclaration)m.getParent()).getName().toString();

					// ignore inner classes
					if (typeDeclarations.indexOf(name) == 0) {
						int flags = m.getModifiers();
						if (Modifier.isPublic(flags) && !Modifier.isStatic(flags)) {
							Type returnType2 = m.getReturnType2();
							if ((m.parameters().size() == 1 && returnType2 != null && returnType2.toString().equals("void")) || m.parameters().size() == 0) {
								List<String[]> params = new java.util.ArrayList<>();
								for (int i = 0; i < m.parameters().size(); i++) {
									SingleVariableDeclaration param = (SingleVariableDeclaration) m.parameters().get(i);
									params.add(new String[] {param.getType().toString(), param.getName().toString()});
								}
								updateCustomAttribute(sourceConfig, widget, methodName, returnType2 != null ? returnType2.toString() : null, params);
							}
						}
					}
				}
				
				return true;
			}
		});
		
		for (String setOnClassName : setOnItems) {
			for (TypeDeclaration typeDeclaration : allClasses) {
				String name = typeDeclaration.getName().toString();
				if (setOnClassName.equals(name)) {
					if (typeDeclaration.getParent() instanceof TypeDeclaration) {
						List<String[]> methods = new java.util.ArrayList<>();
						for (int i = 0; i < typeDeclaration.getMethods().length; i++) {
							MethodDeclaration m = typeDeclaration.getMethods()[i];
							String methodDef = m.toString().replaceAll("\\n", "");
							if (methodDef.endsWith(";")) {
								methodDef = methodDef.substring(0, methodDef.length() - 1);
							}
							methods.add(new String[] {methodDef, m.getName().toString()});
						}
						
						handleSetOn(((TypeDeclaration)typeDeclaration.getParent()).getName().toString(), setOnClassName, methods, widget);
					}
				}
			}
			
		}
	}

	private void updateCustomAttribute(SourceConfig sourceConfig, Widget widget, String methodName,
			String returnType, List<String[]> params) {
		CustomAttribute customAttribute = new CustomAttribute();
		customAttribute.setGeneratorUrl(sourceConfig.getUrl());
		customAttribute.setNamespace("default");

		if (methodName.length() > 3) {
			String attributeName = methodName;
			if (methodName.startsWith("set") || methodName.startsWith("get")) {
				int index = 3;
				if (methodName.startsWith("setIs")) {
					index = 5;
				}
				attributeName = methodName.substring(index);
			}
			if (methodName.startsWith("is")) {
				attributeName = methodName.substring(2);
			}
			attributeName = attributeName.substring(0, 1).toLowerCase() + attributeName.substring(1);
			
			if (attributeName.startsWith("on") || attributeName.endsWith("Listener")) {
				attributeName = CodeGenHelper.getAttributeNameFromChangeListener(attributeName);
				customAttribute.setType("string");
				if (methodName.startsWith("set")) {
					setOnItems.add(methodName.replaceFirst("set", ""));
				}
			}
			
			customAttribute.setName(attributeName);
			
			String type = getJavaType(methodName, returnType, params);
			CodeGenHelper.setTypeOnCustomAttribute("android", customAttribute, type);
			customAttribute.setJavaType(type);
			if (methodName.startsWith("set")) {
				if (attributeName.startsWith("on") || attributeName.endsWith("Listener")) {
					
				} else {
					customAttribute.setCode(methodName);
				}
			} else if (methodName.startsWith("get") || methodName.startsWith("is")) {
				if (returnType.equals("")) {
					customAttribute.setGetterCode(methodName);
				}
			} else {
				customAttribute.setCode(methodName);
				customAttribute.setUseMethodName(true);
			}

			String javaType = customAttribute.getJavaType();
			if (javaType != null) {
				widget.addOrUpdateAttribute(customAttribute);
			}
		}
	}

	private String getJavaType(String methodName, String returnType, List<String[]> params) {
		if (methodName.startsWith("is") || methodName.startsWith("get")) {
			return returnType;
		}
		if (params.size() > 0) {
			return params.get(0)[0];
		}
		
		return null;
	}

	private void handleSetOn(String parentClassName, String className, List<String[]> methods, Widget widget)
			throws Exception {
		CustomAttribute customAttribute = new CustomAttribute();
		customAttribute.setListenerClassName(parentClassName + "." + className);

		if (customAttribute.getListenerMethods() == null) {
			customAttribute.setListenerMethods(new java.util.ArrayList<>());
		}
		customAttribute.setName(CodeGenHelper.getAttributeNameFromChangeListener(className));
		CustomAttribute existingCustomAttribute = widget.addOrUpdateAttribute(customAttribute);

		if (existingCustomAttribute != null) {
			for (String[] methodDef : methods) {
				customAttribute.getListenerMethods().add(methodDef[0].replaceAll("@(.*?)\\s", ""));
	
				CustomAttribute methodNameAttribute = (CustomAttribute) existingCustomAttribute.clone();
				methodNameAttribute.setName(CodeGenHelper.getAttributeNameFromChangeListener(methodDef[1]));
				methodNameAttribute.setCode("code:$var.set" + className + "(new " + className + "(this, strValue, \""
						+ methodNameAttribute.getName() + "\"));");
				widget.addOrUpdateAttribute(methodNameAttribute);
			}
		}
	}
}
