package com.xt.core.proc.impl.fs;

import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * 文件服务器接口，用于对一个文件进行读取已经写入，并屏蔽后端的具体实现。
 * @author albert
 */
public interface FileService {

    public void close();

    /**
     * 关闭指定的文件（当使用Reader，InputStream 等资源时，需要关闭这些服务）
     * @param fileName
     */
    public void close(String fileName);

    public Reader getReader(String fileName);

    public Writer createWriter(String fileName);

    /**
     * 从指定文件中获得输入流, 如果文件不存在,则返回空。
     * @param fileName 文件名称。
     * @return 指定文件的输入流。
     */
    public InputStream read(String fileName);

    /**
     * 从指定文件中获得输入流, 如果文件不存在,则返回空。
     * @param fileName 文件名称。
     * @return 指定文件的输入流。
     */
    public OutputStream writeTo(String fileName, boolean append);

    /**
     * 测试应用程序是否可以读取此抽象路径名表示的文件。
     * @return
     */
    public boolean canRead(String fileName);

    /**
     *  测试应用程序是否可以修改此抽象路径名表示的文件。
     * @return
     */
    public boolean canWrite(String fileName);

    //按字母顺序比较两个抽象路径名。
    // int compareTo(File pathname)

    /**
     *  当且仅当不存在具有此抽象路径名指定的名称的文件时，原子地创建由此抽象路径名指定的一个新的空文件。
     * @return
     */
    public boolean createNewFile(String fileName);
    
    /**
     *  在临时目录下创建一个临时文件，当此次操作完成时，将此文件删除。当文件名为空时，系统将随机产生一个文件名。
     * @return
     */
    public OutputStream writeToTempFile(String fileName, boolean append);

    /**
     * 在默认临时文件目录中创建一个空文件，使用给定前缀和后缀生成其名称。
     * @param prefix
     * @param suffix
     * @return
     */
// public File createTempFile(String prefix, String suffix);
    
    /**
     * 在指定目录中创建一个新的空文件，使用给定的前缀和后缀字符串生成其名称。
     * @param prefix
     * @param suffix
     * @param directory
     * @return
     */
//    public File createTempFile(String prefix, String suffix, File directory);

    /**
     * 删除此抽象路径名表示的文件或目录。
     * @return
     */
    public boolean delete(String fileName);

//  public void deleteOnExit()
//          在虚拟机终止时，请求删除此抽象路径名表示的文件或目录。
//  public boolean equals(Object obj)
//          测试此抽象路径名与给定对象是否相等。
    /**
     * 测试此抽象路径名表示的文件或目录是否存在。
     * @return
     */
    public boolean exists(String fileName);

//    /**
//     * 返回抽象路径名的绝对路径名形式。
//     * @return 
//     */
//  public File getAbsoluteFile(String fileName);
//  
//  /**
//   * 返回抽象路径名的绝对路径名字符串。
//   * @return 
//   */
//  public String getAbsolutePath(String fileName);
          
//  public File getCanonicalFile()
//          返回此抽象路径名的规范形式。
//  public String getCanonicalPath()
//          返回抽象路径名的规范路径名字符串。
    /**
     * 返回由此抽象路径名表示的文件或目录的名称。
     * @return
     */
    public String getName(String fileName);

    /**
     * 返回此抽象路径名的父路径名的路径名字符串，如果此路径名没有指定父目录，则返回 null。
     * ?
     * @return
     */
    public String getParent(String fileName);

//    /**
//     * 返回此抽象路径名的父路径名的抽象路径名，如果此路径名没有指定父目录，则返回 null。
//     * @return
//     */
//    public File getParentFile();

//    /**
//     * 将此抽象路径名转换为一个路径名字符串。
//     * @return
//     */
//    public String getPath();

// int hashCode()
//          计算此抽象路径名的哈希码。
//  public boolean isAbsolute()
//          测试此抽象路径名是否为绝对路径名。
    /**
     * 测试此抽象路径名表示的文件是否是一个目录。
     * @return
     */
    public boolean isDirectory(String fileName);

    /**
     * 测试此抽象路径名表示的文件是否是一个标准文件。
     * @return
     */
    public boolean isFile(String fileName);

//  public boolean isHidden()
//          测试此抽象路径名指定的文件是否是一个隐藏文件。
    
    /**
     * 返回此抽象路径名表示的文件最后一次被修改的时间。
     * @return
     */
    public long getLastModified(String fileName);

    /**
     * 返回由此抽象路径名表示的文件的长度。
     * @return
     */
    public long length(String fileName);

    /**
     * 返回由此抽象路径名所表示的目录中的文件和目录的名称所组成字符串数组。
     * @return
     */
    public String[] list(String directoryName);

    /**
     * 返回由包含在目录中的文件和目录的名称所组成的字符串数组，这一目录是通过满足指定过滤器的抽象路径名来表示的。
     * @param filter
     * @return
     */
    public String[] list(String directoryName, FilenameFilter filter);
//
//    /**
//     * 返回一个抽象路径名数组，这些路径名表示此抽象路径名所表示目录中的文件。
//     * @return
//     */
//    public File[] listFiles(String directoryName);
//
//    /**
//     * 返回表示此抽象路径名所表示目录中的文件和目录的抽象路径名数组，这些路径名满足特定过滤器。
//     * @param filter
//     * @return
//     */
//    public File[] listFiles(String directoryName, FileFilter filter);
//
//    /**
//     * 返回表示此抽象路径名所表示目录中的文件和目录的抽象路径名数组，这些路径名满足特定过滤器。
//     * @param filter
//     * @return
//     */
//    public File[] listFiles(String directoryName, FilenameFilter filter);

//                  列出可用的文件系统根目录。
// public File[] listRoots();
//    /**
//     *  创建此抽象路径名指定的目录。
//     * @return
//     */
//    public boolean mkdir(String directoryName);

    /**
     * 创建此抽象路径名指定的目录，包括创建必需但不存在的父目录。
     * @return
     */
    public boolean mkdirs(String directoryName);


//  public boolean renameTo(File dest)
//          重新命名此抽象路径名表示的文件。
    /**
     * 设置由此抽象路径名所指定的文件或目录的最后一次修改时间。
     * @param time
     * @return
     */
    public boolean setLastModified(String fileName, long time);
    /**
     * 标记此抽象路径名指定的文件或目录，以便只可对其进行读操作。
     * @return
     */
// public boolean setReadOnly();
//  public String toString()
//          返回此抽象路径名的路径名字符串。
// URI toURI()
//          构造一个表示此抽象路径名的 file: URI。
// URL toURL()
//          将此抽象路径名转换成一个 file: URL。
}
