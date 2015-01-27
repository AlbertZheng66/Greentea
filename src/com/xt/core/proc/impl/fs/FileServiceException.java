

package com.xt.core.proc.impl.fs;

import com.xt.core.exception.SystemException;

/**
 *
 * @author albert
 */
public class FileServiceException extends SystemException {

    public FileServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileServiceException(String message) {
        super(message);
    }
}
