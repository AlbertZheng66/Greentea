package com.xt.proxy.impl.rmi.server;

import com.xt.core.app.init.SystemLifecycle;
import com.xt.core.log.LogWriter;
import com.xt.proxy.impl.rmi.RmiProcessor;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import org.apache.log4j.Logger;

/**
 *
 * @author Albert
 */
public class RmiStarter implements SystemLifecycle {

    private static final Logger logger = Logger.getLogger(RmiStarter.class);

    public RmiStarter() {
    }

    public void onInit() {
        try {
            RmiProcessorImpl server = new RmiProcessorImpl();
            Remote stub = UnicastRemoteObject.exportObject(server, RmiProcessor.PORT);
            Registry registry = LocateRegistry.createRegistry(RmiProcessor.PORT);
            registry.bind(RmiProcessorImpl.RMI_NAME, stub);
        } catch (RemoteException e) {
            logger.warn("启动RMI服务失败。", e);
        } catch (AlreadyBoundException e) {
            logger.warn("启动RMI服务失败。", e);
        }
    }

    public void onDestroy() {
        try {
            Naming.unbind(RmiProcessorImpl.RMI_NAME);
        } catch (NotBoundException e) {
            LogWriter.warn2(logger, e, "解绑定失败。");
        } catch (RemoteException e) {
            LogWriter.warn2(logger, e, "解绑定失败。");
        } catch (MalformedURLException e) {
            LogWriter.warn2(logger, e, "解绑定失败。");
        }
    }

}
