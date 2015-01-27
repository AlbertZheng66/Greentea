package com.xt.test.help;

import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletContext;

import org.jdom.Element;

import com.xt.core.app.init.InitializeException;
import com.xt.core.app.init.ServletInit;
import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import com.xt.core.utils.XmlHelper;
import java.util.Map;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author zhengwei
 * @version 1.0
 */
public class HelpTopicParser
        implements ServletInit {

    /**
     * 将帮助的解析结果存放在servlet上下文当中
     */
    public final static String HELP_IN_SERVLET_CONTEXT = "HELP_IN_SERVLET_CONTEXT";

    public HelpTopicParser() {
    }

    /**
     * 在Servlet初始化时调用的方法
     * @param servletContext Servlet上下文
     * @param initParams 所有的促使化参数
     * @throws InitializeException
     */
    public void init(ServletContext servletContext, HashMap initParams)
            throws InitializeException {
        LogWriter.debug("HelpTopicParser init .................................");
        String helpStr = (String) initParams.get("help");
        LogWriter.debug("HelpTopicParser  helpStr=" + helpStr);
        //读取帮助主题文件
        InputStream helpIs = servletContext.getResourceAsStream(helpStr);
        ArrayList help = parse(helpIs);
        servletContext.setAttribute(HELP_IN_SERVLET_CONTEXT, help);

    }

    public ArrayList parse(InputStream xml)
            throws SystemException {
        Element root = null;
        try {
            root = XmlHelper.getRoot(xml);
        } catch (Exception ex) {
            throw new SystemException("解析XML：" + xml + "时出错！", ex);
        }
        return getTopics(root);
    }

    private ArrayList getTopics(Element root) {
        //得到所有的第一级帮助主题
        ArrayList topics = new ArrayList(root.getChildren().size());
        for (Iterator iter = root.getChildren().iterator(); iter.hasNext();) {
            Element topicItem = (Element) iter.next();
            topics.add(parseTopicItem(topicItem));
        }
        return topics;
    }

    /**
     * 解析帮助主题项（如果有子帮助主题，则采用方式递归解析）
     * @param topicItem 帮助主题
     * @return 一个帮助主题项，并包含所有的帮助主题
     */
    private HelpTopicItem parseTopicItem(Element topicItem) {
        HelpTopicItem topic = new HelpTopicItem();
        //从XML中读取帮助主题信息
        topic.setId(topicItem.getAttributeValue("id"));
        topic.setUrl(topicItem.getChildText("url"));
        topic.setTitle(topicItem.getChildText("title"));
        Element subTopics = topicItem.getChild("subtopics");
        LogWriter.debug("HelpTopicParser parseTopicItem menu.getId()=" + topic);

        //递归方式求解帮助主题的子帮助主题
        if (subTopics != null) {
            LogWriter.debug("HelpTopicParser parseTopicItem subTopics=" + subTopics);
            for (Iterator iter = subTopics.getChildren().iterator(); iter.hasNext();) {
                Element childTopic = (Element) iter.next();
                topic.addSubTopic(parseTopicItem(childTopic));
            }
        }
        return topic;
    }

    public void init(ServletContext servletContext, Map initParams, Connection conn) throws InitializeException {
        // TODO Auto-generated method stub
    }
}
