package com.xt.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.xt.core.exception.SystemException;
import com.xt.core.log.LogWriter;
import com.xt.gt.sys.SystemParameter;
import java.util.Map;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description:工具类，提供一些方便的解析XML文件或者字符串的方法。解析的方式采用JDOM解析。</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */
public final class XmlHelper {

    private final static Logger logger = Logger.getLogger(XmlHelper.class);
    /**
     * 编码格式
     */
    public static final String UTF8 = "UTF-8";
    /**
     * 编码格式
     */
    public static final String GBK = "GBK";
    /**
     * 编码格式
     */
    public static final String ASCII = "ASCII";

    /**
     * 传入XML字符串，返回XML的根节点。
     * @param xml XML字符串
     * @return xml JDOM树的根节点，如果XML格式错误，返回空(null)
     * @throws JDOMException
     * @throws IOException
     */
    public static Element getRootFromString(String xml) throws JDOMException,
            IOException {
        if (xml == null) {
            return null;
        }
        LogWriter.debug(logger, "XmlHelper getRootFromString xml=" + xml);
        Document doc = null;
        SAXBuilder builder = new SAXBuilder(); //解析器
        doc = builder.build(new StringReader(xml));
        if (doc != null) {
            return doc.getRootElement();
        }
        return null;
    }

    /**
     * 传入XML文件的名称，返回XML的根节点。
     * @param xml XML文件的路径
     * @return xml JDOM树的根节点，如果XML格式错误，返回空(null)
     * @throws JDOMException
     * @throws IOException
     */
    public static Element getRoot(String xml) throws JDOMException, IOException {
        LogWriter.debug(logger, "XmlHelper getRoot xml=" + xml);
        if (xml == null) {
            return null;
        }
        Document doc = null;
        SAXBuilder builder = new SAXBuilder(); //解析器
        doc = builder.build(xml);
        if (doc != null) {
            return doc.getRootElement();
        }
        return null;
    }

    /**
     * 传入XML文件流，返回XML的根节点。解析的方式采用JDOM解析。
     * @param xml XML文件流
     * @return xml JDOM树的根节点，如果XML格式错误，返回空(null)
     * @throws JDOMException
     * @throws IOException
     */
    public static Element getRoot(InputStream xml) {
        if (xml == null) {
            return null;
        }
        // LogWriter.debug(logger, "XmlHelper getRoot xml", xml);
        Document doc = null;
        SAXBuilder builder = new SAXBuilder(); //解析器
        try {
            doc = builder.build(xml);
        } catch (JDOMException e) {
            throw new SystemException("JDOM 解析异常", e);
        } catch (IOException e) {
            throw new SystemException("XML解析时产生流异常", e);
        }
        if (doc != null) {
            return doc.getRootElement();
        }
        return null;
    }

    /**
     * 传入XML文件流，返回XML的根节点。解析的方式采用JDOM解析。
     * @param xml XML文件流
     * @return xml JDOM树的根节点，如果XML格式错误，返回空(null)
     * @throws JDOMException
     * @throws IOException
     */
    public static Element getRoot(Reader xml) throws JDOMException, IOException {
        // LogWriter.debug(logger, "XmlHelper getRoot xml=", xml);
        if (xml == null) {
            return null;
        }
        Document doc = null;
        SAXBuilder builder = new SAXBuilder(); //解析器
        doc = builder.build(xml);
        if (doc != null) {
            return doc.getRootElement();
        }
        return null;
    }

    /**
     * 输出XML文件流。根据指定的编码，将输入的节点，输出的输出流中。
     * @param xml XML文件流
     * @return xml JDOM树的根节点，如果XML格式错误，返回空(null)
     * @throws JDOMException
     */
    public static void output(OutputStream os, Element root, String encoding) {
        if (os == null || root == null) {
            throw new SystemException("输出参数和根节点都不能为空。");
        }
        encoding = (encoding == null ? UTF8 : encoding);
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(os, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new SystemException(String.format("不支持的编码格式[%s]。", encoding),
                    e);
        }
        output(writer, root, encoding);
    }

    /**
     * 输出XML文件流。根据指定的编码，将输入的节点，输出的输出流中。
     * @param os 文件输出文件流实例，不能为空。
     * @param root 根节点实例，不能为空。
     * @param encoding 输出的编码格式，如果为空，默认采用"UTF-8"格式输出。
     * @throws SystemException
     */
    public static void output(OutputStream os, SystemParameter root, String encoding) {
        if (root == null) {
            throw new SystemException("根节点不能为空。");
        }

        Element rootElem = create(root, null);
        output(os, rootElem, encoding);
    }

    static private Element create(SystemParameter sysParam, Element parent) {
        if (sysParam.getName() == null || sysParam.getName().trim().isEmpty()) {
            throw new SystemException("节点元素的名称不能为空。");
        }
        Element elem = new Element(sysParam.getName());

        // 增加属性
        for (Iterator<Map.Entry<String, String>> it = sysParam.getAttibutes().entrySet().iterator(); it.hasNext();) {
            Map.Entry<String, String> entry = it.next();
            if (entry.getValue() != null) {
                elem.setAttribute(entry.getKey(), entry.getValue());
            }
        }

        // 追加子节点
        for (Iterator<SystemParameter> it = sysParam.getChildren().iterator(); it.hasNext();) {
            SystemParameter child = it.next();
            if (child != null) {
                create(child, elem);
            }
        }

        // 建立父子关系
        if (parent != null) {
            parent.addContent(elem);
        }
        return elem;
    }

    /**
     * 输出XML文件流。根据指定的编码，将输入的节点，输出的输出流中。
     * @param xml XML文件流
     * @return xml JDOM树的根节点，如果XML格式错误，返回空(null)
     * @throws JDOMException
     */
    public static void output(Writer writer, Element root, String encoding) {
        if (writer == null || root == null) {
            throw new SystemException("输出参数和根节点都不能为空。");
        }
        encoding = (encoding == null ? UTF8 : encoding);
        Document doc = new Document(root);
        XMLOutputter outputter = new XMLOutputter();
        Format format = Format.getPrettyFormat();
        format.setEncoding(encoding);
        outputter.setFormat(format);
        try {
            writer.write(outputter.outputString(doc));
        } catch (IOException e) {
            throw new SystemException(String.format("输出节点[%s]时出现错误。", root.toString()));
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                logger.warn("关闭输出流时出现错误。", e);
            }
        }
    }

    /**
     * 传入XML文件流，返回XML的根节点。解析的方式采用JDOM解析。
     * @param xml XML文件流
     * @return xml JDOM树的根节点，如果XML格式错误，返回空(null)
     * @throws JDOMException
     */
    public static Element getRoot(byte[] b) throws JDOMException {
        throw new UnsupportedOperationException("此方法尚未实现");
        //        Document doc = null;
        //        SAXBuilder builder = new SAXBuilder(); //解析器
        //        PushbackInputStream pis = new PushbackInputStream();
        //        pis.unread(b);
        //        doc = builder.build(pis);
        //        if (doc != null)
        //        {
        //            return doc.getRootElement();
        //        }
    }

    /**
     *
     * @param root Element 被解析的XML根节点
     * @param path String 被解析的节点的路径，路径的形式为"\items\item\",第一个斜线代表根节点，以下的每个
     * 斜线代表一级目录
     * @param clazz Class 结果列表中包含的对象的类
     * @param item Item 生成的对象的转换接口
     * @return List
     */
    public static List iterator(Element root, String path, Class clazz,
            Item item) throws IllegalAccessException, InstantiationException {
        //结果列表
        List list = new ArrayList();

        List elems = getElems(path);

        String lastElemName = (String) elems.get(elems.size() - 1);

        //remove the last element

        //the last element of path
        Element elem = null;
        Element prev = root;
        for (Iterator iter = elems.iterator(); iter.hasNext();) {
            String elemName = (String) iter.next();
            prev = prev.getChild(elemName);
            elem = prev;
        }

        //generate a object
        Object obj = ClassHelper.newInstance(clazz);

        //如果item为空，使用默认的接口实现,默认的情况下，使用反射机制进行映射。
        item = (item == null ? new Item() {

            public Object createObject(Object obj, Element elem) {
                return null;
            }
        } : item);
        list.add(item.createObject(obj, root));
        return list;
    }

    private static List getElems(String path) {
        Matcher m = RegExpUtils.generate("\\w+", path);
        List elems = new ArrayList();
        int start = 0; //
        while (m.find(start)) {
            elems.add(path.substring(m.start(start), m.end(start)));
            start = m.end(start);
        }
        return elems;
    }
}
