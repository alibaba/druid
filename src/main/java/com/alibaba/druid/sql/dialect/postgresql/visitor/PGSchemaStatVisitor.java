package com.alibaba.druid.sql.dialect.postgresql.visitor;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLTruncateStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGCurrentOfExpr;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGDeleteStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGInsertStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGTruncateStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock.FetchClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock.ForClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock.IntoClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGSelectQueryBlock.WindowClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGWithClause;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGWithQuery;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.stat.TableStat.Mode;

public class PGSchemaStatVisitor extends SchemaStatVisitor implements PGASTVisitor {

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

        return true;
    }

    @Override
    public void endVisit(PGWithClause x) {

    }

    @Override
    public boolean visit(PGWithClause x) {

        return true;
    }

    @Override
    public void endVisit(IntoClause x) {

    }

    @Override
    public boolean visit(IntoClause x) {
        String ident = x.getTable().toString();

        TableStat stat = tableStats.get(ident);
        if (stat == null) {
            stat = new TableStat();
            tableStats.put(new TableStat.Name(ident), stat);
        }
        stat.incrementInsertCount();
        return false;
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
        
        aliasLocal.set(new HashMap<String, String>());
        
        for (SQLName name : x.getUsing()) {
            String ident = name.toString();

            TableStat stat = tableStats.get(ident);
            if (stat == null) {
                stat = new TableStat();
                tableStats.put(new TableStat.Name(ident), stat);
            }
            stat.incrementSelectCount();

            Map<String, String> aliasMap = aliasLocal.get();
            if (aliasMap != null) {
                aliasMap.put(ident, ident);
            }
        }
        

        x.putAttribute("_original_use_mode", modeLocal.get());
        modeLocal.set(Mode.Delete);

        String ident = ((SQLIdentifierExpr) x.getTableName()).getName();
        currentTableLocal.set(ident);

        TableStat stat = tableStats.get(ident);
        if (stat == null) {
            stat = new TableStat();
            tableStats.put(new TableStat.Name(ident), stat);
            if (x.getAlias() != null) {
                tableStats.put(new TableStat.Name(x.getAlias()), stat);
            }
        }
        stat.incrementDeleteCount();

        accept(x.getWhere());

        return false;
    }

    @Override
    public void endVisit(PGCurrentOfExpr x) {
        
    }

    @Override
    public boolean visit(PGCurrentOfExpr x) {
        return false;
    }

    @Override
    public void endVisit(PGInsertStatement x) {
        
    }

    @Override
    public boolean visit(PGInsertStatement x) {
        if (x.getWith() != null) {
            x.getWith().accept(this);
        }
        
        return visit((SQLInsertStatement) x);
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
}
