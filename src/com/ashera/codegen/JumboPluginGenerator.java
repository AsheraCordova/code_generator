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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import com.ashera.codegen.pojo.Copy;
import com.ashera.codegen.pojo.Plugin;
import com.ashera.codegen.pojo.ReplaceString;

public class JumboPluginGenerator {
	public static void createPluginXml(File mainDir) throws Exception {
		StringBuffer str = new StringBuffer();
		int i = 0;
		File srcFile = new File(mainDir, "android/package.json");
		if (srcFile.exists()) {
			FileUtils.copyFile(srcFile, new File(mainDir, "package.json"));
		}
		for (File pluginDir : mainDir.listFiles()) {
			File pluginXml = new File(pluginDir, "plugin.xml");

			if (!pluginXml.exists()) {
				continue;
			}
			String pluginXmlStr = FileUtils.readFileToString(pluginXml);
			pluginXmlStr = pluginXmlStr.replace("src=\"", "src=\"" + pluginDir.getName() + "/");

			if (i == 0) {
				str.append(pluginXmlStr);
			} else {
				String platformXml = pluginXmlStr.substring(pluginXmlStr.indexOf("<platform"),
						pluginXmlStr.indexOf("</platform>") + "</platform>".length());
				String platformStartTag = "<platform name=\"android\">";
				String platformEndTag = "</platform>";
				boolean handled = false;
				if (str.indexOf(platformStartTag) != -1) {
					int index = platformXml.indexOf(platformStartTag);
					if (index != -1) {
						platformXml = platformXml.replace(platformStartTag, "");
						platformXml = platformXml.replace(platformEndTag, "");

						int strStartTagIndex = str.indexOf(platformStartTag);
						int endTagIndex = str.substring(strStartTagIndex).indexOf(platformEndTag);
						str = str.insert(strStartTagIndex + endTagIndex, platformXml);
						handled = true;
					}
				}

				if (!handled) {
					platformStartTag = "<platform name=\"ios\">";
					platformEndTag = "</platform>";
					handled = false;
					if (str.indexOf(platformStartTag) != -1) {
						int index = platformXml.indexOf(platformStartTag);
						if (index != -1) {
							platformXml = platformXml.replace(platformStartTag, "");
							platformXml = platformXml.replace(platformEndTag, "");

							int strStartTagIndex = str.indexOf(platformStartTag);
							int endTagIndex = str.substring(strStartTagIndex).indexOf(platformEndTag);
							str = str.insert(strStartTagIndex + endTagIndex, platformXml);
							handled = true;
						}
					}
				}

				if (!handled) {
					platformStartTag = "<platform name=\"swt\">";
					platformEndTag = "</platform>";
					handled = false;
					if (str.indexOf(platformStartTag) != -1) {
						int index = platformXml.indexOf(platformStartTag);
						if (index != -1) {
							platformXml = platformXml.replace(platformStartTag, "");
							platformXml = platformXml.replace(platformEndTag, "");

							int strStartTagIndex = str.indexOf(platformStartTag);
							int endTagIndex = str.substring(strStartTagIndex).indexOf(platformEndTag);
							str = str.insert(strStartTagIndex + endTagIndex, platformXml);
							handled = true;
						}
					}
				}

				if (!handled) {
					platformStartTag = "<platform name=\"browser\">";
					platformEndTag = "</platform>";
					handled = false;
					if (str.indexOf(platformStartTag) != -1) {
						int index = platformXml.indexOf(platformStartTag);
						if (index != -1) {
							platformXml = platformXml.replace(platformStartTag, "");
							platformXml = platformXml.replace(platformEndTag, "");

							int strStartTagIndex = str.indexOf(platformStartTag);
							int endTagIndex = str.substring(strStartTagIndex).indexOf(platformEndTag);
							str = str.insert(strStartTagIndex + endTagIndex, platformXml);
							handled = true;
						}
					}
				}

				if (!handled) {
					str = str.insert(str.lastIndexOf(platformEndTag) + platformEndTag.length(),
							"\n  " + platformXml + "\n");
				}
			}
			i++;

		}

		FileUtils.writeStringToFile(new File(mainDir, "plugin.xml"), str.toString());
	}

	public static void main(String[] args) throws Exception {
		String baseLocation = "D:/Java/github_ashera/";
		javax.xml.bind.JAXBContext jaxbContext = javax.xml.bind.JAXBContext
				.newInstance(com.ashera.codegen.pojo.Plugins.class);
		javax.xml.bind.Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		
		List<String> allPluginsList = new java.util.ArrayList<>();
		com.ashera.codegen.pojo.Plugins plugins = (com.ashera.codegen.pojo.Plugins) unmarshaller
				.unmarshal(new java.io.FileInputStream("jumbo/plugin.xml"));
		for (Plugin plugin : plugins.getPlugin()) {
			File pluginDir = new File(baseLocation + plugin.getName());
			pluginDir.mkdir();

			for (Copy copy : plugin.getCopy()) {
				File srcDir = new File(copy.getFrom());
				if (srcDir.isFile()) {
					FileUtils.copyFileToDirectory(srcDir, new File(pluginDir, copy.getTo()));
				} else {
					FileUtils.copyDirectory(srcDir, new File(pluginDir, copy.getTo()));
				}
			}

			if (!plugin.getCopy()[0].getTo().equals("")) {
				// generate jumbo plugin xml
				File srcFile = new File(pluginDir, plugin.getCopy()[0].getTo() + "/package.json");
				if (srcFile.exists()) {
					FileUtils.copyFile(srcFile, new File(pluginDir, "package.json"));
				}

				// combine all plugin xmls
				StringBuffer str = new StringBuffer();
				int i = 0;
				for (Copy copy : plugin.getCopy()) {
					File pluginXml = new File(pluginDir, copy.getTo() + "/plugin.xml");

					if (!pluginXml.exists()) {
						continue;
					}
					String pluginXmlStr = FileUtils.readFileToString(pluginXml);
					pluginXmlStr = pluginXmlStr.replace("src=\"", "src=\"" + copy.getTo() + "/");

					if (copy.getReplaceString() != null && copy.getReplaceString().length > 0) {
						for (ReplaceString replaceString : copy.getReplaceString()) {
							pluginXmlStr = pluginXmlStr.replace(replaceString.getName(), replaceString.getReplace());
						}
					}

					if (i == 0) {
						str.append(pluginXmlStr);
					} else {
						String platformXml = pluginXmlStr.substring(pluginXmlStr.indexOf("<platform"),
								pluginXmlStr.indexOf("</platform>") + "</platform>".length());
						String platformStartTag = "<platform name=\"android\">";
						String platformEndTag = "</platform>";
						boolean handled = false;
						if (str.indexOf(platformStartTag) != -1) {
							int index = platformXml.indexOf(platformStartTag);
							if (index != -1) {
								platformXml = platformXml.replace(platformStartTag, "");
								platformXml = platformXml.replace(platformEndTag, "");

								int strStartTagIndex = str.indexOf(platformStartTag);
								int endTagIndex = str.substring(strStartTagIndex).indexOf(platformEndTag);
								str = str.insert(strStartTagIndex + endTagIndex, platformXml);
								handled = true;
							}
						}

						if (!handled) {
							platformStartTag = "<platform name=\"ios\">";
							platformEndTag = "</platform>";
							handled = false;
							if (str.indexOf(platformStartTag) != -1) {
								int index = platformXml.indexOf(platformStartTag);
								if (index != -1) {
									platformXml = platformXml.replace(platformStartTag, "");
									platformXml = platformXml.replace(platformEndTag, "");

									int strStartTagIndex = str.indexOf(platformStartTag);
									int endTagIndex = str.substring(strStartTagIndex).indexOf(platformEndTag);
									str = str.insert(strStartTagIndex + endTagIndex, platformXml);
									handled = true;
								}
							}
						}

						if (!handled) {
							platformStartTag = "<platform name=\"swt\">";
							platformEndTag = "</platform>";
							handled = false;
							if (str.indexOf(platformStartTag) != -1) {
								int index = platformXml.indexOf(platformStartTag);
								if (index != -1) {
									platformXml = platformXml.replace(platformStartTag, "");
									platformXml = platformXml.replace(platformEndTag, "");

									int strStartTagIndex = str.indexOf(platformStartTag);
									int endTagIndex = str.substring(strStartTagIndex).indexOf(platformEndTag);
									str = str.insert(strStartTagIndex + endTagIndex, platformXml);
									handled = true;
								}
							}
						}

						if (!handled) {
							platformStartTag = "<platform name=\"browser\">";
							platformEndTag = "</platform>";
							handled = false;
							if (str.indexOf(platformStartTag) != -1) {
								int index = platformXml.indexOf(platformStartTag);
								if (index != -1) {
									platformXml = platformXml.replace(platformStartTag, "");
									platformXml = platformXml.replace(platformEndTag, "");

									int strStartTagIndex = str.indexOf(platformStartTag);
									int endTagIndex = str.substring(strStartTagIndex).indexOf(platformEndTag);
									str = str.insert(strStartTagIndex + endTagIndex, platformXml);
									handled = true;
								}
							}
						}

						if (!handled) {
							str = str.insert(str.lastIndexOf(platformEndTag) + platformEndTag.length(),
									"\n  " + platformXml + "\n");
						}
					}
					
					i++;

				}
				
				String pluginXmlStr = str.toString();
				Pattern pattern = Pattern.compile("\\sid\\s*=\\s*\"([^\"]+)\"");
				Matcher matcher = pattern.matcher(pluginXmlStr);

				if (pluginXmlStr.equals("")) {
					continue;
				}
				if (matcher.find()) {
				    String id = matcher.group(1);
				    System.out.println(id);
				    if (id.equals("http://schemas.android.com/apk/res/android")) {
				    	System.out.println(pluginXmlStr);
				    }
				    if (!plugin.getName().equals("SWTSwitch")) {
				    	allPluginsList.add(String.format("<dependency url=\"https://github.com/AsheraCordova/%s.git\" id=\"%s\" />\r\n    ", plugin.getGitName(), id));
				    }
				} else {
					throw new RuntimeException(pluginDir.toString());
				}
				
				FileUtils.writeStringToFile(new File(pluginDir, "plugin.xml"), pluginXmlStr);
			}

			if (plugin.getReplaceString() != null) {
				for (ReplaceString replaceString : plugin.getReplaceString()) {
					System.out.println(
							"replacing : " + baseLocation + plugin.getName() + "/" + replaceString.getTarget());
					String str = FileUtils
							.readFileToString(new File(baseLocation + plugin.getName(), replaceString.getTarget()));
					str = str.replace(replaceString.getName(), replaceString.getReplace());
					FileUtils.writeStringToFile(new File(baseLocation + plugin.getName(), replaceString.getTarget()),
							str);
				}
			}
		}

		File f = new File("D:\\Java\\github_ashera");
		addGitignoreEntry(f);
		J2ObjcPrefixCodeGen.generateClassPathFile("D:\\Java\\github_ashera", "D:\\Java\\github_ashera\\Custom\\src\\main\\java");
		
		File allPlugins = new File(baseLocation + "AsheraCordovaPlugin/plugin.xml");
		String pluginXmlFile = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"
				+ "<plugin xmlns=\"http://www.phonegap.com/ns/plugins/1.0\"\r\n"
				+ "        id=\"com.ashera.cordova\"\r\n"
				+ "        version=\"1.0\">\r\n"
				+ "\r\n"
				+ "  <name>AsheraCordovaPlugin</name>\r\n"
				+ "\r\n"
				+ "  <engines>\r\n"
				+ "    <engine name=\"cordova\" version=\">=3.4.0\"/>\r\n"
				+ "  </engines>\r\n"
				+ "  \r\n"
				+ "  <platform name=\"android\">\r\n"
				+ "    %s"
				+ "\r\n"
				+ "  </platform>\r\n"
				+ "  <platform name=\"browser\">\r\n"
				+ "    %s"				
				+ "\r\n"
				+ "  </platform>\r\n"
				+ "  <platform name=\"ios\">\r\n"
				+ "    %s"    
				+ "\r\n"
				+ "  </platform>\r\n"
				+ "  <platform name=\"swt\">\r\n"
				+ "    %s"
				+ "\r\n"
				+ "  </platform>\r\n"
				+ "</plugin>";
		
		Arrays.stream(new File(baseLocation).listFiles())
				.filter(file -> file.isDirectory() && file.getName().startsWith("cordova-plugin-"))
				.forEach((file) -> {
					allPluginsList.add(String.format("<dependency url=\"https://github.com/AsheraCordova/%s.git\" id=\"%s\" />\r\n    ", file.getName(), file.getName()));
				});
		allPluginsList.add("<dependency id=\"cordova-plugin-sqlite-2\" version=\"^1.0.6\" />\r\n    ");
		Collections.sort(allPluginsList, (a, b) -> {
			String top = "Core.git";
			if (a.contains(top) && !b.contains(top)) return -1;
		    if (!a.contains(top) && b.contains(top)) return 1;
			return a.compareTo(b);
		});
		String allPluginsStr = String.join("", allPluginsList);
		FileUtils.writeStringToFile(allPlugins, String.format(pluginXmlFile, allPluginsStr, allPluginsStr, allPluginsStr, allPluginsStr));
	}

	private static void addGitignoreEntry(File f) throws IOException {
		File[] child = f.listFiles();
		
		for (File file : child) {
			File gitIgnore = new File(file, ".gitignore");
			
			if (gitIgnore.exists()) {
				String str = org.apache.commons.io.FileUtils.readFileToString(gitIgnore);
				if (str.indexOf(".settings/") == -1) {
					System.out.println(file + "str");
					org.apache.commons.io.FileUtils.writeStringToFile(gitIgnore, str + ".settings/\n");
				}
			}
		}
	}
	
	
}
