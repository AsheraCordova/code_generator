package com.ashera.codegen.pojo;

import java.text.NumberFormat;
import java.util.List;
import java.util.Optional;

public class QuirkWidget {
    private String packageName;
    private String group;
    private String name;
	private String nativeClassName;
    private List<String> ignoredAttributes;
    private List<QuirkAttribute> attributes = new java.util.ArrayList<>();
    private List<QuirkAttribute> parentAttributes;

	public List<QuirkAttribute> getParentAttributes() {
		return parentAttributes;
	}
	public void setParentAttributes(List<QuirkAttribute> parentAttributes) {
		this.parentAttributes = parentAttributes;
	}

	public String getSimilarity() {
		if (parentAttributes == null) {
			return "";
		}
		
		java.util.OptionalDouble score = parentAttributes.stream().mapToDouble((parentAttribute) -> {
			Optional<QuirkAttribute> attr = attributes.stream().filter((attribute) -> parentAttribute.getAttributeName().equals(attribute.getAttributeName())).findFirst();
			
			if (!attr.isPresent()) {
				return 0.0f;
			}

			if ("pass".equals(attr.get().getXmlTest())) {
				return 1f;
			}
			
			if ("quirk".equals(attr.get().getXmlTest())) {
				return 0.8f;
			}
			return 0.0f;
		}).average();
		NumberFormat instance = java.text.DecimalFormat.getInstance();
		instance.setMaximumFractionDigits(2);
		return instance.format(score.getAsDouble() * 100) + "%";
	}
	
	
	public String getTrimmedGroup() {

        String localStr = group;
        if (localStr.indexOf(".") != -1) {
            localStr = localStr.substring(localStr.lastIndexOf(".") + 1);
        }
        return localStr;
    }
    public List<String> getIgnoredAttributes() {
        return ignoredAttributes;
    }

    public void setIgnoredAttributes(List<String> ignoredAttributes) {
        this.ignoredAttributes = ignoredAttributes;
    }


    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    
    public String getPackageDir() {
        return packageName.replaceAll("\\.", "/");
    }

    public List<QuirkAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<QuirkAttribute> attributes) {
        this.attributes = attributes;
    }
    
    public String getNativeClassName() {
        return nativeClassName;
    }

    public void setNativeClassName(String nativeClassName) {
        this.nativeClassName = nativeClassName;
    }

    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
