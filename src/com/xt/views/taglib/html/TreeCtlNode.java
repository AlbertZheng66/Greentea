package com.xt.views.taglib.html;

import com.xt.core.utils.tree.TreeNode;

/**
 * 
 * <p>Title: XT框架-事务逻辑部分</p>
 * <p>Description:  </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-11-1
 */
public interface TreeCtlNode extends TreeNode {
	
	public String getName();
	
	public boolean isChecked();
	
	public String getValue();
	
	public String getTitle();

}
