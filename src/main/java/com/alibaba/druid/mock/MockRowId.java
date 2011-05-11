package com.alibaba.druid.mock;

import java.sql.RowId;

public class MockRowId implements RowId {
	private byte[] bytes;

	public MockRowId() {
	}

	public MockRowId(byte[] bytes) {
		this.bytes = bytes;
	}

	@Override
	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

}
