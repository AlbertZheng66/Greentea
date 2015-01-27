package com.xt.comm;

import com.xt.core.log.LogWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author albert
 */
public class CleanerManager {

    private static final CleanerManager instance = new CleanerManager();
    private final Logger logger = Logger.getLogger(CleanerManager.class);
    private final List<Cleanable> cleaners = new ArrayList();

    private CleanerManager() {
    }

    static public CleanerManager getInstance() {
        return instance;
    }

    synchronized public void register(Cleanable cleanable) {
        if (cleanable != null) {
            cleaners.add(cleanable);
        }
    }

    synchronized public void unregister(Cleanable cleanable) {
        if (cleanable != null) {
            cleaners.remove(cleanable);
        }
    }

    synchronized public void clean() {
        for (Iterator<Cleanable> it = cleaners.iterator(); it.hasNext();) {
            Cleanable cleanable = it.next();
            try {
                cleanable.clean();
            } catch (Throwable t) {
                // 异常不再向外传播
                LogWriter.warn2(logger, t, "清理器[%s]出现异常。", cleanable);
            }
        }
    }
}
