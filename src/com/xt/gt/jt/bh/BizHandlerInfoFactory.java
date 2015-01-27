package com.xt.gt.jt.bh;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.xt.core.dd.DynamicDeploy;
import com.xt.core.dd.Loadable;
import com.xt.core.exception.BadParameterException;
import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import com.xt.gt.jt.bh.mapping.BizMapping;
import com.xt.gt.jt.bh.mapping.DefaultBizMapping;
import com.xt.gt.sys.SystemConfiguration;
import com.xt.gt.sys.SystemConstants;

public class BizHandlerInfoFactory {

	/**
	 * 私有的实例
	 */
	private static BizHandlerInfoFactory instance = new BizHandlerInfoFactory();

	/**
	 * 业务处理信息装载器
	 */
	private BizHandlerInfoLoader bizHandlerInfoLoader = new DefaultBizHandlerLoader();

	/**
	 * 缓存所有的业务处理信息，装载后都存储在此，以避免重复加载
	 */
	private Map<BizHandlerKey, BizHandlerInfo> bizHandlerInfos = new HashMap<BizHandlerKey, BizHandlerInfo>();

	/**
	 * 业务逻辑映射关系
	 */
	private BizMapping bizMapping = (BizMapping) SystemConfiguration
			.getInstance().readObject(SystemConstants.BIZ_HANDLER_MAPPING,
					new DefaultBizMapping());

	/**
	 * 私有构建函数
	 */
	private BizHandlerInfoFactory() {
	}

	/**
	 * 返回单件实例
	 * 
	 * @return
	 */
	public static BizHandlerInfoFactory getInstance() {
		return instance;
	}

	public void load(final String fileName) {
		if (fileName == null) {
			throw new BadParameterException("框架配置文件不能为空！");
		}

		File file = new File(fileName);
		try {
			FileInputStream fis = new FileInputStream(file);
			load(fis);
		} catch (FileNotFoundException e) {
			throw new SystemException("读取框架配置文件[" + fileName + "]时出现错误！", e);
		}
	}

	/**
	 * 从配置文件中装载业务处理类信息。如果自定义了业务装载类，则在此进行调用。
	 * 
	 * @param is
	 */
	public void load(InputStream is) {
		List<BizHandlerInfo> list = bizHandlerInfoLoader.load(is, bizMapping);
		if (bizHandlerInfos != null) {
			for (Iterator iter = list.iterator(); iter.hasNext();) {
				BizHandlerInfo bhi = (BizHandlerInfo) iter.next();

				LogWriter.debug("packageName", bhi.getPackages());
				LogWriter.debug("service name", bhi.getName());
				bizHandlerInfos.put(new BizHandlerKey(bhi.getPackages(), bhi
						.getName()), bhi);
			}
		}
	}

	/**
	 * 根据包名称（路径）和业务名称找到唯一的业务处理信息类。首先查找已经缓存的信息， 如果缓存中没有，则使用默认的映射方式，并缓存。
	 * 
	 * @param packageName
	 * @param bizName
	 * @return
	 */
	public BizHandlerInfo getBizHandlerInfo(String packageName, String bizName) {
		LogWriter.debug("packageName", packageName);
		LogWriter.debug("service name", bizName);
		BizHandlerInfo bhi = bizHandlerInfos.get(new BizHandlerKey(packageName,
				bizName));
		if (bhi == null) {
			synchronized (bizHandlerInfos) {
				// 自动转换为默认的
				bhi = bizMapping.getBizHandlerInfo(packageName, bizName);
				if (bhi == null) {
					throw new SystemException("未定义事务处理类[" + packageName + "."
							+ bizName + "]!");
				}
				bizHandlerInfos.put(new BizHandlerKey(packageName, bizName),
						bhi);
			}
		}
		return bhi;
	}

	/**
	 * 返回一个业务处理方法的信息。首先查找已经缓存的信息， 如果缓存中没有，则使用默认的映射方式，如果默认的映射方式返回空，则抛出系统异常。
	 * 
	 * @pre bizHandlerInfo不为空
	 * @param bizHandlerInfo
	 *            业务处理信息
	 * @param methodName
	 * @return 返回找到的业务方法信息或者抛出异常
	 */
	public DealMethodInfo getDealMethodInfo(BizHandlerInfo bizHandlerInfo,
			String methodName) {
		LogWriter.debug("method name", methodName);
		DealMethodInfo dealMethodInfo = bizHandlerInfo
				.getDealMethod(methodName);
		if (dealMethodInfo == null) {
			dealMethodInfo = bizMapping.getDealMethodInfo(bizHandlerInfo,
					methodName);
			if (dealMethodInfo == null) {
				throw new SystemException("未定义事务处理方法[" + methodName + "]!");
			}
			bizHandlerInfo.addDealMethodInfo(dealMethodInfo);
		}
		return dealMethodInfo;
	}

}

class BizHandlerKey {

	public final String packages;

	public final String bizHandlerName;

	public BizHandlerKey(String packages, String bizHandlerName) {
		this.packages = packages;
		this.bizHandlerName = bizHandlerName;
	}

	/**
	 * 一般情况下，bizHadler和method是不能为空的
	 * 
	 * @param pp
	 * @return 是否相等
	 */
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof BizHandlerKey)) {
			return false;
		}
		BizHandlerKey other = (BizHandlerKey) obj;
		if (other.bizHandlerName == null || bizHandlerName == null
				|| !bizHandlerName.equals(other.bizHandlerName)) {
			return false;
		}

		if (other.packages != null && !other.packages.equals(packages)) {
			return false;
		}
		if (packages != null && !packages.equals(other.packages)) {
			return false;
		}
		return true;
	}

	public int hashCode() {
		int code = 0;
		if (bizHandlerName != null) {
			code = code + bizHandlerName.hashCode();
		}
		if (packages != null) {
			code = code + packages.hashCode();
		}
		return code;
	}

	public String toString() {
		StringBuffer strBuf = new StringBuffer();
		if (bizHandlerName != null) {
			strBuf.append(bizHandlerName.toString());
		}
		strBuf.append("@");
		if (packages != null) {
			strBuf.append(packages.toString());
		}
		return strBuf.toString();
	}
}
