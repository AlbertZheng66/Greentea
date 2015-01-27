package com.xt.views.taglib.html;

import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.TagUtils;

import com.xt.core.app.init.AppContext;
import com.xt.gt.sys.SystemConfiguration;

/**
 * 查询标签。
 * <p>
 * Title: XT框架-显示逻辑部分
 * </p>
 * <p>
 * Description: 当处理一个复杂的关联时，可以使用查询标签。页面上显示一些输入框，
 * 和一个查询按钮，当点击查询按钮时将弹出一个查询列表对话框；用户选中需要的结果， 对话框消失，并将结果现在时输入域中。
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
 * @date 2006-9-26
 */
public class DialogTag extends BaseFieldTag {

	private static final long serialVersionUID = -8024658143957450778L;

	/**
	 * 读取数据的请求，不能为空
	 */
	private String initialAction;

	private String returnValue;

	private String id;

	private String width = "500";

	private String height = "360";

	private static String defaultFindImage = SystemConfiguration.getInstance()
			.readString("DEFAULT_FIND_IMAGE", "/images/find.gif");

	/**
	 * 查询按钮的图标
	 */
	private String findImage = defaultFindImage;

	/**
	 * 模式对话框
	 */
	private boolean modelDialog = true;

	/**
	 * 缺省的样式单
	 */
	private final static String DEFAULT_STYLE = "border:0px;background-color:#FFFFFF;cursor:hand";

	/**
	 * 清空按钮的图标
	 */
	private String clearImage = "";

	private boolean displayName = true;

	public DialogTag() {
		setStyle(DEFAULT_STYLE);
	}

	public int doStartTag() throws JspException {

		StringBuffer strBuf = new StringBuffer();
		createButton(strBuf, findImage);
		TagUtils.getInstance().write(pageContext, strBuf.toString());
		return (EVAL_BODY_INCLUDE);
	}

	private void createButton(StringBuffer strBuf, String image) {
//		HttpServletRequest request = (HttpServletRequest) pageContext
//				.getRequest();
//
//		String path = request.getContextPath();
//
//		// 适当的增加分隔符
//		if (StringUtils.isNotEmpty(path) && !path.endsWith("/")) {
//			path = path + "/";
//		}
//      用button的形式显示按钮
//		strBuf.append("<button ");
//		prepareAttribute(strBuf, "class", "btnClass");
//		
//		
//		strBuf.append(" onclick=\"return openDialog(");
//		if (initialAction.endsWith("()")) {
//			strBuf.append(initialAction
//					.substring(0, initialAction.length() - 2));
//		} else {
//			strBuf.append("'").append(path).append(initialAction).append("'");
//		}
//		strBuf.append(",'").append(returnValue).append("'");
//		strBuf.append(",'dialogName', '" + width + "', '" + height
//				+ "', this.form);\"");
//		strBuf.append("><img src='" + path + image + "' alt='不能显示此图形' ");
//
//		prepareAttribute(strBuf, "class", getStyleClass());
//
//		prepareAttribute(strBuf, "style", getStyle());
//		strBuf.append("/></button>");
		
		strBuf.append("<input type='button' value='&nbsp;&nbsp;&nbsp;' ");
		prepareAttribute(strBuf, "class", "dialogClass");
		
		
		strBuf.append(" onclick=\"return openDialog(");
		if (initialAction.endsWith("()")) {
			strBuf.append(initialAction
					.substring(0, initialAction.length() - 2));
		} else {
			AppContext ac = AppContext.getInstance();
			strBuf.append("'").append(ac.createContextPath(initialAction)).append("'");
		}
		strBuf.append(",'").append(returnValue).append("'");
		strBuf.append(",'dialogName', '" + width + "', '" + height
				+ "', this.form);\"");
		strBuf.append(" />");
	}

	public String getInitialAction() {
		return initialAction;
	}

	public void setInitialAction(String action) {
		this.initialAction = action;
	}

	public boolean isDisplayName() {
		return displayName;
	}

	public void setDisplayName(boolean displayName) {
		this.displayName = displayName;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getClearImage() {
		return clearImage;
	}

	public void setClearImage(String clearImage) {
		this.clearImage = clearImage;
	}

	public String getFindImage() {
		return findImage;
	}

	public void setFindImage(String findImage) {
		this.findImage = findImage;
	}

	public String getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(String returnValue) {
		this.returnValue = returnValue;
	}

}

/**
 * 
 * package com.qnuse.qframe.common.taglib;
 * 
 * import java.util.ArrayList; import java.util.Iterator; import java.util.List;
 * 
 * import javax.servlet.http.HttpServletRequest; import
 * javax.servlet.jsp.JspException; import javax.servlet.jsp.tagext.TagSupport;
 * 
 * import org.apache.commons.lang.StringUtils; import
 * org.apache.commons.logging.Log; import org.apache.commons.logging.LogFactory;
 * import org.apache.struts.taglib.TagUtils; import
 * org.apache.struts.taglib.html.BaseFieldTag; import
 * org.apache.struts.taglib.html.HiddenTag; import
 * org.apache.struts.taglib.html.TextTag;
 * 
 * public class LookupTag extends TagSupport { protected transient final Log
 * logger = LogFactory.getLog(getClass());
 * 
 * private String styleClass;
 * 
 * private String reqCode;
 * 
 * private String width = "500";
 * 
 * private String height = "500";
 * 
 * private String value;
 * 
 * private String backImage;
 * 
 * private String clearImage;
 * 
 * private String property;
 * 
 * private String size;
 * 
 * private String maxLength;
 * 
 * private String action = "/lookupAction.do";
 * 
 * private boolean isDisplayName = true;
 * 
 * private boolean isReadOnly = true;
 * 
 * 
 * 
 * 
 * public int doStartTag() throws JspException { List list = renderText();
 * 
 * int index = 0; for (Iterator iter = list.iterator(); iter.hasNext();) {
 * BaseFieldTag element = (BaseFieldTag) iter.next(); printTextTag(element);
 * element.doStartTag(); element.doEndTag();
 * 
 * if(!this.isDisplayName() && index == 0) { continue; } if (iter.hasNext())
 * TagUtils.getInstance().write(pageContext, "-"); } StringBuffer sb = new
 * StringBuffer(); //sb.append(renderTexts()); sb.append(renderButton());
 * TagUtils.getInstance().write(pageContext, sb.toString()); return
 * (EVAL_BODY_INCLUDE); }
 * 
 * private void printTextTag(BaseFieldTag element) { StringBuffer content = new
 * StringBuffer(200);
 * content.append("element.getName()=").append(element.getName());
 * content.append("\nelement.getOnclick()=").append(element.getOnclick());
 * content.append("\nelement.getOnselect()=").append(element.getOnselect());
 * content.append("\nelement.getId()=").append(element.getId());
 * content.append("\nelement.getProperty()=") .append(element.getProperty()); //
 * System.out.println(content.toString()); }
 * 
 * public int doEndTag() throws JspException {
 * 
 * return EVAL_PAGE; }
 * 
 * public void release() { styleClass = null;
 * 
 * reqCode = null;
 * 
 * width = "500";
 * 
 * height = "500";
 * 
 * value = null;
 * 
 * backImage = null;
 * 
 * property = null;
 * 
 * size = null;
 * 
 * action = "/lookupAction.do";
 * 
 * isDisplayName = true; isReadOnly = true; }
 * 
 * private List renderText() throws JspException { List l = new ArrayList();
 * String[] tempPro = property.split(","); String[] tempSize = size.split(","); //
 * 如果不给值，maxLength 没有限制 String[] tempMaxLength = null;
 * if(StringUtils.isNotEmpty(maxLength)) { tempMaxLength = maxLength.split(","); }
 * 
 * 
 * if( !isReadOnly && tempMaxLength == null) { // throw new
 * JspException("允许手工输入时，maxLength属性必须赋值"); }
 * 
 * if ( tempPro.length != tempSize.length ) { throw new
 * JspException("property与size属性不匹配"); } if( tempMaxLength !=null &&
 * tempMaxLength.length != tempSize.length) { throw new
 * JspException("maxLength属性不匹配"); }
 * 
 * 
 * for (int i = 0; i < tempPro.length; i++) { if(!this.isDisplayName() && i==1) {
 * HiddenTag hiddenTag = new HiddenTag(); hiddenTag.setProperty(tempPro[i]);
 * hiddenTag.setPageContext(pageContext);
 * 
 * l.add(hiddenTag); }else{ TextTag textFieldTag = new
 * org.apache.struts.taglib.html.TextTag();
 * textFieldTag.setProperty(tempPro[i]);
 * textFieldTag.setPageContext(pageContext); if (styleClass != null &&
 * !styleClass.equals("")) textFieldTag.setStyleClass(styleClass);
 * textFieldTag.setSize(tempSize[i]);
 * textFieldTag.setReadonly(this.isReadOnly());
 * 
 * if(tempMaxLength!=null) { textFieldTag.setMaxlength(tempMaxLength[i]); }
 * l.add(textFieldTag); } }
 * 
 * return l; }
 * 
 * 
 * private String renderTexts() throws JspException { String[] tempPro =
 * property.split(","); String[] tempSize = size.split(","); if (tempPro.length !=
 * tempSize.length) { throw new JspException("property与size属性不匹配"); }
 * StringBuffer inputText = new StringBuffer(""); for (int i = 0; i <
 * tempPro.length; i++) { if(this.isDisplayName()) { if (i != 0)
 * inputText.append("-"); inputText.append("<input type='text'");
 * inputText.append(" name=" + tempPro[i]); if (styleClass != null &&
 * !styleClass.equals("")) inputText.append(" class=" + styleClass);
 * inputText.append(" size=" + tempSize[i]); inputText.append(" readonly");
 * inputText.append("/>"); }else{ inputText.append("<input type='hidden'
 * name='").append(tempPro[i]).append(">"); } }
 * 
 * return inputText.toString(); }
 * 
 * private String renderButton() { StringBuffer inputButton = new
 * StringBuffer(); HttpServletRequest request = (HttpServletRequest) pageContext
 * .getRequest();
 * 
 * /** 解析Property属性，拆分成URL参数 String property = this.getProperty(); String[] pro =
 * property.split(","); String proQuery = ""; for(int i=0; i<pro.length; i++) {
 * if(i==0) { proQuery += "&_ReturnId=" + pro[i]; }else if(i==1){ proQuery +=
 * "&_ReturnName=" + pro[i]; }else{ proQuery += "&pro" + String.valueOf(i+1) +
 * "=" + pro[i]; } }
 * 
 * String path = request.getContextPath(); if (StringUtils.isNotEmpty(path) &&
 * !path.endsWith("/")) path = path + "/";
 * 
 * /** Lookup 弹出窗口的名字 String openWindowName = "lookupWindow"; int iStartPoint =
 * this.getAction().lastIndexOf("/") + 1; int iEndPoint =
 * this.getAction().lastIndexOf(".do"); if(iStartPoint > -1 && iEndPoint > -1) {
 * openWindowName = this.getAction().substring(iStartPoint, iEndPoint); }
 * 
 * if (backImage != null && !backImage.equals("")) { StringBuffer lc_buffer =
 * new StringBuffer(); lc_buffer.append(path);
 * 
 * if (!path.endsWith("/") && !backImage.startsWith("/")) {
 * lc_buffer.append('/'); } lc_buffer.append(backImage); inputButton.append("<button
 * style=\"border:0px;background-color:#FFFFFF;cursor:hand\"");
 * inputButton.append(" onclick=\"return openlookup('"); inputButton.append(path +
 * action + "?reqCode=" + reqCode + proQuery + "',"); inputButton .append("'" +
 * openWindowName + "'" + ", '" + width + "', '" + height + "');\"");
 * inputButton.append("><img src=\"" + lc_buffer.toString() + "\"
 * alt=\"").append(value).append("\""); // inputButton // .append(" alt=\"" // +
 * value // + "\" border=\"0\" style=\"cursor:hand\" align=\"absbottom\" "); // //
 * if (styleClass != null && !styleClass.equals("")) // inputButton.append("
 * class=" + styleClass); inputButton.append("/></button>"); } else {
 * inputButton .append("<input type=\"button\" value=\"" + value + "\"");
 * inputButton .append(" alt=\"" + value + "\" border=\"0\"
 * style=\"cursor:hand\" align=\"absbottom\" ");
 * inputButton.append("onclick=\"return openlookup('"); inputButton.append(path +
 * action + "?reqCode=" + reqCode + proQuery + "',"); inputButton .append("'" +
 * openWindowName + "'" + ", '" + width + "', '" + height + "');\""); if
 * (styleClass != null && !styleClass.equals("")) inputButton.append(" class=" +
 * styleClass); inputButton.append("/>"); }
 * 
 * if (clearImage != null && !clearImage.equals("")) { StringBuffer lc_buffer =
 * new StringBuffer(); lc_buffer.append(path);
 * 
 * if (!path.endsWith("/") && !backImage.startsWith("/")) {
 * lc_buffer.append('/'); } lc_buffer.append(clearImage); inputButton.append("<button
 * style=\"border:0px;background-color:#FFFFFF;cursor:hand\"");
 * inputButton.append(" onclick=\"return clearlookup('this', new Array(");
 * for(int i=0; i<pro.length; i++) {
 * inputButton.append("'").append(pro[i]).append("'"); if(i != pro.length-1)
 * inputButton.append(", "); } inputButton.append("));\"");
 * inputButton.append("><img src=\"" + lc_buffer.toString() + "\" alt=\"清除\""); //
 * inputButton // .append(" alt=\"清除\" border=\"0\" style=\"cursor:hand\"
 * align=\"absbottom\" "); // if (styleClass != null && !styleClass.equals("")) //
 * inputButton.append(" class=" + styleClass); inputButton.append("/></button>"); }
 * else { inputButton .append("<input type=\"button\" value=\"清除\"");
 * inputButton .append(" alt=\"清除\" border=\"0\" style=\"cursor:hand\"
 * align=\"absbottom\" "); inputButton.append("onclick=\"return
 * clearlookup('this', new Array("); for(int i=0; i<pro.length; i++) {
 * inputButton.append("'").append(pro[i]).append("'"); if(i != pro.length-1)
 * inputButton.append(", "); } inputButton.append("));\""); if (styleClass !=
 * null && !styleClass.equals("")) inputButton.append(" class=" + styleClass);
 * inputButton.append("/>"); }
 * 
 * return inputButton.toString(); }
 * 
 * public boolean isDisplayName() { // logger.debug("Get lookup display name
 * is:" + isDisplayName); return isDisplayName; } public void
 * setDisplayName(String isDisplayName) { // logger.debug("Set lookup display
 * name is:" + isDisplayName); // Boolean.getBoolean 不能用 // this.isDisplayName =
 * Boolean.getBoolean(isDisplayName); // 如果不给值，则取默认值为true，否则"true"以外的其他值返回false
 * if(StringUtils.isNotEmpty(isDisplayName)) { this.isDisplayName =
 * Boolean.valueOf(isDisplayName).booleanValue(); } } public boolean
 * isReadOnly() { return isReadOnly; } public void setReadOnly(boolean
 * isReadOnly) { this.isReadOnly = isReadOnly; } }
 */
