package com.xt.core.validation;

/**
 *
 * @author albert
 */
public enum Level {

    /**
     * 成功信息
     */
    SUCCESS,
    
    /**
     * 提示信息
     */
    PROMPT,
    /**
     * 警告信息
     */
    WARNING,
    /**
     * 错误信息
     */
    ERROR,
    
    /**
     * 严重错误信息，一般是系统设计错误（Bug）引起的错误信息。
     */
    FATAL;
}
