package com.xt.comm.download;

import com.xt.core.exception.ServiceException;
import com.xt.core.proc.impl.fs.FileService;
import com.xt.core.proc.impl.fs.LocalFileService;
import com.xt.gt.sys.SystemConfiguration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Albert
 */
public class MappingLoader implements Loader {

    private final LocalFileService fileService = new LocalFileService();
    
    private final Map<String, String> filesMapping = SystemConfiguration.getInstance().readMap("download.filesMapping");

    public MappingLoader() {
        fileService.setRootPath(null);  // 不设定
    }
    
    public void setFileService(FileService fileService) {
        // this.fileService = fileService;
    }

    public List<FileInfo> getFiles(String dirName, boolean onlyFiles) {
        List<FileInfo> fileInfos = new ArrayList();
        for (Map.Entry<String, String> entry : filesMapping.entrySet()) {
            String fileName = entry.getKey();
            fileInfos.add(getFileInfo(fileName));
        }
        return fileInfos;
    }

    public FileInfo getFileInfo(String fileName) {
        System.out.println("fileService.fileName=" + fileName);
        if (StringUtils.isEmpty(fileName)) {
            throw new ServiceException("文件名不能为空！");
        }
        String realFileName = filesMapping.get(fileName);
        System.out.println("fileService.realFileName=" + realFileName);
        
        if (StringUtils.isEmpty(realFileName)) {
            throw new ServiceException("未找到对应的映射文件！");
        }
        SimpleLoader sl = new SimpleLoader();
        sl.setFileService(fileService);
        sl.setBasePathName(null);
        System.out.println("fileService.rootPath=" + fileService.getRootPath());
        
        System.out.println("fileService.getBasePathName=" + sl.getBasePathName());
        FileInfo fi = sl.getFileInfo(realFileName);
        if (fi != null) {
            fi.setTitle(fileName);
        }
        return fi;
    }

    public LocalFileService getFileService() {
        return fileService;
    }

}
