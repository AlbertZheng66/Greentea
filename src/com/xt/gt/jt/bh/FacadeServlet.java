package com.xt.gt.jt.bh;

import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.log4j.Logger;

import com.xt.core.app.init.ServletInit;
import com.xt.core.app.init.ServletInitFactory;
import com.xt.core.cm.ClassModifierFactory;
import com.xt.core.cm.impl.IPOModifier;
import com.xt.core.db.conn.DatabaseContext;
import com.xt.core.db.meta.Database;
import com.xt.core.dd.DynamicDeploy;
import com.xt.core.dd.Loadable;
import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import com.xt.core.utils.CalendarConverter;
import com.xt.core.utils.ClassHelper;
import com.xt.core.utils.dic.DictionaryFactory;
import com.xt.core.utils.formater.CalendarFormater;
import com.xt.core.utils.formater.FormaterUtils;
import com.xt.core.val.DateValidator;
import com.xt.core.val.DigitalValidator;
import com.xt.core.val.IDValidator;
import com.xt.core.val.IntegerValidator;
import com.xt.core.val.PhoneNumberValidator;
import com.xt.core.val.RequiredValidator;
import com.xt.core.val.TextValidator;
import com.xt.core.val.ValidatorFactory;
import com.xt.gt.jt.bh.callback.DefaultExceptionCallBack;
import com.xt.gt.jt.bh.callback.ExceptionCallBack;
import com.xt.gt.jt.bh.callback.RequestProcessor;
import com.xt.gt.jt.event.HttpRequestEvent;
import com.xt.gt.jt.event.RequestEvent;
import com.xt.gt.jt.proc.ActionParameter;
import com.xt.gt.jt.proc.HttpRequestProcessor;
import com.xt.gt.jt.proc.result.ReservePrev;
import com.xt.gt.jt.screen.IScreenFlow;
import com.xt.gt.jt.screen.ScreenFlowManager;
import com.xt.gt.jt.screen.XmlScreenFlow;
import com.xt.gt.sys.SystemConfiguration;
import com.xt.gt.sys.SystemConstants;
import com.xt.gt.sys.loader.ServerSystemLoader;
import com.xt.gt.sys.loader.SystemLoaderManager;

/**
 * 前端控制类。 <p> Title: XT框架-事务逻辑部分 </p> <p> Description:
 * 所有的HTTP请求（不太严紧）都会通过此类进行处理。init方法只在服务器启动时运行一次。
 * 无论是post方法还是get方法，都通过doPost方法进行处理。处理的过程如下：
 * <li>解析当前请求的处理参数,将其转换为ProcessParams对象</li> <li>调用业务处理方法处理业务逻辑 </li>
 * <li>调用屏幕流处理逻辑得到处理页面（JSP） </li> <li>调用屏幕流处理逻辑得到处理页面（JSP） </li> </p> <p>
 * Copyright: Copyright (c) 2006 </p> <p> Company: </p>
 *
 * @author 郑伟
 * @version 1.0 @date 2006-9-10
 */
public class FacadeServlet extends HttpServlet {

    private static final long serialVersionUID = -6959977468825364742L;
    private final Logger logger = Logger.getLogger(FacadeServlet.class);
    private static Class EXCEPITON_CALL_BACK;

    static {
        SystemLoaderManager.getInstance().init(ServerSystemLoader.getInstance());

        /**
         * ****** 注册类型转换信息 **************
         */
        ConvertUtils.register(new CalendarConverter(), Calendar.class);
        /**
         * ****** 注册类型转换信息 **************
         */
        /**
         * ****** 注册格式化信息 **************
         */
        FormaterUtils.getInstance().register(Calendar.class,
                new CalendarFormater());

        /**
         * ****** 注册校验器信息 **************
         */
        ValidatorFactory.register("ID", IDValidator.class);
        ValidatorFactory.register("TEXT", TextValidator.class);
        ValidatorFactory.register("DATE", DateValidator.class);
        ValidatorFactory.register("DIGITAL", DigitalValidator.class);
        ValidatorFactory.register("INTEGER", IntegerValidator.class);
        ValidatorFactory.register("PHONE", PhoneNumberValidator.class);
        ValidatorFactory.register("required", RequiredValidator.class);

    }
    /**
     * 缺省的配置文件名称
     */
    private static final String DEFAULT_CONFIG_FILE_NAME = "gt-config.xml";

    @Override
    public void init() throws ServletException {
        LogWriter.debug(logger, "FacadeServlet:init  initing.............");

        try {
            // 从Web.xml中读取配置参数,装载系统配置文件
            String confFileName = getInitParameter(SystemConfiguration.SYSTEM_CONFIGERATURE);

            // 如果未自定义文件，则采用默认的配置文件名称
            if (confFileName == null) {
                confFileName = DEFAULT_CONFIG_FILE_NAME;
                LogWriter.warn(logger, "系统未配置SYSTEM_CONFIGERATURE"
                        + "参数，因此未能加载系统配置参数。" + "系统将采用默认的配置文件"
                        + DEFAULT_CONFIG_FILE_NAME);
            }

            /**
             * ***** 装载系统配置文件信息 ***************
             */
            final SystemConfiguration systemConf = SystemConfiguration.getInstance();
            String configFullName = getFullFileName(confFileName);
            systemConf.load(configFullName);

            // 初始化自定义处理方法(需要重新加载输入流)
            final BizHandlerInfoFactory factory = BizHandlerInfoFactory.getInstance();
            factory.load(configFullName);
            
            DynamicDeploy.getInstance().register(configFullName, new Loadable() {

                public void load(String fileName) {
                    systemConf.load(fileName);
                    factory.load(fileName);
                }
            });

            /**
             * ****** 初始化自定义处理方法 **************
             */
            /**
             * ***** 装载系统配置文件信息 ***************
             */
            /**
             * ***** 装载数据库元信息 ***************
             */
            DatabaseContext dc = (DatabaseContext) systemConf.readObject(
                    SystemConstants.DATABASE_CONTEXT, null);
            Database.getInstance().load(dc);
            /**
             * ***** 装载数据库元信息 ***************
             */
            /**
             * ***** 装载注册字典表信息 ***************
             */
            String dicsFileName = getInitParameter(systemConf.readString("dicConfigFile"));
            if (dicsFileName == null) {
                dicsFileName = "/WEB-INF/gt-dics-config.xml";
            }
            String dicsFullFileName = getFullFileName(dicsFileName);
            DynamicDeploy.getInstance().register(dicsFullFileName, new Loadable() {

                public void load(String fileName) {
                    DictionaryFactory.getInstance().load(fileName);
                }
            });
            DictionaryFactory.getInstance().load(dicsFullFileName);
            /**
             * ***** 装载注册字典表信息 **************
             */
            /**
             * ****** 注册类修改信息 ************** 
			注册之前数据库一定要初始化（Database.newInstance().load(dc);）！
             */
            ClassModifierFactory.getInstance().register(new IPOModifier());
            /**
             * ****** 注册类修改信息 **************
             */
            EXCEPITON_CALL_BACK = DefaultExceptionCallBack.class;

            // 装载初始化类
            String[] classes = systemConf.readStrings("servletInits");
            if (classes != null && classes.length > 0) {
                ServletInitFactory siFactory = new ServletInitFactory();
                for (int i = 0; i < classes.length; i++) {
                    ServletInit si = siFactory.newInstance(classes[i]);
                    si.init(getServletContext(), null, null);
                }

            }

        } catch (Throwable e) {
            LogWriter.error(logger, "FacadeServlet初始化错误", e);
        }

        LogWriter.info(logger, "FacadeServlet: Initialization complete.....");
    }

    private String getFullFileName(String fileName) {
        String fullName = getServletContext().getRealPath("/") + fileName;
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
        LogWriter.debug(logger, "FacadeServlet: doPost................");

        // 接收请求时间（开始时间）
        Calendar startTime = Calendar.getInstance();

        LogWriter.debug(logger, String.format("FacadeServlet:[sessionId=%s] startTime=" + startTime, req.getSession(true).getId()));

        RequestEvent requestEvent = HttpRequestEvent.getInstance(req);

        // 读取请求处理器
        RequestProcessor requestProcessor = getRequestProcessor(requestEvent);

        LogWriter.debug(logger, "requestProcessor", requestProcessor);

        try {
            /**
             * *********** 解析当前请求的处理参数 ****************
             */
            ActionParameter actionParameter = ActionParameter.getInstance(requestEvent);
            /**
             * *********** 解析当前请求的处理参数 ****************
             */
            /**
             * *********** 开始进行业务处理 ****************
             */
            Object ret = requestProcessor.process(requestEvent, res,
                    actionParameter);
            /**
             * *********** 开始进行业务处理 ****************
             */
            /**
             * *********** 开始进行屏幕流处理 ****************
             */
            if (ScreenFlowManager.isDisabled(req)) {
                return;
            }

            ScreenFlowManager sfm = getScreenFlowManager();
            String screen = sfm.forword(req, actionParameter, ret);
            if (null == screen) {
                throw new SystemException("屏幕流处理异常");
            }
            /**
             * *********** 开始进行屏幕流处理 ****************
             */
            // 保存当前屏幕流为上一个屏幕流
            if (req.getParameter(ReservePrev.NOT_RESERVE) == null) {
                req.getSession(true).setAttribute(IScreenFlow.SYS_PREV_SCREEN,
                        screen);
            }

            LogWriter.debug("screen=", screen);
            if (!screen.startsWith("/")) {
                screen = "/" + screen;
            }

            // 转向页面
            req.getRequestDispatcher(screen).forward(req, res);
        } catch (Throwable t) {
            synchronized (this) {
                ExceptionCallBack ecb = (ExceptionCallBack) ClassHelper.newInstance(EXCEPITON_CALL_BACK);
                ecb.dealWith(req, res, t);
            }
        }

        // 接收请求后处理结束的时间
        Calendar endTime = Calendar.getInstance();

        /**
         * *********** 开始记录处理时间 ****************
         */
        long processTime = endTime.getTimeInMillis()
                - startTime.getTimeInMillis();
        LogWriter.debug(logger, "FacadeServlet: processTime(ms)=", processTime);
        /**
         * *********** 记录处理时间结束 ****************
         */
        LogWriter.debug(logger, "FacadeServlet: doPost. end...............");
    }

    /**
     * 返回请求处理器。目前只有HttpRequestProcessor一种请求处理器，并且为每个请求创建一个实例。
     *
     * @return 请求处理器的实例
     */
    private RequestProcessor getRequestProcessor(RequestEvent requestEvent) {
        RequestProcessor rp = new HttpRequestProcessor();
        return rp;
    }

    /**
     * 返回请求处理器。第一次调用请求处理器时，创建唯一的请求处理器，并存放在ServletContext
     * 中，以后的调用都从ServletContext中获取请求处理器的实例。
     *
     * @return 请求处理器的实例
     */
    private ScreenFlowManager getScreenFlowManager() {
        ScreenFlowManager sfm = (ScreenFlowManager) getServletContext().getAttribute("WebKeys.ScreenManagerKey");
        if (sfm == null) {
            // 初始化请求处理器
            sfm = new ScreenFlowManager();
            getServletContext().setAttribute("WebKeys.ScreenManagerKey", sfm);
        }
        return sfm;
    }
}
