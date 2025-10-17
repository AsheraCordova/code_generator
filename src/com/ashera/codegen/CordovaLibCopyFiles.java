package com.ashera.codegen;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class CordovaLibCopyFiles extends CodeGenBase{
	public static void main(String[] args) throws Exception{
		new CordovaLibCopyFiles().syncFiles();
	}
	
	
	public void syncFiles() throws Exception {
		final Properties config = new Properties();
		config.load(new FileInputStream(getCPDir() + "config/cordova.config"));
		copyFiles(config, "copyfilesfromurlDestLocation");
		config.put("replacestrings.2","var PLATFORM = 'swt';~var PLATFORM = 'browser';");
		config.put("replacestrings.3","configXml\\: path.join\\(appRes, 'xml', 'config.xml'\\),~configXml: path.join(this.root, 'config.xml'),");
		config.put("replacestrings.4","Creating Cordova project for the Android platform~Creating Cordova project for the Browser platform");
		config.put("replacestrings.5","fs\\.copySync\\(path.join\\(srcDir, 'build.gradle'\\)~fs.copySync(path.join(srcDir, 'app', 'gradle.properties'), path.join(projectPath, 'app', 'gradle.properties'));//fs1.copySync(path.join(srcDir, 'build.gradle')");
		config.put("replacestrings.6","'merges', 'android'~'merges', 'browser'");
		config.put("replacestrings.7","merges/android~merges/browser");
		
		config.put("replacestrings.create.js.6", "fs\\.copySync\\(path\\.join\\(project_template_dir, 'res'\\), path\\.join\\(app_path, 'res'\\)\\);~fs.copySync(path.join(project_template_dir, 'resources'), path.join(app_path, 'resources'));fs.copySync(path.join(ROOT, '../', '../', 'config.xml'), path.join(project_path, 'config.xml'));");;
		
		copyFiles(config, "copyfilesfromurlDestLocationWeb");
		
	}


	private void copyFiles(final Properties config, String copyfilesfromurlDestLocation) throws IOException {
		String rootUrl = config.getProperty("rooturl");
		for (int i = 0; i < 150; i++) {
			String copyFileFromUrl = config.getProperty("copyfilesfromurl." + i);
			
			if (copyFileFromUrl != null) {
				copyFileFromUrl = rootUrl +  copyFileFromUrl;
				String prefix = "";
				if (copyFileFromUrl.indexOf("#") != -1) {
					String url = copyFileFromUrl.split("#")[0];
					prefix = copyFileFromUrl.split("#")[1] + "/";
					copyFileFromUrl = url;
				}
				int lastIndexOf = copyFileFromUrl.lastIndexOf("?");
				if (lastIndexOf == -1) {
					lastIndexOf = copyFileFromUrl.length();
				}
				String fileToCopy = copyFileFromUrl.substring(copyFileFromUrl.lastIndexOf("/") + 1, lastIndexOf);
				File sourceFile = readHttpUrlAsString(copyFileFromUrl, prefix + fileToCopy);
				
				
				int javaIndex = copyFileFromUrl.lastIndexOf("9.0.0/");
				if (javaIndex == -1) {
					javaIndex = copyFileFromUrl.lastIndexOf("main/");
				}
				String fileToCopyDest = copyFileFromUrl.substring(javaIndex + 6, lastIndexOf);

				File destLocation = new File(config.getProperty(copyfilesfromurlDestLocation) + "/" +fileToCopyDest);
				Object renamedFile = config.get("copyfilesfromurl.file.rename." + destLocation.getName().toLowerCase());
				if (renamedFile != null) {
					destLocation = new File(destLocation.getParentFile().getAbsolutePath() + "/" + renamedFile.toString());
				} else {
					renamedFile = config.get("copyfilesfromurl.file.rename." + getPackage(destLocation));
					if (renamedFile != null) {
						destLocation = new File(destLocation.getParentFile().getAbsolutePath() + "/" + renamedFile.toString());
					}
				}
				
				copyFileContent(sourceFile, destLocation, true, config);
			}
			
		}
	}
}
