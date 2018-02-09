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
package com.alibaba.druid.support.opds.udf;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Column;
import com.alibaba.druid.stat.TableStat.Condition;
import com.alibaba.druid.support.json.JSONUtils;
import com.aliyun.odps.udf.UDF;

public class ExportConditions extends UDF {

    public String evaluate(String sql) {
        return evaluate(sql, null);
    }
    
    public String evaluate(String sql, String dbType) {
        return evaluate(sql, dbType, null);
    }

    public String evaluate(String sql, String dbType, Boolean compactValues) {
        try {
            List<SQLStatement> statementList = SQLUtils.parseStatements(sql, dbType);
            SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(dbType);

            for (SQLStatement stmt : statementList) {
                stmt.accept(visitor);
            }

            List<List<Object>> rows = new ArrayList<List<Object>>();
            List<Condition> conditions = visitor.getConditions();
            for (int i = 0; i < conditions.size(); ++i) {
                TableStat.Condition condition = conditions.get(i);
                Column column = condition.getColumn();
                String operator = condition.getOperator();
                List<Object> values = condition.getValues();
                
                List<Object> row = new ArrayList<Object>();
                row.add(column.getTable());
                row.add(column.getName());
                row.add(operator);
                if (values.size() == 0) {
                    row.add(null);
                } else if (values.size() == 1) {
                    if (compactValues != null && compactValues.booleanValue()) {
                        row.add(values);                        
                    } else {
                        row.add(values.get(0));
                    }
                } else {
                    row.add(values);
                }
                rows.add(row);
            }

            return JSONUtils.toJSONString(rows);
        } catch (Throwable ex) {
            System.err.println("error sql : " + sql);
            ex.printStackTrace();
            return null;
        }
    }
}
