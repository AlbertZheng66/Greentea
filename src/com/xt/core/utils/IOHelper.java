package com.xt.core.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.channels.FileChannel;

/**
 * <p>
 * Title: GreeTea 框架。
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 *
 * @author 郑伟
 * @version 1.0
 */
public class IOHelper {

    private static final Logger logger = Logger.getLogger(IOHelper.class);
    /**
     * 数值：一兆
     */
    public static final int ONE_MILLION = 1024 * 1024;
    /**
     * 数值：1K
     */
    public static final int ONE_THOUSAND = 1024;

    private IOHelper() {
    }

    /**
     * 返回输入流的字节数。注意：计算字节之后，不会重新设置其标志(将其放在最起始位置)。
     *
     * @param in
     * @return
     */
    static public long count(InputStream in) {
        if (in == null) {
            return 0;
        }
        byte[] b = new byte[ONE_THOUSAND]; // 每次读取的数据量
        int total = 0;
        int count = 0; // 一次读取的字节数量
        try {
            while ((count = in.read(b)) >= 0) {
                // 将读出的字节写入输出
                total += count;
            }
        } catch (IOException ex) {
            throw new SystemException("由输出流到输入流的转换时发生异常", ex);
        }
        return total;
    }

    /**
     * 将输入流的数据输出到输出流中去, 本方法不负责关闭输入输出流。
     *
     * @param in InputStream 输入流信息
     * @param os OutputStream 输出流信息
     * @throws SystemException
     */
    public static long i2o(InputStream in, OutputStream os)
            throws SystemException {
        return i2o(in, os, false, false);
    }

    public static long i2o(InputStream in, OutputStream os,
            boolean isClose,
            boolean osClose) throws SystemException {
        return i2o(in, os, null, isClose, osClose);
    }

    /**
     * 将输入流的数据输出到输出流中去
     *
     * @param in InputStream 输入流信息
     * @param os OutputStream 输出流信息
     * @param isClose boolean 是否关闭输入流
     * @param osClose boolean 是否关闭输出流
     * @return 读取的字节数
     * @throws SystemException
     */
    public static long i2o(InputStream in, OutputStream os, IOProcessor processor,
            boolean isClose,
            boolean osClose) throws SystemException {
        if (in == null || os == null) {
            return 0;
            // throw new SystemException();
        }
        byte[] b = new byte[ONE_THOUSAND]; // 每次读取的数据量
        long total = 0;  // 总共读取的字节数
        int count = 0; // 一次读取的字节数量
        try {
            while ((count = in.read(b)) >= 0) {
                total += count;
                // 将读出的字节写入输出
                if (processor == null) {
                    os.write(b, 0, count);
                } else {
                    byte[] proccessed = processor.process(b, 0, count);
                    os.write(proccessed);
                }
            }
        } catch (IOException ex) {
            throw new SystemException("由输出流到输入流的转换时发生异常", ex);
        } finally {
            // 关闭输入流
            if (isClose) {
                closeSilently(in);
            }
            // 关闭输出流
            if (osClose) {
                closeSilently(os);
            }
        }
        return total;
    }

    static public ByteArrayInputStream toByteArrayInputStream(InputStream is) {
        if (is == null || (is instanceof ByteArrayInputStream)) {
            return (ByteArrayInputStream) is;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOHelper.i2o(is, baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }

    /**
     * 将字符输入流的数据输出到字符输出流中去。
     *
     *
     * @param r Reader 字符流 Reader
     * @param w Writer 字符流 Writer
     * @param isClose boolean 是否关闭输入流
     * @param osClose boolean 是否关闭输出流
     * @throws SystemException
     */
    public static long r2w(Reader r, Writer w, boolean rClose,
            boolean wClose) throws SystemException {
        if (r == null || w == null) {
            return 0;
        }
        char[] c = new char[ONE_THOUSAND]; // 每次读取的数据量
        long total = 0;  // 总共读取的字节数
        int count = 0; // 一次读取的字节数量
        try {
            while ((count = r.read(c)) >= 0) {
                // 将读出的字节写入输出
                total += count;
                w.write(c, 0, count);
            }
        } catch (IOException ex) {
            throw new SystemException("由输出流（Reader）到输入流（Writer）的转换时发生异常", ex);
        } finally {
            // 关闭输入流
            if (rClose) {
                closeSilently(r);
            }

            // 关闭输出流
            if (wClose) {
                closeSilently(w);
            }
        }
        return total;
    }

    /**
     * 将输入流的数据输出到输出流中去, 本方法不负责关闭输入输出流。
     *
     * @param r Reader 字符流 Reader
     * @param w Writer 字符流 Writer
     * @throws SystemException
     */
    public static long r2w(Reader r, Writer w)
            throws SystemException {
        return r2w(r, w, false, false);
    }

    /**
     * 将输入流的数据输出到输出流中去（最大读取一兆数据）
     *
     * @param in InputStream 输入流信息
     * @param os OutputStream 输出流信息
     * @param isClose boolean 是否关闭输入流
     * @param osClose boolean 是否关闭输出流
     * @throws SystemException
     */
    public static byte[] readBytes(InputStream in, boolean isClose)
            throws SystemException {
        byte[] ret = null; // 返回的所有的读出的字节
        int count = 0; // 一次读取的字节数量
        int totalCount = 0; // 输入流的总共的字节数量
        List<byte[]> totalBytes = new ArrayList<byte[]>();// 存储所有的读出的字节
        try {
            byte[] b = new byte[ONE_THOUSAND]; // 每次读取的数据量
            while ((count = in.read(b)) >= 0) {
                byte[] seg = new byte[count];
                // 将本次读出的字节保存在一个数组中
                System.arraycopy(b, 0, seg, 0, count);
                totalBytes.add(seg);
                totalCount += count;
            }

            // 初始化返回的数组
            ret = new byte[totalCount];

            // 将所有的字节写入的返回的数组中
            int index = 0;
            for (byte[] seg : totalBytes) {
                System.arraycopy(seg, 0, ret, index, seg.length);
                index += seg.length;
            }

            // 关闭输入流
            if (isClose) {
                in.close();
            }
        } catch (IOException ex) {
            throw new SystemException("由输出流到输入流的转换时发生异常", ex);
        }
        return ret;
    }

    public static byte[] readBytes(String fileName) {
        // TODO: fixme
        // if (StringUtils.isEmpty()) {
        //
        // }
        InputStream fis = null;
        try {
            fis = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            throw new SystemException("读取文件[" + fileName + "]字节时产生异常。", e);
        }
        return readBytes(fis, true);
    }

    /**
     * 关闭输入流，即使出现错误，也不抛出异常，只输出警告日志。 如果输入流为空，则不进行任何处理。
     *
     * @param is 输入流对象
     */
    static public void closeSilently(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                LogWriter.warn(logger, "关闭输入流时出现异常。", e);
            }
        }
    }

    /**
     * 关闭输入流，即使出现错误，也不抛出异常，只输出警告日志。 如果输入流为空，则不进行任何处理。
     *
     * @param is 输入流对象
     */
    static public void closeSilently(Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                LogWriter.warn(logger, "关闭输入流时出现异常。", e);
            }
        }
    }

    /**
     * 关闭输入流，即使出现错误，也不抛出异常，只输出警告日志。 如果输入流为空，则不进行任何处理。
     *
     * @param is 输入流对象
     */
    static public void closeSilently(Writer writer) {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                LogWriter.warn(logger, "关闭输入流时出现异常。", e);
            }
        }
    }

    /**
     * 关闭输出流，即使出现错误，也不抛出异常，只输出警告日志。 如果输出流为空，则不进行任何处理。
     *
     * @param os 输出流对象
     */
    static public void closeSilently(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                LogWriter.warn(logger, "关闭输出流时出现异常。", e);
            }
        }
    }

    /**
     * 关闭输入流，即使出现错误，也不抛出异常，只输出警告日志。 如果输入流为空，则不进行任何处理。
     *
     * @param is 输入流对象
     */
    static public void closeSilently(FileChannel fileChannel) {
        if (fileChannel != null) {
            try {
                fileChannel.close();
            } catch (IOException e) {
                LogWriter.warn(logger, "关闭文件通道时出现异常。", e);
            }
        }
    }
}
