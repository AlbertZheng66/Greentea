
package com.xt.comm;

import com.xt.core.exception.SystemException;

/**
 *
 * @author Albert
 */
public class PropertyException extends SystemException {
    private static final long serialVersionUID = -5624475831864034177L;

    public PropertyException(String message, Throwable cause) {
        super(message, cause);
    }

    public PropertyException(String message) {
        super(message);
    }
    
    
}
