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
