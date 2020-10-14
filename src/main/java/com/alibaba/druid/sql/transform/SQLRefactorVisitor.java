package com.alibaba.druid.sql.transform;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.repository.SchemaObject;
import com.alibaba.druid.sql.visitor.SQLASTVisitorAdapter;
import com.alibaba.druid.util.FnvHash;

import java.util.HashMap;
import java.util.Map;

public class SQLRefactorVisitor extends SQLASTVisitorAdapter {
    private int havingLevel = 0;
    private int groupByLevel = 0;

    private char quote = '"';
    public SQLRefactorVisitor(DbType dbType) {
        this.dbType = dbType;

        switch (dbType) {
            case mysql:
            case mariadb:
            case ads:
                quote = '`';
                break;
            default:
                break;
        }
    }

    private Map<Long, TableMapping> tableMappings = new HashMap<Long, TableMapping>();

    public void addMapping(TableMapping mapping) {
        tableMappings.put(mapping.getSrcTableHash(), mapping);
    }

    public boolean visit(SQLExprTableSource x) {
        TableMapping mapping =findMapping(x);
        if (mapping == null) {
            return true;
        }

        String destTable = mapping.getDestTable();

        x.setExpr(new SQLIdentifierExpr(quote(destTable)));

        return false;
    }

    private TableMapping findMapping(SQLExprTableSource x) {
        SchemaObject schemaObject = x.getSchemaObject();
        if (schemaObject == null) {
            return null;
        }

        long nameHashCode = FnvHash.hashCode64(schemaObject.getName());
        return tableMappings.get(nameHashCode);
    }

    public boolean visit(SQLIdentifierExpr x) {
        TableMapping mapping = null;

        if (groupByLevel > 0 || havingLevel > 0) {
            SQLSelectQueryBlock queryBlock = null;
            for (SQLObject parent = x.getParent();
                 parent != null;
                 parent = parent.getParent()
            ) {
                if (parent instanceof SQLSelectQueryBlock) {
                    queryBlock = (SQLSelectQueryBlock) parent;
                    break;
                }
            }

            boolean matchAlias = false;
            if (queryBlock != null) {
                for (SQLSelectItem item : queryBlock.getSelectList()) {
                    if (item.alias_hash() == x.hashCode64()) {
                        matchAlias = true;
                        break;
                    }
                }
            }

            if (matchAlias) {
                SQLObject parent = x.getParent();
                if (parent instanceof SQLOrderBy
                        || parent instanceof SQLSelectGroupByClause) {
                    return false;
                }

                if (havingLevel > 0) {
                    boolean agg = false;
                    for (; parent != null; parent = parent.getParent()) {
                        if (parent instanceof SQLSelectQueryBlock) {
                            break;
                        }

                        if (parent instanceof SQLAggregateExpr) {
                            agg = true;
                            break;
                        }
                    }
                    if (!agg) {
                        return false;
                    }
                }

            }
        }

        SQLObject ownerObject = x.getResolvedOwnerObject();
        if (ownerObject instanceof SQLExprTableSource) {
            mapping = findMapping((SQLExprTableSource) ownerObject);
        }

        if (mapping == null) {
            return false;
        }

        String srcName = x.getName();
        String mappingColumn = mapping.getMappingColumn(srcName);
        if (mappingColumn != null) {
            x.setName(quote(mappingColumn));
        }

        SQLObject parent = x.getParent();
        if (parent instanceof SQLSelectItem
                && ((SQLSelectItem) parent).getAlias() == null) {
            ((SQLSelectItem) parent).setAlias(srcName);
        }

        return false;
    }

    public boolean visit(SQLSelectGroupByClause x) {
        groupByLevel++;
        for (SQLExpr item : x.getItems()) {
            item.accept(this);
        }

        SQLExpr having = x.getHaving();
        if (having != null) {
            havingLevel++;
            having.accept(this);
            havingLevel--;
        }
        groupByLevel--;

        return false;
    }

    public boolean visit(SQLPropertyExpr x) {
        TableMapping mapping = null;
        SchemaObject schemaObject = null;

        boolean aliasOwer = false;
        SQLObject ownerObject = x.getResolvedOwnerObject();
        if (ownerObject instanceof SQLExprTableSource) {
            SQLExprTableSource exprTableSource = (SQLExprTableSource) ownerObject;
            if (exprTableSource.getAlias() != null && x.getOwner() instanceof SQLIdentifierExpr) {
                if (FnvHash.hashCode64(exprTableSource.getAlias()) == ((SQLIdentifierExpr) x.getOwner()).nameHashCode64()) {
                    aliasOwer = true;
                }
            }

            mapping = findMapping(exprTableSource);
            schemaObject = (exprTableSource).getSchemaObject();
        }

        if (mapping == null) {
            return false;
        }

        String srcName = x.getName();
        String mappingColumn = mapping.getMappingColumn(srcName);
        if (mappingColumn != null) {
            x.setName(quote(mappingColumn));
        }

        SQLObject parent = x.getParent();
        if (parent instanceof SQLSelectItem
                && ((SQLSelectItem) parent).getAlias() == null) {
            ((SQLSelectItem) parent).setAlias(srcName);
        }

        if (x.getOwner() instanceof SQLIdentifierExpr
                && ((SQLIdentifierExpr) x.getOwner()).nameHashCode64() == mapping.getSrcTableHash()
                && !aliasOwer
        ) {
            x.setOwner(new SQLIdentifierExpr(quote(mapping.getDestTable())));
        }

        return false;
    }


    private String quote(String name) {
        char[] chars = new char[name.length() + 2];
        name.getChars(0, name.length(), chars, 1);
        chars[0] = '`';
        chars[chars.length - 1] = '`';
        return new String(chars);
    }
}
