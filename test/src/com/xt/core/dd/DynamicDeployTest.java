/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xt.core.dd;

import com.xt.core.app.Stoper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import junit.framework.TestCase;

/**
 *
 * @author Albert
 */
public class DynamicDeployTest extends TestCase {
    
    
        boolean changed = false;
    
    public DynamicDeployTest(String testName) {
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
     * Test of register method, of class DynamicDeploy.
     */
    public void testRegister() throws IOException, InterruptedException {
        // Stoper.getInstance().init(); // 别的应用可能会将这个状态置位（设置为：true）
        File file = File.createTempFile("abcd123", "tmp");
        DynamicDeploy.getInstance().register(file.getAbsolutePath(), new Loadable() {

            public void load(String fileName) {
                changed = true;
            }
        });
        Thread.sleep(DynamicDeploy.CHECK_DURATION * 2);
        assertFalse(changed);
        changed = false;
        FileOutputStream fos = new FileOutputStream(file, true);
        fos.write("hello".getBytes());
        fos.close();
        
        Thread.sleep(DynamicDeploy.CHECK_DURATION * 2);
        assertTrue(changed);
    }

   
}
