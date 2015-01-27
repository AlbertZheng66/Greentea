

package com.xt.core.test.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author albert
 */
public class MockHttpServletResponse
        implements HttpServletResponse {

    private final List<Cookie> cookies = new ArrayList<Cookie>();

    private final Map<String,Object> headers = new HashMap<String, Object>();

    private StringWriter writer = new StringWriter();

    private int contentLength;

    private int status;

    public void addCookie(Cookie cookie) {
        if (cookie != null) {
            cookies.add(cookie);
        }
    }

    public boolean containsHeader(String headerName) {
        if (headerName == null) {
            return false;
        }
        return headers.containsKey(headerName);
    }

    public String encodeURL(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String encodeRedirectURL(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String encodeUrl(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String encodeRedirectUrl(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void sendError(int i, String string) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void sendError(int i) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void sendRedirect(String string) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setDateHeader(String string, long l) {
        headers.put(string, l);
    }

    /**
     * TODO: 暂时没做累加处理
     * @param string
     * @param l
     */
    public void addDateHeader(String string, long l) {
        headers.put(string, l);
    }

    public void setHeader(String string, String string1) {
        headers.put(string, string1);
    }

    public void addHeader(String string, String string1) {
        headers.put(string, string1);
    }

    public void setIntHeader(String string, int i) {
        headers.put(string, i);
    }

    public void addIntHeader(String string, int i) {
        headers.put(string, i);
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setStatus(int i, String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getCharacterEncoding() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ServletOutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(writer, true);
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public void setContentType(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setBufferSize(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getBufferSize() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void flushBuffer() throws IOException {
        writer.flush();
    }

    public void resetBuffer() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isCommitted() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void reset() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setLocale(Locale locale) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Locale getLocale() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        return writer.toString();
    }

    public int getStatus() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getHeader(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<String> getHeaders(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Collection<String> getHeaderNames() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getContentType() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setCharacterEncoding(String string) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
