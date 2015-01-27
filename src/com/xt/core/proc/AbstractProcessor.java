
package com.xt.core.proc;

import com.xt.core.session.Session;
import com.xt.proxy.Context;
import java.lang.reflect.Method;

/**
 *
 * @author Albert
 */
abstract public class AbstractProcessor implements Processor {

    public void onCreate(Class serviceClass, Session session, Context context) {
    }

    public void onAfter(Object service, Object ret) {
    }

    public void onThrowable(Throwable t) {
    }

    public void onFinally() {
    }

    public void onFinish() {
    }
    
}
