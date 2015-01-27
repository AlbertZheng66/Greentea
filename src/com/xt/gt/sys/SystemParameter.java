package com.xt.gt.sys;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SystemParameter {

	private final String name;

	private String content;

	private final Map<String, String> attributes = new LinkedHashMap<String, String>();

	private final List<SystemParameter> children = new ArrayList<SystemParameter>();

	public SystemParameter(String name) {
		this.name = name;
	}

	public void addAttribute(String name, String value) {
		if (name != null && value != null) {
			attributes.put(name, value);
		}
	}

	public String getAttributeValue(String name) {
		if (name == null) {
			return null;
		}
		return attributes.get(name);
	}

	public void addChild(SystemParameter child) {
		children.add(child);
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Map<String, String> getAttibutes() {
		return attributes;
	}

	public List<SystemParameter> getChildren() {
		return children;
	}

	public String getName() {
		return name;
	}
}
