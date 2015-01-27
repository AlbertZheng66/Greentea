package com.xt.core.proc.impl.fs;

import com.xt.core.log.LogWriter;
import com.xt.core.proc.Processor;
import com.xt.core.proc.ProcessorFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author albert
 */
public class FileServiceProcessorFactory implements ProcessorFactory {
    private final Logger logger = Logger.getLogger(FileServiceProcessorFactory.class);

    /**
     * 等待删除的临时文件
     */
    private final List<File> unusedFiles = new ArrayList();
    /**
     * 用于清理的线程
     */
    private final CleaningThread cleaningThread = new CleaningThread();

    public FileServiceProcessorFactory() {
    }

    public void onInit() {
        // 启动一个临时文件清理线程
        cleaningThread.setDaemon(true);
        cleaningThread.start();
    }

    private class CleaningThread extends Thread {

        private final List<File> processingFiles = new ArrayList();
        private volatile boolean stopFlag = false;

        @Override
        public void run() {
            while (!stopFlag) {
                // 无处理的文件，等待一会
                if (unusedFiles.isEmpty()) {
                    try {
                        synchronized(this) {
                            wait(60 * 1000);
                        }
                    } catch (InterruptedException ex) {
                        LogWriter.warn2(logger, ex, "线程等待时出现终端。");
                    }
                }
                synchronized (unusedFiles) {
                    processingFiles.addAll(unusedFiles);
                    unusedFiles.clear();
                }
                
                // 慢慢删除吧
                for (Iterator<File> it = processingFiles.iterator(); it.hasNext();) {
                    File file = it.next();
                    // FIXME: 文件夹的删除
                    if (!file.delete()) {
                        //FIXME: 如果删除失败，需要记录一下
                    }
                }
            }
        }

        public boolean isStopFlag() {
            return stopFlag;
        }

        public void setStopFlag(boolean stopFlag) {
            this.stopFlag = stopFlag;
        }        
    }

    public Processor createProcessor(Class serviceClass) {
        if (FileServiceAware.class.isAssignableFrom(serviceClass)) {
            FileServiceProcessor processor = new FileServiceProcessor(this);
            return processor;
        }
        return null;
    }

    public void addUnusedTempFiles(List<File> unusedFiles) {
        if (unusedFiles == null || unusedFiles.isEmpty()) {
            return;
        }
        synchronized (this.unusedFiles) {
            this.unusedFiles.addAll(unusedFiles);
        }
//        synchronized (cleaningThread) {
//            cleaningThread.notify();
//        }
    }

    public void onDestroy() {
        cleaningThread.setStopFlag(true);
    }
}
