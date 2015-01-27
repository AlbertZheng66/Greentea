package com.xt.comm.privilege;

import java.beans.Beans;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jdom.Element;

import com.xt.core.app.init.ResetException;
import com.xt.core.log.LogWriter;
import com.xt.core.utils.XmlHelper;

/**
 * <p>Title: </p>
 * <p>Description:认证Servlet过滤器；用来检测用户是否有权访问此连接。此Filter初始化时，将权限<br>
 *                转换器和权限控制器进行实例化，并存放在Context中。</p>
 * <p>Copyright: 2003年5月</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class AuthenticationFilter
    implements Filter
{

    private FilterConfig config = null;

    /*
     * 用户登录时使用的URL的值，这时不需要检测这个链接。
     */
    private String signinUrlValue;

    /*
     * 用户登录时使用的URL的名称；
     */
    private static final String SIGNIN_URL_NAME = "SIGNIN_URL_NAME";

    /*
     * 不需要进行检测的URL的XML文件的名称；
     */
    private static final String PUBLIC_LIST = "public_list";

    /*
     * 动态配置的权限转换器的参数名称；
     */
    private static final String PRIVILEGE_TRANSLATOR = "privilege_translator";

    /*
     * 动态配置的权限控制器的参数名称；
     */
    private static final String PRIVILEGE_CONTROLLER = "privilege_controller";

    /*
     * 无须检测的访问链接的列表
     */
    private Collection publicList;

    /*
     * 权限转换器.
     */
    private PrivilegeTranslation privilegeTranslation;

    /**
     * 用户使用的权限转换器.
     */
    private PrivilegeController privilegeController;

    private static Map initValue;

    public void init (FilterConfig config)
        throws ServletException
    {
        this.config = config;

        //免检链接(通过XML配置文件得到)
        String fileName = config.getInitParameter(PUBLIC_LIST);
        LogWriter.debug("AuthenticationFilter init fileName=" + fileName);
        publicList = getPublicList(fileName);

        //读入登录时使用的链接
        signinUrlValue = config.getInitParameter(SIGNIN_URL_NAME);
        LogWriter.debug("AuthenticationFilter init signOn=" + signinUrlValue);
        //权限转换器的具体实现类的名称
        String privilegeName = config.getInitParameter(PRIVILEGE_TRANSLATOR);
        LogWriter.debug("AuthenticationFilter init privilegeName=" + privilegeName);
        //权限控制器的具体实现类的名称
        String controllerName = config.getInitParameter(PRIVILEGE_CONTROLLER);
        LogWriter.debug("AuthenticationFilter init controllerName=" + controllerName);

        //配置参数
        initValue = createInitValues(config);

        try
        {
            //实例化并初始化权限转换器
            privilegeTranslation =
                (PrivilegeTranslation)Beans.instantiate(getClass().getClassLoader(),
                                                        privilegeName);
            privilegeTranslation.init(initValue);
            //实例化并初始化控制转换器
            privilegeController =
                (PrivilegeController)Beans.instantiate(getClass().getClassLoader(), controllerName);

            //初始化参数
            privilegeController.init(initValue);

            //将控制转换器存放在上下文中
            config.getServletContext()
                .setAttribute(PrivilegeController.PRIVILEGE_CONTROLLER_IN_SERVLET_CONTEXT,
                              privilegeController);
        }
        catch (IOException ex)
        {
            LogWriter.error("AuthenticationFilter init IOException=" + ex.getMessage(), ex);
            throw new ServletException("AuthenticationFilter init IOException=" + ex.getMessage(),
                                       ex);
        }
        catch (ClassNotFoundException ex)
        {
            LogWriter.error("AuthenticationFilter init ClassNotFoundException=" + ex.getMessage(),
                            ex);
            throw new ServletException("AuthenticationFilter init ClassNotFoundException="
                                       + ex.getMessage(), ex);
        }
    }

    /**
     * 抽取Filter的参数，形成Map初始化数据
     * @param config FilterConfig
     * @return Map
     */
    private Map createInitValues (FilterConfig config) {
        Map initValue = new HashMap();
        for (Enumeration enumeration = config.getInitParameterNames(); enumeration.hasMoreElements(); ) {
            String name = (String)enumeration.nextElement();
            initValue.put(name, config.getInitParameter(name));
        }
        return initValue;
    }

    /**
     * 通过读取XML配置文件，返回公共权限（不需要检测）的URL集合。
     * @param fileName XML配置文件名称
     * @return 公共权限集合
     */
    private Collection getPublicList (String fileName)
    {
        InputStream is = config.getServletContext().getResourceAsStream(fileName);
        Element root = null;
        try
        {
            root = XmlHelper.getRoot(is);
        }
        catch (Exception ex)
        {
            LogWriter.debug("AuthenticationFilter getPublicList JDOMException=" + ex.getMessage());
        }
        List url = root.getChildren("url");
        ArrayList publicList = new ArrayList(url.size());
        for (Iterator it = url.iterator(); it.hasNext(); )
        {
            Element item = (Element)it.next();
            publicList.add(item.getText());
            LogWriter.debug("AuthenticationFilter getPublicList url=" + item.getText());
        }
        return publicList;
    }

    public void destroy ()
    {
        this.config = null;
        initValue = null;
    }

    /**
     * 每个登录请求都需要通过此连接进行权限校验。1.检验请求连接是否未公共连接，如果是，允许访问；
     *                                           2.如果不是公共连接，使用权限转换器对连接进行转换，
     *                                             转换的结果是连接（URL）对应的权限（如果不存在，权
     *                                             限未空），然后传入权限控制器进行校验。如果通过校
     *                                             验，允许访问，否则拒绝用户登录。
     * @param request 正在处理的Http请求
     * @param response 建立的Http响应
     * @param chain 下一个Filter
     * @throws IOException
     * @throws ServletException
     */
    public void doFilter (ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException
    {
        LogWriter.debug("AuthenticationFilter doFilter.........................");
        LogWriter.debug("AuthenticationFilter doFilter request.getServletPath()="
                        + ((HttpServletRequest)request).getServletPath());

        //如果是请求登录链接，或者是无须检测的链接，则不作权限检测
        //如果是系统复位请求链接，则进行复位操作
        if (isPublic((HttpServletRequest)request))
        {
            chain.doFilter(request, response);
            return;
        }
        //只允许HTTP请求登录。
        if (request instanceof HttpServletRequest && permit((HttpServletRequest)request))
        {
            //允许用户登录
            LogWriter.info("AuthenticationFilter doFilter 允许用户登录.....");
            chain.doFilter(request, response);
            return;
        }
        else
        {
            //拒绝用户登录，将请求转发到登录页
            LogWriter.info("AuthenticationFilter doFilter 拒绝用户登录.....");
            config.getServletContext().getRequestDispatcher(signinUrlValue).forward(request,
                response);
        }
    }

    /**
     * 判断用户是否有权访问该链接。首先将链接转换成相应的权限，然后，通过权限控制器进行校验。
     * @param request Http请求
     * @return 如果用户有权访问返回真，否则返回假（用户未登录，登录未成功，用户
     * 无权访问此链接）。
     */
    private boolean permit (HttpServletRequest request)
    {
        HttpSession session = request.getSession(true);
        //得到存储在Session中的用户（如果用户未登录或登录未成功则此值为空）。
        User user = (User)session.getAttribute(User.USER_IN_SESSION);
        LogWriter.info("AuthenticationFilter permit user=" + user);
        //将链接转换成相应的权限。
        Privilege p = privilegeTranslation.getPrivilege(request);
        LogWriter.info("AuthenticationFilter permit Privilege=" + p);
        return privilegeController.permit(user, p);
    }

    /**
     * 判断Http请求是不是公共请求（不需检测的URL）。
     * @param request Http请求。
     * @return 如果是用户登录请求，返回真，否则返回假。
     */
    private boolean isPublic (HttpServletRequest request)
    {
        LogWriter.info("AuthenticationFilter noChecked................");
        //允许所有用户访问的链接
        String servletPath = request.getServletPath();

        if (servletPath.equals(signinUrlValue))
        {
            return true;
        }

        for (Iterator i = publicList.iterator(); i.hasNext(); )
        {
            String url = (String)i.next();
            if (servletPath.startsWith(url))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 系统复位接口。
     * @throws ResetException
     */
    public synchronized void reset ()
        throws ResetException
    {
        //权限转换器和控制器重新促使化
        privilegeTranslation.init(initValue);
        privilegeController.init(initValue);
    }
}
