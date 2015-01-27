package com.xt.views.taglib.html;

import java.util.Iterator;
import java.util.List;

import javax.servlet.jsp.JspTagException;

import org.apache.struts.taglib.html.BaseHandlerTag;

import com.xt.core.log.LogWriter;
import com.xt.core.utils.CollectionUtils;
import com.xt.core.utils.tree.TreeUtils;

/**
 * <ul id="tree-checkmenu" class="checktree">
 * <li id="show-explorer"> <input id="check-explorer" type="checkbox" />
 * Internet Explorer <span id="count-explorer" class="count"></span>
 * <ul id="tree-explorer">
 * <li id="show-iemac"> <input id="check-iemac" type="checkbox" /> Macintosh
 * <span id="count-iemac" class="count"></span>
 * <ul id="tree-iemac">
 * <li><input type="checkbox" />v4.0</li>
 * <li class="last"><input type="checkbox" />v5.0</li>
 * </ul>
 * </li>
 * <li id="show-iewin" class="last"> <input id="check-iewin" type="checkbox" />
 * Windows <span id="count-iewin" class="count"></span>
 * <ul id="tree-iewin">
 * <li><input type="checkbox" />v4.0</li>
 * <li><input type="checkbox" />v5.0</li>
 * </ul>
 * </li>
 * </ul>
 * </li>
 * </ul>
 * 
 * <p>
 * Title: XT框架-事务逻辑部分
 * </p>
 * <p>
 * Description:
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
 * @date 2006-10-31
 */

public class TreeCtlTag extends BaseHandlerTag {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2995967776949885590L;

	private String name;

	private String id;
	
	private List treeNodes;
	
	enum TreeCtlTagType {
	    Radio, Checkbox 
	};

	private TreeCtlTagType type = TreeCtlTagType.Radio;

	public TreeCtlTag() {
		super();
	}

	public int doStartTag() throws JspTagException {
		treeNodes = (List) pageContext.findAttribute(name);
		if (treeNodes == null) {
			throw new JspTagException("未发现名称为[" + name + "]的属性");
		}
		LogWriter.debug("treeNodes", treeNodes.size());
		return EVAL_BODY_INCLUDE;
	}

	public int doEndTag() throws JspTagException {
		try {
			if (!CollectionUtils.isEmpty(treeNodes)) {
				StringBuilder content = new StringBuilder(
						"<ul id='tree-checkmenu' class='checktree'>");

				for (Iterator nodes = treeNodes.iterator(); nodes.hasNext();) {
					Object tcn = nodes.next();
					if (tcn instanceof TreeCtlNode) {
						create(content, (TreeCtlNode) tcn);
					}
				}
				content.append("</ul>");
				pageContext.getOut().write(content.toString());
			}
		} catch (java.io.IOException e) {
			throw new JspTagException("IO Error: " + e.getMessage());
		}
		return EVAL_PAGE;
	}

	private void create(StringBuilder strBld, TreeCtlNode treeNode) {
		//
		if (TreeUtils.isLeaf(treeNode)) {
			strBld.append("<li><input type='checkbox' />").append(
					treeNode.getTitle()).append("</li>");
		} else {
			strBld.append("<li id='show-").append(treeNode.getValue()).append(
					"' class='last'>");
			strBld.append("<input id='check-").append(treeNode.getValue())
					.append("' type='checkbox' ");
			if (treeNode.isChecked()) {
				strBld.append(" checked='true' ");
			}
			strBld.append("/>");
			strBld.append(treeNode.getTitle());
			// strBld.append("<span id='count-iewin' class='count'></span>");
			strBld.append("<ul id='tree-").append(treeNode.getValue()).append(
					"'>");
			// 输出所有子节点
			for (Iterator children = treeNode.getChildren().iterator(); children
					.hasNext();) {
				Object tn = children.next();
				if (tn instanceof TreeCtlNode) {
					create(strBld, (TreeCtlNode) tn);
				}
			}
			strBld.append("</ul>");
			strBld.append("</li>");
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TreeCtlTagType getType() {
		return type;
	}

	public void setType(TreeCtlTagType type) {
		this.type = type;
	}

}
