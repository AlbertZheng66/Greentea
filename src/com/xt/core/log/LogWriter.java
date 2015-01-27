package com.xt.core.log;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/**
 * <p>
 * Title: GreeTea 框架。
 * </p>
 * <p>
 * Description: 按级别管理日志信息（一般情况下，程序员调试时使用debug方法，系统框架人员使用info方法，
 * 系统发生错误或程序出现异常时使用error方法）
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author 郑伟
 * @version 1.0
 */
public final class LogWriter {

    /**
     * 错误日志。是org.apache.log4j.Logger类的一个实例，我们会用它的
     * 一些方法来记录错误信息。logger会在static方法中被附初值。
     */
    private static Logger error = null;
    /**
     * 系统日志。是org.apache.log4j.Logger类的一个实例，我们会用它的
     * 一些方法来记录系统日志。logger会在static方法中被附初值。
     */
    private static Logger defaultInfo = null;
    /**
     * 调试日志。是org.apache.log4j.Logger类的一个实例，我们会用它的
     * 一些方法来记录程序员调试程序时的信息。logger会在static方法中被附初值。
     */
    private static Logger defaultDebug = null;

    static {
        error = Logger.getLogger("error");
        defaultDebug = Logger.getLogger("debug");
        defaultInfo = Logger.getLogger("info");
    }

    /**
    //	 * 记录程序员调试程序的信息。系统正常运行时，此方法所输出的信息将被屏蔽掉，亦不记录到任何文件。
    //	 *
    //	 * @param message
    //	 *            调试信息
    //	 */
//	public static void trace(Collection coll) {
//
//		if (debug.isDebugEnabled()) {
//			StringBuilder strBld = new StringBuilder("跟踪信息: ");
//			if (coll == null) {
//				strBld.append("<null>");
//			} else {
//				strBld.append(ToStringBuilder.reflectionToString(coll));
//			}
//			System.out.println(strBld.toString());
//		}
//	}
//	/**
//	 * 记录程序员调试程序的信息。系统正常运行时，此方法所输出的信息将被屏蔽掉，亦不记录到任何文件。
//	 * 
//	 * @param message
//	 *            调试信息
//	 */
//	public static void trace(Enumeration coll) {
//
//		if (debug.isDebugEnabled()) {
//			StringBuilder strBld = new StringBuilder("跟踪信息: ");
//			if (coll == null) {
//				strBld.append("<null>");
//			} else {
//				strBld.append("[");
//				for (; coll.hasMoreElements();) {
//					Object value = coll.nextElement();
//					if (value == null) {
//						strBld.append("<null>");
//					} else  if (value instanceof String) {
//						strBld.append((String)value);
//					} else {
//						strBld
//								.append(ToStringBuilder
//										.reflectionToString(value));
//					}
//					if (coll.hasMoreElements()) {
//						strBld.append(",");
//					}
//				}
//				strBld.append("]");
//			}
//			System.out.println(strBld.toString());
//		}
//	}
//	/**
//	 * 记录程序员调试程序的信息。系统正常运行时，此方法所输出的信息将被屏蔽掉，亦不记录到任何文件。
//	 * 
//	 * @param message
//	 *            调试信息
//	 */
//	public static void trace(String message) {
//
//		if (debug.isDebugEnabled()) {
//			System.out.println("跟踪信息:" + message);
//		}
//	}
    public static void trace(String name, String value) {
        if (defaultDebug.isDebugEnabled()) {
            defaultDebug.debug(name + "=" + value);
        }
    }

    public static void trace(String name, Object value) {
        if (defaultDebug.isDebugEnabled()) {
            StringBuilder strBld = new StringBuilder(name);
            strBld.append("=");
            if (value == null) {
                strBld.append("<null>");
            } else if (value instanceof String) {
                strBld.append((String) value);
            } else {
                strBld.append(ToStringBuilder.reflectionToString(value));
            }
            defaultDebug.debug(strBld.toString());
        }
    }

    /**
     * 记录程序员调试程序的信息。系统正常运行时，此方法所输出的信息需要被屏蔽掉，
     * 亦不记录到任何文件。
     *
     * @deprecated
     * @param message
     *            调试信息
     */
    public static void debug(String message) {
        if (defaultDebug.isDebugEnabled()) {
            defaultDebug.debug(message);
        }
    }

    /**
     * 记录程序员调试程序的信息。系统正常运行时，此方法所输出的信息需要被屏蔽掉，
     * 亦不记录到任何文件。
     *
     * @param message
     *            调试信息
     */
    public static void debug(Logger logger, String message) {
        if (logger == null) {
            logger = defaultDebug;
        }
        if (logger.isDebugEnabled()) {
            logger.debug(message);
        }
    }

    /**
     * 记录程序员调试程序的信息。系统正常运行时，此方法所输出的信息需要被屏蔽掉，
     * 亦不记录到任何文件。
     *
     * @param message
     *            调试信息
     */
    public static void debug(Logger logger, String name, String value) {
        if (logger == null) {
            logger = defaultDebug;
        }
        if (logger.isDebugEnabled()) {
            logger.debug(name + "=" + value);
        }
    }

    static public void debug2(Logger logger, String message, Object... args) {
        if (logger == null) {
            logger = defaultDebug;
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format(message, args));
        }
    }

    public static void debug(Logger logger, String name, Object value) {
        if (logger == null) {
            logger = defaultDebug;
        }
        if (logger.isDebugEnabled()) {
            StringBuilder strBld = new StringBuilder(name);
            strBld.append("=");
            if (value == null) {
                strBld.append("<null>");
            } else if (value instanceof String) {
                strBld.append((String) value);
            } else {
                strBld.append(ToStringBuilder.reflectionToString(value));
            }
            logger.debug(strBld.toString());
        }
    }

    public static void debug(String name, Object value) {
        debug(defaultDebug, name, value);
    }

    /**
     * 记录系统运行时的信息。系统正常运行时将显示如下信息，以便系统管理员观察系统状态。建议程序员不要使用 该方法，系统框架人员可以使用该方法。
     *
     * @param message
     *            异常描述信息
     */
    public static void info(Logger logger, String message) {
        if (logger == null) {
            logger = defaultInfo;
        }
        if (logger.isInfoEnabled()) {
            logger.info(message);
        }
    }

    /**
     * 记录系统运行时的信息。系统正常运行时将显示如下信息，以便系统管理员观察系统状态。建议程序员不要使用 该方法，系统框架人员可以使用该方法。
     *
     * @deprecated
     * @param message
     *            异常描述信息
     */
    public static void info(String message) {
        if (defaultInfo.isInfoEnabled()) {
            defaultInfo.info(message);
        }
    }

    /**
     *
     * @deprecated
     * @param message
     * @param args
     */
    public static void info(String name, String value) {
        if (defaultInfo.isInfoEnabled()) {
            defaultInfo.info(name + "=" + value);
        }
    }

    /**
     *
     * @param message
     * @param args
     */
    public static void info(Logger logger, String name, Object value) {
        if (logger == null) {
            logger = defaultInfo;
        }
        if (logger.isInfoEnabled()) {
            logger.info(String.format("%s=%s", name, value));
        }
    }

    /**
     *
     * @param message
     * @param args
     */
    public static void info2(Logger logger, String message, Object... args) {
        if (logger == null) {
            logger = defaultInfo;
        }
        if (logger.isInfoEnabled()) {
            if (message == null) {
                return;
            }
            logger.info(String.format(message, args));
        }
    }

    /**
     * 记录系统错误或程序异常的信息。当系统发生错误或程序出现异常（因为程序的设计问题而不是用户的输入问题，但
     * 可能是因为用户的输入引起的）时使用该方法。建议程序员不要使用该方法，系统框架人员可以使用该方法
     *
     * @deprecated
     * @param message
     *            异常描述信息
     */
    public static void error(String message) {
        error.error("错误信息:" + message);
    }

    /**
     * 警告信息，系统出现了没有解决或者含义模糊等问题，系统不予处理，将继续运行。
     *
     * @deprecated
     * @param message
     *            异常描述信息
     */
    public static void warn(String message) {
        error.error("警告信息:" + message);
    }

    public static void warn(Logger logger, String message) {
        if (logger == null) {
            logger = error;
        }
        logger.warn("警告信息:" + message);
    }


    public static void warn2(Logger logger, Throwable t, String message, Object... args) {
        if (logger == null) {
            logger = error;
        }
        if (logger.isEnabledFor(Priority.WARN)) {
            if (message == null) {
                return;
            }

            logger.warn(String.format(message, convertSafely(args)), t);
        }
    }

    static private Object[] convertSafely(Object... args) {
        if (args == null || args.length == 0) {
            return new Object[0];
        }
        Object[] _args = new Object[args.length];
        for(int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg == null) {
                arg = "<null>";
            }
            _args[i] = arg;
        }
        return _args;
    }

    public static void warn2(Logger logger, String message, Object... args) {
        if (logger == null) {
            logger = error;
        }
        if (logger.isEnabledFor(Priority.WARN)) {
            if (message == null) {
                return;
            }
            logger.warn(String.format(message, args));
        }
    }

    /**
     * 警告信息，系统出现了没有解决或者含义模糊等问题，系统不予处理，将继续运行。
     *
     * @deprecated
     * @param message
     *            异常描述信息
     */
    public static void warn(String message, Throwable t) {
        error.error("警告信息:" + message, t);
    }

    public static void warn(Logger logger, String message, Throwable t) {
        if (logger == null) {
            logger = error;
        }
        logger.warn("警告信息:" + message, t);
    }

    /**
     * 记录错误信息的方法。当系统发生错误或程序出现异常（因为程序的设计问题而不是用户的输入问题，
     * 但可能是因为用户的输入引起的）时使用该方法。建议程序员不要使用该方法，系统框架人员可以使用该方法
     *
     * @deprecated
     * @param message
     *            异常描述信息
     * @param t
     *            异常的事例
     */
    public static void error(String message, Throwable t) {
        error.error(message, t);
    }

    public static void error(Logger logger, String message, Throwable t) {
        if (logger == null) {
            logger = error;
        }
        logger.error(message, t);
    }
}
