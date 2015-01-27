package com.xt.core.utils.dic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Element;

import com.xt.core.db.conn.ConnectionFactory;
import com.xt.core.db.conn.DatabaseContext;
import com.xt.core.dd.DynamicDeploy;
import com.xt.core.dd.Loadable;
import com.xt.core.exception.BadParameterException;
import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import com.xt.core.utils.XmlHelper;
import com.xt.gt.sys.SystemConfiguration;
import com.xt.gt.sys.SystemConstants;

/**
 * 通过配置文件加载所有的数据库字典表信息，并对字典表进行缓存。
 * <p>
 * Title: XT框架-核心逻辑部分
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author 郑伟
 * @version 1.0
 * @date 2006-9-6
 */
public class DictionaryFactory {

    private final static Logger logger = Logger.getLogger(DictionaryFactory.class);
    private static DictionaryFactory instance = new DictionaryFactory();
    public final static String DICTIONARY_CONFIG_FILE = "DICTIONARY_CONFIG_FILE";
    /**
     * 配置文件
     */
    private String configFileName;

    private DictionaryFactory() {
    }

    public static DictionaryFactory getInstance() {
        if (instance.configFileName != null) {
//            // 检查一下是否需要重新装载配置文件
//            DynamicDeploy.getInstance().register(instance.configFileName, this);
        }
        return instance;
    }

    public void load(final String fileName) {
        LogWriter.debug(logger, "开始装载字典表数据......");
        if (fileName == null) {
            LogWriter.warn(logger, "字典表配置文件不存在！");
            return;
        }

        File file = new File(fileName);
        this.configFileName = file.getAbsolutePath();
        try {
            FileInputStream fis = new FileInputStream(file);
            load(fis);
        } catch (FileNotFoundException e) {
            throw new SystemException("读取字典配置文件[" + fileName + "]时出现错误！", e);
        }
    }

    /**
     * 从配置文件中装载
     *
     * @param is
     */
    public synchronized void load(InputStream is) {
        if (is == null) {
            LogWriter.warn(logger, "未发现字典表相关配置！");
            return;
        }

        // 字典服务
        DictionaryService dictionaryService = DictionaryService.getInstnace();

        // 打开数据库链接
        Connection conn = openConnection();

        Element root = XmlHelper.getRoot(is);

        try {
            // 所有字典表
            List dics = root.getChildren("dic");
            for (Iterator iter = dics.iterator(); iter.hasNext();) {
                Element dicElem = (Element) iter.next();
                String name = dicElem.getAttributeValue("name"); // 字典名称
                if (StringUtils.isEmpty(name)) {
                    LogWriter.warn(logger, "字典名称不能为空！！！");
                    continue;
                }
                LogWriter.info(logger, "开始装载字典[" + name + "]...");
                String type = dicElem.getAttributeValue("type"); // 字典类型
                Dictionary dictionary = null;
                if ("db".equals(type)) {
                    dictionary = getDBDictionary(name, dicElem.getAttributeValue("sql"));
                } else if ("xml".equals(type)) {
                    dictionary = getXmlDictionary(name, dicElem);
                } else {
                    LogWriter.warn(logger, "不能确定字典[" + name + "]的类型");
                }

                // 在字典服务中注册字典
                dictionaryService.register(name, dictionary);
            }
        } finally {
            ConnectionFactory.closeConnection(conn);
        }
    }

    private Connection openConnection() {
        SystemConfiguration systemConf = SystemConfiguration.getInstance();
        DatabaseContext dc = (DatabaseContext) systemConf.readObject(
                SystemConstants.DATABASE_CONTEXT, null);
        Connection conn = ConnectionFactory.getConnection(dc);
        return conn;
    }

    /**
     * 刷新数据库类型字典
     *
     * @param name
     *            数据库字典名称，如果字典名称为空，则抛出BadParameterException异常；
     *            如果字典名称没有注册，或者字典不是数据库类型字典，则抛出BadParameterException异常。
     */
    public synchronized void refresh(String name) {
        if (StringUtils.isBlank(name)) {
            throw new BadParameterException("字典名称不能为空");
        }

        // 字典服务
        DictionaryService dictionaryService = DictionaryService.getInstnace();

        Dictionary dic = dictionaryService.get(name);
        if (null == dic || dic instanceof DBDictionary) {
            throw new BadParameterException("字典[" + name + "]不存在或者非数据库字典");
        }

        // 重新装载
        DBDictionary dbDic = (DBDictionary) dic;

        Connection conn = openConnection();
        //
        try {
            dbDic.load(conn);
        } catch (SQLException e) {
            throw new SystemException("数据库链接异常", e);
        } finally {
            // 关闭数据库
            ConnectionFactory.closeConnection(conn);
        }

    }

    /**
     * 根据SQL语句装入字典
     *
     * @param sql
     *            字典表SQL
     * @return
     */
    private Dictionary getDBDictionary(String name, String sql) {
        DBDictionary dic = new DBDictionary(name);
        dic.setSql(sql);
        Connection conn = openConnection();
        try {
            dic.load(conn);
        } catch (SQLException e) {
            throw new SystemException("数据库链接异常", e);
        } finally {
            // 关闭数据库
            ConnectionFactory.closeConnection(conn);
        }
        return dic;
    }

    /**
     * 根据XML文件加载字典
     *
     * @param xml
     * @return
     */
    private Dictionary getXmlDictionary(String name, Element dicElem) {
        DefaultDictionary dic = new DefaultDictionary(name);
        List items = dicElem.getChildren("item");
        for (Iterator iter = items.iterator(); iter.hasNext();) {
            Element item = (Element) iter.next();
            String value = item.getAttributeValue("value");
            String title = item.getAttributeValue("title");
            boolean validate = !("false".equals(item.getAttributeValue("validate")));
            dic.addItem(title, value, validate);
        }
        return dic;
    }
}
