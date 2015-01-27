package com.xt.test.ct;

import java.util.Iterator;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public interface LevelCode
    extends Code
{
    /**
     * 下级编码，如果没有下级编码返回空。
     * @param code 编码
     * @return 所有下级编码
     */
    public Iterator next (LevelCode code);

    /**
     * 上级编码，如果没有上级编码返回空。
     * @param code 编码
     * @return 上级(父级)编码
     */
    public LevelCode prev (LevelCode code);

}
