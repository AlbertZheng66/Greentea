package com.xt.core.utils.dic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.xt.core.exception.BadParameterException;

/**
 * 缺省的字典表。
 * <p>
 * Title: XT框架-核心逻辑部分
 * </p>
 * <p>
 * Description:
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
 * @date 2006-9-6
 */
public class DefaultDictionary implements Dictionary {

    /**
     *
     */
    private static final long serialVersionUID = 7359693203795923700L;
    /**
     * 字典选项列表
     */
    private final List items = new ArrayList();

    /**
     * 字典名称。
     */
    private final String name;

    public DefaultDictionary(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Item[] getItems() {
        return (Item[]) items.toArray(new Item[items.size()]);
    }

    public void setDictionaryItems(List dictionaryItems) {
        if (dictionaryItems != null) {
            this.items.addAll(dictionaryItems);
        }
    }

    public Item getOption(Object value) {
        if (value == null) {
            throw new BadParameterException("参数不能为空");
        }
        for (Iterator iter = items.iterator(); iter.hasNext();) {
            Item item = (Item) iter.next();
            if (value.equals(item.getValue())) {
                return item;
            }
        }
        throw new BadParameterException("未能找到对应的字典选项");
    }

    public Item getValue(int seq) {
        if (seq < 0 && seq >= items.size()) {
            throw new BadParameterException("参数超出字典选项的个数");
        }
        return (Item) items.get(seq);
    }

    /**
     * 增加一个字典项。
     *
     * @param title
     *            字典项描述，不能为空
     * @param value
     *            字典值，不能为空
     * @param validate
     *            是否有效，不能为空
     */
    public void addItem(String title, String value, boolean validate) {
        Item item = new Item();
        item.setIndex(items.size());
        item.setTitle(title);
        item.setValue(value);
        item.setValidate(validate);
        items.add(item);
    }

    public boolean add(Item item) {
        if (item == null) {
            return false;
        }
        item.setIndex(items.size());
        items.add(item);
        return true;
    }

    public boolean remove(Item item) {
        if (item == null || item.getValue() == null
                || getOption(item.getValue()) == null) {
            return false;
        }
        for (Iterator iter = items.iterator(); iter.hasNext();) {
            Item temp = (Item) iter.next();
            if (item.getValue().equals(temp.getValue())) {
                return items.remove(temp);
            }
        }
        return true;
    }
}
