package com.xt.proxy.impl.http.stream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import com.xt.core.utils.IOHelper;
import com.xt.core.utils.SystemUtils;
import com.xt.proxy.Proxy;
import com.xt.proxy.Context;
import com.xt.proxy.event.Request;
import com.xt.proxy.event.Response;
import com.xt.gt.sys.SystemConfiguration;
import com.xt.gt.sys.loader.SystemLoader;
import com.xt.gt.sys.loader.SystemLoaderManager;
import com.xt.gt.utils.TeeInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.lang.StringUtils;

public class HttpStreamProxy implements Proxy {

    private final Logger logger = Logger.getLogger(HttpStreamProxy.class);
    /**
     * 是否输出读取到的流数据.如果为是:系统将在"httpRes"路径下输出以当前时间为文件名, "out" 为扩展名的流文件. 此参数主要用于测试.
     */
    private final boolean outputResult = SystemConfiguration.getInstance().readBoolean("httpStream.output", false);
    /**
     * 输出读取到的流数据的基础目录.如果未定义，则使用系统的临时目录。
     */
    private final String outputBasePath = SystemConfiguration.getInstance().readString("httpStream.output.basePath", SystemUtils.getTempPath());
    /**
     * 输出读取到的流数据的文件前缀.如果未定义，则使用系统的“httpRes”。
     */
    private final String outputFilePrefix = SystemConfiguration.getInstance().readString("httpStream.output.prefix", "httpRes");
    /**
     * 当一个服务器有多个客户端时，服务器需要通过此参数来区分处理URL的Servlet。
     * 此参数的默认值为：httpStream。可通过参数“httpStream.context”进行配置。
     */
    private final String clientContext = SystemConfiguration.getInstance().readString("httpStream.context", "httpStream");
    
    /**
     * 是否将HTTP请求强制转换为HTTPS请求。
     */
    private final static boolean forceToHttps = SystemConfiguration.getInstance().readBoolean("httpStream.forceToHttps", false);
    
    /**
     * 服务器地址
     */
    protected final URL url;

    static {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};
        // Install the all-trusting trust manager
        final SSLContext sc;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(HttpStreamProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public HttpStreamProxy(String url) {
        URL _url = null;
        if (StringUtils.isEmpty(url)) {
            _url = this.readServerURL();
        } else {
            _url = appendContextUrl(url);
        }
        _url = convertToHttps(_url);
        this.url = _url;
    }

    public HttpStreamProxy() {
        this(null);
    }

    public Response invoke(Request request, Context context) throws Throwable {
        LogWriter.debug(logger, "request", request);
        verify(request);

        boolean inputStreamFlag = isInputStream(request);
        OutputStream os = null;
        List<InputStream> closingInputStream = new ArrayList(); // 需要被关闭的输入流
        try {
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
            // 打印输出结果
            if (outputResult) {
                File outputFile = new File(outputBasePath, String.format("%s-%d.out", outputFilePrefix, System.currentTimeMillis()));
                FileOutputStream fos = new FileOutputStream(outputFile, true);
                TeeInputStream tis = new TeeInputStream(bis, fos);
                closingInputStream.add(tis);
                readingStream = tis;
            }
            Object result = null;
            if (inputStreamFlag) {
                // 返回的结果是一个输入流
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                IOHelper.i2o(readingStream, baos);
                byte[] bytes = baos.toByteArray();
                LogWriter.debug(logger, "读取字节数", String.valueOf(bytes.length));
                result = new ByteArrayInputStream(bytes);
            } else {
                // 返回结果是一个普通对象
                ObjectInputStream ois = new ObjectInputStream(readingStream);
                closingInputStream.add(ois);
                result = ois.readObject();
            }

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

    protected URLConnection createConnection() throws IOException {
        // 在此回写Session id
        // Session 的形式！
        // wapbrowse.jsp;jsessionid=5AC6268DD8D4D5D1FDF5D41E9F2FD960?curAlbumID=9
        // 创建连接
        URLConnection conn = url.openConnection();
        conn.setRequestProperty("method", "POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestProperty("Content-Type", "application/text-xml");
        conn.connect();
        return conn;
    }

    protected void verify(Request request) {
        if (request == null) {
            throw new SystemException("请求不能为空。");
        }
        Class serviceClass = request.getService();
        if (serviceClass == null) {
            throw new SystemException("服务类不能为空。");
        }
        String methodName = request.getMethodName();

        if (StringUtils.isEmpty(methodName)) {
            throw new SystemException("调用的方法名称不能为空。");
        }
    }

    /**
     * 判断调用方法的返回值是不是流对象。
     */
    private boolean isInputStream(Request request) {
        Class serviceClass = request.getService();
        String methodName = request.getMethodName();
        try {
            Method method = serviceClass.getMethod(methodName, request.getParamTypes());
            return InputStream.class.isAssignableFrom(method.getReturnType());
        } catch (NoSuchMethodException ex) {
            throw new SystemException(String.format("服务器类[%s]不存在方法[%s]。", serviceClass, methodName), ex);
        } catch (SecurityException ex) {
            throw new SystemException(String.format("读取服务器类[%s]的方法[%s]出现异常。", serviceClass, methodName), ex);
        }

    }

    private URL appendContextUrl(String url) {
        StringBuilder strBld = new StringBuilder(url);

        if (!url.endsWith("/") && !clientContext.startsWith("/")) {
            strBld.append("/");
        }
        strBld.append(clientContext);
        URL _url;
        try {
            _url = new URL(strBld.toString());
        } catch (MalformedURLException ex) {
            throw new SystemException(String.format("URL[%s]的格式不正确。", url), ex);
        }
        return _url;
    }

    private URL readServerURL() throws SystemException {
        SystemLoader loader = SystemLoaderManager.getInstance().getSystemLoader();
        if (loader != null) {
            LogWriter.debug("HttpXmlProxy url", loader.getServerURL());
            return appendContextUrl(loader.getServerURL().toExternalForm());
        } else {
            throw new SystemException("不能确定当前的系统装载器。");
        }
    }

    private URL convertToHttps(URL _url) {
        if (forceToHttps && _url != null && "http".equalsIgnoreCase(_url.getProtocol())) {
            try {
                _url = new URL( "https", _url.getHost(), _url.getPort(), _url.getPath());
            } catch (MalformedURLException ex) {
                throw new SystemException(String.format("服务器地址[%s]格式错误。", _url.toString()), ex);
            }
        }
        return _url;
    }
}
