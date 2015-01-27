
package com.xt.proxy.impl.http.json;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

/**
 * 用于将异常包装成一个便于 进行 JSON 处理的字符串。
 * @author albert
 */
public class ExceptionWrapper implements Serializable {

    private static final long serialVersionUID = 2168030023082478094L;
    private String message;
    private String className;
    private String stackTrace;
    private ExceptionWrapper cause;

    /**
     * 需要忽略的异常（不需要到处到客户端）。
     */
    private static final Set<Class<? extends Throwable>> ignoredExceptions = new HashSet();

    static {
        ignoredExceptions.add(InvocationTargetException.class);
    }

    public ExceptionWrapper() {
    }

    public void load(Throwable wrappedException) {
        if (wrappedException == null) {
            return;
        }
        Throwable expected = wrappedException;
        while(expected != null && ignoredExceptions.contains(expected.getClass())) {
            if (expected.getCause() != null) {
                expected = expected.getCause();
            }
        }
        message = expected.getMessage();
        className = expected.getClass().getName();
        StringWriter sw = new StringWriter();
        expected.printStackTrace(new PrintWriter(sw));
        stackTrace = sw.toString();
        if (expected.getCause() != null) {
            ExceptionWrapper causeWrapper = new ExceptionWrapper();
            causeWrapper.load(expected.getCause());
            cause = causeWrapper;
        }
    }

    public ExceptionWrapper getCause() {
        return cause;
    }

    public void setCause(ExceptionWrapper cause) {
        this.cause = cause;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
