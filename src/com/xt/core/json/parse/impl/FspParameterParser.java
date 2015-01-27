/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xt.core.json.parse.impl;

import com.xt.core.json.parse.Parsable;
import com.xt.core.log.LogWriter;
import com.xt.gt.ui.fsp.FspParameter;
import com.xt.gt.ui.fsp.Pagination;
import com.xt.gt.ui.fsp.filter.FilterGroup;
import com.xt.gt.ui.fsp.filter.FilterType;
import com.xt.gt.ui.fsp.filter.SimpleFilterItem;
import java.util.ArrayList;
import java.util.Iterator;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author albert
 */
public class FspParameterParser implements Parsable {

    private final Logger logger = Logger.getLogger(FspParameterParser.class);

    public Object parse(Object obj) {
        if (!(obj instanceof JSONObject)) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        JSONObject jsonObj = (JSONObject)obj;
        FspParameter fspParameter = new FspParameter();
        Pagination pg = parsePagination(jsonObj.getJSONObject("pagination"));
        fspParameter.setPagination(pg);
        FilterGroup fg = parseFilterGroup(jsonObj.getJSONObject("filterGroup"));
        fspParameter.setFilterGroup(fg);
        return fspParameter;
    }

    /**
     * 解析分页相关参数
     * @param jsonObj
     * @return
     */
    private Pagination parsePagination (JSONObject jsonObj) {
        Pagination pagination = new Pagination();
        if (jsonObj != null) {
            pagination.setMaxRowCount(jsonObj.containsKey("maxRowCount") ? jsonObj.getInt("maxRowCount") : -1);
            pagination.setRowsPerPage(jsonObj.containsKey("rowsPerPage") ? jsonObj.getInt("rowsPerPage") : -1);
            pagination.setName(jsonObj.containsKey("name") ? jsonObj.getString("name") : "");
            pagination.setStartIndex(jsonObj.containsKey("startIndex") ? jsonObj.getInt("startIndex") : 0);
            pagination.setTotalCount(jsonObj.containsKey("totalCount") ? jsonObj.getInt("totalCount") : -1);
        }
        return pagination;
    }

    private FilterGroup parseFilterGroup (JSONObject jsonObj) {
        FilterGroup filterGroup = new FilterGroup();
        if (jsonObj != null) {
            JSONArray filterItems = jsonObj.getJSONArray("filterItems");
            for (Iterator iter = filterItems.iterator(); iter.hasNext(); ) {
                JSONObject _fi = (JSONObject)iter.next();
                SimpleFilterItem sfi = new SimpleFilterItem();
                sfi.setName(_fi.getString("name"));
                // TODO: 异常情况判断
                // FIXME:因为前后台参数拷贝的原因（前台operatorName），后台（type）
                String _type = _fi.containsKey("operatorName") ? _fi.getString("operatorName") : _fi.getString("type");
                LogWriter.debug(logger, "_type", _type);
                if (StringUtils.isNotEmpty(_type)) {
                    sfi.setType(FilterType.valueOf(_type));
                }
                ArrayList values = new ArrayList(1);
                for(Iterator valuesIter = _fi.getJSONArray("values").iterator(); valuesIter.hasNext(); ) {
                    Object value = valuesIter.next();
                    values.add(value);
                }
                sfi.setValues(values);
                filterGroup.addFilterItem(sfi);
            }
        }
        return filterGroup;
    }

}
