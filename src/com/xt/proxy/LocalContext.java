
package com.xt.proxy;

/**
 *
 * @author albert
 */
public class LocalContext implements Context {
    // 本地的IP地址
    private String ip;


    /**
     * 本地的 Session 相关信息。
     */
    // private Session session;


    public LocalContext() {
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    


}
