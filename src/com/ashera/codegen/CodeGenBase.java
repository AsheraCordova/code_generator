package com.ashera.codegen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class CodeGenBase {
	//read file content into a string
	public static String readFileToString(File filePath) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		System.out.println(filePath.getAbsolutePath());
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
 
		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		} 
 
		reader.close();
 
		return  fileData.toString();	
	}
	

	public static void writeOrUpdateFile(String code, String pathname, HashMap<String, String> codeCopyMap)
			throws IOException {
		String[] keyWords = {"imports", "body", "staticinit"};
		writeOrUpdateFile(code, pathname, false, codeCopyMap, keyWords);
	}

	public static void writeOrUpdateFile(String code, String pathname, 
			String... keyWords) throws IOException { 
		writeOrUpdateFile(code, pathname, false, null, keyWords);
	}

	public static void writeOrUpdateFile(String code, String pathname, boolean force, HashMap<String, String> codeCopyMap,
			String... keyWords) throws IOException {
		
		writeOrUpdateFileInternal(code, pathname, force, codeCopyMap, "start - ", "end - ", false, keyWords);		
		
	}
	
	public static void writeOrUpdateXmlFile(String code, String pathname, boolean force, HashMap<String, String> codeCopyMap,
			String... keyWords) throws IOException {
		
		writeOrUpdateFileInternal(code, pathname, force, codeCopyMap, "<!-- start ", " end -->", true, keyWords);		
		
	}


	private static void writeOrUpdateFileInternal(String code, String pathname, boolean force,
			HashMap<String, String> codeCopyMap, String startPrefix, String endPrefix, boolean useSuffix, String... keyWords)
			throws IOException {
		File file = new File(pathname);
		if (!file.exists() || force) {
			file.getParentFile().mkdirs();
		    writeToFile(file, code);
		} else {
			String originalFile = readFileToString(file);
			String finalStr = "";
			for (int j = 0; j < keyWords.length; j++) {
		    	String keyword = keyWords[j];

				String startKeyWord = startPrefix + keyword;
		    	String endKeyWord = endPrefix + keyword;
		    	
		    	if (useSuffix) {
		    		endKeyWord = keyword + endPrefix;
		    	}

				
				int startOrig = originalFile.indexOf(startKeyWord);
				int endOrig = originalFile.indexOf(endKeyWord);
		    	int  startFinal = code.indexOf(startKeyWord);
		    	int endFinal = code.indexOf(endKeyWord);
		    	if (startOrig != -1) {
		        	finalStr = originalFile.substring(0, startOrig) +
		        			code.substring(startFinal, endFinal) +
		        			originalFile.substring(endOrig, originalFile.length());
		        	originalFile = finalStr;
		    	} else {
		    		finalStr = originalFile;
		    	}
			}
			if (codeCopyMap != null) {
				Iterator<String> keys = codeCopyMap.keySet().iterator();
				while (keys.hasNext()) {
					String key = keys.next();
					String start = startPrefix + key;
					String end = endPrefix + key;
			    	if (useSuffix) {
			    		end = key + endPrefix;
			    	}
					
					int  startFinal = finalStr.indexOf(start) + start.length();
			    	int endFinal = finalStr.indexOf(end) - 2;
			    	if (startFinal > endFinal) {
			    	    throw new RuntimeException();
			    	}
					finalStr = finalStr.substring(0, startFinal) + codeCopyMap.get(key) +finalStr.substring(endFinal, finalStr.length());
				}
			}
			
		    writeToFile(file, finalStr);
		}
	}


    public static void writeToFile(File file, String finalStr) throws IOException {
    	File parentFile = file.getParentFile();
    	if (parentFile != null) {
    		parentFile.mkdirs();
    	}
        FileWriter fis = new FileWriter(file);
        fis.write(finalStr);
        fis.close();
    }
	public static void writeOrUpdateFile(String code, String pathname, boolean force,
			String... keyWords) throws IOException {
		writeOrUpdateFile(code, pathname, force, null, keyWords);
	}

	public static File readHttpUrlAsString(String urlStr, String cacheFileName) {
		String baseDir = getCPDir() + "cache/";
		return readHttpUrlAsString(urlStr, cacheFileName, baseDir, true);
	}

	public static void deleteCacheFile(String cacheFileName) {
		String fileAbsPath = getCPDir() + "cache/" + cacheFileName;
		File file = new File(fileAbsPath);
		if (file.exists()) {
			boolean status = file.delete();
			System.out.println(status + " " + file);
		}
	}

	public static File readHttpUrlAsString(String urlStr, String cacheFileName, String baseDir, boolean decode) {
		try {
			
			File file = new File(baseDir + cacheFileName);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			if (!file.exists()) {
				System.out.println(urlStr);
				java.net.URL url = new java.net.URL(urlStr);
				java.net.URLConnection con = url.openConnection();
				java.io.InputStream in = con.getInputStream();
				String encoding = con.getContentEncoding();
				encoding = encoding == null ? "UTF-8" : encoding;
				String body = toString(in, encoding);
				if (decode) {
					try {
						body = new String(java.util.Base64.getDecoder().decode(body.getBytes("UTF-8")), "UTF-8");
					} catch (Exception e) {
					}
				}

				java.io.FileOutputStream fos = new java.io.FileOutputStream(file);
				fos.write(body.getBytes("UTF-8"));
				fos.close();
			}
			return file;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    public static long copyLarge(Reader input, Writer output) throws IOException {
        char[] buffer = new char[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static String toString(InputStream input, String encoding)
            throws IOException {
        StringWriter sw = new StringWriter();
        copy(input, sw, encoding);
        return sw.toString();
    }
    
    public static void copy(InputStream input, Writer output, String encoding)
            throws IOException {
        
        InputStreamReader in = new InputStreamReader(input, encoding);
        copy(in, output);
    }
    
    public static int copy(Reader input, Writer output) throws IOException {
        long count = copyLarge(input, output);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }
    
    public void copyFiles(final Properties config, boolean replaceAndroid) throws IOException {
		for (int i = 0; i < 100; i++) {
			if (config.containsKey("copy.files." + i)) {
				String keyStr = config.getProperty("copy.files." + i);
				
				System.out.println(keyStr);

				File sourceFileLoc = new File(keyStr.substring(0, keyStr.lastIndexOf(":")));
				File copyLoc = new File(keyStr.substring(keyStr.lastIndexOf(":") + 1));
				if (sourceFileLoc.isFile()) {
					copyFileContent(sourceFileLoc, copyLoc, replaceAndroid, config);
				} else if (sourceFileLoc.isDirectory()) {
					File[] listOfFiles = sourceFileLoc.listFiles();
					for (File file : listOfFiles) {
						if (file.isFile()) {
							copyFileContent(file, new File(copyLoc.getAbsolutePath() + "/" + file.getName()), replaceAndroid, config);
						}
					}
				} else {
					throw new RuntimeException("unknown file : " + sourceFileLoc.getAbsolutePath());
				}
			} else {
				break;
			}
		}
	}
    
	public void copyFileContent(File sourceFileLoc, File copyLoc, boolean replaceAndroid)
			throws IOException {
		copyFileContent(sourceFileLoc, copyLoc, replaceAndroid, null);
	}


	public void copyFileContent(File sourceFileLoc, File copyLoc, boolean replaceAndroid, Properties config) throws IOException {
		String fileContent = readFileToString(sourceFileLoc);						
		copyLoc.getParentFile().mkdirs();
		BufferedWriter b1 = new BufferedWriter(new FileWriter(copyLoc));
		if (replaceAndroid) {
			fileContent = fileContent.replaceAll(" android\\.", " r.android.");
			fileContent = fileContent.replaceAll("\\(android\\.", "(r.android.");
			fileContent = fileContent.replaceAll("@android\\.", "@r.android.");
			fileContent = fileContent.replaceAll(" com\\.android\\.", " r.com.android.");
			fileContent = fileContent.replaceAll(" libcore\\.", " r.libcore.");
			fileContent = fileContent.replaceAll("@Deprecated", "//@Deprecated");
			
//			fileContent = fileContent.replaceAll(" com\\.google\\.", " r.com.google.");
		}

		if (config != null) {
			for (int i = 0; i < 100; i++) {
	            if (config.containsKey("replacestrings." + i)) {
	                String[] replaceStrs = config.getProperty("replacestrings." + i).split("~");
	                fileContent = fileContent.replaceAll(replaceStrs[0], replaceStrs[1]);
	            } else {
	                break;
	            }
			}
			
			 if (config.containsKey(sourceFileLoc.getName() + ".deletemethods")) {
				 String[] methods = config.getProperty(sourceFileLoc.getName() + ".deletemethods").split(",");
				 for (String method : methods) {
					 Scanner scanner = new Scanner(fileContent);
					 List<String> lines =new ArrayList<>();
					 int index = 0;
					 while (scanner.hasNextLine()) {
					   String line = scanner.nextLine();
					   
					   // process the line
					   if (line.indexOf(method) != -1)  {
						   if (lines.get(index -1).indexOf("@Override") != -1) {
							   lines.remove(index -1);
						   }
						   long count = line.chars().filter(ch -> ch == '{').count();
						   long initCount = count;
						   while (initCount == 0 || count != 0) {
							   String nextLine = scanner.nextLine();
							   long countOfOpenBackets = nextLine.chars().filter(ch -> ch == '{').count();
							   long countOfCloseBackets = nextLine.chars().filter(ch -> ch == '}').count();
							   count += countOfOpenBackets - countOfCloseBackets;
							   initCount =  1;
							   
						   }
					   } else {
						   lines.add(line);
					   }
					   index++;
					 }
					 
					 
					 fileContent = String.join("\n", lines);
					 scanner.close();
				}
			 }
			for (int i = 0; i < 100; i++) {
	            if (config.containsKey("replacestrings." + sourceFileLoc.getName().toLowerCase() + "." + i)) {
	                String[] replaceStrs = config.getProperty("replacestrings." + sourceFileLoc.getName().toLowerCase() + "." + i).split("~");
	                fileContent = fileContent.replaceAll(replaceStrs[0], replaceStrs[1]);
	            } else if (config.containsKey("replacestrings." + copyLoc.getName().toLowerCase() + "." + i)) {
	            	String[] replaceStrs = config.getProperty("replacestrings." + copyLoc.getName().toLowerCase() + "." + i).split("~");
	                fileContent = fileContent.replaceAll(replaceStrs[0], replaceStrs[1]);
 	            }else {
	                break;
	            }
			}
			
			for (int i = 0; i < 100; i++) {
	            if (config.containsKey("replacestrings." + getPackage(copyLoc) + "." + i)) {
	                String[] replaceStrs = config.getProperty("replacestrings." + getPackage(copyLoc) + "." + i).split("~");
	                fileContent = fileContent.replaceAll(replaceStrs[0], replaceStrs[1]);
	            } else {
	                break;
	            }
			}
		}
		b1.write(fileContent);
		b1.close();
	}


	public String getPackage(File sourceFileLoc) {
		String packageName = sourceFileLoc.getAbsolutePath().toLowerCase().replaceAll("\\\\", ".").replaceAll("/", ".");
		int indexOf = packageName.indexOf("src.main.java.");
		String finalPackageName = packageName.substring(indexOf + "src.main.java.".length());
		return finalPackageName;
	}
	
    public String camelCase(String in) {
        if (in == null || in.length() < 1) { return ""; } //validate in
        String out = "";
        for (String part : in.split("_")) {
            if (part.length() < 1) { //validate length
                continue;
            }
            out += part.substring(0, 1).toUpperCase();
            if (part.length() > 1) { //validate length
                out += part.substring(1);
            }
        }
        out = out.substring(0, 1).toLowerCase() + out.substring(1);
        return out;
    }
    
	public static String getBase64FileName(String url) {
		return Base64.getEncoder().encodeToString(url.getBytes());
	}
	
	public static Set<String> listFilesUsingJavaIO(String dir) {
	    File[] files = new File(dir).listFiles();
	    if (files == null) {
	    	return null;
	    }
		return Stream.of(files)
	      .filter(file -> !file.isDirectory())
	      .map(File::getName)
	      .collect(Collectors.toSet());
	}
	
	public static String getCPDir() {
		String projectBaseDir = System.getProperty("baseDir");
		if (projectBaseDir == null) {
			return "";
		}
		
		return projectBaseDir + "/codepoacher/";
	}



	public static CompilationUnit getCU(File file) throws IOException {
		String str = readFileToString(file);
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(str.toCharArray());
		parser.setCompilerOptions(getCompilerOptions());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);

		final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		return cu;
	}
	
	private static Map getCompilerOptions() {
		Map defaultOptions = new HashMap();
		defaultOptions.put(JavaCore.COMPILER_LOCAL_VARIABLE_ATTR,
				JavaCore.GENERATE);
		defaultOptions.put(JavaCore.COMPILER_PB_UNUSED_PRIVATE_MEMBER,
				JavaCore.IGNORE);
		defaultOptions.put(JavaCore.COMPILER_PB_LOCAL_VARIABLE_HIDING,
				JavaCore.WARNING);
		defaultOptions.put(JavaCore.COMPILER_PB_FIELD_HIDING, JavaCore.WARNING);
		defaultOptions.put(
				JavaCore.COMPILER_PB_POSSIBLE_ACCIDENTAL_BOOLEAN_ASSIGNMENT,
				JavaCore.WARNING);
		defaultOptions.put(JavaCore.COMPILER_PB_SYNTHETIC_ACCESS_EMULATION,
				JavaCore.WARNING);
		defaultOptions.put(JavaCore.COMPILER_PB_SYNTHETIC_ACCESS_EMULATION,
				JavaCore.WARNING);
		defaultOptions.put(JavaCore.COMPILER_CODEGEN_UNUSED_LOCAL,
				JavaCore.PRESERVE);
		defaultOptions.put(JavaCore.COMPILER_PB_UNNECESSARY_ELSE,
				JavaCore.WARNING);
		defaultOptions.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_7);
		defaultOptions.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM,
				JavaCore.VERSION_1_7);
		defaultOptions.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_7);
		return defaultOptions;
	}
}
