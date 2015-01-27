/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xt.gt.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * @author Oleg Ryaboy, based on work by Miguel Enriquez 
 */
public class WindowsReqistry {

    /**
     * 
     * @param location path in the registry
     * @param key registry key
     * @return registry value or null if not found
     */
    public static final String readRegistry(String location, String key) {
        try {
            // Run reg query, then read output with StreamReader (internal class)
            Process process = Runtime.getRuntime().exec("reg query "
                    + '"' + location + "\" /v " + key);

            StreamReader reader = new StreamReader(process.getInputStream());
            reader.start();
            process.waitFor();
            reader.join();
            String output = reader.getResult();

            // Output has the following format:
            // \n<Version information>\n\n<key>    <registry type>    <value>\r\n\r\n
            int i = output.indexOf("REG_SZ");
            if (i == -1) {
                return null;
            }

            StringBuilder sw = new StringBuilder();
            i += 6; // skip REG_SZ

            // skip spaces or tabs
            for (;;) {
                if (i > output.length()) {
                    break;
                }
                char c = output.charAt(i);
                if (c != ' ' && c != '\t') {
                    break;
                }
                ++i;
            }

            // take everything until end of line
            for (;;) {
                if (i > output.length()) {
                    break;
                }
                char c = output.charAt(i);
                if (c == '\r' || c == '\n') {
                    break;
                }
                sw.append(c);
                ++i;
            }

            return sw.toString();

        } catch (Exception e) {
            return null;
        }

    }

    static class StreamReader extends Thread {

        private InputStream is;
        private StringWriter sw = new StringWriter();

        public StreamReader(InputStream is) {
            this.is = is;
        }

        public void run() {
            try {
                int c;
                while ((c = is.read()) != -1) {
                    sw.write(c);
                }
            } catch (IOException e) {
            }
        }

        public String getResult() {
            return sw.toString();
        }
    }

    public static void main(String[] args) {
        // Sample usage
        String value = WindowsReqistry.readRegistry("HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\"
                + "Explorer\\Shell Folders", "Personal");
        System.out.println(value);
    }
}