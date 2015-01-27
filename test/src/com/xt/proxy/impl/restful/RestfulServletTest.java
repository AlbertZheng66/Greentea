/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xt.proxy.impl.restful;

import com.xt.proxy.event.Request;
import junit.framework.TestCase;

/**
 *
 * @author albert
 */
public class RestfulServletTest extends TestCase {

    public RestfulServletTest(String testName) {

        super(testName);
    }

    public void testInit() throws Exception {
    }

    public void testCreateRequest() throws Exception {
    }

    public void testCreateClass() {
        RestfulServlet rs = new RestfulServlet();
        Request request = new Request();
        rs.parseClassAndMethod(request, "com/xt/proxy/impl/restful/RestfulServletTest/testCreateClass/");
        assertEquals("testCreateClass", request.getMethodName());
        assertEquals(this.getClass(), request.getService());
        rs.parseClassAndMethod(request, "com/xt/proxy/impl/restful/RestfulServletTest/testCreateClass");
        assertEquals("testCreateClass", request.getMethodName());
        assertEquals(this.getClass(), request.getService());
        try {
            rs.parseClassAndMethod(request, "/restful/RestfulServletTest/testCreateClass");
            fail();
        } catch (Throwable t) {
        }
    }

    public void testOutputResponse() throws Exception {
    }

    public void testOutputThrowable() throws Exception {
    }
}
