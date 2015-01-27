package com.xt.gt.ui.table;

import com.xt.core.log.LogWriter;
import com.xt.core.utils.ClassHelper;
import com.xt.core.utils.CollectionUtils;
import com.xt.gt.ui.UIException;
import com.xt.gt.ui.dic.DictionaryDecl;
import com.xt.gt.ui.dic.DictionaryInfo;
import com.xt.gt.ui.table.editor.NullTableCellEditor;
import com.xt.gt.ui.table.renderer.NullTableCellRenderer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * 本类封装了从类里体现字段信息(ColumnInfo)的相关的辅助方法。
 *
 * @author albert
 */
public class ColumnInfoHelper {

    private static final Logger logger = Logger.getLogger(ColumnInfoHelper.class);

    static public List<TableColumnInfo> createColumnsFromProperties(Class bindingClass, String[] includeProperties,
            String[] excludeProperties) {
        return createColumnsFromProperties(bindingClass, includeProperties, excludeProperties, true);
    }

    static public List<TableColumnInfo> createColumnsFromProperties(Class bindingClass, String[] includeProperties,
            String[] excludeProperties, boolean recursive) {
        Map<String, Field> nameTitles = new LinkedHashMap();
        String[] propertyNames = ClassHelper.getPropertyNames(bindingClass, recursive);
        for (int i = 0; i < propertyNames.length; i++) {
            String propertyName = propertyNames[i];
            // 看一下属性是否显示
            if (isDisplay(includeProperties, excludeProperties, propertyName)) {
                Field field = ClassHelper.getField(bindingClass, propertyName, recursive);
                if (field != null) {
                    nameTitles.put(propertyName, field);
                } else {
                    LogWriter.warn(logger, String.format("未获取到类[%s]的属性[%s]。", bindingClass, propertyName));
                }
            }
        }
        return createCustomizedColumns(nameTitles);
    }

    static public List<TableColumnInfo> createCustomizedColumns(Map<String, Field> nameTitles) {
        List<TableColumnInfo> columnInfos = new ArrayList();
        if (nameTitles != null && !nameTitles.isEmpty()) {
            int seq = 0;
            for (Map.Entry<String, Field> entry : nameTitles.entrySet()) {
                TableColumnInfo tci = new TableColumnInfo();
                Field field = entry.getValue();
                tci.setPropertyName(entry.getKey());
                tci.setTitle(field.getName());
                tci.setPropertyClass(field.getType());
                tci.setPropertyClassName(field.getType().getName());
                tci.setSeq(seq++);
                columnInfos.add(tci);
            }
        }
        return columnInfos;
    }

    static public List<TableColumnInfo> createColumnsFromAnnotation(Class bindingClass, String[] includeProperties,
            String[] excludeProperties) {
        return createColumnsFromAnnotation(bindingClass, includeProperties, excludeProperties, false);
    }

    static public List<TableColumnInfo> createColumnsFromAnnotation(Class bindingClass, String[] includeProperties,
            String[] excludeProperties, boolean recursive) {
        List<TableColumnInfo> columnInfos = createColumnsFromAnnotation(bindingClass, null, includeProperties, excludeProperties);
        if (recursive) {
            Class currentClass = bindingClass.getSuperclass();
            while (currentClass != null && currentClass != Object.class) {
                List<TableColumnInfo> temp = createColumnsFromAnnotation(currentClass, null, includeProperties, excludeProperties);
                for (TableColumnInfo tci : temp) {
                    if (!columnInfos.contains(tci)) {
                        columnInfos.add(tci);
                    }
                }
                currentClass = currentClass.getSuperclass();
            }
        }
        Collections.sort(columnInfos);
        return columnInfos;
    }

    static private List<TableColumnInfo> createColumnsFromAnnotation(Class clazz, String prefix,
            String[] includeProperties, String[] excludeProperties) {
        Field[] fields = clazz.getDeclaredFields();
        if (fields == null || fields.length == 0) {
            return Collections.emptyList();
        }
        List<TableColumnInfo> columnInfos = new ArrayList();
        for (Field field : fields) {
            ColumnInfo columnInfo = field.getAnnotation(ColumnInfo.class);
            if (columnInfo == null || columnInfo.disable()) {
                continue;
            }

            String propertyName = field.getName();
            // 看一下属性是否显示
            if (!isDisplay(includeProperties, excludeProperties, propertyName)) {
                continue;
            }

            //  处理嵌套类的情况(即,处理在当前属性的类型中定义的标注)
            if (columnInfo.nested()) {
                Class propertyType = ClassHelper.getFieldType(clazz, propertyName);
                List<TableColumnInfo> _columns = createColumnsFromAnnotation(propertyType, propertyName, includeProperties, excludeProperties);
                columnInfos.addAll(_columns);
                continue;
            }

            TableColumnInfo tableColumnInfo = new TableColumnInfo();
            // 装置字典信息
            DictionaryDecl dictionaryDecl = getDictionaryAnnotation(clazz, propertyName);
            if (dictionaryDecl != null) {
                DictionaryInfo dictionaryInfo = new DictionaryInfo();
                dictionaryInfo.load(dictionaryDecl);
                tableColumnInfo.setDictionaryInfo(dictionaryInfo);
            }
            // 加上前缀（对于嵌套属性的情况）
            tableColumnInfo.setPropertyName(prefix == null ? propertyName : prefix + "." + propertyName);
            tableColumnInfo.setPropertyClass(field.getType());
            tableColumnInfo.setPropertyClassName(field.getType().getName());
            tableColumnInfo.setTitle(StringUtils.isEmpty(columnInfo.title()) ? propertyName : columnInfo.title());
            tableColumnInfo.setAlignment(columnInfo.alignment());
            tableColumnInfo.setDisable(columnInfo.disable());
            tableColumnInfo.setReadonly(columnInfo.readonly());
            tableColumnInfo.setSeq(columnInfo.seq());
            tableColumnInfo.setVisiable(columnInfo.visible());
            tableColumnInfo.setWidth(columnInfo.width());
            if (columnInfo.cellEditor() != NullTableCellEditor.class) {
                TableCellEditor tableCellEditor = ClassHelper.newInstance(columnInfo.cellEditor());
                tableColumnInfo.setCellEditor(tableCellEditor);
            }

            if (columnInfo.cellRenderer() != NullTableCellRenderer.class) {
                TableCellRenderer tableCellRenderer = ClassHelper.newInstance(columnInfo.cellRenderer());
                tableColumnInfo.setCellRenderer(tableCellRenderer);
            }
            columnInfos.add(tableColumnInfo);
        }
        LogWriter.debug(logger, "createColumnsFromAnnotation", columnInfos);
        return columnInfos;
    }

    /**
     * 返回定义于类的指定属性的字典标注，如果未定义，返回空。
     *
     * @param propertyClazz 类
     * @param propertyName 指定搜属性
     * @return
     */
    private static DictionaryDecl getDictionaryAnnotation(Class clazz, String propertyName) {
        Field field = ClassHelper.getField(clazz, propertyName, true);
        if (field == null) {
            throw new UIException(String.format("读取类[%s]的属性[%s]时失败。", clazz, propertyName));
        }
        DictionaryDecl dicAnno = field.getAnnotation(DictionaryDecl.class);
        return dicAnno;
    }

    static private boolean isDisplay(String[] includeProperties, String[] excludeProperties, String propertyName) {
        // 包含属性不为空，如果属性不存在于包含属性中，这不显示此属性
        if (includeProperties != null && includeProperties.length > 0
                && CollectionUtils.indexOf(includeProperties, propertyName) < 0) {
            return false;
        }
        // 包含属性不为空，如果属性不存在于包含属性中，这不显示此属性
        if (excludeProperties != null && excludeProperties.length > 0 && CollectionUtils.indexOf(excludeProperties, propertyName) >= 0) {
            return false;
        }
        return true;
    }
}
