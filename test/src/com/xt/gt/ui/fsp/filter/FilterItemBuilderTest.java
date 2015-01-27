
package com.xt.gt.ui.fsp.filter;

import junit.framework.TestCase;

/**
 *
 * @author albert
 */
public class FilterItemBuilderTest extends TestCase {
    
    public FilterItemBuilderTest(String testName) {
        super(testName);
    }

    public void testCreateSimpleFilterItem() {
        FilterItem expected = new SimpleFilterItem("aa", FilterType.STARTS_WITH, "aabb");
        FilterItem actual = FilterItemBuilder.createSimpleFilterItem("aa", FilterType.STARTS_WITH, "aabb").getFilterItem();
        assertEquals(expected, actual);
        actual = new FilterItemBuilder().and("aa", FilterType.STARTS_WITH, "aabb").getFilterItem();
        assertEquals(expected, actual);
        actual = new FilterItemBuilder().or("aa", FilterType.STARTS_WITH, "aabb").getFilterItem();
        assertEquals(expected, actual);
        actual = new FilterItemBuilder().or("aa", FilterType.STARTS_WITH, "aabb").and((FilterItem)null).getFilterItem();
        assertEquals(expected, actual);
    }

    public void testAnd_3args() {
    }

    public void testAnd_FilterItem() {
        FilterItem actual = FilterItemBuilder.createSimpleFilterItem("aa", FilterType.STARTS_WITH, "aabb").and("cc", FilterType.STARTS_WITH, "22").getFilterItem();
        QLBuilder builder = new IPOQLBuilder();
        String expected = "(aa LIKE ?||'%' AND cc LIKE ?||'%')";
        assertEquals(expected, builder.getQLString(actual, null));
    }

    public void testOr_3args() {
    }

    public void testOr_FilterItem() {
        FilterItem actual = FilterItemBuilder.createSimpleFilterItem("aa", FilterType.STARTS_WITH, "aabb").or("cc", FilterType.STARTS_WITH, "22").and("ss", FilterType.STARTS_WITH, "33").getFilterItem();
        QLBuilder builder = new IPOQLBuilder();
        String expected = "((aa LIKE ?||'%' OR cc LIKE ?||'%') AND ss LIKE ?||'%')";
        assertEquals(expected, builder.getQLString(actual, null));

        FilterItem actual2 = FilterItemBuilder.createSimpleFilterItem("aa", FilterType.STARTS_WITH, "aabb").and(FilterItemBuilder.createSimpleFilterItem("cc", FilterType.STARTS_WITH, "22").or("ss", FilterType.STARTS_WITH, "33")).getFilterItem();
        expected = "(aa LIKE ?||'%' AND (cc LIKE ?||'%' OR ss LIKE ?||'%'))";
        assertEquals(expected, builder.getQLString(actual2, null));
    }

    public void testGetFilterItem() {
    }

}
