package com.xt.core.utils;

import org.jdom.Element;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description:工具类，提供一些方便的解析XML文件或者字符串的方法。解析的方式采用JDOM解析。</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public interface Item {
    public Object createObject (Object obj, Element elem);
}
