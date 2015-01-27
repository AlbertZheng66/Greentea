package com.xt.proxy.impl.rmi.client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import com.xt.core.log.LogWriter;
import com.xt.proxy.Proxy;
import com.xt.proxy.Context;
import com.xt.proxy.event.Request;
import com.xt.proxy.event.Response;
import com.xt.proxy.impl.rmi.RmiProcessor;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RmiProxy implements Proxy {

    public RmiProxy() {
    }

    public Response invoke(Request request, Context context) {
        try {
//            Object proxy = Naming
//                    .lookup("rmi://192.168.21.71:6666/" + RmiProcessor.RMI_NAME);
            Registry registry = LocateRegistry.getRegistry("192.168.21.71", RmiProcessor.PORT);
            Object proxy = registry.lookup(RmiProcessor.RMI_NAME);
            LogWriter.debug("proxy", proxy);
            LogWriter.debug("proxy", proxy.getClass().getName());
            RmiProcessor rmiProcessor = (RmiProcessor) proxy;
            return rmiProcessor.invoke(request);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    static public void main(String[] agrv) throws Exception {
        Registry registry = LocateRegistry.getRegistry("192.168.21.71", RmiProcessor.PORT);
        String[] names = registry.list();
//        String[] names = Naming.list(RmiProcessorImpl.RMI_NAME);
//        for (int i = 0; i < names.length; i++) {
//            String name = names[i];
//            LogWriter.debug("RMI name=", name);
//        }
        RmiProxy rpc = new RmiProxy();
        Request req = new Request();
        req.setService(HelloService.class);
        req.setMethodName("sayHello");
        rpc.invoke(req, null);
    }
}
