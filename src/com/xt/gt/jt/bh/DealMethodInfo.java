package com.xt.gt.jt.bh;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xt.gt.sys.SystemConfigurationException;

/**
 * 处理方法的元信息类。
 * <p>Title: XT框架-业务逻辑部分</p>
 * <p>Description: 这个类存储了业务逻辑类的一个方法的信息，
 * 在第一次访问时，框架将自动填充这个信息。 </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-6-22
 */
public class DealMethodInfo implements Serializable {

    /**
     * 用户自定义,可以传入一个或者多个自定义参数
     */
    public final static int CUSTOMIZED_INPUT_PARAM = 0;
    /**
     * 通过方法的输入参数传入参数（目前限定方法只能有一个参数）
     */
    public final static int METHOD_INPUT_PARAM = 1;
    /**
     * 通过类的属性注入参数
     */
    public final static int CLASS_PROPERTIES = 2;
    /**
     * 通过方法的输入参数传入（方法无参数）
     */
    public final static int METHOD_INPUT_PARAM_NULL = 4;
    private static final long serialVersionUID = 1913300415572658253L;
    /**
     * 处理方法的名称，指HTTP请求参数的传入的方法名称
     */
    private String name;
    /**
     * 具体的处理方法，业务类的方法名称
     */
    private String method;
    /**
     * 参数的类型（在参数类型为METHOD_INPUT_PARAM是起作用，其他情况不起作用）
     */
    private Class paramClazz;
    /**
     * 参数的传入方式,默认为由方法输入参数
     */
    private int paramType = METHOD_INPUT_PARAM;
    
    /**
     * 表示此方法无须业务处理
     */
    private boolean isNop = false;
    /**
     * 所有的参数名称及其类型的组合,以参数的名称作为主键,对应的类型作为键值
     * （在参数类型为CUSTOMIZED_INPUT_PARAM是起作用，存放所有的参数值）。
     * 自定义参数只能是简单类型。
     * 一般情况下，参数不会太多，所以初始化为2。
     */
    private List<MethodParamInfo> paramInfos = new ArrayList<MethodParamInfo>(2);
    private Map<String, MethodParamInfo> paramInfoMap = new HashMap<String, MethodParamInfo>();

    public DealMethodInfo() {
    }

    public void appendParam(String name, int scope, String type, Class collectionClass) {
        if (paramInfoMap.containsKey(name)) {
            throw new SystemConfigurationException("参数的名称[" + name + "]重复！");
        }
        MethodParamInfo mpi = new MethodParamInfo();
        mpi.setName(name);
        mpi.setScope(scope);
        mpi.setType(type);
        mpi.setCollectionClass(collectionClass);
        paramInfos.add(mpi);
        paramInfoMap.put(name, mpi);
    }

    public List getParamInfos() {
        return paramInfos;
    }

    public MethodParamInfo getParamInfo(String name) {
        return paramInfoMap.get(name);
    }

    public void setParamNames(List paramNames) {
        this.paramInfos = paramNames;
    }

    public int getParamType() {
        return paramType;
    }

    public void setParamType(int inputParams) {
        this.paramType = inputParams;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class getParamClazz() {
        return paramClazz;
    }

    public void setParamClazz(Class paramClazz) {
        this.paramClazz = paramClazz;
    }

    @Override
    public String toString() {
        StringBuilder strld = new StringBuilder("[");
        strld.append("name=").append(name).append(";");
        strld.append("method=").append(method).append(";");
        strld.append("paramClazz=").append(paramClazz).append(";");
        strld.append("inputParams=").append(paramType).append(";");
        strld.append("]");
        return strld.toString();
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public boolean isNop() {
        return isNop;
    }

    public void setNop(boolean isNop) {
        this.isNop = isNop;
    }
}
