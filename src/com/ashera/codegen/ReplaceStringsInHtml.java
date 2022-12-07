package com.ashera.codegen;

import java.io.File;

public class ReplaceStringsInHtml {
	public static void main(String[] args) throws Exception{
		File f = new File("D:\\Java\\git\\core-widget_library\\code_generator\\documentation");
		String footer = org.apache.commons.io.FileUtils.readFileToString(new File(f, "footer.xhtml"));
		String menu = org.apache.commons.io.FileUtils.readFileToString(new File(f, "menu.xhtml"));
		java.util.ArrayList<File> listFiles = new java.util.ArrayList<>(java.util.Arrays.asList(f.listFiles()));
		listFiles.add(new File("D:\\Java\\github_ashera\\Documentation\\ecommerceapp\\index.html"));
		listFiles.add(new File("D:\\Java\\github_ashera\\Documentation\\playground\\index.html"));
		listFiles.add(new File("D:\\Java\\github_ashera\\Documentation\\hello\\index.html"));
		listFiles.add(new File("D:\\Java\\github_ashera\\Documentation\\tradeapp\\index.html"));
		for (File htmlFile : listFiles) {
			if (htmlFile.getName().endsWith(".html")) {
				String html = org.apache.commons.io.FileUtils.readFileToString(htmlFile);
				int start = html.indexOf("<footer");
				int end = html.indexOf("</footer>");
				
				if (start != -1 && end != -1) {
					html = html.substring(0, start) + footer + html.substring(end + "</footer>".length());
				}
				
				
				int startmenu = html.indexOf("<div class=\"sidebar left bg-defoult\" style=\"width: 100%\">");
				int endMenu = html.indexOf("</div><!--//doc-sidebar-->");
				
				if (startmenu != -1 && endMenu != -1) {
					html = html.substring(0, startmenu) + menu + html.substring(endMenu + "</div><!--//doc-sidebar-->".length());
					html = html.replace("<li><a href=\"" + htmlFile.getName(), "<li class=\"active\"><a href=\"" + htmlFile.getName()); 
					
					if (htmlFile.getName().startsWith("01")) {
						html = html.replace("essentials collapsed\" aria-expanded=\"false\"", "essentials active\" aria-expanded=\"true\"");
						html = html.replace("essentials sub-menu collapse", "show sub-menu");
					} else if (htmlFile.getName().startsWith("02")) {
						html = html.replace("advanced collapsed\" aria-expanded=\"false\"", "advanced active\" aria-expanded=\"true\"");
						html = html.replace("advanced sub-menu collapse", "show sub-menu");
					} else {
						html = html.replace("dashboard collapsed\" aria-expanded=\"false\"", "dashboard active\" aria-expanded=\"true\"");
						html = html.replace("dashboard sub-menu collapse", "show sub-menu");
					}
					
				}
				org.apache.commons.io.FileUtils.writeStringToFile(htmlFile, html);

			}
		}
	}
}
