package com.ashera.codegen;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class LicenseUpdator extends CodeGenBase{
	private static final String END_LICENSE = "//end - license";
	private static boolean REMOVE_LICENSE = false; 

	public static void main(String[] args) throws Exception{
		String[] files = new String[] { "../../core-android-widget", "../../core-widget_library",
				"../../core-javafx-widget", "../../core-ios-widgets", "../../core-web-widget" };
		String licenseTxt = readFileToString(new File("templates/licenseheader.txt"));
		String licenseTxtEpl = readFileToString(new File("templates/licenseheader_EPL-2.0.txt"));
		for (String file : files) {
			Path rootPath = Paths.get(file);
			try (Stream<Path> paths = Files.walk(rootPath)) {
				paths.filter(Files::isRegularFile).filter(p -> p.toString().endsWith(".java"))
						.forEach((path)-> {
							try {
								String pathStr = path.toFile().toString();
								String absolutePath = path.toFile().getAbsolutePath();
								if (pathStr.contains("cordova-plugin-") && !pathStr.contains("-swt")) {
									System.out.println("1. ignored -> " + path.toFile());
									return;
								}
								
								if (pathStr.contains("code_generator") && !pathStr.contains("\\src\\")) {
									System.out.println("2. ignored -> " + path.toFile());
									return;
								}
								
								if (pathStr.contains("\\gen\\")) {
									System.out.println("3. Ignored -> " + path.toFile());
									return;
								}
								
								if (pathStr.contains("LicenseUpdator.java")) {
									System.out.println("4. ignored -> " + path.toFile());
									return;
								}
								
								if (pathStr.contains("\\cordova-swt\\") || pathStr.contains("\\cordova-web\\")) {
									System.out.println("5. ignored -> " + path.toFile());
									return;
								}
								
								if (pathStr.contains("\\plugintemplate\\")) {
									System.out.println("6. ignored -> " + path.toFile());
									return;
								}
								
								
								String originalFile = readFileToString( path.toFile());
								boolean force = originalFile.indexOf("end - license") == -1 || originalFile.indexOf("Copyright (c)") == -1;
								String license = licenseTxt;
								if (isEpl2_0License(absolutePath)) {
									license = licenseTxtEpl; 
								}
								
								if (originalFile.indexOf("end - license") == -1) {
									originalFile = license + originalFile;
								} else {
									int startOrig = originalFile.indexOf("//start - license");
									int endOrig = originalFile.indexOf(END_LICENSE);
							    	if (startOrig != -1 && endOrig != -1) {
							    		if (REMOVE_LICENSE) {
								    		originalFile = originalFile.substring(endOrig + END_LICENSE.length() + 2, originalFile.length());
								    		force = true;
							    		} else {
							    			originalFile = originalFile.substring(0, startOrig) +
								    				license.substring(0, license.length() - 2) +
								        			originalFile.substring(endOrig + END_LICENSE.length() + 2, originalFile.length());
							    		}
							    	}
								}

								writeOrUpdateFile(originalFile,  path.toFile().getAbsolutePath(), force, new HashMap<String, String>(),
										"license");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						});
				
			} catch (IOException e) {
				System.err.println("Error walking directory: " + e.getMessage());
			}
			
			try (Stream<Path> paths = Files.walk(rootPath)) {
				paths.filter(Files::isRegularFile).filter(p -> p.toString().endsWith("package.json"))
				.forEach((path)-> {
							try {
								String absolutePath = path.toFile().getAbsolutePath();
								if (absolutePath.contains("cordova-plugin-") && !absolutePath.contains("-swt")) {
									System.out.println("ignored -> " + path.toFile());
									return;
								}
								String content = readFileToString(path.toFile());
								
								if (content.contains("\"license\": \"Ram M\"")) {
									// Replace existing license line (robust for minor format differences)
									content = content.replaceFirst("\"license\": \"Ram M\"",
											"\"author\": \"Ram M\"");
								}
								String license = "\"Apache-2.0\"";
								if (isEpl2_0License(absolutePath)) {
									license = "\"EPL-2.0\"";
								}
								if (content.contains("\"license\"")) {
									// Replace existing license line (robust for minor format differences)
									content = content.replaceAll("\"license\"\\s*:\\s*\"[^\"]*\"",
											"\"license\": " + license + "");
								} else {
									// Insert license field if missing
									content = content.replaceFirst("\\{", "{\n  \"license\": " + license + ",");
								}
								
								if (content.contains("\"author\"")) {
									// Replace existing license line (robust for minor format differences)
									content = content.replaceFirst("\"author\"\\s*:\\s*\"[^\"]*\"",
											"\"author\": \"Ram M\"");
								}
								
								writeToFile(path.toFile(), content);
							} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
			} catch (IOException e) {
				System.err.println("Error walking directory: " + e.getMessage());
			}
		}

	}

	private static boolean isEpl2_0License(String absolutePath) {
		return absolutePath.contains("SWTDialogPlugin") || absolutePath.contains("SWTSwitchPlugin");
	}
}
