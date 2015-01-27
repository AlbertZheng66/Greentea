package com.xt.proxy.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.xt.core.log.LogWriter;
import com.xt.core.utils.IOHelper;
import com.xt.gt.sys.loader.SystemLoader;
import com.xt.gt.sys.loader.SystemLoaderManager;

/**
 * 装载服务器动态生成的类。如果客户端不存在某些动态生成的类，则从服务器端进行装载。
 * 
 * @author zw
 * 
 */
public class ClassModifierClassLoader extends ClassLoader {
	
	private final Logger logger = Logger.getLogger(ClassModifierClassLoader.class);

	private final SystemLoader systemLoader;
	
//	private final Map<String, String> classesMapping = new HashMap<String, String>();
	
	public ClassModifierClassLoader(ClassLoader parent) {
		super(parent);
		systemLoader = SystemLoaderManager.getInstance().getSystemLoader();
		
	}
	
//	public void register (String newClassName, String oldClassName) {
//		classesMapping.put(newClassName, oldClassName);
//	}

	synchronized public Class<?> findClass(String name) throws ClassNotFoundException {
		LogWriter.debug(logger, "findClass... name", name);
		// 检查是否已经加载
		Class loadedClass = findLoadedClass(name);
		if (loadedClass != null) {
			return loadedClass;
		}
		loadedClass = findSystemClass(name);
		if (loadedClass != null) {
			return loadedClass;
		}
		URL url = systemLoader.getServerURL();
		// String oldClassName = classesMapping.get(name);
		// String path = url.getPath() + "/sf" + "?oldClassName=" + oldClassName + "&newClassName=" + name;
		StringBuffer path = new StringBuffer(url.getPath());
		path.append("/sf?className=").append(name);
		try {
			URL classLoaderUrl = new URL(url.getProtocol(), url.getHost(), url.getPort(), path.toString());
			URLConnection conn = classLoaderUrl.openConnection();
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			BufferedOutputStream bos = new BufferedOutputStream(baos);
			IOHelper.i2o(bis, bos, true, true);
			byte[] byteCodes = baos.toByteArray();
			return defineClass(name, byteCodes, 0, byteCodes.length);			
		} catch (Exception e) {
			throw new ClassNotFoundException(String.format("查找类[%s]时产生异常。", name), e);
		}
	}

}
