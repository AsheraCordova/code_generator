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

import java.io.File;

public class ProjectFileGen {
	public static void main(String[] args) throws Exception{
		String baseLocation = "D:/Java/github_ashera/";
		File[] files =  new File(baseLocation).listFiles();
		
		for (File file : files) {
			File file2 = new File(file, ".project");
			if(!file2.exists()) {
				org.apache.commons.io.FileUtils.writeStringToFile(file2, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
						+ "<projectDescription>\r\n"
						+ "	<name>"
						+ file.getName()
						+ "</name>\r\n"
						+ "	<comment></comment>\r\n"
						+ "</projectDescription>");
			}
			
		}
	}
}
