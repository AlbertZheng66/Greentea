package com.xt.gt.sys.impl;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.xt.core.db.conn.DatabaseContext;
import com.xt.core.db.conn.DatabaseContextManager;
import com.xt.gt.sys.ParameterParser;
import com.xt.gt.sys.SystemConfigurationException;
import com.xt.gt.sys.SystemConstants;
import com.xt.gt.sys.SystemParameter;

/**
 * 数据库参数解析类,即负责解析配置文件(gt-config.xml)中数据库相关的信息。
 * @author albert
 */
public class DataBaseParameterParser implements ParameterParser {

    public static final String DB_PARSER_TAG_DATABASES = "databases";
    public static final String DB_PARSER_TAG_DATABASE = "database";
    public static final String DB_PARSER_TAG_DEFAULT = "default";
    public static final String DB_PARSER_TAG_DRIVER = "driver";
    public static final String DB_PARSER_TAG_ID = "id";
    public static final String DB_PARSER_TAG_JNDI = "jndi";
    public static final String DB_PARSER_TAG_NAME = "name";
    public static final String DB_PARSER_TAG_PASSWORD = "password";
    public static final String DB_PARSER_TAG_SCHEMA = "schema";
    public static final String DB_PARSER_TAG_TYPE = "type";
    public static final String DB_PARSER_TAG_URL = "url";
    public static final String DB_PARSER_TAG_USER = "user";
    public static final String DB_PARSER_TAG_VALUE = "value";

    public DataBaseParameterParser() {
    }
    
    public String getParameterName() {
        return SystemConstants.DATABASE_CONTEXT;
    }

    public Object parse(SystemParameter systemParameter) {
        if (systemParameter == null) {
            throw new SystemConfigurationException("没有配置数据源，请检查配置文件！");
        }
        String defaultId = systemParameter.getAttributeValue(DB_PARSER_TAG_DEFAULT);
        if (StringUtils.isNotEmpty(defaultId)) {
            DatabaseContextManager.getInstance().setDefaultId(defaultId);
        }
        List<SystemParameter> paramsElem = systemParameter.getChildren();
        for (SystemParameter param : paramsElem) {
            loadDatabaseContext(param);
        }
        return DatabaseContextManager.getInstance().getDefaultDatabaseContext();
    }

    protected DatabaseContext loadDatabaseContext(SystemParameter systemParameter) {
        String id = systemParameter.getAttributeValue(DB_PARSER_TAG_ID);
        if (StringUtils.isEmpty(id)) {
            throw new SystemConfigurationException("数据源编码不能为空。");
        }
        DatabaseContext parsedDatabaseContext = new DatabaseContext();
        parsedDatabaseContext.setId(id);
        DatabaseContextManager.getInstance().add(parsedDatabaseContext);

        String type = systemParameter.getAttributeValue(DB_PARSER_TAG_TYPE);
        List<SystemParameter> paramsElem = systemParameter.getChildren();
        String schema = getValue(paramsElem, DB_PARSER_TAG_SCHEMA);
        // 如果类型为空，则认为是JDBC类型，开始装载JDBC类型的数据源
        if (type == null || DatabaseContext.JDBC.equals(type)) {
            parsedDatabaseContext.setType(DatabaseContext.JDBC);
            parsedDatabaseContext.setDriverClass(getValue(paramsElem, DB_PARSER_TAG_DRIVER));
            parsedDatabaseContext.setUrl(getValue(paramsElem, DB_PARSER_TAG_URL));
            String user = getValue(paramsElem, DB_PARSER_TAG_USER);
            parsedDatabaseContext.setUserName(user);
            parsedDatabaseContext.setPassword(getValue(paramsElem, DB_PARSER_TAG_PASSWORD));
            // 如果模式为空则采用用户名称作为模式
            if (schema == null) {
                schema = user;
            }
        } else if (DatabaseContext.DATA_SOURCE.equals(type)) {
            // 开始装载DATA_SOURCE类型的数据源
            parsedDatabaseContext.setType(DatabaseContext.DATA_SOURCE);
            parsedDatabaseContext.setDatabaseName(getValue(paramsElem, DB_PARSER_TAG_JNDI));
        } else {
            throw new SystemConfigurationException(String.format("不能识别的数据源类型[%s]", type));
        }

        parsedDatabaseContext.setSchema(schema);
        return parsedDatabaseContext;
    }

    private String getValue(List<SystemParameter> paramsElem, String name) {
        for (Iterator<SystemParameter> iter = paramsElem.iterator(); iter.hasNext();) {
            SystemParameter param = (SystemParameter) iter.next();
            if (name.equals(param.getAttributeValue(DB_PARSER_TAG_NAME))) {
                return param.getAttributeValue(DB_PARSER_TAG_VALUE);
            }
        }
        return null;
    }
}
