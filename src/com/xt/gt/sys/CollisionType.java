
package com.xt.gt.sys;

/**
 *  参数产生冲突之后(从多个配置文件读取到相同的参数)应该如何处理.
 * @author albert
 */
public enum CollisionType {

    /**
     * 将所有的参数合并到一起("list" 和 "map" 类型的参数的默认行为)，简单参数不能定义为这种类型。
     */
    MERGE,

    /**
     * 后面的参数将覆盖前面的参数(简单类型的参数的默认行为)
     */
    REPLACEMENT,

    /**
     * 后面的参数将被丢弃。
     */
    IGNORED
}
