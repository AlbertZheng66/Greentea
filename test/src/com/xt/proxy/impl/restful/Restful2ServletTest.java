/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xt.proxy.impl.restful;

import junit.framework.TestCase;

/**
 *
 * @author Albert
 */
public class Restful2ServletTest extends TestCase {
    
    public Restful2ServletTest(String testName) {
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
     public void testParseParams() {
         Restful2Servlet r2s = new Restful2Servlet();
         Object[] res = r2s.parseParams("['1','2']");
         assertEquals("1", res[0]);
         assertEquals("2", res[1]);
         res = r2s.parseParams("[1,'2']");
         assertEquals(1, res[0]);
         assertEquals("2", res[1]);
     }
}
