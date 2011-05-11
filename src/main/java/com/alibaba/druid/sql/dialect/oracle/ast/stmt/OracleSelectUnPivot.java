package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectPivot.Item;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;

public class OracleSelectUnPivot extends OracleSelectPivotBase {
    private static final long serialVersionUID = 1L;

    private NullsIncludeType nullsIncludeType;
    private final List<SQLExpr> items = new ArrayList<SQLExpr>();

    private final List<OracleSelectPivot.Item> pivotIn = new ArrayList<Item>();

    public OracleSelectUnPivot() {

    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.items);
            acceptChild(visitor, this.pivotIn);
        }
        visitor.endVisit(this);
    }

    public List<OracleSelectPivot.Item> getPivotIn() {
        return this.pivotIn;
    }

    public List<SQLExpr> getItems() {
        return this.items;
    }

    public NullsIncludeType getNullsIncludeType() {
        return this.nullsIncludeType;
    }

    public void setNullsIncludeType(NullsIncludeType nullsIncludeType) {
        this.nullsIncludeType = nullsIncludeType;
    }

    public static enum NullsIncludeType {
        INCLUDE_NULLS,
        EXCLUDE_NULLS;

        public static String toString(NullsIncludeType type) {
            if (INCLUDE_NULLS.equals(type)) {
                return "INCLUDE NULLS";
            }
            if (EXCLUDE_NULLS.equals(type)) {
                return "EXCLUDE NULLS";
            }

            throw new IllegalArgumentException();
        }
    }
}
