package com.xt.proxy.utils;

import java.io.IOException;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.NotFoundException;

import com.xt.core.exception.BadParameterException;
import com.xt.core.exception.SystemException;
import com.xt.core.service.IService;

public class ServerClassService implements IService {

	private static ClassPool pool;
	
	static {
		ClassPath cp = new ClassClassPath(ServerClassService.class);
		pool = ClassPool.getDefault();
		pool.appendClassPath(cp);
	}

	/**
	 * @param args
	 */
	public byte[] getClass(String className) {
		if (className == null) {
			throw new BadParameterException("类不能为空！");
		}
		try {
			return pool.get(className).toBytecode();
		} catch (IOException e) {
			throw new SystemException("类[" + className + "]IO错误!", e);
		} catch (CannotCompileException e) {
			throw new SystemException("类[" + className + "]编译错误!", e);
		} catch (NotFoundException e) {
			throw new SystemException("类[" + className + "]不存在!", e);
		}
	}

}
