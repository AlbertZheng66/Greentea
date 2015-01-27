package com.xt.comm.encoding;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.xt.core.log.LogWriter;
import com.xt.core.utils.BooleanUtils;
import org.apache.log4j.Logger;

/**
 * <p>
 * Filter that sets the character encoding to be used in parsing the incoming
 * request, either unconditionally or only if the client did not specify a
 * character encoding. Configuration of this filter is based on the following
 * initialization parameters:
 * </p>
 * <ul>
 * <li><strong>encoding</strong> - The character encoding to be configured for
 * this request, either conditionally or unconditionally based on the
 * <code>ignore</code> initialization parameter. This parameter is required,
 * so there is no default.</li>
 * <li><strong>ignore</strong> - If set to "true", any character encoding
 * specified by the client is ignored, and the value returned by the
 * <code>selectEncoding()</code> method is set. If set to "false,
 * <code>selectEncoding()</code> is called <strong>only</strong> if the
 * client has not already specified an encoding. By default, this parameter is
 * set to "true".</li>
 * </ul>
 * 
 * <p>
 * Although this filter can be used unchanged, it is also easy to subclass it
 * and make the <code>selectEncoding()</code> method more intelligent about
 * what encoding to choose, based on characteristics of the incoming request
 * (such as the values of the <code>Accept-Language</code> and
 * <code>User-Agent</code> headers, or a value stashed in the current user's
 * session.
 * </p>
 * 
 * @author <a href="mailto:jwtronics@yahoo.com">John Wong</a>
 * 
 * @version $Id: SetCharacterEncodingFilter.java,v 1.1 2005/10/26 02:53:44
 *          liyong Exp $
 */
public class EncodingFilter implements Filter {

	// ----------------------------------------------------- Instance Variables

    private final Logger logger = Logger.getLogger(EncodingFilter.class);

	/**
	 * The default character encoding to set for requests that pass through this
	 * filter.
	 */
	private static String encoding = null;

	/**
	 * The filter configuration object we are associated with. If this value is
	 * null, this filter instance is not currently configured.
	 */
	protected FilterConfig filterConfig = null;

	private static String contentType;

	/**
	 * Should a character encoding specified by the client be ignored?
	 */
	protected boolean ignore = true;

	public EncodingFilter() {

	}

	/**
	 * Place this filter into service.
	 * 
	 * @param filterConfig
	 *            The filter configuration object
	 */
	public void init(FilterConfig filterConfig) throws ServletException {

		this.filterConfig = filterConfig;
		encoding = filterConfig.getInitParameter("encoding");
		LogWriter.debug(logger, "encoding=", encoding);
		String value = filterConfig.getInitParameter("ignore");
		contentType = filterConfig.getInitParameter("contentType");
		ignore = BooleanUtils.isTrue(value, false);

		if (contentType != null) {
			contentType = "text/html;charset=" + contentType;
		} else {
			contentType = "text/html;charset=GB2312";
		}
		LogWriter.debug("contentType=", contentType);
	}

	/**
	 * Select and set (if specified) the character encoding to be used to
	 * interpret request parameters for this request.
	 * 
	 * @param request
	 *            The servlet request we are processing
	 * @param result
	 *            The servlet response we are creating
	 * @param chain
	 *            The filter chain we are processing
	 * 
	 * @exception IOException
	 *                if an input/output error occurs
	 * @exception ServletException
	 *                if a servlet error occurs
	 */
	public void doFilter(ServletRequest srequest, ServletResponse sresponse,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) srequest;

//		LogWriter.debug(logger, "Content-type=", request.getContentType());

		// prototype的头，只采用utf-8编码格式
		String prototypeHeader = request.getHeader("X-Prototype-Version");
        /**/
		if (prototypeHeader != null) {
			request.setCharacterEncoding("utf-8");
		} else if (ignore || (request.getCharacterEncoding() == null)
				&& (encoding != null)) {
			request.setCharacterEncoding(encoding);
		}
		// LogWriter.debug(logger, "after", request.getCharacterEncoding());

		// 对文件的输出是否会产生影响
		sresponse.setContentType(contentType);

		// Pass control on to the next filter
		chain.doFilter(request, sresponse);

	}

	/**
	 * Take this filter out of service.
	 */
	public void destroy() {
		this.filterConfig = null;
	}

	/**
	 * Select an appropriate character encoding to be used, based on the
	 * characteristics of the current request and/or filter initialization
	 * parameters. If no character encoding should be set, return
	 * <code>null</code>.
	 * <p>
	 * The default implementation unconditionally returns the value configured
	 * by the <strong>encoding</strong> initialization parameter for this
	 * filter.
	 * 
	 * @param request
	 *            The servlet request we are processing
	 */
	protected String selectEncoding(ServletRequest request) {

		return (this.encoding);

	}

	public static String getContentType() {
		return contentType;
	}

	public static void setContentType(String contentType) {
		EncodingFilter.contentType = contentType;
	}

	public static String getEncoding() {
		return encoding;
	}

}
