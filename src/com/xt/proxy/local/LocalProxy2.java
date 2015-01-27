package com.xt.proxy.local;

import java.io.Serializable;
import java.lang.reflect.Method;

import com.xt.core.cm.ClassModifierFactory;
import com.xt.core.cm.impl.IPOModifier;
import com.xt.core.db.conn.DatabaseContext;
import com.xt.core.db.meta.Database;
import com.xt.core.dd.DynamicDeploy;
import com.xt.core.dd.Loadable;
import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import com.xt.core.proc.Processor;
import com.xt.core.proc.impl.GeneralProcessorFactory;
import com.xt.core.service.Asynchronized;
import com.xt.core.service.IService;
import com.xt.core.service.ServerMethod;
import com.xt.core.service.ServiceMappingFactory;
import com.xt.core.session.LocalSession;
import com.xt.core.session.Session;
import com.xt.core.utils.ClassHelper;
import com.xt.core.utils.dic.DictionaryFactory;
import com.xt.proxy.Proxy;
import com.xt.proxy.ServiceHelper;
import com.xt.proxy.Context;
import com.xt.proxy.event.Request;
import com.xt.proxy.event.Response;
import com.xt.gt.sys.RunMode;
import com.xt.gt.sys.SystemConfiguration;
import com.xt.gt.sys.SystemConstants;
import com.xt.gt.sys.loader.SystemLoader;
import com.xt.gt.sys.loader.SystemLoaderManager;
import java.io.File;
import java.io.InputStream;
import org.apache.log4j.Logger;

public class LocalProxy2 implements Proxy {

    private final static Logger logger = Logger.getLogger(LocalProxy2.class);
    private final static GeneralProcessorFactory processorFactory;
    private Session session = LocalSession.getInstance();
    /**
     * 处理器
     */
    private Processor processor;
    /**
     * 调用是否是异步的
     */
    private boolean asynFlag = false;

    static {
        // 在服务器端运行的情况下，不需要加载配置文件
        SystemLoader systemLoader = SystemLoaderManager.getInstance().getSystemLoader();
        if (systemLoader.getRunMode() != RunMode.SERVER) {
            SystemConfiguration config = SystemConfiguration.getInstance();
            InputStream is = systemLoader.getConfigFile();
            if (is != null) {
                config.load(is, false);
            }

            DatabaseContext dbContext = (DatabaseContext) config.readObject(SystemConstants.DATABASE_CONTEXT);
            if (dbContext != null) {
                Database.getInstance().load(dbContext);
            }

            /**
             * ***** 装载注册字典表信息 ***************
             */
            String dicsFileName = config.readString("dicConfigFile");
            if (dicsFileName == null) {
                dicsFileName = "conf/gt-dics-config.xml";
            }
            File file = new File(dicsFileName);
            if (file.exists()) {
                DictionaryFactory.getInstance().load(dicsFileName);
                DynamicDeploy.getInstance().register(file.getAbsolutePath(), new Loadable() {

                    public void load(String fileName) {
                        DictionaryFactory.getInstance().load(fileName);
                    }
                });
            } else {
                LogWriter.info2(logger, "字典配置文件[%s]不存在。", dicsFileName);
            }
            /**
             * ***** 装载注册字典表信息 **************
             */
            /**
             * *****************************************************************
             * ****** 注册类修改信息 注册之前数据库一定要初始化（Database.newInstance().load(dc);）！
             */
            ClassModifierFactory.getInstance().register(new IPOModifier());
            /**
             * ****** 注册类修改信息 **************
             */
        }

        processorFactory = GeneralProcessorFactory.getInstance();
        processorFactory.onInit();
    }

    public LocalProxy2() {
    }

    public Response invoke(final Request request, Context context) throws Throwable {
        if (request == null) {
            throw new SystemException("请求对象不能为空");
        }
        LogWriter.info(logger, "request", request);

        processor = processorFactory.createProcessor(request.getService());

        Class<? extends IService> serviceClass = request.getService();


        // 根据情况调用相应的实现类！，如果类相同，则使用传入的对象，并进行参数注入。
        // 如果参数不同，则使用实现类，并拷贝相应的属性值。
        Class<? extends IService> implServiceClass = ServiceMappingFactory.getInstance().getClass(serviceClass);

        // 构建注入处理器
        processor.onCreate(implServiceClass, session, context);

        final IService serviceObject = ClassHelper.newInstance(implServiceClass);
        if (request.getServiceObject() != null) {
            ServiceHelper.copyLocalProperites(serviceObject, request.getServiceObject());
        }

        final Method method = ClassHelper.getMethod(serviceObject, request.getMethodName(), request.getParamTypes());

        if (method == null) {
            throw new SystemException(String.format("类[%s]的方法[%s]不存在。",
                    implServiceClass.getName(), request.getMethodName()));
        }

        if (null != method.getAnnotation(ServerMethod.class)) {
            throw new SystemException(String.format("类[%s]的方法[%s]调用违例，"
                    + "其被定义为只允许在服务器端调用。",
                    implServiceClass.getName(), request.getMethodName()));
        }

        final Response res = new Response();
        asynFlag = isAsynchronized(implServiceClass, method);
        if (asynFlag) {
            // 启动异步线程
            Thread deamon = new Thread(new Runnable() {

                public void run() {
                    try {
                        execute(request, processor, serviceObject, method, res);
                    } catch (Throwable ex) {
                        LogWriter.warn2(logger, ex, "执行请求[%s]时出现错误。", request);
                    } finally {
                        // 异步的情况下，自行处理“finish”方法
                        onFinish();
                    }
                }
            });
            deamon.setDaemon(true);
            deamon.start();
        } else {
            // 执行同步方法
            execute(request, processor, serviceObject, method, res);
        }
        return res;
    }

    protected void execute(Request request, Processor processor, IService serviceObject,
            Method method, Response res) throws Throwable {
        try {
            // 原始参数
            Object[] originalParams = request.getParams();
            // 参数可能需要经过转换并重新赋值
            Object[] params = processor.onBefore(serviceObject, method, originalParams);
            /**
             * ************ 执行业务处理方法 ********************
             */
            Object ret = method.invoke(serviceObject, params);
            processor.onAfter(serviceObject, ret);
            // 处理返回值
            if (serviceObject instanceof Serializable) {
                res.setServiceObject(serviceObject);
            }
            res.setReturnValue(ret);
            res.setRefParams(originalParams);
        } catch (Throwable t) {
            processor.onThrowable(t);
            throw t;
        } finally {
            processor.onFinally();
        }
    }

    public void onFinish() {
        if (processor != null && !asynFlag) {
            processor.onFinish();
        }
    }

    private boolean isAsynchronized(Class<? extends IService> implServiceClass, Method method) {
        return (implServiceClass.getAnnotation(Asynchronized.class) != null
                || method.getAnnotation(Asynchronized.class) != null);
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
