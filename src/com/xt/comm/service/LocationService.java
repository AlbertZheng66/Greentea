/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xt.comm.service;

import com.xt.core.exception.ServiceException;
import com.xt.core.service.IService;
import com.xt.core.utils.ClassHelper;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Albert
 */
public class LocationService implements IService {

    public String getURL(String className) {
        if (StringUtils.isEmpty(className)) {
            return "";
        }
        Class clazz = ClassHelper.getClass(className);
        if (clazz == null) {
            throw new ServiceException(String.format("未发现类[%s]的定义。", className));
        }
        
        String strURL = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
        return strURL;
    }
}
