
package com.xt.gt.sys.loader;

import java.io.InputStream;
import java.net.URL;

import com.xt.gt.sys.RunMode;

/**
 * 系统装载器提供了系统在运行初期所需要加载的参数。有些参数（如服务器地址）需要
 * 在系统加载之前获得，因此
 * 这些参数不能通过系统进行配置，可以通过此接口的实现类进行装载。
 * @author albert
 */
public interface SystemLoader {

    /**
     * 系统装载器的参数名称(使用方法 System.getProperty() 取得系统加载器)
     */
    public static final String SYSTEM_LOADER_NAME = "SystemLoader";
    
    /**
     * 设置系统的命令行参数，此参数应该在系统启动时进行设置。
     */
    public void setArguments(String[] args);
    
    /**
     * 返回当前应用（可能是一个单独的客户端，也可能使用 JNLP 协议启动的Web Start的客户端）的运行模式。
     * @return
     */
    public RunMode getRunMode();
    
    /**
     * 返回当前服务器的访问地址
     * @return
     */
    public URL getServerURL();
    
    // 网络代理信息暂时不处理
    
    /**
     * 配置代理的类型
     * @return
     */
    public String getProxyType();
    
    /**
     * 返回配置文件的输入流。
     * @return 配置文件的输入流。
     */
    public InputStream getConfigFile();

    /**
     * 返回当前上下文路径
     * @return
     */
    //public String getContextPath();
}
