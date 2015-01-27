/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xt.comm.service;

import com.xt.comm.download.DownloadService;
import com.xt.core.proc.impl.fs.LocalFileService;
import com.xt.core.utils.IOHelper;
import com.xt.gt.jt.proc.result.DownloadedFileInfo;
import com.xt.gt.sys.SystemConfiguration;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;

/**
 *
 * @author Albert
 */
public class DownloadServiceTest extends TestCase {
    
    
    private final DownloadService downloadService;

    public DownloadServiceTest(String testName) {
        super(testName);
        SystemConfiguration.getInstance().set("locaFileService.rootPath", "E:\\work\\xthinker\\gt_html\\deploy\\");
        SystemConfiguration.getInstance().set("simpleLoader.basePath", "downloads");
        final LocalFileService localFileService = new LocalFileService();
        downloadService = new DownloadService();
        downloadService.setFileService(localFileService);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testDownload() {
        DownloadedFileInfo dfi = downloadService.download("boot.js");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOHelper.i2o(dfi.getInputStream(), baos, true, true);
        try {
            System.out.println(new String(baos.toByteArray(), "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(DownloadServiceTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
