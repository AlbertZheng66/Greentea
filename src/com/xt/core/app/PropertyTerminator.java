package com.xt.core.app;

import com.xt.comm.PropertyManager;
import org.apache.log4j.Logger;

/**
 *
 * @author Albert
 */
public class PropertyTerminator implements Terminator {

    private final static String perpertyFileName = "starter.properties";
    
    private final PropertyManager propertyManager = new PropertyManager(perpertyFileName);
    
    private final static Logger logger = Logger.getLogger(Stoper.class);

    public void init(String id) {
        String name = getName(id);
        propertyManager.setValue(name, Boolean.FALSE.toString());
    }

    private String getName(String id) {
        return (id == null) ? "stopFlag" : (id + ".stopFlag");
    }
    
    public boolean isStoped(String id) {
        String name = getName(id);
        return propertyManager.getBoolean(name, false);
    }

   
    public void stop(String id) {
        String name = getName(id);
        propertyManager.setValue(name, Boolean.TRUE.toString());
    }
    
}
