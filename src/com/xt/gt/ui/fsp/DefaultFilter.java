/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xt.gt.ui.fsp;

import com.xt.gt.ui.fsp.filter.FilterItem;

/**
 *
 * @author albert
 */
public class DefaultFilter implements Filterable {

    public DefaultFilter() {
    }

    public boolean select(Object row, FilterItem filterItem) {
        if (filterItem != null) {
            return filterItem.match(row);
        }
        return true;
    }

}