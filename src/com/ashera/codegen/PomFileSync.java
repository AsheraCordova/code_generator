package com.ashera.codegen;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PomFileSync extends CodeGenBase {

	public static void main(String[] args) throws IOException {

		for (String name : listFilesUsingJavaIO("plugininfo/pomsync")) {
			PomFileSync gen = new PomFileSync();
			Properties properties;
			properties = new Properties();
			properties.load(new FileInputStream("plugininfo/pomsync/" + name));
			gen.copyFiles(properties, true);

			for (int i = 0; i < 100; i++) {
				if (properties.containsKey("update.files." + i)) {
					String[] keyStr = properties.getProperty("update.files." + i).split("\\:");
					String code = readFileToString(new File(keyStr[0]));
					writeOrUpdateXmlFile(code, keyStr[1], false, new HashMap<>(), keyStr[2].split(","));

				}
			}
		}
	}

}
