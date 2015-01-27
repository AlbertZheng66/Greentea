package com.xt.core.proc.impl.fs;

import com.xt.core.proc.AbstractProcessor;
import java.lang.reflect.Method;

/**
 *
 * @author albert
 */
public class FileServiceProcessor extends AbstractProcessor {

    private final FileService fileService = new LocalFileService();
    
    private final FileServiceProcessorFactory factory;

    public FileServiceProcessor(FileServiceProcessorFactory factory) {
        this.factory = factory;
    }

    public Object[] onBefore(Object service, Method method, Object[] params) {
        if (service instanceof FileServiceAware) {
            ((FileServiceAware) service).setFileService(fileService);
        }
        return params;
    }
    
    @Override
    public void onFinish() {
        // 关闭所有的文件资源
        fileService.close();
        
        // 清理所有的临时文件资源
    }
}
