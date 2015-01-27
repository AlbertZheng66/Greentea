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

public class CodeManager
{
    /**
     * 唯一的静态单件实例
     */
    private static CodeManager instance;

    private CodeManager()
    {
    }

    /**
     * 单件模式，返回唯一的实例
     * @return 唯一的CodeManager实例
     */
    public static CodeManager newInstance ()
    {
        if (instance == null)
        {
            instance = new CodeManager();
        }
        return instance;
    }

    /**
    * 返回代码的集合。计划由Code的实例组成。
    * @param codeTableName 代码表名称
    * @return 代码的集合
    */
   public Iterator getCodes (String codeTableName) {
       return null;
   }
}
