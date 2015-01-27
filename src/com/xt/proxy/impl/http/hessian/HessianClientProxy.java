package com.xt.proxy.impl.http.hessian;

import java.net.URL;

import org.apache.log4j.Logger;

import com.caucho.hessian.client.HessianProxyFactory;
import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import com.xt.proxy.Proxy;
import com.xt.proxy.Context;
import com.xt.proxy.event.Request;
import com.xt.proxy.event.Response;
import com.xt.gt.sys.loader.SystemLoader;
import com.xt.gt.sys.loader.SystemLoaderManager;

public class HessianClientProxy  implements Proxy {
	
	private final Logger logger = Logger.getLogger(HessianClientProxy.class);
	
	private URL url = null;

	public HessianClientProxy () {
		SystemLoader loader = SystemLoaderManager.getInstance().getSystemLoader();
		if (loader != null) {
			url = loader.getServerURL();		    
			LogWriter.debug(logger, "HessianClientProxy url", url);
		} else {
			throw new SystemException("不能确定当前的系统装载器。");
		}
	}

	public Response invoke(Request request, Context context) throws Throwable {
        
		HessianProxyFactory factory = new HessianProxyFactory();
		
		// Session 的形式！
		// wapbrowse.jsp;jsessionid=5AC6268DD8D4D5D1FDF5D41E9F2FD960?curAlbumID=9
	    
	    Proxy proxy = (Proxy)factory.create(Proxy.class, url.toExternalForm());
	    
	    Response res = proxy.invoke(request, context);

		return res;
		
	}
}
