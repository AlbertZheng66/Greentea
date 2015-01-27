package com.xt.core.db.conn;

import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;

import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: 通过属性文件com/myco/core/db/conn/database.properties定义数据库
 * 连接.数据库的名称，位置，等标识使用属性文件定义。属性文件的第一个数据源（数
 *     据库）的名称为默认名称，如果使用方法getConnection ()时，默认连接此数据库。这个
 *     类可以同多个数据库建立连接。
 </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @deprecated 
 */

public class PropertiesContextFactory
{
    /**
     * 默认的数据库配置文件的名
     */
//    public final static String DEFAULT_FILE_NAME = "com/myco/core/db/conn/database.properties";
    public final static String DEFAULT_FILE_NAME = "database.properties";

    private static PropertiesContextFactory instance;

    private PropertiesContextFactory()
    {
        LogWriter.debug("PropertiesContextFactory.................");
    }

    public static PropertiesContextFactory newInstance() throws SystemException
    {
        if (instance == null)
        {
            instance = new PropertiesContextFactory();
            instance.loadFile(DEFAULT_FILE_NAME);
        }
        return instance;
    }

    /**
     * 传入配置文件的名称，读取配置文件的内容并付给相应的属
     * @param fileName 配置文件的名
     * @throws SystemException
     */
    private void loadFile(String fileName) throws SystemException
    {
        LogWriter.debug("PropertiesContextFactory fileName=" + fileName);
        Properties properties = new Properties();
        try
        {
//            properties.load(getClass().getClassLoader().getResourceAsStream(fileName));
            properties.load(PropertiesContextFactory.class.getResourceAsStream(fileName));
        } catch (IOException ex)
        {
            throw new SystemException("00115", ex);
        }
        //得到数据库的名称
        String dbNames = properties.getProperty("dbNames", null);
        LogWriter.debug("PropertiesContextFactory dbNames=" + dbNames);
        if (dbNames != null)
        {
            StringTokenizer st = new StringTokenizer(dbNames, ",");
            while (st.hasMoreTokens())
            {
                String id = st.nextToken(); //数据库名

                //将数据库上下文装载到数据库上下文集合
                DatabaseContext context = loadContext(properties, id);

                DatabaseContextManager manager = DatabaseContextManager.getInstance();
                //第一个数据库的为默认数据
                if (manager.getDefaultId() == null)
                {
                    manager.setDefaultId(id);
                }
                manager.add(context);
            }
        }
    }

    private DatabaseContext loadContext(Properties properties, String id) throws SystemException
    {
        DatabaseContext context = new DatabaseContext();
        context.setId(id);
        context.setUrl(properties.getProperty(id + ".url", null));
        context.setDatabaseName(properties.getProperty(id + ".databaseName", null));
        context.setUserName(properties.getProperty(id + ".userName", null));
        context.setPassword(properties.getProperty(id + ".password", null));
        context.setPort(properties.getProperty(id + ".port", null));
        context.setType(properties.getProperty(id + ".type", null));
        context.setEncoding(properties.getProperty(id + ".encoding", null));
        context.setPrefix(properties.getProperty(id + ".prefix", null));
        context.setDriverClass(properties.getProperty(id + ".driverClass", null));
        context.setDescription(properties.getProperty(id + ".description", null));
        return context;
    }
}
