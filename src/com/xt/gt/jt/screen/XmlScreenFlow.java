package com.xt.gt.jt.screen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jdom.Element;

import com.xt.core.dd.DynamicDeploy;
import com.xt.core.dd.Loadable;
import com.xt.core.exception.BaseException;
import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import com.xt.core.utils.BooleanUtils;
import com.xt.core.utils.XmlHelper;
import com.xt.gt.jt.proc.ActionParameter;

/**
 * 根据配置文件（XML文件）屏幕流转控制器。
 * <p>
 * Title: XT框架-事务逻辑部分
 * </p>
 * <p>
 * Description: 根据配置文件（XML文件）确定屏幕的流转方向，如果配置文件中未定义， 则返回空。配置文件的样式如下： <action
 * package="demo" bizHadler="order" class="com.xt.bl.BizHandlerInfoFactoryTest"
 * needPersistence="1" > <dealMethod name="customizedAction" method="add"
 * paramType="METHOD_INPUT_PARAM"> <screen-flow variable="screen"
 * default="/account/DefaultDatAcctOutputFileLog.jsp"> <variable value="gotoAdd"
 * path="/account/AddDatAcctOutputFileLog.jsp"/> <variable value="list"
 * path="/account/ListDatAcctOutputFileLog.jsp"/> </screen-flow> </dealMethod>
 * <flowHandler name="com.xt.gt.screen.FlowHandlerTest"> <screen name="List"
 * path="/account/List.jsp"/> <screen name="Add" path="/account/Add.jsp"/>
 * <screen name="other" path="/account/Other.jsp"/> </flowHandler> </action>
 * </p>
 * <p>
 * 此屏幕流的处理逻辑是：如果一个请求的包是“demo”，业务处理类是“order”， 则由这个标签处理:
 * <li>如果处理方法是“customizedAction”，屏幕的处理结果
 * 和“variable”属性的值相关。也就是说：处理器将读取HTTP请求中的参数 （如：screen），然后根据这个值，返回对应的页面。如果返回的值不在列举
 * 的范围之内，则返回默认的页面。</li>
 * <li>如果没有相应的处理方法，则调用“flowHandler”定义的屏幕流处理类。 根据这个屏幕流处理类返回的结果再确定结果页面。</li>
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
 * @date 2006-10-21
 */
public class XmlScreenFlow implements IScreenFlow {

	/**
	 * 用户自定义的屏幕流，主键是ProcessParams对象，值是CustomizedScreenFlow对象。
	 * 
	 */
	private static Map<Key, ActionElement> customizedScreenFlows = new Hashtable<Key, ActionElement>();
	
	public static final String XML_SCREEN_FLOW_FILE = "XML_SCREEN_FLOW_FILE";
	
	public XmlScreenFlow () {
	}

//	// 热部署时重新装载文件
//	public void load(String fileName) {
//		_load(fileName);
//	}
	
	public synchronized String forword(HttpServletRequest req, ActionParameter pp, Object ret)
			throws BaseException {
		
		String path = null;

		// 从已经保存的屏幕流提取信息
		Key key = new Key(pp.getAllPackages(), pp.getBizHandler());
		ActionElement actionElement = customizedScreenFlows.get(key);

		if (actionElement != null) {
			path = actionElement.getPath(req, pp, ret);
		}

		return path;
	}
	
	private void _load(String fileName) {
		if (fileName == null) {
			LogWriter.warn("XmlScreenFlow 屏幕流文件为空！");
			return;
		}
		
		File file = new File(fileName);
		try {
			FileInputStream fis = new FileInputStream(file);
		    load(fis);
		} catch (FileNotFoundException e) {
			throw new SystemException("读取屏幕流配置文件[" + fileName + "]时出现错误！", e);
		}
	}

	/**
	 * 装载配置文件。如果文件为空，则不处理，直接返回。
	 * 
	 * @param is
	 */
	private static void load(InputStream is) {
		// 校验XML的格式是否正确

		if (is == null) {
			LogWriter.warn("XmlScreenFlow 屏幕流文件为空！");
			return;
		}

		Element root = XmlHelper.getRoot(is);

		// xml的格式由DTD或者schema来保证
		// Element actionsElem = root.getChild("actions");
		List actions = root.getChildren("action");
		for (Iterator iter = actions.iterator(); iter.hasNext();) {
			Element action = (Element) iter.next();
			ActionElement csf = createActionElement(action);
//			LogWriter.debug("csf.getPackages()", csf.getPackages());
//			LogWriter.debug("pp.getBizHadler()", csf.getBizHadler());
			customizedScreenFlows.put(new Key(csf.getPackages(), csf
					.getBizHadler()), csf);
		}
	}

	/**
	 * 载入屏幕流的“action”标签对应的类。
	 * 
	 * @param xmlAction
	 *            action标签
	 * @return
	 */
	private static ActionElement createActionElement(Element xmlAction) {
		String packageStr = xmlAction.getAttributeValue("package");
		String bizHandler = xmlAction.getAttributeValue("bizHadler");
		ActionElement actionElement = new ActionElement();
		actionElement.setPackages(packageStr);
		actionElement.setBizHadler(bizHandler);

		// 载入dealMethod部分
		List dealMethods = xmlAction.getChildren("dealMethod");
		for (Iterator iter = dealMethods.iterator(); iter.hasNext();) {
			Element xmlDealMethod = (Element) iter.next();

			DealMethodElement dmf = null;

			// 每个处理方法可以有一个简化的屏幕流
			Element xmlScreenFlow = xmlDealMethod.getChild("screen-flow");
			if (xmlScreenFlow != null) {
				String name = xmlDealMethod.getAttributeValue("name");
				dmf = createDealMethodElement(name, xmlScreenFlow);
			}

			// 每个处理方法节点可以有自己的屏幕流
			Element xmlFlowHandler = xmlDealMethod.getChild("flowHandler");
			FlowHadlerElement fh = loadFlowHandler(xmlFlowHandler);
			if (fh != null) {
				if (dmf == null) {
					dmf = new DealMethodElement();
					String name = xmlDealMethod.getAttributeValue("name");
					dmf.setDealMethod(name);
				}
				dmf.setFlowHadler(fh);
			}

			if (dmf != null) {
				actionElement.put(dmf);
			}
		}

		// 载入flowHandler部分
		FlowHadlerElement fh = loadFlowHandler(xmlAction
				.getChild("flowHandler"));
		actionElement.setFlowHadler(fh);

		return actionElement;
	}

	/**
	 * @param xmlFlowHandler
	 * @return
	 */
	private static FlowHadlerElement loadFlowHandler(Element xmlFlowHandler) {
		if (null == xmlFlowHandler) {
			return null;
		}

		FlowHadlerElement fh = new FlowHadlerElement(xmlFlowHandler
				.getAttributeValue("name"));
		//LogWriter.debug("name", fh.getName());
		boolean direct = BooleanUtils.isTrue(xmlFlowHandler
				.getAttributeValue("direct"), false);
		fh.setDirect(direct);

		// 直接返回参数时不需要进行屏幕映射
		if (!direct) {
			List screens = xmlFlowHandler.getChildren("screen");
			for (Iterator iter = screens.iterator(); iter.hasNext();) {
				Element screen = (Element) iter.next();
				fh.addScreens(screen.getAttributeValue("name"), screen
						.getAttributeValue("path"));
			}
		}
		return fh;
	}

	/**
	 * 载入业务处理方法节点
	 */
	private static DealMethodElement createDealMethodElement(String name,
			Element xmlDealMethod) {

		DealMethodElement dmf = new DealMethodElement();
		dmf.setDealMethod(name);
		dmf.setVariableName(xmlDealMethod.getAttributeValue("variable"));
		dmf.setDefaultPath(xmlDealMethod.getAttributeValue("default"));

		List variables = xmlDealMethod.getChildren("variable");
		for (Iterator iter = variables.iterator(); iter.hasNext();) {
			Element variable = (Element) iter.next();
			dmf.setVariables(variable.getAttributeValue("value"), variable
					.getAttributeValue("path"));
		}
		return dmf;
	}

}

/**
 * <action package="account" bizHadler ="DatAcctOutputFile" > <dealMethod
 * name="add" variable="screen" path="/${default}/AddDatAcctOutputFileLog.jsp">
 * <variable value="gotoAdd" path="/${default}/AddDatAcctOutputFileLog.jsp" />
 * <variable value="list" path="/${default}/AddDatAcctOutputFileLog.jsp" />
 * </dealMethod> <flowHandler name= "com.xt.demo.OutputFlowHandler"> <screen
 * name="ListDatAcctOutputFileLog"
 * path="/${default}/ListDatAcctOutputFileLog.jsp" /> <screen
 * name="AddDatAcctOutputFileLog"
 * path="/${default}/ListDatAcctOutputFileLog.jsp" /> </flowHandler> </action>
 */

class Key {
	private final String packages;

	private final String bizHandler;

	public Key(String packages, String bizHandler) {
		this.packages = packages;
		this.bizHandler = bizHandler;
	}

	/**
	 * 一般情况下，bizHadler和method是不能为空的
	 * 
	 * @param pp
	 * @return 是否相等
	 */
    @Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Key)) {
			return false;
		}
		Key pp = (Key) obj;
		if (pp.bizHandler == null || bizHandler == null
				|| !bizHandler.equals(pp.bizHandler)) {
			return false;
		}

		if (pp.packages != null && !pp.packages.equals(packages)) {
			return false;
		}
		if (packages != null && !packages.equals(pp.packages)) {
			return false;
		}
		return true;
	}

    @Override
	public int hashCode() {
		int code = 0;
		if (bizHandler != null) {
			code = code + bizHandler.hashCode();
		}
		if (packages != null) {
			code = code + packages.hashCode();
		}
		return code;
	}

    @Override
	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		if (bizHandler != null) {
			strBuf.append(bizHandler.toString());
		}
		strBuf.append("@");
		if (packages != null) {
			strBuf.append(packages.toString());
		}
		return strBuf.toString();
	}

}