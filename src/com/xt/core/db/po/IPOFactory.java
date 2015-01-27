package com.xt.core.db.po;

import java.util.HashMap;
import java.util.Map;

import com.xt.core.cm.ClassModifierFactory;
import com.xt.core.cm.impl.IPOModifier;
import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import com.xt.core.utils.ClassHelper;
import java.util.Collections;
import org.apache.log4j.Logger;

/**
 * <p> Title: 持久化框架类. </p> <p> Description: 此类用于动态生产持久化的对象，自动织入持久化类需要的一些方法. </p>
 * <p> Copyright: Copyright (c) 2004 </p> <p> Company: </p>
 *
 * @author 郑伟
 * @version 1.0
 */
public class IPOFactory {
    
    private Logger logger = Logger.getLogger(IPOFactory.class);

    private static Map ipos = Collections.synchronizedMap(new HashMap());
    
    private static volatile IPOFactory instance = new IPOFactory();

    private IPOFactory() {
    }

    public static IPOFactory newInstance() {
        return instance;
    }

    public IPO create(Class ipo) {
        Class ipoClass = getClass(ipo);
        return (IPO) ClassHelper.newInstance(ipoClass);
    }

    /**
     * 将新类转换成IPO类
     *
     * @param base
     * @return
     */
    public synchronized Class getClass(Class base) {
        Class newClass = (Class) ipos.get(base);
        LogWriter.debug(logger, "IPOFactory.newClass", newClass);
        if (null == newClass) {
            ClassModifierFactory factory = ClassModifierFactory.getInstance();

            // 生产新的PO类
            newClass = factory.modify(base);

            if (!IPO.class.isAssignableFrom(newClass)) {
                throw new SystemException(String.format("类[%s]未进行修改，请查看系统是否注册了类修改器[%s]。",
                        base.getName(), IPOModifier.class.getName()));
            }

            ipos.put(base, newClass);
        }
        return (newClass);
    }
}
