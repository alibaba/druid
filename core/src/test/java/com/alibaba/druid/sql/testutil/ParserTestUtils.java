/*
 * Copyright 1999-2026 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.testutil;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import java.util.List;

public final class ParserTestUtils {
    private ParserTestUtils() {
    }

    public static SQLStatement parseSingleStatement(String sql, String dbType) {
        List<SQLStatement> statements = SQLUtils.parseStatements(sql, dbType);
        return getSingleStatement(statements);
    }

    public static SQLStatement parseSingleStatement(String sql, DbType dbType) {
        List<SQLStatement> statements = SQLUtils.parseStatements(sql, dbType);
        return getSingleStatement(statements);
    }

    private static SQLStatement getSingleStatement(List<SQLStatement> statements) {
        if (statements.size() != 1) {
            throw new IllegalStateException("expect 1 statement, but was " + statements.size());
        }
        return statements.get(0);
    }

    public static String formatFirstStatement(String sql, String dbType, SQLParserFeature... features) {
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType, features);
        List<SQLStatement> statements = parser.parseStatementList();
        if (statements.isEmpty()) {
            throw new IllegalStateException("no parsed statements");
        }
        return statements.get(0).toString();
    }
}
