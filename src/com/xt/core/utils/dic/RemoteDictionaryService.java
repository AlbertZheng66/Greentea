package com.xt.core.utils.dic;

import com.xt.core.exception.ServiceException;
import com.xt.core.service.IService;
import com.xt.core.utils.ClassHelper;
import org.apache.commons.lang.StringUtils;

/**
 * 在客户端使用字典时需要先调用此服务得到相应的字典。
 * @author albert
 *
 */
public class RemoteDictionaryService implements IService {

    public RemoteDictionaryService() {
    }

    /**
     * 判断一个字典是否已经注册。
     * @param dicName 字典名称，如果为空，返回 false。
     * @return 如果字典已经注册，返回true，否则返回false。
     */
    public boolean exists(String dicName) {
        if (StringUtils.isEmpty(dicName)) {
            return false;
        }
        return DictionaryService.getInstnace().exists(dicName);
    }

    /**
     * 根据指定的字典名称返回相应的字典。
     * @param dicName 指定名称
     * @return        字典实例，不为空。
     * @throws ServiceException 如果指定的名称为空或者字典不存在, 系统将抛出服务异常..
     */
    public Dictionary getDictionary(String dicName) {
        if (StringUtils.isEmpty(dicName)) {
            throw new ServiceException("字典的名称不能为空。");
        }
        DictionaryService disService = DictionaryService.getInstnace();
        if (disService.exists(dicName)) {
            return DictionaryService.getInstnace().get(dicName);
        }
        // 尝试是否是枚举类型的
        if (isEnum(dicName)) {
            return _getEnumDictionary(dicName, false);
        }
        throw new ServiceException(String.format("字典[%s]不存在。", dicName));
    }

    private boolean isEnum(String enumClassName) {
        Class enumClass = ClassHelper.getClass(enumClassName);
        if (enumClass == null) {
            // throw new ServiceException(String.format("类[%s]未定义。", enumClassName));
            return false;
        }
        return Enum.class.isAssignableFrom(enumClass);
    }

    /**
     * 返回和枚举类型对应的字典。
     * @param enumClassName
     * @return
     */
    public Dictionary getEnumDictionary(String enumClassName) {
        return _getEnumDictionary(enumClassName, true);
    }

    private Dictionary _getEnumDictionary(String enumClassName, boolean validate) {
        DictionaryService dicService = DictionaryService.getInstnace();
        if (validate && dicService.exists(enumClassName)) {
            return DictionaryService.getInstnace().get(enumClassName);
        }
        if (validate && isEnum(enumClassName)) {
            throw new ServiceException(String.format("类[%s]必须是枚举类型。", enumClassName));
        }
        Class enumClass = ClassHelper.getClass(enumClassName);
        Dictionary dic = new EnumDictionary(enumClass);
        dicService.register(enumClassName, dic);
        return dic;
    }
}
