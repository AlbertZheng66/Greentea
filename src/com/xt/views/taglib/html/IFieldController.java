package com.xt.views.taglib.html;

import com.xt.gt.jt.event.RequestEvent;

public interface IFieldController {
	
	/**
	 * 是否显示此控件
	 * @return
	 */
    public boolean isVisiable(RequestEvent requestEvent, String param);
    
    /**
     * 此控件是否只读
     * @return
     */
    public boolean isReadonly(RequestEvent requestEvent, String param);
}
