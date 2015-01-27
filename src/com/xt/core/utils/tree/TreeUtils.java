package com.xt.core.utils.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>Title: GreeTea 框架。</p> <p>Description: </p> <p>Copyright: Copyright (c)
 * 2003</p> <p>Company: </p>
 *
 * @author 郑伟
 * @version 1.0
 */
public class TreeUtils {

    public TreeUtils() {
    }

    /**
     * 此树节点是否是叶子节点，如果是，返回真，否则返回假。
     *
     * @param node TreeNode
     * @return boolean 如果是叶子节点，返回真，否则返回假。
     */
    public static boolean isLeaf(TreeNode node) {
        boolean leaf = false;
        //没有子节点（为空），或者，子节点的个数为0
        if (node.getChildren() == null || node.getChildren().isEmpty()) {
            leaf = true;
        }
        return leaf;
    }

    /**
     * 此树节点是否是根节点，如果是，返回真，否则返回假。
     *
     * @param node TreeNode
     * @return boolean 如果是根节点，返回真，否则返回假。
     */
    public static boolean isRoot(TreeNode node) {
        boolean root = false;
        //没有父节点（为空）
        if (node.getParent() == null) {
            root = true;
        }
        return root;
    }

    /**
     * 返回指定节点的所有的子节点的个数（包含所有的后继节点）。不包含本身的节点个数
     *
     * @param node TreeNode
     * @return int 子节点的个数。
     */
    public static int getDescendantCount(TreeNode node) {
        return getDescendantCountIncluded(node) - 1;
    }

    /**
     * 返回指定节点的所有的子节点的个数（包含所有的后继节点）。包含其本身的节点个数
     *
     * @param node TreeNode
     * @return int 子节点的个数。
     */
    private static int getDescendantCountIncluded(TreeNode node) {
        int count = 1;
        //通过递归方式得到子节点的个数
        if (node.getChildren() != null) {
            for (Iterator iter = node.getChildren().iterator(); iter.hasNext();) {
                TreeNode child = (TreeNode) iter.next();
                count += getDescendantCountIncluded(child);
            }
        }
        return count;
    }

    /**
     * 返回指定节点的所有的叶子节点的个数（包含所有的后继的叶子节点）。如果本身是叶子节点，返回1。
     *
     * @param node TreeNode
     * @return int 子叶子节点的个数。
     */
    public static int getLeafCount(TreeNode node) {
        int count = 0;
        //通过递归方式得到子叶子节点的个数
        if (!isLeaf(node)) {
            for (Iterator iter = node.getChildren().iterator(); iter.hasNext();) {
                TreeNode child = (TreeNode) iter.next();
                count += getLeafCount(child);
            }
        } else {
            //只有叶子节点计数，非叶子节点不计数
            count = 1;
        }
        return count;
    }

    /**
     * 返回指定节点到与其相距最远（深度最大）的叶子节点的层数数。如果本身是叶子节点，返回1。
     *
     * @param node TreeNode
     * @return int 节点的深度最大。
     */
    public static int getMaxDepth(TreeNode node) {
        return (getMaxDepthIncluded(node) - 1);
    }

    private static int getMaxDepthIncluded(TreeNode node) {
        int depth = 0;
        //通过递归方式得到子叶子节点的最大深度
        if (!isLeaf(node)) {
            for (Iterator iter = node.getChildren().iterator(); iter.hasNext();) {
                TreeNode child = (TreeNode) iter.next();
                depth = Math.max(depth, getMaxDepthIncluded(child));
            }
        }
        return ++depth;
    }
    
    /**
     * Not finished yet.
     * @param <T>
     * @param nodes
     * @param selector
     * @return 
     */
     static public <T> List<T> buildTree(List<T> nodes, NodeSelector<T> selector) {
        final List roots = new ArrayList();
         for (Iterator<T> it = roots.iterator(); it.hasNext();) {
             T node = it.next();
             if (selector.isRoot(node)) {
                 roots.add(node);
             } else {
                 
             }
         }
        return roots;
    }
     
     static  public <T> List<T> findParent(List<T> roots, NodeSelector<T> selector, T child) {
         T parent = null;
         for (Iterator<T> it = roots.iterator(); it.hasNext();) {
             T node = it.next();
             if (selector.isRoot(node)) {
                 roots.add(node);
             } else {
                 
             }
         }
        return roots;
    }
}
