

package com.xt.gt.utils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 空的输出流, 不进行任何处理.
 * @author albert
 */
public class NullOutputStream extends OutputStream {

    @Override
    public void write(int b) throws IOException {
        // just do nothing
    }

}
