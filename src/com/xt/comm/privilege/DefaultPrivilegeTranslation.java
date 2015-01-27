package com.xt.comm.privilege;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.xt.core.log.LogWriter;

/**
 * <p>Title: </p>
 * <p>Description:缺省的权限转换器 </p>
 * <p>Copyright: Copyright (c) 2003年5月</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class DefaultPrivilegeTranslation
    implements PrivilegeTranslation
{
    /**
     * 用户请求与权限对照表
     */
    private HashMap privilegeMap;

    /**
     * 用户请求的handler参数
     */
    private static final String HANDLER = "handler";

    /**
     * 用户请求的command参数
     */
    private static final String COMMAND = "command";

    public DefaultPrivilegeTranslation ()
    {
        LogWriter.debug("DefaultPurviewTranslation.............");
    }

    /**
     * 分隔用户请求中的各个参数的分隔符（使用尽量不要在链接的handler和command参
     * 数中使用的符号）。
     */
    private static final String DELIMETER = "~";

    public void init (Map initValue)
    {
        LogWriter.debug("DefaultPurviewTranslation...initPurviewMap..........");
    }

    /**
     * 初始化请求与权限对照表
     */
    private void initPrivilegeHashMap ()
    {
        LogWriter.debug("DefaultPurviewTranslation...initPrivilegeHashMap......");
        privilegeMap = new HashMap(100);
        //将所有的链接与权限对照放入对照表中
        //String jndiName = "privilege.query_all_privilege";
//        try
//        {
//            //String sql = DefaultSQLFinder.newInstance().lookup(jndiName);
//            String sql = "SELECT f_privilegeid, f_url, f_handler, f_command "
//                         + " FROM fa_privileges";
//            LogWriter.println("DefaultPurviewTranslation.initPurviewMap.sql=" + sql);
//            LhdDatabaseFactory.newInstance().excuteQuery(sql, this);
//        }
//        catch (LhdError ex)
//        {
//            LogWriter.println("DefaultPurviewTranslation...initPurviewMap..LhdError="
//                          + ex.getSystemMessage());
//        }
    }

    /**
     * 根据用户请求形成权限在权限Hash表中的“名称”，此命称由链接和参数累加而成，
     * 中间由分隔符分隔（名称与权限应该是一一对应的）名称的组成的第一部分是
     * servletPath，然后是分隔符，第一个参数，分隔符，第二个参数。
     * @param request 用户请求
     * @return 权限在权限Hash表中的名称。
     */
    private String getPrivilegeName (HttpServletRequest request)
    {
        StringBuffer url = new StringBuffer();
        //名称的组成的第一部分是servletPath，然后是分隔符；第一个参数，分隔符，第二个参数
        url.append(request.getServletPath()).append(DELIMETER).append(request.getParameter(HANDLER)).
            append(DELIMETER).append(request.getParameter(COMMAND));
        LogWriter.debug("DefaultPurviewTranslation.getPrivilegeName.privilegeName=" + url.toString());
        return url.toString();
    }

    public void reset ()
    {
        //迫使系统重新进行初始化，重新检索数据库
        privilegeMap = null;
    }

    /**
     * 根据用户输入的请求得到此请求对应的权限
     * @param request 用户请求
     * @return 请求对应的权限,如果不存在，则返回空（null）
     */
    public Privilege getPrivilege (HttpServletRequest request)

    {
        //第一次进行请求与权限转换时调用此函数
        if (privilegeMap == null)
        {
            initPrivilegeHashMap();
        }
        String privilegeName = getPrivilegeName(request);
        LogWriter.debug("DefaultPurviewTranslation.getPrivilege.privilegeName=" + privilegeName);
        Iterator it = privilegeMap.keySet().iterator();
        return (Privilege)privilegeMap.get(privilegeName);
    }

    public void createResultSet (ResultSet rs)
        throws SQLException
    {
        LogWriter.debug("DefaultPurviewTranslation.createObject................");
        StringBuffer url = new StringBuffer();
        while (rs.next())
        {
            url.delete(0, url.length());
            String privilegeId = rs.getString("f_privilegeid");
            LogWriter.debug("DefaultPurviewTranslation.createObject.privilegeId=" + privilegeId);
            url.append(rs.getString("f_url")).append(DELIMETER).append(rs.getString("f_handler")).
                append(DELIMETER).append(rs.getString("f_command"));
            LogWriter.debug("DefaultPurviewTranslation.createObject.url=" + url);
//            privilegeMap.put(url.toString(), new DefaultPrivilege (privilegeId));
        }
    }
}
