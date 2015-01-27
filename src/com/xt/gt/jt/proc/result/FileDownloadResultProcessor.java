package com.xt.gt.jt.proc.result;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xt.core.exception.BaseException;
import com.xt.core.exception.ServiceException;
import com.xt.core.utils.IOHelper;
import com.xt.gt.jt.proc.ActionParameter;
import com.xt.gt.jt.screen.ScreenFlowManager;

public class FileDownloadResultProcessor implements ResultProcessor {

	public FileDownloadResultProcessor() {
	}

	public void process(Object ret, HttpServletRequest req,
			HttpServletResponse res, Object bizHandler,
			ActionParameter processParams, Object[] params) throws BaseException {
		DownloadedFileInfo fd = (DownloadedFileInfo) ret;
		if (fd.getInputStream() == null) {
		    throw new ServiceException("下载的文件内容不能为空！");	
		}
		try {
			if (fd.getContentType() == null) {
				res.setContentType(DownloadedFileInfo.FILE_TYPE_OCTET_STREAM);
			} else {
				res.setContentType(fd.getContentType());
			}
			if (fd.getFileName() == null) {
				res.setHeader("Content-disposition",
						"attachment; filename=unknowed");
			} else {
				res.setHeader("Content-disposition", "attachment; filename="
						+ fd.getFileName());
			}

            if (fd.getContentLength() > 0) {
                res.setHeader("Content-Length", String.valueOf(fd.getContentLength()));
            }

			IOHelper.i2o(fd.getInputStream(), res.getOutputStream(), true, false);
		} catch (IOException e) {
			throw new ServiceException("输出文件异常！");
		}
		// 将屏幕流关闭，不再调整
		ScreenFlowManager.disable(req);
	}

	public boolean willProcess(Object ret, HttpServletRequest req) {
		return (ret != null && ret instanceof DownloadedFileInfo);
	}

}
