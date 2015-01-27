package com.xt.proxy.impl.http.json;

import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import com.xt.core.utils.IOHelper;
import com.xt.gt.utils.TeeInputStream;
import com.xt.proxy.Context;
import com.xt.proxy.event.Request;
import com.xt.proxy.event.Response;
import com.xt.proxy.impl.http.stream.HttpStreamProxy;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Albert
 */
public class JsonProxy extends HttpStreamProxy {

    private final Logger logger = Logger.getLogger(JsonProxy.class);

    public JsonProxy() {
    }

    public JsonProxy(String url) {
        super(url);
    }

    public Response invoke(Request request, Context context) throws Throwable {
        LogWriter.debug(logger, "request", request);
        verify(request);

        OutputStream os = null;
        List<InputStream> closingInputStream = new ArrayList(); // 需要被关闭的输入流
        try {
            // 创建连接
            URLConnection conn = createConnection();

            // 将请求对象输出到服务器端
            os = conn.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(os);
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(request);
            oos.flush();
            os.flush();

            // 从请求连接中读取响应对象
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            InputStream readingStream = bis;  // 被读取的输入流

            Object result = null;

            // 返回结果是一个普通对象
            ObjectInputStream ois = new ObjectInputStream(readingStream);
            closingInputStream.add(ois);
            result = ois.readObject();

            // 如果服务器端返回异常，则模拟抛出异常
            if (result instanceof Throwable) {
                throw (Throwable) result;
            } else if (result instanceof Response) {
                return (Response) result;
            } else {
                Response res = new Response();
                res.setReturnValue(result);
                return res;
            }
        } catch (ClassNotFoundException e) {
            throw new SystemException("类缺失异常。", e);
        } catch (MalformedURLException e) {
            throw new SystemException("远程地址解析异常。", e);
        } catch (IOException e) {
            throw new SystemException(String.format("连接服务器[%s]时出现网络异常。", url == null ? "null"
                    : url.toExternalForm()), e);
        } finally {
            IOHelper.closeSilently(os);
            for (InputStream is : closingInputStream) {
                IOHelper.closeSilently(is);
            }
        }
    }
}
