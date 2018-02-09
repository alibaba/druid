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
package com.alibaba.druid.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import javax.sql.XAConnection;

import org.postgresql.core.BaseConnection;
import org.postgresql.xa.PGXAConnection;

public class PGUtils {

    public static XAConnection createXAConnection(Connection physicalConn) throws SQLException {
        return new PGXAConnection((BaseConnection) physicalConn);
    }

    public static List<String> showTables(Connection conn) throws SQLException {
        List<String> tables = new ArrayList<String>();

        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT tablename FROM pg_catalog.pg_tables where schemaname not in ('pg_catalog', 'information_schema', 'sys')");
            while (rs.next()) {
                String tableName = rs.getString(1);
                tables.add(tableName);
            }
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
        }

        return tables;
    }

    private static Set<String> keywords;
    public static boolean isKeyword(String name) {
        if (name == null) {
            return false;
        }

        String name_lower = name.toLowerCase();

        Set<String> words = keywords;

        if (words == null) {
            words = new HashSet<String>();
            Utils.loadFromFile("META-INF/druid/parser/postgresql/keywords", words);
            keywords = words;
        }

        return words.contains(name_lower);
    }

    private final static long[] pseudoColumnHashCodes;
    static {
        long[] array = {
                FnvHash.Constants.CURRENT_TIMESTAMP
        };
        Arrays.sort(array);
        pseudoColumnHashCodes = array;
    }

    public static boolean isPseudoColumn(long hash) {
        return Arrays.binarySearch(pseudoColumnHashCodes, hash) >= 0;
    }
}
