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
package com.alibaba.druid.bvt.proxy;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockResultSet;
import com.alibaba.druid.mock.MockResultSetMetaData;
import com.alibaba.druid.mock.MockStatement;
import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.util.Utils;
import com.alibaba.druid.util.JdbcUtils;

public class JdbcUtilsTest extends TestCase {
    protected void tearDown() throws Exception {
        DruidDriver.getProxyDataSources().clear();
        Assert.assertEquals(0, JdbcStatManager.getInstance().getSqlList().size());
    }
    
    public void test_print() throws Exception {
        final AtomicInteger nextCount = new AtomicInteger(2);

        final MockResultSetMetaData rsMeta = new MockResultSetMetaData() {

            private int[] types = new int[] { Types.DATE, Types.BIT, Types.BOOLEAN, Types.TINYINT,

                                Types.SMALLINT, Types.INTEGER, Types.CLOB, Types.LONGVARCHAR, Types.OTHER,

                                Types.VARCHAR, Types.CHAR, Types.NVARCHAR, Types.NCHAR

                                };

            @Override
            public int getColumnCount() throws SQLException {
                return types.length;
            }

            @Override
            public int getColumnType(int column) throws SQLException {
                return types[column - 1];
            }

            @Override
            public String getColumnName(int column) throws SQLException {
                return "C" + column;
            }
        };

        MockResultSet rs = new MockResultSet(null) {

            @Override
            public boolean next() throws SQLException {
                return nextCount.getAndDecrement() > 0;
            }

            @Override
            public ResultSetMetaData getMetaData() throws SQLException {
                return rsMeta;
            }

            @Override
            public java.sql.Date getDate(int columnIndex) throws SQLException {
                return new java.sql.Date(System.currentTimeMillis());
            }

            @Override
            public boolean wasNull() throws SQLException {
                if (nextCount.get() == 1) {
                    return true;
                }
                return false;
            }

            public Object getObject(int columnIndex) throws SQLException {
                return null;
            }
        };

        JdbcUtils.printResultSet(rs);
    }

    public void test_close() throws Exception {
        JdbcUtils.close((Connection) null);
        JdbcUtils.close((Statement) null);
        JdbcUtils.close((ResultSet) null);

        JdbcUtils.close(new MockConnection() {

            @Override
            public void close() throws SQLException {
                throw new SQLException();
            }
        });
        JdbcUtils.close(new MockStatement(null) {

            @Override
            public void close() throws SQLException {
                throw new SQLException();
            }
        });
        JdbcUtils.close(new MockResultSet(null) {

            @Override
            public void close() throws SQLException {
                throw new SQLException();
            }
        });
        JdbcUtils.close(new Closeable() {

            @Override
            public void close() throws IOException {
                throw new IOException();
            }
        });
        JdbcUtils.close(new Closeable() {

            @Override
            public void close() throws IOException {
            }
        });
        JdbcUtils.close((Closeable) null);

        new JdbcUtils();
    }

    public void test_getTypeName() {
        JdbcUtils.getTypeName(Types.ARRAY);
        JdbcUtils.getTypeName(Types.BIGINT);
        JdbcUtils.getTypeName(Types.BINARY);
        JdbcUtils.getTypeName(Types.BIT);
        JdbcUtils.getTypeName(Types.BLOB);
        JdbcUtils.getTypeName(Types.BOOLEAN);
        JdbcUtils.getTypeName(Types.CHAR);
        JdbcUtils.getTypeName(Types.CLOB);
        JdbcUtils.getTypeName(Types.DATALINK);
        JdbcUtils.getTypeName(Types.DATE);
        JdbcUtils.getTypeName(Types.DECIMAL);
        JdbcUtils.getTypeName(Types.DISTINCT);
        JdbcUtils.getTypeName(Types.DOUBLE);
        JdbcUtils.getTypeName(Types.FLOAT);
        JdbcUtils.getTypeName(Types.INTEGER);
        JdbcUtils.getTypeName(Types.JAVA_OBJECT);
        JdbcUtils.getTypeName(Types.LONGNVARCHAR);
        JdbcUtils.getTypeName(Types.LONGVARBINARY);
        JdbcUtils.getTypeName(Types.NCHAR);
        JdbcUtils.getTypeName(Types.NCLOB);
        JdbcUtils.getTypeName(Types.NULL);
        JdbcUtils.getTypeName(Types.NUMERIC);
        JdbcUtils.getTypeName(Types.NVARCHAR);
        JdbcUtils.getTypeName(Types.REAL);
        JdbcUtils.getTypeName(Types.REF);
        JdbcUtils.getTypeName(Types.ROWID);
        JdbcUtils.getTypeName(Types.SMALLINT);
        JdbcUtils.getTypeName(Types.SQLXML);
        JdbcUtils.getTypeName(Types.STRUCT);
        JdbcUtils.getTypeName(Types.TIME);
        JdbcUtils.getTypeName(Types.TIMESTAMP);
        JdbcUtils.getTypeName(Types.TINYINT);
        JdbcUtils.getTypeName(Types.VARBINARY);
        JdbcUtils.getTypeName(Types.VARCHAR);
        JdbcUtils.getTypeName(Types.OTHER);
    }

    public void test_read() throws Exception {
        {
            Exception error = null;
            try {
                Utils.read(new Reader() {

                    @Override
                    public int read(char[] cbuf, int off, int len) throws IOException {
                        throw new IOException();
                    }

                    @Override
                    public void close() throws IOException {
                        throw new IOException();
                    }

                });
            } catch (RuntimeException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }
        {
            Exception error = null;
            try {
                Utils.read(new Reader() {

                    @Override
                    public int read(char[] cbuf, int off, int len) throws IOException {
                        throw new IOException();
                    }

                    @Override
                    public void close() throws IOException {
                        throw new IOException();
                    }

                }, 0);
            } catch (RuntimeException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        {
            String text = Utils.read(new Reader() {

                @Override
                public int read(char[] cbuf, int off, int len) throws IOException {
                    return -1;
                }

                @Override
                public void close() throws IOException {
                    throw new IOException();
                }

            }, 1);
            Assert.assertEquals("", text);
        }
        {
            String text = Utils.read(new Reader() {

                @Override
                public int read(char[] cbuf, int off, int len) throws IOException {
                    for (int i = off; i < len; ++i) {
                        cbuf[i] = 'A';
                    }
                    return len;
                }

                @Override
                public void close() throws IOException {
                    throw new IOException();
                }

            }, 2);
            Assert.assertEquals("AA", text);
        }
        {
            Reader reader = new Reader() {

                @Override
                public int read(char[] cbuf, int off, int len) throws IOException {
                    cbuf[off] = 'A';
                    return 1;
                }

                @Override
                public void close() throws IOException {
                    throw new IOException();
                }

            };
            String text = Utils.read(reader, 2);
            Assert.assertEquals("AA", text);
        }
    }

}
