package com.xt.core.proc.impl.cache;

import com.xt.core.service.LocalMethod;

/**
 * 需要使用缓存的服务所需要实现的接口。
 * @author albert
 */
public interface CacheAware {

    @LocalMethod
    public void setCacheManager(CacheManager cacheManager);

    @LocalMethod
    public CacheManager getCacheManager();
}
