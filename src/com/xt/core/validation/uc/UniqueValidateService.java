
package com.xt.core.validation.uc;

import com.xt.core.service.IService;

/**
 * 唯一约束的服务器接口校验类。
 * @author albert
 */
public interface UniqueValidateService extends IService {
    public boolean isUnique (UniqueConstraintInfo uci);
}
