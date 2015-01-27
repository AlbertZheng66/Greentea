package com.xt.core.exception;

/**
 * 在服务器（或者业务逻辑层）抛出此异常。
 * @author albert
 */
public class ServiceException extends BaseException {

    /**
     * 构造函数
     * 异常信息代码
     */
    public ServiceException(String msg) {
        super(msg);
    }

    /**
     * 构造函数
     * 异常信息代码
     */
    public ServiceException(String msg, Throwable cause) {
        super(msg, cause);
    }
}

