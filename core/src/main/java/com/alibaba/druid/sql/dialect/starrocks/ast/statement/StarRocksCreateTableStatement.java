package com.alibaba.druid.sql.dialect.starrocks.ast.statement;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.starrocks.visitor.StarRocksASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StarRocksCreateTableStatement extends SQLCreateTableStatement {
    protected SQLName modelKey;
    protected SQLExpr partitionBy;
    protected Map<SQLObject, SQLObject> lessThanMap = new LinkedHashMap<>();
    protected Map<SQLObject, SQLObject> fixedRangeMap = new LinkedHashMap<>();

    protected final List<SQLExpr> parameters = new ArrayList<SQLExpr>();



    public StarRocksCreateTableStatement() {
        super(DbType.starrocks);
    }

    public Map<SQLObject, SQLObject> getLessThanMap() {
        return lessThanMap;
    }

    public void setLessThanMap(Map<SQLObject, SQLObject> lessThanMap) {
        this.lessThanMap = lessThanMap;
    }

    public SQLName getModelKey() {
        return modelKey;
    }

    public void setModelKey(SQLName modelKey) {
        this.modelKey = modelKey;
    }

    public List<SQLExpr> getParameters() {
        return parameters;
    }

    public void setPartitionBy(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.partitionBy = x;
    }

    public SQLExpr getPartitionBy() {
        return partitionBy;
    }



    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v instanceof StarRocksASTVisitor) {
            StarRocksASTVisitor vv = (StarRocksASTVisitor) v;
            if (vv.visit(this)) {
                acceptChild(vv);
            }
            vv.endVisit(this);
            return;
        }

        if (v.visit(this)) {
            acceptChild(v);
        }
        v.endVisit(this);
    }

}
