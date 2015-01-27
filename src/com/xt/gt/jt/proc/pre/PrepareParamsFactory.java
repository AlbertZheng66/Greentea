package com.xt.gt.jt.proc.pre;

import com.xt.core.exception.SystemException;
import com.xt.gt.jt.bh.DealMethodInfo;

public class PrepareParamsFactory {
	
	private static PrepareParamsFactory instance = new PrepareParamsFactory();
	
	/**
	 * 构建函数
	 */
    private PrepareParamsFactory () {
    	
    }
    
    /**
     * 返回单件实例
     * @return
     */
    public static PrepareParamsFactory getInstance() {
    	return instance;
    }
    
    public synchronized PrepareParams getPrepareParams (DealMethodInfo dmInfo) {
    	PrepareParams prepareParams = null;
    	if (DealMethodInfo.CUSTOMIZED_INPUT_PARAM == dmInfo.getParamType()) {
    		prepareParams = new CustomizedParams();
		} else if (DealMethodInfo.CLASS_PROPERTIES == dmInfo.getParamType()) {
			prepareParams = new ClassPropertiesParams();
		} else if (DealMethodInfo.METHOD_INPUT_PARAM == dmInfo.getParamType()) {
			prepareParams = new MethodInputPrepareParams();
		}  else if (DealMethodInfo.METHOD_INPUT_PARAM_NULL == dmInfo.getParamType()) {
			prepareParams = new NullInputPrepareParams();
		} else {
			throw new SystemException("未发现合适的方法处理[" + dmInfo.getName()
					+ "]参数类型[" + dmInfo.getParamType() + "]");
		}
    	return prepareParams;    	
    }
}
