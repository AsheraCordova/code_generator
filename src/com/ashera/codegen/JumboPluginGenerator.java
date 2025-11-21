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

				FileUtils.writeStringToFile(new File(pluginDir, "plugin.xml"), str.toString());
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
