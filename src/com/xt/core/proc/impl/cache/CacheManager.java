

package com.xt.core.proc.impl.cache;

/**
 * 提供了缓存服务的接口。
 * @author albert
 */
public interface CacheManager {

    
    public void put(String name, Object value);

    public Object get(String name);

}
