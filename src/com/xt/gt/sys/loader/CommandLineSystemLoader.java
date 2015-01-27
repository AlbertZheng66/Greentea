package com.xt.gt.sys.loader;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import com.xt.gt.sys.RunMode;

/**
 *
 * @author albert
 */
public class CommandLineSystemLoader extends AbstractSystemLoader {
	
	private final Logger logger = Logger.getLogger(CommandLineSystemLoader.class);
	
	private String systemLoader;


    public CommandLineSystemLoader() {
        super();
        // 避免从属性中加载
        runMode = null;
        serverURLString = null;
        proxyType = null;
        configFileName = null;
    }

    public void setArguments(String[] args) {
    	if (args != null && args.length > 0) {
        	parse(args);
    	}
    }
    
    private void parse(String[] args) {
    	LogWriter.info(logger, "正在解析参数", StringUtils.join(args, ","));
    	
    	
    	Options options = new Options();

    	// 系统启动时将用到这个参数
        Option loaderOpt = new Option("l", "loader",   true, "系统装载器");
        Option modeOpt   = new Option("m", "mode",   true, "运行模式");
        Option proxyOpt  = new Option("p", "proxy", true, "代理类型");
        Option serverOpt = new Option("s", "server", true, "服务器地址");
        Option fileOpt   = new Option("f", "file", true, "配置文件名称");
        options.addOption(loaderOpt).addOption(modeOpt).addOption(serverOpt).addOption(proxyOpt).addOption(fileOpt);
        BasicParser bp = new BasicParser();
        try {
        	CommandLine commandLine = bp.parse(options, args);
        	systemLoader = commandLine.getOptionValue(loaderOpt.getOpt());
        	LogWriter.info(logger, "systemLoader", systemLoader);
        	
        	String rmString = commandLine.getOptionValue(modeOpt.getOpt());
        	LogWriter.info(logger, "rmString", rmString);
            if (StringUtils.isNotEmpty(rmString)) {
                runMode = RunMode.valueOf(rmString);
            }
            
            serverURLString = commandLine.getOptionValue(serverOpt.getOpt());
        	LogWriter.info(logger, "serverURLString", serverURLString);
            
            proxyType = commandLine.getOptionValue(proxyOpt.getOpt());
        	LogWriter.info(logger, "proxyType", proxyType);
        	
            configFileName = commandLine.getOptionValue(fileOpt.getOpt());
        	LogWriter.info(logger, "configFileName", configFileName);
        } catch (ParseException ex) {
            throw new SystemException(String.format("解析命令行参数[%s]异常。", StringUtils.join(args, ",")), ex);
        }
    }

	public String getSystemLoader() {
		return systemLoader;
	}
    
    
    
}
