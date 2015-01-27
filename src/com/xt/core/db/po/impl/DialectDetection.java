package com.xt.core.db.po.impl;

import com.xt.core.db.po.Dialect;
import com.xt.core.exception.SystemException;
import com.xt.core.utils.ClassHelper;
import com.xt.gt.sys.SystemConfiguration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 根据驱动程序的信息确定使用的方言类。
 * @author albert
 */
public class DialectDetection {

    private static DialectDetection instance = new DialectDetection();
    private Map<String, Class> dialects = new HashMap();

    private DialectDetection() {
        // 注意：名称不区分大小写
        Map<String, String> _dialects = SystemConfiguration.getInstance().readMap("dialects");
        // 如果用户未定义，则采用系统定义
        if (_dialects != null) {
            for (Iterator<Map.Entry<String, String>> iter = _dialects.entrySet().iterator(); iter.hasNext();) {
                Map.Entry<String, String> entry = iter.next();
                try {
                    Class clazz = Class.forName(entry.getValue());
                    if (Dialect.class.isAssignableFrom(clazz)) {
                        throw new SystemException(String.format("类[%s]必须实现接口[%s]。",
                                entry.getValue(), Dialect.class.getName()));
                    }
                    dialects.put(entry.getKey(), clazz);
                } catch (ClassNotFoundException ex) {
                    throw new SystemException(String.format("未发现类[%s]。", entry.getValue()), ex);
                }
            }
        } else {
            dialects.put("Oracle", OracleDialect.class);
            dialects.put("Hsql", HsqlDialect.class);
        }
    }

    public static DialectDetection getInstance() {
        return instance;
    }

    synchronized public Dialect getDialect(String driverInfo) {
        if (driverInfo == null) {
            return null;
        }
        for (Iterator<Map.Entry<String, Class>> iter = dialects.entrySet().iterator(); iter.hasNext();) {
            Map.Entry<String, Class> entry = iter.next();
            if (driverInfo.toUpperCase().contains(entry.getKey().toUpperCase())) {
                return (Dialect) ClassHelper.newInstance(entry.getValue());
            }
        }
        return null;
    }
}
