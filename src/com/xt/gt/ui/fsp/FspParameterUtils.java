package com.xt.gt.ui.fsp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.xt.core.db.pm.IPOPersistenceManager;
import com.xt.core.exception.SystemException;
import com.xt.core.utils.SqlUtils;
import com.xt.gt.ui.fsp.filter.AndFilterItem;
import com.xt.gt.ui.fsp.filter.FilterGroup;

public class FspParameterUtils {

    static public List query(IPOPersistenceManager persistenceManager,
            FspParameter fspParameter, final String sql, String alias,
            List params, Class<?> clazz) {

        // 参数传入时可能是字段形式，也可能是属性形式，应提供可以简单转换的方法。
        List queryParams = new ArrayList(5);
        queryParams.addAll(params);
        String querySql = attach(sql, alias, fspParameter, queryParams);
        List list = persistenceManager.query(clazz, querySql, queryParams);

        if (fspParameter != null) {
            // 设置查询结果的总数（无条件的总数，和被过滤过的查询结果的总数, MAX COUNT）
            if (fspParameter.getPagination().isLoadMaxRowCount()) {
                int maxRowCount = persistenceManager.queryInt(SqlUtils.createCountSql(sql), params);
                fspParameter.getPagination().setMaxRowCount(maxRowCount);
            }

            // 设置查询结果的总数（无条件的总数，和被过滤过的查询结果的总数, total COUNT）
            List totalCountParams = new ArrayList(5);
            totalCountParams.addAll(params);
            StringBuilder totalSql = new StringBuilder(sql);
            totalSql.append(" and ").append(
                    FspParameterUtils.createFilter(fspParameter, alias,
                    totalCountParams));
            int totalCount = persistenceManager.queryInt(SqlUtils.createCountSql(totalSql.toString()), totalCountParams);
            fspParameter.getPagination().setTotalCount(totalCount);
        }

        return list;
    }

    /**
     * 根据随附参数给指定的 SQL 语句附加上合适条件语句。
     *
     * @param sql
     *            SQL 语句，要求其后已经有 Where 部分（如：SELECT * FROM TAB WHERE 1=1）。
     * @param fspParameter
     *            过滤排序和分页参数
     * @return 附加上合适条件的 SQL 语句。
     */
    public static String attach(String sql, FspParameter fspParameter,
            List<Object> params) {
        return attach(sql, null, fspParameter, params);
    }

    public static String attach(String sql, String alias,
            FspParameter fspParameter, List<? extends Object> params) {
        if (sql == null || fspParameter == null) {
            return sql;
        }
        /**
         * TODO: 检查是否有where关键字
         */
        StringBuffer strBuf = new StringBuffer(sql);
        String where = createFilter(fspParameter, alias, params);
        if (StringUtils.isNotEmpty(where)) {
            strBuf.append(AndFilterItem.AND_STATEMENT_PARAMETER).append(where);
        }
        strBuf.append(createSorts(fspParameter, alias));
        return strBuf.toString();
    }

    static public List execute() {
        return null;
    }

    /**
     * 根据别名组建排序语句
     *
     * @param fspParameter
     *            随附参数实例
     * @param alias
     *            别名
     * @return 返回一个以“ORDER BY” 起始的合法的排序语句。如果没有排序选项，则返回0长度的字符串。
     */
    static public String createSorts(FspParameter fspParameter, String alias) {
        StringBuilder strBld = new StringBuilder(" ORDER BY ");
        int count = 0; // 记录有效的排序的个数
        for (Iterator<SortItem> iter = fspParameter.getSorts(); iter.hasNext();) {
            SortItem si = iter.next();
            if (si.getType() == SortType.NONE) {
                continue;
            }
            if (count > 0) {
                strBld.append(", ");
            }
            if (StringUtils.isNotEmpty(alias)) {
                strBld.append(alias).append('.');
            }
            strBld.append(si.getName());
            strBld.append(" ");
            strBld.append(si.getType().name());
            count++;
        }
        if (count == 0) {
            return "";
        }
        return strBld.toString();
    }

    /**
     * 根据别名组建过滤语句
     *
     * @param fspParameter
     *            随附参数实例
     * @param alias
     *            别名
     * @return 返回一个合法的过滤语句。如果过来条件为空，则返回恒等过来条件（1=1）。
     */
    static public String createFilter(FspParameter fspParameter, String alias,
            List params) {
        if (params == null) {
            throw new SystemException("组建过滤语句[params]时参数不能为空。");
        }
        FilterGroup filterGroup = fspParameter.getFilterGroup();
        if (filterGroup.isEmpty()) {
            return " 1=1";
        }
        String qlString = filterGroup.toQlString(alias);
        for (Object param : filterGroup.getParameters()) {
            params.add(param);
        }

        return StringUtils.isEmpty(qlString) ? " 1=1" : qlString;
    }

    /**
     * 采用变量替换方式处理 SQL 语句的查询参数。
     *
     * @param sql
     *            SQL 语句原型
     * @param varName
     *            变量名称
     * @param fspParameter
     *            过滤排序和分页参数
     * @param params
     *            SQL 参数
     * @return 替换后的 SQL 语句。
     */
    static public String replace(String sql, String varName,
            FspParameter fspParameter, List<Object> params) {
        return sql;
    }

    /**
     * 采用变量（变量采用默认的名称：${filter}）替换方式处理 SQL 语句的查询参数。
     *
     * @see fill(String sql, String varName, fspParameter fspParameter,
     *      List<Object> params)
     * @param sql
     *            SQL 语句原型
     * @param fspParameter
     *            过滤排序和分页参数
     * @param params
     *            SQL 参数
     * @return 替换后的 SQL 语句。
     */
    static public String replace(String sql, FspParameter fspParameter,
            List<Object> params) {
        return sql;
    }
}
