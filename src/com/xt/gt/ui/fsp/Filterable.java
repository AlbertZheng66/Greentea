

package com.xt.gt.ui.fsp;

import com.xt.gt.ui.fsp.filter.FilterItem;

/**
 *
 * @author albert
 */
public interface Filterable {
    public boolean select (Object target, FilterItem filterItem);
}
