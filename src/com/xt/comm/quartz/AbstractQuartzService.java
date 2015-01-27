package com.xt.comm.quartz;

import java.util.Calendar;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.xt.core.log.LogWriter;
import com.xt.core.service.IService;
import com.xt.core.utils.DateUtils;
import com.xt.proxy.Context;
import com.xt.proxy.event.Request;
import com.xt.proxy.local.LocalProxy2;

abstract public class AbstractQuartzService implements IService, Job {

    private final Logger logger = Logger.getLogger(AbstractQuartzService.class);

    public AbstractQuartzService() {
    }

    public void execute(JobExecutionContext executionContext)
            throws JobExecutionException {
        LogWriter.info2(logger, "Job class[%s] started at [%s].........",
                getClass().getName(), DateUtils.toDateStr(Calendar.getInstance(), "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
        final LocalProxy2 proxy2 = new LocalProxy2();
        try {
            // 注入参数
            Request req = new Request();
            req.setService(this.getClass());
            req.setMethodName("run");
            req.setParams(new Object[]{});
            req.setParamTypes(new Class[]{});

            Context quartzContext = new QuartzContext(executionContext);
            proxy2.invoke(req, quartzContext);
        } catch (Throwable t) {
            LogWriter.warn(logger, "业务逻辑出现异常。", t);
        } finally {
            proxy2.onFinish();
        }
        logger.info(String.format("The job of class[%s] finished at [%s]。", getClass().getName(), DateUtils.toDateStr(Calendar.getInstance(),
                "yyyy-MM-dd'T'HH:mm:ss.SSSZ")));
    }

    /**
     * 使用此方法运行业务逻辑。如同其他服务类，运行此方法之前系统已经注入必要的参数。
     */
    abstract public void run();
}
