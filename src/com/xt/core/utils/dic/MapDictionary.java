package com.xt.core.utils.dic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用Map类型的数据表示为一个字典。
 * @author albert
 *
 */
public class MapDictionary implements Dictionary {

    /**
     *
     */
    private static final long serialVersionUID = -1527674863645228275L;
    private Item[] items;
    /**
     * 字典名称。
     */
    private final String name;

    public MapDictionary(String name, Map<?, String> map) {
        this.name = name;
        if (null != map) {
            items = new Item[map.size()];
            int index = 0;
            for (Map.Entry<?, String> entry : map.entrySet()) {
                Item item = new Item();
                items[index] = item;
                item.setIndex(index++);
                item.setTitle(entry.getValue());
                item.setValue(entry.getKey());
                item.setValidate(true);
            }
        }
        if (items == null) {
            items = new Item[0];
        }
    }

    public String getName() {
        return this.name;
    }

    /**
     * 增加一个字典选项，如果选项或者其值为空，返回 false。如果选项已经存在，则不继续增加。
     *
     */
    synchronized public boolean add(Item item) {
        if (item == null || item.getValue() == null) {
            return false;
        }
        boolean existed = false;
        List<Item> temp = new ArrayList<Item>(items.length);
        for (Item di : items) {
            if (di.equals(item)) {
                existed = true;
            }
            temp.add(di);
        }
        if (!existed) {
            temp.add(item);
        }
        items = temp.toArray(new Item[temp.size()]);
        return !existed;
    }

    synchronized public Item[] getItems() {
        return items;
    }

    synchronized public Item getOption(Object value) {
        if (value == null) {
            return null;
        }
        for (int i = 0; i < items.length; i++) {
            if (value.equals(items[i].getValue())) {
                return items[i];
            }
        }
        return null;
    }

    synchronized public Item getValue(int seq) {
        if (seq < 0 || seq >= items.length) {
            return null;
        }
        return items[seq];
    }

    synchronized public boolean remove(Item item) {
        List<Item> temp = new ArrayList<Item>(items.length);
        for (Item di : items) {
            if (!di.equals(item)) {
                temp.add(di);
            }
        }
        boolean flag = temp.size() != items.length;
        items = temp.toArray(new Item[temp.size()]);
        return flag;
    }
}
