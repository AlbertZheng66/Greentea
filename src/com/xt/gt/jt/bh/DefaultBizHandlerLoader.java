package com.xt.gt.jt.bh;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import com.xt.core.utils.BooleanUtils;
import com.xt.core.utils.ClassHelper;
import com.xt.core.utils.XmlHelper;
import com.xt.gt.jt.bh.mapping.BizMapping;
import com.xt.gt.jt.event.RequestEvent;

/**
 * 框架提供的默认的类装载器。
 * <p>
 * Title: XT框架-事务逻辑部分
 * </p>
 * <p>
 * Description: 这个装载器从如下结构中装载信息：
 * <actions>
	   	<action package="demo" bizHadler="Order" class="com.xt.bl.BizHandlerInfoFactoryTest"
			          needPersistence="1" >
			<dealMethod name="add" method="customizedAction"
			          paramType="METHOD_INPUT_PARAM">
			   <params>
			       <param name="userName" type="java.lang.String" scope="request|session|application"/>
			       <param name="password" type="java.lang.String"/>
			    </params>
			    <screen-flow variable="screen" default="/account/FileLog.jsp">
				    <variable value="gotoAdd" path="/account/AddFileLog.jsp"/>
				    <variable value="list"    path="/account/ListFileLog.jsp"/>
				</screen-flow>
			</dealMethod>
			<flowHandler name="com.xt.gt.screen.FlowHandlerTest">
				<screen name="List" path="/account/List.jsp"/>
				<screen name="Add" path="/account/Add.jsp"/>
				<screen name="other" path="/account/Other.jsp"/>
			</flowHandler>
		</action>
	</actions>
	但是不处理“dealMethod”标签的“screen-flow”标签和flowHandler标签，
	这两个标签放在屏幕流中进行处理。
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author 郑伟
 * @version 1.0
 * @date 2006-10-12
 */
public class DefaultBizHandlerLoader implements BizHandlerInfoLoader {

	/**
	 * 构建函数
	 */
	public DefaultBizHandlerLoader() {

	}

	/**
	 * 根据从配置文件中装载所有的业务处理信息。
	 */
	public List<BizHandlerInfo> load(InputStream is, BizMapping bizMapping) {
		List<BizHandlerInfo> bizHandlerInfos = new ArrayList<BizHandlerInfo>();
		Element root = XmlHelper.getRoot(is);
		if (root == null) {
			throw new SystemException("装载事务处理映射关系失败！");
		}

		// 业务处理类的根节点
		Element actionsElem = root.getChild("actions");
		if (actionsElem != null) {
			List actions = actionsElem.getChildren("action");
			for (Iterator iter = actions.iterator(); iter.hasNext();) {
				Element action = (Element) iter.next();

				// 组建业务处理类
				BizHandlerInfo bhi = createBizHandlerInfo(action, bizMapping);

				List dealMehtods = action.getChildren("dealMethod");

				// 组建业务处理方法
				for (Iterator iterDealMehtod = dealMehtods.iterator(); iterDealMehtod
						.hasNext();) {
					Element dealMethod = (Element) iterDealMehtod.next();
					DealMethodInfo dmi = createDealMethodInfo(dealMethod, bhi,
							bizMapping);
					bhi.addDealMethodInfo(dmi);
				}
				bizHandlerInfos.add(bhi);
			}
		}

		return bizHandlerInfos;
	}

	/**
	 * 根据action标签组建一个BizHandlerInfo对象。action标签的bizHadler属性不能为空，
	 * 但是package属性可能为空。如果标签的class属性为空，则采用默认的映射方式。如果
	 * 没有找到class属性指定的类，则抛出系统异常。默认情况下是需要进行持久化的。
	 * 
	 * @param action
	 * @return
	 */
	private BizHandlerInfo createBizHandlerInfo(Element action,
			BizMapping bizMapping) {
		BizHandlerInfo bhi = new BizHandlerInfo();
		bhi.setName(action.getAttributeValue("bizHadler"));
		bhi.setPackages(action.getAttributeValue("package"));
		String className = action.getAttributeValue("class");
		LogWriter.debug("service name", bhi.getName());
		LogWriter.debug("service package", bhi.getPackages());
		if (className == null) {
			// 如果没有自定义的事务处理类，采用默认的方式
			BizHandlerInfo defaultBizHandler = bizMapping.getBizHandlerInfo(bhi
					.getPackages(), bhi.getName());
			bhi.setHandlerClass(defaultBizHandler.getHandlerClass());
		} else {
			Class clazz = ClassHelper.getClass(className);
			bhi.setHandlerClass(clazz);
		}
		LogWriter.debug("service class", bhi.getHandlerClass());
		bhi.setNeedPersistence(BooleanUtils.isTrue(action
				.getAttributeValue("needPersistence"), true));
		return bhi;
	}

	/**
	 * 通过dealMethod标签组建一个DealMethodInfo对象。
	 * 如果属性method为空， 则默认是name属性作为其方法的名称
	 * 
	 * @param dealMethod
	 * @return
	 */
	private DealMethodInfo createDealMethodInfo(Element dealMethod,
			BizHandlerInfo bhi, BizMapping bizMapping) {
		DealMethodInfo dmi = new DealMethodInfo();
		dmi.setName(dealMethod.getAttributeValue("name"));
		LogWriter.debug("method name", dmi.getName());

		// 设置方法
		String method = getMthodName(dealMethod, bhi, bizMapping, dmi);
		dmi.setMethod(method);
		LogWriter.debug("method method", dmi.getMethod());
		// 如果是个空转方法，则不再进行任何处理
		if (dmi.isNop()) {
			return dmi;
		}

		// 载入参数类型
		String paramType = dealMethod.getAttributeValue("paramType");
		if (paramType == null || "METHOD_INPUT_PARAM".equals(paramType)) {
			dmi.setParamType(DealMethodInfo.METHOD_INPUT_PARAM);
		} else if ("CLASS_PROPERTIES".equals(paramType)) {
			dmi.setParamType(DealMethodInfo.CLASS_PROPERTIES);
		} else if ("CUSTOMIZED_INPUT_PARAM".equals(paramType)) {
			dmi.setParamType(DealMethodInfo.CUSTOMIZED_INPUT_PARAM);
		} else if ("METHOD_INPUT_PARAM_NULL".equals(paramType)) {
			dmi.setParamType(DealMethodInfo.METHOD_INPUT_PARAM_NULL);
		} else {
			throw new SystemException("不能识别的参数类型！");
		}

		// 提取参数
		Element paramsElem = dealMethod.getChild("params");
		if (paramsElem != null) {
			List params = paramsElem.getChildren();
			// 组建业务处理类
			for (Iterator iter = params.iterator(); iter.hasNext();) {
				Element param = (Element) iter.next();
				String name = param.getAttributeValue("name");
				String scopeStr = param.getAttributeValue("scope");
				int scope = RequestEvent.REQUEST; // 默认的范围都是请求范围内的
				if ("session".equals(scopeStr)) {
					scope = RequestEvent.SESSION;
				} else if ("application".equals(scopeStr)) {
					scope = RequestEvent.APPLICATION;
				} else if ("all".equals(scopeStr)) {
					scope = RequestEvent.ALL;
				}
				
				String type = param.getAttributeValue("type");
				String clazz = param.getAttributeValue("class");
				Class colClass = null;
                 if (StringUtils.isNotEmpty(clazz)) {
                	 try {
						colClass = Class.forName(clazz);
					} catch (ClassNotFoundException e) {
                        throw new SystemException("未发现类[" + clazz + "]!", e);
					}
                 }
				dmi.appendParam(name, scope, type, colClass);
			}
		}

		return dmi;
	}

	/**
	 * @param dealMethod
	 * @param bhi
	 * @param bizMapping
	 * @param dmi
	 * @return
	 */
	private String getMthodName(Element dealMethod, BizHandlerInfo bhi,
			BizMapping bizMapping, DealMethodInfo dmi) {
		String method = dealMethod.getAttributeValue("method");
		if (method == null) {
			// 如果没有自定义的事务处理方法，采用默认的映射方式
			BizHandlerInfo defaultBizHandler = bizMapping.getBizHandlerInfo(bhi
					.getPackages(), bhi.getName());
			if (defaultBizHandler != null) {
				DealMethodInfo defaultDmi = defaultBizHandler.getDealMethod(dmi
						.getName());
				if (defaultDmi != null) {
					method = defaultDmi.getName();
				}
			}
		} else if ("null".equals(method)) {
			dmi.setNop(true);
			return null;
		}

		// 如果默认的方式也没有，则与方法同名
		if (method == null) {
			method = dmi.getName();
		}
		return method;
	}

}
