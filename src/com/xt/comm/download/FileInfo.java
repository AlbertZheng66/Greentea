
package com.xt.comm.download;

import java.io.Serializable;
import java.util.Calendar;

/**
 * 描述文件进行信息的类。
 * @author Albert
 */
public class FileInfo implements Serializable {
    
    /**
     * 文件的名称（包括其路径，可能是相对的，也可能是绝对的）。
     */
    private String fileName;
    
    /**
     * 文件的显示名称，默认是系统的文件名称。
     */
    private String title;
    
    /**
     * 最后修改时间
     */
    private Calendar lastModifiedTime;
    
    /**
     * 创建时间
     */
    private Calendar createdTime;
    
    /**
     * 文件字节数
     */
    private long size;
    
    /**
     * 文件类型
     */
    private String type;
    
    /**
     * 是否是路径
     */
    private boolean directory;

    public FileInfo() {
    }

    public Calendar getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Calendar createdTime) {
        this.createdTime = createdTime;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Calendar getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Calendar lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FileInfo other = (FileInfo) obj;
        if ((this.fileName == null) ? (other.fileName != null) : !this.fileName.equals(other.fileName)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (this.fileName != null ? this.fileName.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "FileInfo{" + "fileName=" + fileName + ", title=" + title + ", lastModifiedTime=" + lastModifiedTime + ", createdTime=" + createdTime + ", size=" + size + ", type=" + type + '}';
    }
    
}
