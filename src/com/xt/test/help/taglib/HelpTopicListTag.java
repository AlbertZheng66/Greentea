package com.xt.test.help.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.xt.core.log.LogWriter;
import com.xt.test.help.HelpTopicItem;
import com.xt.test.help.HelpTopicParser;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author zhengwei
 * @version 1.0
 */

public class HelpTopicListTag
    extends BodyTagSupport
{

    /**
     * 所有帮助主题
     */
    private Iterator topics;

    /**
     * 当前帮助主题
     */
    private HelpTopicItem topic;

    /**
     * 默认构建函数
     */
    public HelpTopicListTag ()
    {
    }

    public int doStartTag ()
        throws JspTagException
    {
        LogWriter.debug("HelpTopicListTag doStartTag.................");
        Collection col = (Collection) pageContext.getServletContext()
                         .getAttribute(HelpTopicParser.HELP_IN_SERVLET_CONTEXT);
        ArrayList temp = new ArrayList();
        for (Iterator iter = col.iterator(); iter.hasNext(); ) {
            HelpTopicItem item = (HelpTopicItem)iter.next();
            addTopic (temp, item);
        }
        LogWriter.debug("HelpTopicListTag temp=" + temp);
        topics = temp.iterator();
        if (topics == null || !topics.hasNext())
        {
            return (SKIP_BODY);
        }
        else
        {
            topic = (HelpTopicItem) topics.next();
            return (EVAL_PAGE);
        }

    }

    // process the body again with the next item if it exists
    public int doAfterBody ()
    {
        if (topics.hasNext())
        {
            topic = (HelpTopicItem) topics.next();
            return EVAL_PAGE;
        }
        else
        {
            return (SKIP_BODY);
        }
    }

    public int doEndTag ()
        throws JspException
    {
        LogWriter.debug("HelpTopicListTag doEndTag.................");
        try
        {
            BodyContent body = getBodyContent();
            if (body != null)
            {
                JspWriter out = body.getEnclosingWriter();
                out.print(body.getString());
            }
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
        if (topics.hasNext())
        {
            return EVAL_PAGE;
        }
        else
        {
            return (SKIP_BODY);
        }
    }

    private void addTopic (ArrayList topicsList, HelpTopicItem item) {
        topicsList.add(item);
        if (item.getSubTopics() != null) {
            for (Iterator iter = item.getSubTopics().iterator(); iter.hasNext(); ) {
                HelpTopicItem topicItem = (HelpTopicItem)iter.next();
                addTopic(topicsList, topicItem);
            }
        }
    }

    public HelpTopicItem getTopic()
    {
        return topic;
    }
}
