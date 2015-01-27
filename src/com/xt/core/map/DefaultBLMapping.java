package com.xt.core.map;

import com.xt.core.exception.BadParameterException;
import com.xt.core.utils.StringUtils;

/**
 * 缺省的事务逻辑层映射类。
 * <p>Title: XT框架-事务逻辑部分</p>
 * <p>Description:  </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-7-9
 */
public class DefaultBLMapping implements BLMapping {

	/**
	 * 需要在系统配置文件中进行配置
	 */
    private String defaultPackage = "com.xxx";
    
	/**
	 * 输入的URL形式为：/billing/UserMgr.do
	 */
	public String mapHandler(String url) {
		//检查参数是否合法
		if (url == null || url.trim().length() == 0) {
			throw new BadParameterException("无法映射相应的类！");
		}
		
		//如果参数中存在一个或者多个(多个连续视为一个)“\”，将其转换为"/"
		if (url.indexOf("\\") >= 0) {
			url = url.replaceAll("\\\\", "/");
		}
		
		//去掉开头的斜线
		while (url.startsWith("/")) {
			url = url.substring(1);
		}
		
		//去除连续的斜杠“/”
		while (url.indexOf("//") >=0) {
			url = url.replaceAll("//", "/");
		}
		
		//纠正参数的形式，转换成多个字段
		String[] segs = url.split("/");

		//最后一个字段应该去掉“.”后面的字符串，并将
		if (segs.length > 0) {
			String last = segs[segs.length - 1];
		    if (last.indexOf(".") > 0) {
		        last = last.substring(0, last.indexOf("."));
		    }
		    segs[segs.length - 1] = StringUtils.capitalize(last);
		}
		
		return defaultPackage + "." + StringUtils.join(segs, ".");
	}

	/**
	 * 输入的URL形式为：
	 */
	public String mapMethod(String url) {
		return url;
	}

}
