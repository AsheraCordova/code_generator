package com.ashera.codegen;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;

import java.io.*;
import java.util.*;

 
public class LayoutDependencyGenerator extends  CodeGenBase{
		private HashMap<String, ASTNode> tyepdeclarationNodes = new HashMap<>();
		
		
		public void parse(final String key, File file) throws IOException{
			final Properties config = new Properties();
			config.load(new FileInputStream(getCPDir() + "config/" + key + ".config"));
			String copyUrl = config.getProperty("copyto");

			parseAndCreateFile(key, file, config, copyUrl);
			
			copyFiles(config, true);
			
			for (int i = 0; i < 150; i++) {
				String copyFileFromUrl = config.getProperty("copyfilesfromurl." + i);
				
				if (copyFileFromUrl != null) {
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
					
					
					String fileToCopyDest = copyFileFromUrl.substring(copyFileFromUrl.lastIndexOf("java/") + 5, lastIndexOf);
					File destLocation = new File(config.getProperty("copyfilesfromurlDestLocation") + "/" +fileToCopyDest);
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

			
			for (int i = 0; i < 50; i++) {
				String copyFileFromUrl = config.getProperty("dependent.files." + i);
				if (copyFileFromUrl != null) {
					int lastIndexOf = copyFileFromUrl.lastIndexOf("?");
					if (lastIndexOf == -1) {
						lastIndexOf = copyFileFromUrl.length();
					}
					String fileToCopy = copyFileFromUrl.substring(copyFileFromUrl.lastIndexOf("/") + 1, lastIndexOf);
					File sourceFile = readHttpUrlAsString(copyFileFromUrl, fileToCopy + "dependent");
					String fileToCopyDest = copyFileFromUrl.substring(copyFileFromUrl.lastIndexOf("java/") + 5, lastIndexOf);
					File destLocation = new File(config.getProperty("copyfilesfromurlDestLocation") +  fileToCopyDest);
					parseAndCreateFile(sourceFile.getName().toLowerCase().substring(0, sourceFile.getName().lastIndexOf(".")), sourceFile, config, destLocation.getAbsolutePath());

				}
			}
			//System.out.println(cu.toString());
		}


		private void parseAndCreateFile(final String key, File file, final Properties config, String copyUrl)
				throws IOException {
			final CompilationUnit cu = getCU(file);

			cu.accept(new ASTVisitor() {
				
				@Override
				public boolean visit(NormalAnnotation node) {
					node.delete();
					return super.visit(node);
				}
				
				@Override
				public boolean visit(Initializer node) {
					node.delete();
					return super.visit(node);
				}
				
				@Override
				public boolean visit(FieldDeclaration node) {
					String suffix = "fields";
					
					String include = config.getProperty(key + ".include." + suffix).replaceAll("\\s","");
					String exclude = config.getProperty(key + ".exclude." + suffix).replaceAll("\\s","");

					if (exclude.equals("*")) {
						node.delete();
					} else if (!include.equals("*")) {
						String[] includeclasses = include.split(",");
						String[] excludeclasses = exclude.split(",");
						
						String type = "type:" + node.getType().toString();
						Object fragment = node.fragments().get(0);
						String name = "";
						if (fragment instanceof VariableDeclarationFragment) {
							name = ((VariableDeclarationFragment)fragment).getName().toString();
						}
						List<String> excluded = Arrays.asList(excludeclasses);
						List<String> included = Arrays.asList(includeclasses);
						
						if (excluded.contains(name)){
							node.delete();
						} else if (excluded.contains(type)){
							if (!included.contains(name)) {
								node.delete();
							}
						} else if (!included.contains(type)){
							if (!included.contains(name)) {
								node.delete();
							}
						}
					}
					
					return super.visit(node);
				}
				
				@Override
				public boolean visit(VariableDeclarationExpression node) {
//					node.delete();
					return super.visit(node);
				}
				
//				@Override
//				public boolean visit(VariableDeclarationFragment node) {
//					node.delete();
//					// TODO Auto-generated method stub
//					return super.visit(node);
//				}
				@Override
				public boolean visit(MarkerAnnotation node) {
					node.delete();
					return super.visit(node);
				}
				@Override
				public boolean visit(VariableDeclarationStatement node) {
					//System.out.println(node.toString());
					return super.visit(node);
				}
				@Override
				public boolean visit(SingleMemberAnnotation node) {
					node.delete();
					return super.visit(node);
				}
				@Override
				public boolean visit(Block node) {
					return super.visit(node);
				}
				@Override
				public boolean visit(TypeDeclaration node) {
					String property = config.getProperty(key + ".include.interfaces");
					String include = "";
					if (property != null) {
						include = property.replaceAll("\\s","");
					}
					
					if (include.equals("")) {
						node.superInterfaceTypes().clear();
					} else {
						if (!include.equals("*")) {
							List<String> interfaces = Arrays.asList(include.split(","));
							Iterator iterator = node.superInterfaceTypes().iterator();
							while (iterator.hasNext()) {
								Type type = (Type) iterator.next();
								if (!interfaces.contains(type.toString())) {
									iterator.remove();
								}
							}

						}
					}
					
					String className = node.getName().toString();
					boolean  deleted =retainCode(key, config, node, className, "classes");
					tyepdeclarationNodes.put(className.toLowerCase(), node);

					if (deleted) {
						return false;
					}
					return super.visit(node);
				}

				private boolean retainCode(final String key,
						final Properties config, ASTNode node,
						String name, String suffix) {
					String include = config.getProperty(key + ".include." + suffix).replaceAll("\\s","");
					String exclude = config.getProperty(key + ".exclude." + suffix).replaceAll("\\s","");
					boolean deleted = false;
					if (exclude.equals("*")) {
						node.delete();
						deleted = true;
					} else if (!include.equals("*")) {
						String[] includeclasses = include.split(",");
						String[] excludeclasses = exclude.split(",");
						
						
						if (!exclude.equals("")) {
							if (Arrays.asList(excludeclasses).contains(name)){
								node.delete();
								deleted = true;
							}
						} else {
							if (!Arrays.asList(includeclasses).contains(name)){
								node.delete();
								deleted = true;
							}	
						}						
					} else {
						String[] excludeclasses = exclude.split(",");
						if (!exclude.equals("")) {
							if (Arrays.asList(excludeclasses).contains(name)){
								node.delete();
								deleted = true;
							}
						} 
					}
					
					return deleted;
				}
				
				public boolean visit(org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration node) {
					node.delete();
					return false;
				}
				
				@Override
				public boolean visit(ImportDeclaration node) {
					String className = node.getName().toString();
					retainCode(key, config, node, className, "imports");
					
					return super.visit(node);
				}
				@Override
				public boolean visit(MethodDeclaration node) {	
					
					if (node.getParent() instanceof TypeDeclaration) {
						String declClass = ((TypeDeclaration)node.getParent()).getName().toString().toLowerCase();
						String include = config.getProperty(declClass + ".include.methods").replaceAll("\\s","");
						String[] includeclasses = include.split(",");
						String type = "";
						if (declClass.equals("recyclebin")) {
							System.out.println(node);
						}
						
		                for (Object parameter : node.parameters()) {
		                    VariableDeclaration variableDeclaration = (VariableDeclaration) parameter;
		                    String def = "-" + variableDeclaration.getStructuralProperty(SingleVariableDeclaration.TYPE_PROPERTY).toString();
		                    
		                    if (def.indexOf("<") != -1) {
		                    	def = def.substring(0, def.indexOf("<"));
		                    }
							type += def;
		                    for (int i = 0; i < variableDeclaration.getExtraDimensions(); i++) {
		                        type += "[]";
		                    }
		                }

		                if (node.parameters().isEmpty() && node.isConstructor()) {
		                	type += "-default";
		                }
		                if (!type.equals("")) {
		                	//System.out.println(node.getName().toString() + type + " " + includeclasses);
		                	if (Arrays.asList(includeclasses).contains(node.getName().toString() + type)) {
		                		return super.visit(node);
		                	}
		                }
						retainCode(declClass, config, node, node.getName().toString(), "methods");
						
						
					} else if (node.getParent() instanceof AnonymousClassDeclaration) {
//						String declClass = ((AnonymousClassDeclaration)node.getParent()).getClass().getName();
//						System.out.println("11111" + node.getParent().getClass() );
//						node.delete();
					}
					else {
						node.delete();
					}

					return super.visit(node);
				}
				@Override
				public boolean visit(Javadoc node) {
					node.delete();
					return super.visit(node);
				}
				
				@Override
				public boolean visit(CompilationUnit node) {
					return super.visit(node);
				}
				
				@Override
				public boolean visit(AnonymousClassDeclaration node) {
//					node.delete();
					return super.visit(node);
				}
				@Override
				public boolean visit(AnnotationTypeDeclaration node) {
					node.delete();
					return super.visit(node);
				}
//				public boolean visit(VariableDeclarationFragment node) {
//					SimpleName name = node.getName();
//					this.names.add(name.getIdentifier());
//					System.out.println("Declaration of '" + name + "' at line"
//							+ cu.getLineNumber(name.getStartPosition()));
//					return false; // do not continue 
//				}
//	 
//				public boolean visit(SimpleName node) {
//					if (this.names.contains(node.getIdentifier())) {
//						System.out.println("Usage of '" + node + "' at line "
//								+ cu.getLineNumber(node.getStartPosition()));
//					}
//					return true;
//				}
			});
			
			File codeFile = new File(getCPDir() + "config/" + key + ".code");
			if (codeFile.exists()) {
				CompilationUnit tempCU = getCU(codeFile);
				if (cu.types().size() > 0) {
					final List<Object> bodyDecls = ((TypeDeclaration)cu.types().get(0)).bodyDeclarations();
	
					tempCU.accept(new ASTVisitor() {
						
						@Override
						public boolean visit(FieldDeclaration node) {
							bodyDecls.add(ASTNode.copySubtree(cu.getAST(), node));
							return super.visit(node);
						}
						public boolean visit(MethodDeclaration node) {	
							bodyDecls.add((MethodDeclaration)ASTNode.copySubtree(cu.getAST(), node));
							return false;
						}
						
						@Override
						public boolean visit(TypeDeclaration node) {						
							if (node.getName().toString().toLowerCase().equals(key)) {
								return super.visit(node); 
							}
							bodyDecls.add(ASTNode.copySubtree(cu.getAST(), node));
							return false;
						}
					});
				}
			}
			
			File f = new File(copyUrl);
			
			Object renamedFile = config.get("file.rename." + f.getName().toLowerCase());
			if (renamedFile != null) {
				f = new File(f.getParentFile().getAbsolutePath() + "/" + renamedFile.toString());
			}
			
			f.getParentFile().mkdirs();
			BufferedWriter b = new BufferedWriter(new FileWriter(f));
			String string = cu.toString();
			
			string = string.replaceAll(" android\\.icu", " com.ibm.icu");
			string = string.replaceAll(" android\\.", " r.android.");
			string = string.replaceAll("=android\\.", "=r.android.");
			string = string.replaceAll("\\(android\\.", "(r.android.");
			string = string.replaceAll("@android\\.", "@r.android.");
			string = string.replaceAll(" com\\.android\\.", " r.com.android.");
			string = string.replaceAll(" libcore\\.", " r.libcore.");
//			string = string.replaceAll(" com\\.google\\.", " r.com.google.");
			for (int i = 0; i < 100; i++) {
	            if (config.containsKey("replacestrings." + i)) {
	                String[] replaceStrs = config.getProperty("replacestrings." + i).split("~");
	                string = string.replaceAll(replaceStrs[0], replaceStrs[1]);
	            } else {
	                break;
	            }
			}
			
			for (int i = 0; i < 100; i++) {
	            if (config.containsKey("replacestrings." + f.getName().toLowerCase() + "." + i)) {
	                String[] replaceStrs = config.getProperty("replacestrings." + f.getName().toLowerCase() + "." + i).split("~");
	                string = string.replaceAll(replaceStrs[0], replaceStrs[1]);
	            } else if (config.containsKey("replacestrings." + getPackage(f) + "." + i)) {
	                String[] replaceStrs = config.getProperty("replacestrings." + getPackage(f) + "." + i).split("~");
	                string = string.replaceAll(replaceStrs[0], replaceStrs[1]);
	            } else {
	                break;
	            }
			}
			
			b.write(string);
			b.close();
		}
		

		private static CompilationUnit getCU(File file) throws IOException {
			String str = readFileToString(file);
			ASTParser parser = ASTParser.newParser(AST.JLS8);
			parser.setSource(str.toCharArray());
			parser.setCompilerOptions(getCompilerOptions());
			parser.setKind(ASTParser.K_COMPILATION_UNIT);

			final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
			return cu;
		}
	 
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

	 
		public static void main(String[] args) throws IOException {
			Properties properties = new Properties();
			properties.load(new FileInputStream(getCPDir() + "config/files.properties"));
			LayoutDependencyGenerator gen = new LayoutDependencyGenerator();
			Set<Object> keys = properties.keySet();
			for (Object key : keys) {
			   
				gen.parse(key.toString(), readHttpUrlAsString(properties.getProperty(key.toString()), key.toString() + ".java"));
			}	
			
			gen.syncFiles();
		}
		


		private void syncFiles() throws IOException,
				FileNotFoundException {
			Properties properties;
			properties = new Properties();
			properties.load(new FileInputStream(getCPDir() + "config/copyfiles.properties"));
			copyFiles(properties, true);
		}
		
		private static Map getCompilerOptions() {
			Map defaultOptions = new HashMap();
			defaultOptions.put(JavaCore.COMPILER_LOCAL_VARIABLE_ATTR, JavaCore.GENERATE);
			defaultOptions.put(JavaCore.COMPILER_PB_UNUSED_PRIVATE_MEMBER, JavaCore.IGNORE);
			defaultOptions.put(JavaCore.COMPILER_PB_LOCAL_VARIABLE_HIDING, JavaCore.WARNING);
			defaultOptions.put(JavaCore.COMPILER_PB_FIELD_HIDING, JavaCore.WARNING);
			defaultOptions.put(JavaCore.COMPILER_PB_POSSIBLE_ACCIDENTAL_BOOLEAN_ASSIGNMENT, JavaCore.WARNING);
			defaultOptions.put(JavaCore.COMPILER_PB_SYNTHETIC_ACCESS_EMULATION, JavaCore.WARNING);
			defaultOptions.put(JavaCore.COMPILER_PB_SYNTHETIC_ACCESS_EMULATION, JavaCore.WARNING);
			defaultOptions.put(JavaCore.COMPILER_CODEGEN_UNUSED_LOCAL, JavaCore.PRESERVE);
			defaultOptions.put(JavaCore.COMPILER_PB_UNNECESSARY_ELSE, JavaCore.WARNING);
			defaultOptions.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
			defaultOptions.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_8);
			defaultOptions.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
			return defaultOptions;
		}
	
}