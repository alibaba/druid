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
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsNewExpr;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsASTVisitor;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.aliyun.odps.udf.UDF;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
            List<SQLStatement> statementList = SQLUtils.parseStatements(sql, dbType
                    , SQLParserFeature.EnableMultiUnion
                    , SQLParserFeature.EnableSQLBinaryOpExprGroup);
            Visitor visitor = new Visitor();

            for (SQLStatement stmt : statementList) {
                stmt.accept(visitor);
            }

            StringBuffer buf = new StringBuffer();

            List<SQLMethodInvokeExpr> functions = visitor.getFunctions();
            Set<String> functionSet = new LinkedHashSet<>();
            for (int i = 0; i < functions.size(); i++) {
                functionSet.add(functions.get(i).getMethodName());
            }

            for (String funcName : functionSet) {
                if (funcName.length() > 2 && funcName.charAt(0) == '`' && funcName.charAt(funcName.length() - 1) == '`') {
                    funcName = funcName.substring(1, funcName.length() - 1);
                }
                if (buf.length() != 0) {
                    buf.append(",");
                }

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
        } catch (StackOverflowError ex) {
            System.err.println("error sql : " + sql);
            ex.printStackTrace();

            if (throwError) {
                throw ex;
            }

            return null;
        }
    }

    public static class Visitor extends SQLASTVisitorAdapter implements OdpsASTVisitor {
        private final List<SQLMethodInvokeExpr> functions = new ArrayList<SQLMethodInvokeExpr>();

        public boolean visit(SQLMethodInvokeExpr x) {
            functions.add(x);
            return true;
        }

        public boolean visit(SQLAggregateExpr x) {
            functions.add(x);
            return true;
        }

        public boolean visit(OdpsNewExpr x) {
            functions.add(x);
            return true;
        }

        public List<SQLMethodInvokeExpr> getFunctions() {
            return functions;
        }

        public boolean visit(SQLCastExpr x) {
            functions.add(new SQLMethodInvokeExpr("CAST"));
            return true;
        }

        public boolean visit(SQLBetweenExpr x) {
            functions.add(new SQLMethodInvokeExpr("BETWEEN"));
            return true;
        }

        public boolean visit(SQLCaseExpr x) {
            functions.add(new SQLMethodInvokeExpr("CASE"));
            return true;
        }

        public boolean visit(SQLInListExpr x) {
            functions.add(new SQLMethodInvokeExpr("IN"));
            return true;
        }
    }
}
