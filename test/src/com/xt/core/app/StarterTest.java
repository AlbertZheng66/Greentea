/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xt.core.app;

import junit.framework.TestCase;

/**
 *
 * @author Albert
 */
public class StarterTest extends TestCase {

    public StarterTest(String testName) {
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
     * Test of main method, of class Starter.
     */
    public void testMain() {
        String[] argv = new String[]{"-l", "com.xt.gt.sys.loader.CommandLineSystemLoader",
            "-m", "CLIENT_SERVER",
            "-p", "local", "-f", "conf\\gt-config.xml"};
      
        System.setProperty("starter.class", StartableTest.class.getName());
        Starter.main(argv);
    }
}
