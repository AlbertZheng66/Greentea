
package com.xt.comm.download;

import com.xt.core.proc.impl.fs.FileService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 装载文件信息
 * @author Albert
 */
public interface Loader {
    
    /**
     * 类型映射表。主键是文件扩展名，键值是对应的类型。
     */
    public final static Map<String, String> TYPE_MAPPING = new HashMap();
    
    
    /**
     * 有其他指定文件服务类
     * @param fileService 
     */
    public void setFileService(FileService fileService);
    
    /**
     * 加载指定路径下的所有文件
     */
    public List<FileInfo> getFiles(String dirName, boolean onlyFiles);
    
     /**
     * 加载指定路径下的所有文件
     */
    public FileInfo getFileInfo(String fileName);
    
}
