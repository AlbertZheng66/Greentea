/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xt.core.utils;

import junit.framework.TestCase;

/**
 *
 * @author Albert
 */
public class CommandLineParserTest extends TestCase {

    public CommandLineParserTest(String testName) {
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

    public void testParse() {
        CommandLineParser clp = new CommandLineParser();
        
        String[] actual0 = clp.parse("\"\\\\\"");
        assertEquals("\"\\\"", actual0[0]);
        
        String[] actual = clp.parse("/opt/mdu/exe.sh \"a b c\" d e");
        String[] expected = new String[]{"/opt/mdu/exe.sh", "\"a b c\"", "d", "e"};
        assertEquals(actual, expected);
        
        
        actual = clp.parse("/opt/mdu/exe.sh a b c");
        expected = new String[]{"/opt/mdu/exe.sh", "a", "b", "c"};
        assertEquals(actual, expected);
        
        actual = clp.parse("\"/opt/mdu/exe.sh\" \"a b\" c");
        expected = new String[]{"\"/opt/mdu/exe.sh\"", "\"a b\"", "c"};
        assertEquals(actual, expected);
        
        actual = clp.parse("/opt/mdu/exe.sh \"a b\\\" c\" \"\\\\\" e");
        expected = new String[]{"/opt/mdu/exe.sh", "\"a b\" c\"", "\"\\\"", "e"};
        assertEquals(actual, expected);
        
        actual = clp.parse("/opt/mdu/exe.sh a\\\\\\b\\\" cd \"e f\"");
        expected = new String[]{"/opt/mdu/exe.sh", "a\\\\b\"", "cd", "\"e f\""};
        assertEquals(actual, expected);
    }

    private void assertEquals(String[] actual, String[] expected) {
        assertEquals(expected.length, actual.length);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i]);
        }
    }
}
