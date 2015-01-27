package com.xt.proxy.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.xt.core.cm.IClassModifier;
import com.xt.core.cm.NewMethod;
import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import com.xt.core.service.IService;
import com.xt.core.utils.ClassHelper;
import com.xt.proxy.Proxy;
import com.xt.proxy.ProxyFactory;
import com.xt.proxy.ProxyStub;
import com.xt.proxy.Task;
import com.xt.proxy.event.DefaultResponseProcessor;
import com.xt.gt.ui.fsp.FspParameter;
import com.xt.proxy.event.Request;
import com.xt.proxy.event.Response;
import com.xt.proxy.event.ResponseProcessor;

public class ServiceStubModifier implements IClassModifier {// 生成后的新PO名称的后缀

	private final static Logger logger = Logger.getLogger(ServiceStubModifier.class);

	private static final String subfix = "_SSTUB";

	/**
	 * 被修改的服务类。
	 */
	private Class modifiedClass;

	/**
	 * 用于设置随附参数的属性名称
	 */
	static final String FSP_PARAMETER_NAME = "_$fspParameter";

	/**
	 * 用于设置是否是异步调用的方法名称
	 */
	static final String ASYNCH_INVOKE_NAME = "_$asynchInvoke";

	/**
	 * 用于设置异步调用的时的任务方法（回调函数）名称
	 */
	static final String TASK_NAME = "_$task";

	private List<Method> methods = new ArrayList<Method>();

	public ServiceStubModifier() {

	}

	public String getName(String modifiedClassName) {
		return modifiedClassName + subfix;
	}

	public NewMethod[] getNewMethods(CtClass modifiedClass)
			throws NotFoundException, CannotCompileException {
		List<NewMethod> nms = new ArrayList<NewMethod>();
		for (int i = 0; i < methods.size(); i++) {
			nms.add(createProxyMethod(methods.get(i)));
		}

		// 随附参数的 setter 方法
		NewMethod apMethod = createSetterMethod(FSP_PARAMETER_NAME,
				FspParameter.class);
		if (apMethod != null) {
			nms.add(apMethod);
		}

		// 异步调用的 setter 方法
		NewMethod aiMethod = createSetterMethod(ASYNCH_INVOKE_NAME,
				boolean.class);
		nms.add(aiMethod);

		// 异步任务的 setter 方法
		NewMethod taskMethod = createSetterMethod(TASK_NAME, Task.class);
		nms.add(taskMethod);

		return nms.toArray(new NewMethod[nms.size()]);
	}

	private NewMethod createSetterMethod(String propertyName,
			Class perpertyClass) {
		// 已经有同名的类存在
		if (ClassHelper.getMethod(modifiedClass, propertyName,
				new Class[] { perpertyClass }) != null) {
			return null;
		}

		NewMethod taskMethod = new NewMethod();
		taskMethod.setName("set" + StringUtils.capitalize(propertyName));
		taskMethod.setParamsType(new Class[] { perpertyClass });
		taskMethod.setExceptionsType(new Class[0]);
		taskMethod.setReturnType(void.class);
		taskMethod.setByInterface(false);
		taskMethod.setBody(String.format("this._$%s = $1;", propertyName));

		return taskMethod;
	}

	/**
	 * 创建一个新的代理方法，其大体的形式如下： <br>
	 * Request request = new Request();<br>
	 * request.setService(HttpXmlRequestTest.class);<br>
	 * request.setMethodName("add"); <br>
	 * request.setParamTypes(new Class[]{int.class, int.class});<br>
	 * request.setParams(new Object[]{2, 8}); <br>
	 * Proxy proxy = ProxyFactory.getInstance(); <br>
	 * return proxy.invoke(request);<br>
	 * request.setFSPParameter(this._$fspFSPParameter);<br>
	 * Proxy proxy = ProxyFactory.getInstance().getProxy(); <br>
	 * if (this._$asynchInvoke) { <br>
	 * ____proxy = new AsynchProxy(proxy, this._$task); <br>
	 * }<br>
	 * Response res = proxy.invoke(request); <br>
	 * ResponseProcessor responseProcessor = new DefaultResponseProcessor(); <br>
	 * Object ret = responseProcessor.process(request, res);<br>
	 * if (retType != void.class) {<br>
	 * ____return ret;<br>
	 * }<br>
	 * 
	 * 
	 * @param method
	 * @return
	 * @throws NotFoundException
	 * @throws CannotCompileException
	 */
	private NewMethod createProxyMethod(Method method)
			throws NotFoundException, CannotCompileException {
		LogWriter.info(logger, "create proxy method of " + method.getName());
		NewMethod gtn = new NewMethod();
		gtn.setName(method.getName());
		gtn.setParamsType(method.getParameterTypes());
		gtn.setExceptionsType(method.getExceptionTypes());
		gtn.setReturnType(method.getReturnType());
		gtn.setByInterface(false);

		String requestName = Request.class.getName();

		StringBuffer body = new StringBuffer();
		body.append(requestName).append(" request = new ").append(requestName)
				.append("(); \n");
		body.append("request.setService(").append(modifiedClass.getName())
				.append(".class); \n");
		body.append("request.setMethodName(\"").append(method.getName())
				.append("\"); \n");
		body.append("java.lang.Class[]  paramTypes = null; \n");
		body.append("java.lang.Object[] params     = null; \n");

		// 组装参数
		Class<?>[] parameterTypes = method.getParameterTypes();

		if (parameterTypes != null) {
			body.append("paramTypes = new java.lang.Class[").append(
					parameterTypes.length).append("]; \n");
			body.append("params = new java.lang.Object[").append(
					parameterTypes.length).append("]; \n");
			for (int i = 0; i < parameterTypes.length; i++) {
				body.append("paramTypes[").append(i).append("] = ").append(
						getParameterTypeString(parameterTypes[i])).append(
						".class; \n");
				body.append("params[").append(i).append("] = ").append("($w)$")
						.append(i + 1).append("; \n");

			}
		} else {
			body.append("paramTypes = new java.lang.Class[0];");
			body.append("params = new java.lang.Object[0];");
		}
		body.append("request.setParamTypes(paramTypes); \n");
		body.append("request.setParams(params); \n");
		body.append("request.setFSPParameter(this._$").append(
				FSP_PARAMETER_NAME).append("); \n");

		//
		body.append(Proxy.class.getName()).append(" proxy = ").append(
				ProxyFactory.class.getName()).append(
				".getInstance().getProxy(); \n");

		// 如果是异步调用，此处使用异步代理来代替原始的代理进行调用
		body.append("if (this._$").append(ASYNCH_INVOKE_NAME).append(") {");
		body.append("    proxy = new ").append(AsynchProxy.class.getName())
				.append("(proxy, this._$").append(TASK_NAME).append(");");
		body.append("}");
		Class<?> retType = method.getReturnType();
		body.append(Response.class.getName()).append(" res = ").append(
				" proxy.invoke(request); \n");
		body.append(ResponseProcessor.class.getName()).append(
				" responseProcessor = new ").append(
				DefaultResponseProcessor.class.getName()).append("(); \n");
		body.append("Object ret = responseProcessor.process(request, res); \n");
		if (retType != void.class) {
			body.append("return ($r)ret; \n");
		}

		gtn.setBody(body.toString());

		return gtn;
	}

/**
	 * 将类型转换字符串形式。对于数组类型，需要特殊处理，其他形式可采用“getName()”方法获取。
	 *  The encoding of element type names is as follows:
     *
     * <blockquote><table summary="Element types and encodings">
     * <tr><th> Element Type <th> &nbsp;&nbsp;&nbsp; <th> Encoding
     * <tr><td> boolean      <td> &nbsp;&nbsp;&nbsp; <td align=center> Z
     * <tr><td> byte         <td> &nbsp;&nbsp;&nbsp; <td align=center> B
     * <tr><td> char         <td> &nbsp;&nbsp;&nbsp; <td align=center> C
     * <tr><td> class or interface  
     *                       <td> &nbsp;&nbsp;&nbsp; <td align=center> L<i>classname</i>;
     * <tr><td> double       <td> &nbsp;&nbsp;&nbsp; <td align=center> D
     * <tr><td> float        <td> &nbsp;&nbsp;&nbsp; <td align=center> F
     * <tr><td> int          <td> &nbsp;&nbsp;&nbsp; <td align=center> I
     * <tr><td> long         <td> &nbsp;&nbsp;&nbsp; <td align=center> J
     * <tr><td> short        <td> &nbsp;&nbsp;&nbsp; <td align=center> S
     * </table></blockquote>
	 * @param clazz
	 * @return
	 */
	private String getParameterTypeString(Class clazz) {
		StringBuffer type = new StringBuffer();
		if (clazz.isArray()) {
			char[] tempName = clazz.getName().toCharArray();
			int count = 0;
			for (int i = 0; i < tempName.length; i++) {
				char c = tempName[i];
				if (c == '[') {
					count++;
				} else if (c == 'Z') {
					type.append(boolean.class.getName());
					break;
				} else if (c == 'B') {
					type.append(byte.class.getName());
					break;
				} else if (c == 'C') {
					type.append(char.class.getName());
					break;
				} else if (c == 'L') {
					// 去掉前导的“[[L”和后面的“;”
					String name = clazz.getName();
					type.append(name.substring(i + 1, name.length() - 1));
					break;
				} else if (c == 'D') {
					type.append(double.class.getName());
					break;
				} else if (c == 'F') {
					type.append(float.class.getName());
					break;
				} else if (c == 'I') {
					type.append(int.class.getName());
					break;
				} else if (c == 'J') {
					type.append(long.class.getName());
					break;
				} else if (c == 'S') {
					type.append(short.class.getName());
					break;
				}
			}
			for (int i = 0; i < count; i++) {
				type.append("[]");
			}
		} else {
			type.append(clazz.getName());
		}
		return type.toString();
	}

	public String[] getNewFields(CtClass modifiedClass)
			throws NotFoundException, CannotCompileException {
		List<String> nfs = new ArrayList<String>(5);

		nfs.add(String.format("private %s _$%s = new %s();", FspParameter.class
				.getName(), FSP_PARAMETER_NAME, FspParameter.class.getName()));
		// 是否采用异步方法调用
		nfs.add(String.format("private boolean _$%s = false;",
				ASYNCH_INVOKE_NAME));
		nfs.add(String.format("private %s _$%s = null;", Task.class.getName(),
				TASK_NAME));
		return nfs.toArray(new String[nfs.size()]);
	}

	public Class getInterface() {
		return ProxyStub.class;
	}

	public boolean needModify(Class modifidClass) {
		return (!methods.isEmpty() && IService.class
				.isAssignableFrom(modifidClass));
	}

	public void init(Class modifiedClass) {
		this.modifiedClass = modifiedClass;
		methods.clear();

		// 不反射父类的方法
		Method[] methods = modifiedClass.getDeclaredMethods();
		if (methods == null) {
			throw new SystemException("服务类必须有公共的的方法！");
		}
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (Modifier.isPublic(method.getModifiers())
					&& !Modifier.isFinal(method.getModifiers())) {
				this.methods.add(method);
			}
		}

		if (this.methods.isEmpty()) {
			throw new SystemException("服务类必须有公共的的方法！");
		}
	}

}
