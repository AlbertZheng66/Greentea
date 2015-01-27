package com.xt.test.conf;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jdom.Element;

import com.xt.core.utils.ClassHelper;
import com.xt.core.utils.XmlHelper;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class Configuration
{
    public static final String CONFIGURATION = "configuration";

    public static final String CLASS = "class";

    public static final String VALUE_ELEMENT = "value-element";

    public static final String CODE_NAME = "code-attr";

    public static final String FILE_PATH = "file-path";

    public static final String NAMESPACE = "namespace";

    public static final String DEFAULT_PATH = "default_path";

    private static Configuration instance = null;

    /**
     * 装载所有配置文件的名字空间，主键是名字空间字符串，键值是码值Hash表。
     */
    private Map namespaces = new HashMap();

    private Configuration ()
    {
    }

    public static Configuration newInstance ()
    {
        if (instance == null)
        {
            instance = new Configuration();
        }
        return instance;
    }

    /**
     * 装载配置文件
     */
    public void load (InputStream is)
        throws ConfigurationException
    {
        String fileName = "";
        try
        {
            Element root = XmlHelper.getRoot(is);
            //配置文件的缺省路径
            String defaultPath = root.getAttributeValue(this.DEFAULT_PATH);
            for (Iterator iter = root.getChildren().iterator(); iter.hasNext(); ) {
                Element conf = (Element)iter.next();
                //读取配置文件属性
                String className = conf.getChildText(CLASS);  //类名称
                String namespace = conf.getAttributeValue(NAMESPACE);  //名字空间
                String value = conf.getChildText(this.VALUE_ELEMENT);  //值节点的名称
                String codeAttr = conf.getChildText(VALUE_ELEMENT); //代表编码的属性名称
                fileName = conf.getChildText(FILE_PATH);
                InputStream confFileIs = loadFile (defaultPath + fileName);  //配置文件的路径
                Element confFile = XmlHelper.getRoot(confFileIs);
                //生成配置文件，并装载到HashMap中
                namespaces.put(namespace, loadConcreteConfiguration(confFile, className, value,
                                                                    codeAttr));
            }
        }
        catch (Exception ex)
        {
            throw new ConfigurationException("XML解析异常！file:" + fileName, ex);
        }
    }

    private Map loadConcreteConfiguration (Element confFile, String className,
                                           String value, String codeAttr)
        throws ConfigurationException
    {
        Map codeTable = new HashMap();
        try
        {
            //根据类名称得到其对应的类
            Class clazz = ClassHelper.getClass(className, false);

            //即使是私有的构建函数也应该初始化
            IConfig config = (IConfig)ClassHelper.newInstance(clazz, true);

            //依次读取配置文件的节点，根据指定的节点名称
            for (Iterator iter = confFile.getChildren(value).iterator(); iter.hasNext(); ) {
                Element child = (Element)iter.next();
                String id = child.getAttributeValue(codeAttr);

                //将每个节点的值装载到映射表中
                codeTable.put(id, config.load(id, child));
            }
        }
        catch (ClassNotFoundException ex)
        {
            throw new ConfigurationException("类未发现，可能是类名称出现了错误！", ex);
        }
        catch (IllegalAccessException ex)
        {
            throw new ConfigurationException("异常存取异常", ex);
        }
        catch (InstantiationException ex)
        {
            throw new ConfigurationException("实例化异常", ex);
        }
        catch (java.lang.reflect.InvocationTargetException ex)
        {
            throw new ConfigurationException("", ex);
        }
        catch (java.lang.NoSuchMethodException ex)
        {
            throw new ConfigurationException("", ex);
        }

        return codeTable;
    }

    /**
     * 根据指定的路径装载文件
     * @param filePath String
     * @return InputStream
     */
    private InputStream loadFile (String filePath) {
        return null;
    }
}
