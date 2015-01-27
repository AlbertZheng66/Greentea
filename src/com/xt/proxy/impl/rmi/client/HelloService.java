
package com.xt.proxy.impl.rmi.client;

import com.xt.core.service.IService;
import java.io.Serializable;

/**
 *
 * @author Albert
 */
public class HelloService implements IService, Serializable {

    public HelloService() {
    }
    
    public long sayHello() {
        System.out.println("Hello world.............");
        return System.currentTimeMillis();
    }
}
