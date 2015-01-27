package com.xt.core.utils.tree;

import java.util.Set;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: 代表树的一个节点。</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public interface TreeNode
{
    /**
     * 返回树节点的所有孩子节点(TreeNode实例的集合)。
     * @return 节点的所有孩子节点的集合
     */
    public Set getChildren ();

    /**
     * 返回树节点的父亲节点。如果返回为空，则表示此节点为根节点。
     * @return 节点的父亲节点
     */
    public TreeNode getParent ();
}
