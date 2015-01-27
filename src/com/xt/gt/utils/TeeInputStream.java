

package com.xt.gt.utils;

import com.xt.core.exception.BadParameterException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 用于将 InputStream 分流到其他的输出流中， 以便于测试时使用。
 * @author albert
 */
public class TeeInputStream extends InputStream {

    /**
     * 原始的输入流
     */
    private final InputStream originalInputStream;

    private final OutputStream outputStream;

    public TeeInputStream(InputStream originalInputStream, OutputStream outputStream) {
        this.originalInputStream = originalInputStream;
        this.outputStream = outputStream;
        if (originalInputStream == null) {
            throw new BadParameterException("原始输入流不能为空。");
        }
        if (outputStream == null) {
            throw new BadParameterException("输出流不能为空。");
        }
    }

    @Override
    public int read() throws IOException {
        int b = originalInputStream.read();
        outputStream.write(b);
        return b;
    }

    @Override
    public int available() throws IOException {
        return originalInputStream.available();
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
        originalInputStream.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        originalInputStream.mark(readlimit);
    }

    @Override
    public boolean markSupported() {
        return originalInputStream.markSupported();
    }

    @Override
    public int read(byte[] b) throws IOException {
        int length = originalInputStream.read(b);
        if (length > 0) {
            byte[] _bytes = new byte[length];
            System.arraycopy(b, 0, _bytes, 0, length);
            outputStream.write(_bytes);
        }
        return length;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int length = originalInputStream.read(b, off, len);
        if (length > 0) {
            byte[] _bytes = new byte[length];
            System.arraycopy(b, off, _bytes, 0, length);
            outputStream.write(_bytes);
        }
        return length;
    }

    @Override
    public synchronized void reset() throws IOException {
        originalInputStream.reset();
    }

    @Override
    public long skip(long n) throws IOException {
        return originalInputStream.skip(n);
    }

}
