package com.xt.core.db.kcg;

import java.io.IOException;
import java.util.Properties;

import com.xt.core.log.LogWriter;
import com.xt.core.utils.ClassHelper;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class KeyCodeGeneratorFactory
{
    private static KeyCodeGeneratorFactory instance = null;

    /**
     * 关键字产生器的配置文件
     */
//    private static final String CONFIG_FILE_NAME =
//        "com/myco/core/db/kcg/KeyCodeGenerator.properties";
    private static final String CONFIG_FILE_NAME =
        "KeyCodeGenerator.properties";

    /**
     * 关键字产生器的具体实现类
     */
    private KeyGenerator keyCodeGenerator = null;

    private KeyCodeGeneratorFactory ()
    {
    }

    public static KeyCodeGeneratorFactory newInstance ()
    {
        if (instance == null)
        {
            instance = new KeyCodeGeneratorFactory();
            instance.init();
        }
        return instance;
    }

    private void init ()
    {
        Properties properties = new Properties();
        try
        {
            properties.load(KeyCodeGeneratorFactory.class.getResourceAsStream(CONFIG_FILE_NAME));
        }
        catch (IOException ex)
        {
            LogWriter.error(ex.getMessage(), ex);
        }
        //得到具体实现类的名称
        String className = properties.getProperty("class_name", null);
        LogWriter.debug("KeyCodeGeneratorFactory className=" + className);
        if (className != null)
        {
            keyCodeGenerator = (KeyGenerator)ClassHelper.newInstance(className);
        }
    }

    public KeyGenerator newGenerator ()
    {
        return keyCodeGenerator;
    }
}
