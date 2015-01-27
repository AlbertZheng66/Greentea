
package com.xt.core.json.parse;

/**
 * 参数解析接口，开发人员可通过此接口自行处理JSON对象的解析。
 * @author albert
 */
public interface Parsable {

    public Object parse (Object obj);
}
