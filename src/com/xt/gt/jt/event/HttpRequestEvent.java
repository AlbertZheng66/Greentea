package com.xt.gt.jt.event;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.xt.comm.encoding.EncodingFilter;
import com.xt.core.exception.BadParameterException;
import com.xt.core.log.LogWriter;
import com.xt.core.utils.BeanHelper;
import com.xt.core.utils.ClassHelper;

/**
 * <p>
 * Title: 框架类.
 * </p>
 * <p>
 * Description: 解析由浏览器提交的HTTP请求.请求的样式如下: 1,基本类型:str1=aaa;int1=123
 * 2,复合类型(类):class1.name=asas;calss1.code=123;class1.innerClass.rowstate=1;
 * class1.innerClass.attr1=aa 3,集合类型: 3.1集合里包含的是基本类型:
 * collName1=aads;collName1=ddd collName2=222;collName2=111 3.2集合里包含的是复合类型:
 * collName.0.name=aaa;collName.0.code=123
 * collName.1.name=bbb;collName.1.name=678
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author 郑伟
 * @version 1.0
 */

public class HttpRequestEvent implements RequestEvent {
	/**
	 * Http请求
	 */
	private HttpServletRequest request;
	
	private static final String REQEUST_EVENT_IN_REQUEST = "REQEUST_EVENT_IN_REQUEST";

	/**
	 * 判断一个Http请求是否带有多个附件
	 */
	private boolean isMultipart = false;

	/**
	 * 当一个Http请求是否带有多个附件时，将其名称和值存在这个容器中，
	 * 以备读取时使用。只有当“isMultipart”为真时，这个变量才会被实例化。
	 */
	private Map<String, Object> values;
	
	
	public static HttpRequestEvent getInstance(HttpServletRequest request) {
		HttpRequestEvent instance = (HttpRequestEvent)request.getAttribute(REQEUST_EVENT_IN_REQUEST);
		if (instance != null) {
			return instance;
		} else {
			instance = new HttpRequestEvent(request);
			request.setAttribute(REQEUST_EVENT_IN_REQUEST, instance);
		    return instance;
		}
	}

	private HttpRequestEvent(HttpServletRequest request) {
		
		this.request = request;
//		for (Iterator iter = request.getParameterMap().keySet().iterator(); iter.hasNext();) {
//			String element = (String) iter.next();
//			LogWriter.debug("HttpRequestEvent name", element);
//			LogWriter.debug("HttpRequestEvent value", request.getParameterMap().get(element));
//		}
		//ServletRequestContext src = new ServletRequestContext(request);
		isMultipart = ServletFileUpload.isMultipartContent(request);
		if (isMultipart) {
			loadMultipart();
		} else {
			values = new HashMap();
                        values.putAll(request.getParameterMap());
		}
	}

	public Object getAttribute(String paramName) {
		return request.getAttribute(paramName);
	}

	public Object findAttribute(String paramName) {
		Object value = request.getAttribute(paramName);

		// 如果Request中无此属性,则Session中查找
		if (value == null) {
			value = request.getSession(true).getAttribute(paramName);
		}

		// 如果Session中无此属性,则Application中查找
		if (value == null) {
			value = request.getSession().getServletContext().getAttribute(
					paramName);
		}

		return value;
	}

	public Object getAttribute(String name, int scope) {
		Object ret = null;
		if (scope == RequestEvent.REQUEST) {
			ret = getParameter(name);
		} else if (scope == RequestEvent.SESSION) {
			ret = request.getSession().getAttribute(name);
		} else if (scope == RequestEvent.APPLICATION) {
			ret = request.getSession().getServletContext().getAttribute(name);
		}
		return ret;
	}

	public Object getObject(String paramName, Class clazz)
			throws BadParameterException {
		return createObject(paramName, clazz);
	}

	private Object createObject(String paramName, Class clazz)
			throws BadParameterException {
		Object result = null;
		try {
			result = ClassHelper.newInstance(clazz);
			String[] fieldNames = ClassHelper.getPropertyNames(clazz); // 所有属性名称
			for (int i = 0; i < fieldNames.length; i++) {
				String fieldName = fieldNames[i];
				Class fieldType = ClassHelper.getFieldType(clazz, fieldName);
				// 处理字符串类型及原始类型
				if (String.class.equals(fieldType) || fieldType.isPrimitive()) {
					copyPrimitiveField(result, paramName, fieldName, fieldType);
				}
			}
		} catch (Exception ex) {
			throw new BadParameterException("???", ex);
		}
		return result;
	}

	public int getInt(String paramName) {
		return Integer.parseInt(getParameter(paramName));
	}

	public double getDouble(String paramName) {
		return Double.parseDouble(getParameter(paramName));
	}

	public float getFloat(String paramName) {
		return Float.parseFloat(getParameter(paramName));
	}

	public long getLong(String paramName) {
		return Long.parseLong(getParameter(paramName));
	}

	public Object[] getObjects(String paramName, Class clazz)
			throws BadParameterException {
		Object[] result = null;
		// 处理字符串类型及原始类型
		if (String.class.equals(clazz) || clazz.isPrimitive()) {
			try {
				result = getPrimitiveArrayField(paramName, clazz);
			} catch (InvocationTargetException ex) {
				throw new BadParameterException("调用目标异常", ex);
			} catch (IllegalAccessException ex) {
				throw new BadParameterException("非法存取异常", ex);
			}
		}
		// 处理复合类型(类)
		else {
			int size = calculateArraySize(paramName);
			result = new Object[size];
			for (int i = 0; i < size; i++) {
				/**
				 * @todo 重复反射clazz类,待优化
				 */
				result[i] = createObject(paramName + "." + i, clazz);
			}
		}
		return result;
	}

	/**
	 * 计算数组的尺寸
	 * 
	 * @param paramName
	 *            String
	 * @return int
	 */
	private int calculateArraySize(String paramName) {
		int size = 0; // 数组的个数
		for (; true; size++) {
			boolean success = false; // 参数匹配是否成功
			for (Enumeration paramNames = request.getParameterNames(); paramNames
					.hasMoreElements();) {
				String name = (String) paramNames.nextElement();
				if (name.startsWith(paramName + "." + size)) {
					success = true;
					break; // 匹配成功则跳出循环
				}
			}

			// 如果匹配不成功则记录数组的尺寸
			if (!success) {
				break;
			}
		}
		return size;
	}

	private Object[] getPrimitiveArrayField(String paramName, Class fieldType)
			throws java.lang.IllegalAccessException,
			java.lang.reflect.InvocationTargetException {
		Object[] result = null;
		String[] values = request.getParameterValues(paramName);
		if (int.class.equals(fieldType)) {
			result = new Integer[values.length];
			for (int i = 0; i < result.length; i++) {
				result[i] = new Integer(values[i]);
			}
		} else if (float.class.equals(fieldType)) {
			result = new Float[values.length];
			for (int i = 0; i < result.length; i++) {
				result[i] = new Float(values[i]);
			}
		} else if (double.class.equals(fieldType)) {
			result = new Double[values.length];
			for (int i = 0; i < result.length; i++) {
				result[i] = new Double(values[i]);
			}
		} else if (long.class.equals(fieldType)) {
			result = new Long[values.length];
			for (int i = 0; i < result.length; i++) {
				result[i] = new Long(values[i]);
			}

		} else {
			result = values;
		}
		return result;
	}

	private void copyPrimitiveField(Object obj, String paramName,
			String fieldName, Class fieldType) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		String value = request.getParameter(paramName + "." + fieldName);
		if (int.class.equals(fieldType)) {
			BeanHelper.copyProperty(obj, fieldName, new Integer(value));
		} else if (float.class.equals(fieldType)) {
			BeanHelper.copyProperty(obj, fieldName, new Float(value));
		} else if (double.class.equals(fieldType)) {
			BeanHelper.copyProperty(obj, fieldName, new Double(value));
		} else if (long.class.equals(fieldType)) {
			BeanHelper.copyProperty(obj, fieldName, new Long(value));
		} else {
			BeanHelper.copyProperty(obj, fieldName, value);
		}
	}

	public String getParameter(String paramName) {
//		LogWriter.debug("getParameter paramName", paramName);
		Object value = values.get(paramName);
//		LogWriter.debug("getParameter value", value);
		if (value == null) {
			return null;
		} else if (value instanceof String) {
			return (String)value;
		} else if (value instanceof String[] && ((String[])value).length > 0) {
			return ((String[])value)[0];
		} else {
		    return value.toString();
		}
	}

	public String[] getParameterNames() {
		Collection<String> paramNamesColl = values.keySet();
		
		// 将名称集合转换为数组
		List<String> pns = new ArrayList<String>();
		pns.addAll(paramNamesColl);
		return ((String[]) pns.toArray(new String[pns.size()]));
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public String[] getParameterValues(String paramName) {
		Object value = request.getParameterValues(paramName);
		if (value instanceof String[]) {
			return (String[])value;
		}
		return null;
	}

	private void loadMultipart() {
		if (values == null) {
			values = new HashMap<String, Object>();
			// Parse the request
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			try {
				Iterator iter = upload.parseRequest(request).iterator();

				while (iter.hasNext()) {
					FileItem item = (FileItem) iter.next();
					String name = item.getFieldName();

					LogWriter.debug("HttpRequestEvent name", name);
					Object value = null;
					if (item.isFormField()) {
						value = encodingConvert(item.getString());
						//进行编码转换
						
					} else if (item.getInputStream() != null){
						value = item.getInputStream();
					} else {
						value = encodingConvert(item.getString());
					}
					LogWriter.debug("HttpRequestEvent value", value);
					values.put(name, value);
				}
			} catch (IOException e) {
				throw new RequestEventException("解析多附件HTTP请求时产生IO异常", e);
			} catch (FileUploadException e) {
				throw new RequestEventException("解析多附件HTTP请求时产生文件传输异常", e);
			}
		}
	}
	
	
	private String encodingConvert(String value) {
		if (value != null) {
			try {
				return new String(value.getBytes("ISO-8859-1"), EncodingFilter.getEncoding());
			} catch (UnsupportedEncodingException e) {
				LogWriter.error("编码格式错误", e);
			}
		} 
		return null;		
	}

	public InputStream getInputStream(String name) {
		if (isMultipart) {
			return (InputStream) values.get(name);
		}
		return null;
	}
}
