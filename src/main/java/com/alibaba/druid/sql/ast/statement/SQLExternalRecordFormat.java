package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLExternalRecordFormat extends SQLObjectImpl {
    private SQLExpr delimitedBy;
    private SQLExpr terminatedBy;
    private SQLExpr escapedBy;
    private SQLExpr collectionItemsTerminatedBy;
    private SQLExpr mapKeysTerminatedBy;
    private SQLExpr linesTerminatedBy;
    private SQLExpr nullDefinedAs;
    private SQLExpr serde;

    private Boolean logfile;
    private Boolean badfile;
    private boolean ltrim;
    private boolean missingFieldValuesAreNull;
    private boolean rejectRowsWithAllNullFields;

    @Override
    public void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, delimitedBy);
            acceptChild(visitor, terminatedBy);
            acceptChild(visitor, escapedBy);
            acceptChild(visitor, collectionItemsTerminatedBy);
            acceptChild(visitor, mapKeysTerminatedBy);
            acceptChild(visitor, linesTerminatedBy);
            acceptChild(visitor, nullDefinedAs);
            acceptChild(visitor, serde);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getDelimitedBy() {
        return delimitedBy;
    }

    public void setDelimitedBy(SQLExpr delimitedBy) {
        if (delimitedBy != null) {
            delimitedBy.setParent(this);
        }
        this.delimitedBy = delimitedBy;
    }

    public SQLExpr getTerminatedBy() {
        return terminatedBy;
    }

    public void setTerminatedBy(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.terminatedBy = x;
    }

    public SQLExpr getSerde() {
        return serde;
    }

    public void setSerde(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.serde = x;
    }

    public SQLExpr getMapKeysTerminatedBy() {
        return mapKeysTerminatedBy;
    }

    public void setMapKeysTerminatedBy(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        mapKeysTerminatedBy = x;
    }

    public SQLExpr getCollectionItemsTerminatedBy() {
        return collectionItemsTerminatedBy;
    }

    public void setCollectionItemsTerminatedBy(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.collectionItemsTerminatedBy = x;
    }

    public SQLExpr getEscapedBy() {
        return escapedBy;
    }

    public void setEscapedBy(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.escapedBy = x;
    }

    public SQLExpr getLinesTerminatedBy() {
        return linesTerminatedBy;
    }

    public void setLinesTerminatedBy(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.linesTerminatedBy = x;
    }

    public SQLExpr getNullDefinedAs() {
        return nullDefinedAs;
    }

    public void setNullDefinedAs(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.nullDefinedAs = x;
    }

    public Boolean getLogfile() {
        return logfile;
    }

    public void setLogfile(Boolean logfile) {
        this.logfile = logfile;
    }

    public Boolean getBadfile() {
        return badfile;
    }

    public void setBadfile(Boolean badfile) {
        this.badfile = badfile;
    }

    public boolean isLtrim() {
        return ltrim;
    }

    public void setLtrim(boolean ltrim) {
        this.ltrim = ltrim;
    }

    public boolean isRejectRowsWithAllNullFields() {
        return rejectRowsWithAllNullFields;
    }

    public void setRejectRowsWithAllNullFields(boolean rejectRowsWithAllNullFields) {
        this.rejectRowsWithAllNullFields = rejectRowsWithAllNullFields;
    }

    public boolean isMissingFieldValuesAreNull() {
        return missingFieldValuesAreNull;
    }

    public void setMissingFieldValuesAreNull(boolean missingFieldValuesAreNull) {
        this.missingFieldValuesAreNull = missingFieldValuesAreNull;
    }

    public SQLExternalRecordFormat clone() {
        SQLExternalRecordFormat x = new SQLExternalRecordFormat();

        if (delimitedBy != null) {
            x.setDelimitedBy(delimitedBy.clone());
        }

        if (escapedBy != null) {
            x.setEscapedBy(escapedBy.clone());
        }

        if (collectionItemsTerminatedBy != null) {
            x.setCollectionItemsTerminatedBy(collectionItemsTerminatedBy.clone());
        }

        if (mapKeysTerminatedBy != null) {
            x.setMapKeysTerminatedBy(mapKeysTerminatedBy.clone());
        }

        if (linesTerminatedBy != null) {
            x.setLinesTerminatedBy(linesTerminatedBy.clone());
        }

        if (nullDefinedAs != null) {
            x.setNullDefinedAs(nullDefinedAs.clone());
        }

        if (serde != null) {
            x.setSerde(serde.clone());
        }

        x.logfile = logfile;
        x.badfile = badfile;
        x.ltrim = ltrim;
        x.missingFieldValuesAreNull = missingFieldValuesAreNull;
        x.rejectRowsWithAllNullFields = rejectRowsWithAllNullFields;

        return x;
    }
}
