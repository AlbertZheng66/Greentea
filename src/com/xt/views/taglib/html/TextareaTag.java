package com.xt.views.taglib.html;

import javax.servlet.jsp.JspException;

import org.apache.struts.taglib.TagUtils;

public class TextareaTag extends org.apache.struts.taglib.html.TextareaTag {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7342259277325327804L;

	/**
	 * Construct a new instance of this tag.
	 */
	public TextareaTag() {
		super();
	}


    /**
     * Renders the value displayed in the &lt;textarea&gt; tag.
     * @throws JspException
     * @since Struts 1.1
     */
    protected String renderData() throws JspException {
        String data = this.value;

        if (data == null) {
            data = this.lookupProperty(this.name, this.property);
        }
        
        return (data == null) ? "" : TagUtils.getInstance().filter(data);
    }

}
