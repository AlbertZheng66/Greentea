/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xt.core.proc.impl.fs;

import com.xt.gt.sys.SystemConfiguration;
import junit.framework.TestCase;

/**
 *
 * @author Albert
 */
public class LocalFileServiceTest extends TestCase {
    
    private final LocalFileService localFileService;
    
    public LocalFileServiceTest(String testName) {
        super(testName);
        SystemConfiguration.getInstance().set("locaFileService.rootPath", "e:\\");
        localFileService = new LocalFileService();
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    public void testList() {
        String[] fileNames = localFileService.list("dump");
        for (int i = 0; i < fileNames.length; i++) {
            System.out.println("fileName=" + fileNames[i]);
        }
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}
}
