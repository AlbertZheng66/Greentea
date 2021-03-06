/* Generated by Together */

package com.xt.comm.privilege;

public class Privilege
{

    public Privilege ()
    {

    }

    public Privilege (String id)
    {
        this.id = id;
    }

    /**
     * 权限编码
     */
    protected String id;

    /**
     * 判断两个权限是否相等
     * @param privilege 权限
     * @return 相等返回真，否则返回假，传入的权限为空时返回假。
     */
    public boolean equals (Privilege privilege)
    {
        return id.equals(privilege.getId());
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }
}
