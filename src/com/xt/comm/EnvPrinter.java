
package com.xt.comm;

import com.xt.core.service.IService;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Albert
 */
public class EnvPrinter implements IService {
    
    public void print() {
        System.getProperties().list(System.out);
    }
    
    public String read(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        return System.getProperty(name);
    }
    
    public Map readAll() {
        return System.getProperties();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        EnvPrinter ep = new EnvPrinter();
        ep.print();
    }
}
