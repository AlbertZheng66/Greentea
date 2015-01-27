

package com.xt.core.conv.impl;

import com.xt.core.conv.BaseConverter;
import com.xt.core.utils.SqlUtils;
import com.xt.core.utils.StringUtils;
import java.sql.Blob;

/**
 * 用于 Blob 与字符串之间进行转换的处理器。Blob 只能完成单向转换，即从Blob中读取数据的转换。
 * @author albert
 */
public class BlobConverter implements BaseConverter<Blob> {

    public Class<Blob> getConvertedClass() {
        return Blob.class;
    }

    public Blob convert(String value) {
        if (value == null) {
            return null;
        }
        throw new UnsupportedOperationException("不支持用此方法进行转换。");
    }

    public String convertToString(Blob blob) {
//        if (blob == null) {
//            return null;
//        }
//        byte[] bytes = SqlUtils.readBlob(blob, byte[].class);
//        try {
//            return new String(bytes, ENCODING);
//        } catch (UnsupportedEncodingException ex) {
//            throw new SystemException(String.format("不支持的编码类型[%s]。", ENCODING), ex);
//        }
        byte[] bytes = SqlUtils.readBlob(blob, byte[].class);
        return StringUtils.base64Encode(bytes, ENCODING);
    }

}
