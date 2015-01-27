

package com.xt.gt.ui.fsp;

import com.xt.core.exception.SystemException;
import com.xt.gt.ui.fsp.filter.FilterComparator;

/**
 *
 * @author albert
 */
public class NullFilterComparator implements FilterComparator {

    public boolean match(Object arg0, Object[] arg1) {
        throw new SystemException("不允许使用空过滤比较器，需要使用自定义过滤器。");
    }

}
