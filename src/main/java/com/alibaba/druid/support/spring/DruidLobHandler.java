package com.alibaba.druid.support.spring;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.support.lob.AbstractLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;

public class DruidLobHandler extends AbstractLobHandler {

    public byte[] getBlobAsBytes(ResultSet rs, int columnIndex) throws SQLException {
        Blob blob = rs.getBlob(columnIndex);

        if (blob == null) {
            return null;
        }

        return blob.getBytes(1, (int) blob.length());
    }

    public InputStream getBlobAsBinaryStream(ResultSet rs, int columnIndex) throws SQLException {
        Blob blob = rs.getBlob(columnIndex);

        if (blob == null) {
            return null;
        }

        return blob.getBinaryStream();
    }

    public String getClobAsString(ResultSet rs, int columnIndex) throws SQLException {
        Clob clob = rs.getClob(columnIndex);

        if (clob == null) {
            return null;
        }

        return clob.getSubString(1, (int) clob.length());
    }

    public InputStream getClobAsAsciiStream(ResultSet rs, int columnIndex) throws SQLException {
        Clob clob = rs.getClob(columnIndex);

        if (clob == null) {
            return null;
        }

        return clob.getAsciiStream();
    }

    public Reader getClobAsCharacterStream(ResultSet rs, int columnIndex) throws SQLException {
        Clob clob = rs.getClob(columnIndex);

        if (clob == null) {
            return null;
        }

        return clob.getCharacterStream();
    }

    @Override
    public LobCreator getLobCreator() {
        return new DruidLobCreator();
    }

}
