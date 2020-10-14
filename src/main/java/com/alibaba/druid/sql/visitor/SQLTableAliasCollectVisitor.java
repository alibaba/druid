package com.alibaba.druid.sql.visitor;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.util.FnvHash;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class SQLTableAliasCollectVisitor extends SQLASTVisitorAdapter {
    protected Map<Long, SQLTableSource> tableSourceMap = new LinkedHashMap<Long, SQLTableSource>();
    protected volatile int seed;

    public boolean visit(SQLLateralViewTableSource x) {
        String alias =x.getAlias();
        if (alias == null) {
            return false;
        }

        long hashCode64 = FnvHash.hashCode64(alias);
        tableSourceMap.put(hashCode64, x);
        return true;
    }

    public boolean visit(SQLValuesTableSource x) {
        String alias =x.getAlias();
        if (alias == null) {
            return false;
        }

        long hashCode64 = FnvHash.hashCode64(alias);
        tableSourceMap.put(hashCode64, x);
        return true;
    }

    public boolean visit(SQLUnionQueryTableSource x) {
        String alias =x.getAlias();
        if (alias == null) {
            x.getUnion().accept(this);
            return false;
        }

        long hashCode64 = FnvHash.hashCode64(alias);
        tableSourceMap.put(hashCode64, x);
        return true;
    }

    public boolean visit(SQLSubqueryTableSource x) {
        String alias =x.getAlias();
        if (alias == null) {
            x.getSelect().accept(this);
            return false;
        }

        long hashCode64 = FnvHash.hashCode64(alias);
        tableSourceMap.put(hashCode64, x);
        return true;
    }

    public boolean visit(SQLJoinTableSource x) {
        String alias = x.getAlias();
        if (alias == null) {
            return true;
        }
        long hashCode64 = FnvHash.hashCode64(alias);
        tableSourceMap.put(hashCode64, x);
        return true;
    }

    public boolean visit(SQLWithSubqueryClause.Entry x) {
        String alias = x.getAlias();
        if (alias == null) {
            return true;
        }
        long hashCode64 = FnvHash.hashCode64(alias);
        tableSourceMap.put(hashCode64, x);
        return true;
    }

    public boolean visit(SQLExprTableSource x) {
        String alias = x.getAlias();

        if (alias == null) {
            SQLExpr expr = x.getExpr();
            if (expr instanceof SQLName) {
                long hashCode64 = ((SQLName) expr).nameHashCode64();
                tableSourceMap.put(hashCode64, x);
                return false;
            }
            return true;
        }

        return true;
    }

    public Collection<SQLTableSource> getTableSources() {
        return tableSourceMap.values();
    }

    public SQLTableSource getTableSource(long hashCode64) {
        return tableSourceMap.get(hashCode64);
    }

    public boolean containsTableSource(String alias) {
        if (alias == null) {
            return false;
        }
        long hashCode64 = FnvHash.hashCode64(alias);
        return tableSourceMap.containsKey(hashCode64);
    }

    public String genAlias(int seed) {
        String alias = null;
        for (; seed < 100;) {
            String str = "G" + (seed++);
            if (!containsTableSource(str)) {
                alias = str;
                this.seed = seed;
                break;
            }
        }
        return alias;
    }

    public int getSeed() {
        return seed;
    }
}
