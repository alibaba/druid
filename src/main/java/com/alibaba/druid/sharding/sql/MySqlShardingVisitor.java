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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.sharding.ShardingRuntimeException;
import com.alibaba.druid.sharding.config.MappingRule;
import com.alibaba.druid.sharding.config.RouteConfig;
import com.alibaba.druid.sharding.config.TablePartition;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitorAdapter;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;
import com.alibaba.druid.util.JdbcConstants;

public class MySqlShardingVisitor extends MySqlASTVisitorAdapter implements ShardingVisitor {

    private final RouteConfig  routeConfig;

    private final List<Object> parameters;

    private List<SQLStatement> result = new ArrayList<SQLStatement>(2);

    private SQLStatement       input  = null;

    public MySqlShardingVisitor(RouteConfig routeConfig, List<Object> parameters){
        this.routeConfig = routeConfig;
        this.parameters = parameters;
    }

    public MySqlShardingVisitor(RouteConfig routeConfig, Object... parameters){
        this(routeConfig, Arrays.asList(parameters));
    }

    public List<SQLStatement> getResult() {
        return result;
    }

    public RouteConfig getRouteConfig() {
        return routeConfig;
    }

    public List<Object> getParameters() {
        return parameters;
    }

    public boolean visit(SQLSelectStatement x) {
        input = x;
        return true;
    }

    public boolean visit(MySqlDeleteStatement x) {
        input = x;

        if (x.getFrom() != null) {
            x.getFrom().setParent(x);
            x.getFrom().accept(this);
        }

        if (x.getTableSource() != null) {
            x.getTableSource().setParent(x);
            x.getTableSource().accept(this);
        }

        if (x.getWhere() != null) {
            x.getWhere().setParent(x);
            x.getWhere().accept(this);
        }

        result.add(x);

        return false;
    }

    @Override
    public boolean visit(MySqlUpdateStatement x) {
        input = x;

        if (x.getTableSource() != null) {
            x.getTableSource().setParent(x);
            x.getTableSource().accept(this);
        }

        if (x.getWhere() != null) {
            x.getWhere().setParent(x);
            x.getWhere().accept(this);
        }

        result.add(x);
        return false;
    }

    public boolean visit(MySqlInsertStatement x) {
        input = x;
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

    @Override
    public boolean visit(MySqlSelectQueryBlock x) {
        if (x.getFrom() != null) {
            x.getFrom().setParent(x);
            x.getFrom().accept(this);
        }

        if (x.getWhere() != null) {
            x.getWhere().setParent(x);
            x.getWhere().accept(this);
        }

        return false;
    }

    @Override
    public boolean visit(SQLJoinTableSource x) {
        x.getLeft().setParent(x);
        x.getRight().setParent(x);

        if (x.getCondition() != null) {
            x.getCondition().setParent(x);
        }

        return true;
    }

    @Override
    public boolean visit(SQLExprTableSource x) {
        Map<String, SQLTableSource> aliasMap = getAliasMap(x);

        if (aliasMap != null) {
            if (x.getAlias() != null) {
                aliasMap.put(x.getAlias(), x);
            }

            if (x.getExpr() instanceof SQLIdentifierExpr) {
                String tableName = ((SQLIdentifierExpr) x.getExpr()).getName();
                aliasMap.put(tableName, x);
            }
        }

        return false;
    }

    @Override
    public boolean visit(SQLBinaryOpExpr x) {
        x.getLeft().setParent(x);
        x.getRight().setParent(x);

        x.getLeft().accept(this);
        x.getRight().accept(this);

        String column = null;
        if ((column = getColumn(x.getLeft())) != null && isValue(x.getRight())) {
            boolean isMappingColumn = false;
            SQLTableSource tableSource = getBinaryOpExprLeftOrRightTableSource(x.getLeft());
            MappingRule mappingRule = getMappingRule(tableSource);
            if (mappingRule != null) {
                if (mappingRule.getColumn().equalsIgnoreCase(column)) {
                    isMappingColumn = true;
                }
            }
            if (isMappingColumn) {
                Object value = SQLEvalVisitorUtils.eval(JdbcConstants.MYSQL, x.getRight(), parameters);
                String partitionName = null;
                switch (x.getOperator()) {
                    case Equality:
                        partitionName = mappingRule.getPartition(value);
                        break;
                    default:
                        throw new ShardingRuntimeException("not support operator " + x.getOperator());
                }

                if (partitionName == null) {
                    throw new ShardingRuntimeException("sharding rule violation, partition not match, value : " + value);
                }

                TablePartition tablePartition = routeConfig.getPartition(mappingRule.getTable(), partitionName);

                if (tableSource.getAttribute(ATTR_PARTITION) == null) {
                    ((SQLExprTableSource) tableSource).setExpr(new SQLIdentifierExpr(tablePartition.getTable()));

                    tableSource.putAttribute(ATTR_PARTITION, tablePartition);

                    if (tablePartition.getDatabase() != null && input != null) {
                        input.putAttribute(ATTR_DB, tablePartition.getDatabase());
                    }
                } else if (tableSource.getAttribute(ATTR_PARTITION) != tablePartition) {
                    throw new ShardingRuntimeException("sharding rule violation, multi-partition matched, value : "
                                                       + value);
                }
            }
        }

        return false;
    }

    MappingRule getMappingRule(SQLTableSource tableSource) {
        if (tableSource instanceof SQLExprTableSource) {
            SQLExpr expr = ((SQLExprTableSource) tableSource).getExpr();
            if (expr instanceof SQLIdentifierExpr) {
                String table = ((SQLIdentifierExpr) expr).getName();
                return routeConfig.getMappingRule(table);
            }
        }

        return null;
    }

    static boolean isValue(SQLExpr x) {
        return x instanceof SQLLiteralExpr || x instanceof SQLVariantRefExpr;
    }

    static String getColumn(SQLExpr x) {
        if (x instanceof SQLPropertyExpr) {
            return ((SQLPropertyExpr) x).getName();
        }
        if (x instanceof SQLIdentifierExpr) {
            return ((SQLIdentifierExpr) x).getName();
        }
        return null;
    }

    static SQLTableSource getBinaryOpExprLeftOrRightTableSource(SQLExpr x) {
        SQLTableSource tableSource = (SQLTableSource) x.getAttribute(ATTR_TABLE_SOURCE);
        if (tableSource != null) {
            return tableSource;
        }

        SQLTableSource defaltTableSource = getDefaultTableSource(x.getParent());
        if (defaltTableSource instanceof SQLExprTableSource) {
            SQLExpr expr = ((SQLExprTableSource) defaltTableSource).getExpr();
            if (expr instanceof SQLIdentifierExpr) {
                x.putAttribute(ATTR_TABLE_SOURCE, defaltTableSource);
                return defaltTableSource;
            }
        }

        return null;
    }

    @Override
    public boolean visit(SQLPropertyExpr x) {
        x.getOwner().setParent(x);

        x.getOwner().accept(this);

        return false;
    }

    @Override
    public boolean visit(SQLIdentifierExpr x) {
        SQLTableSource tableSource = getTableSource(x.getName(), x.getParent());
        if (tableSource != null) {
            x.putAttribute(ATTR_TABLE_SOURCE, tableSource);
        }

        return false;
    }

    public static SQLTableSource getTableSource(String name, SQLObject parent) {
        Map<String, SQLTableSource> aliasMap = getAliasMap(parent);

        if (aliasMap == null) {
            return null;
        }

        SQLTableSource tableSource = aliasMap.get(name);

        if (tableSource != null) {
            return tableSource;
        }

        for (Map.Entry<String, SQLTableSource> entry : aliasMap.entrySet()) {
            if (name.equalsIgnoreCase(entry.getKey())) {
                return tableSource;
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, SQLTableSource> getAliasMap(SQLObject x) {
        if (x == null) {
            return null;
        }

        if (x instanceof SQLSelectQueryBlock || x instanceof SQLDeleteStatement) {
            Map<String, SQLTableSource> map = (Map<String, SQLTableSource>) x.getAttribute(ATTR_ALIAS);
            if (map == null) {
                map = new HashMap<String, SQLTableSource>();
                x.putAttribute(ATTR_ALIAS, map);
            }
            return map;
        }

        return getAliasMap(x.getParent());
    }

    public static SQLTableSource getDefaultTableSource(SQLObject x) {
        if (x == null) {
            return null;
        }

        if (x instanceof SQLSelectQueryBlock) {
            return ((SQLSelectQueryBlock) x).getFrom();
        }

        if (x instanceof SQLDeleteStatement) {
            return ((SQLDeleteStatement) x).getTableSource();
        }

        if (x instanceof SQLUpdateStatement) {
            return ((SQLUpdateStatement) x).getTableSource();
        }

        return getDefaultTableSource(x.getParent());
    }

}
