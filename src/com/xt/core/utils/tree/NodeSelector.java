
package com.xt.core.utils.tree;

/**
 *
 * @author Administrator
 */
public interface NodeSelector<T> {
    
    public boolean isRoot(T node);
    
    public boolean isChild(T parent, T node);
    
     public boolean isParent(T parent, T node);
    
}
