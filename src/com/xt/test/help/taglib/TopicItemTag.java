package com.xt.test.help.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.xt.core.log.LogWriter;
import com.xt.test.help.HelpTopicItem;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author zhengwei
 * @version 1.0
 */

public class TopicItemTag
    extends TagSupport
{
    /**
    * 当前帮助主题
    */
   private HelpTopicItem topic;

   /**
    * 属性
    */
   private String attribute;

    public TopicItemTag()
    {
    }

    public int doStartTag ()
        throws JspException
    {
        LogWriter.debug("CustomiseQueryTableColumnTag doStartTag.................");
        HelpTopicTag topicTag = (HelpTopicTag)findAncestorWithClass(this, HelpTopicTag.class);
        if (topicTag == null)
        {
            throw new JspTagException("TopicItemTag not inside HelpTopicTag tag");
        }
        topic = topicTag.getTopic();
        try{
            JspWriter out = pageContext.getOut();
            out.print(createText());
        } catch(Exception ex) {
            throw new JspTagException(ex.getMessage());
        }
        return(SKIP_BODY);
    }

    private String createText() {
        if ("id".equals(attribute)) {
            return topic.getId();
        } else if ("title".equals(attribute)) {
            return topic.getTitle();
        } else if ("url".equals(attribute)) {
            return topic.getUrl();
        }
        return "";
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }
}
