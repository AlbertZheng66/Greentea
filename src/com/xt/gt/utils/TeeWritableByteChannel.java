package com.xt.gt.utils;

import com.xt.core.exception.BadParameterException;
import com.xt.core.log.LogWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import org.apache.log4j.Logger;

/**
 *
 * @author Albert
 */
public class TeeWritableByteChannel implements WritableByteChannel {

    private Logger logger = Logger.getLogger(TeeWritableByteChannel.class);
    /**
     * 原始通道
     */
    private final WritableByteChannel originalChannel;
    /**
     * 转向的输出通道
     */
    private final WritableByteChannel redirectedChannel;

    public TeeWritableByteChannel(WritableByteChannel originalChannel, WritableByteChannel redirectedChannel) {
        this.originalChannel = originalChannel;
        this.redirectedChannel = redirectedChannel;

        if (originalChannel == null) {
            throw new BadParameterException("原始通道不能为空。");
        }
        if (redirectedChannel == null) {
            throw new BadParameterException("输出通道不能为空。");
        }
    }

    public int write(ByteBuffer src) throws IOException {
        if (src != null) {
            try {
                ByteBuffer _redirect = src.duplicate();
                redirectedChannel.write(_redirect);
            } catch (Throwable t) {
                LogWriter.warn2(logger, t, "写入转向通道时出现异常。");
            }
        }
        return originalChannel.write(src);
    }

    public boolean isOpen() {
        return originalChannel.isOpen();
    }

    public void close() throws IOException {
        originalChannel.close();
        try {
            // 输出不能影响原始信息
            redirectedChannel.close();
        } catch (Throwable t) {
            LogWriter.warn2(logger, t, "关闭输出通道时出现异常。");
        }
    }
}
