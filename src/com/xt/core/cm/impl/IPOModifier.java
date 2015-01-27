package com.xt.core.cm.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.NotFoundException;

import com.xt.core.cm.IClassModifier;
import com.xt.core.cm.NewMethod;
import com.xt.core.conv.Converter;
import com.xt.core.conv.ConverterFactory;
import com.xt.core.db.meta.Column;
import com.xt.core.db.meta.Database;
import com.xt.core.db.meta.Table;
import com.xt.core.db.po.IPO;
import com.xt.core.db.pm.IPersistence;
import com.xt.core.exception.BadParameterException;
import com.xt.core.map.DBMapping;
import com.xt.core.utils.ClassHelper;
import com.xt.core.utils.StringUtils;
import com.xt.core.db.po.mapping.SimpleDBMapping;
import org.apache.log4j.Logger;

/**
 * 
 * <p>
 * Title: XT框架-核心逻辑部分-IPO类更改器
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author 郑伟
 * @version 1.0
 * @date 2006-8-26
 */
public class IPOModifier implements IClassModifier {

    private final Logger logger = Logger.getLogger(IPOModifier.class);

    private static final Class DESTINATION = IPO.class;
    private Class modifiedClass;

    // 生成后的新PO名称的后缀
    public static final String IPO_SUBFIX = "_PO";

    // 映射文件(需要补充属性与字段的映射关系)
    private DBMapping mapping = new SimpleDBMapping();

    public IPOModifier() {
    }

    public Class getInterface() {
        return IPO.class;
    }

    public String getName(String modifiedClassName) {
        return modifiedClassName + IPO_SUBFIX;
    }

    public String[] getNewFields(CtClass modifiedClass)
            throws NotFoundException, CannotCompileException {
        String[] nfs = new String[3];

        nfs[0] = "protected java.util.Map _$status = new java.util.HashMap();";

        // 增加记录原始主键的值对
        // nfs[1] = "protected java.util.ArrayList _$oldPrimaryKeys = new java.util.ArrayList();";

        // 增加控制翻页的参数
        nfs[1] = "protected int _$fetchSize = " + IPO.DEFAULT_MAX_ITEMS_PER_PAGE + ";";
        nfs[2] = "protected int _$startIndex = 0;";

        return nfs;
    }

    public NewMethod[] getNewMethods(CtClass modifiedClass)
            throws NotFoundException, CannotCompileException {
        List<NewMethod> nms = new ArrayList<NewMethod>(20);
        // 增加一些基本方法
        nms.addAll(createBaseMethod());

        // 增加主键处理方法
        nms.add(createPrimaryKeys());

        // 增加Setter方法
        nms.addAll(modifySetter());

        // 装载装载方法
        nms.add(createLoad());

        //
        // nms.add(createGetOldPrimaryKeys());

        return (NewMethod[]) nms.toArray(new NewMethod[0]);
    }

    public boolean needModify(Class modifidClass) {
        return !modifidClass.equals(DESTINATION) && IPersistence.class.isAssignableFrom(modifidClass);
    }

    // add the getTableName method to modified class
    private List createBaseMethod() throws NotFoundException,
            CannotCompileException {
        List<NewMethod> nms = new ArrayList<NewMethod>();

        NewMethod gtn = new NewMethod();
        gtn.setName("__getTableName");
        gtn.setBody(String.format("return \"%s\";", mapping.getTableName(modifiedClass)));
        nms.add(gtn);

        // public Map getStatus ();
        NewMethod gs = new NewMethod();
        gs.setName("__getStatus");
        gs.setBody("return _$status ;");
        nms.add(gs);

        // public int getFetchSize();
        NewMethod gtpp = new NewMethod();
        gtpp.setName("getFetchSize");
        gtpp.setBody("return _$fetchSize;");
        nms.add(gtpp);

        // public void setFetchSize(int fetchSize);
        NewMethod stpp = new NewMethod();
        stpp.setName("setFetchSize");
        stpp.setBody("this._$fetchSize = $1 ;");
        nms.add(stpp);

        // public int getStartIndex ();
        NewMethod gpn = new NewMethod();
        gpn.setName("getStartIndex");
        gpn.setBody("return _$startIndex ;");
        nms.add(gpn);

        // public void setStartIndex (int startIndex);
        NewMethod spn = new NewMethod();
        spn.setName("setStartIndex");
        spn.setBody("this._$startIndex = $1 ;");
        nms.add(spn);

        // public Class getEntityClass();
        NewMethod gec = new NewMethod();
        gec.setName("__getEntityClass");
        gec.setBody(String.format("return %s.class ;", modifiedClass.getName()));
        nms.add(gec);

        return nms;
    }

    // add the getTableName method to modified class
    private NewMethod createPrimaryKeys() throws NotFoundException,
            CannotCompileException {
        NewMethod nm = new NewMethod();
        nm.setName("__getPrimaryKeys");

        // 得到与一个类相关的主键
        List pks = getPrimaryKeys(modifiedClass);

        StringBuffer body = new StringBuffer("String[] pks = new String[").append(pks.size()).append("];");
        int index = 0;
        for (Iterator iter = pks.iterator(); iter.hasNext();) {
            String columnName = (String) iter.next();
            body.append("pks[").append(index).append("] = \"").append(
                    columnName).append("\";");
            index++;
        }
        body.append("return pks;");

        nm.setBody(body.toString());
        return nm;
    }

    /**
     * 返回所有的主键
     *
     * @param modifiedClass
     * @return
     */
    private List<String> getPrimaryKeys(Class modifiedClass) {
        List<String> listPks = new ArrayList<String>(1);
        String tableName = mapping.getTableName(modifiedClass);
        Table table = Database.getInstance().find(null, tableName);
        if (table == null) {
            throw new BadParameterException(String.format("不能根据表名称[%s]找到对应的数据库表", tableName));
        }
        Column[] pks = table.getPks();
        if (pks != null) {
            for (int i = 0; i < pks.length; i++) {
                Column pk = pks[i];
                if (pk == null || pk.getName() == null) {
                    throw new BadParameterException("主键或者主键的名称不能为空");
                }
                listPks.add(pk.getName());
            }
        }
        return listPks;
    }

    private List modifySetter() throws CannotCompileException,
            NotFoundException {
        List<NewMethod> nms = new ArrayList<NewMethod>();
        String[] propertyNames = mapping.getPropertyNames(modifiedClass);
        for (int i = 0; i < propertyNames.length; i++) {
            NewMethod nm = new NewMethod();

            String propertyName = propertyNames[i];
            String setter = "set" + StringUtils.capitalize(propertyName);
            StringBuffer body = new StringBuffer();
            String columnName = mapping.getColumnName(modifiedClass,
                    propertyName);

            Method method = ClassHelper.getGetterMethod(modifiedClass,
                    propertyName);
            body.append("_$status.put(\"" + columnName + "\", ($w)$1);");
            //body.append("System.out.println(\"columnName=" + columnName + "; value=\" + $1);");
            body.append("super.").append(setter).append("($1);");

            nm.setName(setter);
            nm.setBody(body.toString());

            Class[] paramsType = new Class[1];
            Class fieldType = method.getReturnType();
            paramsType[0] = fieldType;
            nm.setByInterface(false);
            nm.setParamsType(paramsType);
            nms.add(nm);
        }
        return nms;

    }

    private NewMethod createLoad() throws NotFoundException,
            CannotCompileException {
        NewMethod nm = new NewMethod();
        nm.setName("__load");

        StringBuffer body = new StringBuffer();
        // 读出所有的需要映射的属性名称
        String[] fieldNames = mapping.getPropertyNames(modifiedClass);
        body.append("Object __obj = null;");
        for (int i = 0; i < fieldNames.length; i++) {
            // 属性名称
            String fieldName = fieldNames[i];

            // 得到属性的Getter方法
            Method method = ClassHelper.getGetterMethod(modifiedClass,
                    fieldName);

            // 属性的类型
            Class fieldType = method.getReturnType();

            // 数据库的列值
            String columnName = mapping.getColumnName(modifiedClass, fieldName);

            /*******************************************************************
             * 组建型如如下表达式的语句: <br />
             * 可识别的类型： <br />
             * <code>
             * Type _res = null;
             * _res = ($w)$1.getInt("columnName");
             * super.setInt1(_res);
             * </code>
             * 不可识别的类型：<br />
             * <code>
             * Type _res = null;
             * Object _obj = res.getObject("columnName");
             * // 处理 BLOB 和 CLOB 字段
             *
             * if (_obj != null) {
             *     Class _sourceClass = _obj.getClass();
             *     Class _targetClass = fieldType.getClass();
             *     Converter _conv = ConverterFactory.getInstance().getConverter(_sourceClass, _targetClass);
             *     _res = ($w)_conv.convert(__sourceClass, __targetClass, _obj);
             * }
             * super.setInt1(_res); <br />
             * </code>
             ******************************************************************/
            String resultSetType = getResultSetType(fieldType);

            // 作为变量结果的名称
            String resName = new StringBuffer(" __").append(fieldName).append("Res").toString();
            if ((resultSetType == null || fieldType.isEnum())) {
                // 不可识别，需要转换的类型
                body.append(getTypeName(fieldType)).append(resName);
                // 首先需要初始化（如果值未初始化，javassist 将产生运行异常。）
                if (fieldType.isPrimitive()) {
                    body.append(" = ").append(ClassHelper.getPrimitiveInitialValue(fieldType).toString());
                } else {
                    body.append(" = null");
                }
                body.append(";");
                body.append("__obj = $1.getObject(\"").append(columnName).append("\"); ");
                body.append("if (__obj != null) { ");
                body.append("    Class __sourceClass = __obj.getClass();");
//                body.append("    System.out.println(\"__sourceClass=\" + __sourceClass);");
                body.append("    Class __targetClass = ").append(getTypeName(fieldType)).append(".class;");
//                body.append("    System.out.println(\"__targetClass=\" + __targetClass);");
                body.append("    ").append(Converter.class.getName());
                body.append("       __conv = ").append(ConverterFactory.class.getName());
                body.append(".getInstance().getConverter(__sourceClass, __targetClass);");
//                body.append("    System.out.println(\"__conv=\" + __conv.toString());");
                body.append("    if (__conv == null) ");
                body.append("        throw new com.xt.core.exception.POException(\"未定义源类型[\" + __sourceClass + \"]到目标类型[\" + __targetClass + \"]的转换。\");");
                if (fieldType.isPrimitive()) {
                    // 原始类型需要解包装
                    String className = ClassHelper.getWrapper(fieldType).getName();
                    body.append(className).append(" wrapper = (").append(className).append(")__conv.convert(__sourceClass, __targetClass, __obj);");
                    body.append(resName).append(" = wrapper.").append(getTypeName(fieldType)).append("Value();");
                } else {
                    body.append(resName).append(" = (").append(getTypeName(fieldType)).append(")__conv.convert(__sourceClass, __targetClass, __obj);");
                }
                body.append("}");  // end of 'if (__obj != null) { '
            } else {
                // 可识别的类型(多数属于简单类型)
                body.append(getTypeName(fieldType));
                body.append(resName).append(" = ");

                // 处理 Blob 和 Clob 字段
                // Blob 和 Clob 的查询处理同一用Converter进行转换
                /*if ("Blob".equals(resultSetType) || "Clob".equals(resultSetType)) {
                // 调用SqlUtils.readBlob 和 readClob 进行转换。
                body.append("(").append(getTypeName(fieldType)).append(")");
                body.append(SqlUtils.class.getName()).append(".read").append(resultSetType).append("(").append("$1.get").append(resultSetType).append("(\"").append(columnName).append("\")").append(", ").append(getTypeName(fieldType)).append(".class)");
                } else*/ {
                    if (!fieldType.isPrimitive()) {
                        body.append("($w)");
                    }
                    body.append("($1.get").append(resultSetType).append("(\"").append(columnName).append("\"));");
                }
                // System.out.println("body" + body.toString());
            }

            body.append(" ((").append(modifiedClass.getName()).append(")$2).set").append(StringUtils.capitalize(fieldName)).append("(").append(resName).append("); \n");
        }

//		// 记录原始主键
//		List pks = getPrimaryKeys(modifiedClass);
//		for (int i = 0; i < pks.size(); i++) {
//			// 属性名称
//			String fieldName = (String) pks.get(i);
//			body
//					.append("com.xt.core.utils.Pair __p = new com.xt.core.utils.Pair();");
//			body.append("__p.setName(\"").append(fieldName).append("\");");
//			body.append("__p.setValue(").append("$1.getObject(\"").append(
//					fieldName).append("\"));");
//			body.append("_$oldPrimaryKeys.add(__p);");
//
//		}

        nm.setBody(body.toString());
        return nm;
    }

    /**
     * 返回类型的名称，此方法主要是对数组类型进行了处理（目前只处理byte[]）。
     * @param clazz
     * @return
     */
    private String getTypeName (Class clazz) {
        if (clazz == null) {
            return null;
        }
        if (clazz.isArray()) {
            if (clazz == byte[].class) {
                return "byte[]";
            }
        }
        return clazz.getName();
    }

//	private NewMethod createGetOldPrimaryKeys() throws NotFoundException,
//			CannotCompileException {
//		NewMethod nm = new NewMethod();
//		nm.setName("getOldPrimaryKeys");
//		nm.setBody("return _$oldPrimaryKeys;");
//		return nm;
//	}
    /**
     * 根据不同的属性类型，使用不同的ResultSet的getter方法。
     *
     * @param fieldType
     *            属性类型
     * @return
     * @throws NotFoundException
     */
    private String getResultSetType(Class fieldType) throws NotFoundException {
        String type = null;
        if (String.class == fieldType) {
            type = "String";
        } else if (int.class == fieldType || Integer.class == fieldType) {
            type = "Int";
        } else if (long.class == fieldType || Long.class == fieldType) {
            type = "Long";
        } else if (float.class == fieldType || Float.class == fieldType) {
            type = "Float";
        } else if (double.class == fieldType || Double.class == fieldType) {
            type = "Double";
//        } else if (InputStream.class == fieldType || fieldType == byte[].class /*|| Blob.class.isAssignableFrom(fieldType)*/) {
//            type = "Blob";
//		} else if (BigDecimal.class == fieldType) {
//			type = "BigDecimal";
//        } else if (Reader.class == fieldType || fieldType == char[].class /*|| Clob.class.isAssignableFrom(fieldType)*/) {
//            type = "Clob";
        }
        return type;
    }

    public void init(Class modifiedClass) {
        this.modifiedClass = modifiedClass;
    }
}
