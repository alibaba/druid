/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.support.spring;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.support.lob.LobCreator;

import com.alibaba.druid.util.JdbcUtils;

public class DruidLobCreator implements LobCreator {

    @Override
    public void setBlobAsBytes(PreparedStatement ps, int paramIndex, byte[] content) throws SQLException {
        Blob blob = ps.getConnection().createBlob();
        blob.setBytes(1, content);
        ps.setBlob(paramIndex, blob);
    }

    @Override
    public void setBlobAsBinaryStream(PreparedStatement ps, int paramIndex, InputStream contentStream, int contentLength)
                                                                                                                         throws SQLException {
        ps.setBlob(paramIndex, contentStream, contentLength);
    }

    @Override
    public void setClobAsString(PreparedStatement ps, int paramIndex, String content) throws SQLException {
        Clob clob = ps.getConnection().createClob();
        clob.setString(1, content);
        ps.setClob(paramIndex, clob);
    }

    @Override
    public void setClobAsAsciiStream(PreparedStatement ps, int paramIndex, InputStream asciiStream, int contentLength)
                                                                                                                      throws SQLException {
        if (asciiStream != null) {
            Clob clob = ps.getConnection().createClob();

            OutputStream out = clob.setAsciiStream(1);

            final int BUFFER_SIZE = 4096;
            try {
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead = -1;
                while ((bytesRead = asciiStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                out.flush();
            } catch (Exception e) {
                throw new SQLException("setClob error", e);
            } finally {
                JdbcUtils.close(asciiStream);
                JdbcUtils.close(out);
            }

            ps.setClob(paramIndex, clob);
        } else {
            ps.setClob(paramIndex, (Clob) null);
        }
    }

    @Override
    public void setClobAsCharacterStream(PreparedStatement ps, int paramIndex, Reader characterStream, int contentLength)
                                                                                                                         throws SQLException {
        ps.setClob(paramIndex, characterStream, contentLength);
    }

    @Override
    public void close() {

    }

}
