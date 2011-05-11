package com.alibaba.druid.mock;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.SQLException;

public class MockClob implements Clob {
	private byte[] bytes;

	public MockClob() {
		this(new byte[0]);
	}

	public MockClob(byte[] bytes) {
		this.bytes = bytes;
	}

	@Override
	public long length() throws SQLException {
		return bytes.length;
	}

	@Override
	public String getSubString(long pos, int length) throws SQLException {
		return new String(bytes, (int) pos, length);
	}

	@Override
	public Reader getCharacterStream() throws SQLException {
		return null;
	}

	@Override
	public InputStream getAsciiStream() throws SQLException {
		return new ByteArrayInputStream(bytes);
	}

	@Override
	public long position(String searchstr, long start) throws SQLException {
		if (bytes.length == 0) {
			return 0;
		}
		
		return new String(bytes).indexOf(searchstr);
	}

	@Override
	public long position(Clob searchstr, long start) throws SQLException {
		return 0;
	}

	@Override
	public int setString(long pos, String str) throws SQLException {
		return 0;
	}

	@Override
	public int setString(long pos, String str, int offset, int len) throws SQLException {
		return 0;
	}

	@Override
	public OutputStream setAsciiStream(long pos) throws SQLException {
		return null;
	}

	@Override
	public Writer setCharacterStream(long pos) throws SQLException {
		return null;
	}

	@Override
	public void truncate(long len) throws SQLException {

	}

	@Override
	public void free() throws SQLException {

	}

	@Override
	public Reader getCharacterStream(long pos, long length) throws SQLException {
		return null;
	}
}
