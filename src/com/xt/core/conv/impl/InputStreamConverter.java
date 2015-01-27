
package com.xt.core.conv.impl;

import com.xt.core.conv.BaseConverter;
import com.xt.core.exception.SystemException;
import com.xt.core.utils.IOHelper;
import com.xt.core.utils.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * 用于输入流与字符串之间进行转换的处理器。
 * @author albert
 */
public class InputStreamConverter  implements BaseConverter<InputStream> {

    public Class<InputStream> getConvertedClass() {
        return InputStream.class;
    }

    public InputStream convert(String value) {
//        if (value == null) {
//            return null;
//        }
//        ByteArrayInputStream bais = null;
//        try {
//            System.out.println("11111111111 value.getBytes(ENCODING).length=" + value.getBytes(ENCODING).length);
//            bais = new ByteArrayInputStream(value.getBytes(ENCODING));
//        } catch (UnsupportedEncodingException ex) {
//            throw new SystemException(String.format("不支持的编码类型[%s]。", ENCODING), ex);
//        }
//        return bais;
        byte[] bytes = StringUtils.base64Decode(value, ENCODING);
        return new ByteArrayInputStream(bytes);
    }

    public String convertToString(InputStream obj) {
        if (obj == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOHelper.i2o(obj, baos, true, true);
        byte[] bytes = baos.toByteArray();
        return StringUtils.base64Encode(bytes, ENCODING);
//        try {
//            return new String(bytes, ENCODING);
//        } catch (UnsupportedEncodingException ex) {
//            throw new SystemException(String.format("不支持的编码类型[%s]。", ENCODING), ex);
//        }
    }

}
