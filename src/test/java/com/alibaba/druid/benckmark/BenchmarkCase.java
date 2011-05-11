package com.alibaba.druid.benckmark;

public abstract class BenchmarkCase {
	private final String name;

	public BenchmarkCase(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setUp(SQLExecutor sqlExec) throws Exception {

	}

	public abstract void execute(SQLExecutor sqlExec) throws Exception;

	public void tearDown(SQLExecutor sqlExec) throws Exception {

	}
}
