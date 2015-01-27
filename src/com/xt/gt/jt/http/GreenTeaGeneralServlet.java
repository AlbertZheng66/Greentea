package com.xt.gt.jt.http;

import com.xt.core.app.init.SystemLifecycle;
import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.xt.core.cm.ClassModifierFactory;
import com.xt.core.cm.impl.IPOModifier;
import com.xt.core.db.conn.DatabaseContext;
import com.xt.core.db.meta.Database;
import com.xt.core.dd.DynamicDeploy;
import com.xt.core.dd.Loadable;
import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import com.xt.core.session.HttpSessionWrapper;
import com.xt.core.session.Session;
import com.xt.core.utils.ClassHelper;
import com.xt.core.utils.DateUtils;
import com.xt.core.utils.dic.DictionaryFactory;
import com.xt.proxy.Context;
import com.xt.proxy.event.Request;
import com.xt.proxy.event.Response;
import com.xt.proxy.local.LocalProxy2;
import com.xt.gt.sys.SystemConfiguration;
import com.xt.gt.sys.SystemConstants;
import com.xt.gt.sys.loader.ServerSystemLoader;
import com.xt.gt.sys.loader.SystemLoaderManager;
import java.util.Calendar;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang.StringUtils;

/**
 * 客户端流 Servlet。此类用于处理富客户端（Swing/SWT）发出的流请求
 * （即输入流的形式是一个请求对象）；服务器接收到此请求之后，进行一定的业务处理 （使用本地代理代为完成），然后将响应对象（处理结果）以输出流的方式进行返回。
 * 如果相应对象为空，则返回空指针异常。如果其他步骤产生异常，将此异常写入输出流。
 *
 * @author albert
 *
 */
abstract public class GreenTeaGeneralServlet extends HttpServlet {
    /**
     * 定义系统是否采用分布式的标记
     */
    public static final String SYSTEM_CLUSTERED_FLAG = "system.session.clustered";

    /**
     * 分布式的 Session 的实现类。必须实现“Session”接口，并有一个“Session”类型的构造函数。
     */
    public static final String SYSTEM_CLUSTER_SESSION_CLASS = "system.session.cluster.class";

    /**
     *
     */
    private static final long serialVersionUID = 3293280156704179221L;
    
    /**
     * 缺省的配置文件名称
     */
    protected static final String DEFAULT_CONFIG_FILE_NAME = "/WEB-INF/gt-config.xml";

    private final static Logger logger = Logger.getLogger(GreenTeaGeneralServlet.class);
    
//    /**
//     * 将是否是“Cluster”的标记存放在Session中。
//     */
//    private final static String IS_CLUSTER_IN_SESSION = "IS_CLUSTER_IN_SESSION";
    /**
     * 将是否是“ClusterSession”的实例存放在HTTP Session中。
     */
    private final static String SESSION_WRAPPER_IN_LOCAL_SESSION = "CLUSTER_SESSION_IN_HTTP_SESSION";

    // 在系统配置文件中定义使用的PersistenceManager
    protected static SystemConfiguration config;

    /**
     * 是否是分布式配置。
     */
    private boolean isCluster;

    /**
     * 生命周期相关的回调接口
     */
    private SystemLifecycle[] lifeCycles = null;

    static {
        SystemLoaderManager.getInstance().init(ServerSystemLoader.getInstance());
        config = SystemConfiguration.getInstance();
    }

    @Override
    public void init() throws ServletException {
        LogWriter.info2(logger, "GreenTeaGeneralServlet: initing.............");

        super.init();
        try {
            // 从Web.xml中读取配置参数,装载系统配置文件
            String confFileName = getInitParameter(SystemConfiguration.SYSTEM_CONFIGERATURE);


            // 如果未自定义文件，则采用默认的配置文件名称
            if (confFileName == null) {
                confFileName = DEFAULT_CONFIG_FILE_NAME;
                LogWriter.warn(logger, "系统未配置SYSTEM_CONFIGERATURE" + "参数，因此未能加载系统配置参数。" + "系统将采用默认的配置文件" + DEFAULT_CONFIG_FILE_NAME);
            }

            // 注册应用的路径
            String contextPath = getServletContext().getRealPath("/");
            contextPath = contextPath.replaceAll("\\\\", "/");
            // 在各种服务器下，有的（Jetty）地址最后不带有“/”，此处理保证其统一,
            if (!contextPath.endsWith("/")) {
                contextPath += "/";
            }
            LogWriter.debug(logger, "contextPath", contextPath);
            config.set(SystemConstants.APP_CONTEXT, contextPath);

            /** ***** 装载系统配置文件信息 *************** */
            String configFullName = getFullFileName(confFileName);

            if (new File(configFullName).exists()) {
                DynamicDeploy.getInstance().register(configFullName, new Loadable() {

                    public void load(String fileName) {
                        config.load(fileName, false);
                    }
                });
                config.load(configFullName, false);
            } else {
                LogWriter.info(logger, String.format("配置文件[%s]不存在。", configFullName));
            }

            /** ***** 装载数据库元信息 *************** */
            DatabaseContext dc = (DatabaseContext) SystemConfiguration.getInstance().readObject(
                    SystemConstants.DATABASE_CONTEXT, null);
            // 某些情况下，服务器不需要数据库。
            if (dc != null) {
                Database.getInstance().load(dc);
            } else {
                LogWriter.info(logger, String.format("未读取到数据库相关信息。"));
            }

            /** ***** 装载数据库元信息 *************** */
            /** ***** 装载注册字典表信息 *************** */
            String dicsFileName = config.readString("dicConfigFile", "/WEB-INF/gt-dics-config.xml");
            String dicsFullFileName = getFullFileName(dicsFileName);
            File dicConfFile = new File(dicsFullFileName);
            if (dicConfFile.exists()) {
                LogWriter.info(logger, "装载字典文件:", dicsFullFileName);
                DynamicDeploy.getInstance().register(dicsFullFileName,
                        new Loadable() {

                    public void load(String fileName) {
                        DictionaryFactory.getInstance().load(fileName);
                    }
                });
                DictionaryFactory.getInstance().load(dicsFullFileName);
            } else {
                LogWriter.info(logger, String.format("字典文件[%s]不存在。", dicsFullFileName));
            }
            /** ***** 装载注册字典表信息 ************** */
            /*******************************************************************
             * ****** 注册类修改信息 注册之前数据库一定要初始化（Database.newInstance().load(dc);）！
             */
            if (dc != null) {
                ClassModifierFactory.getInstance().register(new IPOModifier());
            }
            /** ****** 注册类修改信息 ************** */
            // 启动初始化信息
            lifeCycles = SystemConfiguration.getInstance().readObjects(SystemConstants.appLifecycles, SystemLifecycle.class);
            executeLifecycles("onInit", true);

            isCluster = SystemConfiguration.getInstance().readBoolean(SYSTEM_CLUSTERED_FLAG, false);
        } catch (Throwable e) {
            LogWriter.error(logger, "GreenTeaGeneralServlet初始化错误。", e);
            throw new ServletException("Servlet 初始化时产生错误。", e);
        }

        LogWriter.info(logger,
                "GreenTeaGeneralServlet: Initialization complete.....");
    }

    /**
     * 返回当前文件的全文件名，即在当前文件上加上上下文所在的本地路径（RealPath）。
     * @param fileName 指定文件名称
     * @return
     */
    protected String getFullFileName(String fileName) {
        if (fileName == null) {
            return null;
        }
        String contextPath = getServletContext().getRealPath("/");
        contextPath = contextPath.replaceAll("\\\\", "/");
        if (contextPath.endsWith("/") && fileName.startsWith("/")) {
            contextPath = contextPath.substring(0, contextPath.length() - 1);
        }
        String fullName = contextPath + fileName;
        return fullName;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, java.io.IOException {
        doPost(req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, java.io.IOException {
        LogWriter.debug(logger, "GreenTeaGeneralServlet: doPost................");

        // 接收请求时间（开始时间）
        long startTime = System.currentTimeMillis();

        LogWriter.debug(logger, "GreenTeaGeneralServlet:[sessionId=" + req.getSession(true).getId() + "] startTime=" + startTime);

        final LocalProxy2 localProxy2 = new LocalProxy2();
        try {
            Request request = createRequest(req);
            // 进行安全检查

            // TODO: 封装一个客户端
            Context context = new ServletContext(req, res);

            Session session = createSession(req, context);
            localProxy2.setSession(session);
            Response response = localProxy2.invoke(request, context);
            outputResponse(req, res, response);
        } catch (Throwable t) {
            LogWriter.error(logger, "业务处理出现异常。", t);
            outputThrowable(req, res, t);
        } finally {
            localProxy2.onFinish();
        }

        // 接收请求后处理结束的时间
        long endTime = System.currentTimeMillis();

        /** *********** 记录处理时间 **************** */
        long processTime = endTime - startTime;
        LogWriter.debug("GreenTeaGeneralServlet: processTime(ms)=", processTime);
    }

    /**
     * 创建一个Session。
     * @param req
     * @return
     */
    protected Session createSession(HttpServletRequest req, Context context) {
        HttpSession httpSession = req.getSession(true);

        // 使用缓存的Session
        Object _session = httpSession.getAttribute(SESSION_WRAPPER_IN_LOCAL_SESSION);
        if (_session != null && (_session instanceof Session)) {
            return (Session) _session;
        }
       
        Session session = new HttpSessionWrapper(httpSession);
        if (isCluster) {
            String className = SystemConfiguration.getInstance().readString(SYSTEM_CLUSTER_SESSION_CLASS);
            if (StringUtils.isEmpty(className)) {
                throw new SystemException(String.format("分布式 Session 的实现类未配置，请核查参数[%s]。",
                        SYSTEM_CLUSTER_SESSION_CLASS));
            }
            try {
                session = (Session)ClassHelper.newInstance(className, 
                        new Class[]{Session.class, Context.class}, new Object[]{session, context});
            } catch (Exception ex) {
                throw new SystemException(String.format("实例化类[%s]错误。", className), ex);
            }
        }
        httpSession.setAttribute(SESSION_WRAPPER_IN_LOCAL_SESSION, session);
        return session;
    }

    abstract protected Request createRequest(HttpServletRequest req) throws Exception;

    /**
     * 输出一个对象。
     *
     * @param res
     * @param obj
     * @throws IOException
     */
    abstract protected void outputResponse(HttpServletRequest req, HttpServletResponse res, Response response) throws IOException;

    /**
     * 输出一个对象。
     *
     * @param res
     * @param obj
     * @throws IOException
     */
    abstract protected void outputThrowable(HttpServletRequest req, HttpServletResponse res, Throwable throwable) throws IOException;

    @Override
    public void destroy() {
        LogWriter.info2(logger, "destroy ..........");

        executeLifecycles("onDestroy", false);
        super.destroy();

    }

    /**
     * 执行生命周期函数
     * @param action
     * @param interrupt 出现异常时是否终止执行
     */
    private void executeLifecycles(String action, boolean interrupt) {
        if (lifeCycles == null || lifeCycles.length == 0) {
            return;
        }
        if (!("onInit".equals(action) || "onDestroy".equals(action))) {
            throw new SystemException(String.format("不支持系统生命周期的动作[%s]。",
                    action));
        }
        for (int i = 0; i < lifeCycles.length; i++) {
            SystemLifecycle lifecycle = lifeCycles[i];
            LogWriter.info2(logger, "启动生命周期函数[%s] at [%s].........",
                    lifecycle, DateUtils.toDateStr(Calendar.getInstance(), "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
            runLifecycle(lifecycle, action, interrupt);
        }
    }

    protected void runLifecycle(SystemLifecycle lifecycle, String action, boolean interrupt) {
        if (lifecycle == null) {
            return;
        }
        logger.info(String.format("SystemLifecycle class[%s] started at [%s].........",
                lifecycle.getClass().getName(), DateUtils.toDateStr(Calendar.getInstance(), "yyyy-MM-dd'T'HH:mm:ss.SSSZ")));
        try {

            // 注入参数
            Request req = new Request();
            req.setService(lifecycle.getClass());
            req.setMethodName(action);
            req.setParams(new Object[]{});
            req.setParamTypes(new Class[]{});
            LocalProxy2 proxy2 = new LocalProxy2();
            Context context = new ServletContextWrapper(this.getServletContext());
            proxy2.invoke(req, context);
        } catch (Throwable t) {
            if (interrupt) {
                throw new SystemException(String.format("执行生命周期[%s]的动作[%s]是出现异常。",
                        lifecycle, action), t);
            } else {
                LogWriter.warn(logger, String.format("执行生命周期[%s]的动作[%s]是出现异常。",
                        lifecycle, action), t);
            }
        }
    }
}

class ServletContextWrapper implements Context {

    private final javax.servlet.ServletContext wrapper;

    public ServletContextWrapper(javax.servlet.ServletContext wrapper) {
        this.wrapper = wrapper;
    }

    public javax.servlet.ServletContext getWrapper() {
        return wrapper;
    }
}

