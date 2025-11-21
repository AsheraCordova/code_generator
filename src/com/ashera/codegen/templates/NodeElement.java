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
package com.ashera.codegen.templates;

public class NodeElement {
    public String varType;
    public String attributeName;
    public String actualAttributeName;
    public String apiLevel;
    public String setterMethod;
    public String converterKeys1 = "";
    public String converterKeys2 = "";
    public boolean isBitFag;
    public String getterMethod;
    public String methodDef;
    public String javaType;
	public boolean supportIntegerAlso;
	public boolean disposable;
    public String apiLevelForGet;
	public String namespace;
	public String generatorUrl;
	public String iosMinVersion;
	public String setterMethodNoCompat = "";
}