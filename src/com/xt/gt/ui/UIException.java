
package com.xt.gt.ui;

import com.xt.core.exception.SystemException;

/**
 * 与界面相关的错误均使用此异常。
 * @author albert
 */

public class UIException extends SystemException {
    public UIException(String message, Throwable cause) {
        super(message, cause);
    }

    public UIException(String message) {
        super(message);
    }

}

