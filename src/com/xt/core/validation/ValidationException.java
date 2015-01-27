
package com.xt.core.validation;

import com.xt.core.exception.SystemException;

/**
 *
 * @author albert
 */
public class ValidationException extends SystemException {
    
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(String message) {
        super(message);
    }

}