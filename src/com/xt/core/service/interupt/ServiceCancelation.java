
package com.xt.core.service.interupt;

import com.xt.core.service.IService;
import java.util.UUID;

/**
 *
 * @author Administrator
 */
public class ServiceCancelation implements Cancelable {
    
    private final IService service;
    
    private final String id;

    public ServiceCancelation(IService service) {
        this.service = service;
        id = generateId();
    }

    private String generateId() {
     return UUID.randomUUID().toString();   
    }
    
    public String start() {
        return id;
    }

    public void setProgress(String id, int ratio) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getProgress(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isCancelled(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void cancel(String id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setProgress(int ratio) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getProgress() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isCancelled() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void cancel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
