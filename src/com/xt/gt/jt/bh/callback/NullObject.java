package com.xt.gt.jt.bh.callback;

import java.io.Serializable;

/**
 * 代表一个空对象（null），避免在网络上传递null指针，引起歧义。
 * @author zw
 *
 */
public class NullObject implements Serializable{

    public NullObject() {
    }
    
}
