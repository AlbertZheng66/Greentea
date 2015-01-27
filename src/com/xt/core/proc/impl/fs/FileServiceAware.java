

package com.xt.core.proc.impl.fs;

import com.xt.core.service.LocalMethod;

/**
 * 文件服务接入接口。
 * @author albert
 */
public interface FileServiceAware {

    @LocalMethod
	public void setFileService(FileService fileService);

    @LocalMethod
	public FileService getFileService();

}
