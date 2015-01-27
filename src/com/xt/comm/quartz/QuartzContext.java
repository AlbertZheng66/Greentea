
package com.xt.comm.quartz;

import com.xt.proxy.Context;
import org.quartz.JobExecutionContext;

/**
 *
 * @author albert
 */
public class QuartzContext  implements Context {

    /**
     * 当前的启动时间。
     */
    private final long startTime = System.currentTimeMillis();

    private final JobExecutionContext executionContext;

    public QuartzContext(JobExecutionContext executionContext) {
        this.executionContext = executionContext;
    }

    public JobExecutionContext getExecutionContext() {
        return executionContext;
    }

    public long getStartTime() {
        return startTime;
    }

}
