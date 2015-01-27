
package com.xt.core.service.interupt;

import com.xt.core.service.IService;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Administrator
 */
public class CancellationManager {
    
    private static final CancellationManager instance = new CancellationManager();
    
    private final Map<String, Cancelable> store = new HashMap();
    
    private CancellationManager() {
        
    }
    
    static public CancellationManager getInstance() {
        return instance;
    }
    
    public Cancelable create(IService service) {
        return new ServiceCancelation(service);
    }
    
    /**
     * Set the percent, which means how much work has been done.
     * @param id an ID
     * @param ratio how much work has been done, from 0 to 100.
     */
    public void setProgress(String id, int ratio) {
        Cancelable cl = store.get(id);
        if (cl != null) {
            cl.setProgress(ratio);
        }
    }
    
    /**
     * get the percent of how much work has been done.
     * @param id an ID
     */
    public int getProgress(String id) {
        Cancelable cl = store.get(id);
        return (cl != null ? cl.getProgress() : -1);
    }
    
    /**
     * if the transaction is cancelled by the user.
     * @param id 
     */
    public boolean isCancelled(String id) {
        Cancelable cl = store.get(id);
        return (cl == null);
    }
    
    /**
     * Cancel the transaction which is identified by the ID
     * @param id 
     */
    public void cancel(String id) {
        Cancelable cl = store.get(id);
        if (cl != null) {
            cl.cancel();
        }
    }
    
}
