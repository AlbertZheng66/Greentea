

package com.xt.core.utils;

import com.xt.gt.sys.SystemConfiguration;

/**
 * 和系统相关的一些帮助类
 * @author albert
 */
public class SystemUtils {

    private static String defaultTempPath = SystemConfiguration.getInstance().readString("system.tempPath");

    /**
     * 根据操作系统的类型，返回默认的临时文件路径。规则如下：
     *   1. 如果系统定义参数“system.tempPath”，则使用此定义的路径。
     *   2. 如果未定义，则根据操作系统的类型进行判断；如果是“Windows”平台，返回“c:\\temp”;
     *      其他平台，返回“/tmp”。
     * @return
     */
    static public String getTempPath() {
        if (org.apache.commons.lang.StringUtils.isNotEmpty(defaultTempPath)) {
            return defaultTempPath;
        }
        String tempdir = System.getProperty("java.io.tmpdir");
        if (org.apache.commons.lang.StringUtils.isNotEmpty(tempdir)) {
            return tempdir;
        }

        String osName = System.getProperty("os.name");
        if (osName != null && osName.toLowerCase().contains("windows")) {
            return "c:\\temp";
        }
        return "/tmp";
    }

}
