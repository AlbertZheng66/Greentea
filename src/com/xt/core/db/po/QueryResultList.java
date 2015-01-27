package com.xt.core.db.po;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询结果列表。
 * <p>Title: XT框架-事务逻辑部分</p>
 * <p>Description: 这个类封装了查询结果相关信息，如翻页时用到的起始位置，每页的查询条数
 * 、查询结果的总数。 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-10-21
 */
public class QueryResultList {

    /**
     * 命名查询时的查询名称
     */
    private String name;
    /**
     * 查询结果的开始位置
     */
    private long startIndex;
    /**
     * 总共查到项的数目
     */
    private long totalItems;
    /**
     * 每页要显示的数目
     */
    private int itemsPerPage;
    /**
     * 总共的页数
     */
    private long pageCount;
    /**
     * 当前显示的页数
     */
    private int pageNo;
    /**
     * 用来缓存查询结果（对象）的列表。
     */
    private List resultList;

    /**
     * 构造器
     */
    public QueryResultList() {
        totalItems = 0;
        itemsPerPage = 0;
        pageCount = 0;
        pageNo = 0;
        resultList = new ArrayList();
    }

    /**
     * @return 返回查询结果列表
     */
    public List getResultList() {
        return resultList;
    }

    /**
     * @return  得到每页显示的行数
     */
    public int getItemsPerPage() {
        return itemsPerPage;
    }

    /**
     * @return  得到pageCount属性
     */
    public long getPageCount() {
        return pageCount;
    }

    /**
     * @return  得到pageNo属性
     */
    public int getPageNo() {
        return pageNo;
    }

    /**
     * @param itemsPerPage 设置itemPerPage属性
     */
    public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    /**
     * 判断是否查询得到了结果，如果得到，则返回true，如果没有得到则返回false
     * @return 查询得到的结果数大于0返回真，否则返回假
     */
    public boolean hasResult() {
        return (resultList != null && !resultList.isEmpty());
    }

    /**
     * 向bpoObject属性中加入一条数据。
     * @param obj  加入到结果集中的对象。
     */
    public void addItem(Object obj) {
        if (obj != null) {
            resultList.add(obj);
        }
    }

    public void setStartIndex(long startIndex) {
        this.startIndex = startIndex;
    }

    public long getStartIndex() {
        return startIndex;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }

    public void setResultList(List resultList) {
        this.resultList = resultList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
