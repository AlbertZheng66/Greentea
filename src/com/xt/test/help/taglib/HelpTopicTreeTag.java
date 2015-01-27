package com.xt.test.help.taglib;

import java.util.Collection;
import java.util.Iterator;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

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

public class HelpTopicTreeTag
    extends TagSupport
{
    /**
     * 所有帮助主题
     */
    private Collection topics;

    public HelpTopicTreeTag()
    {
    }

    public int doStartTag ()
        throws JspException
    {
        LogWriter.debug("HelpTopicTreeTag doStartTag.................");
        topics = (Collection) pageContext.getServletContext()
                        .getAttribute(HelpTopicParser.HELP_IN_SERVLET_CONTEXT);

        try{
            JspWriter out = pageContext.getOut();
            out.print(createTopic());
        } catch(Exception ex) {
            throw new JspTagException(ex.getMessage());
        }
        return(SKIP_BODY);
    }

    private String createTopic() {
        StringBuffer str = new StringBuffer("<TABLE><TBODY><TR><TD>");
        str.append(drawTopic(topics));
        str.append("</TD></TR></TBODY></TABLE>");
        return str.toString();
    }

    private String drawTopic (Collection topics) {
        StringBuffer str = new StringBuffer();
        for (Iterator iter = topics.iterator(); iter.hasNext(); ) {
            HelpTopicItem topic = (HelpTopicItem)iter.next();
            str.append("<LI ");
            if (topic.getSubTopics() != null) {
                str.append("id=foldheader ");
            }
            str.append("style=\"FONT-SIZE: 10.5pt\">").append(topic.getTitle());
            if (topic.getSubTopics() != null) {
                str.append("<UL id=foldinglist style=\"DISPLAY: none; head: \">");
                str.append(drawTopic(topic.getSubTopics()));
                str.append("</UL>");
            }
            str.append("</LI>");
        }
        return str.toString();
    }
/*

          <UL>
            <LI id=foldheader style="FONT-SIZE: 10.5pt">科目1
              <UL id=foldinglist style="DISPLAY: none; head: ">
                <LI id=foldheader style="FONT-SIZE: 9pt">篇章1
              <UL id=foldinglist style="DISPLAY: none; head: ">
                <LI style="FONT-SIZE: 9pt">章节1
                <LI style="FONT-SIZE: 9pt">章节2
              </LI></UL>
              <LI id=foldheader style="FONT-SIZE: 9pt">篇章7
              <UL id=foldinglist style="DISPLAY: none; head: ">
                <LI style="FONT-SIZE: 9pt">章节1
                <LI style="FONT-SIZE: 9pt">章节2
                <LI style="FONT-SIZE: 9pt">章节6 </LI></UL></LI></UL>
            <LI id=foldheader style="FONT-SIZE: 10.5pt">科目2
            <UL id=foldinglist style="DISPLAY: none; head: ">
              <LI style="FONT-SIZE: 9pt">篇章1
              <LI style="FONT-SIZE: 9pt">篇章2
              <LI style="FONT-SIZE: 9pt">篇章7 </LI></UL>
            </LI>
          </UL>
*/
}
