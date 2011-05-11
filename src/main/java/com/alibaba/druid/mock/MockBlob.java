package com.alibaba.druid.mock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;

public class MockBlob implements Blob {
	private ByteArrayOutputStream out = new ByteArrayOutputStream();

	@Override
	public long length() throws SQLException {
		return out.size();
	}

	@Override
	public byte[] getBytes(long pos, int length) throws SQLException {
		byte[] bytes = new byte[length];
		System.arraycopy(out.toByteArray(), (int) pos, bytes, 0, length);
		return bytes;
	}

	@Override
	public InputStream getBinaryStream() throws SQLException {
		return new ByteArrayInputStream(out.toByteArray());
	}

	@Override
	public long position(byte[] pattern, long start) throws SQLException {
		return 0;
	}

	@Override
	public long position(Blob pattern, long start) throws SQLException {
		return 0;
	}

	@Override
	public int setBytes(long pos, byte[] bytes) throws SQLException {
		return 0;
	}

	@Override
	public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException {
		return 0;
	}

	@Override
	public OutputStream setBinaryStream(long pos) throws SQLException {
		return null;
	}

	@Override
	public void truncate(long len) throws SQLException {

	}

	@Override
	public void free() throws SQLException {

	}

	@Override
	public InputStream getBinaryStream(long pos, long length) throws SQLException {
		return null;
	}

}
