
package com.xt.comm.quartz;

import com.xt.core.log.LogWriter;
import com.xt.core.utils.VarTemplate;
import com.xt.gt.sys.SystemConfiguration;
import org.apache.log4j.Logger;
import org.quartz.plugins.xml.JobInitializationPlugin;

/**
 *
 * @author albert
 */
public class GtJobInitializationPlugin extends JobInitializationPlugin {

    private final Logger logger = Logger.getLogger(GtJobInitializationPlugin.class);

    public GtJobInitializationPlugin() {
        super();
    }

    /**
     * The file name (and path) to the XML file that should be read.
     * 重写这个方法，以便将路径中的相对参数替换为绝对参数。
     */
    @Override
    public void setFileNames(String fileNames) {
        String _fileNames = VarTemplate.format(fileNames, SystemConfiguration.getInstance().getParams());
        LogWriter.info2(logger, "替换后的 Quartz 的配置文件名称[%s]。", _fileNames);
        super.setFileNames(_fileNames);
    }

}
