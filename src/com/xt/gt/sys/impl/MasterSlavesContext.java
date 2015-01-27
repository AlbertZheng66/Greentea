

package com.xt.gt.sys.impl;

import com.xt.core.db.conn.DatabaseContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author albert
 */
public class MasterSlavesContext {
    
    /**
     * 定义主备数据库的参数名称（可以通过SystemConfigration.readObject(Constants.MASTERS_SLAVES_NAME)获取）。
     */
    public final static String MASTERS_SLAVES_PARAM_NAME = "db_service_masters_slaves";
    
    /**
     * 多个主库（Cluster）的情况
     */
    private final List<DatabaseContext> masters = new ArrayList() ;

    /**
     * 多个从库
     */
    private final List<DatabaseContext> slaves = new ArrayList();

    public MasterSlavesContext() {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MasterSlavesContext other = (MasterSlavesContext) obj;
        if (this.masters != other.masters && (this.masters == null || !this.masters.equals(other.masters))) {
            return false;
        }
        if (this.slaves != other.slaves && (this.slaves == null || !this.slaves.equals(other.slaves))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.masters.hashCode();
        hash = 97 * hash + this.slaves.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder strBld = new StringBuilder();
        strBld.append(super.toString()).append("[");
        strBld.append("masters=").append(masters).append("; ");
        strBld.append("slaves=").append(slaves);
        strBld.append("]");
        return strBld.toString();
    }



    /**
     * 返回所有的主库上下文，注意：返回的主库列表不可修改。
     * @return 当前所有主库实例的列表。
     */
    public List<DatabaseContext> getMasters() {
        return Collections.unmodifiableList(masters);
    }

    /**
     * 增加一个主库实例，如果参数为空，则不进行任何处理。
     * @param master 主库上下文的实例
     */
    synchronized public void addMaster(DatabaseContext master) {
        if (master != null) {
            this.masters.add(master);
        }
    }

    /**
     * 返回所有的从库上下文，注意：返回的从库列表不可修改。
     * @return 当前所有从库实例的列表。
     */
    public List<DatabaseContext> getSlaves() {
        return Collections.unmodifiableList(slaves);
    }

    /**
     * 增加一个从库实例，如果参数为空，则不进行任何处理。
     * @param slave 从库上下文的实例
     */
    synchronized public void addSlave(DatabaseContext slave) {
        if (slave != null) {
            this.slaves.add(slave);
        }
    }    
}
