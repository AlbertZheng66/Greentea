package com.xt.proxy.impl;

import com.xt.proxy.Proxy;
import com.xt.proxy.Task;
import com.xt.proxy.Context;
import com.xt.proxy.event.Request;
import com.xt.proxy.event.Response;
import com.xt.proxy.local.LocalProxy2;

/**
 * 异步代理方式，用于处理Service的异步调用。此类仅限于框架内部使用，
 * 不建议在项目中直接调用。
 * 
 * @author albert
 * 
 */
public class AsynchProxy implements Proxy {

	private final Proxy original;

	private final Task task;

	public AsynchProxy(Proxy original, Task task) {
		this.original = original;
		this.task     = task;
	}

	public Response invoke(Request request, Context context) {
		InvokerThread it = new InvokerThread(request, context);
		new Thread(it).start();
		return null;
	}

	private class InvokerThread implements Runnable {

		private final Request request;

        private final Context context;

		public InvokerThread(Request request, Context client) {
			this.request = request;
            this.context  = client;
		}

		public void run() {
			if (original != null) {
				try {
					Response res = original.invoke(request, context);
					if (task != null) {
						task.onComplete(res.getReturnValue());
					}
				} catch (Throwable t) {
					if (task != null) {
						task.onError(t);
					}
					
				} finally {
					if (task != null) {
						task.onFinally();
					}
                                        if (original instanceof LocalProxy2) {
                                            ((LocalProxy2)original).onFinish();
                                        }
				}
			}

		}

	}

}
