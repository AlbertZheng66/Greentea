package com.xt.views.taglib.html;

import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.TagUtils;

import com.xt.core.app.init.AppContext;
import com.xt.gt.sys.SystemConfiguration;

public class ImgTag extends BaseFieldTag {
	/**
	 * 图片的高度
	 */
	private String height;

	/**
	 * 图片的宽度
	 */
	private String width;

	/**
	 * 图片的边界宽度
	 */
	private int border;

	/**
	 * 如果属性值为空，则图片的路径（src）属性显示为src的指定值,可直接指定src的属性值， 系统将不再自动读取
	 */
	private String src;

	/**
	 * 无图片的时候显示的图片
	 */
	private static final String NON_PICTURE;

	static {
		SystemConfiguration sc = SystemConfiguration.getInstance();
		NON_PICTURE = sc.readString("NON_PICTURE",
				"pictures/_public/non_picture.jpg");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7329541872012402728L;

	public ImgTag() {
		super();
		this.property = "<null />"; // 标准标签中这个属性不能为空，赋予一个模拟值
	}

	/**
	 * 显示一个<div> 控件标签.
	 * 
	 * @throws JspException
	 * @since Struts 1.2
	 */
	protected String renderInputElement() throws JspException {
		StringBuffer results = new StringBuffer("<img");
		results.append(prepareStyles());
		results.append(" ");
		
		String imgPath;

		// 如果用户没有指定src，则到属性中进行读取
		if (src == null) {
			Object value = null;
			if (property != null) {
				value = TagUtils.getInstance().lookup(pageContext, name,
						property, null);
			} else {
				throw new JspException("属性值[property]不能同时为空！");
			}

			if (value == null || !(value instanceof String)) {
				imgPath = NON_PICTURE; // 显示默认图片
			} else {
				imgPath = (String) value;
			}
		} else {
			imgPath = src;
		}

		// 如果图形没有以根"/"开头，这增加一个根路径
		if (!imgPath.startsWith("/")) {
		    AppContext ac = AppContext.getInstance();
		    imgPath = ac.createContextPath(imgPath);
		}
		
		prepareAttribute(results, "src", imgPath);
		if (height != null) {
			prepareAttribute(results, "height", height);
		}
		if (width != null) {
			prepareAttribute(results, "width", width);
		}
		prepareAttribute(results, "alt", getAlt());
		
		results.append(prepareEventHandlers());
		
		results.append("></img>");
		return results.toString();
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public int getBorder() {
		return border;
	}

	public void setBorder(int border) {
		this.border = border;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

}
