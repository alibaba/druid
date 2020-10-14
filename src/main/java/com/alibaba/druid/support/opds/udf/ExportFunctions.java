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
package com.alibaba.druid.support.opds.udf;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.aliyun.odps.udf.UDF;

import java.util.List;

public class ExportFunctions extends UDF {

    public String evaluate(String sql) throws Exception {
        return evaluate(sql, null, false);
    }

    public String evaluate(String sql, String dbTypeName) throws Exception {
        return evaluate(sql, dbTypeName, false);
    }

    public String evaluate(String sql, String dbTypeName, boolean throwError) throws Exception {
        DbType dbType = dbTypeName == null ? null : DbType.valueOf(dbTypeName);

        try {
            List<SQLStatement> statementList = SQLUtils.parseStatements(sql, dbType);
            SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);

            for (SQLStatement stmt : statementList) {
                stmt.accept(visitor);
            }

            StringBuffer buf = new StringBuffer();

            List<SQLMethodInvokeExpr> functions = visitor.getFunctions();
            for (int i = 0; i < functions.size(); i++) {
                if (i != 0) {
                    buf.append(",");
                }
                SQLMethodInvokeExpr func = functions.get(i);
                String funcName = func.getMethodName();
                buf.append(funcName);
            }

            for (int i = 0; i < visitor.getAggregateFunctions().size(); i++) {
                if (buf.length() > 0) {
                    buf.append(",");
                }

                SQLAggregateExpr func = visitor.getAggregateFunctions().get(i);
                String funcName = func.getMethodName();
                buf.append(funcName);
            }

            return buf.toString();
        } catch (Exception ex) {
            System.err.println("error sql : " + sql);
            ex.printStackTrace();

            if (throwError) {
                throw ex;
            }

            return null;
        }
    }
}
