package com.alibaba.druid.sql.dialect.postgresql.visitor;

import java.util.Map;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLTruncateStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGAggregateExpr;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGWithClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGWithQuery;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGAnalytic;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGDeleteStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGInsertStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock.FetchClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock.ForClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock.WindowClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGTruncateStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGUpdateStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Mode;
import com.alibaba.druid.util.JdbcUtils;

public class PGSchemaStatVisitor extends SchemaStatVisitor implements PGASTVisitor {

    @Override
    public String getDbType() {
        return JdbcUtils.POSTGRESQL;
    }
    
    @Override
    public void endVisit(WindowClause x) {

    }

    @Override
    public boolean visit(WindowClause x) {
        return true;
    }

    @Override
    public void endVisit(FetchClause x) {

    }

    @Override
    public boolean visit(FetchClause x) {
        return true;
    }

    @Override
    public void endVisit(ForClause x) {

    }

    @Override
    public boolean visit(ForClause x) {

        return true;
    }

    @Override
    public void endVisit(PGWithQuery x) {

    }

    @Override
    public boolean visit(PGWithQuery x) {
        Map<String, String> aliasMap = getAliasMap();
        if (aliasMap != null) {
            String alias = null;
            if (x.getName() != null) {
                alias = x.getName().toString();
            }

            if (alias != null) {
                aliasMap.put(alias, null);
                subQueryMap.put(alias, x.getQuery());
            }
        }
        x.getQuery().accept(this);
        return false;
    }

    @Override
    public void endVisit(PGWithClause x) {

    }

    @Override
    public boolean visit(PGWithClause x) {
        return true;
    }

    @Override
    public void endVisit(PGTruncateStatement x) {

    }

    @Override
    public boolean visit(PGTruncateStatement x) {
        this.visit((SQLTruncateStatement) x);
        return false;
    }

    @Override
    public void endVisit(PGDeleteStatement x) {

    }

    @Override
    public boolean visit(PGDeleteStatement x) {
        if (x.getWith() != null) {
            x.getWith().accept(this);
        }

        setAliasMap();

        for (SQLName name : x.getUsing()) {
            String ident = name.toString();

            TableStat stat = getTableStat(ident);
            stat.incrementSelectCount();

            Map<String, String> aliasMap = getAliasMap();
            if (aliasMap != null) {
                aliasMap.put(ident, ident);
            }
        }

        x.putAttribute("_original_use_mode", getMode());
        setMode(x, Mode.Delete);

        String ident = ((SQLIdentifierExpr) x.getTableName()).getName();
        setCurrentTable(ident);

        TableStat stat = getTableStat(ident, x.getAlias());
        stat.incrementDeleteCount();

        accept(x.getWhere());

        return false;
    }

    @Override
    public void endVisit(PGInsertStatement x) {

    }

    @Override
    public boolean visit(PGInsertStatement x) {
        setAliasMap();

        if (x.getWith() != null) {
            x.getWith().accept(this);
        }

        x.putAttribute("_original_use_mode", getMode());
        setMode(x, Mode.Insert);

        String originalTable = getCurrentTable();

        if (x.getTableName() instanceof SQLName) {
            String ident = ((SQLName) x.getTableName()).toString();
            setCurrentTable(ident);
            x.putAttribute("_old_local_", originalTable);

            TableStat stat = getTableStat(ident);
            stat.incrementInsertCount();

            Map<String, String> aliasMap = getAliasMap();
            if (aliasMap != null) {
                if (x.getAlias() != null) {
                    aliasMap.put(x.getAlias(), ident);
                }
                aliasMap.put(ident, ident);
            }
        }

        accept(x.getColumns());
        accept(x.getQuery());

        return false;
    }

    @Override
    public void endVisit(PGSelectStatement x) {

    }
    
    @Override
    public boolean visit(PGSelectStatement x) {
        if (x.getWith() != null) {
            x.getWith().accept(this);
        }

        return visit((SQLSelectStatement) x);
    }

    @Override
    public void endVisit(PGUpdateStatement x) {

    }

    @Override
    public boolean visit(PGUpdateStatement x) {
        Map<String, String> oldAliasMap = getAliasMap();

        setAliasMap();

        if (x.getWith() != null) {
            x.getWith().accept(this);
        }

        String ident = x.getTableName().toString();
        setCurrentTable(ident);

        TableStat stat = getTableStat(ident);
        stat.incrementUpdateCount();

        Map<String, String> aliasMap = getAliasMap();
        aliasMap.put(ident, ident);

        accept(x.getItems());
        accept(x.getWhere());

        setAliasMap(oldAliasMap);

        return false;
    }

    @Override
    public void endVisit(PGSelectQueryBlock x) {
        
    }

    @Override
    public boolean visit(PGSelectQueryBlock x) {
        return this.visit((SQLSelectQueryBlock) x);
    }

	@Override
	public void endVisit(PGAggregateExpr x) {
		
	}

	@Override
	public boolean visit(PGAggregateExpr x) {
		return false;
	}

	@Override
	public void endVisit(PGAnalytic x) {
		
	}

	@Override
	public boolean visit(PGAnalytic x) {
		return false;
	}
}
