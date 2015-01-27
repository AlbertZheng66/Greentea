package com.xt.core.utils.dic;

import com.xt.core.exception.BadParameterException;
import java.util.ArrayList;
import java.util.List;

public class EnumDictionary implements Dictionary {

    /**
     *
     */
    private static final long serialVersionUID = -5303688307638908893L;
    private final List<Item> items = new ArrayList();
    private final String name;

    public <T extends Enum<T>>   EnumDictionary(Class<T> enumClass) {
        if (null == enumClass) {
            throw new BadParameterException("枚举类不能为空。");
        }
        this.name = enumClass.getName();
        T[] values = enumClass.getEnumConstants();
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                Item item = new Item();
                item.setIndex(i);
                item.setTitle(values[i].toString());
                item.setValue(values[i]);
                item.setValidate(true);
                items.add(item);
            }
        }

    }

    public String getName() {
        return this.name;
    }

    public boolean add(Item item) {
        return false;
    }

    public Item[] getItems() {
        return items.toArray(new Item[items.size()]);
    }

    synchronized public Item getOption(Object value) {
        if (value == null) {
            return null;
        }
        for (int i = 0; i < items.size(); i++) {
            if (value.equals(items.get(i).getValue())) {
                return items.get(i);
            }
        }
        return null;
    }

    synchronized public Item getValue(int seq) {
        if (seq < 0 || seq >= items.size()) {
            return null;
        }
        return items.get(seq);
    }

    public boolean remove(Item item) {
        return false;
    }
}
