package com.xt.core.utils;

import com.xt.core.exception.SystemException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author Albert
 */
public class SecurityUtils {
    
    static public String encodeMD5(File file) {
        if (file == null) {
            return "";
        }
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            IOHelper.i2o(new FileInputStream(file), baos, true, true);
        } catch (FileNotFoundException ex) {
            throw new SystemException("文件不存在", ex);
        }
        return encodeMD5(baos.toByteArray());
    }

    static public String encodeMD5(String str) {
        if (org.apache.commons.lang.StringUtils.isEmpty(str)) {
            return "";
        }
        try {
            byte[] b = str.getBytes("UTF8");
            return encodeMD5(b);
        } catch (UnsupportedEncodingException ex) {
            throw new SystemException("对字符串进行编码时出现异常。", ex);
        }
    }

    static public String encodeMD5(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(bytes);
            String md5Str = Base64.encodeBase64String(hash);
            return md5Str;
        } catch (NoSuchAlgorithmException ex) {
            throw new SystemException("对字符串进行编码时出现异常。", ex);
        }
    }
}
