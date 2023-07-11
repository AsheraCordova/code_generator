package com.ashera.codegen.attributegen;

import static com.ashera.codegen.CodeGenHelper.getApiInt;
import static com.ashera.codegen.CodeGenHelper.getAttributeWithoutNameSpace;
import static com.ashera.codegen.CodeGenHelper.getMethodName;
import static com.ashera.codegen.CodeGenHelper.getMethodParams;
import static com.ashera.codegen.CodeGenHelper.getNameSpace;
import static com.ashera.codegen.CodeGenHelper.getSetMethodFromAttr;
import static com.ashera.codegen.CodeGenHelper.isAndroidAttribute;
import static com.ashera.codegen.CodeGenHelper.isApiElement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ashera.codegen.CodeGenHelper;
import com.ashera.codegen.pojo.CustomAttribute;
import com.ashera.codegen.pojo.ReplaceString;
import com.ashera.codegen.pojo.SourceConfig;
import com.ashera.codegen.pojo.Url;
import com.ashera.codegen.pojo.Widget;
import com.ashera.codegen.pojo.WidgetConfig;

public class AndroidDoc extends com.ashera.codegen.CodeGenBase{
	private WidgetConfig widgetConfig;
	private SourceConfig sourceConfig;
	protected Document getDocument(String url, Url[] parentUrls)
			throws IOException {
		return getDocument(url, parentUrls, null);
	}
	
	protected Document getDocument(String url, Url[] parentUrls, ReplaceString[] replaceStrings)
			throws IOException {
		String content = getContentOfUrls(url, parentUrls);
		if (replaceStrings != null) {
			for (com.ashera.codegen.pojo.ReplaceString replaceString : replaceStrings) {
				content = content.replaceAll(replaceString.getName(),
						replaceString.getReplace());
			}
		}
		return Jsoup.parse(content);
	}

	public String getContentOfUrls(String url, Url[] parentUrls) throws IOException {
		ArrayList<String> parentUrlsStr = new ArrayList<String>();
		ArrayList<String> parentUrlsCacheFile = new ArrayList<String>();
		if (parentUrls != null) {
			for (Url url2 : parentUrls) {
				parentUrlsStr.add(url2.getUrl());
				parentUrlsCacheFile.add(CodeGenHelper.getCacheFileName(widgetConfig, sourceConfig, url2.getId()));
			}
		}
		String[] urls = combine(new String[] {url}, parentUrlsStr.toArray(new String[0]));
		String[] cacheFileNames = combine(new String[] {CodeGenHelper.getCacheFileName(widgetConfig, sourceConfig)}, parentUrlsCacheFile.toArray(new String[0]));
		cacheFiles(urls, cacheFileNames);
		
		String content = readFileContents(url, parentUrls);
		return content;
	}
	
    public String[] combine(String[] a, String[] b){
        int length = a.length + b.length;
        String[] result = new String[length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    private String readFileContents(String url, Url[] parentUrls) throws IOException{
    	StringBuilder data = new StringBuilder("");
    	
    	if (url != null) {
	    	String cacheFileName = CodeGenHelper.getCacheFileName(widgetConfig, sourceConfig);
			String fileContents = readFromCacheFile(cacheFileName);
	    	data.append(fileContents);
    	} else {
    		data.append("<div></div>\n");
    	}
    	if (parentUrls != null) {
	    	for (Url parentUrl : parentUrls) {
	    		String cacheFileName = CodeGenHelper.getCacheFileName(widgetConfig, sourceConfig, parentUrl.getId());
				String parentData = readFromCacheFile(cacheFileName);
				
	    		if (parentUrl.getReplaceStrings() != null) {
		    		for (ReplaceString replaceString : parentUrl.getReplaceStrings()) {
		    			parentData = parentData.replaceAll(replaceString.getName(), replaceString.getReplace());	    					
					}
	    		}
	    		int firstIndex = parentData.indexOf("<div");
	    		int lastIndex = parentData.lastIndexOf("</div>");
	    		data.insert(data.lastIndexOf("</div>") + "</div>".length(), parentData.substring(firstIndex, lastIndex + "</div>".length()));
			}
    	}
    	
    	return data.toString();
    }

	private String readFromCacheFile(String cacheFileName) throws IOException {
		File file = new File("cache/" + cacheFileName);
		String fileContents = "";
		if (file.exists()) {
			fileContents = readFileToString(file);
			fileContents = fileContents.replaceAll("&nbsp;", " ");	
		}
		return fileContents;
	}
	private void cacheFiles(String[] urls, String[] cacheFileNames) throws IOException{
		for (int i = 0; i < urls.length; i++) {
			String url = urls[i];
			if (url != null) {
				File file = new File(getCPDir() + "cache/" + cacheFileNames[i]);
				if (!file.exists()) {
					Document doc = Jsoup.connect(url).maxBodySize(0)
						       .timeout(10000).get();
					String htmlString = doc.html();
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdir();
					}
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(htmlString.getBytes("UTF-8"));
					fos.close();
				}
			}
		}
	}
	
	private String[] getMethodNameAndApiLevel(Document doc, String methodStr) {
		Elements methodElements = doc.select("h3:contains(" + methodStr + ")");
		
		if (methodElements.size() > 0) {
			for (Element element2 : methodElements) {
				Element apiLevelSibmling = element2.nextElementSibling();
				String apiLevelElement = element2.nextElementSibling().text();
				String methodSignature = apiLevelSibmling.nextElementSibling().text();
				if (methodSignature.indexOf((methodStr + " (")) != -1
						|| methodSignature.indexOf((methodStr + "Enabled (")) != -1) {

					if (isApiElement(apiLevelElement)) {
						String apiLevel = getApiInt(apiLevelElement);
						String methodName = apiLevelSibmling.nextElementSibling().text();
						return new String[] { apiLevel, methodName };
					}
				}
			}
		}

		return null;
	}
	

	private String[] getMethodNameAndApiLevelForLayoutAttr(Document doc, String publicSttrName) {
		Elements methodElements = doc.select("h3:contains(" + publicSttrName + ")");
		
		if (methodElements.size() > 0) {
			for (Element element2 : methodElements) {
				Element apiLevelSibmling = element2.nextElementSibling();
				String apiLevelElement = element2.nextElementSibling().text();
				String methodSignature = apiLevelSibmling.nextElementSibling().text();
				if (methodSignature.endsWith(" " + publicSttrName)) {
					String apiLevel = getApiInt(apiLevelElement);
					return new String[] { apiLevel, methodSignature };
				}
			}
		}
		return null;
	}


	public void updateAttribute(WidgetConfig widgetConfig, SourceConfig sourceConfig, Widget widget) throws Exception{
		this.widgetConfig = widgetConfig;
		this.sourceConfig = sourceConfig;
		String url = sourceConfig.getUrl();
		Document doc = getDocument(sourceConfig.getUrl(), sourceConfig.getParentUrls(), sourceConfig.getReplaceStrings());

		Elements elements = doc.select("h3");
		
		
		for (int i = 0; i < elements.size(); i++) {
			Element element = elements.get(i);
			String text = element.text();
			if (isAndroidAttribute(text)) {
				CustomAttribute nodeElement = new CustomAttribute();
				String namespace = getNameSpace(text);
				String attributeWithoutNameSpace = getAttributeWithoutNameSpace(text);
				nodeElement.setName(attributeWithoutNameSpace);
				CodeGenHelper.setTypeOnCustomAttribute("android", nodeElement, attributeWithoutNameSpace);
				
				nodeElement.setNamespace(namespace);
				nodeElement.setGeneratorUrl(url);

				Element nextSibLing = element.nextElementSibling();
				String methodSignature = "";
				String apiLevel = "1";
				while (!nextSibLing.tagName().equals("h3")) {

					nextSibLing = nextSibLing.nextElementSibling();

					if (nextSibLing == null) {
						break;
					}
					
					// try related method in documentation
					if (nextSibLing.previousElementSibling().text().equals("Related methods:")) {
						methodSignature = nextSibLing.text();
						String methodName = getMethodName(methodSignature);

						String[] methodNameAndApi = getMethodNameAndApiLevel(doc, methodName);
						if (methodNameAndApi != null) {
							apiLevel = methodNameAndApi[0];
							methodSignature = methodNameAndApi[1];

							break;
						}

					}
				}

				if (methodSignature.equals("")) {
					// try direct method name
					if (attributeWithoutNameSpace.startsWith("layout_")) {
						String publicSttrName =  attributeWithoutNameSpace.substring("layout_".length());
						String[] methodNameAndApi = getMethodNameAndApiLevelForLayoutAttr(doc, publicSttrName);
						if (methodNameAndApi != null) {
							apiLevel = methodNameAndApi[0];
							nodeElement.setApiLevel(apiLevel);
							methodSignature = methodNameAndApi[1];
						}
						
					} else {					
						String[] methodNameAndApi = getMethodNameAndApiLevel(doc,
								getSetMethodFromAttr(attributeWithoutNameSpace));
						if (methodNameAndApi != null) {
							nodeElement.setApiLevel(apiLevel);
							apiLevel = methodNameAndApi[0];
							methodSignature = methodNameAndApi[1];
						}
					}
				}

				if (attributeWithoutNameSpace.startsWith("layout_")) {
					String[] method = methodSignature.split(" ");
					if (method.length == 3 && method[0].equals("public")) {
						nodeElement.setCode(method[2] + " = ");
						nodeElement.setGetterCode("code:$var." + method[2]);
						nodeElement.setJavaType(method[1]);
						continue;
					}
				}  
				if (methodSignature.indexOf("(") != -1 && methodSignature.indexOf(")") != -1) {
					String[] methodsParams = getMethodParams(methodSignature);

					if (methodsParams.length == 1) {
						if (methodsParams.length == 1) {
							nodeElement.setJavaType(methodsParams[0]);
						}
						
						String methodName = getMethodName(methodSignature);
						nodeElement.setCode(methodName);

						Elements methodElements = doc.select("a:contains(" + text + ")");
						// try one using linked xml attribute
						for (Element methodElement : methodElements) {
							Element previousElementSibling = methodElement.parent().parent().previousElementSibling();
							if (previousElementSibling != null) {
								String relatedXmlAttr = previousElementSibling.text();
								if (relatedXmlAttr.equals("Related XML Attributes:")
										&& methodElement.text().equals(text)) {
									String getterMethod = previousElementSibling.firstElementSibling().text();
									if (getterMethod.indexOf("get") != -1 && getterMethod.toLowerCase()
											.indexOf(attributeWithoutNameSpace.toLowerCase()) != -1) {
										nodeElement.setGetterCode(getterMethod);
										break;
									}
								}
							}
						}
						String apiLevelForGet = apiLevel;
						nodeElement.setApiLevelForGet(getApiInt(apiLevelForGet));
					}
				}
				
				widget.addOrUpdateAttribute(nodeElement);
			}
		}

	}
}
