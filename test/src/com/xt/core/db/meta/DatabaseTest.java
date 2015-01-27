

package com.xt.core.db.meta;

import com.xt.core.db.conn.DatabaseContext;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import junit.framework.TestCase;

/**
 *
 * @author albert
 */
public class DatabaseTest extends TestCase {
    
    public DatabaseTest(String testName) {
        super(testName);
    }

    public void testGetInstance() {
    }

    public void testGetTableNames() {
    }

    public void testFind() {
    }

    public void testLoad() {
        try {
            // MySQL 测试
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection con = java.sql.DriverManager.getConnection("jdbc:mysql://localhost/b_cloud", "b_cloud", "b_cloud");
            Statement stmt = con.createStatement();
            ResultSet rst = stmt.executeQuery("select * from server_info");
            while (rst.next()) {
                System.out.println("2222221=" + rst.getString(1));
            }

            // 测试装载元数据
            DatabaseContext dc = new DatabaseContext();
            dc.setId("test");
            dc.setUserName("b_cloud");
            dc.setPassword("b_cloud");
            dc.setDriverClass("com.mysql.jdbc.Driver");
            dc.setUrl("jdbc:mysql://localhost/b_cloud");
            dc.setType(DatabaseContext.JDBC);
            Database.getInstance().load(dc);
            System.out.println("db=" + Database.getInstance());
            Table table = Database.getInstance().find(null, "SERVER_INFO");
            //关闭连接、释放资源
            rst.close();
            stmt.close();
            con.close();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            System.out.println("finally");
        }
    }

    public void testIsLoaded() {
    }

}
