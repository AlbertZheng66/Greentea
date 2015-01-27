/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xt.core.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;

/**
 *
 * @author albert
 */
public class VarTemplateTest extends TestCase {
    
    public VarTemplateTest(String testName) {
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

    public void testFormat() {
        System.out.println("format");
        String str = "aa${aa}b${bb} dcc${aa}${sfs";
        String expResult = "aa11b222 dcc11${sfs";
        Map<String, Object> values = new HashMap();
        values.put("aa", "11");
        values.put("bb", "222");
        String result = VarTemplate.formatMap(str, values);
        assertEquals(expResult, result);
    }
    
    public void testFormat2() {
        System.out.println("format");
        String str = "aa${aa.bb} dcc${aa}${bb}${sfs";
        String expResult = "aa333 dcc11222${sfs";
        Map<String, Object> values = new HashMap();
        values.put("aa", "11");
        values.put("bb", "222");
        values.put("aa.bb", "333");
        String result = VarTemplate.formatMap(str, values);
        assertEquals(expResult, result);
    }

    public void testGetVarNames() {
        String str = "aa${aa}b${bb} dcc${aa}${sfs${aa.bb}";
        Set<String> result = VarTemplate.getVarNames(str);
        assertTrue(result.contains("aa"));
        assertTrue(result.contains("bb"));
        assertFalse(result.contains("sfs"));
        assertTrue(result.contains("aa.bb"));
    }

    public void testFind() {
        String str = "aa${aa}b${bb} dcc${aa}${sfs";
        assertTrue(VarTemplate.contains(str));
        str = "aab}ad${sfs";
        assertFalse(VarTemplate.contains(str));
    }
    
    

}
