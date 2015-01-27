package com.xt.test.help;

import java.util.ArrayList;
import java.util.Collection;

import com.xt.core.log.LogWriter;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author zhengwei
 * @version 1.0
 */

public class HelpTopicItem
    implements Comparable
{
    /**
     * 帮助主题的标识
     */
    private String id;

    /**
     * 帮助主题的链接
     */
    private String url;

    /**
     * 帮助主题的显示文字
     */
    private String title;

    /**
     * 下级帮助主题
     */
    private Collection subTopics;

    public HelpTopicItem ()
    {
    }

    /**
     * 增加下级帮助主题
     * @param child 下级帮助主题
     */
    public void addSubTopic (HelpTopicItem child)
    {
        if (subTopics == null)
        {
            subTopics = new ArrayList();
        }
        subTopics.add(child);
    }

    public int compareTo (Object o1)
    {
        LogWriter.debug("o1=" + o1);
        HelpTopicItem item1 = (HelpTopicItem) o1;
        return getTitle().compareTo(item1.getTitle());
    }
    
    public boolean equals(Object other) {
    	if (title == null || (!(other instanceof HelpTopicItem))) {
    		return false;
    	}
    	HelpTopicItem hti = (HelpTopicItem)other;
    	return title.equals(hti.getTitle());
    }
    
    public int hashCode() {
    	if (title == null) {
    		return 0;
    	}
    	return title.hashCode();
    }

    public String getTitle ()
    {
        return title;
    }

    public String getUrl ()
    {
        return url;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    public void setUrl (String url)
    {
        this.url = url;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }
    public Collection getSubTopics()
    {
        return subTopics;
    }
    public void setSubTopics(Collection subTopics)
    {
        this.subTopics = subTopics;
    }
}
