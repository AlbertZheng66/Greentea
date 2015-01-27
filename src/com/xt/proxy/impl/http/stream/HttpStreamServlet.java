package com.xt.proxy.impl.http.stream;

import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import com.xt.core.utils.IOHelper;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.xt.proxy.event.Request;
import com.xt.proxy.event.Response;
import com.xt.gt.jt.http.GreenTeaGeneralServlet;
import java.io.Serializable;
import org.apache.log4j.Logger;

/**
 * 客户端流 Servlet。此类用于处理富客户端（Swing/SWT）发出的流请求
 * （即输入流的形式是一个请求对象）；服务器接收到此请求之后，进行一定的业务处理 （使用本地代理代为完成），然后将响应对象（处理结果）以输出流的方式进行返回。
 * 如果相应对象为空，则返回空指针异常。如果其他步骤产生异常，将此异常写入输出流。
 * 
 * @author albert
 * 
 */
public class HttpStreamServlet extends GreenTeaGeneralServlet {

    private final Logger logger = Logger.getLogger(HttpStreamServlet.class);

    /**
     *
     */
    private static final long serialVersionUID = 3293280100304179221L;

    public HttpStreamServlet() {
    }

    

    /**
     * 使用流方式输出一个对象。
     *
     * @param res
     * @param obj
     * @throws IOException
     */
    private void outputObject(HttpServletResponse res, Object obj) throws IOException {
        OutputStream os = res.getOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(os);
        OutputStream closingOutputStream = null;
        try {
            if (obj == null) {
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                closingOutputStream = oos;
                oos.writeObject(new NullPointerException("返回对象不能为空。"));
                oos.flush();
            } else {
                if (obj instanceof InputStream) {
                    closingOutputStream = bos;
                    InputStream is = (InputStream) obj;
                    long total = IOHelper.i2o(is, bos, false, false);
                    LogWriter.debug2(logger, "输出字节数:[%d]", total);
                    bos.flush();
                } else {
                    if (!(obj instanceof Serializable)) {
                        throw new SystemException(String.format("对象[%s]不能序列化。", obj));
                    }
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    closingOutputStream = oos;
                    oos.writeObject(obj);
                    oos.flush();
                }
            }
        } finally {
            if (closingOutputStream != null) {
                closingOutputStream.close();
            }
        }
    }

    @Override
    protected Request createRequest(HttpServletRequest req) throws Exception {
        InputStream is = req.getInputStream();
        BufferedInputStream bis = new BufferedInputStream(is);
        ObjectInputStream ois = new ObjectInputStream(bis);
        Request request = (Request) ois.readObject();
        return request;
    }

    @Override
    protected void outputResponse(HttpServletRequest req, HttpServletResponse res, Response response) throws IOException {
        // 如果返回值为流的时候，需要单独处理
        if (response != null && response.getReturnValue() != null
                && (response.getReturnValue() instanceof InputStream)) {
            outputObject(res, response.getReturnValue());
        } else {
            outputObject(res, response);
        }
    }

    @Override
    protected void outputThrowable(HttpServletRequest req, HttpServletResponse res, Throwable throwable) throws IOException {
        outputObject(res, throwable);
    }
}
