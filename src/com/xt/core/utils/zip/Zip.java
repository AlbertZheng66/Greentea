

package com.xt.core.utils.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Albert
 */
public class Zip {

    protected final File rootFile;
    
    protected final OutputStream outputStream;
    
    protected String encoding = "UTF-8";
    
    /**
     * 如果是根目录，忽略输出根目录
     */
    private boolean ignoreRootDir = true;

    public Zip(File rootFile, OutputStream outputStream) {
        this.rootFile = rootFile;
        this.outputStream = outputStream;
    }

    public void build() throws IOException {
        ArchiveOutputStream zaos = getArchiveOutputStream();
        if (rootFile.isDirectory()) {
            zipDir(zaos, null, rootFile, ignoreRootDir);
        } else {
            zipFile(zaos, null, rootFile);
        }
        zaos.close();
        outputStream.close();
    }
    
    protected ArchiveOutputStream getArchiveOutputStream() {
        ZipArchiveOutputStream zaos = new ZipArchiveOutputStream(outputStream);
        zaos.setEncoding(encoding);
        return zaos;
    }

    protected void zipFile(ArchiveOutputStream zaos, String base, File file) throws IOException {
        String entryName = getEntryName(base, file);
        ArchiveEntry entry = new ZipArchiveEntry(file, entryName);
        zaos.putArchiveEntry(entry);
        IOUtils.copy(new FileInputStream(file), zaos);
        zaos.closeArchiveEntry();
    }

    protected String getEntryName(String base, File file) {
        String entryName = StringUtils.isEmpty(base) ? file.getName()
                : String.format("%s/%s", base, file.getName());
        return entryName;
    }

    private void zipDir(ArchiveOutputStream zaos, String base, File file, boolean ignoreRootDir) throws IOException {
        File[] children = file.listFiles();
        String entryName = null;
        if (!ignoreRootDir) {
           entryName = getEntryName(base, file);
        }

        if (children != null) {
            for (File child : children) {
                if (child.isFile()) {
                    zipFile(zaos, entryName, child);
                } else if (child.isDirectory()) {
                    zipDir(zaos, entryName, child, false);
                } else {
                    System.out.println("不能处理的文件类型");
                }
            }
        }
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public boolean isIgnoreRootDir() {
        return ignoreRootDir;
    }

    public void setIgnoreRootDir(boolean ignoreRootDir) {
        this.ignoreRootDir = ignoreRootDir;
    }
    
    
}

