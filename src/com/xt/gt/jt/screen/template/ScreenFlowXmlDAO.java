package com.xt.gt.jt.screen.template;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.xt.core.log.LogWriter;
/**
 * <p>Title: XT框架-显示逻辑部分</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

/**
 * This class provides the data bindings for the screendefinitions.xml
 * and the requestmappings.xml file.
 * The data obtained is maintained by the ScreenFlowManager
 */

public class ScreenFlowXmlDAO
{

    // constants
    public static final String URL_MAPPING = "url-mapping";

    public static final String SCREEN_DEFINITION = "screen-definition";

    public static final String URL = "url";

    public static final String LANGUAGE = "language";

    public static final String TEMPLATE = "template";

    public static final String RESULT = "result";

    public static final String NEXT_SCREEN = "screen";

    public static final String USE_FLOW_HANDLER = "useFlowHandler";

    public static final String FLOW_HANDLER_CLASS = "class";

    public static final String HANDLER_RESULT = "handler-result";

    public static final String FLOW_HANDLER = "flow-handler";

    public static final String BIZ_HANDLER = "biz-handler";

    public static final String TRANSACTION_LEVEL = "transaction-level";

    /**
     * 请求连接的第一个参数
     */
    public static final String FIRST_PARAM = "handler";

    /**
     * 请求连接的第二个参数
     */
    public static final String SECOND_PARAM = "command";

    /**
     * 请求连接的参数的值
     */
    public static final String PARAM_VALUE = "value";

    public static final String KEY = "key";

    public static final String VALUE = "value";

    public static final String DIRECT = "direct";

    public static final String SCREEN = "screen";

    public static final String SCREEN_NAME = "screen-name";

    public static final String PARAMETER = "parameter";

    public static Element loadDocument (String location)
    {
        Document doc = null;
        SAXBuilder docBuilderFactory = new SAXBuilder();
        try
        {
            LogWriter.debug("ScreenFlowXmlDAO loadDocument location:" + location);
            doc = docBuilderFactory.build(location);
            return doc.getRootElement();
        }
        catch (Exception ex)
        {
            LogWriter.error("ScreenFlowXmlDAO loadDocument JDOMException:" + ex, ex);
        }
        return null;
    }

    public static Element loadDocument (InputStream location)
    {
        Document doc = null;
        SAXBuilder docBuilderFactory = new SAXBuilder();
        try
        {
            LogWriter.debug("ScreenFlowXmlDAO loadDocument location:" + location);
            doc = docBuilderFactory.build(location);
            return doc.getRootElement();
        }
        catch (Exception ex)
        {
            LogWriter.error("ScreenFlowXmlDAO loadDocument JDOMException:" + ex, ex);
        }
        return null;
    }

    /**
     * 装载屏幕流
     * @param location 屏幕流所在的位置
     * @return 屏幕流定义
     */
    public static ScreenFlowData loadScreenFlowData (String location)
    {
        Element root = loadDocument(location);
        HashMap screenDefinitionMappings = getScreenDefinitions(root);
        return new ScreenFlowData(screenDefinitionMappings);
    }

    /**
     * 装载屏幕流
     * @param location 屏幕流所在的位置
     * @return 屏幕流定义
     */
    public static ScreenFlowData loadScreenFlowData (InputStream location)
    {
        Element root = loadDocument(location);
        HashMap screenDefinitionMappings = getScreenDefinitions(root);
        return new ScreenFlowData(screenDefinitionMappings);
    }

    /**
     * 装载屏幕定义
     * @param location 屏幕定义所在的位置
     * @return 所有屏幕定义的HashMap
     */
    public static HashMap loadScreenDefinitions (String location)
    {
        Element root = loadDocument(location);
        return getScreens(root);
    }

    /**
     * 装载屏幕定义
     * @param location 屏幕定义所在的位置
     * @return 所有屏幕定义的HashMap
     */
    public static HashMap loadScreenDefinitions (InputStream location)
    {
        Element root = loadDocument(location);
        return getScreens(root);
    }

    public static HashMap loadScreenDefinitionMappings (String location)
    {
        Element root = loadDocument(location);
        return getScreenDefinitions(root);
    }

    /**
     * 得到所有的语言的屏幕定义XML，并将其装载到HashMap中。这样可以根据不同的地区确定不同的显示
     * 屏幕
     * @param root 解析屏幕定义XML得到的根
     * @return 屏幕定义HashMap
     */
    public static HashMap getScreenDefinitions (Element root)
    {
        HashMap screensDefs = new HashMap();
        List list = root.getChildren(SCREEN_DEFINITION);
        for (Iterator iter = list.iterator(); iter.hasNext(); )
        {
            Element node = (Element)iter.next();
            if (node != null)
            {
                String language = null;
                String url = null;
                if (node instanceof Element)
                {
                    language = ((Element)node).getAttributeValue(LANGUAGE);
                    url = ((Element)node).getAttributeValue(URL);
                }
                if ((language != null) && (url != null) && !screensDefs.containsKey(language))
                {
                    screensDefs.put(language, url);
                }
                else
                {
                    LogWriter.error("*** Non Fatal errror: ScreenDefinitions for language "
                        + language + " defined more than once in screen definitions file");
                }
            }
        }
        return screensDefs;
    }

    /**
     * 解析屏幕定义XML
     * @param root 屏幕定义XML的根节点
     * @return 所有的屏幕定义
     */
    public static HashMap getScreens (Element root)
    {
        HashMap screens = new HashMap();
        // 得到JSP模板的名称
        String templateName = root.getChild(TEMPLATE).getText();
        screens.put(TEMPLATE, templateName);
        // get screens
        List list = root.getChildren(SCREEN);
        for (Iterator iter = list.iterator(); iter.hasNext(); )
        {
            Element node = (Element)iter.next();
            if (node != null)
            {
                String screenName = node.getChildText(SCREEN_NAME);
                LogWriter.debug("screenName=" + screenName);
                HashMap parameters = getParameters(node);
                LogWriter.debug("parameters=" + parameters);
                Screen screen = new Screen(screenName, parameters);
                if (!screens.containsKey(screenName))
                {
                    screens.put(screenName, screen);
                }
                else
                {
                    LogWriter.error("*** 非致命错误: Screen " + screenName + " 在屏幕定义文件中的定义多于一次！");
                }
            }
        }
        return screens;
    }

    /**
     * 得到一个屏幕定义的所有参数。
     * @param node 屏幕定义XML中的一个屏幕节点
     * @return 一个屏幕定义的所有参数
     */
    private static HashMap getParameters (Element node)
    {
        HashMap params = new HashMap();
        for (Iterator iter = node.getChildren().iterator(); iter.hasNext(); )
        {
            Element child = (Element)iter.next();
            if ((child != null) && (child.getName() != null) && child.getName().equals(PARAMETER))
            {
                Element childElement = ((Element)child);
                String key = childElement.getAttributeValue(KEY);
                String value = childElement.getAttributeValue(VALUE);
                String directString = childElement.getAttributeValue(DIRECT);
                boolean direct = false;
                if ((directString != null) && directString.equals("true"))
                {
                    direct = true;
                }
                //检验参数是否进行了多次定义，如果多次定义只是提供错误信息，不进行任何处理
                if (!params.containsKey(key))
                {
                    params.put(key, new Parameter(key, value, direct));
                }
                else
                {
                    LogWriter.error("*** 非致命错误: " + "Parameter " + key
                                    + " 在屏幕定义文件中的定义多于一次！");
                }
            }
        }
        return params;
    }

    public static HashMap loadRequestMappings (String location)
    {
        Element root = loadDocument(location);
        return getRequestMappings(root);
    }

    public static HashMap loadRequestMappings (InputStream location)
    {
        Element root = loadDocument(location);
        return getRequestMappings(root);
    }

    public static HashMap getRequestMappings (Element root)
    {
        HashMap urlMappings = new HashMap();
        for (Iterator iter = root.getChildren(URL_MAPPING).iterator(); iter.hasNext(); )
        {
            Element node = (Element)iter.next();
            if (node != null)
            {
                URLMapping urlMapping = null;
                String url = "";
                String screen = null;
                String useFlowHandlerString = null;
                String flowHandler = null;
                HashMap resultMappings = null;
                boolean useFlowHandler = false;
                // get url mapping attributes
                // need to be a element to get attributes
                //读取连接，屏幕名称，是否使用流处理器，事务处理器等参数
                url = node.getAttributeValue(URL);
                screen = node.getAttributeValue(NEXT_SCREEN);
                useFlowHandlerString = node.getAttributeValue(USE_FLOW_HANDLER);                

                //是否使用流处理器
                if ("true".equals(useFlowHandlerString))
                {
                    useFlowHandler = true;
                }
                //使用流处理器的情况
                if (useFlowHandler)
                {
                    // need to be a element to find sub nodes by name
                    Element flowHandlerNode = node.getChild(FLOW_HANDLER);
                    // get the flow handler details
                    if (flowHandlerNode != null)
                    {
                        flowHandler = flowHandlerNode.getAttributeValue(FLOW_HANDLER_CLASS);
                        List results = flowHandlerNode.getChildren(HANDLER_RESULT);
                        if (results.size() > 0)
                        {
                            resultMappings = new HashMap();
                        }
                        for (Iterator it = results.iterator(); it.hasNext(); )
                        {
                            Element resultNode = (Element)it.next();
                            String key = resultNode.getAttributeValue(RESULT);
                            String value = resultNode.getAttributeValue(NEXT_SCREEN);
                            resultMappings.put(key, value);
                        } // end for
                    }
                    urlMapping = new URLMapping(url, screen, useFlowHandler, flowHandler,
                                                resultMappings);
                } // end if (useFlowHandler)
                else
                {

                    HashMap params = getRequestParamsMapping(node.getChildren(FIRST_PARAM));

                    //不使用流处理器的情况
                    urlMapping = new URLMapping(url, screen, params);
                }
                urlMappings.put(url, urlMapping);

            } // end if (node != null)
        } // end for
        return urlMappings;
    }

    private static HashMap getRequestParamsMapping (List requestParams)
    {
        HashMap requestParamsMapping = null;
        if (requestParams != null && requestParams.size() > 0)
        {
            requestParamsMapping = new HashMap();
            for (Iterator iter = requestParams.iterator(); iter.hasNext(); )
            {
                Element node = (Element)iter.next();
                RequestParamMapping mapping = new RequestParamMapping(node.getName(),
                                                                      node.
                                                                      getAttributeValue(PARAM_VALUE),
                                                                      node.getAttributeValue(
                    NEXT_SCREEN));
                //得到子标签
                mapping.setSubParams(getRequestParamsMapping(node.getChildren()));
                //存放参数
                requestParamsMapping.put(mapping.getValue(), mapping);
            }
        }
        return requestParamsMapping;
    }
}
