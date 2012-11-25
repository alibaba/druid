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
package com.alibaba.druid.sharding.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.alibaba.druid.sharding.ShardingRuntimeException;
import com.alibaba.druid.sharding.config.MappingRule;
import com.alibaba.druid.sharding.config.RouteConfig;
import com.alibaba.druid.sharding.config.TablePartition;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;

public class MySqlShardingVisitor extends MySqlASTVisitorAdapter implements ShardingVisitor {

    private final RouteConfig  routeConfig;

    private final List<Object> parameters;

    private List<SQLStatement> result = new ArrayList<SQLStatement>(2);

    public MySqlShardingVisitor(RouteConfig routeConfig, List<Object> parameters){
        this.routeConfig = routeConfig;
        this.parameters = parameters;
    }

    public MySqlShardingVisitor(RouteConfig routeConfig, Object... parameters){
        this(routeConfig, Arrays.asList(parameters));
    }

    public RouteConfig getRouteConfig() {
        return routeConfig;
    }

    public List<Object> getParameters() {
        return parameters;
    }

    public boolean visit(MySqlInsertStatement x) {
        String table = x.getTableName().getSimleName();

        MappingRule mappingRule = routeConfig.getMappingRule(table);

        if (mappingRule == null) {
            result.add(x);
            return false;
        }

        if (x.getValues() == null) {
            throw new ShardingRuntimeException("sharding rule violation, insert's values clause is null");
        }

        String column = mappingRule.getColumn();

        int columnIndex = -1;
        for (int i = 0; i < x.getColumns().size(); ++i) {
            SQLExpr columnExpr = x.getColumns().get(i);
            if (columnExpr instanceof SQLIdentifierExpr) {
                String columnName = ((SQLIdentifierExpr) columnExpr).getName();
                if (column.equalsIgnoreCase(columnName)) {
                    columnIndex = i;
                    break;
                }
            }
        }

        if (columnIndex == -1) {
            throw new ShardingRuntimeException("sharding rule violation, columns not set : " + column);
        }

        SQLExpr valueExpr = x.getValues().getValues().get(columnIndex);
        Object value = SQLEvalVisitorUtils.eval(null, valueExpr, parameters);

        String partition = mappingRule.getPartition(value);

        if (partition == null) {
            throw new ShardingRuntimeException("sharding rule violation, partition not match, value : " + value);
        }

        TablePartition tablePartition = routeConfig.getPartition(table, partition);

        x.setTableName(new SQLIdentifierExpr(tablePartition.getTable()));
        
        if (tablePartition.getDatabase() != null) {
            x.putAttribute(ATTR_DB, tablePartition.getDatabase());
        }

        result.add(x);
        
        return false;
    }
}
