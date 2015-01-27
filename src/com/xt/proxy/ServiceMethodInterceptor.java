package com.xt.proxy;

import com.xt.core.exception.BadParameterException;
import com.xt.core.exception.SystemException;
import java.io.Serializable;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import com.xt.core.service.IService;
import com.xt.core.service.LocalMethod;
import com.xt.core.utils.ClassHelper;
import com.xt.gt.sys.loader.SystemLoader;
import com.xt.gt.sys.loader.SystemLoaderManager;
import com.xt.proxy.event.DefaultResponseProcessor;
import com.xt.proxy.event.Request;
import com.xt.proxy.event.Response;
import com.xt.proxy.event.ResponseProcessor;
import com.xt.proxy.impl.AsynchProxy;
import com.xt.proxy.local.LocalProxy2;
import org.apache.commons.lang.StringUtils;

public class ServiceMethodInterceptor<T extends IService> implements
        MethodInterceptor {

    /**
     *
     */
    private static final long serialVersionUID = 4158215205585869074L;
    /**
     * 被代理的服务器类。
     */
    private final Class<T> serviceClass;
    /**
     * 指定的代理。
     */
    private final Proxy proxy;
    private Task task;
    private boolean asynchInvoke = false;

    public ServiceMethodInterceptor(Class<T> serviceClass, Proxy proxy) {
        super();
        this.serviceClass = serviceClass;
        this.proxy = proxy;
        if (this.serviceClass == null) {
            throw new BadParameterException("代理服务类不能为空。");
        }
    }

    public Object intercept(Object serviceObject, Method method,
            Object[] params, MethodProxy methodProxy) throws Throwable {
        // 如果方法名称为本地方法，则不使用代理方法进行处理
        LocalMethod lmAnno = method.getAnnotation(LocalMethod.class);
        if (lmAnno != null) {
            return methodProxy.invokeSuper(serviceObject, params);
        }

        // 不反射父接口的类
        if (method.getDeclaringClass() == Object.class) {
            return methodProxy.invokeSuper(serviceObject, params);
        }

        // 非本地方方法则使用远程代理
        Request request = new Request();
        request.setService(serviceClass);
        request.setMethodName(method.getName());
        request.setParamTypes(method.getParameterTypes());
        request.setParams(params);
        if (serviceObject instanceof Serializable) {
            IService mockService = ClassHelper.newInstance(serviceClass);
            ServiceHelper.copyLocalProperites(mockService, serviceObject);
            request.setServiceObject(mockService);
        }

        // 获取服务的代理类
        Proxy _proxy = getProxy(method);

        try {

            //TODO: 记录代理本地的信息
            Context context = new LocalContext();

            // 开始进行业务处理
            Response res = _proxy.invoke(request, context);
            ResponseProcessor responseProcessor = new DefaultResponseProcessor();
            Object ret = responseProcessor.process(request, res);
            if (res.getServiceObject() != null) {
                ServiceHelper.copyLocalProperites(serviceObject, res.getServiceObject());
            }
            if (method.getReturnType() != void.class) {
                return ret;
            }
        } finally {
            if (_proxy instanceof LocalProxy2) {
                ((LocalProxy2) _proxy).onFinish();
            }
        }


        return null;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public boolean isAsynchInvoke() {
        return asynchInvoke;
    }

    public void setAsynchInvoke(boolean asynchInvoke) {
        this.asynchInvoke = asynchInvoke;
    }

    public Class<T> getServiceClass() {
        return serviceClass;
    }

    /**
     * 根据情况返回指定的代理类。
     * 如果当前类指定了代理类，则使用此代理类；如果未指定，但是方法上定义了代理标注，
     * 则使用标注的代理；如果也未定义代理标注，这使用系统默认的代理。
     * @param method
     * @return
     */
    private Proxy getProxy(Method method) {

        // 如果定义了特定代理，则使用指定的代理，否则使用自动切换代理。
        Proxy _proxy = null;
        if (this.proxy != null) {
            _proxy = this.proxy;
        } else {
            ProxyDecl proxyDecl = method.getAnnotation(ProxyDecl.class);
            if (proxyDecl != null) {
                _proxy = ProxyFactory.getInstance().getProxy(proxyDecl.name());
            }
            if (_proxy == null) {
                SystemLoader loader = SystemLoaderManager.getInstance().getSystemLoader();
                if (loader != null && StringUtils.isNotEmpty(loader.getProxyType())) {
                    _proxy = ProxyFactory.getInstance().getProxy(loader.getProxyType());
                }
            }
            if (_proxy == null) {
                _proxy = ProxyFactory.getInstance().getDefaultProxy();
            }
        }
        if (_proxy == null) {
            throw new SystemException(String.format("类[%s]的方法[%s]未指定代理类。",
                    this.serviceClass.getName(), method.getName()));
        }
        if (asynchInvoke) {
            _proxy = new AsynchProxy(_proxy, task);
        }
        return _proxy;
    }
}