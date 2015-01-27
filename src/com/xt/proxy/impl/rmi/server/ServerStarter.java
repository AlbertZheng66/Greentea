package com.xt.proxy.impl.rmi.server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import com.xt.core.log.LogWriter;
import com.xt.proxy.impl.rmi.RmiProcessor;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ServerStarter {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            RmiProcessorImpl server = new RmiProcessorImpl();
            
            Remote stub = UnicastRemoteObject.exportObject(server, RmiProcessor.PORT);
            
            Registry registry = LocateRegistry.createRegistry(RmiProcessor.PORT);
            registry.bind(RmiProcessorImpl.RMI_NAME, stub); 

            // Naming.bind(RmiProcessorImpl.RMI_NAME, server);

            System.out.println("服务器启动完毕...");

//			
        } catch (Exception e) {
            clear();
            e.printStackTrace();
        }

    }

    private static void clear() {
        try {
            String[] names = Naming.list(RmiProcessorImpl.RMI_NAME);
            for (int i = 0; i < names.length; i++) {
                String name = names[i];
                LogWriter.debug("RMI name=", name);
                Naming.unbind(RmiProcessorImpl.RMI_NAME);
            }
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

}
