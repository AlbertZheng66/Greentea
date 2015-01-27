package com.xt.core.test.file;

import java.io.File;
import java.io.FileInputStream;

import junit.framework.TestCase;

import com.xt.core.log.LogWriter;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description:文件单元测试类。当单元测试需要相应的测试文件时，实现此方法，并将测试文件存放在
 * 指定的目录下（目前的情形下是统一放置在工程的test/files目录下）。 </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public abstract class FileTestCase
    extends TestCase
{
    /* 文件实例 */
    protected File theFile = null;
    
    protected FileInputStream theFileInputStream = null;
    
    //测试目录的环境变量
    private final static String GREEN_TEA_TEST_HOME = "GREEN_TEA_TEST_HOME";

    //单元测试中需要的测试文件的存放位置
    protected String path = System.getenv(GREEN_TEA_TEST_HOME);

    public FileTestCase ()
    {
        super();
    }

    /**
     * 返回文件的名称
     * @return 文件的名称
     */
    protected abstract String getFileName ();

    public FileTestCase (String name)
    {
        super(name);
    }

    protected void setUp ()
        throws Exception
    {
        super.setUp();

        String fileName = getFileName();
        
        if (path != null) {
        	fileName = path + fileName;
        }

        LogWriter.debug("fileName", fileName);

        //初始化文件
        theFile = new File(fileName);
        
        theFileInputStream = new FileInputStream(theFile);
    }

    protected void tearDown ()
        throws Exception
    {
        super.tearDown();
    }
}
