/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xt.comm.sqltest;

import com.xt.core.exception.ServiceException;

/**
 *
 * @author Albert
 */
public class SQLTesterException extends ServiceException{

    public SQLTesterException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SQLTesterException(String msg) {
        super(msg);
    }
}
