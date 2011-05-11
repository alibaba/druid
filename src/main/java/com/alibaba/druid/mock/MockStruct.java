package com.alibaba.druid.mock;

import java.sql.SQLException;
import java.sql.Struct;
import java.util.Map;

public class MockStruct implements Struct {

	@Override
	public String getSQLTypeName() throws SQLException {
		return null;
	}

	@Override
	public Object[] getAttributes() throws SQLException {
		return null;
	}

	@Override
	public Object[] getAttributes(Map<String, Class<?>> map) throws SQLException {
		return null;
	}

}
