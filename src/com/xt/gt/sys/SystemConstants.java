package com.xt.gt.sys;

/**
 * 定义框架需要的所有常量。
 * <p>Title: XT框架-事务逻辑部分</p>
 * <p>Description:  </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: </p>
 * @author 郑伟
 * @version 1.0
 * @date 2006-10-31
 */
public class SystemConstants {
	
    /**
     * 系统默认的日期格式
     */
	public static final String DATE_FORMAT = "dateFormat";
	
	/**
     * 系统默认的日期时间格式
     */
	public static final String DATE_TIME_FORMAT = "dateTimeFormat";
	

	/**
	 * @deprecated
	 * 持久化处理器
	 */
	public static final String PERSISTENCE_MANAGER = "sysPersistenceManager";

	/**
	 * 系统定义的数据库映射处理器.如果用户没有指定,将采用系统默认的处理方式,DefaultDBMapping.
	 */
	public static final String DATABASE_MAPPING = "sysDatabaseMapping";

	/**
	 * JSP文件存放的基础路径,一般来说,这个路径是相对于Web的应用路径的.
	 *  注意:此路径一定要以"/"结尾.
	 */
	public static final String JSP_BASE_PATH = "sysJspBasePath";

	/**
	 * 事务请求的后缀,需要与web.xml中的Servlet后缀一致,默认为".do"
	 */
	public static final String REQUEST_SUBFIX = "sysRequestSubfix";

	/**
	 * 请求参数的名称,默认为“method”。
	 */
	public static final String REQUEST_METHOD_PARAM = "sysRequestMethodParam";

	/**
	 * 存放类文件的基础包,一般情况下是公司名称的缩写和项目名称,如"com.xxx.assets"
	 */
	public static final String BASE_PACKAGE = "sysBasePackage";

	/**
	 * 事务处理方法的后缀,默认为"Service"
	 */
	public static final String SERVICE_SUBFIX = "sysServiceSubfix";

	/**
	 * 用于存放当前请求的解析后的ProcessParams对象
	 */
	public static final String CURRENT_PROCESS_PARAMS = "SYS_CURRENT_PROCESS_PARAMS";

	/**
	 * 用于存放具体的屏幕流管理实现类名称的参数
	 */
	public static final String SCREEN_FLOW = "sysScreenFlows";
	
	/**
	 * 用于存放具体的数据源的参数
	 */
	public static final String DATABASE_CONTEXT = "DATABASE_CONTEXT";

	/**
	 * 用于存放具体的JSP基础路径
	 */
	public static final String SCREEN_FLOW_BASE_PATH = "sysScreenFlowBasePath";
	
	/**
	 * 用户自定义的事务处理映射接口
	 */
	public static final String BIZ_HANDLER_MAPPING = "sysBizHandlerMapping";
	
	/**
	 * 用户自定义的结果处理器
	 */
	public static final String RESULT_PROCESSORS = "resultProcessors";
	
	/**
	 * @deprecated
	 * 排序处理器
	 */
	public static final String SORT_PROCESSOR = "sortProcessor";

	/**
	 * @deprecated
	 * 翻页处理器
	 */
	public static final String PAGINATION_PROCESSOR = "paginationProcessor";
	
	/**
	 * 每次翻页的行数
	 */
	public static final String PAGINATION_PAGE_SIZE = "paginationPageSize";
	
	/**
	 * 翻页的起始行数
	 */
	public static final String PAGINATION_START_INDEX = "paginationStartIndex";
	
	/**
	 * 记录的总行数
	 */
	public static final String PAGINATION_TOTAL_ROWS = "paginationTotalRows";
	
	/**
	 * 装载屏幕流文件的配置文件
	 */
	public static final String SCREEN_FLOW_FILE = "screenFlowFile";
	
	/**
	 * 数据库专有特性的实现类
	 */
	public static final String DATABASE_DIALECT = "databaseDialect";
	
//	/**
//	 * 输入域控制器
//	 */
//	public static final String FIELD_CONTROLLER = "FIELD_CONTROLLER";
	
	/**
	 * 用于数值比较（浮点或者双精度）的比较精度的参数名称
	 */
	public static final String NUMBER_COMPARATOR_PRECISION = "doubleComparatorPrecision";
	
	/**
	 * 表格的奇数行的颜色
	 */
	public static final String ODD_ROW_COLOR = "oddRowColor";
	
	
	/**
	 * 表格的偶数行的颜色
	 */
	public static final String EVEN_ROW_COLOR = "evenRowColor";
	
	/**
	 * 表格的偶数行的颜色
	 */
	public static final String DEFAULT_CELL_RENDERER_DECORATOR = "cellRendererDecorator";

    /**
     * 应用上下文，一般情况下，对于Web应用来说，是指Web的发布路径；
     * 对于客户端应用，即程序运行的当前路径。
     */
	public static final String APP_CONTEXT = "appContext";

    /**
     * 系统的生命周期调用接口。注意：和每个Servlet时的生命周期一致。
     */
    public static final String appLifecycles = "appLifecycles";
	
}
