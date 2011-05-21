package com.alibaba.druid.sql.dialect.oracle.ast.clause;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleAggregateExpr;

public class PivotClause {

    private boolean             xml;
    private final List<Entry>   entries  = new ArrayList<Entry>();
    private final List<SQLName> pivotFor = new ArrayList<SQLName>();
    private final List<SQLName> pivotIn  = new ArrayList<SQLName>();

    public List<Entry> getEntries() {
        return entries;
    }

    public List<SQLName> getPivotFor() {
        return pivotFor;
    }

    public List<SQLName> getPivotIn() {
        return pivotIn;
    }

    public static class Entry {

        private OracleAggregateExpr aggregateExpr;
        private String              alias;

        public OracleAggregateExpr getAggregateExpr() {
            return aggregateExpr;
        }

        public void setAggregateExpr(OracleAggregateExpr aggregateExpr) {
            this.aggregateExpr = aggregateExpr;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

    }

    public boolean isXml() {
        return xml;
    }

    public void setXml(boolean xml) {
        this.xml = xml;
    }

}
