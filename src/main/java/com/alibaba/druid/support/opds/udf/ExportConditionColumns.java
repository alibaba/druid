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
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.aliyun.odps.udf.UDF;

import java.util.List;

public class ExportConditionColumns extends UDF {

    public String evaluate(String sql) {
        return evaluate(sql, null);
    }

    public String evaluate(String sql, String dbTypeName) {
        DbType dbType = dbTypeName == null ? null : DbType.valueOf(dbTypeName);
        try {
            List<SQLStatement> statementList = SQLUtils.parseStatements(sql, dbType);
            SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);

            for (SQLStatement stmt : statementList) {
                stmt.accept(visitor);
            }

            StringBuffer buf = new StringBuffer();

            for (TableStat.Column column : visitor.getColumns()) {
                if ((!column.isWhere()) && !column.isJoin()) {
                    continue;
                }
                
                if (buf.length() != 0) {
                    buf.append(',');
                }
                buf.append(column.toString());
            }

            return buf.toString();
        } catch (Exception ex) {
            System.err.println("error sql : " + sql);
            ex.printStackTrace();
            return null;
        }
    }
}
