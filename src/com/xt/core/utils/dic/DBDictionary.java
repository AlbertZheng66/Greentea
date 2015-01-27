package com.xt.core.utils.dic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.xt.core.exception.BadParameterException;
import com.xt.core.utils.BooleanUtils;

/**
 * 数据库字典表。
 * <p>Title: XT框架-事务逻辑部分</p>
 * <p>Description: 一个数据库字典表实例代表了一个数据库字典，与其他的字典相同，
 * 用唯一的名称标识处理。数据库字典中的数据是通过一个给定的sql语句来提供。本类提供
 * 了一个装载方法（load）来提取SQL语句的查询结果。要求要求查询语句必须提供
 * value,title,available这三个字段（如果数据库里定义名称与此不同，可以用别名
 * 将其转换）。 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-9-10
 */
public class DBDictionary implements Dictionary {

    /**
     * 查询字典的SQL语句，要求查询语句必须提供value,title,available字段，
     *  并按照需要的规则排序。字典框架将字段读取这两个值。
     * available的取值为：1：有效，其余则为无效。
     * 比如：select nation_id as value, nation_name as
     * title , 1 available from national order by nation_name
     */
    private String sql;
    /**
     * 本字典中所有的选项列表
     */
    private List<Item> items = new ArrayList<Item>();
    /**
     * 字典名称。
     */
    private final String name;

    /**
     * 默认构建函数
     */
    public DBDictionary(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void load(Connection conn) throws SQLException {
        if (conn == null) {
            throw new BadParameterException("数据库连接不能为空！");
        }

        if (StringUtils.isBlank(sql)) {
            throw new BadParameterException("查询语句不能为空！");
        }
        //从查询结果中读取相关信息
        ResultSet rs = conn.createStatement().executeQuery(sql);
        int index = 0;
        //如果字段多于三个，则认为包含“validate字段”
        boolean hasVaildate = (rs.getMetaData().getColumnCount() > 2);
        while (rs.next()) {
            Item item = new Item();
            item.setValue(rs.getString("value"));
            item.setTitle(rs.getString("title"));
            //"validate"字段为可选字段
            if (hasVaildate) {
                item.setValidate(BooleanUtils.isTrue(rs.getString("available")));
            }
            item.setIndex(index++);
            items.add(item);
        }
    }

    public Item[] getItems() {
        return (Item[]) items.toArray(new Item[items.size()]);
    }

    public Item getOption(Object value) {
        if (items == null || value == null) {
            throw new BadParameterException("请求的字典项不存在");
        }
        for (Iterator<Item> iter = items.iterator(); iter.hasNext();) {
            Item item = iter.next();
            if (value.equals(item.getValue())) {
                return item;
            }
        }

        return null;
    }

    public Item getValue(int seq) {
        if (seq < 0 || items == null || seq >= items.size()) {
            throw new BadParameterException("请求的字典项位置超出字典的长度");
        }
        return items.get(seq);
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    synchronized public boolean add(Item item) {
        if (item == null) {
            return false;
        }
        items.add(item);
        return true;
    }

    synchronized public boolean remove(Item item) {
        if (item == null || item.getValue() == null
                || getOption(item.getValue()) == null) {
            return false;
        }
        for (Iterator<Item> iter = items.iterator(); iter.hasNext();) {
            Item temp = iter.next();
            if (item.getValue().equals(temp.getValue())) {
                return items.remove(temp);
            }
        }
        return true;
    }
}
