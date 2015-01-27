package com.xt.proxy.impl.rmi.server;

import com.xt.core.session.LocalSession;
import com.xt.gt.sys.SystemConfiguration;
import java.rmi.RemoteException;
import java.rmi.server.RemoteServer;

import com.xt.proxy.event.Request;
import com.xt.proxy.event.Response;
import com.xt.proxy.impl.rmi.RmiProcessor;
import com.xt.proxy.local.LocalProxy2;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RmiProcessorImpl extends RemoteServer implements RmiProcessor {

    private final int corePoolSize = SystemConfiguration.getInstance().readInt("rmi.corePoolSize", 100);
    private final int maximumPoolSize = SystemConfiguration.getInstance().readInt("rmi.maximumPoolSize", 100);
    private final long keepAliveTime = SystemConfiguration.getInstance().readInt("rmi.keepAliveTime", 30 * 60);  // 默认是30分钟

    private final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(corePoolSize), new ThreadPoolExecutor.AbortPolicy());

    public RmiProcessorImpl() throws RemoteException {
    }

    public Response invoke(Request request) throws RemoteException {
        final LocalProxy2 localProxy2 = new LocalProxy2();
        try {
            // 进行安全检查
            localProxy2.setSession(LocalSession.getInstance());
            return localProxy2.invoke(request, null);
        } catch (Throwable ex) {
            throw new RemoteException("业务处理异常", ex);
        } finally {
            localProxy2.onFinish();
        }
    }

    private class Task implements Runnable {

        private final Request request;

        public Task(Request request) {
            this.request = request;
        }

        public void run() {
//            final LocalProxy2 localProxy2 = new LocalProxy2();
//        try {
//            // 进行安全检查
//            localProxy2.setSession(LocalSession.getInstance());
//             localProxy2.invoke(request, null);
//        } catch (Throwable ex) {
//            throw new RemoteException("业务处理异常", ex);
//        } finally {
//            localProxy2.onFinish();
//        }
        }

    }
}
