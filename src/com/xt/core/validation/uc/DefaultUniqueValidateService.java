package com.xt.core.validation.uc;

import com.xt.core.db.pm.IPOPersistenceManager;
import com.xt.core.utils.CollectionUtils;
import com.xt.core.utils.SqlUtils;
import com.xt.gt.ui.fsp.FspParameter;
import java.util.List;

/**
 * 缺省的唯一校验服务类。本类采用 IPO 方式对类进行校验，将唯一约束（一个或者多个）
 * 形成标准 SQL 语句，如果未能检索到结果，表示此值唯一。
 * @author albert
 */
public class DefaultUniqueValidateService implements UniqueValidateService {

    private IPOPersistenceManager persistenceManager;
    
    private FspParameter FspParameter;
    
    @Override
    public boolean isUnique(UniqueConstraintInfo uci) {
        Class clazz = uci.getBindingClass();
        StringBuffer sqlWhere = new StringBuffer();
        boolean appendSubfix = false;
        for(String propertyName : uci.getPropertyNames()) {
            if (appendSubfix) {
                sqlWhere.append(" AND ");
            }
            sqlWhere.append(propertyName).append(" = ? ");
            appendSubfix = true;
        }
        List list = persistenceManager.findAll(clazz, sqlWhere.toString(), SqlUtils.getParams(uci.getValues()), null);
        return CollectionUtils.isEmpty(list);
    }

    public void setPersistenceManager(IPOPersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }

    public FspParameter getFspParameter() {
        return FspParameter;
    }

    public void setFspParameter(FspParameter FspParameter) {
        this.FspParameter = FspParameter;
    }
}
