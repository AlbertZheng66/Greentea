package com.xt.gt.jt.bh;

import java.io.InputStream;
import java.util.List;

import com.xt.gt.jt.bh.mapping.BizMapping;

/**
 * 业务逻辑信息装载接口。
 * <p>Title: XT框架-事务逻辑部分</p>
 * <p>Description:  这个类用于需要自定义类装载器的情况下使用。</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-10-12
 */
public interface BizHandlerInfoLoader
{
    public List<BizHandlerInfo> load(InputStream is, BizMapping bizMapping);
}
