package com.xt.core.db.conn;



/**
 * <p>
 * Title: GreeTea 框架。
 * </p>
 * <p>
 * Description:存储数据库连接或者是数据库缓冲池的配置文件信息。
 * 属性文件的必须存放在 编译后的类路径下，即：目录与此类编译后在同一路径下。
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author 郑伟
 * @version 1.0
 */

public class DatabaseContext {
	/**
	 * 与数据库的连接的形式是：通过ODBC连接
	 */
	public final static String ODBC = "odbc";

	/**
	 * 与数据库的连接的形式是：通过JDBC连接
	 */
	public final static String JDBC = "jdbc";

	/**
	 * 与数据库的连接的形式是：通过数据源连接
	 */
	public final static String DATA_SOURCE = "dataSource";

	/**
	 * 唯一标识数据库标志
	 */
	private String id;

	/**
	 * 与数据库的连接的类型ODBC或JDBC连接
	 */
	private String type;

	/**
	 * 数据库的ODBC名称，或者是数据源名称
	 */
	private String databaseName;

	/**
	 * 用户名称
	 */
	private String userName;

	/**
	 * 口令
	 */
	private String password;

	/**
	 * 数据库服务器的地
	 */
	private String url;

	/**
	 * 数据库服务器的端口号
	 */
	private String port;

	/**
	 * 编码格式
	 */
	private String encoding;

	/**
	 * 在有些数据库中（HSQL），schema和用户有可能是不同的
	 */
	private String schema;

	/**
	 * 数据源的前缀
	 */
	private String prefix;

	/**
	 * 数据库驱动程序的类名
	 */
	private String driverClass;


	/**
	 * 对数据库的描述信息.
	 */
	private String description;

	public DatabaseContext() {
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public String getEncoding() {
		return encoding;
	}

	public String getPassword() {
		return password;
	}

	public String getPort() {
		return port;
	}

	public String getUrl() {
		return url;
	}

	public String getUserName() {
		return userName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public String getDescription() {
		return description;
	}

	public String getId() {
		return id;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DatabaseContext other = (DatabaseContext) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder strBld = new StringBuilder();
        strBld.append(super.toString()).append("[");
        strBld.append("id=").append(id).append("; ");
        strBld.append("type=").append(type).append("; ");
        strBld.append("databaseName=").append(databaseName).append("; ");
        strBld.append("userName=").append(userName).append("; ");
        strBld.append("url=").append(url).append("; ");
        strBld.append("schema=").append(schema).append("; ");
        strBld.append("driverClass=").append(driverClass);
        strBld.append("]");
        return strBld.toString();
    }

}
