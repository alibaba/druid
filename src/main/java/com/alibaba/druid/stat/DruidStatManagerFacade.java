package com.alibaba.druid.stat;

import java.util.List;
import java.util.Map;

/**
 * 监控相关的对外数据暴露
 * 
 * <pre>
 * 1. 为了支持jndi数据源本类内部调用druid相关对象均需要反射调用,返回值也应该是Object,List<Object>,Map<String,Object>等无关于druid的类型
 * 2. 对外暴露的public方法都应该先调用init()，应该有更好的方式，暂时没想到
 * </pre>
 * 
 * @author sandzhang<sandzhangtoo@gmail.com>
 */
public class DruidStatManagerFacade {

	private final static DruidStatManagerFacade instance = new DruidStatManagerFacade();

	private static DruidDataSourceStatStrategyContext ctx = null; 

	private static DruidDataSourceStatStrategy strategy = null;

	static {
		strategy = new DruidDataSourceStatJNDIStatStrategy();
		ctx = new DruidDataSourceStatStrategyContext(strategy);
	}

	private DruidStatManagerFacade() {
	}

	public static DruidStatManagerFacade getInstance() {
		return instance;
	}

	public Map<String, Object> getSqlStatData(Integer id) {
		return ctx.getSqlStatData(id);
	}

	public List<Map<String, Object>> getSqlStatDataList() {
		return ctx.getSqlStatDataList();
	}

	public List<String> getActiveConnectionStackTraceByDataSourceId(Integer id) {
		return ctx.getActiveConnectionStackTraceByDataSourceId(id);
	}

	public Map<String, Object> returnJSONBasicStat() {
		return ctx.returnJSONBasicStat();
	}

	public List<Object> getDataSourceStatList() {
		return ctx.getDataSourceStatList();
	}

	public Map<String, Object> getDataSourceStatData(Integer id) {
		return ctx.getDataSourceStatData(id);
	}

	public List<Map<String, Object>> getPoolingConnectionInfoByDataSourceId(Integer id) {
		return ctx.getPoolingConnectionInfoByDataSourceId(id);
	}

	public void resetAll() {
		ctx.resetAll();
	}
}
