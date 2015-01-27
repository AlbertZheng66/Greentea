/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xt.proxy.impl.http.json;


import com.xt.core.exception.POException;
import com.xt.core.exception.SystemException;
import com.xt.core.test.http.MockHttpServletRequest;
import com.xt.core.test.http.MockHttpServletResponse;
import com.xt.proxy.event.Request;
import javax.servlet.http.HttpServletRequest;
import junit.framework.TestCase;

/**
 *
 * @author albert
 */
public class JsonClientServletTest extends TestCase {
    
    public JsonClientServletTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of createRequest method, of class JsonClientServlet.
     */
    public void xtestCreateRequest() throws Exception {
        System.out.println("createRequest");
        MockHttpServletRequest req = new MockHttpServletRequest();
        // String jsonValue = "{\"serviceClassName\":\"com.xt.gt.html.demo.TableService\",\"methodName\":\"list\",\"invokedMethods\":[{\"localMethodName\":\"setFspParameter\",\"localParams\":[{\"pagination\":{\"startIndex\":0,\"totalCount\":-1,\"rowsPerPage\":20},\"filterGroup\":{}}]}],\"params\":[]}";
        String jsonValue = "{'serviceClassName':'com.xt.gt.demo.service.BookService','methodName':'list','serviceObject':{'className':'com.xt.gt.demo.service.BookService','invokedMethods':[],'__className':'com.xt.gt.demo.service.BookService','fspParameter':{'__className':'com.xt.gt.chr.event.FspParameter','filterGroup':{'__className':'com.xt.gt.chr.event.filter.FilterGroup','filterItems':[{'name':'name','type':'CONTAINS','values':['c']},{'name':'name','type':'CONTAINS','values':['c']}]},'pagination':{'__className':'com.xt.gt.chr.event.Pagination','startIndex':0,'maxRowCount':12,'rowsPerPage':10,'totalCount':-1}}},'type':null,'params':[]}";
        req.addParameter("jsonValue", jsonValue);
        JsonClientServlet instance = new JsonClientServlet();
        Request result = instance.createRequest(req);
        // assertEquals(expResult, result);
    }

    public void testListParam() {
        String jsonValue = "{'serviceClassName':'com.xt.proxy.impl.http.json.MakingService','methodName':'buildParams','serviceObject':{'className':'com.xt.proxy.impl.http.json.MakingService','invokedMethods':[]},'type':null,'params':[[{'__className':'com.xt.proxy.impl.http.json.ParamsVO','to':'29999','from':'20000','name':'jmxPort','type':{'__className':'java.lang.Enum','name':'PORT_RANGE','ordinal':1,'title':'','className':'com.xt.proxy.impl.http.json.ParamsType'}},{'__className':'com.xt.proxy.impl.http.json.ParamsVO','to':'39999','from':'30000','name':'serverPort','type':{'__className':'java.lang.Enum','name':'PORT_RANGE','ordinal':1,'title':'','className':'com.xt.proxy.impl.http.json.ParamsType'}},{'__className':'com.xt.proxy.impl.http.json.ParamsVO','to':'49999','from':'40000','name':'serverRedirectPort','type':{'__className':'java.lang.Enum','name':'PORT_RANGE','ordinal':1,'title':'','className':'com.xt.proxy.impl.http.json.ParamsType'}},{'__className':'com.xt.proxy.impl.http.json.ParamsVO','from':'appServNo','name':'appServNo','type':{'__className':'java.lang.Enum','name':'INCREMENT','ordinal':3,'title':'','className':'com.xt.proxy.impl.http.json.ParamsType'}}]]}";
                MockHttpServletRequest req = new MockHttpServletRequest();
                req.addParameter("jsonValue", jsonValue);
        JsonClientServlet instance = new JsonClientServlet() {

            @Override
            protected boolean isMultipart(HttpServletRequest req) {
                return false;
            }
            
        };
        Request result = instance.createRequest(req);
                
    }

    public void xtestOutputThrowable() {
        POException pe = new POException("sfasl", new NullPointerException());
        SystemException se = new SystemException("asdlad", pe);
         MockHttpServletResponse res = new MockHttpServletResponse();
        JsonClientServlet instance = new JsonClientServlet();
        instance.outputThrowable(null, res, se);
        System.out.println("throwable=" + res.toString());
    }

    public void testOutputObject() {
    }

}
