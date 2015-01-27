package com.xt.core.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/**
 *
 * @author Albert
 */
public class EncodingUtils {

    private static final String encoding = Charset.defaultCharset().name();

    // char转byte
    public static byte[] getBytes(char[] chars) {
        Charset cs = Charset.forName(encoding);
        CharBuffer cb = CharBuffer.allocate(chars.length);
        cb.put(chars);
        cb.flip();
        ByteBuffer bb = cs.encode(cb);
        return bb.array();

    }

// byte转char
    public static char[] getChars(byte[] bytes) {
        Charset cs = Charset.forName(encoding);
        ByteBuffer bb = ByteBuffer.allocate(bytes.length);
        bb.put(bytes);
        bb.flip();
        CharBuffer cb = cs.decode(bb);

        return cb.array();
    }
}
