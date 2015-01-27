package com.xt.core.utils;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.xt.core.exception.POException;
import com.xt.core.log.LogWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SqlUtils {

    private final static Logger logger = Logger.getLogger(SqlUtils.class);

    /**
     * 判断一个值是不是应该代表 Blob 字段类型的值。如果值是数组、输入流（InputStream）和 Blob 类型，
     * 则返回true；其他情况返回 false。
     * @param value 值对象。
     * @return 对象是否是Blob字段类型的值
     */
    static public boolean isBlob(Object value) {
        if (value == null) {
            return false;
        }
        return (value instanceof byte[] || value instanceof InputStream || value instanceof Blob);
    }

    /**
     * 判断一个值是不是代表 Clob 字段类型的值。如果值是Reader和 Clob 类型，
     * 则返回true；其他情况返回 false。
     * @param value 值对象。
     * @return 对象是否是Clob字段类型的值
     */
    static public boolean isClob(Object value) {
        if (value == null) {
            return false;
        }
        return (value instanceof Reader || value instanceof Clob);
    }

    /**
     * 判断一个值是不是代表Blob 和 Clob字段类型的值。
     * @see isBlob 
     * @see isClob
     * @param value 值对象。
     * @return 对象是否是Clob 或者 Blob 字段类型的值。
     */
    static public boolean isLob(Object value) {
        return (isBlob(value) || isClob(value));
    }

    /**
     * 关闭准备语句。
     * @param ps 准备事务
     * @throws POException
     */
    public static void closeResultSet(ResultSet rs)
            throws POException {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException ex) {
            throw new POException("关闭结果集时发生异常。", ex);
        }
    }

    /**
     * 关闭准备语句和结果集，如果关闭操作出现，则记录到错误日志中并抛出异常
     *
     * @param ps
     *            准备语句
     * @param rs
     *            结果集
     * @throws POException
     */
    public static void close(PreparedStatement ps, ResultSet rs)
            throws POException {
        try {
            // 关闭结果集
            if (rs != null) {
                rs.close();
            }
            // 关闭preparedStatement对象
            if (ps != null) {
                ps.close();
            }
        } catch (SQLException ex1) {
            throw new POException("关闭结果集时出现数据库异常。", ex1);
        }

    }

    /**
     * 关闭准备语句。
     * @param ps 准备事务
     * @throws POException
     */
    public static void closePreparedStatement(PreparedStatement ps)
            throws POException {
        try {
            if (ps != null /* && !ps.isClosed()*/) {
                ps.close();
            }
        } catch (SQLException ex) {
            throw new POException("关闭准备语句时发生异常。", ex);
        }
    }

    /**
     * 从 Blob 字段中读取数据，并将其转化为指定的目标类型。
     * @param blob 字段实例，如果为空，则返回空。
     * @param targetClass 返回值的类型。目前，目标类型只能是：byte[] 和 InputStream
     * @return 字段数据。如果是目标类型 InputStream， 则实例是 ByteArrayInputStream。
     */
    public static <T> T readBlob(Blob blob, Class<T> targetClass) {
        if (blob == null) {
            return null;
        }
        if (targetClass == null) {
            throw new POException("目标类型不能为空。");
        }
        ByteArrayOutputStream bais = new ByteArrayOutputStream();
        byte[] bytes = null;
        InputStream inStream = null;
        try {
            inStream = blob.getBinaryStream();
            //data是读出并需要返回的数据，类型是byte[]
            IOHelper.i2o(inStream, bais, true, true);
            bytes = bais.toByteArray();
        } catch (Exception ex) {
            throw new POException("读取字段的字节流时出现异常。", ex);
        } finally {
            IOHelper.closeSilently(inStream);
        }
        if (targetClass == byte[].class) {
            return (T)bytes;
        } else if (InputStream.class == targetClass) {
            return (T)(new ByteArrayInputStream(bytes));
//        } else if (Blob.class.isAssignableFrom(targetClass)) {
//            Blob blob = new
        } else {
            throw new POException(String.format("不能识别的目标类型[%s]", targetClass.getName()));
        }
    }

    /**
     * 从 Clob 字段中读取数据，并将其转化为指定的目标类型。
     * @param clob 字段实例，如果为空，则返回空。
     * @param targetClass 返回值的类型。目前，目标类型只能是：char[] 和 Reader
     * @return 字段数据。如果是目标类型 Reader， 则实例是 StringReader。
     */
    public static <T> T readClob(Clob clob, Class<T> targetClass) {
        if (clob == null) {
            return null;
        }
        if (targetClass == null) {
            throw new POException("目标类型不能为空。");
        }
        StringWriter bais = new StringWriter();
        String _str = null;
        Reader reader = null;
        try {
            reader = clob.getCharacterStream();
            //data是读出并需要返回的数据，类型是byte[]
            IOHelper.r2w(reader, bais, true, true);
            _str = bais.toString();
        } catch (SQLException ex) {
            throw new POException("读取字段的字符流时出现异常。", ex);
        } finally {
            IOHelper.closeSilently(reader);
        }
        if (targetClass == char[].class) {
            return (T)_str.toCharArray();
        } else if (Reader.class == targetClass) {
            return ((T)new StringReader(_str));
        } else {
            throw new POException(String.format("不能识别的目标类型[%s]", targetClass.getName()));
        }
    }

    /**
     * 关闭事务
     * @param ps 准备事务
     * @throws POException
     */
    public static void closeStatement(Statement statement)
            throws POException {
        try {
            if (statement != null /* && !statement.isClosed() */) {
                statement.close();
            }
        } catch (SQLException ex) {
            LogWriter.error(logger, ex.getMessage(), ex);
            throw new POException("关闭Statement语句时发生异常。", ex);
        }
    }

    /**
     * 主键无参数的列表
     * @param obj1
     * @return
     */
    public static List getParams() {
        return Collections.emptyList();
    }

    /**
     * 组建一个参数的列表
     * @param obj1
     * @return
     */
    public static List getParams(Object obj1) {
        Object[] _params = new Object[1];
        _params[0] = obj1;
        return getParams(_params);
    }

    /**
     * 组建二个参数的列表
     * @param obj1
     * @return
     */
    public static List getParams(Object obj1, Object obj2) {
        Object[] _params = new Object[2];
        _params[0] = obj1;
        _params[1] = obj2;
        return getParams(_params);
    }

    /**
     * 组建三个参数的列表
     * @param obj1
     * @return
     */
    public static List getParams(Object obj1, Object obj2, Object obj3) {
        Object[] _params = new Object[3];
        _params[0] = obj1;
        _params[1] = obj2;
        _params[2] = obj3;
        return getParams(_params);
    }

    /**
     * 组建一个参数的列表
     * @param obj1
     * @return
     */
    public static List getParams(Object[] objs) {
        if (objs == null || objs.length == 0) {
            return getParams();
        }
        List params = new ArrayList(objs.length);
        for (int i = 0; i < objs.length; i++) {
            Object obj = objs[i];
            
            params.add(obj);
        }
        return params;
    }

    public static String createCountSql(String sql) {
        if (sql == null || sql.trim().length() == 0) {
            return sql;
        }
        StringBuilder strBld = new StringBuilder("SELECT COUNT(1) FROM (");
        strBld.append(sql).append(") c");
        return strBld.toString();
    }
}
