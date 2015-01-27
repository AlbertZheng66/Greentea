
package com.xt.core.app;

/**
 *
 * @author Albert
 */
public interface Terminator {
    
    public void init(String id);
    
    public void stop(String id);
    
    public boolean isStoped(String id);
}
