package com.xt.core.proc;

public interface ProcessorFactory {
	
	public static final String PROCESSOR_FACTORIES = "processorFactories";
	
	/**
	 * 系统初始化时进行处理（只调用一次）。
	 */
	public void onInit();
	
	/**
	 * 创建一个处理器对象。
	 * @return
	 */
	public Processor createProcessor(Class serviceClass);
	
	
	/**
	 * 应用系统关闭时将调用此方法。
	 */
	public void onDestroy ();

}
