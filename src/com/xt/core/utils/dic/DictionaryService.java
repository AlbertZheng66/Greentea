package com.xt.core.utils.dic;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * <p>Title: 框架类.</p>
 * <p>Description: 字典服务类.所有的字典需要从此处获得.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class DictionaryService
{
    //单件的实例
    private final static DictionaryService instance = new DictionaryService();
    
    private Map<String, Dictionary> dics = new HashMap<String, Dictionary>();
    

    /**
     * 私有构建函数
     */
    private DictionaryService ()
    {
    }

    /**
     * 返回单件实例
     * @return POContext
     */
    public static DictionaryService getInstnace () {
        return instance;
    }

    /**
     * 根据名称返回指定的字典.
     * @param name String
     * @return Dictionary
     */
    public Dictionary get (String name) throws DictionaryException {
    	if (StringUtils.isEmpty(name)) {
    		throw new DictionaryException("字典名称不能为空。");
    	}
    	Dictionary dic = (Dictionary)dics.get(name);
    	if (null == dic) {
    		throw new DictionaryException("需要的字典[" + name + "]未注册。");
    	}
        return dic;
    }
    
    /**
     * 根据名称返回指定的字典.
     * @param name String
     * @return Dictionary
     */
    public boolean exists (String name) throws DictionaryException{
    	if (StringUtils.isEmpty(name)) {
    		throw new DictionaryException("字典名称不能为空。");
    	}
    	return dics.containsKey(name);
    }
    
    /**
     * 返回参数字典
     * @param name
     * @return
     * @throws BaseException
     */
    public ParamDictionary get (String name, String param) throws DictionaryException {
    	Dictionary dic = get(name);
    	if (!(dic instanceof ParamDictionary)) {
    		throw new DictionaryException("需要的字典[" + name + "]非参数字典!");
    	}
    	ParamDictionary pDic = (ParamDictionary)dic;
    	pDic.setParameters(param);
        return pDic;
    }
    
    /**
     * 注册一个字典
     * @param name
     * @param dic
     */
    public void register (String name, Dictionary dic) {
    	dics.put(name, dic);
    }
}
