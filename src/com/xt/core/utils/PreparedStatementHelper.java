package com.xt.core.utils;

import com.xt.core.conv.Converter;
import com.xt.core.conv.ConverterFactory;
import com.xt.core.conv.impl.EnumConverterType;
import com.xt.core.conv.impl.EnumConverterTypeDecl;
import com.xt.core.exception.POException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;

import javax.sql.RowSet;
import org.apache.log4j.Logger;

/**
 * <p>
 * Title: 框架类.
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author 郑伟
 * @version 1.0
 */
public class PreparedStatementHelper {

    private static final Logger logger = Logger.getLogger(PreparedStatementHelper.class);

    public PreparedStatementHelper() {
    }

    public static void setValue(PreparedStatement ps, int index, Object value)
            throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.CHAR);
        } else if (value instanceof String) {
            String str = (String) value;
            ps.setString(index, str);
        } else if (value instanceof Long) {
            ps.setLong(index, ((Long) value).longValue());
        } else if (value instanceof Integer) {
            ps.setInt(index, ((Integer) value).intValue());
        } else if (value instanceof Double) {
            double dblValue = ((Double) value).doubleValue();
            ps.setDouble(index, dblValue);
        } else if (value instanceof Timestamp) {
            ps.setTimestamp(index, ((Timestamp) value));
        } else if (value instanceof Date) {
            ps.setDate(index, (Date) value);
        } else if (value instanceof Time) {
            ps.setTime(index, ((Time) value));
        } else if (value instanceof Calendar) {
            ps.setTimestamp(index, (DateUtils.convertCalendarToTimestamp((Calendar) value)));
        } else if (value instanceof java.util.Date) {
            ps.setDate(index, DateUtils.convertUtilDateToSqlDate(((java.util.Date) value)));
        } else if (value instanceof Boolean) {
            ps.setBoolean(index, ((Boolean) value).booleanValue());
        } else if (value instanceof Short) {
            ps.setShort(index, ((Short) value).shortValue());

        } else if (value instanceof Float) {
            ps.setFloat(index, ((Float) value).floatValue());
        } else if (value instanceof Blob) {
            ps.setBlob(index, ((Blob) value));
        } else if (value instanceof Clob) {
            ps.setClob(index, ((Clob) value));
        } else if (value instanceof InputStream) {
            InputStream is = (InputStream) value;
            ps.setAsciiStream(index, is, 100);
        } else if (value instanceof BigInteger) {
            ps.setBigDecimal(index, new BigDecimal((BigInteger) value));
        } else if (value instanceof BigDecimal) {
            ps.setBigDecimal(index, (BigDecimal) value);
        } else if (value.getClass().isEnum()) {
            // 在数值是枚举类型是需要进行转换（默认为“String”类型）
            EnumConverterTypeDecl type = value.getClass().getAnnotation(EnumConverterTypeDecl.class);
            if (type != null && type.value() == EnumConverterType.Integer) {
                Converter conv = ConverterFactory.getInstance().getConverter(value.getClass(), Integer.class);
                if (conv != null) {
                    Integer _int = (Integer) conv.convert(value.getClass(), Integer.class, value);
                    ps.setInt(index, _int.intValue());
                } else {
                    throw new POException(String.format("未发现从类型[%s]到类型[%s]的转换器。", value.getClass(), Integer.class));
                }
            } else {
                Converter conv = ConverterFactory.getInstance().getConverter(value.getClass(), String.class);
                if (conv != null) {
                    String _value = (String) conv.convert(value.getClass(), String.class, value);
                    ps.setString(index, _value);
                } else {
                    throw new POException(String.format("未发现从类型[%s]到类型[%s]的转换器。", value.getClass(), String.class));
                }
            }
        } else {
            ps.setObject(index, value);
        }

    }

    private static void setValue(RowSet rs, int index, Object value)
            throws SQLException {
        if (value == null) {
            rs.setNull(index, Types.CHAR);
        } else if (value instanceof String) {
            String str = (String) value;
            rs.setString(index, str);
        } else if (value instanceof Long) {
            rs.setLong(index, ((Long) value).longValue());
        } else if (value instanceof Integer) {
            rs.setInt(index, ((Integer) value).intValue());
        } else if (value instanceof Double) {
            double dblValue = ((Double) value).doubleValue();
            rs.setDouble(index, dblValue);
        } else if (value instanceof Timestamp) {
            rs.setTimestamp(index, ((Timestamp) value));
        } else if (value instanceof Date) {
            rs.setDate(index, (Date) value);
        } else if (value instanceof Time) {
            rs.setTime(index, ((Time) value));
        } else if (value instanceof Calendar) {
            rs.setTimestamp(index, (DateUtils.convertCalendarToTimestamp((Calendar) value)));
        } else if (value instanceof java.util.Date) {
            rs.setDate(index, DateUtils.convertUtilDateToSqlDate(((java.util.Date) value)));
        } else if (value instanceof Boolean) {
            rs.setBoolean(index, ((Boolean) value).booleanValue());
        } else if (value instanceof Short) {
            rs.setShort(index, ((Short) value).shortValue());

        } else if (value instanceof Float) {
            rs.setFloat(index, ((Float) value).floatValue());
        } else if (value instanceof Blob) {
            rs.setBlob(index, ((Blob) value));
        } else if (value instanceof Clob) {
            rs.setClob(index, ((Clob) value));
        } else if (value instanceof InputStream) {
            InputStream is = (InputStream) value;
            rs.setAsciiStream(index, is, 100);
        } else if (value instanceof BigDecimal) {
            rs.setBigDecimal(index, (BigDecimal) value);
        } else {
            rs.setObject(index, value);
        }
    }

    private static void updateValue(RowSet rs, String columnName, Object value)
            throws SQLException {
        if (value == null) {
            rs.updateNull(columnName);
        } else if (value instanceof String) {
            rs.updateString(columnName, (String) value);
        } else if (value instanceof Long) {
            rs.updateLong(columnName, ((Long) value).longValue());
        } else if (value instanceof Integer) {
            rs.updateInt(columnName, ((Integer) value).intValue());
        } else if (value instanceof Double) {
            double dblValue = ((Double) value).doubleValue();
            rs.updateDouble(columnName, dblValue);
        } else if (value instanceof Date) {
            rs.updateDate(columnName, (Date) value);
        } else if (value instanceof Boolean) {
            rs.updateBoolean(columnName, ((Boolean) value).booleanValue());
        } else if (value instanceof Short) {
            rs.updateShort(columnName, ((Short) value).shortValue());
        } else if (value instanceof Time) {
            rs.updateTime(columnName, ((Time) value));
        } else if (value instanceof Timestamp) {
            rs.updateTimestamp(columnName, ((Timestamp) value));
        } else if (value instanceof Calendar) {
            Calendar cal = (Calendar) value;
            rs.updateTimestamp(columnName, (DateUtils.convertCalendarToTimestamp(cal)));
        } else if (value instanceof Float) {
            rs.updateFloat(columnName, ((Float) value).floatValue());
        } else if (value instanceof Blob) {
            rs.updateBlob(columnName, ((Blob) value));
        } else if (value instanceof Clob) {
            rs.updateClob(columnName, ((Clob) value));
        } else {
            rs.updateObject(columnName, value);
        }
    }

    private static Object getValue(RowSet rs, int index) throws SQLException {
        Object value = null;
        ResultSetMetaData metaData = rs.getMetaData();
        int type = metaData.getColumnType(index + 1);
        switch (type) {
            case Types.INTEGER:
            case Types.SMALLINT:
            case Types.TINYINT:
                value = new Integer(rs.getInt(index + 1));
                break;
            case Types.BIGINT:
                value = new Long(rs.getLong(index + 1));
                break;
            case Types.DOUBLE:
            case Types.FLOAT:
                value = new Double(rs.getDouble(index + 1));
                break;
            case Types.BIT:
                value = new Boolean(rs.getBoolean(index + 1));
                break;
            case Types.DATE:
                value = rs.getDate(index + 1);
                break;
            case Types.TIMESTAMP:
                value = DateUtils.convertTimestampToCalendar(rs.getTimestamp(index + 1));
                break;
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                value = rs.getString(index + 1);
                break;
            default:
                logger.warn("PreparedStatementHelper.getValueAt(): Unknown data type");
        }
        return value;
    }

    public static Object getValue(ResultSet rs, int index) throws SQLException {
        Object value = null;
        ResultSetMetaData metaData = rs.getMetaData();
        int type = metaData.getColumnType(index + 1);
        switch (type) {
            case Types.INTEGER:
            case Types.SMALLINT:
            case Types.TINYINT:
                value = new Integer(rs.getInt(index + 1));
                break;
            case Types.BIGINT:
                value = new Long(rs.getLong(index + 1));
                break;
            case Types.DOUBLE:
            case Types.FLOAT:
                value = new Double(rs.getDouble(index + 1));
                break;
            case Types.BIT:
                value = new Boolean(rs.getBoolean(index + 1));
                break;
            case Types.DATE:
                value = rs.getDate(index + 1);
                break;
            case Types.TIMESTAMP:
                value = DateUtils.convertTimestampToCalendar(rs.getTimestamp(index + 1));
                break;
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                value = rs.getString(index + 1);
                break;
            default:
                System.out.println("PreparedStatementHelper.getValueAt(): Unknown data type");
        }
        return value;
    }

    private static Object getValue(RowSet rs, String columnName, Class type)
            throws SQLException {
        Object value = null;
        if (String.class.equals(type)) {
            value = rs.getString(columnName);
        } else if (Long.class.equals(type) || long.class.equals(type)) {
            value = new Long(rs.getLong(columnName));
        } else if (Integer.class.equals(type) || int.class.equals(type)) {
            value = new Integer(rs.getInt(columnName));
        } else if (Float.class.equals(type) || float.class.equals(type)) {
            value = new Float(rs.getFloat(columnName));
        } else if (Double.class.equals(type) || double.class.equals(type)) {
            value = new Double(rs.getDouble(columnName));
        } else if (Calendar.class.equals(type)) {
            value = DateUtils.convertTimestampToCalendar(rs.getTimestamp(columnName));
        } else if (Date.class.equals(type)) {
            value = rs.getDate(columnName);
        } else {
            value = rs.getObject(columnName);
        }
        return value;
    }
}
