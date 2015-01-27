
package com.xt.core.conv.impl;

import com.xt.core.conv.BaseConverter;
import com.xt.core.exception.SystemException;
import com.xt.core.utils.StringUtils;
import java.io.UnsupportedEncodingException;

/**
 * 处理字节数组的转换器。
 * @author albert
 */
public class ByteArrayConverter implements BaseConverter<byte[]> {

    public Class<byte[]> getConvertedClass() {
        return byte[].class;
    }

    public byte[] convert(String value) {
//        if (value == null) {
//            return null;
//        }
//        try {
//            return value.getBytes(ENCODING);
//        } catch (UnsupportedEncodingException ex) {
//            throw new SystemException(String.format("不支持的编码类型[%s]。", ENCODING), ex);
//        }
        return StringUtils.base64Decode(value, ENCODING);
    }

    public String convertToString(byte[] bytes) {
//        if (bytes == null) {
//            return null;
//        }
//
//        try {
//            return new String(bytes, ENCODING);
//        } catch (UnsupportedEncodingException ex) {
//            throw new SystemException(String.format("不支持的编码类型[%s]。", ENCODING), ex);
//        }
        return StringUtils.base64Encode(bytes, ENCODING);
    }

    

}
