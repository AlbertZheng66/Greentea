package com.xt.core.dd;

import java.io.File;
import java.util.Calendar;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xt.core.exception.BadParameterException;
import com.xt.core.log.LogWriter;
import com.xt.core.app.Stoper;
import com.xt.gt.sys.SystemConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * 此类用于配置文件的动态部署之用。 
 *
 * @author zw
 *
 */
public class DynamicDeploy implements Runnable {
    /**
     * 检测文件是否变化的间隔时间（单位：毫秒），默认为：2000。
     */
    public static final int CHECK_DURATION = SystemConfiguration.getInstance().readInt("dd.checkDuration", 2000);

    private final Logger logger = Logger.getLogger(DynamicDeploy.class);
    /**
     * 关闭动态部署
     */
    private boolean closed = false;
    private static DynamicDeploy instance = new DynamicDeploy();
    private Map<FileDesc, DefaultLoader> fileDescs = new HashMap();
//    /**
//     * 这种方法还是比较麻烦，稍后尝试
//     */
//    private final WatchService watcher;
    private final Thread monitor;

    private DynamicDeploy() {
//        WatchService _wService = null;
//        try {
//            _wService = FileSystems.getDefault().newWatchService();
//        } catch (IOException ex) {
//            //FIXME: 启用默认线程监控方式;
//        }
//        watcher = _wService;
        monitor = new Thread(this);
        monitor.setDaemon(true);
        monitor.start();
    }

    public void run() {
        // while (!Stoper.getInstance().isStoped()) {
        while (true) {
            try {
                Thread.sleep(CHECK_DURATION);
            } catch (InterruptedException ex) {
                LogWriter.warn2(logger, "线程休眠被终止[%s]", ex);
            }
            check();
        }
    }

    public static DynamicDeploy getInstance() {

        return instance;
    }

    public void register(String fileName, Loadable loadable) {
        if (StringUtils.isEmpty(fileName)) {
            throw new BadParameterException("注册文件都不能为空！");
        }
        if (loadable == null) {
            throw new BadParameterException("装载器都不能为空！");
        }
        FileDesc desc = new FileDesc(fileName);
        DefaultLoader dLoader = fileDescs.get(desc);

        if (dLoader == null) {
            dLoader = new DefaultLoader();
            fileDescs.put(desc, dLoader);
        }
        dLoader.add(loadable);

    }
    
    public void unregister(String fileName) {
        unregister0(fileName, null);
    }
    
    public void unregister(String fileName, Loadable loadable) {
        if (loadable == null) {
            throw new BadParameterException("装载器都不能为空！");
        }
        unregister0(fileName, loadable);
    }

    private void unregister0(String fileName, Loadable loadable) {
        if (StringUtils.isEmpty(fileName)) {
            throw new BadParameterException("注册文件都不能为空！");
        }
        FileDesc desc = new FileDesc(fileName);
        if (!fileDescs.containsKey(desc)) {
            return;
        }
        // 移除文件的所有监控
        if (loadable == null) {
            fileDescs.remove(desc);
        } else {
            DefaultLoader dLoader = fileDescs.get(desc);
            if (dLoader != null) {
                dLoader.remove(loadable);
            }
        }
    }

    private void check() {
        for (Map.Entry<FileDesc, DefaultLoader> entry : fileDescs.entrySet()) {
            FileDesc fileDesc = entry.getKey();
            Loadable loadable = entry.getValue();
            checkAndReload(fileDesc, loadable);
        }
    }

    /**
     * 判断文件是否需要动态装载，如果需要，则进行装载！
     *
     * @param fileName
     * @param loader
     */
    private void checkAndReload(FileDesc desc, Loadable loadable) {
        if (closed || desc == null) {
            return;
        }

        if (desc.needReload() && loadable != null) {
            loadable.load(desc.fileName);
        }
    }

    /**
     * 关闭动态装载需求！
     *
     * @param name
     * @param loader
     */
    public synchronized void close() {
        this.closed = true;

    }

    public synchronized boolean closed() {
        return this.closed;
    }

    /**
     * 这个类用于处理对于同一个文件可能存在多个监控的情况
     */
    class DefaultLoader implements Loadable {

        private final List<Loadable> loadables = new ArrayList(2);

        public void load(String fileName) {
            for (Iterator<Loadable> it = loadables.iterator(); it.hasNext();) {
                Loadable loadable = it.next();
                try {
                    loadable.load(fileName);
                } catch (Throwable t) {
                    LogWriter.warn2(logger, t, "重新装载文件[%s]出现异常。", fileName);
                }
            }
        }

        public void add(Loadable loadable) {
            if (loadable == null || loadables.contains(loadable)) {
                return;
            }
            loadables.add(loadable);
        }

        public void remove(Loadable loadable) {
            if (loadable == null) {
                return;
            }
            loadables.remove(loadable);
        }
    }

    /**
     * 文件描述信息。针对一个文件，可能有多个Loader对其进行监听。
     *
     * @author zw
     *
     */
    class FileDesc {

        private final Logger logger = Logger.getLogger(FileDesc.class);
        /**
         * 文件名称(文件需要使用绝对路径)
         */
        private final String fileName;
        /**
         * 注册时间
         */
        private final Calendar registerTime = Calendar.getInstance();
        /**
         * 最后一次修改时间
         */
        private long lastModifiedTime = -1;
        /**
         * 文件的大小
         */
        private long fileSize = -1;
        /**
         * 文件的标识（如果一个文件的标识发生了变化，则认为这个文件发生了变化），通常是MD5编码
         */
        private Object identity;

        public FileDesc(String fileName) {
            this.fileName = fileName;
            init();
        }

        private void init() {
            File file = new File(fileName);

            lastModifiedTime = file.lastModified();
            fileSize = file.length();
        }

        /**
         * 判断此文件是否需要重新进行加载
         *
         * @return
         */
        public boolean needReload() {
            File file = new File(fileName);
            // 文件不存在的情况不需要重新加载
            if (!file.exists()) {
                return false;
            }

            long modifiedTime = file.lastModified();

            if (lastModifiedTime > 0 && lastModifiedTime != modifiedTime) {
                // 文件已经发生变化了
                init();
                return true;
            }

            // 判断文件大小
            if (fileSize >= 0 && fileSize != file.length()) {
                init();
                return true;
            }

            // 判断文件
            return false;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final FileDesc other = (FileDesc) obj;
            if ((this.fileName == null) ? (other.fileName != null) : !this.fileName.equals(other.fileName)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 67 * hash + (this.fileName != null ? this.fileName.hashCode() : 0);
            return hash;
        }
    }
}
