package com.xt.views.taglib.html.fc;

import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xt.core.exception.BadParameterException;
import com.xt.views.taglib.html.IFieldController;

public class FieldControllerFactory {

	private Map<String, IFieldController> fcs = new Hashtable<String, IFieldController>(
			3);

	private static FieldControllerFactory instance = new FieldControllerFactory();

	private FieldControllerFactory() {
	}

	public static FieldControllerFactory getInstance() {
		return instance;
	}

	/**
	 * 给指定的名称注册一个域控制器。
	 * @param name 域注册器的名称，对应于标签的头部（例如：param{_visible_}）的“param”部分。
	 * @param fc 域控制器实例
	 * @throws BadParameterException 名称和域控制器实例都不能为空，否则抛出此异常。
	 */
	public void register(String name, IFieldController fc) {
		if (StringUtils.isEmpty(name) || fc == null) {
			throw new BadParameterException("域控制器的注册参数name[" + name
					+ "]和fieldController[" + fc + "]都不能为空！");
		}
		fcs.put(name, fc);
	}
	
	public IFieldController get(String name) {
		if (StringUtils.isEmpty(name)) {
			throw new BadParameterException("参数不能为空！");
		}
		if (!fcs.containsKey(name)) {
			throw new BadParameterException("未发现名称为[" + name + "]的域控制器！");
		}
		return fcs.get(name);
	}

}
