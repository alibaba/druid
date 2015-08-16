/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.proxy.jdbc;

import java.sql.Types;
import java.util.Calendar;

public final class JdbcParameterNull implements JdbcParameter {

    private final int                     sqlType;

    public final static JdbcParameterNull CHAR      = new JdbcParameterNull(Types.CHAR);
    public final static JdbcParameterNull VARCHAR   = new JdbcParameterNull(Types.VARCHAR);
    public final static JdbcParameterNull NVARCHAR  = new JdbcParameterNull(Types.NVARCHAR);

    public final static JdbcParameterNull BINARY    = new JdbcParameterNull(Types.BINARY);
    public final static JdbcParameterNull VARBINARY = new JdbcParameterNull(Types.VARBINARY);

    public final static JdbcParameterNull TINYINT   = new JdbcParameterNull(Types.TINYINT);
    public final static JdbcParameterNull SMALLINT  = new JdbcParameterNull(Types.SMALLINT);
    public final static JdbcParameterNull INTEGER   = new JdbcParameterNull(Types.INTEGER);
    public final static JdbcParameterNull BIGINT    = new JdbcParameterNull(Types.BIGINT);

    public final static JdbcParameterNull DECIMAL   = new JdbcParameterNull(Types.DECIMAL);
    public final static JdbcParameterNull NUMERIC   = new JdbcParameterNull(Types.NUMERIC);
    public final static JdbcParameterNull FLOAT     = new JdbcParameterNull(Types.FLOAT);
    public final static JdbcParameterNull DOUBLE    = new JdbcParameterNull(Types.DOUBLE);

    public final static JdbcParameterNull NULL      = new JdbcParameterNull(Types.NULL);

    public final static JdbcParameterNull DATE      = new JdbcParameterNull(Types.DATE);
    public final static JdbcParameterNull TIME      = new JdbcParameterNull(Types.TIME);
    public final static JdbcParameterNull TIMESTAMP = new JdbcParameterNull(Types.TIMESTAMP);

    private JdbcParameterNull(int sqlType){
        this.sqlType = sqlType;
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public long getLength() {
        return 0;
    }

    @Override
    public Calendar getCalendar() {
        return null;
    }

    @Override
    public int getSqlType() {
        return sqlType;
    }

    public static JdbcParameterNull valueOf(int sqlType) {
        switch (sqlType) {
            case Types.TINYINT:
                return INTEGER;
            case Types.SMALLINT:
                return SMALLINT;
            case Types.INTEGER:
                return INTEGER;
            case Types.BIGINT:
                return BIGINT;

            case Types.DECIMAL:
                return DECIMAL;
            case Types.NUMERIC:
                return NUMERIC;
            case Types.FLOAT:
                return FLOAT;
            case Types.DOUBLE:
                return DOUBLE;

            case Types.CHAR:
                return CHAR;
            case Types.VARCHAR:
                return VARCHAR;
            case Types.NVARCHAR:
                return NVARCHAR;

            case Types.BINARY:
                return BINARY;
            case Types.VARBINARY:
                return VARBINARY;

            case Types.TIME:
                return TIME;
            case Types.DATE:
                return DATE;
            case Types.TIMESTAMP:
                return TIMESTAMP;

            case Types.NULL:
                return NULL;

            default:
                return new JdbcParameterNull(sqlType);
        }
    }
}
