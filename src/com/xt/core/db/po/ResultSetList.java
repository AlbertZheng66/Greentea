package com.xt.core.db.po;

import java.util.ArrayList;

import com.xt.core.log.LogWriter;

public class ResultSetList
{
    /**
     * 总共查到项的数目
     */
    protected long totalCount;

    /**
     * 每页要显示的数目
     */
    protected int itemsPerPage;

    /**
     * 总共的页数
     */
    protected long pageCount;

    /**
     * 当前显示的页数
     */
    protected int pageNo;

    /**
     * 用来缓存BPO类型的容器。
     */
    protected ArrayList bpoObject; //

    /**
     * 构造器
     */
    public ResultSetList()
    {
        totalCount = 0;
        itemsPerPage = 0;
        pageCount = 10;
        pageNo = 0;
        bpoObject = new ArrayList();
    }

    /**
     *
     * @return ArrayList 得到bpoObject属性
     */
    public ArrayList getBpoList()
    {
        return bpoObject;
    }

    /**
     *
     * @return long 得到itemPerPage属性
     */
    public int getItemsPerPage()
    {
        return itemsPerPage;
    }

    /**
     *
     * @return long 得到pageCount属性
     */
    public long getPageCount()
    {
        return pageCount;
    }

    /**
     *
     * @return long 得到pageNo属性
     */
    public int getPageNo()
    {
        return pageNo;
    }

    /**
     *
     * @return long 得到totalCount属性
     */
    public long getTotalCount()
    {
        return totalCount;
    }

    /**
     *
     * @param totalCount:long 设置totalCount属性
     */
    public void setTotalCount(long totalCount)
    {
        this.totalCount = totalCount;
    }

    /**
     *
     * @param pageNo:long 设置pageNo属性
     */
    public void setPageNo(int pageNo)
    {
        this.pageNo = pageNo;
    }

    /**
     *
     * @param itemsPerItems:long 设置itemPerPage属性
     */
    public void setItemsPerPage(int itemsPerPage)
    {
        this.itemsPerPage = itemsPerPage;
    }

    /**
     *
     * @param pageCount:long 设置pageCount属性
     */
    public void setPageCount(long pageCount)
    {
        this.pageCount = pageCount;
    }

    /**
     *
     * @param bpoObject:ArrayList 设置bpoObject属性
     */
    public void setBpoObject(ArrayList bpoObject)
    {
        this.bpoObject = bpoObject;
    }

    /**
     * 判断是否查询得到了结果，如果得到，则返回true，如果没有得到则返回false
     */
    public boolean hasResult()
    {
        if (bpoObject.isEmpty())
        {
            return false;
        }
        else
        {
            return true;
        }

    }

    /**
     * 实现根据位置从 bpoObject 中取对象。
     * @param i:int 要得到的BaseBPO实例在缓存bpoObject中的位置
     * @return BaseBPO
     */
    public IBasePO getBpo(int i)
    {
        //判断输入参数是否越界
        if (bpoObject.size() > 0 && i >= 0 && i < bpoObject.size())
        { //没有越界
            LogWriter.debug("从缓存中得到第" + (i + 1) + "条记录.");
            return (IBasePO) bpoObject.get(i);
        }
        else
        { //越界
            LogWriter.debug("输入的参数超过缓存范围，无法得到记录！");
            return null;
        }

    }

    /**
     * 向bpoObject属性中加入一条数据。
     * @param bpo：BaseBPO  加入到bpoObject中的BPO。
     */
    public void addItem(IBasePO bpo)
    {
        bpoObject.add(bpo);
    }

}
