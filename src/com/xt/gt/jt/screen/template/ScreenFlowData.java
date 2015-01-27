package com.xt.gt.jt.screen.template;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 
 * <p>Title: XT框架-显示逻辑部分</p>
 * <p>Description:  </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-8-9
 */
public class ScreenFlowData
    implements Serializable
{
    private HashMap screenDefinitionMappings;

    public ScreenFlowData (HashMap screenDefinitionMappings)
    {
        this.screenDefinitionMappings = screenDefinitionMappings;
    }

    public HashMap getScreenDefinitionMappings ()
    {
        return screenDefinitionMappings;
    }

    public String toString ()
    {
        return "ScreenFlowData: {screenDefinitionMappings=" + screenDefinitionMappings
            + ",}";
    }
}
