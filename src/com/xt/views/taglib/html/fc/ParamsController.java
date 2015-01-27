package com.xt.views.taglib.html.fc;

import org.apache.commons.lang.StringUtils;

import com.xt.core.exception.BadParameterException;
import com.xt.core.utils.BooleanUtils;
import com.xt.gt.jt.event.RequestEvent;
import com.xt.views.taglib.html.IFieldController;

/**
 * 参数域控制器，根据一个参数判定输入域是否可见。参数的形式为:param{visiable=_visiable_,readonly=_readonly_}，如果Http请求或者属性中存在_visiable_
 * 则控件不可见；即默认为可见。
 * 
 * @author zw
 * 
 */
public class ParamsController implements IFieldController {

	/**
	 * 缺省的可见参数
	 */
	public static final String DEFAULT_VISIABLE_PARAM = "_visiable_";

	/**
	 * 缺省的只读参数
	 */
	public static final String DEFAULT_READONLY_PARAM = "_readonly_";

	public boolean isVisiable(RequestEvent requestEvent, String param) {
		if (StringUtils.isEmpty(param)) {
			throw new BadParameterException("参数域控制器的参数不能为空！");
		}
		return parseVisiale(requestEvent, param);
	}

	public boolean isReadonly(RequestEvent requestEvent, String param) {
		if (StringUtils.isEmpty(param)) {
			throw new BadParameterException("参数域控制器的参数不能为空！");
		}
		return parseReadonly(requestEvent, param);
	}

	private boolean parseVisiale(RequestEvent requestEvent, String param) {
		boolean visiable = true;
		String[] segs = param.split(",");
		String paramName = DEFAULT_VISIABLE_PARAM; // 参数名称
		if (segs != null) {
			for (int i = 0; i < segs.length; i++) {
				String seg = segs[i].trim();
				if (seg.contains("visiable")) {
					if (seg.indexOf("=") != -1) {
						// 自定义参数
						paramName = seg.substring(seg.indexOf("=") + 1);
					}
					visiable = validateParam(requestEvent, paramName);
				}
			}
		}

		return visiable;
	}

	private boolean parseReadonly(RequestEvent requestEvent, String param) {
		boolean visiable = true;
		String[] segs = param.split(",");
		String paramName = DEFAULT_READONLY_PARAM; // 参数名称
		if (segs != null) {
			for (int i = 0; i < segs.length; i++) {
				String seg = segs[i].trim();
				if (seg.indexOf("readonly") != -1) {
					if (seg.indexOf("=") != -1) {
						// 自定义参数
						paramName = seg.substring(seg.indexOf("=") + 1);
					}
					// 默认为非只读（可编辑）
					return !validateParam(requestEvent, paramName);
				}
			}
		}
		return visiable;
	}

	private boolean validateParam(RequestEvent requestEvent, String paramName) {
		Object attrValue = requestEvent.getAttribute(paramName);
		String value = attrValue == null ? "" : attrValue.toString();
		
		return ((BooleanUtils.isTrue(requestEvent.getParameter(paramName),
				false)) || BooleanUtils.isTrue(value, false));
	}
}
