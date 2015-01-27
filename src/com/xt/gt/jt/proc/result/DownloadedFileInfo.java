package com.xt.gt.jt.proc.result;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.apache.log4j.Logger;

/**
 * 被下载文件的相关信息。
 * @author albert
 */
public class DownloadedFileInfo {
    
    public static final String FILE_NAME_ENCODING = "ISO8859_1";
    
    public static final String FILE_TYPE_OCTET_STREAM = "application/octet-stream";
    
    private final Logger logger = Logger.getLogger(DownloadedFileInfo.class);

    /**
     * 下载文件的类型
     */
    private final String contentType;

    /**
     * 下载时使用的输入流
     */
    private final InputStream inputStream;

    /**
     * 下载文件显示的文件名，如果为空，则显示为：unknown。
     */
    private final String fileName;

    /**
     * 下载内容的长度，-1 表示未知。
     */
    private long contentLength = -1;
    
    public DownloadedFileInfo (String contentType, InputStream inputStream,
    		String fileName) {
    	this.contentType = contentType;
    	this.inputStream = inputStream;
        if (fileName == null) {
            fileName = "unknown";
        } else {
            try {
                // 对编码要进行转换，否则，当文件名为中文时将出现乱码问题
                fileName = new String(fileName.getBytes(),FILE_NAME_ENCODING);
            } catch (UnsupportedEncodingException ex) {
                // 如果编码出错，不进行任何处理
                logger.warn(String.format("不支持的编码格式[%s]", FILE_NAME_ENCODING), ex);
            }
        }
    	this.fileName    = fileName;
    }

	public String getContentType() {
		return contentType;
	}

	public String getFileName() {
		return fileName;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public long getContentLength() {
		return contentLength;
	}

	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}
    
    
}
