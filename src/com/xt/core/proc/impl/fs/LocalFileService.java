package com.xt.core.proc.impl.fs;

import com.xt.core.log.LogWriter;
import com.xt.core.utils.RandomUtils;
import com.xt.gt.sys.SystemConfiguration;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * 本地文件服务, 即通过当前路径,进行所有的文件操作。
 *
 * @author albert
 */
public class LocalFileService implements FileService {

    private final Logger logger = Logger.getLogger(LocalFileService.class);
    /**
     * 应用的跟路径，可通过参数“locaFileService.rootPath”进行配置，默认为当前路径。
     * 当参数值为零长度的字符串时，跟路径为空，即系统不再为文件名添加根路径
     */
    private final String rootPathName = SystemConfiguration.getInstance().readString("locaFileService.rootPath");
    private File tempPath;
    private File rootPath;
    /**
     * 打开的资源。 FIXME: 有些资源可以设定为“自行管理”
     */
    private Map<String, Object> openedResources = new HashMap();

    public LocalFileService() {
        if (StringUtils.isEmpty(rootPathName)) {
            rootPath = null;
        } else {
            rootPath = new File(rootPathName);
        }
    }

    /**
     * 返回系统当前的工作目录
     *
     * @return
     */
    private File getTempDir() {
        String tempDir = SystemConfiguration.getInstance().readString("locaFileService.tempDir");
        if (StringUtils.isEmpty(tempDir)) {
            tempDir = System.getProperty("java.io.tmpdir");
        }
        
        if (StringUtils.isEmpty(tempDir)) {
            if (rootPath != null) {
                // 需要和根文件通用一个系统
                tempDir = FilenameUtils.concat(rootPath.getAbsolutePath(), "temp");
            } else {
                tempDir = "temp";  // 使用当前路径
            }
        }
        
        // 不存在的话自动创建路径
        File dir = new File(tempDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        LogWriter.info2(logger, "使用临时目录[%s]", dir.getAbsoluteFile());
        return dir;
    }

    public Reader getReader(String fileName) {
        verifyReopenedResource(fileName);

        File file = createFile(fileName, true);
        try {
            FileReader fileReader = new FileReader(file);
            openedResources.put(fileName, fileReader);
            return fileReader;
        } catch (FileNotFoundException ex) {
            throw new FileServiceException(String.format("在路径[%s]下文件[%s]不存在。",
                    rootPathName, fileName), ex);
        }
    }

    /**
     * 关闭指定的资源。
     *
     * @param resource
     * @throws com.xt.core.proc.impl.fs.FileServiceException
     */
    private void closeResource(Object resource) throws FileServiceException {
        if (resource == null) {
            return;
        }
        try {
            if (resource instanceof Reader) {
                ((Reader) resource).close();
            } else if (resource instanceof Writer) {
                ((Writer) resource).close();
            } else if (resource instanceof InputStream) {
                ((InputStream) resource).close();
            } else if (resource instanceof OutputStream) {
                ((OutputStream) resource).close();
            } else {
                throw new FileServiceException(String.format("不能识别的资源类型[%s]。", resource));
            }
        } catch (IOException ex) {
            LogWriter.warn(logger, "关闭文件是出现错误。", ex);
        }
    }

    private void verifyReopenedResource(String fileName) {
        if (openedResources.containsKey(fileName)) {
            throw new FileServiceException(String.format("文件[%s]已经被打开，需要先关闭才能再继续使用。",
                    fileName));
        }
    }

    private File createFile(String fileName, boolean existTest) {
        if (StringUtils.isEmpty(fileName)) {
            throw new FileServiceException("文件名不能为空。");
        }

        if (fileName.startsWith("/")) {
            fileName = fileName.substring(1);  // 去除前导的根符号“/”
        }
        File file = null;
        if (rootPath == null) {
            file = new File(fileName);
        } else {
            file = new File(rootPath, fileName);
        }
        if (existTest && !file.exists()) {
            throw new FileServiceException(String.format("在路径[%s]下文件[%s]不存在。",
                    rootPathName, fileName));
        }
        return file;
    }

    public Writer createWriter(String fileName) {
        verifyReopenedResource(fileName);
        File file = createFile(fileName, false);
        try {
            FileWriter fileReader = new FileWriter(file);
            openedResources.put(fileName, fileReader);
            return fileReader;
        } catch (IOException ex) {
            throw new FileServiceException(String.format("在路径[%s]下文件[%s]不存在。",
                    rootPathName, fileName), ex);
        }
    }

    public InputStream read(String fileName) {
        verifyReopenedResource(fileName);
        File file = createFile(fileName, true);
        try {
            FileInputStream fis = new FileInputStream(file);
            openedResources.put(fileName, fis);
            return fis;
        } catch (IOException ex) {
            throw new FileServiceException(String.format("在路径[%s]下文件[%s]不存在。",
                    rootPathName, fileName), ex);
        }
    }

    public OutputStream writeTo(String fileName, boolean append) {
        verifyReopenedResource(fileName);
        File file = createFile(fileName, false);
        return writeTo0(file, fileName, append);
    }

    private OutputStream writeTo0(File file, String fileName, boolean append) throws FileServiceException {
        try {
            FileOutputStream fos = new FileOutputStream(file, append);
            openedResources.put(fileName, fos);
            return fos;
        } catch (IOException ex) {
            throw new FileServiceException(String.format("在路径[%s]下文件[%s]不存在。",
                    rootPathName, fileName), ex);
        }
    }

    public boolean canRead(String fileName) {
        File file = createFile(fileName, true);
        return file.canRead();
    }

    public boolean canWrite(String fileName) {
        File file = createFile(fileName, true);
        return file.canWrite();
    }

    public boolean createNewFile(String fileName) {
        if (exists(fileName)) {
            throw new FileServiceException(String.format("创建的文件[%s]已经存在。", fileName));
        }
        File file = createFile(fileName, false);
        try {
            return file.createNewFile();
        } catch (IOException ex) {
            throw new FileServiceException(String.format("在路径[%s]下创建文件[%s]时出现异常。",
                    rootPathName, fileName), ex);
        }
    }

    public boolean delete(String fileName) {
        File file = createFile(fileName, true);
        return file.delete();
    }

    public boolean exists(String fileName) {
        File file = createFile(fileName, false);
        return file.exists();
    }

    public boolean isDirectory(String fileName) {
        File file = createFile(fileName, true);
        return file.isDirectory();
    }

    public boolean isFile(String fileName) {
        File file = createFile(fileName, true);
        return file.isFile();
    }

    public long getLastModified(String fileName) {
        File file = createFile(fileName, true);
        return file.lastModified();
    }

    public long length(String fileName) {
        File file = createFile(fileName, true);
        return file.length();
    }

    public String[] list(String directoryName) {
        File file = createFile(directoryName, true);
        String[] fileNames = file.list();
        return trim(fileNames);
    }

    private String[] trim(String[] fileNames) {
        if (rootPath == null || fileNames == null) {
            // 不需要去除根路径
            return fileNames;
        }
        String[] trimedFileNames = new String[fileNames.length];
        String prefix = rootPath.getAbsolutePath();
        for (int i = 0; i < fileNames.length; i++) {
            String fileName = fileNames[i];
            if (fileName.startsWith(prefix)) {
                trimedFileNames[i] = fileName.substring(prefix.length() - 1);
            } else {
                trimedFileNames[i] = fileName;
            }
        }
        return trimedFileNames;
    }

    public String[] list(String directoryName, FilenameFilter filter) {
        File dic = createFile(directoryName, true);
        // 去除根路径
        String[] fileNames = dic.list(filter);
        return trim(fileNames);
    }

//    public File[] listFiles(String directoryName) {
//        File dic = createFile(directoryName, true);
//        return dic.listFiles();
//    }
//
//    public File[] listFiles(String directoryName, FileFilter filter) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
//
//    public File[] listFiles(String directoryName, FilenameFilter filter) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }
    public boolean mkdirs(String directoryName) {
        File dic = createFile(directoryName, false);
        return dic.mkdirs();
    }

    public boolean setLastModified(String fileName, long time) {
        File file = createFile(fileName, true);
        return file.setLastModified(time);
    }

    /**
     * 关闭已经打开的资源。
     */
    public void close() {
        for (Iterator<Object> it = openedResources.values().iterator(); it.hasNext();) {
            Object resource = it.next();
            closeResource(resource);
        }
        openedResources.clear();
    }

    public void close(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return;
        }
        if (openedResources.containsKey(fileName)) {
            Object resource = openedResources.get(fileName);
            closeResource(resource);
        }
    }

    public File getRootPath() {
        return rootPath;
    }

//    public File getAbsoluteFile(String fileName) {
//        File file = createFile(fileName, true);
//        return file.getAbsoluteFile();
//    }
//
//    public String getAbsolutePath(String fileName) {
//        File file = createFile(fileName, true);
//        return file.getAbsolutePath();
//    }
    public String getName(String fileName) {
        File file = createFile(fileName, true);
        return file.getName();
    }

    public String getParent(String fileName) {
        File file = createFile(fileName, true);
        String[] fileNames = new String[]{file.getParent()};
        fileNames = trim(fileNames);
        if (fileNames == null || fileNames.length == 0) {
            return null;
        }
        return fileNames[0];
    }

    public OutputStream writeToTempFile(String fileName, boolean append) {
        if (tempPath == null) {
            tempPath = getTempDir();
        }
        if (StringUtils.isEmpty(fileName)) {
            fileName = RandomUtils.randomAlphabetic(15) + ".tmp";
        }
        File tempFile = new File(tempPath, fileName);
        if (!tempFile.exists()) {
            try {
                tempFile.createNewFile();
            } catch (IOException ex) {
                 throw new FileServiceException(String.format("创建文件[%s]失败。",
                         tempFile.getAbsolutePath()), ex);
            }
        }
        // factory.
        return writeTo0(tempFile, tempFile.getAbsolutePath(), append);
    }

    public void setRootPath(File rootPath) {
        this.rootPath = rootPath;
    }
}
