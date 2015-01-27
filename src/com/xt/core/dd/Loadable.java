package com.xt.core.dd;

/**
 * 当热部署观察点检测到文件变化时，通过回调此接口来通知部署重新加载此文件。
 * @author albert
 */
public interface Loadable {
	public void load(String fileName);
}
