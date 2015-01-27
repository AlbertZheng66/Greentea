package com.xt.gt.sys;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Element;

import com.xt.core.dd.Loadable;
import com.xt.core.exception.BadParameterException;
import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import com.xt.core.utils.CollectionUtils;
import com.xt.core.utils.BooleanUtils;
import com.xt.core.utils.ClassHelper;
import com.xt.core.utils.RegExpUtils;
import com.xt.core.utils.VarTemplate;
import com.xt.core.utils.XmlHelper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 系统级所有的参数配置信息。
 * <p>
 * Title: XT框架-事务逻辑部分
 * </p>
 * <p>
 * Description: 系统配置参数。在系统初始化时首先装载系统信息�?默认情况下，系统信息是�?gt-config.xml“文件中读取�?
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author 郑伟
 * @version 1.0
 * @date 2006-7-30
 */
public class SystemConfiguration implements Loadable {

    /** 定义在配置文件中使用的标签  **/
    public static final String CONF_FILE_TAG_EXTENSION = "extension";
    public static final String CONF_FILE_TAG_EXTENSIONS = "extensions";
    public static final String CONF_FILE_TAG_FILENAME = "fileName";
    public static final String CONF_FILE_TAG_LIST_DATA = "data";
    public static final String CONF_FILE_TAG_LIST_VALUE = "value";
    public static final String CONF_FILE_TAG_MAP_ENTRY = "entry";
    public static final String CONF_FILE_TAG_MAP_KEY = "key";
    public static final String CONF_FILE_TAG_MAP_VALUE = "value";
    public static final String CONF_FILE_TAG_PARAM = "param";
    public static final String CONF_FILE_TAG_PARAM_NAME = "name";
    public static final String CONF_FILE_TAG_PARAM_TYPE = "type";
    public static final String CONF_FILE_TAG_PARAM_VALUE = "value";
    public static final String CONF_FILE_TAG_SYSTEM = "system";
    /**
     * 参数产生冲突之后(从多个配置文件读取到相同的参数)应该如何处理
     */
    public static final String CONF_FILE_TAG_COLLISION = "collision";
    public static final String PAPAM_TYPE_LIST = "list";
    public static final String PAPAM_TYPE_MAP = "map";
    public static final String PAPAM_TYPE_SIMPLE = "simple";  // 默认的类型
    private static final String CONF_FILE_RELATIVE_PREFIX = "${relative}";
    public static final String CONF_FILE_TAG_COLLISION_MERGE = "merge";
    public static final String CONF_FILE_TAG_COLLISION_REPLACEMENT = "replacement";
    public static final String CONF_FILE_TAG_COLLISION_IGNORED = "ignored";
    /** 定义在配置文件中使用的标签  **/
    /**
     * 配置文件中，解析器的自定义参数解析
     */
    public static final String CONF_FILE_TAG_PARSERS = "parsers";
    /** 
     * 日志实例
     */
    private final static Logger logger = Logger.getLogger(SystemConfiguration.class);
    /**
     * 指定一个布尔参数的值，以确定是否输出参数（因为有些参数（如：密码等）可能会涉及到安全问题）。
     * 默认为输出。
     */
    private final static String PRINT_PARAMS = "system.printParams";
    /**
     * 自定义标签对应的解析类。
     */
    private static final Map<String, ParameterParser> parserMap = new HashMap<String, ParameterParser>();
    /**
     * 将参数的名称作为键，值作为变量；值可以是字符串、字符数组、列表和Map等类型。
     */
    private final Map<String, Object> params = new ConcurrentHashMap<String, Object>();
    /**
     * 在servlet的Web.xml的配置文件中，系统参数的配置文件名称
     */
    public static final String SYSTEM_CONFIGERATURE = "systemConf";
    /**
     * 单件静态实例
     */
    private static SystemConfiguration instance;
    /**
     * 动态加载的注册名称
     */
    public static final String CONFIG_FILE = "CONFIG_FILE";
//    private boolean initialized = false;
    /**
     * 记录已经加载的文件，使其不再重复加载
     */
    private final Set<String> initializedFiles = new HashSet();

    /**
     * 私有构建函数
     */
    private SystemConfiguration() {
    }

    /**
     * 注册一个参数解析器。（注意：注册后需要重新加载时，此解析器才能生效）
     *
     * @param name
     *            节点名称（XML 根下的一个标签名称），不能为空。
     * @param pp
     *            解析器实例，不能为空。
     */
    synchronized public void register(String name, ParameterParser pp) {
        if (name == null || pp == null) {
            throw new SystemException("系统参数解析器实例及其名称都不能空�?");
        }
        parserMap.put(name, pp);
    }

    /**
     * 返回单件的实例对象
     *
     * @return
     */
    synchronized public static SystemConfiguration getInstance() {
        if (instance == null) {
            instance = new SystemConfiguration();
        }
        return instance;
    }

    /**
     * 通过配置文件加载配置,
     * @param fileName 合法的配置文件名称,不能为空.
     * @throws BadParameterException 参数为空时抛出此异常。
     */
    synchronized public void load(final String fileName) throws BadParameterException {
        load(fileName, true);
    }

    /**
     * 装在指定的配置文件。
     * @param fileName 配置文件名称,不能为空.
     * @param clear    是否清空已将装载的参数
     * @throws SystemException 配置文件不存在是抛出此异常
     * @throws BadParameterException 参数为空时抛出此异常。
     */
    synchronized public void load(final String fileName, boolean clear)
            throws BadParameterException, SystemException {
        if (fileName == null) {
            throw new BadParameterException("被装载的配置文件不能为空。");
        }
        LogWriter.info2(logger, "正在装载配置文件[%s]。", fileName);

        File file = new File(fileName);
        try {
            FileInputStream fis = new FileInputStream(file);
            _load(fis, fileName, clear);
        } catch (FileNotFoundException e) {
            throw new SystemException(String.format("读取框架配置文件[%s]时出现错误！", fileName), e);
        }
    }

    /**
     * 通过流装载配置文件
     *
     * @param is 配置文件流，调用时要保证不能传入空参数
     * @param clear 是否清空已经装载的配置信息
     */
    synchronized public void load(InputStream is, boolean clear) {
        _load(is, null, clear);
    }

    /**
     * 通过流装载配置文件
     *
     * @param is 配置文件流，调用时要保证不能传入空参数
     * @param clear 是否清空已经装载的配置信息
     */
    synchronized private void _load(InputStream is, String configFileName, boolean clear) {
        if (configFileName != null) {
            if (initializedFiles.contains(configFileName)) {
                logger.warn(String.format("配置文件[%s]已经加载，系统将不再重复加载。", configFileName));
                return;
            }
            this.initializedFiles.add(configFileName);
            // 重新装载配置文件(需要慎重考虑何时重新加载此文件)
            // DynamicDeploy.getInstance().register(fileName, SystemConfiguration.instance);
        }

        if (clear) {
            // 避免加载多次引起问题，加载前要清除已有数据
            clear();
        }

        // 根节点
        Element root = XmlHelper.getRoot(is);

        // 系统参数最先加载(系统标签不能通过自定义进行解析)
        Element system = root.getChild(CONF_FILE_TAG_SYSTEM);
        if (system != null) {
            List<Element> paramElems = system.getChildren(CONF_FILE_TAG_PARAM);
            for (Iterator<Element> iter = paramElems.iterator(); iter.hasNext();) {
                Element param = iter.next();
                String name = param.getAttributeValue(CONF_FILE_TAG_PARAM_NAME); // 参数的名称
                if (StringUtils.isEmpty(name)) {
                    LogWriter.warn2(logger, "标签[%s]的名称属性为空，系统将丢弃此参数", param);
                    continue;
                }
                String type = param.getAttributeValue(CONF_FILE_TAG_PARAM_TYPE); // 参数的类型
                String attrValue = param.getAttributeValue(CONF_FILE_TAG_PARAM_VALUE);

                // 替换值中的变量
                if (attrValue != null && VarTemplate.contains(attrValue)) {
                    attrValue = VarTemplate.format(attrValue, this.params);
                }

                Object value = attrValue;

                // 如果标签的value属性值为空且其类型为空，则表示是多值标签
                if (PAPAM_TYPE_LIST.equals(type)) {
                    value = loadListValues(param);
                } else if (PAPAM_TYPE_MAP.equals(type)) {
                    value = loadMapValues(param);
                } else if (attrValue == null) {
                    LogWriter.warn2(logger, "名称为[%s]的标签[%s]的值未空，系统将丢弃此参数", param, name);
                    continue;
                }

                // 解决冲突
                if (params.containsKey(name)) {
                    CollisionType collistionType = getCollisionType(type, param);
                    LogWriter.info2(logger, "参数[%s]的冲突解决类型是[%s]。", name, collistionType);
                    switch (collistionType) {
                        case MERGE:
                            mergeParam(name, type, value);
                            break;
                        case REPLACEMENT:
                            params.put(name, value);
                            break;
                        case IGNORED:
                        // noting to do
                    }
                } else {
                    params.put(name, value);
                }
            }
        }

        // 加载自解析标签
        Map<String, String> parsers = readMap(CONF_FILE_TAG_PARSERS);
        if (parsers != null) {
            for (Iterator<Map.Entry<String, String>> iter = parsers.entrySet().iterator(); iter.hasNext();) {
                Map.Entry<String, String> entry = iter.next();
                String value = entry.getValue();
                Object obj = ClassHelper.newInstance(value);
                ParameterParser pp = (ParameterParser) obj; // 为了处理某些类加载器的问题，在此不能进行转型处理
                parserMap.put(entry.getKey(), pp);
            }
        }

        /** ******* 装载其他自定义解析的标签 ********** */
        List children = root.getChildren();
        for (Iterator iter = children.iterator(); iter.hasNext();) {
            Element child = (Element) iter.next();
            String name = child.getName();
            if (parserMap.containsKey(name)) {
                SystemParameter systemParameter = parse(child, true);
                ParameterParser pp = parserMap.get(name);
                Object obj = pp.parse(systemParameter);
                if (obj != null) {
                    this.params.put(pp.getParameterName(), obj);
                }
            } else if (!CONF_FILE_TAG_SYSTEM.equals(name)) {
                LogWriter.warn2(logger, "标签[%s]未找到自定义的解析类，系统将丢弃此标签。", name);
            }
        }

        // 加载扩展标签
        Element extentions = root.getChild(CONF_FILE_TAG_EXTENSIONS);
        if (extentions != null && extentions.getChildren(CONF_FILE_TAG_EXTENSION) != null) {
            for (Element extension : (List<Element>) extentions.getChildren()) {
                String childFileName = extension.getAttributeValue(CONF_FILE_TAG_FILENAME);
                if (StringUtils.isEmpty(childFileName)) {
                    continue;
                }
                // 尝试用流的方式进行读取
                InputStream _chlidInputStream = SystemConfiguration.class.getResourceAsStream(childFileName);
                if (_chlidInputStream != null) {
                    LogWriter.info(logger, "开始从文件流装载配置文件：", childFileName);
                    _load(_chlidInputStream, childFileName, false);
                } else {
                    // 引用的文件名称可以使用相对于本配置文件的相对路径
                    if (configFileName != null && childFileName.startsWith(CONF_FILE_RELATIVE_PREFIX)) {
                        File baseFile = new File(configFileName);
                        childFileName = baseFile.getAbsolutePath() + childFileName.substring(CONF_FILE_RELATIVE_PREFIX.length());
                    }
                    LogWriter.info(logger, "开始装载配置文件：", childFileName);
                    // 递归装载
                    load(childFileName, false);
                }
            }
        }

        /** ******* 装载其他自定义解析的参数 ********** */
        // 输出参数信息
        print();
    }

    /**
     * 将新老参数进行合并。
     * @param name
     * @param type
     * @param newValue
     */
    private void mergeParam(String name, String type, Object newValue) {
        if (!PAPAM_TYPE_LIST.equals(type) && !PAPAM_TYPE_MAP.equals(type)) {
            throw new BadParameterException(String.format("类型为[%s]的参数[%s]不能使用合并参数方式", type, name));
        }
        Object oldValue = params.get(name);
        LogWriter.info2(logger, "合并参数[%s]，原值=%s; 新值=%s", name, oldValue, newValue);
        if (oldValue instanceof List) {
            if (!(newValue instanceof List)) {
                throw new SystemConfigurationException(String.format("参数[%s]定义的类型可能存在不一致(new=%s; old=%s)。", name, newValue, oldValue));
            }
            ((List) oldValue).addAll((List) newValue);
        } else if (oldValue instanceof Map) {
            if (!(newValue instanceof Map)) {
                throw new SystemConfigurationException(String.format("参数[%s]定义的类型可能存在不一致(new=%s; old=%s)。", name, newValue, oldValue));
            }
            Map oldMap = (Map) oldValue;
            if (oldMap == Collections.EMPTY_MAP || oldMap == Collections.emptyMap()) {
                oldValue = newValue;
            } else {
                oldMap.putAll((Map) newValue);
            }
        } else {
            throw new SystemConfigurationException(String.format("参数[%s]的原值[%s]的类型不能处理。", name, oldValue));
        }
        params.put(name, oldValue);
    }

    /**
     * 从指定标签中读取“冲突”属性。
     * @param paramType 标签的类型
     * @param param     当前标签
     * @return 冲突的类型
     */
    private CollisionType getCollisionType(String paramType, Element param) {
        CollisionType collistionType = ((PAPAM_TYPE_LIST.equals(paramType)
                || PAPAM_TYPE_MAP.equals(paramType)) ? CollisionType.MERGE : CollisionType.REPLACEMENT);
        String collisionAttr = param.getAttributeValue(CONF_FILE_TAG_COLLISION);
        if (CONF_FILE_TAG_COLLISION_MERGE.equals(collisionAttr)) {
            collistionType = CollisionType.MERGE;
        } else if (CONF_FILE_TAG_COLLISION_IGNORED.equals(collisionAttr)) {
            collistionType = CollisionType.IGNORED;
        } else if (CONF_FILE_TAG_COLLISION_REPLACEMENT.equals(collisionAttr)) {
            collistionType = CollisionType.REPLACEMENT;
        }
        return collistionType;
    }

    /**
     * 输出参数信息
     */
    public void print() {
        // FIXME: 可能会留下参数隐患(输出密码等信息)
        if (!readBoolean(PRINT_PARAMS, true)) {
            return;
        }
        LogWriter.info(logger, "已经加载的系统参数如下：");
        for (Iterator<String> iter = params.keySet().iterator(); iter.hasNext();) {
            String key = iter.next();
            LogWriter.info(logger, key, params.get(key));
        }
    }

    /**
     * 解析参数，将参数转换为简单的“系统参数（SystemParameter）”形式。
     * @param elem
     * @param recursive
     * @return
     */
    protected SystemParameter parse(final Element elem, boolean recursive) {
        SystemParameter systemParameter = new SystemParameter(elem.getName());
        // 读取内容
        String content = elem.getText();
        if (content != null) {
            content = VarTemplate.format(content.trim(), params);
            systemParameter.setContent(content);
        }
        // 读取属性
        for (Iterator attrs = elem.getAttributes().iterator(); attrs.hasNext();) {
            Attribute attr = (Attribute) attrs.next();
            String value = attr.getValue();
            // 替换值中的变量
            if (value != null && VarTemplate.contains(value)) {
                value = VarTemplate.format(value, this.params);
            }
            systemParameter.addAttribute(attr.getName(), value);
        }
        if (recursive) {
            for (Iterator children = elem.getChildren().iterator(); children.hasNext();) {
                Element child = (Element) children.next();
                systemParameter.addChild(parse(child, recursive));
            }

        }
        return systemParameter;
    }

    /**
     * 将系统状态恢复到原初状态
     */
    synchronized public void clear() {
        LogWriter.info2(logger, "清空当前的参数");
        params.clear();
        initializedFiles.clear();
        parserMap.clear();
    }

    /**
     * 装载列表型的参数。配置文件的形式如下：<BR/> <system> <param name="SCREEN_FLOWS"
     * type="list"> <BR/> <data value="com.xt.gt.screen.XmlScreenFlow" /> <BR/>
     * <data value="com.xt.gt.screen.RuleScreenFlow" /> <BR/> </param> <BR/>
     * </system><BR/> <BR/> 如果标签“data”为空，则返回一个空List（Collections.EMPTY_LIST），
     * 否则返回空List实例。如果标签值“value”为空，则抛出系统异常。
     * TODO: List 类型是否需要定义顺序。
     * @param param
     *            配置文件的Element参数。
     * @return 如果无参数返回空。
     */
    private List<String> loadListValues(Element param) {
        // 配置文件中的列表数据
        List valueElems = param.getChildren(CONF_FILE_TAG_LIST_DATA);
        if (CollectionUtils.isEmpty(valueElems)) {
            LogWriter.warn(logger, "多值(type='list')参数未定义“data”标签。");
            return Collections.emptyList();
        }
        List<String> data = new ArrayList(valueElems.size());
        for (int i = 0; i < valueElems.size(); i++) {
            Element dataElem = (Element) valueElems.get(i);
            String value = dataElem.getAttributeValue(CONF_FILE_TAG_LIST_VALUE);
            if (value == null) {
                throw new SystemConfigurationException("列表参数的不能为空！");
            }
            // 替换值中的变量
            if (VarTemplate.contains(value)) {
                value = VarTemplate.format(value, this.params);
            }
            data.add(value);
        }
        return data;
    }

    /**
     * 装载 Map 类型的参数。配置文件的形式如下：<br/> <system> <param name="CONVERTERS"
     * type="map"> <entry key="java.util.Calendar"
     * value="com.xt.core.utils.CalendarConverter" /> </param> </system> <br/>
     * 如果参数“entry”为空，则返回一个空Map（Collections.EMPTY_MAP）， 否则返回 Map类型的实例。如果属性值
     * key”为空，或属性 value”为空，则抛出系统异常。
     *
     * @param param
     *            配置文件的Element参数。
     * @return Map对象
     */
    private Map<String, String> loadMapValues(Element param) {
        List valueElems = param.getChildren(CONF_FILE_TAG_MAP_ENTRY);
        if (CollectionUtils.isEmpty(valueElems)) {
            LogWriter.warn(logger, "值对（Map）属性的 entry 标签不能为空，读取失败。");
            return Collections.emptyMap();
        }
        Map<String, String> data = new LinkedHashMap<String, String>(2);
        for (int i = 0; i < valueElems.size(); i++) {
            Element entry = (Element) valueElems.get(i);
            String key = entry.getAttributeValue(CONF_FILE_TAG_MAP_KEY);
            String value = entry.getAttributeValue(CONF_FILE_TAG_MAP_VALUE);
            if (StringUtils.isEmpty(key) || value == null) {
                throw new SystemConfigurationException(String.format("装载参数[%s]时失败，"
                        + "主键不能为空或者是空串，且值也不能为空！", key));
            }
            // 替换值中的变量
            if (VarTemplate.contains(value)) {
                value = VarTemplate.format(value, this.params);
            }
            data.put(key, value);
        }
        return data;
    }

    /**
     * 返回当前所有的参数名称。
     * @return 参数名称列表
     */
    public List<String> getParamNames() {
        List<String> names = new ArrayList();
        for (Iterator<String> iter = params.keySet().iterator(); iter.hasNext();) {
            names.add(iter.next());
        }
        return names;
    }

    /**
     * 返回所有的参数参数集合。
     * @return 参数集合，不为空。
     */
    final public Map getParams() {
        return Collections.unmodifiableMap(params);
    }

    /**
     *读取字符类型的参数值，如果参数名称为空，则抛出非法参数异常。
     * 如果为找到对应的参数，系统将返回默认参数；如果参数非字符串类型，
     * 则返回类型的toString()方法取得对应的字符串值。
     * @return
     */
    public String readString(String name) {
        return readString(name, null);
    }

    /**
     * 读取字符类型的参数值，如果参数名称为空，则抛出非法参数异常。
     * 如果为找到对应的参数，系统将返回默认参数；如果参数非字符串类型，
     * 则返回类型的toString()方法取得对应的字符串值。
     *
     * @param name
     *            参数名称
     * @param defaultValue
     *            默认值。
     * @return 字符串类型，如果无此参数值，则返回空。
     */
    public String readString(String name, String defaultValue) {
        if (StringUtils.isEmpty(name)) {
            throw new BadParameterException("参数的名称不能为空！");
        }
        if (!params.containsKey(name)) {
            return defaultValue;
        }
        Object obj = params.get(name);
        String value = null;
        if (obj instanceof String) {
            value = (String) obj;
        } else {
            value = obj.toString();
        }

        return value;
    }

    /**
     * 从配置文件中读取颜色属性，要求其值必须是“#RRGGBB”格式，RGB 为 16进制的整数。
     *
     * @param name
     *            参数名称
     * @param defaultColor
     *            默认颜色
     * @return
     */
    public Color readColor(String name, Color defaultColor) {
        Color color = defaultColor;
        String colorStr = readString(name);
        if (StringUtils.isNotEmpty(colorStr)) {
            // 首先进行格式校验
            if (!RegExpUtils.isColorDigitals(colorStr)) {
                throw new SystemException(String.format("参数[%s]值的格式错误。", name));
            }
            // 去掉前导的�?#�?
            colorStr = colorStr.startsWith("#") ? colorStr.substring(1)
                    : colorStr;
            int r = Integer.parseInt(colorStr.substring(0, 2), 16);
            int g = Integer.parseInt(colorStr.substring(2, 4), 16);
            int b = Integer.parseInt(colorStr.substring(4), 16);
            color = new Color(r, g, b);
        }
        return color;
    }

    /**
     * 用指定的名称注册一个参数，如果参数以及存在，则新参数将覆盖原有参数。
     * 注意：此方法不支持跨越虚拟机的。
     * @param name 参数名称
     * @param value 参数值，如果参数值为空，则相当于移除此参数。
     */
    public void set(String name, Object value) {
        if (StringUtils.isEmpty(name)) {
            throw new SystemConfigurationException("参数名称不能为空。");
        }
        if (value == null) {
            remove(name);
        }
        synchronized (this.params) {
            this.params.put(name, value);
        }
    }

    /**
     * 移除此参数。
     * @param name 参数名称，如果为空或者不存在此参数，不进行任何处理。
     */
    public void remove(String name) {
        if (name != null) {
            params.remove(name);
        }
    }

    /**
     * 判断系统参数中是否包含以此名称命名的参数。
     * @param name 参数名称
     * @return 如果名称为空，或者不存在，返回 false。
     */
    public boolean contains(String name) {
        if (name != null) {
            return params.containsKey(name);
        }
        return false;
    }

    /**
     * 读取Map类型的参值。
     *
     * @param name
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> readMap(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new BadParameterException("参数的名称不能为空！");
        }
        Object obj = params.get(name);
        if (obj == null) {
            return Collections.EMPTY_MAP;
        }
        if (!(obj instanceof Map)) {
            throw new BadParameterException(String.format("参数[%s]的类型必须是Map类型。", name));
        }
        return ((Map<String, String>) obj);
    }

    /**
     * 读取字符串数组配置参数，当字符串多于一个配置参数时可以采用这种方式读取。 如果参数为空，则返回长度为 0 的字符串数组。
     *
     * @param name
     *            参数名称
     * @return 非空的字符串数组。
     */
    public String[] readStrings(String name) {
        if (StringUtils.isEmpty(name)) {
            throw new BadParameterException("参数的名称不能为空！");
        }
        Object obj = params.get(name);
        if (obj == null) {
            return new String[0];
        }
        if (!(obj instanceof List)) {
            throw new BadParameterException(String.format("参数[%s]的类型必须是列表类型。", name));
        }
        List values = (List) params.get(name);
        return (String[]) values.toArray(new String[values.size()]);
    }

    /**
     * 读取 Boolean 类型的配置参数。默认为：fasle。
     *
     * @see readBoolean(String name, boolean defaultValue)
     * @param name
     *            参数名称
     * @return true|false。
     */
    public boolean readBoolean(String name) {
        return readBoolean(name, false);
    }

    /**
     * 读取 Boolean 类型的配置参数。如果参数为如下字符串：true|y|1|yes|on，则返回真。
     *
     * @param name
     *            参数名称
     * @return true|false。
     */
    public boolean readBoolean(String name, boolean defaultValue) {
        boolean value = defaultValue;
        String strValue = readString(name, String.valueOf(defaultValue));
        value = BooleanUtils.isTrue(strValue);
        return value;
    }

    /**
     * 读取类型为整数的参数值，这个方法与读取字符值的方法一致，并将其转换为整数类型。 如果参数的格式非法，则将抛出非法参数异常
     *
     * @param name
     *            参数名称
     * @param defaultValue
     *            默认值
     * @return 参数配置的整数值或默认值。
     */
    public int readInt(String name, int defaultValue) {
        int value = defaultValue;
        String strValue = readString(name, String.valueOf(defaultValue));
        try {
            value = Integer.parseInt(strValue);
        } catch (NumberFormatException e) {
            throw new BadParameterException("参数值是非法的整数格式！");
        }
        return value;
    }

    /**
     * 读取整数值，这个方法与读取字符串的方法一致， 并将其转换为整数类型。如果参数的格式非法，则将抛出非法参数异常
     *
     * @param name
     *            参数名称
     * @param defaultValue
     *            默认值
     * @return 参数配置的浮点数或默认值
     */
    public double readDouble(String name, double defaultValue) {
        double value = defaultValue;
        String strValue = readString(name, String.valueOf(defaultValue));
        try {
            value = Double.parseDouble(strValue);
        } catch (NumberFormatException e) {
            throw new BadParameterException("参数值是非法的双精度数格式！");
        }
        return value;
    }

    /**
     * 读取配置文件中的对象。这个方法要求对应的配置项一定是可实例化的类名称， 并拥有一个无参数的公共构造函数，此方法将初始这个对象。如果配置参数
     * 未指定参数，则使用默认的参数配置。
     *
     * @param name
     *            参数的名称
     * @return 参数的实例或者默认值
     */
    public Object readObject(String name) {
        return readObject(name, null);
    }

    /**
     * 读取配置文件中的对象。这个方法要求对应的配置项一定是可实例化的类名称， 并拥有一个无参数的公共构造函数，此方法将初始这个对象。如果配置参数
     * 未指定参数，则使用默认的参数配置。
     *
     * @param name
     *            参数的名称
     * @param defaultValue
     *            默认值
     * @return 参数的实例或者默认值
     */
    public Object readObject(String name, Object defaultValue) {

        Object value = params.get(name);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof String) {
            String className = (String) value;
            value = ClassHelper.newInstance(className);
        }
        return value;
    }

    /**
     * 读取参数数组，并将参数名称转换为对象，这些数组必须是同一个类型组 如果用户指定了类型参数，则对参数的类型进行检查�?如果没有配置�?
     * 则返回长度为0的对象数组�?
     *
     * @param name
     *            参数的名�?
     * @param Class
     *            参数的类型，可以为空，表示不�?��参数的类�?
     * @return 参数的实例或者默认�?
     */
    public <T> T[] readObjects(String name, Class<T> clazz) {
        String[] classNames = readStrings(name);
        if (classNames == null) {
            return null;
        }
        // Object[] rets = Arrays. new Object[classNames.length];
        T[] rets = (T[]) Array.newInstance(clazz, classNames.length);
        for (int i = 0; i < classNames.length; ++i) {
            Object obj = ClassHelper.newInstance(classNames[i]);
            if (clazz != null && !clazz.isInstance(obj)) {
                throw new SystemConfigurationException(String.format("参数[%s]的类型非要求的类型[%s]。", classNames[i], clazz.getName()));
            }
            rets[i] = (T) obj;
        }
        return rets;
    }
}
