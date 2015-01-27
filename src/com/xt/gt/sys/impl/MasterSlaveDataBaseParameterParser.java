
package com.xt.gt.sys.impl;

import com.xt.core.db.conn.DatabaseContext;
import com.xt.core.db.conn.DatabaseContextManager;
import com.xt.core.exception.BadParameterException;
import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import com.xt.gt.sys.SystemParameter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * 此类扩展了数据参数解析的相关工作。除了完成对参数解析外，也解析了主从库这种形式。
 * @author albert
 */
public class MasterSlaveDataBaseParameterParser extends DataBaseParameterParser {

    public static final String MSDB_PARSER_TAG_DATABASES = "msDatabases";
    public static final String MSDB_PARSER_TAG_MASTERS = "masters";
    public static final String MSDB_PARSER_TAG_SLAVES = "slaves";
    private static final String NAME_SEPRATOR = "[,]";

    private final Logger logger = Logger.getLogger(MasterSlaveDataBaseParameterParser.class);

    public MasterSlaveDataBaseParameterParser() {
    }

    @Override
    public String getParameterName() {
        return MasterSlavesContext.MASTERS_SLAVES_PARAM_NAME;
    }



    @Override
    public Object parse(SystemParameter systemParameter) {
        // 首先解析数据库相关参数
        super.parse(systemParameter);

        DatabaseContextManager manager = DatabaseContextManager.getInstance();

        // 如果为定义主库，则使用默认数据库作为主库
        // 考虑多主多备的情况（Cluster 的时候有多主的情形）
        String masters = systemParameter.getAttributeValue(MSDB_PARSER_TAG_MASTERS);
        if (StringUtils.isEmpty(masters)) {
            masters = manager.getDefaultId();
        } 
        if (StringUtils.isEmpty(masters)) {
            throw new BadParameterException("未定义主备数据库的主库(或者默认的确实库)。");
        }
        MasterSlavesContext msContext = new MasterSlavesContext();
        String[] masterIds = masters.split(NAME_SEPRATOR);
        for(String masterId : masterIds) {
            DatabaseContext dc = manager.getDatabaseContext(masterId);
            msContext.addMaster(dc);
        }
        if (msContext.getMasters().isEmpty()) {
            throw new SystemException(String.format("定义的主数据库的参数[%s]错误。", masters));
        }

        // 从库运行为空
        String slaveIdStr = systemParameter.getAttributeValue(MSDB_PARSER_TAG_SLAVES);
        if (StringUtils.isNotEmpty(slaveIdStr)) {
             String[] _slaveIds = slaveIdStr.split(NAME_SEPRATOR);
             for (int i = 0; i < _slaveIds.length; i++) {
                String id = _slaveIds[i];
                if (StringUtils.isNotEmpty(id)) {
                    DatabaseContext dc = manager.getDatabaseContext(id);
                    msContext.addSlave(dc);
                }
            }
        } 
        LogWriter.info2(logger, "加载主备数据库环境[%s]。", msContext);
        
        return msContext;
    }

}
