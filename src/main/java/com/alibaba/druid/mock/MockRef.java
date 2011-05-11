package com.alibaba.druid.mock;

import java.sql.Ref;
import java.sql.SQLException;
import java.util.Map;

public class MockRef implements Ref {
	private String baseTypeName;
	private Object object;

	public void setBaseTypeName(String baseTypeName) {
		this.baseTypeName = baseTypeName;
	}

	@Override
	public String getBaseTypeName() throws SQLException {
		return baseTypeName;
	}

	@Override
	public Object getObject(Map<String, Class<?>> map) throws SQLException {
		return object;
	}

	@Override
	public Object getObject() throws SQLException {
		return object;
	}

	@Override
	public void setObject(Object value) throws SQLException {
		this.object = value;
	}

}
