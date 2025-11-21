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
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.commonmark.Extension;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.AttributeProvider;
import org.commonmark.renderer.html.AttributeProviderContext;
import org.commonmark.renderer.html.AttributeProviderFactory;
import org.commonmark.renderer.html.HtmlRenderer;

import com.ashera.codegen.pojo.Plugin;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class HtmlToMarkDownGenerator extends CodeGenBase{
	public static void main(String[] args) throws Exception{
		HtmlToMarkDownGenerator t = new HtmlToMarkDownGenerator();
		javax.xml.bind.JAXBContext jaxbContext = javax.xml.bind.JAXBContext
				.newInstance(com.ashera.codegen.pojo.Plugins.class);
		javax.xml.bind.Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		com.ashera.codegen.pojo.Plugins plugins = (com.ashera.codegen.pojo.Plugins) unmarshaller
				.unmarshal(new java.io.FileInputStream("jumbo/plugin.xml"));

		List<Plugin> gitplugins = java.util.Arrays.asList(plugins.getPlugin()).stream().filter((plugin) -> plugin.hasGitName()).sorted((a, b) -> a.getName().toLowerCase().compareTo(b.getName().toLowerCase())).
				collect(java.util.stream.Collectors.toList());
		for (Plugin plugin : gitplugins) {
			//deleteCacheFile(plugin.getName());
			String markdown = readFileToString(readHttpUrlAsString("https://raw.githubusercontent.com/AsheraCordova/"
					+ plugin.getGitName() +"/refs/heads/main/README.md", plugin.getName()));
			Map<String, Object> params = new HashMap<>();
			params.put("markup", t.markdownToHtml(markdown));
			params.put("plugins", gitplugins);
			generateHtmlDoc(params, "documentation/03" + plugin.getName() + ".html", "documentation/plugin_documenation.html"); 
		}
		
		gitplugins = java.util.Arrays.asList(plugins.getPlugin()).stream().filter((plugin) -> !plugin.hasGitName()).sorted((a, b) -> a.getName().toLowerCase().compareTo(b.getName().toLowerCase())).
				collect(java.util.stream.Collectors.toList());
		gitplugins.forEach((p) -> System.out.println(p.getName()));
	}
	
	private static void generateHtmlDoc(Map<String, Object> params, String pathname, String templateName) {
		StringWriter stringWriter = new StringWriter();
		try {
			
			// Load template from source folder

			Template template = new Template("name", new FileReader(new File(templateName)), new Configuration());

			// Console output
			stringWriter = new StringWriter();
			template.process(params, stringWriter);
			stringWriter.flush();
			String projectBaseDir = System.getProperty("basedir");
			if (projectBaseDir == null) {
				projectBaseDir = "";
			}
			writeToFile(new File(projectBaseDir + pathname), stringWriter.toString());
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
	}

	public String markdownToHtml(String markdown) {
		List<Extension> extensions = java.util.Arrays.asList(TablesExtension.create(), AutolinkExtension.create());
		Parser parser = Parser.builder().extensions(extensions).build();
		Node document = parser.parse(markdown);
		return HtmlRenderer.builder().attributeProviderFactory(new AttributeProviderFactory() {
            public AttributeProvider create(AttributeProviderContext context) {
                return new AttributeProvider() {

					@Override
					public void setAttributes(Node node, String tagName, Map<String, String> attributes) {
						 if (tagName.equals("code") || tagName.equals("pre")) {
							 attributes.put("class", "language-markup");
						 }
						 
						 if (tagName.equals("table")) {
							 attributes.put("class", "table table-striped");
						 }
						 
						 if (tagName.equals("a")) {
							 attributes.put("class", "link");
						 }
					}
                	
                };
            }
        }).extensions(extensions).build().render(document);
	}
}
