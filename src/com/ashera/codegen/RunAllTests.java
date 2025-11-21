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
package com.ashera.codegen;

public class RunAllTests {
	public static void main(String[] args) throws Exception{
		CodeGenFromHtml.main(args);
		J2ObjcPrefixCodeGen.main(args);
		J2ObjcPathGenerator.main(args);
		LayoutDependencyGenerator.main(args);
		PluginPathGen.main(args);
		ResourcesSupplierGen.main(args);
		CodeGenPluginInvoker.main(args);
		CopyFilesToDemoGen.main(args);
		CordovaLibCopyFiles.main(args);
		HtmlToMarkDownGenerator.main(args);
		ReplaceStringsInHtml.main(args);
		LicenseUpdator.main(args);
	}
}
