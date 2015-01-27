
package com.xt.comm.download;

import com.xt.core.exception.ServiceException;

/**
 * 文件下载服务所属异常。
 * @author Albert
 */
public class DownloadServiceException extends ServiceException {
     /**
     * 构造函数
     * 异常信息代码
     */
    public DownloadServiceException(String msg) {
        super(msg);
    }

    /**
     * 构造函数
     * 异常信息代码
     */
    public DownloadServiceException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
