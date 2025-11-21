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
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class J2ObjcPathGenerator extends CodeGenBase {
	public static void main(String[] args)  throws Exception {
		String[] paths = J2ObjcPrefixCodeGen.paths;

		List<String> classPaths = Arrays.stream(paths).map(path -> {
			try {
				return new File(path).getCanonicalPath();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}).collect(Collectors.toList());

		List<File> fileNames = classPaths.stream().map((path) -> {
			return getFileStream(path).filter((filePath) -> filePath.toFile().isDirectory() && filePath.toFile().list(new java.io.FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".java") && !dir.getAbsolutePath().contains("freemarker");
				}
				
			}).length > 0)
					.map((filePath) -> filePath.toFile()).collect(Collectors.toList());
		}).flatMap(list -> list.stream()).collect(Collectors.toList());
		StringBuffer antCalls = new StringBuffer("<!-- start antcalls -->\n");
		for (int i = 0; i < fileNames.size(); i++) {
			File file = fileNames.get(i);
			File myParent = file.getParentFile();
			String outDirStr = null;
			while (myParent != null) {
				File outDir = new File(myParent, ".outdir");
				if (outDir.exists()) {
					outDirStr = readFileToString(outDir);
					break;
				}
					
				myParent = myParent.getParentFile();
			}
			
			if (outDirStr == null) {
				System.out.println("ignored -> " + file);
				continue;
			}
			antCalls.append(("  		<antcall target=\"" + (outDirStr.contains(File.separator + "library" + File.separator) ?  "j2objc-no-arc" : "j2objc-use-arc")
					+ "\">\r\n"
					+ "    		<param name=\"inputfile\" value=\"" + file.getAbsolutePath() + "\\*.java\"/>\r\n"
					+ "    		<param name=\"outputdir\" value=\"" + outDirStr +"\"/>\r\n"
					+ "    		\r\n"
					+ "  		</antcall>\n"));
			
		}
		
		antCalls.append("\n<!-- antcalls end -->\n");
		writeOrUpdateXmlFile(antCalls.toString(), "build-j2objc-ios.xml", false, new java.util.HashMap<>(), "antcalls");
		
		System.out.println(antCalls);
		
	}
	
	private static Stream<Path> getFileStream(String path) {
		try {
			return Files.walk(Paths.get(path), FileVisitOption.FOLLOW_LINKS);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
