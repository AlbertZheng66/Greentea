package com.xt.core.utils;

import java.io.Serializable;

/**
 * <p>
 * Title: GreeTea 框架。
 * </p>
 * <p>
 * Description: 表示一对名值对。
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author 郑伟
 * @version 1.0
 */

public class Pair<T> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7778901706986817380L;

	private String name;

	private T value;

	public Pair() {
	}

	public Pair(String name, T value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public T getValue() {
		return value;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(T value) {
		this.value = value;
	}

}
