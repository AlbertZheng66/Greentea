package com.xt.core.utils;

import java.util.*;

/**
 * <p>Title: GreeTea 框架。</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 */

public class CollectionUtils {
    private static final String DEFAULT_SEPERATOR = ",";

	public CollectionUtils() {
    }

    public static int indexOf(Object[] objs, Object obj) {
        int index = -1;
        if (objs != null && obj != null) {
            for (int i = 0; i < objs.length; i++) {
                if (obj.equals(objs[i])) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    public static void fill(Object[] objs, Object obj) {
        if (objs != null && obj != null) {
            for (int i = 0; i < objs.length; i++) {
                objs[i] = obj;
            }
        }
    }

    //根据数组产生一个数组列表
    public static <T> List<T> newInstance (T[] objs) {
        List<T> list;
        if (objs != null) {
        	list = new ArrayList<T>(objs.length);
                list.addAll(Arrays.asList(objs));
        } else {
        	list = Collections.EMPTY_LIST;
        }
        return list;
    }
    
    /**
     * 将字符串数组变为由分隔符分割的字符串，只在字符串的中间加入分割符。
     * @param strs 字符串数组
     * @param sep 分割符
     * @return 如果字符串为空或者分割符为空，则返回空。
     */
    public static String toString (String[] strs, char sep) {
		if (strs == null) {
			return null;
		}
		StringBuilder strBuf = new StringBuilder ();
		int length = strs.length;
		for (int i = 0; i < length; i++) {
			String str = strs[i];
			strBuf.append(str);
			if (i < length - 1) {
				strBuf.append(sep);
			}
		}
		return strBuf.toString();
	}
    
    /**
     * 将字符串数组变为由分隔符分割的字符串，只在字符串的中间加入分割符。
     * @param strs 字符串数组
     * @param sep 分割符
     * @return 如果字符串为空或者分割符为空，则返回空。
     */
    public static String toString (String[] strs, String sep) {
		if (strs == null) {
			return null;
		}
		StringBuilder strBuf = new StringBuilder ();
		int length = strs.length;
		for (int i = 0; i < length; i++) {
			String str = strs[i];
			strBuf.append(str);
			if (i < length - 1) {
				strBuf.append(sep);
			}
		}
		return strBuf.toString();
	}
    
    /**
     * 将字符串数组变为由分隔符分割的字符串，只在字符串的中间加入分割符。
     * @param strs 字符串数组
     * @param sep 分割符
     * @return 如果字符串为空或者分割符为空，则返回空。
     */
    public static String join (List<?> list, String sep) {
		if (list == null) {
			return null;
		}
		StringBuilder strBuf = new StringBuilder ();
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (element == null) {
				// 空字符串不写
			} else if (element instanceof String) {
				strBuf.append((String)element);
			} else {
				strBuf.append(element.toString());
			}
			if (iter.hasNext()) {
				strBuf.append(sep);
			}
		}
		return strBuf.toString();
	}
    
    /**
     * 将字符串数组变为由分隔符分割的字符串，只在字符串的中间加入分割符。
     * @param strs 字符串数组
     * @param sep 分割符
     * @return 如果字符串为空或者分割符为空，则返回空。
     */
    public static List<String> split (String str, String sep) {
		if (str == null) {
			return null;
		}
		
		if (sep == null) {
			sep = DEFAULT_SEPERATOR;
		}
		
		return newInstance(str.split(sep));
	}
    
    public static boolean isEmpty(Collection<?> col) {
        return (col == null || col.isEmpty());
    }

    public static boolean isNotEmpty(Collection<?> col) {
        return (col != null && !col.isEmpty());
    }


    public static boolean isEmpty(Object[] array) {
        return (array == null || array.length == 0);
    }
    
    static public <T> List<T> unique(Collection<T> col, Comparator<T> com) {
        if (col == null) {
            return Collections.emptyList();
        }
        if (com == null) {
            if (col instanceof List) {
                return (List)col;
            } else {
                List<T> list = new ArrayList(col.size());
                list.addAll(col);
                return list;
            }
            
        }
        final List<T> list = new ArrayList();
        for (Iterator<T> it = col.iterator(); it.hasNext();) {
            T obj = it.next();
            if (obj == null) {
                continue;
            }
            if (list.isEmpty() || !exists(list, obj, com)) {
                list.add(obj);
            }
        }
        return list;
    }
    
    private static <T> boolean exists(List<T> list, T target, Comparator<T> com) {
        for (Iterator<T> it = list.iterator(); it.hasNext();) {
            T elem = it.next();
            int ret = com.compare(elem, target);
            if (ret == 0) {
                return true;
            }
        }
        return false;
    }
    
    static public Map toMap(Object... argv) {
        if (argv == null || argv.length == 0) {
            return Collections.emptyMap();
        }
        Map map = new HashMap();
        for (int i = 0; i < argv.length; i+=2) {
            Object key = argv[i];
            Object value = argv[i+1];
            if (key != null && value != null) {
                map.put(key, value);
            }
        }
        return map;
    }

}
