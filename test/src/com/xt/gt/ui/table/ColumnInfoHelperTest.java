/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xt.gt.ui.table;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;

/**
 *
 * @author albert
 */
public class ColumnInfoHelperTest extends TestCase {
    
    public ColumnInfoHelperTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testCreateColumnsFromProperties_3args() {
        System.out.println("createColumnsFromProperties");
        Class bindingClass = Book.class;
        List<TableColumnInfo> result = ColumnInfoHelper.createColumnsFromAnnotation(bindingClass, null, null);
        assertTrue(result.size() > 0);
    }

}
