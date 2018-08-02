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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.jdbc.support.nativejdbc.NativeJdbcExtractorAdapter;

public class DruidNativeJdbcExtractor extends NativeJdbcExtractorAdapter {

    protected Connection doGetNativeConnection(Connection con) throws SQLException {
        return (Connection) con.unwrap(Connection.class);
    }

    public Statement getNativeStatement(Statement stmt) throws SQLException {
        return (Statement) stmt.unwrap(Statement.class);
    }

    public PreparedStatement getNativePreparedStatement(PreparedStatement ps) throws SQLException {
        return (PreparedStatement) ps.unwrap(PreparedStatement.class);
    }

    public CallableStatement getNativeCallableStatement(CallableStatement cs) throws SQLException {
        return (CallableStatement) cs.unwrap(CallableStatement.class);
    }

    public ResultSet getNativeResultSet(ResultSet rs) throws SQLException {
        return (ResultSet) rs.unwrap(ResultSet.class);
    }
}
