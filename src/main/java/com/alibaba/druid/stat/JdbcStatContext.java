package com.alibaba.druid.stat;


public class JdbcStatContext {
	private String name;
	private String file;
	private String requestId;

	private boolean traceEnable;
	
	public JdbcStatContext() {
		
	}

	public boolean isTraceEnable() {
		return traceEnable;
	}

	public void setTraceEnable(boolean traceEnable) {
		this.traceEnable = traceEnable;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

}
