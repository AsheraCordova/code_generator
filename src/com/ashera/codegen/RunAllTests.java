package com.ashera.codegen;

public class RunAllTests {
	public static void main(String[] args) throws Exception{
		CodeGenFromHtml.main(args);
//		CordovaLibCopyFiles.main(args);
		J2ObjcPrefixCodeGen.main(args);
		LayoutDependencyGenerator.main(args);
		PluginPathGen.main(args);
		ResourcesSupplierGen.main(args);
		CodeGenPluginInvoker.main(args);
		CopyFilesToDemoGen.main(args);
		
	}
}
