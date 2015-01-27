

package com.xt.core.json.parse;


import com.xt.core.exception.SystemException;
import com.xt.core.json.parse.impl.EnumParser;
import com.xt.core.json.parse.impl.FspParameterParser;
import com.xt.gt.ui.fsp.FspParameter;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author albert
 */
public class ParserFactory {
    private final static ParserFactory instance = new ParserFactory();

    private Map<String, Parsable> parsables = new HashMap();

    static {
        // 默认的测试
        instance.register(FspParameter.class.getName(), new FspParameterParser());
        instance.register(Enum.class.getName(), new EnumParser());

        //TODO: 需要从配置文件中读取配置
    }

    private ParserFactory() {

    }

    public static ParserFactory getInstance () {
        return instance;
    }

    synchronized public void register (String className, Parsable parsable) {
        if (className == null || parsable == null) {
            throw new SystemException(String.format("参数 className[%s]和parsable[%s]都不能为空。", className, parsable));
        }
        parsables.put(className, parsable);
    }

    /**
     * 返回类名称对应的解析接口对象。如果类名为空，或者为未注册相应的对象，则返回空。
     * 此方法为类型安全。
     * @param className 类名称
     * @return 解析接口对象。
     */
    synchronized public Parsable getParsable (String className) {
        if (className == null) {
            return null;
        }
        return parsables.get(className);
    }
}
