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
package com.alibaba.druid.sql.repository;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateTableStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitorAdapter;
import com.alibaba.druid.util.JdbcUtils;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by wenshao on 03/06/2017.
 */
public class SchemaRepository {
    public Map<String, SchemaObject> objects = new ConcurrentSkipListMap<String, SchemaObject>();

    public final SchemaVisitor visitor = new SchemaVisitor();

    public SchemaObject findTable(String tableName) {
        String lowerName = tableName.toLowerCase();
        SchemaObject object = objects.get(lowerName);

        if (object != null && object.type == SchemaObject.Type.Table) {
            return object;
        }

        return null;
    }

    public void acceptDDL(String ddl, String dbType) {
        List<SQLStatement> stmtList = SQLUtils.parseStatements(ddl, dbType);
        for (SQLStatement stmt : stmtList) {
            accept(stmt);
        }
    }

    public void accept(SQLStatement stmt) {
        stmt.accept(visitor);
    }

    public boolean isSequence(String name) {
        SchemaObject object = objects.get(name);
        return object != null
                && object.type == SchemaObject.Type.Sequence;
    }

    public class SchemaVisitor extends OracleASTVisitorAdapter {

        public boolean visit(SQLDropSequenceStatement x) {
            String name = x.getName().getSimpleName();
            objects.remove(name);
            return false;
        }

        public boolean visit(SQLCreateSequenceStatement x) {
            String name = x.getName().getSimpleName();
            SchemaObject object = new SchemaObject(name, SchemaObject.Type.Sequence);

            objects.put(name.toLowerCase(), object);
            return false;
        }

        public boolean visit(OracleCreateTableStatement x) {
            visit((SQLCreateTableStatement) x);
            return false;
        }

        public boolean visit(SQLCreateTableStatement x) {
            String name = x.computeName();
            SchemaObject object = new SchemaObject(name, SchemaObject.Type.Table, x);

            objects.put(name.toLowerCase(), object);

            return false;
        }

        public boolean visit(SQLCreateIndexStatement x) {
            String name = x.getName().getSimpleName();
            SchemaObject object = new SchemaObject(name, SchemaObject.Type.Index);

            objects.put(name.toLowerCase(), object);

            return false;
        }
    }

    public SchemaObject findTable(SQLTableSource tableSource, String alias) {
        if (tableSource instanceof SQLExprTableSource) {
            if (alias.equalsIgnoreCase(tableSource.computeAlias())) {
                SQLExpr expr = ((SQLExprTableSource) tableSource).getExpr();
                if (expr instanceof SQLIdentifierExpr) {
                    String tableName = ((SQLIdentifierExpr) expr).getName();
                    SchemaObject tableObject = findTable(tableName);
                    return tableObject;
                }
            }
            return null;
        }

        if (tableSource instanceof SQLJoinTableSource) {
            SQLJoinTableSource join = (SQLJoinTableSource) tableSource;
            SQLTableSource left = join.getLeft();

            SchemaObject tableObject = findTable(left, alias);
            if (tableObject != null) {
                return tableObject;
            }

            SQLTableSource right = join.getRight();
            tableObject = findTable(right, alias);
            return tableObject;
        }

        return null;
    }
}
