package com.xt.gt.jt.screen.template;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.xt.core.app.init.InitializeException;
import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import com.xt.gt.jt.screen.FlowHandler;

/**
 * <p>Title: XT框架-显示逻辑部分</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author zhengwei
 * @version 1.0
 */

public class ScreenFlowManager
    implements Serializable
{

    /**
     * 工厂模式的实例
     */
    private static ScreenFlowManager manager = null;

    /**
     * 请求映射文件
     */
    public static final String REQUEST_MAPPING = "/WEB-INF/requestmappings.xml";

    private HashMap screens;

    private HashMap urlMappings;

    private HashMap screenDefinitionMappings;

    public ScreenFlowManager ()
    {
        screens = new HashMap();
    }

    /**
     * Servlet初始化时调用此方法
     * @param servletContext
     */
    public void init (ServletContext servletContext)
        throws InitializeException
    {
        LogWriter.debug("ScreenFlowManager init..................................");
        InputStream screenDefinitionsURL = null;

        //读取请求映射文件
        InputStream requestMappingsURL = servletContext.getResourceAsStream(REQUEST_MAPPING);

        //得到屏幕流数据
        ScreenFlowData screenFlowData = ScreenFlowXmlDAO.loadScreenFlowData(requestMappingsURL);
        LogWriter.debug("ScreenFlowManager: " + screenFlowData);
        screenDefinitionMappings = screenFlowData.getScreenDefinitionMappings();

        //将屏幕定义的映射进行多语言处理
        for (Iterator iter = screenDefinitionMappings.keySet().iterator(); iter.hasNext(); )
        {
            String language = (String)iter.next();
            LogWriter.debug("ScreenFlowManager loading screen definitions for language " + language);
            String mappings = (String)screenDefinitionMappings.get(language);
            LogWriter.debug("ScreenFlowManager: mappings are: " + mappings);
            screenDefinitionsURL = servletContext.getResourceAsStream(mappings);
            HashMap screenDefinitions = ScreenFlowXmlDAO.loadScreenDefinitions(screenDefinitionsURL);
            screens.put(language, screenDefinitions);
        }
        //初始化屏幕映射（链接与屏幕的映射）
        LogWriter.info("ScreenFlowManager:  initialized Screens and mappings");
        urlMappings = ScreenFlowXmlDAO.loadRequestMappings(
            servletContext.getResourceAsStream(REQUEST_MAPPING));
        LogWriter.info("ScreenFlowManager:urlMappings=" + urlMappings);
        //将请求映射保持在上下文中
        servletContext.setAttribute(WebKeys.URLMappingsKey, urlMappings);
//        //将屏幕流管理器保存在上下文中
//        servletContext.setAttribute(WebKeys.ScreenManagerKey, this);
    }

    /**
     * The UrlMapping object contains information that will match
     * a url to a mapping object that contains information about
     * the current screen, the RequestHandler that is needed to
     * process a request, and the RequestHandler that is needed
     * to insure that the propper screen is displayed.
     */
    private URLMapping getURLMapping (String urlPattern)
    {
        LogWriter.debug("urlMappings=" + urlMappings);
        LogWriter.debug("urlPattern=" + urlPattern);
        if ((urlMappings != null) && urlMappings.containsKey(urlPattern))
        {
            return (URLMapping)urlMappings.get(urlPattern);
        }
        else
        {
            return null;
        }
    }

    /**
     * Get the screens for the specified language.
     */
    public HashMap getScreens (Locale locale)
    {
        String languageKey = locale.getLanguage() + "_" + locale.getCountry();
        LogWriter.debug("ScreenFlowManager: getScreens for language:" + languageKey);
        if (screens.containsKey(languageKey))
        {
            return (HashMap)screens.get(languageKey);
        }
        return null;
    }

    /**
     * Get the template for the specified language.
     */
    public String getTemplate (Locale locale)
    {
        LogWriter.debug("ScreenFlowManager: getTemplate........................");
        HashMap localeScreens = getScreens(locale);
        if (localeScreens == null)
        {
            return null;
        }
        else
        {
            return (String)localeScreens.get(ScreenFlowXmlDAO.TEMPLATE); //"template"
        }
    }

    /**
     * Using the information we have in the request along with
     * The url map for the current url we will insure that the
     * propper current screen is selected based on the settings
     * in both the screendefinitions.xml file and requestmappings.xml
     * files.
     */
    public void getNextScreen (HttpServletRequest request)
        throws SystemException
    {
        String previousScreen = (String)request.getSession().getAttribute(WebKeys.CurrentScreen);
        LogWriter.debug("ScreenFlowManager: previousScreen=" + previousScreen);
        if (previousScreen != null)
        {
            // set the presious screen(设置前一屏幕)
            request.getSession().setAttribute(WebKeys.PreviousScreen, previousScreen);
        }
        //去掉请求连接的前面的斜杆“/”和后面的“.do”
        String selectedURL = processPath(request);
        String currentScreen = "";
        LogWriter.debug("ScreenFlowManager: selectedURL=" + selectedURL);
        //得到与url对应的屏幕定义
        URLMapping urlMapping = getURLMapping(selectedURL);
        LogWriter.debug("ScreenFlowManager: urlMapping=" + urlMapping);
        if (urlMapping != null)
        {
            //不使用屏幕流处理器
            if (!urlMapping.useFlowHandler())
            {
                //没有子标签的情况
                if (urlMapping.getParamMappings() == null)
                {
                    currentScreen = urlMapping.getScreen();
                }
                else
                {
                    //有子标签的情况需要特殊处理
                    currentScreen = getScreen(urlMapping.getParamMappings(), request);
                }
                LogWriter.debug("ScreenFlowManager: urlMapping=" + urlMapping);
            }
            else
            {
                //使用屏幕流处理器
                LogWriter.debug("ScreenFlowManager: using flow handler for:" + selectedURL);
                // load the flow handler
                FlowHandler handler = null;
                String flowHandlerString = urlMapping.getFlowHandler();

                try
                {
                    handler = (FlowHandler)getClass().getClassLoader().loadClass(flowHandlerString)
                              .newInstance();
                    // invoke the processFlow(HttpServletRequest)
                    LogWriter.debug("ScreenFlowManager: flow handler=" + handler);
                    //handler.doStart(request);
                    String flowResult = handler.processFlow(request, null);
                    LogWriter.debug("ScreenFlowManager: flow handler processing result="
                                    + flowResult);
                    //handler.doEnd(request);
                    // get the matching screen from the URLMapping object
                    if (flowResult.equals("TARGET_URL"))
                    {
                        String urlPattern = (String)request.getSession().getAttribute(WebKeys.
                            SigninTargetURL);
                        currentScreen = getURLMapping(urlPattern).getScreen();
                        LogWriter.debug("ScreenFlowManager: using SigninTargetURL=" + currentScreen);
                    }
                    else
                    {
                        currentScreen = urlMapping.getResultScreen(flowResult);
                    }
                }
                catch (ClassNotFoundException ex)
                {
                    throw new SystemException("00060", ex);
                }
                catch (IllegalAccessException ex)
                {
                    throw new SystemException("00065", ex);
                }
                catch (InstantiationException ex)
                {
                    throw new SystemException("00070", ex);
                }
            }
        }
        LogWriter.debug("ScreenFlowManager: currentScreen=" + currentScreen);
        if (currentScreen != null)
        {
            request.getSession().setAttribute(WebKeys.CurrentScreen, currentScreen);
        }
        else
        {
            LogWriter.error("ScreenFlowManager: Screen not found for " + selectedURL);
            throw new SystemException("00075");
        }
    }

    private String getScreen (HashMap paramMapping, HttpServletRequest request)
    {
        String screen = null;
        //请求连接的参数
        for (Iterator iter = paramMapping.values().iterator(); iter.hasNext(); )
        {
            RequestParamMapping requestMapping = (RequestParamMapping)iter.next();
            //请求连接的参数的值，根据RequestParamMapping中的name属性得到
            String value = request.getParameter(requestMapping.getName());
            if (paramMapping.containsKey(value))
            {
                RequestParamMapping temp = (RequestParamMapping)paramMapping.get(value);
                if (temp.getSubParams() == null)
                {
                    //如果没有子标签，直接取得屏幕的值
                    screen = temp.getScreen();
                }
                else
                {
                    //如果有子标签，通过子标签取得屏幕的值
                    screen = getScreen(temp.getSubParams(), request);
                }
                //跳出循环
                break;
            }
        }
        return screen;
    }

    public String getPreviousScreen (HttpServletRequest request)
    {
        Object previousScreen = request.getSession().getAttribute(WebKeys.PreviousScreen);
        if (previousScreen != null)
        {
            return (String)previousScreen;
        }
        return null;
    }

    /**
     * Gets the required parameter for the current screen
     *
     * This method is used by the insert tag to get the parameters
     * needed to build a page.
     *
     * If a language is not set then the default properties will be loaded.
     */
    public Parameter getParameter (String key, HttpSession session)
    {
        String currentScreen = (String)session.getAttribute(WebKeys.CurrentScreen);
        Locale locale = getLocale(session);
        if (screens == null || currentScreen == null)
        {
            return null;
        }
        Screen screen = (Screen)getScreens(locale).get(currentScreen);
        if (screen == null)
        {
            return null;
        }
        return screen.getParameter(key);
    }

    /**
     * Identify and return the path component (from the request URI) that
     * we will use to select an ActionMapping to dispatch with.  If no such
     * path can be identified, create an error response and return
     * <code>null</code>.
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     *
     * @throws IOException if an input/output error occurs
     * @return 返回请求路径
     */
    protected String processPath (HttpServletRequest request)
    {
        String path = request.getServletPath();
        //去掉连接后面的“.do”
        int dot = path.indexOf(".");
        if (dot > 0)
        {
            path = path.substring(0, dot);
        }
        //去掉连接后面的“.do”
        int slash = path.indexOf("/");
        if (slash > -1)
        {
            path = path.substring(slash + 1);
        }
        LogWriter.debug("RequestProcessor: processPath..path=" + path);
        return path;
    }


    /**
     * Returs the current screen
     */
    public String getCurrentScreen (HttpSession session)
    {
        return (String)session.getAttribute(WebKeys.CurrentScreen);
    }

    /**
     * 设置当前屏幕
     */
    public void setCurrentScreen (HttpSession session, String screen)
    {
        session.setAttribute(WebKeys.CurrentScreen, screen);
    }

    /**
     * Get the Locale specified in the session or return a default locale.
     */
    private Locale getLocale (HttpSession session)
    {
        Locale locale = (Locale)session.getAttribute(WebKeys.LanguageKey);
        if (locale == null)
        {
            locale = Locale.CHINA;
        }
        return locale;
    }
}
