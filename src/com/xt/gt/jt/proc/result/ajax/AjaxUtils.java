package com.xt.gt.jt.proc.result.ajax;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xt.core.exception.BLException;
import com.xt.core.exception.ServiceException;
import com.xt.core.exception.ValidateException;
import com.xt.core.utils.BooleanUtils;
import com.xt.gt.jt.screen.ScreenFlowManager;
import com.xt.gt.sys.SystemConfiguration;

public class AjaxUtils {
	/**
	 * 如果一个HTTP请求是AJAX请求，则需要在HTTP请求中放置一个“_ajax_”参数，并赋值为真。
	 * 真值包括："yes","y","1","on","true".
	 */
	public static final String AJAX_IN_REQUEST = "_ajax_";
	
	/**
	 * 输出XML的编码格式
	 */
	private static final String ENCODING;
	
	static {
		SystemConfiguration sc = SystemConfiguration.getInstance();
		ENCODING = sc.readString("AJAX_ENCODING", "gbk");
	}

	/**
	 * 判断一个请求是否是Ajax请求，如果是ajax请求将关闭屏幕流
	 * 
	 * @param req
	 *            HTTP请求
	 * @return 如果参数中的_ajax_的值为（"Y","true","1"）等则返回真，否则返回假
	 */
	public static boolean isAjaxRequest(HttpServletRequest req) {
		boolean ajax = BooleanUtils.isTrue(req.getParameter(AJAX_IN_REQUEST),
				false);

		if (ajax) {
			ScreenFlowManager.disable(req);
		}

		return ajax;
	}

	public static void toAjax(OutputStream os, HttpServletResponse res, Object value) {
		res.setHeader("Charset", ENCODING);
		ToAjax toAjax = AjaxFactory.getToAjax(value);
		StringBuilder strBld = new StringBuilder();
		strBld.append("<return ");
		toAjax.appendAttributes(strBld, value);
		strBld.append(" >");
		toAjax.appendChildren(strBld, value);
		strBld.append("</return>");
		output(os, strBld.toString());
	}
	
	

	private static void output(OutputStream os, String str) {
		BufferedOutputStream bos = new BufferedOutputStream(os);
		StringBuilder output = new StringBuilder("<?xml version=\"1.0\""
				+ " encoding=\"").append(ENCODING).append("\"?>");
		output.append("<response>");
		try {
			output.append(str);
			output.append("</response>");
//			LogWriter.debug("output=", output.toString());
			bos.write(output.toString().getBytes(ENCODING));
			bos.flush();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void toAjax(OutputStream os, HttpServletResponse res, Throwable t) {
		res.setHeader("Charset", ENCODING);
		StringBuilder strBld = new StringBuilder();
		String type = null;
		String msg = null;
		String calssName = null;
		if (getExcepiton(t, ValidateException.class) != null) {
			type = "validate";
			msg = ((ValidateException) getExcepiton(t, ValidateException.class))
					.getCode();
			calssName = ValidateException.class.getName();
		} else if (getExcepiton(t, ServiceException.class) != null) {
			type = "service";
			msg = ((ServiceException) getExcepiton(t, ServiceException.class))
					.getMessage();
			calssName = ServiceException.class.getName();
		} else if (getExcepiton(t, BLException.class) != null) {
			type = "bl";
			msg = ((BLException) getExcepiton(t, BLException.class))
					.getMessage();
			calssName = BLException.class.getName();
		} else {
			type = "system";
			calssName = t.getClass().getName();
			msg = t.getMessage();
		}
		
		msg = (msg == null ? "" : msg); 
		strBld.append("<exception className=\"").append(calssName).append(
				"\" type=\"").append(type);
		strBld.append("\" >");
		strBld.append("<msg>").append(msg).append("</msg>");
		strBld.append("</exception>");
//		StringBuilder strBld = new StringBuilder("<return  type=\"list\"  ><value   type=\"Object\" className=\"com.gt.efoodoo.adr.CfgDistrict\" >" +
//				        "<field  name=\"validate\" type=\"null\"  ></field>" +
//				        "<field  name=\"cityCode\" type=\"String\"  >0312</field>" +
//				        "<field  name=\"districtCode\" type=\"String\"  >340</field>" +
//				        "<field  name=\"zipCode\" type=\"null\"  ></field>" +
//				    "</value></return>");

		output(os, strBld.toString());
	}

	/**
	 * 根据指定的异常查找异常的原因
	 * 
	 * @param t
	 * @param expClass
	 *            目标类
	 * @return
	 */
	private static Throwable getExcepiton(Throwable t, Class expClass) {
		Throwable target = null;
		Throwable temp = t;
		while (true) {
			if (temp == null || temp.getClass() == expClass) {
				target = temp;
				break;
			}
			temp = temp.getCause();
		}
		return target;
	}
}

/**
 * 输出样例
 * 
 * <response> <exception class="com.gt.BaseException" type="prompt" > <msg>
 * </msg> </exception> <return type="boolean" value="1" /> <!-- <return
 * type="Object" className="com.ll.yy.User"> <field name="name" type="String"
 * value="aa" /> <field name="name" type="com.ll.yy.Address"> <field name="city"
 * type="String" value="beijing" /> <field name="district" type="String"
 * value="haidian" /> </field> <field name="aMap" type="map"> <key name="1"
 * type="String" value="111" /> <key name="2" type="Object" > </key> </field>
 * <field name="aMap" type="list"> <value type="String" value="111" /> <value
 * type="Object" > </value> </field> </return> --> </response>
 */
