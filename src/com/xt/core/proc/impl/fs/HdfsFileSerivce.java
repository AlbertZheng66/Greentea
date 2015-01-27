
package com.xt.core.proc.impl.fs;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import org.apache.log4j.Logger;

/**
 * 采用 HDFS(Hadoop) 文件系统提供的文件服务。
 * @author albert
 */
public class HdfsFileSerivce  implements FileService {

    private final Logger logger = Logger.getLogger(HdfsFileSerivce.class);

    public void close() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void close(String fileName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Reader getReader(String fileName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Writer createWriter(String fileName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public InputStream read(String fileName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public OutputStream writeTo(String fileName, boolean append) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean canRead(String fileName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean canWrite(String fileName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean createNewFile(String fileName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean delete(String fileName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean exists(String fileName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isDirectory(String fileName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isFile(String fileName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long getLastModified(String fileName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long length(String fileName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String[] list(String directoryName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String[] list(String directoryName, FilenameFilter filter) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean mkdirs(String directoryName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean setLastModified(String fileName, long time) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public File getAbsoluteFile(String fileName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getAbsolutePath(String fileName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getName(String fileName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getParent(String fileName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public OutputStream writeToTempFile(String fileName, boolean append) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
