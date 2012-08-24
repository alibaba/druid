/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.test;

import java.util.Arrays;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

public class TestUtils {

    public static String outputOracle(List<SQLStatement> stmtList) {
        StringBuilder out = new StringBuilder();
        OracleOutputVisitor visitor = new OracleOutputVisitor(out);

        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }

        return out.toString();
    }

    public static String outputSqlServer(List<SQLStatement> stmtList) {
        StringBuilder out = new StringBuilder();
        SQLServerOutputVisitor visitor = new SQLServerOutputVisitor(out);

        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }

        return out.toString();
    }

    public static String outputOracle(SQLStatement... stmtList) {
        return outputOracle(Arrays.asList(stmtList));
    }

    public static String outputSqlServer(SQLStatement... stmtList) {
        return outputSqlServer(Arrays.asList(stmtList));
    }

    public static String output(SQLStatement... stmtList) {
        return output(Arrays.asList(stmtList));
    }

    public static String output(List<SQLStatement> stmtList) {
        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = new SQLASTOutputVisitor(out);

        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
        }

        return out.toString();
    }
}
