package com.xt.core.utils.dic;

import java.io.Serializable;

/**
 * <p>Title: 字典接口.</p>
 * <p>Description: 字典接口。</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public interface Dictionary extends Serializable
{
    /**
     * 返回字典的注册名称，不能为空。
     * @return
     */
    public String getName();

    /**
     * 返回字典的所有显示标题的集合
     * @return List
     */
    public Item[] getItems ();

    /**
     * 根据名称返回相应的主键
     * @param seq int 名称在名称表中的顺序
     * @return Object
     */
    public Item getValue (int seq);

    /**
     * 根据值返回其对应的显示标题
     * @param value Object
     * @return Object
     */
    public Item getOption (Object value);
    
    /**
     * 增加一个字典项
     * @param item
     * @return 增加是否成功
     */
    public boolean add (Item item);
    
    /**
     * 移除一个字典项
     * @param item
     * @return
     */
    public boolean remove (Item item);

}
