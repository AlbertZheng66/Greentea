package com.xt.proxy.impl.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.xt.proxy.event.Request;
import com.xt.proxy.event.Response;

public interface RmiProcessor extends Remote {

    /**
     * 定义RMI服务器的名称
     */
    public static final String RMI_NAME = "GREENTEA_RMI_SERVICE";
    
     /**
     * 定义RMI服务器的名称
     */
    public static final int PORT = 6666;
    

    /**
     * 调用RMI服务器的方法.
     *
     * @param request
     * @return
     * @throws RemoteException
     */
    public Response invoke(Request request) throws RemoteException;
}
