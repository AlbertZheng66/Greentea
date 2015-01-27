
package com.xt.proxy.impl.http.json;

import com.xt.core.exception.SystemException;

/**
 *
 * @author albert
 */
public class JsonClientException extends SystemException {

    /**
     * 构造函数
     * 传入异常信息
     */
    public JsonClientException(String msg) {
        super(msg);
    }

    /**
     * 构造函数
     * 传入异常信息和异常类的实例
     */
    public JsonClientException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
