
package com.xt.proxy.impl.restful;

import com.xt.core.exception.SystemException;

/**
 * 和RestfulServlet相关的异常.
 * @author albert
 */
public class RestfulException extends SystemException {
    
    private static final long serialVersionUID = -5256572474506683463L;

    public RestfulException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestfulException(String message) {
        super(message);
    }


}
