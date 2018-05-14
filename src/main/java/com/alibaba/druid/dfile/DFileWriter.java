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
package com.alibaba.druid.dfile;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

public class DFileWriter {
    final static Charset UTF8 = Charset.forName("UTF-8");

    private final Column[] columns;
    private int rowCount;

    public DFileWriter(ResultSet rs) throws SQLException  {
        ResultSetMetaData meta = rs.getMetaData();
        final int columnCount = meta.getColumnCount();
        columns = new Column[columnCount];
        for (int i = 0; i < columnCount; ++i) {
            columns[i] = new Column(meta, i + 1);
        }

        byte[] b = new byte[1024 * 128];
        int off = 0;
        off += ((columnCount - 1) / 8);

        while (rs.next()) {
            for (int i = 0; i < columnCount; ++i) {
                Column column = columns[i];
                int columnIndex = i + 1;
                switch (column.type) {
                    case Types.TINYINT: {
                        byte val = rs.getByte(columnIndex);
                        b[off++] = val;
                        break;
                    }
                    case Types.SMALLINT: {
                        short val = rs.getShort(columnIndex);
                        b[off + 1] = (byte) (val);
                        b[off] = (byte) (val >>> 8);
                        off += 2;
                        break;
                    }
                    case Types.INTEGER: {
                        int val = rs.getInt(columnIndex);
                        b[off + 3] = (byte) (val);
                        b[off + 2] = (byte) (val >>> 8);
                        b[off + 1] = (byte) (val >>> 16);
                        b[off] = (byte) (val >>> 24);
                        off += 4;
                        break;
                    }
                    case Types.BIGINT: {
                        long val = rs.getLong(columnIndex);
                        b[off + 7] = (byte) (val);
                        b[off + 6] = (byte) (val >>> 8);
                        b[off + 5] = (byte) (val >>> 16);
                        b[off + 4] = (byte) (val >>> 24);
                        b[off + 3] = (byte) (val >>> 32);
                        b[off + 2] = (byte) (val >>> 40);
                        b[off + 1] = (byte) (val >>> 48);
                        b[off] = (byte) (val >>> 56);
                        off += 8;
                        break;
                    }
                    case Types.FLOAT: {
                        float floatVal = rs.getFloat(columnIndex);
                        int val = Float.floatToIntBits(floatVal);
                        b[off + 3] = (byte) (val);
                        b[off + 2] = (byte) (val >>> 8);
                        b[off + 1] = (byte) (val >>> 16);
                        b[off] = (byte) (val >>> 24);
                        off += 4;
                        break;
                    }
                    case Types.DOUBLE:
                        double doubleVal = rs.getDouble(columnIndex);
                        long val = Double.doubleToLongBits(doubleVal);
                        b[off + 7] = (byte) (val);
                        b[off + 6] = (byte) (val >>> 8);
                        b[off + 5] = (byte) (val >>> 16);
                        b[off + 4] = (byte) (val >>> 24);
                        b[off + 3] = (byte) (val >>> 32);
                        b[off + 2] = (byte) (val >>> 40);
                        b[off + 1] = (byte) (val >>> 48);
                        b[off] = (byte) (val >>> 56);
                        off += 8;
                        break;
                    case Types.VARCHAR: {
                        String str = rs.getString(columnIndex);
                        if (str != null) {
                            byte[] bytes = str.getBytes(UTF8);
                            int len = bytes.length;
                            if (len <= 256 * 256 * 256) {
                                b[off + 2] = (byte) (len);
                                b[off + 1] = (byte) (len >>> 8);
                                b[off + 0] = (byte) (len >>> 16);
                                off += 3;
                            } else {
                                throw new SQLException("TODO : too large string value. " + len);
                            }
                            System.arraycopy(bytes, 0, b, off, len);
                            off += len;
                        } else {
                            off += 3;
                        }
                        break;
                    }
                    case Types.DECIMAL: {
                        BigDecimal decimal = rs.getBigDecimal(columnIndex);
                        if (decimal != null) {
                            byte[] bytes = decimal.toString().getBytes(UTF8);
                            int len = bytes.length;
                            b[off + 1] = (byte) (len);
                            b[off + 0] = (byte) (len >>> 8);
                            off += 2;
                            System.arraycopy(bytes, 0, b, off, len);
                            off += len;
                        } else {
                            off += 2;
                        }
                        break;
                    }
                    case Types.DATE: {
                        break;
                    }
                    default:
                        throw new SQLException("TODO");
                }

                boolean wasNull = rs.wasNull();
                if (!wasNull) {
                    int nullFlagIndex = i >> 3;
                    b[nullFlagIndex] |= (1 << i);
                }
            }
        }
    }

    public static class Column {
        public final int index;
        public final int type;
        public final String name;
        public final int presion;
        public final int scale;

        public Column(ResultSetMetaData meta, int i) throws SQLException {
            index = i;
            type = meta.getColumnType(i);
            name = meta.getColumnName(i);
            presion = meta.getPrecision(i);
            scale = meta.getScale(i);
        }
    }

    public static class Row {
        private byte[] bytes;

        public Row(ResultSet rs, Column[] columns) {

        }
    }

    public static void main(String[] args) throws Exception {
//        for (int i = 0; i < 100; ++i) {
//            int val = (i - 1) / 8;
//            int v1 = (i - 1) >> 3;
//            System.out.println(val + " - " + i + " - " + v1);
//        }

//        System.out.println(256 * 256 * 256);
        byte[] bytes = "ALIDFILE".getBytes();
        System.out.println(bytes.length);
    }

    static long getLong(byte[] b, int off) {
        return ((b[off + 7] & 0xFFL)      ) +
                ((b[off + 6] & 0xFFL) <<  8) +
                ((b[off + 5] & 0xFFL) << 16) +
                ((b[off + 4] & 0xFFL) << 24) +
                ((b[off + 3] & 0xFFL) << 32) +
                ((b[off + 2] & 0xFFL) << 40) +
                ((b[off + 1] & 0xFFL) << 48) +
                (((long) b[off])      << 56);
    }

    static void putLong(byte[] b, int off, long val) {
        b[off + 7] = (byte) (val       );
        b[off + 6] = (byte) (val >>>  8);
        b[off + 5] = (byte) (val >>> 16);
        b[off + 4] = (byte) (val >>> 24);
        b[off + 3] = (byte) (val >>> 32);
        b[off + 2] = (byte) (val >>> 40);
        b[off + 1] = (byte) (val >>> 48);
        b[off    ] = (byte) (val >>> 56);
    }
}
