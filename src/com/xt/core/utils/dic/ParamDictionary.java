package com.xt.core.utils.dic;

/**
 * 参数字典
 * <p>Title: XT框架-事务逻辑部分</p>
 * <p>Description: 根据系统传入的参数返回不同的值. </p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2007-1-21
 */
public interface ParamDictionary extends Dictionary {
    public void setParameters (Object param);
}
