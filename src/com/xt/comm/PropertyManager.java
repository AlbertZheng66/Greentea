
package com.xt.comm;

import com.xt.core.dd.DynamicDeploy;
import com.xt.core.dd.Loadable;
import com.xt.core.log.LogWriter;
import com.xt.core.utils.BooleanUtils;
import com.xt.core.utils.DateUtils;
import com.xt.core.utils.IOHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author Albert
 */
public class PropertyManager {
     private final String perpertyFileName;
    
    private final Properties properites = new Properties();
    
    
    private final static Logger logger = Logger.getLogger(PropertyManager.class);

    public PropertyManager(String perpertyFileName) {
        this.perpertyFileName = perpertyFileName;
        load();
        File file = getFile();
        DynamicDeploy.getInstance().register(file.getAbsolutePath(), new Loadable() {

            public void load(String fileName) {
                PropertyManager.this.load();
            }
        });
    }
    
     /**
     * 读取文件的位置。
     *
     * @return
     */
    private File getFile() {
        File file = new File(perpertyFileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                LogWriter.warn2(logger, ex, "创建属性文件[%s]失败。", file.getAbsolutePath());
            }
        }
        return file;
    }

    private void load() {
        File file = getFile();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            properites.load(fis);
        } catch (Exception ex) {
            LogWriter.warn2(logger, ex, "属性文件[%s]不存在。", file.getAbsolutePath());
        } finally {
            IOHelper.closeSilently(fis);
        }
        LogWriter.info2(logger, "属性文件[%s]装载完毕。", file.getAbsolutePath());

    }
    
     private void save() {
        File file = getFile();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            properites.store(fos, String.format("属性文件，保存时间：%s。", DateUtils.toDateStr(Calendar.getInstance())));
        } catch (Exception ex) {
            LogWriter.warn2(logger, ex, "保存属性文件时出现错误。");
        } finally {
            IOHelper.closeSilently(fos);
        }
        LogWriter.info2(logger, "属性文件[%s]保存完毕。", file.getAbsolutePath());
    }

     /**
     * 读取参数名称。
     * @param name
     * @return
     */
    public String getString(String name) {
        assertName(name);
        return getString(name, null);
    }

    public String getString(String name, String defaultValue) {
        LogWriter.info2(logger, "读取属性[%s]。", name);
        assertName(name);
        String value = properites.getProperty(name);
        if (StringUtils.isEmpty(value)) {
            value = defaultValue;
        }
        return value;
    }

    public int getInt(String name, int defaultValue) {
        int value = defaultValue;
        String strValue = getString(name, String.valueOf(defaultValue));
        if (StringUtils.isNotEmpty(strValue)) {
            try {
                value = Integer.parseInt(strValue);
            } catch (NumberFormatException nfe) {
                throw new PropertyException(String.format("属性[%s]的值[%s]不能转换为整数。", name, strValue));
            }
        }
        return value;
    }
    
    public long getLong(String name, long defaultValue) {
        long value = defaultValue;
        String strValue = getString(name, String.valueOf(defaultValue));
        if (StringUtils.isNotEmpty(strValue)) {
            try {
                value = Long.parseLong(strValue);
            } catch (NumberFormatException nfe) {
                throw new PropertyException(String.format("属性[%s]的值[%s]不能转换为整数。", name, strValue));
            }
        }
        return value;
    }
    
    public double getDouble(String name, double defaultValue) {
        double value = defaultValue;
        String strValue = getString(name, String.valueOf(defaultValue));
        if (StringUtils.isNotEmpty(strValue)) {
            try {
                value = Double.parseDouble(strValue);
            } catch (NumberFormatException nfe) {
                throw new PropertyException(String.format("属性[%s]的值[%s]不能转换为双精度。", name, strValue));
            }
        }
        return value;
    }

    public boolean getBoolean(String name, boolean defaultValue) {
        boolean value = defaultValue;
        String strValue = getString(name, String.valueOf(defaultValue));
        if (StringUtils.isNotEmpty(strValue)) {
            value = BooleanUtils.isTrue(strValue, defaultValue);
        }
        return value;
    }

    /**
     * 移除指定的属性。
     * @param name
     * @return
     */
    public void remove(String name) {
        assertName(name);
        properites.remove(name);
        save();
    }
    
    /**
     * 移除指定的属性。
     * @param name
     * @return
     */
    public void clear(String name) {
        assertName(name);
        properites.put(name, "");
        save();
    }

    /**
     * 设置指定的属性。
     * @param name
     * @param value
     */
    public void setValue(String name, Object value) {
        LogWriter.info2(logger, "保存属性[%s=%s]。", name, value);
        assertName(name);
        String _value = (value == null ? "" : value.toString());
        properites.put(name, _value);
        save();
    }

    private void assertName(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new PropertyException("属性名称不能为空。");
        }
    }

}
