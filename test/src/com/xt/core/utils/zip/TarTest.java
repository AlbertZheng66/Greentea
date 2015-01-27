/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xt.core.utils.zip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.apache.commons.compress.archivers.ArchiveOutputStream;

/**
 *
 * @author Albert
 */
public class TarTest extends TestCase {
    
    public TarTest(String testName) {
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
     * Test of getArchiveOutputStream method, of class Tar.
     */
    public void testBuild() {
        System.out.println("getArchiveOutputStream");
        File rootFile = new File("E:\\temp\\repo\\001\\patches\\0.1-0.2\\0.1_0.2_0");
        File tarFile  = new File("E:\\temp\\repo\\001\\patches\\0.1_0.2_0.tar");
        try {
            Tar instance = new Tar(rootFile, new FileOutputStream(tarFile));
            instance.build();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }
        
    }

 
    
}
