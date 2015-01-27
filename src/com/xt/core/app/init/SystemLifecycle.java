package com.xt.core.app.init;

import com.xt.core.service.IService;

/**
 * 系统生命周期接口,在所定义的Servlet启动时进行调用。
 * @author Albert
 *
 */
public interface SystemLifecycle extends IService {

    /**
     * Servlet 初始化时调用此方法。
     */
    public void onInit();

    /**
     * Servlet 销毁时调用此方法。
     */
    public void onDestroy();
}
