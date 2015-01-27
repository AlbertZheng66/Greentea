
package com.xt.core.validation;

import java.util.Set;

/**
 * 
 * @author albert
 */
public interface Validatable {

    /**
     * 用于注册监听关联校验的“事件名称”，当关联校验有“错误”变为“正确”时，将触发此事件。
     */
    public static final String EVENT_RELEVANT_VALIDATE = "relevant.validate";
    
    /**
     * 对传入值进行校验，并将校验结果返回。
     * @param value 被校验的对象
     * @return 校验的结果列表。
     */
    public Set<ValidateMessage> validate(Object value);

}
