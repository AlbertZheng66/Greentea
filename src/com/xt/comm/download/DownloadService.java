
package com.xt.comm.download;

import com.xt.core.log.LogWriter;
import com.xt.core.service.AbstractService;
import com.xt.gt.jt.proc.result.DownloadedFileInfo;
import com.xt.gt.sys.SystemConfiguration;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 * 为应用提供通用下载服务的服务类。
 * @author Albert
 */
public class DownloadService extends AbstractService {
    private static final long serialVersionUID = -2303418544848445404L;
    
    /**
     * 文件服务的装载器。
     */
    private final Loader loader = (Loader)SystemConfiguration.getInstance().readObject("downloadService.loaderName", new SimpleLoader());
    
   
    /**
     * 下载指定的文件
     * @param fileName
     * @return 
     */
    public DownloadedFileInfo download(String fileName) {
        loader.setFileService(fileService);
        FileInfo fileInfo = loader.getFileInfo(fileName);
        LogWriter.info2(logger, "系统开始下载文件[%s]", fileInfo.getFileName());
        InputStream is = fileService.read(fileInfo.getFileName());
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        IOHelper.i2o(is, baos, true, true);
//        byte[] bytes = baos.toByteArray();
//        DownloadedFileInfo dfi = new DownloadedFileInfo(fileInfo.getType(), new ByteArrayInputStream(bytes), fileInfo.getTitle());
        DownloadedFileInfo dfi = new DownloadedFileInfo(fileInfo.getType(), is, fileInfo.getTitle());
        return dfi;
    }
    
    /**
     * 显示指定路径下的所有文件列表。
     * @param directory 路径名称
     * @return 
     */
    public List<FileInfo> list(String directory) {
        return list(directory, true);
    }
    
    /**
     * 显示指定路径下的所有文件列表。
     * @param directory 路径名称
     * @param onlyFiles 是否包含子路径名称,true，只包含文件，不包含路径。
     * @return 
     */
    public List<FileInfo> list(String directory, boolean onlyFiles) {
        if (StringUtils.isEmpty(directory)) {
            return Collections.emptyList();
        }
        loader.setFileService(fileService);
        return loader.getFiles(directory, onlyFiles);
    }
    
    public DownloadedFileInfo download2(String fileName) {
        MappingLoader ml = new MappingLoader();
        FileInfo fileInfo = ml.getFileInfo(fileName);
        LogWriter.info2(logger, "系统开始下载文件[%s]", fileInfo.getFileName());
        InputStream is = ml.getFileService().read(fileInfo.getFileName());
        DownloadedFileInfo dfi = new DownloadedFileInfo(fileInfo.getType(), is, fileInfo.getTitle());
        return dfi;
    }
    
    public List<FileInfo> list2() {
        MappingLoader ml = new MappingLoader();
        return ml.getFiles(null, true);
    }
    
}
