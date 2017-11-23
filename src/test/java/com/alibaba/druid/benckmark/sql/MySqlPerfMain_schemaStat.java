/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.benckmark.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.test.TestUtils;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.Utils;

import java.lang.management.ManagementFactory;
import java.util.List;

public class MySqlPerfMain_schemaStat {

    public static void main(String[] args) throws Exception {
        System.out.println(System.getProperty("java.vm.name") + " " + System.getProperty("java.runtime.version"));
        List<String> arguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
        System.out.println(arguments);

        String sql = "select * from t ";
        for (int i = 0; i < 300; ++i) {
            if (i == 0) {
                sql += " WHERE ";
            } else {
                sql += " OR ";
            }
            sql += (" id = " + i);
        }
        
        for (int i = 0; i < 10; ++i) {
            perfMySql(sql);
        }
    }

    static long perfMySql(String sql) {
        long startYGC = TestUtils.getYoungGC();
        long startYGCTime = TestUtils.getYoungGCTime();
        long startFGC = TestUtils.getFullGC();

        long startMillis = System.currentTimeMillis();
        for (int i = 0; i < 100 * 1; ++i) {
            execMySql(sql);
        }
        long millis = System.currentTimeMillis() - startMillis;

        long ygc = TestUtils.getYoungGC() - startYGC;
        long ygct = TestUtils.getYoungGCTime() - startYGCTime;
        long fgc = TestUtils.getFullGC() - startFGC;

        System.out.println("MySql\t" + millis + ", ygc " + ygc + ", ygct " + ygct + ", fgc " + fgc);
        return millis;
    }

    static String execMySql(String sql) {
        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);

        for (SQLStatement stmt : statementList) {
            stmt.accept(statVisitor);
        }
        // for (SQLStatement statement : statementList) {
        // statement.accept(visitor);
        // visitor.println();
        // }
        return "";
    }
}
